# Floaty/CircularMenu 内存泄漏：分析与解决复盘

## 1. 背景与现象

- **问题现象**
  - LeakCanary 持续报告 `CircularActionMenu`（View 已 detach）与 `FloatyService`（已 `onDestroy()`）被长期持有。
  - 设备与环境信息见 LeakCanary METADATA（SDK 36 等）。

- **影响范围**
  - 悬浮菜单（CircularMenu）相关的浮窗视图与服务生命周期。
  - 可能导致：内存占用增长、服务/窗口对象无法释放、后续 UI/悬浮窗行为异常。

- **触发条件（推测/复现方向）**
  - 打开悬浮菜单后关闭（或权限变化/系统回收）导致 `FloatyService` 销毁。
  - 窗口视图已从 `WindowManager` 移除，但仍存在上游强引用链。

## 2. LeakCanary 引用链路解读（关键路径）

### 2.1 链路 A：单例 -> listener -> UI -> View（detach 后仍 retained）

- `GlobalActionRecorder.sSingleton`（静态单例，进程级常驻）
- `GlobalActionRecorder.mOnStateChangedListeners`（`CopyOnWriteArrayList`，强引用 listener）
- `CircularMenu`（实现 `Recorder.OnStateChangedListener`，被强引用）
- `CircularMenu.binding -> CircularActionMenuBinding.rootView -> CircularActionMenu`
- `CircularActionMenu` 已 `onDetachedFromWindow()`，但仍被链路强引用，无法回收

### 2.2 链路 B：View(Context=Service) -> Service（onDestroy 后仍 retained）

- `CircularMenu.mActionViewIcon`（View）
- `View.mContext` 指向 `FloatyService`
- `FloatyService` 已 `onDestroy()`，但仍被上游对象间接持有

## 3. 代码层面根因（结合当前实现）

### 3.1 关键类与职责

- `GlobalActionRecorder`
  - 进程级单例，维护录制状态与 listener 列表。
  - `addOnStateChangedListener()`/`removeOnStateChangedListener()` 需要调用方保证成对。

- `CircularMenu`
  - 构造时注册：
    - `mRecorder.addOnStateChangedListener(this)`
    - `LayoutInspector.addCaptureAvailableListener(this)`
  - 关闭时（`close()`）才取消注册：
    - `mRecorder.removeOnStateChangedListener(this)`
    - `LayoutInspector.removeCaptureAvailableListener(this)`
  - 同时持有 `binding`、`mActionViewIcon` 等 View 引用。

- `FloatyService`
  - `onDestroy()` 仅遍历 `windows` 回调 `window.onServiceDestroy(this)`。

- `CircularMenuWindow`
  - `onServiceDestroy()` 仅调用 `close()` 关闭窗口：
    - 从 `WindowManager` 移除 view
    - 从 `FloatyService` 的 `windows` 集合移除
    - `FloatyWindowManger.clearCircularMenu()`

### 3.2 根因结论

- **根因不是 Compose/UDF 本身**。
- **根因是生命周期清理不对称**：
  - `CircularMenu` 绑定了“进程级单例 listener”（强引用），但在 `FloatyService` 销毁路径上并不保证执行 `CircularMenu.close()`。
  - 结果是：即使浮窗 view 已从 `WindowManager` 移除，`CircularMenu` 仍被 `GlobalActionRecorder` 持有，进而继续强持有 `binding/view/context`。

一句话：**窗口被关掉了，但 `CircularMenu` 还活着；`CircularMenu` 之所以还活着，是因为被单例 listener 列表强引用。**

## 4. 修复方案（最小改动）

### 4.1 修复目标

- 当 `FloatyService` 触发销毁（`onDestroy()`）时：
  - 不仅要移除 `WindowManager` 上的 view
  - 还要确保 `CircularMenu` 解除单例 listener 订阅、释放对 View/binding 的强引用

### 4.2 实施改动

- 文件：`app/src/main/java/org/autojs/autojs/ui/floating/CircularMenu.java`

- 关键改动点：
  - 增加 `releaseResources()`：集中处理
    - 解除 `GlobalActionRecorder` 监听
    - 解除 `LayoutInspector` 监听
    - 释放 `binding`、`mActionViewIcon`、`mCaptureDeferred`
    - 关闭并置空相关 dialog 引用
  - 在 `close()` 最终调用 `releaseResources()`（保持原有关闭入口语义不变）
  - 在 `CircularMenuWindow.onServiceDestroy()` 路径上，确保也会触发 `releaseResources()`（通过覆盖 `onServiceDestroy()` 执行对齐释放）

### 4.3 为什么这样能修复

- 断开泄漏链路的关键环节：
  - `GlobalActionRecorder.sSingleton -> listeners -> CircularMenu`
- 一旦解除监听，`CircularMenu` 不再被单例强引用，GC 才可能回收。
- 同时将 `binding/view` 置空，避免即使 `CircularMenu` 暂时存活也继续强引用已 detach 的 View 与其 Context。

## 5. 验证与回归

### 5.1 验证步骤

- 手工复现流程：
  - 打开悬浮菜单 -> 触发一次展开/收起 -> 关闭悬浮菜单 / 停止 FloatyService
  - 或触发系统回收/权限变更导致 Service 销毁

- LeakCanary 验证：
  - 观察是否仍出现：
    - `CircularActionMenu` detach 后 retained
    - `FloatyService` onDestroy 后 retained

### 5.2 关注点

- 确认 `releaseResources()` 不会造成重复调用崩溃（应允许幂等）。
- 确认 `setState()` 等路径在释放后不会被调用（或调用时能安全处理）。

## 6. 架构层面复盘（与 UDF/MVVM/Compose 的关系）

### 6.1 问题本质

- 这是一个典型的 **“全局单例 + 强引用回调 + 生命周期边界不清”** 问题。
- 是否 Compose 并不决定是否泄漏；泄漏来自“引用链没有断开”。

### 6.2 与 UDF 的关联（间接）

- UDF/MVVM 强调：
  - 状态由上游产生，UI 以生命周期安全的方式订阅
  - 避免把 UI/Controller 对象直接塞进全局单例的 listener 列表

- 当前实现的风险点：
  - `GlobalActionRecorder` 通过 `addOnStateChangedListener()` 直接持有 `CircularMenu`（UI 对象）。
  - `CircularMenu` 又持有 view/binding/context。
  - 一旦缺少对称解绑，就会形成稳定泄漏链。

### 6.3 后续改进建议（可选）

- 将 `GlobalActionRecorder` 的状态变化改为可观察的状态流（例如通过 Flow/回调转 StateFlow），并由具备生命周期的宿主负责收集与取消。
- 明确“悬浮窗”模块的生命周期边界：
  - 由 `FloatyService` 统一托管创建/销毁
  - 避免 `Activity Context` 进入长期存活对象（对浮窗尤其重要）

## 7. 相关文件索引

- `app/src/main/java/org/autojs/autojs/core/record/GlobalActionRecorder.java`
- `app/src/main/java/org/autojs/autojs/ui/floating/CircularMenu.java`
- `app/src/main/java/org/autojs/autojs/ui/floating/CircularMenuWindow.java`
- `app/src/main/java/org/autojs/autojs/ui/enhancedfloaty/FloatyService.java`
- `app/src/main/java/org/autojs/autojs/ui/floating/FloatyWindowManger.java`


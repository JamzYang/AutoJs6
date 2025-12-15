# 复盘：JS 截屏权限复用（MediaProjection）改造与“第二次执行阻塞”问题修复

- 时间：2025-12-15
- 目标：在 **App 进程不退出** 的前提下，实现 **JS 脚本跨生命周期复用截屏授权**，避免每次执行脚本都弹出系统授权；同时保证资源释放与稳定性。

## 1. 背景与现象

### 1.1 原始现象

- 每次运行 JS 脚本，只要调用 `images.requestScreenCapture()` / `images.captureScreen()`，都会触发一次 `MediaProjection` 系统授权弹窗。
- 脚本层面表现为：首次需要人工点授权，后续脚本再次运行仍会重复弹窗并阻塞。

### 1.2 期望行为

- **同一 App 进程内**：只要用户授权过一次，后续脚本执行时无需再次弹窗。
- 脚本退出时应当释放重资源（避免泄漏），但不应破坏“跨脚本复用授权”的能力。

## 2. 根因分析

### 2.1 生命周期导致的重复申请

- `ScriptRuntime` 每次脚本执行都会新建；`Images` API 也随 `ScriptRuntime` 新建。
- `Images` 内部的 `mScreenCapturer` 是实例字段，脚本结束后会被释放/丢弃。
- 原实现把“授权结果”与“截屏对象实例”耦合在脚本生命周期里：脚本退出后没有可复用的授权数据，自然导致下次脚本再次弹窗。

### 2.2 资源与泄漏风险

- `ScreenCapturer` 内部持有 `MediaProjection / VirtualDisplay / ImageReader` 等系统资源，并注册 `EventBus` 监听。
- 如果简单把 `ScreenCapturer` 做成全局单例，容易出现：
  - `EventBus` 常驻、`VirtualDisplay` 未释放
  - 隐式持有 Context/回调导致泄漏
  - 与脚本线程/Looper 绑定关系复杂，稳定性风险更大

结论：
- **授权数据** 适合做进程级缓存。
- **ScreenCapturer 实例** 仍应与脚本生命周期绑定，并在脚本结束时释放。

## 3. 改造方案概述

### 3.1 核心思路

- 将 `MediaProjection` 授权返回的 `Intent data` 做成 **进程级缓存**：
  - 首次授权成功：缓存 `Intent`。
  - 后续脚本：优先使用缓存 `Intent` 直接创建 `ScreenCapturer`，避免再次弹窗。

### 3.2 Android 10+/14 前台服务时序要求

为了符合 Android 10+/14 对 `MediaProjection` 的要求，需要保证启动顺序：

- 先授权
- 再启动/确保 `mediaProjection` 类型前台服务处于运行/就绪
- 再通过 `MediaProjectionManager#getMediaProjection` 建立投影

因此在“复用缓存授权”路径里，需要确保 `ScreenCapturerForegroundService` 已 ready。

## 4. 关键实现点（代码位置）

### 4.1 `Images.java`：进程级缓存授权 Intent

文件：`app/src/main/java/org/autojs/autojs/runtime/api/Images.java`

- 新增进程级缓存字段：`sScreenCapturePermissionData`
- 新增缓存读写方法：
  - `getCachedScreenCapturePermissionData()`
  - `cacheScreenCapturePermissionData(Intent data)`
  - `clearCachedScreenCapturePermissionData()`

行为：
- 首次授权成功（result ok 且 intent 非空）时写缓存。
- 复用时读取缓存并尝试创建 `ScreenCapturer`。
- 若构造 `ScreenCapturer` 抛 `SecurityException`，清空缓存并回退到“弹窗授权”。

### 4.2 `Images.java`：复用时确保前台服务 ready

- 新增：`ensureScreenCapturerForegroundServiceReady(Context, Runnable)`
- 逻辑：
  - `startService()`
  - 若服务已 running：直接 `onReady`
  - 否则 `bindService()` 等待 `onServiceConnected()` 后再 `onReady`

### 4.3 `Images.java`：避免 `ScreenCaptureRequester` 连接泄漏

- 在授权流程回调结束（成功/失败/异常）时，确保调用释放逻辑（unbind + 置空），避免 `ServiceConnection` 未解绑。

### 4.4 `ScriptRuntime.kt`：脚本退出时释放 capturer，但不清空授权缓存

文件：`app/src/main/java/org/autojs/autojs/runtime/ScriptRuntime.kt`

- 脚本退出回收时：
  - **继续** `images.releaseScreenCapturer()`：释放 `VirtualDisplay/ImageReader/EventBus` 等重资源，避免泄漏。
  - **不自动**停止 `ScreenCapturerForegroundService`：保持进程内复用能力。
  - 授权缓存由 `Images` 进程级字段维护，不随脚本退出清空。

## 5. 踩坑复盘：第二次脚本执行一直阻塞、不截图

### 5.1 现象

- 第一次执行：弹出授权，脚本成功截图。
- 第二次执行：不再弹授权，但脚本一直 block，且没有截图。

### 5.2 根因

- 第二次执行走“复用缓存授权”分支。
- 复用分支中依赖 `ensureScreenCapturerForegroundServiceReady()` 回调 `onReady` 来创建 `ScreenCapturer` 并 resolve Promise。
- 原实现 **没有超时兜底**：当 `bindService()` 在特定 ROM/时序下不回调 `onServiceConnected()` 时，Promise 永远不 resolve，脚本就无限阻塞。

### 5.3 修复

- 对 `ensureScreenCapturerForegroundServiceReady()` 增加三类兜底：
  - 一次性回调保护：避免多次触发导致重复解绑/重复 resolve。
  - 超时兜底（3 秒）：超过时间直接进入 `onReady`，避免死等。
  - `bindService()` 返回 false 兜底：立即进入 `onReady`。

修复目的：
- **保证 JS 侧不会出现无限 block**。

## 6. 验证方式

建议按以下步骤验证：

1. 杀进程重启 App（确保缓存从空开始）。
2. 第一次运行脚本：
   - `requestScreenCapture()` 弹授权
   - 同意后 `captureScreen()` 成功
3. 第二次运行脚本：
   - 不弹授权
   - `requestScreenCapture()` 快速返回
   - `captureScreen()` 成功

额外建议：
- 在 Android 10+/14 机型重点验证前台服务通知与稳定性。

## 7. 风险与后续建议

- 授权缓存本质上依赖系统策略：
  - 可能因系统回收/策略/用户撤销导致失效
  - 失效时应当回退到弹窗流程（已实现：捕获 `SecurityException` 后清缓存并重新申请）
- 仍建议保持 `ScreenCapturer` 非全局单例：
  - 单例易引入跨脚本资源常驻与泄漏风险
  - 当前方案用“授权缓存 + per-script capturer”更易控、更安全

---

## 变更摘要

- `Images.java`
  - 增加进程级授权缓存
  - requestScreenCapture 优先复用缓存
  - 复用前确保前台服务 ready
  - 修复可能导致 Promise 永不 resolve 的等待逻辑（超时/兜底）
- `ScriptRuntime.kt`
  - 脚本退出释放 `ScreenCapturer`，但不停止前台服务、不清除进程级授权缓存

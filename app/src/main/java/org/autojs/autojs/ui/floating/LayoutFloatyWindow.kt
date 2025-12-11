package org.autojs.autojs.ui.floating

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.graphics.Rect
import org.autojs.autojs.app.AppLevelThemeDialogBuilder
import org.autojs.autojs.app.DialogUtils
import org.autojs.autojs.core.accessibility.Capture
import org.autojs.autojs.core.accessibility.NodeInfo
import org.autojs.autojs.core.accessibility.WindowInfo
import org.autojs.autojs.core.accessibility.WindowInfo.Companion.WindowInfoDataItem
import org.autojs.autojs.core.accessibility.WindowInfo.Companion.WindowInfoDataSummary
import org.autojs.autojs.core.accessibility.WindowInfo.Companion.WindowInfoOrderDataItem
import org.autojs.autojs.core.accessibility.WindowInfo.Companion.WindowInfoRootNodeDataItem
import org.autojs.autojs.core.accessibility.WindowInfo.Companion.parseWindowType
import org.autojs.autojs.ui.enhancedfloaty.FloatyService
import org.autojs.autojs.ui.floating.layoutinspector.LayoutBoundsFloatyWindow
import org.autojs.autojs.ui.floating.layoutinspector.LayoutBoundsView
import org.autojs.autojs.ui.floating.layoutinspector.LayoutHierarchyFloatyWindow
import org.autojs.autojs.ui.floating.layoutinspector.NodeInfoView
import org.autojs.autojs.ui.widget.BubblePopupMenu
import org.autojs.autojs.util.ClipboardUtils
import org.autojs.autojs.util.EnvironmentUtils
import org.autojs.autojs.util.ViewUtils
import org.autojs.autojs6.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.reflect.KFunction0

abstract class LayoutFloatyWindow(
    private val capture: Capture,
    private val context: Context,
    private val isServiceRelied: Boolean,
) : FullScreenFloatyWindow() {

    protected abstract val popMenuActions: List<Pair<Int, KFunction0<Unit>>?>

    private lateinit var mServiceContext: Context

    private var mLayoutSelectedNode: NodeInfo? = null

    private val mNodeInfoView by lazy { NodeInfoView(mServiceContext) }

    private val mNodeInfoDialog by lazy {
        AppLevelThemeDialogBuilder(mServiceContext)
            .customView(mNodeInfoView, false)

            // @Overruled by SuperMonster003 on Jul 21, 2023.
            //  ! Author: 抠脚本人
            //  ! Related PR: http://pr.autojs6.com/98
            //  ! Reason: Pending processing [zh-CN: 将于后续版本继续处理].
            //  !
            //  # .positiveText("生成")
            //  # .onPositive { _, _ ->
            //  #     ViewUtils.showToast(context, "TODO")
            //  #     val selector = mNodeInfoView.getCheckedDate().joinToString(".")
            //  #     if (selector.isNotEmpty()) ClipboardUtils.setClip(context, selector)
            //  # }

            .build()
            .also { it.window!!.setType(FloatyWindowManger.getWindowType()) }
    }

    fun onCreate(floatyService: FloatyService) {
        mServiceContext = if (isServiceRelied) ContextThemeWrapper(floatyService, R.style.AppTheme) else context
    }

    fun setLayoutSelectedNode(selectedNode: NodeInfo?) {
        mLayoutSelectedNode = selectedNode
    }

    protected fun getLayoutSelectedNode() = mLayoutSelectedNode

    protected fun getBubblePopMenu(): BubblePopupMenu {
        val (keys, values) = popMenuActions.filterNotNull().unzip()
        val dividerPositions = mutableListOf<Int>()
        var realIndex = -1
        for (pair in popMenuActions) {
            if (pair != null) {
                realIndex += 1
                continue
            }
            dividerPositions.add(realIndex)
        }
        return BubblePopupMenu(mServiceContext, keys.map { context.getString(it) }, dividerPositions)
            .apply {
                setOnItemClickListener { _: View?, position: Int ->
                    dismiss()
                    values.elementAtOrNull(position)?.invoke()
                }
                width = ViewGroup.LayoutParams.WRAP_CONTENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
    }

    protected fun showNodeInfo() {
        mLayoutSelectedNode?.let { mNodeInfoView.setNodeInfo(it) }
        mNodeInfoDialog.show()
    }

    protected fun showLayoutBounds() {
        close()
        LayoutBoundsFloatyWindow(capture, context, isServiceRelied)
            .apply { setLayoutSelectedNode(mLayoutSelectedNode) }
            .let { FloatyService.addWindow(it) }
    }

    protected fun showLayoutHierarchy() {
        close()
        LayoutHierarchyFloatyWindow(capture, context, isServiceRelied)
            .apply { setLayoutSelectedNode(mLayoutSelectedNode) }
            .let { FloatyService.addWindow(it) }
    }

    protected fun generateCode() {
        CodeGenerateDialog(context, capture.root, mLayoutSelectedNode)
            .build()
            .let { DialogUtils.showDialog(it) }
    }

    protected fun switchWindow() {
        val windows = capture.windows
        val windowInfoList = windows.map { win: WindowInfo ->
            WindowInfoDataSummary(
                win,
                title = WindowInfoDataItem(context.getString(R.string.text_captured_window_info_title), win.title, context.getString(R.string.text_captured_window_info_title_null)),
                order = WindowInfoOrderDataItem(context.getString(R.string.text_captured_window_info_order), win.order),
                type = WindowInfoDataItem(context.getString(R.string.text_captured_window_info_type), parseWindowType(context, win.type), context.getString(R.string.text_captured_window_info_type_unknown)),
                packageName = WindowInfoDataItem(context.getString(R.string.text_captured_window_info_package_name), win.packageName, context.getString(R.string.text_captured_window_info_package_name_unknown)),
                rootNode = WindowInfoRootNodeDataItem(context.getString(R.string.text_captured_window_info_root_node), win.rootClassName, context.getString(R.string.text_captured_window_info_root_node_unknown)),
            )
        }
        val builder = WindowSwitchingDialog(context, windowInfoList).apply {
            sortItems(compareBy { it.order.rawValue })
        }
        val dialog = DialogUtils.showDialog(builder.build())
        builder.itemsClickCallback = { _, position ->
            builder.itemList[position].window.root?.let {
                dialog.dismiss()
                capture.root = it
                mLayoutSelectedNode = null
                showLayoutBounds()
            }
        }
    }

    protected fun excludeNode() {
        mLayoutSelectedNode?.let {
            it.hidden = true
            mLayoutSelectedNode = null
        }
    }

    protected fun excludeAllBoundsSameNode(layoutBoundsView: LayoutBoundsView) {
        mLayoutSelectedNode?.let {
            layoutBoundsView.hideAllBoundsSameNode(it)
            mLayoutSelectedNode = null
        }
    }

    /**
     * 导出 GPT 友好版布局树
     *
     * 以易读的缩进文本格式输出：
     * [id] Class id=xxx text="..." clickable=true bounds=[l,t,r,b]
     * 并同时写入剪贴板与文件，方便直接粘贴给 GPT 进行关系分析。
     */
    protected fun exportLayoutTreeForGpt() {
        try {
            val gptText = buildGptFriendlyLayoutTreeText(capture.root)

            // 写入文件，便于持久保存
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val dir = File(EnvironmentUtils.externalStoragePath, "AutoJs6")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val file = File(dir, "layout_tree_gpt_$timestamp.txt")
            file.writeText(gptText)

            ViewUtils.showToast(context, context.getString(R.string.text_layout_tree_exported_gpt, file.absolutePath), true)
        } catch (e: Exception) {
            ViewUtils.showToast(context, context.getString(R.string.text_layout_tree_export_failed, e.message), true)
        }
    }

    /**
     * 构造 GPT 友好版的缩进布局树文本
     *
     * @param root 根节点
     * @return 可直接粘贴到 GPT 的缩进树字符串
     */
    private fun buildGptFriendlyLayoutTreeText(root: NodeInfo): String {
        val counter = intArrayOf(0)
        val builder = StringBuilder()

        fun Rect.toBracketString(): String = "[${left},${top},${right},${bottom}]"

        // 使用 "路径" 表示层级及同级序号，例如：
        // 根节点 path="0"，其第一个子节点 path="0.0"，第二个子节点 path="0.1"，依此类推。
        // 这样既能直观看出同级关系，又方便 GPT 还原整棵树结构。
        fun traverse(node: NodeInfo, depth: Int, path: MutableList<Int>) {
            val id = counter[0]++
            val indent = "  ".repeat(depth)
            val className = node.className ?: "unknown"
            val text = node.text?.replace("\n", "\\n") ?: ""
            val contentDesc = node.desc ?: ""
            val boundsStr = node.boundsInScreen.toBracketString()
            val clickable = node.clickable
            val checked = node.checked
            val enabled = node.enabled
            val resourceId = node.id ?: ""
            val pathString = path.joinToString(separator = ".")

            builder.append(indent)
                .append("[").append(id).append("] ")
                .append("(path=").append(pathString).append(", depth=").append(depth).append(") ")
                .append(className)
                .append(" id=").append(resourceId)
                .append(" text=\"").append(text).append("\"")
                .append(" desc=\"").append(contentDesc).append("\"")
                .append(" clickable=").append(clickable)
                .append(" checked=").append(checked)
                .append(" enabled=").append(enabled)
                .append(" bounds=").append(boundsStr)
                .append("\n")

            node.children.forEachIndexed { index, child ->
                // 子节点 path 继承父节点路径，并在末尾追加当前同级序号
                val childPath = (path + index).toMutableList()
                traverse(child, depth + 1, childPath)
            }
        }

        // 根节点从 path=[0] 开始，depth=0
        traverse(root, 0, mutableListOf(0))
        return builder.toString()
    }

    /**
     * 导出整棵布局树到本地文件
     *
     * 从根节点开始序列化为 JSON 格式，仅写入外部存储目录下的 AutoJs6 目录，
     * 不再写入剪贴板，避免在布局树过大时触发 TransactionTooLargeException。
     */
    protected fun exportLayoutTree() {
        try {
            // 序列化整棵树为 JSON 字符串
            val jsonString = capture.root.toJson().toString(2)

            // 保存到文件
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val dir = File(EnvironmentUtils.externalStoragePath, "AutoJs6")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val file = File(dir, "layout_tree_$timestamp.json")
            file.writeText(jsonString)

            // 显示成功提示，提示包含导出路径
            ViewUtils.showToast(context, context.getString(R.string.text_layout_tree_exported, file.absolutePath), true)
        } catch (e: Exception) {
            // 导出失败时提示异常信息
            ViewUtils.showToast(context, context.getString(R.string.text_layout_tree_export_failed, e.message), true)
        }
    }

    companion object {

        @JvmStatic
        protected val SPLIT_LINE: Nothing? = null

    }

}
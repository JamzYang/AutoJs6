package org.autojs.autojs.core.accessibility

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Rect
import androidx.annotation.Keep
import android.view.accessibility.AccessibilityNodeInfo

import org.autojs.autojs.core.automator.UiObject

import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by Stardust on Mar 10, 2017.
 * Modified by SuperMonster003 as of Jun 17, 2022.
 */

@Suppress("unused", "MemberVisibilityCanBePrivate")
@Keep
class NodeInfo(private val resources: Resources?, private val node: UiObject, var parent: NodeInfo?) {

    @Suppress("DEPRECATION")
    val boundsInParent = Rect().also { node.getBoundsInParent(it) }
    val boundsInScreen = Rect().also { node.getBoundsInScreen(it) }

    val packageName = node.packageName()
    val id = node.simpleId()
    val fullId = node.fullId()
    @SuppressLint("DiscouragedApi")
    val idHex = takeIf { resources != null && packageName != null && fullId != null }?.let {
        "0x${Integer.toHexString(resources!!.getIdentifier(fullId, null, null))}"
    }
    val desc = node.desc()
    val text = node.text()
    val bounds = boundsInScreen
    val center = node.center()
    val className = node.className()
    val clickable = node.isClickable
    val longClickable = node.isLongClickable
    val scrollable = node.isScrollable
    val indexInParent = node.indexInParent()
    val childCount = node.childCount()
    val depth = node.depth()
    val checked = node.isChecked
    val enabled = node.isEnabled
    val editable = node.isEditable
    val focusable = node.isFocusable
    val checkable = node.isCheckable
    val selected = node.isSelected
    val dismissable = node.isDismissable
    val visibleToUser = node.visibleToUser()
    val contextClickable = node.isContextClickable
    val focused = node.focused()
    val accessibilityFocused = node.isAccessibilityFocused
    val rowCount = node.rowCount()
    val columnCount = node.columnCount()
    val row = node.row()
    val column = node.column()
    val rowSpan = node.rowSpan()
    val columnSpan = node.columnSpan()
    val drawingOrder = node.drawingOrder
    val actionNames = node.actionNames()
    var hidden = false

    val children = ArrayList<NodeInfo>()

    override fun toString() = "$className${node.summary()}"

    /**
     * 将当前节点及其所有子节点递归序列化为 JSONObject
     * 用于导出整棵布局树以便在电脑上分析
     */
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("className", className)
            put("packageName", packageName)
            put("id", id)
            put("fullId", fullId)
            put("idHex", idHex)
            put("desc", desc)
            put("text", text)
            put("bounds", JSONObject().apply {
                put("left", bounds.left)
                put("top", bounds.top)
                put("right", bounds.right)
                put("bottom", bounds.bottom)
            })
            put("center", JSONObject().apply {
                put("x", center?.x)
                put("y", center?.y)
            })
            put("clickable", clickable)
            put("longClickable", longClickable)
            put("scrollable", scrollable)
            put("checkable", checkable)
            put("checked", checked)
            put("enabled", enabled)
            put("editable", editable)
            put("focusable", focusable)
            put("focused", focused)
            put("selected", selected)
            put("dismissable", dismissable)
            put("visibleToUser", visibleToUser)
            put("contextClickable", contextClickable)
            put("accessibilityFocused", accessibilityFocused)
            put("indexInParent", indexInParent)
            put("childCount", childCount)
            put("depth", depth)
            put("drawingOrder", drawingOrder)
            put("rowCount", rowCount)
            put("columnCount", columnCount)
            put("row", row)
            put("column", column)
            put("rowSpan", rowSpan)
            put("columnSpan", columnSpan)
            put("actionNames", JSONArray(actionNames))

            // 递归序列化子节点
            if (children.isNotEmpty()) {
                put("children", JSONArray().apply {
                    children.forEach { child ->
                        put(child.toJson())
                    }
                })
            }
        }
    }

    companion object {

        @JvmStatic
        fun boundsToString(rect: Rect) = rect.toString().replace(" - ", " , ").replace(" ", "").substring(4)

        internal fun capture(resourcesCache: HashMap<String, Resources>, context: Context, uiObject: UiObject, parent: NodeInfo?): NodeInfo {
            val resources: Resources? = uiObject.packageName()?.let { pkg ->
                return@let resourcesCache[pkg] ?: run {
                    try {
                        return@run context.packageManager.getResourcesForApplication(pkg).also { resourcesCache[pkg] = it }
                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                        return@run null
                    }
                }
            }
            return NodeInfo(resources, uiObject, parent).apply {
                0.until(uiObject.childCount)
                    .asSequence()
                    .mapNotNull { uiObject.child(it) }
                    .forEach { children += capture(resourcesCache, context, it, this) }
            }
        }

        fun capture(context: Context, root: AccessibilityNodeInfo): NodeInfo {
            val r = UiObject.createRoot(root)
            val resourcesCache = HashMap<String, Resources>()
            return capture(resourcesCache, context, r, null)
        }

    }

}

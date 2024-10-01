package org.ys.game.runtime.api

import android.content.Context
import android.graphics.drawable.Drawable
import org.ys.game.core.ui.inflater.DynamicLayoutInflater
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.rhino.ProxyObject
import org.ys.game.runtime.ScriptRuntime
import org.mozilla.javascript.Scriptable
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by Stardust on May 14, 2017.
 * Modified by SuperMonster003 as of Dec 5, 2021.
 * Transformed by SuperMonster003 on May 20, 2023.
 */
class UI(val context: Context, private val mRuntime: ScriptRuntime) : ProxyObject() {

    private val mProperties: MutableMap<String, Any> = ConcurrentHashMap()

    val layoutInflater: DynamicLayoutInflater
    val resourceParser: ResourceParser

    init {
        resourceParser = ResourceParser(Drawables())
        layoutInflater = DynamicLayoutInflater(resourceParser)
        mProperties["layoutInflater"] = layoutInflater
    }

    var bindingContext: Any?
        get() = mProperties["bindingContext"]
        set(context) {
            context?.let { mProperties["bindingContext"] = it } ?: mProperties.remove("bindingContext")
        }

    override fun getClassName(): String = UI::class.java.simpleName

    override fun get(name: String, start: Scriptable) = mProperties[name] ?: super.get(name, start)

    override fun put(name: String, start: Scriptable, value: Any?) {
        if (mProperties.containsKey(name)) {
            value?.let { mProperties[name] = it } ?: mProperties.remove(name)
        } else {
            super.put(name, start, value)
        }
    }

    fun recycle() {
        layoutInflater.context = null
    }

    private inner class Drawables : org.ys.game.core.ui.inflater.util.Drawables() {

        override fun decodeImage(context: Context, path: String?): Drawable? {
            return super.decodeImage(context, mRuntime.files.path(path))
        }

    }

}
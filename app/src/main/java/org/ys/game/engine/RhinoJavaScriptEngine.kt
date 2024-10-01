package org.ys.game.engine

import android.util.Log
import android.view.View
import org.ys.game.core.ui.ViewExtras
import org.ys.game.engine.module.AssetAndUrlModuleSourceProvider
import org.ys.game.execution.ExecutionConfig
import org.ys.game.pio.PFiles
import org.ys.game.pio.UncheckedIOException
import org.ys.game.project.ScriptConfig
import org.ys.game.rhino.AndroidContextFactory
import org.ys.game.rhino.AutoJsContext
import org.ys.game.rhino.RhinoAndroidHelper
import org.ys.game.rhino.TopLevelScope
import org.ys.game.runtime.ScriptRuntime
import org.ys.game.script.JavaScriptSource
import org.mozilla.javascript.Context
import org.mozilla.javascript.Script
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.commonjs.module.RequireBuilder
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.net.URI
import java.util.Locale

/**
 * Created by Stardust on Apr 2, 2017.
 * Modified by SuperMonster003 as of May 26, 2022.
 */
open class RhinoJavaScriptEngine(private val mAndroidContext: android.content.Context) : JavaScriptEngine() {

    val context: Context = enterContext()

    val scriptable: TopLevelScope = createScope(context)

    lateinit var thread: Thread
        private set

    private val wrapFactory = WrapFactory()

    private val mInitScript: Script by lazy {
        try {
            val reader = InputStreamReader(mAndroidContext.assets.open(SOURCE_FILE_INIT))
            context.compileReader(reader, SOURCE_NAME_INIT, 1, null)
        } catch (e: IOException) {
            throw UncheckedIOException(e)
        }
    }

    override fun put(name: String, value: Any?) {
        ScriptableObject.putProperty(scriptable, name, Context.javaToJS(value, scriptable))
    }

    override fun setRuntime(runtime: ScriptRuntime) {
        super.setRuntime(runtime)
        runtime.bridges.setup(this)
        runtime.topLevelScope = scriptable
    }

    public override fun doExecution(source: JavaScriptSource): Any? {
        try {
            val reader = preprocess(source.nonNullScriptReader)
            val script = context.compileReader(reader, source.fullPath, 1, null)
            return if (hasFeature(ScriptConfig.FEATURE_CONTINUATION)) {
                context.executeScriptWithContinuations(script, scriptable)
            } else {
                script.exec(context, scriptable)
            }
        } catch (e: IOException) {
            throw UncheckedIOException(e)
        }
    }

    fun hasFeature(feature: String): Boolean {
        return (getTag(ExecutionConfig.tag) as ExecutionConfig? ?: return false).scriptConfig.hasFeature(feature)
    }

    @Throws(IOException::class)
    protected fun preprocess(script: Reader) = script

    override fun forceStop() {
        Log.d(TAG, "forceStop: interrupt Thread: $thread")
        thread.interrupt()
    }

    @Synchronized
    override fun destroy() {
        super.destroy()
        Log.d(TAG, "on destroy")
        Context.exit()
    }

    override fun init() {
        thread = Thread.currentThread()
        ScriptableObject.putProperty(scriptable, "__engine__", this)
        initRequireBuilder(context, scriptable)
        try {
            context.executeScriptWithContinuations(mInitScript, scriptable)
        } catch (e: IllegalArgumentException) {
            if (e.message?.contains("Script argument was not a script or was not created by interpreted mode") == true) {
                mInitScript.exec(context, scriptable)
            } else {
                throw e
            }
        }
    }

    private fun initRequireBuilder(context: Context, scope: Scriptable) {
        val provider = AssetAndUrlModuleSourceProvider(
            mAndroidContext, MODULES_ROOT_PATH, listOf<URI>(File(File.separator).toURI())
        )
        RequireBuilder()
            .setModuleScriptProvider(SoftCachingModuleScriptProvider(provider))
            .setSandboxed(true)
            .createRequire(context, scope)
            .install(scope)
    }

    protected fun createScope(context: Context): TopLevelScope {
        return TopLevelScope().apply {
            initStandardObjects(context, false)
        }
    }

    fun enterContext(): Context {
        return RhinoAndroidHelper(mAndroidContext).enterContext().also { setupContext(it) }
    }

    fun setupContext(context: Context) {
        context.also {
            if (it is AutoJsContext) {
                it.rhinoJavaScriptEngine = this
            }
            it.optimizationLevel = -1
            it.languageVersion = Context.VERSION_ES6
            it.locale = Locale.getDefault()
            it.wrapFactory = WrapFactory()
        }
    }

    private inner class WrapFactory : AndroidContextFactory.WrapFactory() {

        override fun wrapAsJavaObject(cx: Context?, scope: Scriptable, javaObject: Any?, staticType: Class<*>?): Scriptable? {
            return when (javaObject) {
                is View -> ViewExtras.getNativeView(scope, /* view = */ javaObject, staticType, runtime)
                else -> super.wrapAsJavaObject(cx, scope, javaObject, staticType)
            }
        }

    }

    companion object {

        const val SOURCE_FILE_INIT = "init.js"
        const val SOURCE_NAME_INIT = "<init>"
        const val MODULES_ROOT_PATH = "modules"
        const val JS_BEAUTIFY_PATH = "js-beautify"

        @JvmField
        val JS_BEAUTIFY_FILE = PFiles.join(JS_BEAUTIFY_PATH, "beautify.js")

        private val TAG = RhinoJavaScriptEngine::class.java.simpleName

    }

}

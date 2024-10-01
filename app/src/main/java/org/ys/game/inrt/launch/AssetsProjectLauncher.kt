package org.ys.game.inrt.launch

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import org.ys.game.inrt.LogActivity
import org.ys.game.inrt.Pref
import org.ys.game.inrt.autojs.AutoJs
import org.ys.game.engine.encryption.ScriptEncryption
import org.ys.game.execution.ExecutionConfig
import org.ys.game.execution.ScriptExecution
import org.ys.game.pio.PFiles
import org.ys.game.pio.UncheckedIOException
import org.ys.game.project.ProjectConfig
import org.ys.game.script.JavaScriptFileSource
import org.ys.game.script.JavaScriptSource
import java.io.File
import java.io.IOException

/**
 * Created by Stardust on Jan 24, 2018.
 */
open class AssetsProjectLauncher(private val mAssetsProjectDir: String, private val mActivity: Context) {
    private val mProjectDir: String = File(mActivity.filesDir, "project/").path
    private val mProjectConfig: ProjectConfig = ProjectConfig.fromAssets(mActivity, ProjectConfig.configFileOfDir(mAssetsProjectDir))
    private val mMainScriptFile: File = File(mProjectDir, mProjectConfig.mainScriptFile)
    private val mHandler: Handler = Handler(Looper.getMainLooper())
    private var mScriptExecution: ScriptExecution? = null

    init {
        prepare()
    }

    fun launch(activity: Activity) {
        //如果需要隐藏日志界面，则直接运行脚本
        if (mProjectConfig.launchConfig.shouldHideLogs() || Pref.shouldHideLogs()) {
            runScript(activity)
        } else {
            //如果不隐藏日志界面
            //如果当前已经是日志界面则直接运行脚本
            if (activity is LogActivity) {
                runScript(null)
            } else {
                //否则显示日志界面并在日志界面中运行脚本
                mHandler.post {
                    activity.startActivity(Intent(mActivity, LogActivity::class.java)
                            .putExtra(LogActivity.EXTRA_LAUNCH_SCRIPT, true))
                    activity.finish()
                }
            }
        }
    }

    private fun runScript(activity: Activity?) {
        if (mScriptExecution != null && mScriptExecution!!.engine != null &&
                !mScriptExecution!!.engine.isDestroyed) {
            return
        }
        try {
            val source = JavaScriptFileSource("main", mMainScriptFile)
            val config = ExecutionConfig(workingDirectory = mProjectDir)
            if (source.executionMode and JavaScriptSource.EXECUTION_MODE_UI != 0) {
                config.intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
            } else {
                activity?.finish()
            }
            mScriptExecution = AutoJs.instance.scriptEngineService.execute(source, config)
        } catch (e: Exception) {
            AutoJs.instance.globalConsole.error(e)
        }

    }

    private fun prepare() {
        val projectConfigPath = PFiles.join(mProjectDir, ProjectConfig.CONFIG_FILE_NAME)
        val projectConfig = ProjectConfig.fromFile(projectConfigPath)
        if (projectConfig != null &&
                TextUtils.equals(projectConfig.buildInfo.buildId, mProjectConfig.buildInfo.buildId)) {
            initKey(projectConfig)
            return
        }
        initKey(mProjectConfig)
        PFiles.deleteRecursively(File(mProjectDir))
        try {
            PFiles.copyAssetDir(mActivity.assets, mAssetsProjectDir, mProjectDir, null)
        } catch (e: IOException) {
            throw UncheckedIOException(e)
        }
    }

    private fun initKey(projectConfig: ProjectConfig) {
        val key = MD5.md5(projectConfig.packageName + projectConfig.versionName + projectConfig.mainScriptFile)
        val vec = MD5.md5(projectConfig.buildInfo.buildId + projectConfig.name).substring(0, 16)
        try {
            val fieldKey = ScriptEncryption::class.java.getDeclaredField("mKey")
            fieldKey.isAccessible = true
            fieldKey.set(null, key)
            val fieldVector = ScriptEncryption::class.java.getDeclaredField("mInitVector")
            fieldVector.isAccessible = true
            fieldVector.set(null, vec)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}

package org.ys.game.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.*
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import org.ys.game.AutoJs
import org.ys.game.app.OnActivityResultDelegate
import org.ys.game.app.OnActivityResultDelegate.DelegateHost
import org.ys.game.core.accessibility.AccessibilityTool
import org.ys.game.core.permission.RequestPermissionCallbacks
import org.ys.game.event.BackPressedHandler
import org.ys.game.event.BackPressedHandler.DoublePressExit
import org.ys.game.event.BackPressedHandler.HostActivity
import org.ys.game.external.foreground.MainActivityForegroundService
import org.ys.game.model.explorer.Explorers
import org.ys.game.permission.DisplayOverOtherAppsPermission
import org.ys.game.permission.ManageAllFilesPermission
import org.ys.game.permission.PostNotificationPermission
import org.ys.game.pref.Pref
import org.ys.game.runtime.api.WrappedShizuku
import org.ys.game.theme.ThemeColorManager.addViewBackground
import org.ys.game.ui.BaseActivity
import org.ys.game.ui.enhancedfloaty.FloatyService
import org.ys.game.ui.floating.FloatyWindowManger
import org.ys.game.ui.log.LogActivity
import org.ys.game.ui.account.LoginActivity
import org.ys.game.ui.main.drawer.DrawerFragment
import org.ys.game.ui.settings.PreferencesActivity
import org.ys.game.ui.widget.DrawerAutoClose
import org.ys.game.user.UserManager
import org.ys.game.util.ForegroundServiceUtils
import org.ys.game.util.UpdateUtils
import org.ys.game.util.ViewUtils
import org.ys.game.util.WorkingDirectoryUtils
import org.ys.gamecat.R
import org.ys.gamecat.databinding.ActivityMainBinding
import org.greenrobot.eventbus.EventBus
import org.ys.gamecat.BuildConfig
import java.net.URL

/**
 * Modified by SuperMonster003 as of Dec 1, 2021.
 * Transformed by SuperMonster003 on May 11, 2023.
 */
class MainActivity : BaseActivity(), DelegateHost, HostActivity {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mLogMenuItem: MenuItem
    private lateinit var mDrawerLayout: DrawerLayout
    private val mActivityResultMediator = OnActivityResultDelegate.Mediator()
    private val mRequestPermissionCallbacks = RequestPermissionCallbacks()
    private val mBackPressObserver = BackPressedHandler.Observer()

    private val mA11yToolService by lazy {
        AccessibilityTool(this).service
    }

    val requestMultiplePermissionsLauncher = registerForActivityResult(RequestMultiplePermissions()) {
        it.forEach { (key: String, isGranted: Boolean) ->
            Log.d(TAG, "$key: $isGranted")
            if (key == Manifest.permission.POST_NOTIFICATIONS) {
                Pref.putBoolean(R.string.key_post_notification_permission_requested, true)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 调试时重置登录状态
        if (!UserManager.isLoggedIn(this)) {
            // 用户未登录,打开登录页面
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // 结束MainActivity,防止用户按返回键回到未登录状态的主页面
            return
        }
        
        UserManager.refreshUserInfo(this) { isMember ->
            // 可以在这里处理刷新后的会员状态
        }

        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
            mDrawerLayout = it.drawerLayout
        }

        PostNotificationPermission(this).urgeIfNeeded()
        ManageAllFilesPermission(this).urgeIfNeeded()
        WorkingDirectoryUtils.determineIfNeeded()
        DisplayOverOtherAppsPermission(this).urgeIfNeeded()
        FloatyWindowManger.refreshCircularMenuIfNeeded(this)

        @Suppress("DEPRECATION")
        ViewUtils.appendSystemUiVisibility(this, View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        ViewUtils.registerOnSharedPreferenceChangeListener(this)

        WrappedShizuku.onCreate()
        setUpToolbar()
        registerBackPressHandlers()
        addViewBackground(binding.appBar)
        setUpWebView()
        // 删除或注释掉以下代码
        // supportFragmentManager.beginTransaction()
        //     .replace(R.id.content_frame, ExplorerFragment())
        //     .commit()

        // 如果需要,可以添加一个新的默认Fragment或视图
        // 例如:
        // binding.contentFrame.addView(TextView(this).apply {
        //     text = "欢迎使用AutoJs6"
        //     gravity = Gravity.CENTER
        // })
    }

    override fun onPostResume() {
        recreateIfNeeded()
        UpdateUtils.autoCheckForUpdatesIfNeededWithSnackbar(this)
        super.onPostResume()
    }

    override fun onStart() {
        super.onStart()
        WrappedShizuku.bindUserServiceIfNeeded()
    }

    private fun recreateIfNeeded() {
        if (shouldRecreateMainActivity) {
            shouldRecreateMainActivity = false
            recreate()
            Explorers.workspace().refreshAll()
        }
    }

    private fun registerBackPressHandlers() {
        mBackPressObserver.registerHandler(DrawerAutoClose(mDrawerLayout, Gravity.START))
        mBackPressObserver.registerHandler(DoublePressExit(this, R.string.text_press_again_to_exit))
    }

    private fun setUpToolbar() {
        val toolbar = binding.toolbar.also {
            setSupportActionBar(it)
            it.setTitle(R.string.app_name)
            it.setOnLongClickListener { true.also { PreferencesActivity.launch(this) } }
        }

        object : ActionBarDrawerToggle(
            this,
            mDrawerLayout,
            toolbar,
            R.string.text_drawer_open,
            R.string.text_drawer_close
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                EventBus.getDefault().post(object : DrawerFragment.Companion.Event.OnDrawerOpened {})
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                EventBus.getDefault().post(object : DrawerFragment.Companion.Event.OnDrawerClosed {})
            }
        }.also {
            it.syncState()
            mDrawerLayout.addDrawerListener(it)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView() {
        binding.webView.apply {

            setWebViewClient(object : WebViewClient() {
//                override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
//                    request?.url?.let { url ->
//                        try {
//                            val connection = URL(url.toString()).openConnection()
//                            connection.connectTimeout = 5000 // 5秒超时
//                        } catch (e: Exception) {
//                            // 处理超时异常
////                            hideLoading()
//                        }
//                    }
//                    return super.shouldInterceptRequest(view, request)
//                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
//                    showLoading()
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
//                    hideLoading()
                }


//                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
//                    super.onReceivedError(view, request, error)
//                    if (error?.errorCode == ERROR_TIMEOUT || error?.errorCode == ERROR_HOST_LOOKUP || error?.errorCode == ERROR_CONNECT) {
//                        // 在超时或网络错误的情况下，尝试加载缓存
//                        view?.loadUrl(request?.url.toString())
//                    } else {
//                        hideLoading()
//                        showErrorMessage()
//                    }
//                }
            })
            settings.javaScriptEnabled = true
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK)
            settings.setDomStorageEnabled(true)
            loadUrl(BuildConfig.BASE_URL + "/static/setting.html")
            addJavascriptInterface(WebAppInterface(this@MainActivity), "Android")
        }

        WebView.setWebContentsDebuggingEnabled(true)
        
        // 加载URL
    }

//    private fun showLoading() {
//        binding.loadingProgressBar.visibility = View.VISIBLE
//        binding.webView.visibility = View.INVISIBLE
//        binding.errorMessageTextView.visibility = View.GONE
//    }
//
//    private fun hideLoading() {
//        binding.loadingProgressBar.visibility = View.GONE
//        binding.webView.visibility = View.VISIBLE
//        binding.errorMessageTextView.visibility = View.GONE
//    }
//
//    private fun showErrorMessage() {
//        binding.loadingProgressBar.visibility = View.GONE
//        binding.webView.visibility = View.GONE
//        binding.errorMessageTextView.visibility = View.VISIBLE
//    }

    fun rebirth(view: View) {
        val context = view.context as MainActivity
        context.packageManager.getLaunchIntentForPackage(context.packageName)?.let {
            context.startActivity(Intent.makeRestartActivityTask(it.component))
        }
        context.exitCompletely()
    }

    fun exitCompletely() {
        FloatyWindowManger.hideCircularMenuAndSaveState()
        ForegroundServiceUtils.stopServiceIfNeeded(this, MainActivityForegroundService::class.java)
        stopService(Intent(this, FloatyService::class.java))
        AutoJs.instance.scriptEngineService.stopAll()
        mA11yToolService.stop(false)
        Process.killProcess(Process.myPid())
    }

    @Suppress("OVERRIDE_DEPRECATION")
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mActivityResultMediator.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (mRequestPermissionCallbacks.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return
        }
        if (getGrantResult(permissions, grantResults) == PackageManager.PERMISSION_GRANTED) {
            Explorers.workspace().refreshAll()
        }
    }

    private fun getGrantResult(permissions: Array<String>, grantResults: IntArray): Int {
        val i = listOf(*permissions).indexOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        return if (i < 0) 2 else grantResults[i]
    }

    override fun getOnActivityResultDelegateMediator() = mActivityResultMediator

    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    override fun onBackPressed() {
//        val fragment = supportFragmentManager.findFragmentById(R.id.content_frame)
//        if (fragment is BackPressedHandler) {
//            if ((fragment as BackPressedHandler).onBackPressed(this)) {
//                return
//            }
//        }
//        if (!mBackPressObserver.onBackPressed(this)) {
//            super.onBackPressed()
//        }
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun getBackPressedObserver() = mBackPressObserver

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        mLogMenuItem = menu.findItem(R.id.action_log)
//        setUpSearchMenuItem(menu.findItem(R.id.action_search))

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_log) {
            LogActivity.launch(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

/*    private fun setUpSearchMenuItem(searchMenuItem: MenuItem) {
        mSearchViewItem = object : SearchViewItem(this, searchMenuItem) {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                if (isCurrentPageDocs) {
                    mDocsSearchItemExpanded = true
                    mLogMenuItem.setIcon(R.drawable.ic_ali_up)
                }
                return super.onMenuItemActionExpand(item)
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                if (mDocsSearchItemExpanded) {
                    mDocsSearchItemExpanded = false
                    mLogMenuItem.setIcon(R.drawable.ic_ali_log)
                }
                return super.onMenuItemActionCollapse(item)
            }
        }.apply {
            setQueryCallback { query: String? -> submitQuery(query) }
        }
    }*/

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        WrappedShizuku.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        ViewUtils.configKeepScreenOnWhenInForeground(this)
    }

    companion object {

        private val TAG = MainActivity::class.java.simpleName

        var shouldRecreateMainActivity = false

        @JvmStatic
        fun launch(context: Context) = context.startActivity(getIntent(context).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

        @JvmStatic
        fun getIntent(context: Context?) = Intent(context, MainActivity::class.java)

    }

    class WebAppInterface(private val context: Context) {
        @JavascriptInterface
        fun saveScriptConfig(data: String) {
            val sharedPref = context.getSharedPreferences("autojs.localstorage.script_config", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("config", data)
                apply()
            }
        }

        @JavascriptInterface
        fun loadScriptConfig(): String? {
            val sharedPref = context.getSharedPreferences("autojs.localstorage.script_config", Context.MODE_PRIVATE)
            return sharedPref.getString("config", null)
        }
    }
}
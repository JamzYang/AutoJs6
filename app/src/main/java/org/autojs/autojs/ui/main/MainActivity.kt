package org.autojs.autojs.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import org.autojs.autojs.AutoJs
import org.autojs.autojs.app.OnActivityResultDelegate
import org.autojs.autojs.app.OnActivityResultDelegate.DelegateHost
import org.autojs.autojs.core.accessibility.AccessibilityTool
import org.autojs.autojs.core.permission.RequestPermissionCallbacks
import org.autojs.autojs.event.BackPressedHandler
import org.autojs.autojs.event.BackPressedHandler.DoublePressExit
import org.autojs.autojs.event.BackPressedHandler.HostActivity
import org.autojs.autojs.external.foreground.MainActivityForegroundService
import org.autojs.autojs.model.explorer.Explorers
import org.autojs.autojs.permission.DisplayOverOtherAppsPermission
import org.autojs.autojs.permission.ManageAllFilesPermission
import org.autojs.autojs.permission.PostNotificationPermission
import org.autojs.autojs.pref.Pref
import org.autojs.autojs.runtime.api.WrappedShizuku
import org.autojs.autojs.theme.ThemeColorManager.addViewBackground
import org.autojs.autojs.ui.BaseActivity
import org.autojs.autojs.ui.Constants.BASE_URL
import org.autojs.autojs.ui.enhancedfloaty.FloatyService
import org.autojs.autojs.ui.floating.FloatyWindowManger
import org.autojs.autojs.ui.log.LogActivity
import org.autojs.autojs.ui.account.LoginActivity
import org.autojs.autojs.ui.main.drawer.DrawerFragment
import org.autojs.autojs.ui.settings.PreferencesActivity
import org.autojs.autojs.ui.widget.DrawerAutoClose
import org.autojs.autojs.user.UserManager
import org.autojs.autojs.util.ForegroundServiceUtils
import org.autojs.autojs.util.UpdateUtils
import org.autojs.autojs.util.ViewUtils
import org.autojs.autojs.util.WorkingDirectoryUtils
import org.autojs.autojs6.R
import org.autojs.autojs6.databinding.ActivityMainBinding
import org.greenrobot.eventbus.EventBus

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
//        setContentView(R.layout.activity_main)
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
        val webView = findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(WebAppInterface(this), "Android")

        binding.webView.apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
//            loadUrl("https://www.baidu.com")
            loadUrl(BASE_URL + "/setting.html")
        }
    }

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
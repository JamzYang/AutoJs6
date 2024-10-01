package org.ys.game.ui.main.drawer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import org.ys.game.app.tool.FloatingButtonTool
import org.ys.game.app.tool.JsonSocketClientTool
import org.ys.game.app.tool.JsonSocketServerTool
import org.ys.game.core.accessibility.AccessibilityTool
import org.ys.game.permission.*
import org.ys.game.pluginclient.DevPluginService
import org.ys.game.pluginclient.JsonSocketClient
import org.ys.game.pluginclient.JsonSocketServer
import org.ys.game.service.AccessibilityService
import org.ys.game.service.ForegroundService
import org.ys.game.service.NotificationService
import org.ys.game.ui.account.AccountActivity
import org.ys.game.ui.floating.FloatyWindowManger
import org.ys.game.ui.main.MainActivity
import org.ys.game.ui.membership.FreeMembershipActivity
import org.ys.game.ui.notification.NotificationActivity
import org.ys.game.ui.settings.PreferencesActivity
import org.ys.game.util.NetworkUtils
import org.ys.game.util.ViewUtils
import org.ys.gamecat.R
import org.ys.gamecat.databinding.FragmentDrawerBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import android.provider.Settings
import android.net.Uri
/**
 * Created by Stardust on Jan 30, 2017.
 * Modified by SuperMonster003 as of Nov 16, 2021.
 * Transformed by SuperMonster003 on Sep 19, 2022.
 */
open class DrawerFragment : Fragment() {

    private lateinit var binding: FragmentDrawerBinding

    private lateinit var mDrawerMenu: RecyclerView
    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity

    private lateinit var mAccessibilityServiceItem: DrawerMenuToggleableItem
    private lateinit var mForegroundServiceItem: DrawerMenuToggleableItem
    private lateinit var mFloatingWindowItem: DrawerMenuToggleableItem
    private lateinit var mClientModeItem: DrawerMenuDisposableItem
    private lateinit var mServerModeItem: DrawerMenuDisposableItem
    private lateinit var mNotificationPostItem: DrawerMenuToggleableItem
    private lateinit var mNotificationAccessItem: DrawerMenuToggleableItem
    private lateinit var mUsageStatsPermissionItem: DrawerMenuToggleableItem
    private lateinit var mIgnoreBatteryOptimizationsItem: DrawerMenuToggleableItem
    private lateinit var mDisplayOverOtherAppsItem: DrawerMenuToggleableItem
    private lateinit var mWriteSystemSettingsItem: DrawerMenuToggleableItem
    private lateinit var mWriteSecuritySettingsItem: DrawerMenuToggleableItem
    private lateinit var mProjectMediaAccessItem: DrawerMenuToggleableItem
    private lateinit var mShizukuAccessItem: DrawerMenuToggleableItem

//    private lateinit var mAboutAppAndDevItem: DrawerMenuShortcutItem

    private lateinit var mAccountItem: DrawerMenuShortcutItem
    private lateinit var mMemberCenterItem: DrawerMenuShortcutItem
    private lateinit var mFreeMembershipItem: DrawerMenuShortcutItem

    private lateinit var mA11yService: AccessibilityTool.Service

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EventBus.getDefault().register(this)

        mContext = requireContext()

        mActivity = (requireActivity() as MainActivity)

        mA11yService = AccessibilityTool(mContext).service

        mAccessibilityServiceItem = DrawerMenuToggleableItem(
            object : AccessibilityService(mContext) {

                override fun refreshSubtitle(aimState: Boolean) {
                    val oldSubtitle = mAccessibilityServiceItem.subtitle
                    if (aimState) {
                        if (mA11yService.exists() && !mA11yService.isRunning()) {
                            mAccessibilityServiceItem.subtitle = mContext.getString(R.string.text_malfunctioning)
                        } else {
                            mAccessibilityServiceItem.subtitle = null
                        }
                    } else {
                        mAccessibilityServiceItem.subtitle = null
                    }
                    if (mAccessibilityServiceItem.subtitle != oldSubtitle) {
                        /* To refresh subtitle view. */
                        mAccessibilityServiceItem.isChecked = mAccessibilityServiceItem.isChecked
                    }
                    super.refreshSubtitle(aimState)
                }

            },
            R.drawable.ic_accessibility_black_48dp,
            R.string.text_a11y_service,
            DrawerMenuItem.DEFAULT_DIALOG_CONTENT,
            R.string.key_a11y_service,
        )

        mForegroundServiceItem = DrawerMenuToggleableItem(
            ForegroundService(mContext),
            R.drawable.ic_service_green,
            R.string.text_foreground_service,
            R.string.text_foreground_service_description,
            R.string.key_foreground_service,
        )

        mFloatingWindowItem = DrawerMenuToggleableItem(
            object : FloatingButtonTool(mContext) {
                override fun toggle(aimState: Boolean): Boolean = try {
                    // @BeforeSuper
                    if (!aimState /* is to switch off */) {
                        FloatyWindowManger.getCircularMenu()?.let { circularMenu ->
                            if (circularMenu.isRecording) {
                                circularMenu.stopRecord()
                            }
                        }
                    }

                    super.toggle(aimState)

                    // @AfterSuper
                    // if (aimState /* is to switch on */) {
                    //     if (!mAccessibilityServiceItem.isChecked) {
                    //         mAccessibilityServiceItem.syncDelay()
                    //     }
                    // }
                    true
                } catch (_: Exception) {
                    false
                }
            },
            R.drawable.ic_robot_64,
            R.string.text_floating_button,
            DrawerMenuItem.DEFAULT_DIALOG_CONTENT,
            R.string.key_floating_menu_shown,
        )

        JsonSocketClientTool(mContext).apply {
            mClientModeItem = DrawerMenuDisposableItem(this, R.drawable.ic_computer_black_48dp, R.string.text_client_mode).also {
                setClientModeItem(it)
            }
            setStateDisposable(JsonSocketClient.cxnState
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.state == DevPluginService.State.DISCONNECTED) {
                        mClientModeItem.subtitle = null
                    }
                    consumeJsonSocketItemState(mClientModeItem, it)
                })
            setOnConnectionException { e: Throwable ->
                mClientModeItem.setCheckedIfNeeded(false)
                ViewUtils.showToast(mContext, getString(R.string.error_connect_to_remote, e.message), true)
            }
            setOnConnectionDialogDismissed { mClientModeItem.setCheckedIfNeeded(false) }
            connectIfNotNormallyClosed()
        }

        JsonSocketServerTool(mContext).apply {
            setStateDisposable(JsonSocketServer.cxnState
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { state: DevPluginService.State ->
                    mServerModeItem.subtitle = takeIf { state.state == DevPluginService.State.CONNECTED }?.let {
                        NetworkUtils.getIpAddress()
                    }
                    consumeJsonSocketItemState(mServerModeItem, state)
                })
            setOnConnectionException { e: Throwable ->
                mServerModeItem.setCheckedIfNeeded(false)
                ViewUtils.showToast(mContext, getString(R.string.error_enable_server, e.message), true)
            }
            mServerModeItem = DrawerMenuDisposableItem(this, R.drawable.ic_smartphone_black_48dp, R.string.text_server_mode)
            connectIfNotNormallyClosed()
        }

        mNotificationPostItem = DrawerMenuToggleableItem(
            PostNotificationPermission(mContext),
            R.drawable.ic_ali_notification,
            R.string.text_post_notifications_permission,
        )

        mNotificationAccessItem = DrawerMenuToggleableItem(
            NotificationService(mContext),
            R.drawable.ic_ali_notification,
            R.string.text_notification_access_permission,
        )

        mUsageStatsPermissionItem = DrawerMenuToggleableItem(
            UsageStatsPermission(mContext),
            R.drawable.ic_assessment_black_48dp,
            R.string.text_usage_stats_permission,
            R.string.text_usage_stats_permission_description,
        )

        mIgnoreBatteryOptimizationsItem = DrawerMenuToggleableItem(
            IgnoreBatteryOptimizationsPermission(mContext),
            R.drawable.ic_battery_std_black_48dp,
            R.string.text_ignore_battery_optimizations,
        )

        mDisplayOverOtherAppsItem = DrawerMenuToggleableItem(
            DisplayOverOtherAppsPermission(mContext),
            R.drawable.ic_layers_black_48dp,
            R.string.text_display_over_other_app,
        )

        mWriteSystemSettingsItem = DrawerMenuToggleableItem(
            WriteSystemSettingsPermission(mContext),
            R.drawable.ic_settings_black_48dp,
            R.string.text_write_system_settings,
        )

        mWriteSecuritySettingsItem = DrawerMenuToggleableItem(
            WriteSecureSettingsPermission(mContext),
            R.drawable.ic_security_black_48dp,
            R.string.text_write_secure_settings,
            R.string.text_write_secure_settings_description,
        )

        mProjectMediaAccessItem = DrawerMenuToggleableItem(
            MediaProjectionPermission(mContext),
            R.drawable.ic_cast_connected_black_48dp,
            R.string.text_project_media_access,
            R.string.text_project_media_access_description,
        )

        mShizukuAccessItem = DrawerMenuToggleableItem(
            ShizukuPermission(mContext),
            R.drawable.ic_app_shizuku_representative,
            R.string.text_shizuku_access,
            R.string.text_shizuku_access_description,
        )




        mAccountItem = DrawerMenuShortcutItem(R.drawable.ic_person_black_48dp, R.string.text_account)
            .setAction(Runnable {
                // 打开账号页面
                startActivity(Intent(requireContext(), AccountActivity::class.java))
            })

        mMemberCenterItem = DrawerMenuShortcutItem(R.drawable.ic_star_black_48dp, R.string.text_member_center)
            .setAction(Runnable { /* 打开会员中心页面 */ })

        mFreeMembershipItem = DrawerMenuShortcutItem(R.drawable.ic_card_giftcard_black_48dp, R.string.text_free_membership)
            .setAction(Runnable { 
                startActivity(Intent(requireContext(), FreeMembershipActivity::class.java))
            })

        // 检查浮动按钮开关状态
//        if (!ViewUtils.isFloatingButtonEnabled) {
//            mFloatingWindowItem.toggle(true) // 尝试开启浮动按钮
//        }

        // 检查忽略电池优化权限
        if (!IgnoreBatteryOptimizationsPermission.isGranted(mContext)) {
            // 引导用户去设置中开启权限
            showBatteryOptimizationSettings()
        }
    }

    // 引导用户去设置中开启忽略电池优化权限
    @SuppressLint("BatteryLife")
    private fun showBatteryOptimizationSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
        intent.data = Uri.parse("package:${mContext.packageName}")
        startActivity(intent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentDrawerBinding.inflate(inflater, container, false).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDrawerMenu = binding.drawerMenu
        initMenuItems()
        initMenuItemStates()
        setupListeners()
    }

    private fun setupListeners() {
        binding.settings.setOnClickListener { view ->
            startActivity(
                Intent(view.context, PreferencesActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
        binding.inbox.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        syncMenuItemStates()
        val unreadCount = getUnreadNotificationCount()
        updateNotificationBadge(unreadCount)
    }

    override fun onDestroy() {
        super.onDestroy()
        mClientModeItem.dispose()
        mServerModeItem.dispose()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    @Suppress("UNUSED_PARAMETER")
    fun onDrawerOpened(event: Event.OnDrawerOpened) {
        syncMenuItemStates()
    }

    @Subscribe
    @Suppress("UNUSED_PARAMETER")
    fun onDrawerClosed(event: Event.OnDrawerClosed) {
    }

    private fun initMenuItems() {
        drawerMenuAdapter = listOf(
            DrawerMenuGroup(R.string.text_my_account),
            mAccountItem,
            mMemberCenterItem,
            mFreeMembershipItem,
            DrawerMenuGroup(R.string.text_service),
            mAccessibilityServiceItem,
            mForegroundServiceItem,
            DrawerMenuGroup(R.string.text_tools),
            mFloatingWindowItem,
            DrawerMenuGroup(R.string.text_connect_to_pc),
            mClientModeItem,
            mServerModeItem,
            DrawerMenuGroup(R.string.text_permissions),
            mNotificationPostItem,
            mNotificationAccessItem,
            mUsageStatsPermissionItem,
            mIgnoreBatteryOptimizationsItem,
            mDisplayOverOtherAppsItem,
            mWriteSystemSettingsItem,
            mWriteSecuritySettingsItem,
            mProjectMediaAccessItem,
            mShizukuAccessItem,
        ).let { items -> DrawerMenuAdapter(items.filterNot { it.isHidden }) }

        mDrawerMenu.apply {
            adapter = drawerMenuAdapter
            layoutManager = LinearLayoutManager(mContext)
        }
    }

    private fun initMenuItemStates() = listOf(
        mFloatingWindowItem,
        mForegroundServiceItem,
    ).forEach { it.selfActive() }

    private fun syncMenuItemStates() = listOf(
        mAccessibilityServiceItem,
        mForegroundServiceItem,
        mFloatingWindowItem,
        mClientModeItem,
        mServerModeItem,
        mNotificationPostItem,
        mNotificationAccessItem,
        mUsageStatsPermissionItem,
        mIgnoreBatteryOptimizationsItem,
        mDisplayOverOtherAppsItem,
        mWriteSystemSettingsItem,
        mWriteSecuritySettingsItem,
        mProjectMediaAccessItem,
        mShizukuAccessItem,
    ).forEach { it.sync() }

    private fun consumeJsonSocketItemState(item: DrawerMenuToggleableItem, state: DevPluginService.State) {
        item.setCheckedIfNeeded(state.state == DevPluginService.State.CONNECTED)
        item.isProgress = state.state == DevPluginService.State.CONNECTING
        state.exception?.let { e ->
            item.subtitle = null
            ViewUtils.showToast(mContext, e.message)
        }
    }

    private fun updateNotificationBadge(unreadCount: Int) {
        if (unreadCount > 0) {
            binding.inboxBadge.visibility = View.VISIBLE
            binding.inboxBadge.text = unreadCount.toString()
        } else {
            binding.inboxBadge.visibility = View.GONE
        }
    }

    private fun getUnreadNotificationCount(): Int {
        // TODO: 实现获取未读消息数量的逻辑
        return 3
    }

    companion object {

        lateinit var drawerMenuAdapter: DrawerMenuAdapter
            private set

        class Event {
            interface OnDrawerOpened
            interface OnDrawerClosed
        }

    }

}
package org.ys.game.ui.main.scripts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.android.schedulers.AndroidSchedulers
import org.ys.game.app.GlobalAppContext
import org.ys.game.external.fileprovider.AppFileProvider
import org.ys.game.model.explorer.ExplorerItem
import org.ys.game.model.script.Scripts.edit
import org.ys.game.tool.SimpleObserver
import org.ys.game.ui.common.ScriptOperations
import org.ys.game.ui.explorer.ExplorerView
import org.ys.game.ui.main.FloatingActionMenu
import org.ys.game.ui.main.FloatingActionMenu.OnFloatingActionButtonClickListener
import org.ys.game.ui.main.QueryEvent
import org.ys.game.ui.main.ViewPagerFragment
import org.ys.game.ui.main.ViewStatesManageable
import org.ys.game.ui.project.ProjectConfigActivity
import org.ys.game.util.IntentUtils.viewFile
import org.ys.gamecat.R
import org.ys.gamecat.databinding.FragmentExplorerBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Created by Stardust on Mar 13, 2017.
 * Modified by SuperMonster003 as of Mar 20, 2022.
 * Transformed by SuperMonster003 on Mar 31, 2023.
 */
open class ExplorerFragment : ViewPagerFragment(0), OnFloatingActionButtonClickListener, ViewStatesManageable {

    private lateinit var binding: FragmentExplorerBinding

    private var mExplorerView: ExplorerView? = null
    private var mFloatingActionMenu: FloatingActionMenu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentExplorerBinding.inflate(inflater, container, false).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mExplorerView = binding.itemList.apply {
            setOnItemClickListener { _, item: ExplorerItem ->
                if (item.isEditable) {
                    edit(requireActivity(), item.toScriptFile())
                } else {
                    viewFile(GlobalAppContext.get(), item.path, AppFileProvider.AUTHORITY)
                }
            }
        }
        restoreViewStates()
    }



    override fun onBackPressed(activity: Activity): Boolean {
        mFloatingActionMenu?.let {
            if (it.isExpanded) {
                it.collapse()
                return@onBackPressed true
            }
        }
        mExplorerView?.let {
            if (it.canGoBack()) {
                it.goBack()
                return@onBackPressed true
            }
        }
        return false
    }

    override fun onFabClick(fab: FloatingActionButton?) {
//        TODO("Not yet implemented")
    }

    override fun onPageHide() {
        super.onPageHide()
        mFloatingActionMenu?.let { if (it.isExpanded) it.collapse() }
    }

    @Subscribe
    fun onQuerySummit(event: QueryEvent) {
        if (!isShown) {
            return
        }
        if (event === QueryEvent.CLEAR) {
            mExplorerView?.setFilter(null)
            return
        }
        mExplorerView?.setFilter { item: ExplorerItem -> item.name.contains(event.query) }
    }

    override fun onStop() {
        super.onStop()
        if (activity?.isFinishing == false) {
            saveViewStates()
        }
        mExplorerView?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        ExplorerView.clearViewStates()
        EventBus.getDefault().unregister(this)
    }

    override fun onDetach() {
        super.onDetach()
        mFloatingActionMenu?.setOnFloatingActionButtonClickListener(null)
    }

    override fun onClick(button: FloatingActionButton, pos: Int) {
        mExplorerView?.let { view ->
            when (pos) {
                0 -> ScriptOperations(context, view, view.currentPage)
                    .newDirectory()
                1 -> ScriptOperations(context, view, view.currentPage)
                    .newFile()
                2 -> ScriptOperations(context, view, view.currentPage)
                    .importFile()
                3 -> context?.startActivity(
                    Intent(context, ProjectConfigActivity::class.java)
                        .putExtra(ProjectConfigActivity.EXTRA_PARENT_DIRECTORY, view.currentPage.path)
                        .putExtra(ProjectConfigActivity.EXTRA_NEW_PROJECT, true)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                else -> {}
            }
        }
    }

    override fun saveViewStates() {
        mExplorerView?.saveViewStates()
    }

    override fun restoreViewStates() {
        mExplorerView?.restoreViewStates()
    }

}
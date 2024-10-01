package org.ys.game.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import org.ys.game.app.GlobalAppContext
import org.ys.game.ui.BaseActivity
import org.ys.gamecat.R
import org.ys.gamecat.databinding.ActivityPreferencesBinding

/**
 * Created by Stardust on Feb 2, 2017.
 * Modified by SuperMonster003 as of Dec 1, 2021.
 */
open class PreferencesActivity : BaseActivity() {

    private lateinit var binding: ActivityPreferencesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreferencesBinding.inflate(layoutInflater).also {
            setContentView(it.root)
            //标题栏设置
            it.toolbar.apply {
                setTitle(R.string.text_settings)
                setSupportActionBar(this)
                setNavigationOnClickListener { finish() }
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_preferences, PreferencesFragment())
            .disallowAddToBackStack()
            .commit()
    }

    companion object {

        @JvmStatic
        @JvmOverloads
        fun launch(context: Context = GlobalAppContext.get()) = try {
            true.also {
                Intent(context, PreferencesActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .let { context.startActivity(it) }
            }
        } catch (e: Exception) {
            false.also { e.printStackTrace() }
        }

    }

}
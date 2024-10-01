package org.ys.game.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.util.AttributeSet
import androidx.preference.Preference.SummaryProvider
import org.ys.game.pref.Pref
import org.ys.game.theme.preference.MaterialPreference
import org.ys.game.util.UpdateUtils
import org.ys.gamecat.R

/**
 * Created by SuperMonster003 on May 31, 2022.
 */
class CheckForUpdatesPreference : MaterialPreference, OnSharedPreferenceChangeListener {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context) : super(context)

    init {
        Pref.registerOnSharedPreferenceChangeListener(this)
        summaryProvider = SummaryProvider<CheckForUpdatesPreference> {
            Pref.lastUpdatesCheckedTimeString?.let { prefContext.getString(R.string.text_last_updates_checked_time, it) }
        }
    }

    override fun onClick() {
        UpdateUtils.getDialogChecker(prefContext).checkNow()
        super.onClick()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) = notifyChanged()

}
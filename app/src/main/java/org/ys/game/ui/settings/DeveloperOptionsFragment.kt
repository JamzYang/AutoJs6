package org.ys.game.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import org.ys.gamecat.R

class DeveloperOptionsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_developer_options, rootKey)
    }

}
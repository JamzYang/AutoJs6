package org.ys.game.theme.preference;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.ys.game.theme.ThemeColor;
import org.ys.game.theme.ThemeColorManager;
import org.ys.game.theme.ThemeColorMutable;
import org.ys.game.theme.internal.ScrollingViewEdgeGlowColorHelper;

/**
 * Created by Stardust on Aug 14, 2016.
 */
public class ThemeColorPreferenceFragment extends PreferenceFragmentCompat implements ThemeColorMutable {

    private int mThemeColor;
    private boolean hasAppliedThemeColor = false;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    @Override
    public void setPreferencesFromResource(int preferencesResId, @Nullable String key) {
        super.setPreferencesFromResource(preferencesResId, key);
        ThemeColorManager.add(this);
    }

    @Override
    public void setThemeColor(ThemeColor color) {
        if (mThemeColor == color.colorPrimary)
            return;
        mThemeColor = color.colorPrimary;
        hasAppliedThemeColor = false;
    }

    private void applyThemeColor() {
        RecyclerView listView = getListView();
        if (listView != null) {
            ScrollingViewEdgeGlowColorHelper.setEdgeGlowColor(listView, mThemeColor);
            hasAppliedThemeColor = true;
        }
    }

    public void onResume() {
        super.onResume();
        if (!hasAppliedThemeColor) {
            applyThemeColor();
        }
    }

}

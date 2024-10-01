package org.ys.game.theme.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.ys.game.theme.ThemeColor;
import org.ys.game.theme.ThemeColorManager;
import org.ys.game.theme.ThemeColorMutable;
import org.ys.gamecat.R;

/**
 * Created by Stardust on Jan 23, 2018.
 */
public class ThemeColorSwipeRefreshLayout extends SwipeRefreshLayout implements ThemeColorMutable {

    public ThemeColorSwipeRefreshLayout(Context context) {
        super(context);
        init();
    }

    public ThemeColorSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        ThemeColorManager.add(this);
        setProgressBackgroundColorSchemeResource(R.color.swipe_refresh_background);
    }

    @Override
    public void setThemeColor(ThemeColor themeColor) {
        setColorSchemeColors(themeColor.colorPrimary);
    }

}

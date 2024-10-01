package org.ys.game.theme.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.SwitchCompat;

import org.ys.game.theme.ThemeColor;
import org.ys.game.theme.ThemeColorHelper;
import org.ys.game.theme.ThemeColorManager;
import org.ys.game.theme.ThemeColorMutable;

/**
 * Created by Stardust on Aug 7, 2016.
 */
public class ThemeColorSwitch extends SwitchCompat implements ThemeColorMutable {
    public ThemeColorSwitch(Context context) {
        super(context);
        init();
    }

    public ThemeColorSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ThemeColorSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ThemeColorManager.add(this);
    }

    @Override
    public void setThemeColor(ThemeColor color) {
        ThemeColorHelper.setColorPrimary(this, color.colorPrimary);
    }
}

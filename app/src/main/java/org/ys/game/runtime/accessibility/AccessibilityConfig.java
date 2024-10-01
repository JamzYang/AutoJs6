package org.ys.game.runtime.accessibility;

import org.ys.game.pref.Pref;
import org.ys.game.util.DeveloperUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on Apr 29, 2017.
 */
public class AccessibilityConfig {

    private static boolean isUnintendedGuardEnabled = false;

    private final List<String> mWhiteList = new ArrayList<>();
    private boolean mSealed = false;

    public AccessibilityConfig() {
        if (isUnintendedGuardEnabled()) {
            mWhiteList.add(DeveloperUtils.selfPackage());
        }
    }

    public static boolean isUnintendedGuardEnabled() {
        return isUnintendedGuardEnabled;
    }

    public static void setIsUnintendedGuardEnabled(boolean isUnintendedGuardEnabled) {
        AccessibilityConfig.isUnintendedGuardEnabled = isUnintendedGuardEnabled;
    }

    public static void refreshUnintendedGuardState() {
        setIsUnintendedGuardEnabled(Pref.isGuardModeEnabled());
    }

    public boolean whiteListContains(String packageName) {
        return mWhiteList.contains(packageName);
    }

    public void addWhiteList(String packageName) {
        if (mSealed)
            throw new IllegalStateException("Sealed");
        mWhiteList.add(packageName);
    }

    public final void seal() {
        mSealed = true;
    }

}

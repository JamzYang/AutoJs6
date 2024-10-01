package org.ys.game.ui.explorer;

import static org.ys.game.util.FileUtils.TYPE.AUTO;
import static org.ys.game.util.FileUtils.TYPE.JAVASCRIPT;
import static org.ys.game.util.FileUtils.TYPE.PROJECT;
import static org.ys.game.util.FileUtils.TYPE.UNKNOWN;

import android.content.Context;

import androidx.annotation.NonNull;

import org.ys.game.model.explorer.ExplorerFileItem;
import org.ys.game.model.explorer.ExplorerItem;
import org.ys.game.model.explorer.ExplorerPage;
import org.ys.game.model.explorer.ExplorerProjectPage;
import org.ys.game.model.explorer.ExplorerSamplePage;
import org.ys.game.pio.PFiles;
import org.ys.game.pref.Language;
import org.ys.game.util.FileUtils.TYPE;
import org.ys.gamecat.R;

public class ExplorerViewHelper {

    public static String getDisplayName(Context context, ExplorerItem item) {
        if (item instanceof ExplorerSamplePage && ((ExplorerSamplePage) item).isRoot()) {
            return context.getString(R.string.text_sample);
        }
        if (item instanceof ExplorerPage) {
            return item.getName();
        }
        TYPE type = item.getType();
        if (type == JAVASCRIPT || type == AUTO) {
            if (item instanceof ExplorerFileItem) {
                return ((ExplorerFileItem) item).getFile().getSimplifiedName();
            }
            return PFiles.getNameWithoutExtension(item.getName());
        }
        return item.getName();
    }

    public static String getIconText(@NonNull ExplorerItem item) {
        TYPE type = item.getType();
        switch (type) {
            case UNKNOWN:
                return UNKNOWN.getIconText();
            case AUTO:
                return AUTO.getIconText();
        }
        if (item.getName().equalsIgnoreCase(PROJECT.getTypeName())) {
            return PROJECT.getIconText();
        }
        return type.getTypeName().substring(0, 1).toUpperCase(Language.getPrefLanguage().getLocale());
    }

    public static int getIcon(ExplorerPage page) {
        if (page instanceof ExplorerSamplePage) {
            return R.drawable.ic_sample_dir;
        }
        if (page instanceof ExplorerProjectPage) {
            return R.drawable.ic_project;
        }
        return R.drawable.ic_folder_yellow_100px;
    }

}

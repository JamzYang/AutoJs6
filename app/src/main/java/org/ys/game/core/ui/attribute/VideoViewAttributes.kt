package org.ys.game.core.ui.attribute

import android.view.View
import android.widget.VideoView
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.inflater.util.Strings

open class VideoViewAttributes(resourceParser: ResourceParser, view: View) : SurfaceViewAttributes(resourceParser, view) {

    override val view = super.view as VideoView

    override fun onRegisterAttrs() {
        super.onRegisterAttrs()

        registerAttrs(arrayOf("videoPath", "path", "src")) { view.setVideoPath(Strings.parsePath(view, it) ?: return@registerAttrs) }
    }

}

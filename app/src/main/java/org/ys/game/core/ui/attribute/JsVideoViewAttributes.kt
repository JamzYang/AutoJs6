package org.ys.game.core.ui.attribute

import android.view.View
import org.ys.game.core.ui.inflater.ResourceParser
import org.ys.game.core.ui.widget.JsVideoView

class JsVideoViewAttributes(resourceParser: ResourceParser, view: View) : VideoViewAttributes(resourceParser, view) {

    override val view = super.view as JsVideoView

    override fun onRegisterAttrs() {
        super.onRegisterAttrs()

        registerAttrs(arrayOf("controller", "mediaController", "isControllerEnabled", "controllerEnabled", "enableController", "isMediaControllerEnabled", "mediaControllerEnabled", "enableMediaController")) {
            when (it) {
                "null", "false" -> view.clearMediaController()
                "true" -> view.resetMediaController()
            }
        }
    }

}
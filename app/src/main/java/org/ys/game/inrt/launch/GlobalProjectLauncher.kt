package org.ys.game.inrt.launch

import android.annotation.SuppressLint
import org.ys.game.app.GlobalAppContext

/**
 * Created by Stardust on Mar 21, 2018.
 */
@SuppressLint("StaticFieldLeak")
object GlobalProjectLauncher: AssetsProjectLauncher("project", GlobalAppContext.get())

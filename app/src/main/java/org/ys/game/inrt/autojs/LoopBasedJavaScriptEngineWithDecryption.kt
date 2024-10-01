package org.ys.game.inrt.autojs

import android.content.Context
import org.ys.game.engine.LoopBasedJavaScriptEngine
import org.ys.game.engine.encryption.ScriptEncryption
import org.ys.game.pio.PFiles
import org.ys.game.script.EncryptedScriptFileHeader
import org.ys.game.script.EncryptedScriptFileHeader.BLOCK_SIZE
import org.ys.game.script.JavaScriptFileSource
import org.ys.game.script.ScriptSource
import org.ys.game.script.StringScriptSource

class LoopBasedJavaScriptEngineWithDecryption(context: Context) : LoopBasedJavaScriptEngine(context) {

    override fun execute(source: ScriptSource?, callback: ExecuteCallback?) {
        if (source is JavaScriptFileSource) {
            try {
                val file = source.file
                val bytes = PFiles.readBytes(file.path)
                if (EncryptedScriptFileHeader.isValidFile(bytes)) {
                    super.execute(StringScriptSource(file.name, String(ScriptEncryption.decrypt(bytes, BLOCK_SIZE))), callback)
                } else {
                    super.execute(source, callback)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        } else {
            super.execute(source, callback)
        }
    }

}
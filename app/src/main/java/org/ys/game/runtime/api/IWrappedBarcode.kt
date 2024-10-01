package org.ys.game.runtime.api

import com.google.mlkit.vision.barcode.common.internal.BarcodeSource

interface IWrappedBarcode : BarcodeSource {

    fun getFormatName(): String

    fun getValueTypeName(): String

    fun getType(): Int

    fun getTypeName(): String

}

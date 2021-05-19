package com.reynarz.shder3D.models

import android.graphics.Bitmap
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class TextureData(var name: String) {
    var id = 0
    var path: String? = null
    var filter = 0

    @Transient
    var previewBitmap: Bitmap? = null
}

//data class CubemapTextureData(var list: MutableList<String>) {
//
//}
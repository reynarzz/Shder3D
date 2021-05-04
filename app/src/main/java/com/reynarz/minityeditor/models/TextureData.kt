package com.reynarz.minityeditor.models

import kotlinx.serialization.Serializable

@Serializable
data class TextureData(var name: String) {
    var id = 0
    var path = ""
    var filter = 0
}

data class CubemapTextureData(var list: MutableList<String>) {

}
package com.reynarz.minityeditor.models

import android.util.Log
import java.util.*


data class SceneEntityData(var name: String) {
    var active = true
    var selected = false

    var transformData = TransformComponentData()
    var meshRendererData = MeshRendererComponentData()

    var UUid = ""
        private set

    init {
        UUid = UUID.randomUUID().toString()
    }
}
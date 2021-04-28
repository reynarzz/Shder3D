package com.reynarz.minityeditor.models

import android.util.Log
import java.util.*


data class SceneEntityData(var name: String) {
    var visible = true
    var selected = false

    val transformData = TransformComponentData()
    val meshRendererData = MeshRendererComponentData()

    var UUid = ""
        private set

    init {
        UUid = UUID.randomUUID().toString()
    }
}
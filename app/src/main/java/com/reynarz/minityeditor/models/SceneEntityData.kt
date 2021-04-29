package com.reynarz.minityeditor.models

import java.util.*


data class SceneEntityData(var name: String) {
    var active = true
    var selected = false

    var transformData = TransformComponentData()
    var meshRendererData = MeshRendererComponentData()

    var entityID = ""
        private set

    init {
        entityID = UUID.randomUUID().toString()
    }
}
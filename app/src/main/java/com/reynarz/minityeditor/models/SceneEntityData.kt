package com.reynarz.minityeditor.models

import java.util.*


data class SceneEntityData(var name: String) {
    var active = true
    var selected = false

    var transformData = TransformComponentData()
    var meshRendererData = MeshRendererComponentData()

    // this could be the path of the resource in the phone or a UUID
    var entityID = ""


    init {
        //entityID = UUID.randomUUID().toString()
    }
}
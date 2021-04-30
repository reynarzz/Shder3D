package com.reynarz.minityeditor.models

import kotlinx.serialization.*

@Serializable
data class SceneEntityData(var name: String) {
    var active = true
    var isSelected = false

    var transformData = TransformComponentData()
    var meshRendererData: MeshRendererComponentData = MeshRendererComponentData()

    // this could be the path of the resource in the phone or a UUID
    var entityID = ""


    init {
        //entityID = UUID.randomUUID().toString()
    }
}
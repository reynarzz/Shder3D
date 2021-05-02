package com.reynarz.minityeditor.models

import kotlinx.serialization.*
import java.util.*

@Serializable
data class SceneEntityData(
    var name: String,
    var transformData: TransformComponentData,
    var meshRendererData: MeshRendererComponentData
) {
    var active = true
    var isSelected = false

    //var transformData = TransformComponentData()
    // var meshRendererData: MeshRendererComponentData = MeshRendererComponentData()

    var entityModelPath = ""
    var entityID = ""

    init {
        entityID = UUID.randomUUID().toString()
    }
}
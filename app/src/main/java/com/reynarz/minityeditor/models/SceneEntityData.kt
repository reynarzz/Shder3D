package com.reynarz.minityeditor.models

import kotlinx.serialization.*
import java.util.*

@Serializable
enum class EntityType {
    Editor,
    User
}

@Serializable
data class SceneEntityData(
    var name: String,
    var transformData: TransformComponentData,
    var meshRendererData: MeshRendererComponentData
) {
    var active = true

    @Transient
    var isSelected = false

    //var transformData = TransformComponentData()
    // var meshRendererData: MeshRendererComponentData = MeshRendererComponentData()

    var entityModelPath = ""
    var entityID = ""
    var entityType: EntityType = EntityType.User

    init {
        entityID = UUID.randomUUID().toString()
    }
}
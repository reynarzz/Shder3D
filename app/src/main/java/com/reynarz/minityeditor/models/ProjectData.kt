package com.reynarz.minityeditor.models

import kotlinx.serialization.Serializable

@Serializable
data class ProjectData(var projectName: String) {
    var cameraTransformData = TransformComponentData("Camera Transform")

    var sceneEntities = mutableListOf<SceneEntityData>()

}
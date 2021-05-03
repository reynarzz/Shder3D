package com.reynarz.minityeditor.models

import kotlinx.serialization.Serializable

@Serializable
data class ProjectData(var projectName: String) {
    var selectedEntityIndex = 0
    var sceneEntities = mutableListOf<SceneEntityData>()

}
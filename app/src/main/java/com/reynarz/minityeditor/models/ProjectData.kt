package com.reynarz.minityeditor.models

import kotlinx.serialization.Serializable

@Serializable
data class ProjectData(var projectName: String) {
    var sceneEntities = mutableListOf<SceneEntityData>()

}
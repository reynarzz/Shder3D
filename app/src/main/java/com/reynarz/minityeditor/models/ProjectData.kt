package com.reynarz.minityeditor.models

data class ProjectData(var projectName: String) {
    val selectedEntityIndex = 0
    var sceneEntities = mutableListOf<SceneEntityData>()

}
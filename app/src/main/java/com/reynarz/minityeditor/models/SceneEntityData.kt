package com.reynarz.minityeditor.models

data class SceneEntityData(var name : String) {
    var visible = true
    var selected = false

    val transformData = TransformComponentData()
    val meshRendererData = MeshRendererComponentData()
}
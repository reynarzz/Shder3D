package com.reynarz.minityeditor.models

import com.reynarz.minityeditor.R
import kotlinx.serialization.Serializable

@Serializable
class MeshRendererComponentData() : ComponentData("Mesh Renderer", ComponentType.MeshRenderer) {
    var materialsData = mutableListOf<MaterialData>()
}
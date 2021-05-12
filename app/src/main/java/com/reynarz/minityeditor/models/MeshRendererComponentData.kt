package com.reynarz.minityeditor.models

import com.reynarz.minityeditor.R
import kotlinx.serialization.Serializable

@Serializable
class MeshRendererComponentData(private var cname:String = "Mesh Renderer") : ComponentData(cname, ComponentType.MeshRenderer) {
    var materialsData = mutableListOf<MaterialData?>()
}
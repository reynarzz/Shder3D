package com.reynarz.shder3D.models

import kotlinx.serialization.Serializable

@Serializable
class MeshRendererComponentData(private var cname:String = "Mesh Renderer") : ComponentData(cname, ComponentType.MeshRenderer) {
    var castShadows = true
    var materialsData = mutableListOf<MaterialData?>()
}
package com.reynarz.minityeditor.models

import com.reynarz.minityeditor.R
import kotlinx.serialization.Serializable

@Serializable
class MeshRendererComponentData() : ComponentData("Mesh Renderer", R.layout.mesh_renderer_fragment_view) {
    var materialsData = mutableListOf<MaterialData>()
}
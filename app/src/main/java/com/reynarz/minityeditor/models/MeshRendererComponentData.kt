package com.reynarz.minityeditor.models

import com.reynarz.minityeditor.R

class MeshRendererComponentData : ComponentData("Mesh Renderer", R.layout.mesh_renderer_fragment_view) {
    val materialsData = mutableListOf<MaterialData>()
}
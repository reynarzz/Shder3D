package com.example.viewer3d.engine.components

import com.example.viewer3d.engine.Material
import com.example.viewer3d.engine.Mesh
import com.example.viewer3d.engine.Shader

class MeshRenderer(private val mesh: Mesh, private val material: Material) : Component() {

    val indexBuffer = mesh.indexBuffer
    val indicesCount = mesh.indicesCount

    fun bind(view: FloatArray, projection: FloatArray) {
        material.bind(transform!!.modelM!!, view, projection)
        mesh.bind(material.program)
    }

    fun bindForDepth(view: FloatArray, projection: FloatArray, shader : Shader){
        material.bind(transform!!.modelM!!, view, projection, shader)
        mesh.bind(material.program)
    }
}
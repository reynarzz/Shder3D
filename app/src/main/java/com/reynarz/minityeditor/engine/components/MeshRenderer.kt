package com.reynarz.minityeditor.engine.components

import com.reynarz.minityeditor.engine.Material
import com.reynarz.minityeditor.engine.Mesh
import com.reynarz.minityeditor.engine.Shader

class MeshRenderer(private val mesh: Mesh, val material: Material) : Component() {

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
package com.reynarz.minityeditor.engine.components

import com.reynarz.minityeditor.engine.Material
import com.reynarz.minityeditor.engine.Mesh

class MeshRenderer(private val mesh: Mesh, var material: Material?) : Component() {

    val indexBuffer = mesh.indexBuffer
    val indicesCount = mesh.indicesCount
    val vertexCount = mesh.vertexCount

    fun bind(view: FloatArray, projection: FloatArray, default: Material) {

        val mat = if (material != null) material else default

        mat!!.bind(transform!!.modelM!!, view, projection)
        mesh.bind(mat!!.program)
    }

//    fun bindForDepth(view: FloatArray, projection: FloatArray, shader: Shader) {
//        material!!.bind(transform!!.modelM!!, view, projection, shader)
//        mesh.bind(material!!.program)
//    }
}
package com.reynarz.minityeditor.engine.components

import com.reynarz.minityeditor.engine.Material
import com.reynarz.minityeditor.engine.Mesh

class MeshRenderer() : Component() {

    lateinit var meshes: List<Mesh> //Mesh(FloatArray(1), IntArray(1), FloatArray(1))
    var materials = mutableListOf<Material?>()

//    val indexBuffer = mesh?.indexBuffer
//    val indicesCount = mesh?.indicesCount
//    val vertexCount = mesh?.vertexCount

    constructor(mesh: List<Mesh>, material: Material?) : this() {
        this.meshes = mesh
        materials.add(material)
    }

    fun bind(view: FloatArray?, projection: FloatArray?, default: Material?, meshIndex: Int) {

        val selectedMat = materials.getOrNull(meshIndex)

        var mat = if (selectedMat != null && selectedMat!!.shader.compiledCorrectly) selectedMat else default


        mat!!.bind(transform!!.modelM!!, view, projection)
        meshes[meshIndex].bind(mat!!.program)
    }

    fun bindWithMaterial(view: FloatArray, projection: FloatArray, mat: Material, meshIndex: Int) {

        mat!!.bind(transform!!.modelM!!, view, projection)
        meshes[meshIndex].bind(mat!!.program)
    }

    fun bindShadow(view: FloatArray, projection: FloatArray, default: Material, lightViewM: FloatArray, meshIndex: Int) {

        val mat = if (materials.getOrNull(meshIndex) != null && materials.getOrNull(meshIndex)!!.shader.compiledCorrectly) materials.getOrNull(meshIndex) else default

        mat!!.bind(transform!!.modelM!!, view, projection, lightViewM)
        meshes[meshIndex].bind(mat!!.program)
    }
}
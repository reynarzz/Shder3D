package com.reynarz.minityeditor.engine.components

import com.reynarz.minityeditor.engine.Material
import com.reynarz.minityeditor.engine.Mesh

class MeshRenderer() : Component() {

    lateinit var mesh: Mesh //Mesh(FloatArray(1), IntArray(1), FloatArray(1))
    var material: Material? = null
    var materials = mutableListOf<Material?>()

//    val indexBuffer = mesh?.indexBuffer
//    val indicesCount = mesh?.indicesCount
//    val vertexCount = mesh?.vertexCount

    constructor(mesh: Mesh, material: Material?) : this() {
        this.mesh = mesh
        this.material = material
    }

    fun bind(view: FloatArray?, projection: FloatArray?, default: Material?) {

        var mat = if (material != null && material!!.shader.compiledCorrectly) material else default

        // test, use the materials list instead.
        if(materials.size != 0){
            mat = materials[0]
        }

        mat!!.bind(transform!!.modelM!!, view, projection)
        mesh.bind(mat!!.program)
    }

    fun bindWithMaterial(view: FloatArray, projection: FloatArray, mat: Material) {

        mat!!.bind(transform!!.modelM!!, view, projection)
        mesh.bind(mat!!.program)
    }

    fun bindShadow(view: FloatArray, projection: FloatArray, default: Material, lightViewM: FloatArray) {

        val mat = if (material != null && material!!.shader.compiledCorrectly) material else default

        mat!!.bind(transform!!.modelM!!, view, projection, lightViewM)
        mesh.bind(mat!!.program)
    }
}
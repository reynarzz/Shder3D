package com.reynarz.minityeditor.engine.components

import com.reynarz.minityeditor.engine.Material
import com.reynarz.minityeditor.engine.Mesh

class MeshRenderer() : Component() {

    private var _meshes = listOf<Mesh>()

    var meshes: List<Mesh>
        get() {
            return _meshes
        }
        set(value) {
            _meshes = value
        }

    var materials = mutableListOf<Material?>()



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

        val selectedMat = materials.getOrNull(meshIndex)

        val mat = if (selectedMat != null && selectedMat!!.shader.compiledCorrectly) selectedMat else default

        mat!!.bind(transform!!.modelM!!, view, projection, lightViewM)
        meshes[meshIndex].bind(mat!!.program)
    }

    fun unBind() {

    }
}
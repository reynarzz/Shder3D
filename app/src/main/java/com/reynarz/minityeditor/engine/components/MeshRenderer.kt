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
    private var lastSelectedMat: Material? = null


    constructor(mesh: List<Mesh>, material: Material?) : this() {
        this.meshes = mesh
        materials.add(material)
    }

    fun bind(view: FloatArray?, projection: FloatArray?, default: Material?, meshIndex: Int) {

        lastSelectedMat = materials.elementAtOrNull(meshIndex)

        var mat = if (lastSelectedMat != null && lastSelectedMat!!.shader.compiledCorrectly) lastSelectedMat else default

        mat!!.bind(transform!!.modelM!!, view, projection)
        meshes[meshIndex].bind(mat!!.program)
    }
//
//    fun bindWithMaterial(view: FloatArray, projection: FloatArray, mat: Material, meshIndex: Int) {
//
//        mat!!.bind(transform!!.modelM!!, view, projection)
//        meshes[meshIndex].bind(mat!!.program)
//    }

    fun bindShadow(view: FloatArray, projection: FloatArray, default: Material, lightViewM: FloatArray, meshIndex: Int) {
        lastSelectedMat = materials.elementAtOrNull(meshIndex)

        val mat = if (lastSelectedMat != null && lastSelectedMat!!.shader.compiledCorrectly) lastSelectedMat else default

        mat!!.bind(transform!!.modelM!!, view, projection, lightViewM)
        meshes[meshIndex].bind(mat!!.program)
    }

    fun unBind() {
        lastSelectedMat?.unBind()
    }
}
package com.reynarz.shder3D.engine

import android.opengl.GLES20.*
import com.reynarz.shder3D.engine.components.SceneEntity
import com.reynarz.shder3D.models.MaterialConfig
import java.nio.IntBuffer

// A single mesh with a single material (this is equivalent to one pass)
class QueuedRenderableMesh(val meshindexInsideEntity: Int, val sceneEntity: SceneEntity, private val modelM: FloatArray, val mesh: Mesh, var material: Material?) {

    val entityID: String
        get() {
            return sceneEntity.entityID
        }

    val meshIndexBuffer: IntBuffer?
        get() {
            return mesh.indexBuffer
        }

    val meshIndicesCount: Int
        get() {
            return mesh.indicesCount
        }

    val Active: Boolean
        get() {
            return sceneEntity.entityData.active
        }

    val Selected: Boolean
        get() {
            return sceneEntity.entityData.isSelected
        }

    val RenderQueue: Int
        get() {
            return if (materialConfig?.renderQueue != null) materialConfig?.renderQueue!! else 0
        }

    val materialConfig: MaterialConfig?
        get() {
            return material?.materialData?.materialConfig
        }

    val canCastShadows: Boolean
        get() {
            return sceneEntity.entityData.meshRendererData.castShadows
        }

    fun bind(view: FloatArray?, projection: FloatArray?, default: Material?) {

        // lastSelectedMat = materials.elementAtOrNull(meshIndex)

        var mat = if (material != null && material!!.shader.compiledCorrectly) material else default

        mat!!.bind(modelM, view, projection)
        mesh.bind(mat!!.program)
    }

    fun bindWithMaterial(view: FloatArray, projection: FloatArray, mat: Material) {

        mat!!.bind(modelM, view, projection)
        mesh.bind(mat!!.program)
    }

    fun bindShadow(view: FloatArray, projection: FloatArray, default: Material, lightViewM: FloatArray) {
        //lastSelectedMat = materials.elementAtOrNull(meshIndex)

        var mat = if (material != null && material!!.shader.compiledCorrectly) material else default

        if (mat == default) {
            glEnable(GL_DEPTH_TEST)
        }

        mat!!.bind(modelM, view, projection, lightViewM)
        mesh.bind(mat!!.program)
    }

    fun unBind() {
        material?.unBind()
    }
}
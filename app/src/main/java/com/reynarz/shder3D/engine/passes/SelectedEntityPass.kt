package com.reynarz.shder3D.engine.passes

import android.opengl.GLES20.*
import com.reynarz.shder3D.engine.Material
import com.reynarz.shder3D.engine.QueuedRenderableMesh
import com.reynarz.shder3D.engine.Utils

class SelectedEntityPass : RenderPass() {

    private var outlineMaterial: Material

    init {
        outlineMaterial = Utils.getUnlitMaterial(1f)
    }

    override fun renderPass(entities: List<QueuedRenderableMesh>, sceneMatrices: SceneMatrices, errorMaterial: Material, test: RenderPassFrameBuffers) {

        for (entity in entities) {
            if (entity.active && entity.selected) {
                glLineWidth(1.3f)

                glEnable(GL_DEPTH_TEST)

                entity.bindWithMaterial(sceneMatrices.cameraViewM!!, sceneMatrices.cameraProjM!!, outlineMaterial)
                glDrawElements(GL_LINES, entity.meshIndicesCount, GL_UNSIGNED_INT, entity.meshIndexBuffer)
            }
        }
    }
}


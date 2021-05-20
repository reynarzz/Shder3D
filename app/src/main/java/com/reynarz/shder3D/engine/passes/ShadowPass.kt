package com.reynarz.shder3D.engine.passes

import android.opengl.GLES20.*
import com.reynarz.shder3D.engine.Material
import com.reynarz.shder3D.engine.QueuedRenderableMesh
import com.reynarz.shder3D.views.MainActivity

class ShadowPass : RenderPass() {

    init {
        fbo?.genBufferForDepth(MainActivity.width, MainActivity.height)
    }

    override fun renderPass(entities: List<QueuedRenderableMesh>, sceneMatrices: SceneMatrices, errorMaterial: Material, test: RenderPassFrameBuffers) {
        glViewport(0, 0, fbo?.width!!, fbo?.height!!)

        fbo?.bind()

        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)

        for (entity in entities) {
            if (entity.active && entity.canCastShadows && entity.materialConfig != null && entity.materialConfig!!.gl_depthTestEnabled) {

                entity.bind(sceneMatrices.directionalLightVIewM, sceneMatrices.directionalLightProjM, errorMaterial)

                glDrawElements(GL_TRIANGLES, entity.meshIndicesCount, GL_UNSIGNED_INT, entity.meshIndexBuffer)

                entity.unBind()
            }
        }

        fbo?.unBind()

        test.shadowFrameBuffer = fbo
    }
}
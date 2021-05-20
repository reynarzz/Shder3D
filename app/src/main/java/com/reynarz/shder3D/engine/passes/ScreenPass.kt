package com.reynarz.shder3D.engine.passes

import android.opengl.GLES20.*
import com.reynarz.shder3D.engine.Material
import com.reynarz.shder3D.engine.QueuedRenderableMesh
import com.reynarz.shder3D.views.MainActivity

// Gen a buffer to obtain the depth and color.
class ScreenPass : RenderPass() {

    init {
        fbo?.genNormalFrameBuffer(MainActivity.width, MainActivity.height, GL_CLAMP_TO_EDGE)
    }

    override fun renderPass(entities: List<QueuedRenderableMesh>, sceneMatrices: SceneMatrices, errorMaterial: Material, test: RenderPassFrameBuffers) {
        fbo?.bind()
        glViewport(0, 0, fbo?.width!!, fbo?.height!!)

        for (entity in entities) {
            if (entity.active && entity.materialConfig != null && entity.materialConfig?.gl_depthTestEnabled!! && !entity.materialConfig?.hiddeFromBackPass!!) {
                setApplyMaterialConfig_GL(entity.materialConfig)

                entity.bindShadow(sceneMatrices.cameraViewM!!, sceneMatrices.cameraProjM!!, errorMaterial, sceneMatrices.directionalLightVIewProjM)
                glActiveTexture(GL_TEXTURE0)
                glBindTexture(GL_TEXTURE_2D, test.shadowFrameBuffer?.depthTexture!!)


                glDrawElements(GL_TRIANGLES, entity.meshIndicesCount, GL_UNSIGNED_INT, entity.meshIndexBuffer)
                entity.unBind()
            }
        }

        fbo?.unBind()

        glActiveTexture(GL_TEXTURE0)
        test.screenPass =fbo
    }
}
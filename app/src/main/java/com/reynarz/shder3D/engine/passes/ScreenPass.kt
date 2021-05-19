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

        glClearColor(0.2f, 0.2f, 0.2f, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        for (entity in entities) {
            if (entity.Active && entity.materialConfig != null && entity.materialConfig?.gl_depthTestEnabled!!) {
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
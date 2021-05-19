package com.reynarz.minityeditor.engine.passes

import android.opengl.GLES20.*
import com.reynarz.minityeditor.engine.Material
import com.reynarz.minityeditor.engine.QueuedRenderableMesh
import com.reynarz.minityeditor.views.MainActivity

class FinalPass : RenderPass() {

    init {
        fbo?.genNormalFrameBuffer(MainActivity.width, MainActivity.height, GL_REPEAT)
    }

    override fun renderPass(entities: List<QueuedRenderableMesh>, sceneMatrices: SceneMatrices, errorMaterial: Material, test: RenderPassFrameBuffers) {
        fbo?.bind()
        glViewport(0, 0, fbo?.width!!, fbo?.height!!)


        glEnable(GL_DEPTH_TEST)

        glClearColor(0.2f, 0.2f, 0.2f, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        for (entity in entities) {
            if (entity.Active) {
                setApplyMaterialConfig_GL(entity.materialConfig)

                entity.bindShadow(sceneMatrices.cameraViewM!!, sceneMatrices.cameraProjM!!, errorMaterial, sceneMatrices.directionalLightVIewProjM)
                glBindTexture(GL_TEXTURE_2D, test.shadowFrameBuffer?.depthTexture!!)

                glDrawElements(GL_TRIANGLES, entity.meshIndicesCount, GL_UNSIGNED_INT, entity.meshIndexBuffer)
                entity.unBind()
            }
        }

        fbo?.unBind()

        // possible bug prone.
        test.mainFrameBufferPass = fbo
    }
}
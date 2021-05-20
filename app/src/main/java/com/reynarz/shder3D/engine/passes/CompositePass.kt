package com.reynarz.shder3D.engine.passes

import android.opengl.GLES20.*
import com.reynarz.shder3D.engine.Material
import com.reynarz.shder3D.engine.QueuedRenderableMesh
import com.reynarz.shder3D.views.MainActivity

class CompositePass : RenderPass() {

    private val selectedPass: SelectedEntityPass

    init {
        selectedPass = SelectedEntityPass()
        fbo?.genNormalFrameBuffer(MainActivity.width, MainActivity.height, GL_REPEAT)
    }


    override fun renderPass(entities: List<QueuedRenderableMesh>, sceneMatrices: SceneMatrices, errorMaterial: Material, test: RenderPassFrameBuffers) {
        fbo?.bind()
        glViewport(0, 0, fbo?.width!!, fbo?.height!!)

        glEnable(GL_DEPTH_TEST)

        for (entity in entities) {
            if (entity.active) {

                //setApplyMaterialConfig_GL(entity.materialConfig)

                entity.bindShadow(sceneMatrices.cameraViewM!!, sceneMatrices.cameraProjM!!, errorMaterial, sceneMatrices.directionalLightVIewProjM)
                glActiveTexture(GL_TEXTURE0)
                glBindTexture(GL_TEXTURE_2D, test.shadowFrameBuffer?.depthTexture!!)

                glActiveTexture(GL_TEXTURE1)
                glBindTexture(GL_TEXTURE_2D, test.screenPass?.colorTexture!!)

                glActiveTexture(GL_TEXTURE2)
                glBindTexture(GL_TEXTURE_2D, test.screenPass?.depthTexture!!)

                glDrawElements(GL_TRIANGLES, entity.meshIndicesCount, GL_UNSIGNED_INT, entity.meshIndexBuffer)
                entity.unBind()
            }
        }

        selectedPass.renderPass(entities, sceneMatrices, errorMaterial, test)
        fbo?.unBind()

        // possible bug prone.
        test.mainFrameBufferPass = fbo
    }
}
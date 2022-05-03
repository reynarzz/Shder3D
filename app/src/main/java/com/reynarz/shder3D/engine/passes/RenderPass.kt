package com.reynarz.shder3D.engine.passes

import android.opengl.GLES20.*
import com.reynarz.shder3D.engine.FrameBuffer
import com.reynarz.shder3D.engine.Material
import com.reynarz.shder3D.engine.QueuedRenderableMesh
import com.reynarz.shder3D.models.MaterialConfig

abstract class RenderPass {
    protected var fbo: FrameBuffer? = null

    val fbo_colorTexID = fbo?.colorTexture
    val fbo_depthTexID = fbo?.depthTexture

    init {
        fbo = FrameBuffer()
    }

    abstract fun renderPass(entities: List<QueuedRenderableMesh>, sceneMatrices: SceneMatrices, errorMaterial: Material, test: RenderPassFrameBuffers)

    protected fun setApplyMaterialConfig_GL(materialConfig: MaterialConfig?) {

        if (materialConfig != null) {
            // Blending
            if (materialConfig.gl_blendingEnabled) {
                glEnable(GL_BLEND)
                glBlendFunc(materialConfig.gl_srcFactor, materialConfig.gl_dstFactor)
            } else {
                glDisable(GL_BLEND)
            }

            // Depth test
            if (materialConfig.gl_depthTestEnabled) {
                glEnable(GL_DEPTH_TEST)
                glDepthFunc(materialConfig.gl_depthFunc)
            } else {
                glDisable(GL_DEPTH_TEST)
                glDepthFunc(GL_LEQUAL)
            }

            // Culling
            if (materialConfig.gl_cullEnabled) {
                glEnable(GL_CULL_FACE)
                glCullFace(materialConfig.gl_cullFace)
            } else {
                glDisable(GL_CULL_FACE)
            }
        }
    }

    fun clear(){
        fbo?.bind()
        glClearColor(0.2f, 0.2f, 0.2f, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        fbo?.unBind()
    }
}
package com.reynarz.minityeditor.engine.passes

import android.opengl.GLES20.*
import com.reynarz.minityeditor.MinityProjectRepository
import com.reynarz.minityeditor.engine.FrameBuffer
import com.reynarz.minityeditor.engine.Material
import com.reynarz.minityeditor.engine.QueuedRenderableMesh
import com.reynarz.minityeditor.models.MaterialConfig
import com.reynarz.minityeditor.views.MainActivity
import org.koin.java.KoinJavaComponent.get

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
}
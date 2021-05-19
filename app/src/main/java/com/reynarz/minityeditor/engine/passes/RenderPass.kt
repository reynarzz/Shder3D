package com.reynarz.minityeditor.engine.passes

import android.opengl.GLES20.*
import com.reynarz.minityeditor.MinityProjectRepository
import com.reynarz.minityeditor.engine.FrameBuffer
import com.reynarz.minityeditor.engine.Material
import com.reynarz.minityeditor.engine.QueuedRenderableMesh
import com.reynarz.minityeditor.models.MaterialConfig
import org.koin.java.KoinJavaComponent.get

open class RenderPass {
    protected val repository = get<MinityProjectRepository>(MinityProjectRepository::class.java)
    protected var fbo: FrameBuffer? = null

    val fbo_colorTexID = fbo?.colorTexture
    val fbo_depthTexID = fbo?.depthTexture

    init {
        //fbo = FrameBuffer()
    }

    open fun renderPass(entities: List<QueuedRenderableMesh>, sceneMatrices: SceneMatrices, errorMaterial: Material) {
        //fbo.bind()
        //glViewport(0, 0, fbo?.width!!, fbo?.height!!)

        glEnable(GL_DEPTH_TEST)

        glClearColor(0.2f, 0.2f, 0.2f, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        for (entity in entities) {
            if (entity.Active) {

//                    glActiveTexture(GL_TEXTURE0)
                //--glBindTexture(GL_TEXTURE_2D, shadowMapFrameBuffer.depthTexture)
//                    if (renderer?.materials?.getOrNull(phaseIndex) != null) {
//                        val depthUniform = glGetUniformLocation(renderer!!.materials[phaseIndex]!!.shader.program, "_SHADOWMAP")
//                        glUniform1i(depthUniform, 0)
//                    }

                setApplyMaterialConfig_GL(entity.materialConfig)

                entity.bindShadow(sceneMatrices.cameraViewM!!, sceneMatrices.cameraProjM!!, errorMaterial, sceneMatrices.directionalLightVIewProjM)

                glDrawElements(GL_TRIANGLES, entity.meshIndicesCount, GL_UNSIGNED_INT, entity.meshIndexBuffer)
                entity.unBind()
            }
        }
        //fbo.unBind()
    }

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
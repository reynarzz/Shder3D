package com.reynarz.minityeditor.engine.passes

import android.opengl.GLES20.*
import com.reynarz.minityeditor.MinityProjectRepository
import com.reynarz.minityeditor.engine.FrameBuffer
import com.reynarz.minityeditor.engine.Material
import com.reynarz.minityeditor.engine.components.MeshRenderer
import com.reynarz.minityeditor.engine.components.SceneEntity
import com.reynarz.minityeditor.models.MaterialConfig
import com.reynarz.minityeditor.views.MainActivity
import org.koin.java.KoinJavaComponent.get

open class RenderPass {
    protected val repository = get<MinityProjectRepository>(MinityProjectRepository::class.java)
    protected lateinit var fbo: FrameBuffer

    val fbo_colorTexID = fbo.colorTexture
    val fbo_depthTexID = fbo.depthTexture

    init {
        fbo = FrameBuffer()
    }

    open fun renderPass(entities: List<SceneEntity?>, sceneMatrices: SceneMatrices, errorMaterial: Material) {
        fbo.bind()
        glViewport(0, 0, fbo.width, fbo.height)

        glEnable(GL_DEPTH_TEST)

        glClearColor(0.2f, 0.2f, 0.2f, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        for (i in 0 until entities.size) {

            val entity = entities.getOrNull(i)

            if (entity != null && entity.entityData.active) {
                val renderer = entity.getComponent(MeshRenderer::class.java)
                val materialsData = entity.entityData.meshRendererData.materialsData

                for (meshIndex in 0 until renderer!!.meshes.size) {

                    glActiveTexture(GL_TEXTURE0)
                    //--glBindTexture(GL_TEXTURE_2D, shadowMapFrameBuffer.depthTexture)

                    if (renderer?.materials?.getOrNull(meshIndex) != null) {
                        val depthUniform = glGetUniformLocation(renderer!!.materials[meshIndex]!!.shader.program, "_SHADOWMAP")
                        glUniform1i(depthUniform, 0)
                    }

                    setApplyMaterialConfig_GL(materialsData.getOrNull(meshIndex)?.materialConfig)

                    renderer.bindShadow(sceneMatrices.cameraViewM!!, sceneMatrices.cameraProjM!!, errorMaterial, sceneMatrices.directionalLightVIewProjM, meshIndex)

                    val mesh = renderer.meshes[meshIndex]

                    glDrawElements(GL_TRIANGLES, mesh.indicesCount, GL_UNSIGNED_INT, mesh.indexBuffer)
                    renderer.unBind()
                }
            }
        }
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
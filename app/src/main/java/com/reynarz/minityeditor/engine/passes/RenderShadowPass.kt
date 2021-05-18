package com.reynarz.minityeditor.engine.passes

import android.opengl.GLES20.*
import com.reynarz.minityeditor.engine.FrameBuffer
import com.reynarz.minityeditor.engine.Material
import com.reynarz.minityeditor.engine.components.MeshRenderer
import com.reynarz.minityeditor.engine.components.SceneEntity
import com.reynarz.minityeditor.views.MainActivity

class RenderShadowPass : RenderPass() {

    init {
        fbo.genBufferForDepth(MainActivity.width, MainActivity.height)
    }

    override fun renderPass(entities: List<SceneEntity?>, sceneMatrices: SceneMatrices, errorMaterial: Material) {
        glViewport(0, 0, fbo.width, fbo.height)

        fbo.bind()

        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)
        glClear(GL_STENCIL_BUFFER_BIT)

        glClearColor(0.0f, 0.0f, 0.0f, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        for (i in 0 until entities?.size!!) {

            // sometimes the user can destroy an entity in another thread!, this is a patch! but it can cause problems.
            val entity = entities.getOrNull(i)

            if (entity != null && entity.entityData.active && entity.entityData.meshRendererData.castShadows) {
                val renderer = entity.getComponent(MeshRenderer::class.java)

                for (meshIndex in 0 until renderer!!.meshes.size) {

                    renderer?.bind(sceneMatrices.directionalLightVIewM, sceneMatrices.directionalLightProjM, errorMaterial, meshIndex)

                    val mesh = renderer.meshes[meshIndex]

                    glDrawElements(GL_TRIANGLES, mesh.indicesCount, GL_UNSIGNED_INT, mesh.indexBuffer)

                    renderer.unBind()
                }
            }
        }

        fbo.unBind()
        glViewport(0, 0, fbo.width, fbo.height)
    }
}
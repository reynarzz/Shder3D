package com.reynarz.minityeditor.engine.passes

import android.opengl.GLES20.*
import com.reynarz.minityeditor.engine.Material
import com.reynarz.minityeditor.engine.QueuedRenderableMesh
import com.reynarz.minityeditor.engine.components.MeshRenderer
import com.reynarz.minityeditor.engine.components.SceneEntity
import com.reynarz.minityeditor.views.MainActivity

class ShadowPass : RenderPass() {

    init {
        fbo?.genBufferForDepth(MainActivity.width, MainActivity.height)
    }

    override fun renderPass(entities: List<QueuedRenderableMesh>, sceneMatrices: SceneMatrices, errorMaterial: Material) {
        glViewport(0, 0, fbo?.width!!, fbo?.height!!)

        fbo?.bind()

        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)
        glClear(GL_STENCIL_BUFFER_BIT)

        glClearColor(0.0f, 0.0f, 0.0f, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        for (entity in entities) {
            if (entity.Active && entity.canCastShadows) {

                entity.bind(sceneMatrices.directionalLightVIewM, sceneMatrices.directionalLightProjM, errorMaterial)

                glDrawElements(GL_TRIANGLES, entity.meshIndicesCount, GL_UNSIGNED_INT, entity.meshIndexBuffer)

                entity.unBind()
            }
        }

        fbo?.unBind()
    }
}
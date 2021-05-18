package com.reynarz.minityeditor.engine.passes

import android.opengl.GLES20.*
import com.reynarz.minityeditor.engine.FrameBuffer
import com.reynarz.minityeditor.engine.Material
import com.reynarz.minityeditor.engine.OpenGLRenderer
import com.reynarz.minityeditor.engine.components.MeshRenderer
import com.reynarz.minityeditor.engine.components.SceneEntity
import com.reynarz.minityeditor.engine.vec3
import com.reynarz.minityeditor.views.MainActivity

class NormalPass : RenderPass() {



    override fun renderPass(entities: List<SceneEntity?>, sceneMatrices: SceneMatrices, errorMaterial: Material) {


            //Selected entity outline pass.
//            if (selectedEntity != null && entity === selectedEntity && selectedEntity?.isActive!!) {
//
//                val renderer = selectedEntity!!.getComponent(MeshRenderer::class.java)
//                glLineWidth(1.3f)
//
//                glEnable(GL_DEPTH_TEST)
//
//                for (meshIndex in 0 until renderer!!.meshes.size) {
//                    val mesh = renderer.meshes[meshIndex]
//                    renderer!!.bindWithMaterial(viewM, projM, outlineMaterial, meshIndex)
//                    glDrawElements(GL_LINES, mesh.indicesCount, GL_UNSIGNED_INT, mesh.indexBuffer)
//                }
//            }
        }

        mainFrameBuffer.unBind()

    }
}
package com.reynarz.shder3D.engine

import android.opengl.GLES20.*
import android.opengl.Matrix
import com.reynarz.shder3D.engine.components.MeshRenderer
import com.reynarz.shder3D.views.MainActivity

class ScreenQuad(private val cameraMeshRenderer: MeshRenderer) {

    val identityM = FloatArray(16).also {
        Matrix.setIdentityM(it, 0)
    }

    fun draw(mainFrameBuffer: FrameBuffer, errorMaterial: Material) {
        //--editorRenderingBack()

        // Screen Quad
        glDisable(GL_DEPTH_TEST)
        glClear(GL_COLOR_BUFFER_BIT or GL_COLOR_BUFFER_BIT)

        cameraMeshRenderer?.bind(identityM, identityM, errorMaterial, 0)

        // this should be binded differently
        glActiveTexture(GL_TEXTURE7)
        glBindTexture(GL_TEXTURE_2D, mainFrameBuffer.colorTexture)
        //glBindTexture(GL_TEXTURE_2D, colorPickerFrameBuffer.colorTexture)

        glActiveTexture(GL_TEXTURE8)
        glBindTexture(GL_TEXTURE_2D, mainFrameBuffer.depthTexture)


        val grabPass = glGetUniformLocation(cameraMeshRenderer!!.materials[0]!!.program, "_MainTex")
        glUniform1i(grabPass, 7)

        val depthUniform = glGetUniformLocation(cameraMeshRenderer!!.materials[0]!!.program, "_CameraDepthTexture")
        glUniform1i(depthUniform, 8)


        glViewport(0, 0, MainActivity.width, MainActivity.height)

        glDrawElements(GL_TRIANGLES, cameraMeshRenderer?.meshes!![0].indicesCount, GL_UNSIGNED_INT, cameraMeshRenderer?.meshes!![0].indexBuffer)
        cameraMeshRenderer.unBind()
    }
}
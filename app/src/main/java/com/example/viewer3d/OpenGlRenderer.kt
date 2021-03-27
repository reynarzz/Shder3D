package com.example.viewer3d

import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import java.nio.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

const val COORDS_PER_VERTEX = 2

class OpenGlRenderer : GLSurfaceView.Renderer {

    private val vertexShaderCode =
        "attribute vec4 vPosition;" +
                "void main() {" +
                "  gl_Position = vPosition;" +
                "}"

    private val fragmentShaderCode =

    var triangleCoords = floatArrayOf(// in counterclockwise order:
        -1.0f, 0.0f,      // top
        0.0f, 1.0f,    // bottom left
        1.0f, 0.0f     // bottom right
    )


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

        var mesh = Mesh(triangleCoords, null)
        var shader = Shader(vertexShaderCode, fragmentShaderCode)
        var program = shader.Bind()
        mesh.Bind_Test(program)
    }


    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_DEPTH_BUFFER_BIT or GL_COLOR_BUFFER_BIT)

        glDrawArrays(GL_TRIANGLES, 0, vertexCount)

        //glDisableVertexAttribArray(it)
    }
}
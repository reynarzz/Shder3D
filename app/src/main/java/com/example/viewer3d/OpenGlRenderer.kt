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
                "uniform mat4 _MVP_;"+
                "void main() {" +
                "gl_Position = _MVP_ * vPosition;" +
                "}"

    private val fragmentShaderCode = "precision mediump float;"+
    "uniform vec4 vColor;"+
    "void main()"+
    "{"+
        "gl_FragColor = vec4(1.0);"+
    "}"

    var triangleCoords = floatArrayOf(
        -10.0f, 0.0f,
        0.0f, 10.0f,
        10.0f, 0.0f
    )

    private var width = 0;
    private var height = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        this.width = width
        this.height = height

        var camera = Camera()

        camera.updateProjection(width, height)
        camera.updateView()

        mesh = Mesh(triangleCoords, intArrayOf(0, 1, 2))
        var shader = Shader(vertexShaderCode, fragmentShaderCode)

        var program = shader.Bind(camera)
        mesh.Bind_Test(program)
    }

    lateinit var mesh : Mesh

    override fun onDrawFrame(gl: GL10?)
    {
        glClear(GL_DEPTH_BUFFER_BIT or GL_COLOR_BUFFER_BIT)

        glDrawElements(GL_TRIANGLES, mesh.indices.size, GL_UNSIGNED_INT, mesh.indexBuffer)
    }
}
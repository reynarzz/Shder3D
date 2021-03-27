package com.example.viewer3d

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import java.nio.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class OpenGlRenderer(val context: Context) : GLSurfaceView.Renderer {

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
        "gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);"+
    "}"

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

        var camera = Camera()
        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)

        camera.updateProjection(width, height)
        camera.updateView()
        ObjParser(context, "models/cube.obj").also {

            var data = it.getModelData()

                mesh = Mesh(data.mVertices, data.mIndices)
                var shader = Shader(vertexShaderCode, fragmentShaderCode)

                var program = shader.Bind(camera)
                mesh.Bind_Test(program)
        }

    }

    lateinit var mesh : Mesh

    override fun onDrawFrame(gl: GL10?)
    {
        glClear(GL_DEPTH_BUFFER_BIT or GL_COLOR_BUFFER_BIT)

        glDrawElements(GL_TRIANGLES, mesh.indices.size, GL_UNSIGNED_INT, mesh.indexBuffer)
    }
}
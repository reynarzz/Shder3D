package com.example.viewer3d.engine

import android.opengl.GLES20.*
import android.opengl.Matrix

class Shader(vertexSource: String, fragmentSource: String) {

    private var program = 0

    init {
          val vertexShader: Int = GetCompiledShader(GL_VERTEX_SHADER, vertexSource)
          val fragmentShader: Int = GetCompiledShader(GL_FRAGMENT_SHADER, fragmentSource)

          program = glCreateProgram();

          glAttachShader(program, vertexShader)
          glAttachShader(program, fragmentShader)

          glLinkProgram(program)
    }

    private fun GetCompiledShader(type: Int, shaderCode: String): Int {

        var shaderID = glCreateShader(type);

        glShaderSource(shaderID, shaderCode)
        glCompileShader(shaderID)

        return shaderID
    }

    public fun Bind(cameraTest: Camera) : Int {
        glUseProgram(program)

        var mvpLocation = glGetUniformLocation(program, "_VP_")

        var identity = FloatArray(16)

        Matrix.setIdentityM(identity, 0)

        var result = FloatArray(16)

        Matrix.multiplyMM(result,0, cameraTest.projectionM, 0, cameraTest.viewM, 0)
        glUniformMatrix4fv(mvpLocation, 1, false, result , 0)

        modelM = FloatArray(16)
        Matrix.setIdentityM(modelM, 0)

        return program
    }

    lateinit var modelM :FloatArray

    fun TestRotation(){
        val modelID = glGetUniformLocation(program, "_M_")

        Matrix.rotateM(modelM, 0, 0.3f, 0.0f, 1.0f, 0.0f)

        glUniformMatrix4fv(modelID, 1, false, modelM, 0)
    }

    fun GetProgram() : Int {
        return program;
    }
}
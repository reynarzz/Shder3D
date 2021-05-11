package com.reynarz.minityeditor.engine

import android.opengl.GLES20.*
import android.util.Log

class Shader(vertexSource: String, fragmentSource: String) {

    var program = -1
        private set

    private var vertexShader = 0
    private var fragmentShader = 0

    var compiledCorrectly = false
        private set

    init {
        createShaders(vertexSource, fragmentSource)
    }

    fun bind(): Int {
        glUseProgram(program)
        return program
    }

    fun unBind() {
        glUseProgram(0)
    }

    fun replaceShaders(vertex: String, fragment: String) {

        unBind()

        if (glIsProgram(program)) {
            glDetachShader(program, fragmentShader)
            glDetachShader(program, vertexShader)
        }

        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)

        createShaders(vertex, fragment)
    }

    private fun createShaders(vertex: String, fragment: String) {
        var vertexResult = GetCompiledShader(GL_VERTEX_SHADER, vertex);
        var fragmentResult = GetCompiledShader(GL_FRAGMENT_SHADER, fragment);

        vertexShader = vertexResult.first
        fragmentShader = fragmentResult.first

        val couldVertexShaderCompile = vertexResult.second
        val couldFragmentShaderCompile = fragmentResult.second

        compiledCorrectly = couldVertexShaderCompile && couldFragmentShaderCompile

        if (compiledCorrectly) {

            if (!glIsProgram(program))
                program = glCreateProgram()

            glAttachShader(program, vertexShader)
            glAttachShader(program, fragmentShader)

            glLinkProgram(program)
        } else {

            if (!couldVertexShaderCompile) {
                val log = glGetShaderInfoLog(vertexShader)
                Log.d("Vertex Shader", log)
            }

            if (!couldFragmentShaderCompile) {
                val log = glGetShaderInfoLog(fragmentShader)
                Log.d("Fragment Shader", log)
            }


        }
    }

    private fun GetCompiledShader(type: Int, shaderCode: String): Pair<Int, Boolean> {

        var shaderID = glCreateShader(type);

        glShaderSource(shaderID, shaderCode)
        glCompileShader(shaderID)

        val result = IntArray(1);

        glGetShaderiv(shaderID, GL_COMPILE_STATUS, result, 0)

        return Pair(shaderID, result[0] == 1)
    }

    //var modelM: FloatArray? = null

//    fun TestRotation() {
//        val modelID = glGetUniformLocation(program, "unity_ObjectToWorld")
//
//        Matrix.rotateM(modelM, 0, 0.3f, 0.0f, 1.0f, 0.0f)
//
//        glUniformMatrix4fv(modelID, 1, false, modelM, 0)
//    }
}
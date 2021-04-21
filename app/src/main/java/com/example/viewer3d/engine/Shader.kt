package com.example.viewer3d.engine

import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.Matrix
import com.example.viewer3d.MainActivity
import glm_.vec4.operators.vec4_operators

class Shader(vertexSource: String, fragmentSource: String) {

    var program = 0
        private set

    private var vertexShader = 0
    private var fragmentShader = 0

    init {
        createShaders(vertexSource, fragmentSource)
    }

    fun bind(model: FloatArray, view: FloatArray, projection: FloatArray): Int {
        glUseProgram(program)
        return program
    }

    fun bind() : Int {
        glUseProgram(program)

        return program
    }

    fun unBind() {
        glUseProgram(0)
    }

    fun replaceShaders(vertex: String, fragment: String) {

        if (glIsProgram(program)) {
            glDetachShader(program, fragmentShader)
            glDeleteShader(fragmentShader)

            glDetachShader(program, vertexShader)
            glDeleteShader(vertexShader)

            createShaders(vertex, fragment)
        }

    }

    private fun createShaders(vertex: String, fragment: String) {
        var vertexResult = GetCompiledShader(GL_VERTEX_SHADER, vertex);
        var fragmentResult = GetCompiledShader(GL_FRAGMENT_SHADER, fragment);

        vertexShader = vertexResult.first
        fragmentShader = fragmentResult.first

        val couldVertexShaderCompile = vertexResult.second
        val couldFragmentShaderCompile = fragmentResult.second

        val anErrorhappened = !couldVertexShaderCompile || !couldFragmentShaderCompile

        if (anErrorhappened) {

            if (!couldVertexShaderCompile) {
                val log = glGetShaderInfoLog(vertexShader)
            }

            if (!couldFragmentShaderCompile) {
                val log = glGetShaderInfoLog(fragmentShader)
            }

            val errorShader = Utils.getErrorShaderCode()

            vertexResult = GetCompiledShader(GL_VERTEX_SHADER, errorShader.first);
            fragmentResult = GetCompiledShader(GL_FRAGMENT_SHADER, errorShader.second);

            vertexShader = vertexResult.first
            fragmentShader = fragmentResult.first
        } else {

        }

        program = glCreateProgram();

        glAttachShader(program, vertexShader)
        glAttachShader(program, fragmentShader)

        glLinkProgram(program)
    }

    fun setDeltaTimeTest(time: Float, delta: Float) {
        var _Time = glGetUniformLocation(program, "_Time")
        var unity_DeltaTime = glGetUniformLocation(program, "unity_DeltaTime")

        glUniform4f(_Time, time / 20f, time, time * 2, time * 3)
        glUniform4f(unity_DeltaTime, delta, 1f / delta, 0f, 0f) // implement smooth deltatime.
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
package com.example.viewer3d.engine

import android.opengl.GLES20.*
import android.opengl.Matrix

class Shader(val vertexSource: String, val fragmentSource: String) {

    var program = 0
        private set

    private var vertexShader = 0
    private var fragmentShader = 0

    init {
        vertexShader = GetCompiledShader(GL_VERTEX_SHADER, vertexSource)
        fragmentShader = GetCompiledShader(GL_FRAGMENT_SHADER, fragmentSource)

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

    fun Bind(cameraTest: Camera): Int {
        glUseProgram(program)

        var UNITY_MATRIX_MVP = glGetUniformLocation(program, "UNITY_MATRIX_MVP")
        var UNITY_MATRIX_MV = glGetUniformLocation(program, "UNITY_MATRIX_MV")
        var UNITY_MATRIX_V = glGetUniformLocation(program, "UNITY_MATRIX_V")
        var UNITY_MATRIX_P = glGetUniformLocation(program, "UNITY_MATRIX_P")
        var UNITY_MATRIX_T_MV = glGetUniformLocation(program, "UNITY_MATRIX_T_MV")
        var UNITY_MATRIX_IT_MV = glGetUniformLocation(program, "UNITY_MATRIX_IT_MV")
        var unity_ObjectToWorld = glGetUniformLocation(program, "unity_ObjectToWorld")
        var unity_WorldToObject = glGetUniformLocation(program, "unity_WorldToObject")

        var _ScreenParams = glGetUniformLocation(program, "_ScreenParams")
        var _WorldSpaceCameraPos = glGetUniformLocation(program, "_WorldSpaceCameraPos")
        var _ProjectionParams = glGetUniformLocation(program, "_ProjectionParams")
        var _ZBufferParams = glGetUniformLocation(program, "_ZBufferParams")

        var _WorldSpaceLightPos0 = glGetUniformLocation(program, "_WorldSpaceLightPos0")
        var _LightColor0 = glGetUniformLocation(program, "_LightColor0")

        var _Time = glGetUniformLocation(program, "_Time")
        var unity_DeltaTime = glGetUniformLocation(program, "unity_DeltaTime")


        val MVP = FloatArray(16)
        val MV = FloatArray(16)

        val modelM = FloatArray(16)
        val inverseModelM = FloatArray(16)

        Matrix.setIdentityM(modelM, 0)

        Matrix.multiplyMM(MVP, 0, cameraTest.projectionM, 0, cameraTest.viewM, 0)
        Matrix.multiplyMM(MVP, 0, MVP, 0, modelM, 0)

        Matrix.multiplyMM(MV, 0,cameraTest.viewM, 0, modelM , 0)


        Matrix.invertM(inverseModelM, 0, modelM, 0)

        glUniformMatrix4fv(UNITY_MATRIX_MVP, 1, false, MVP, 0)
        glUniformMatrix4fv(UNITY_MATRIX_MV, 1, false, MV, 0)
        glUniformMatrix4fv(UNITY_MATRIX_V, 1, false, cameraTest.viewM, 0)
        glUniformMatrix4fv(UNITY_MATRIX_P, 1, false, cameraTest.projectionM, 0)


        glUniformMatrix4fv(unity_ObjectToWorld, 1, false, modelM, 0)
        glUniformMatrix4fv(unity_WorldToObject, 1, false, inverseModelM, 0)


        return program
    }


    fun replaceShaders(vertex: String, fragment: String) {
        glDetachShader(program, fragmentShader)
        glDeleteShader(fragmentShader)

        glDetachShader(program, vertexShader)
        glDeleteShader(vertexShader)

        fragmentShader = GetCompiledShader(GL_FRAGMENT_SHADER, fragment)
        vertexShader = GetCompiledShader(GL_VERTEX_SHADER, vertex)

        glAttachShader(program, fragmentShader)
        glAttachShader(program, vertexShader)

        glLinkProgram(program)
    }

    fun GetProgram(): Int {
        return program
    }
}
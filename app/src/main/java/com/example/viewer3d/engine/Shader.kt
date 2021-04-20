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

     fun Bind(cameraTest: Camera) : Int {
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

        var _VP_ = glGetUniformLocation(program, "_VP_")
        var _M_ = glGetUniformLocation(program, "_M_")


        var MVP = FloatArray(16)

        Matrix.multiplyMM(MVP,0, cameraTest.projectionM, 0, cameraTest.viewM, 0)

        modelM = FloatArray(16)
        Matrix.setIdentityM(modelM, 0)


         glUniformMatrix4fv(_M_, 1, false, modelM, 0)
         glUniformMatrix4fv(_VP_, 1, false, MVP , 0)

        return program
    }

    lateinit var modelM :FloatArray

    fun replaceShaders(vertex : String, fragment : String) {
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

    fun TestRotation(){
        val modelID = glGetUniformLocation(program, "_M_")

        Matrix.rotateM(modelM, 0, 0.3f, 0.0f, 1.0f, 0.0f)

        glUniformMatrix4fv(modelID, 1, false, modelM, 0)
    }

    fun GetProgram() : Int {
        return program
    }
}
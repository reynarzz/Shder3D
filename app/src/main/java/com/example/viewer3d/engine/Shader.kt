package com.example.viewer3d.engine

import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.Matrix

class Shader(vertexSource: String, fragmentSource: String) {

    var program = 0
        private set

    private var vertexShader = 0
    private var fragmentShader = 0

    init {

        createShaders(vertexSource, fragmentSource)

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

        modelM = FloatArray(16)
        val inverseModelM = FloatArray(16)

        Matrix.setIdentityM(modelM, 0)

        Matrix.multiplyMM(MVP, 0, cameraTest.projectionM, 0, cameraTest.viewM, 0)
        Matrix.multiplyMM(MVP, 0, MVP, 0, modelM, 0)

        Matrix.multiplyMM(MV, 0, cameraTest.viewM, 0, modelM, 0)


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

        createShaders(vertex, fragment)
    }

    private fun createShaders(vertex: String, fragment: String){
        var vertexResult = GetCompiledShader(GL_VERTEX_SHADER, vertex);
        var fragmentResult = GetCompiledShader(GL_FRAGMENT_SHADER, fragment);

        vertexShader = vertexResult.first
        fragmentShader = fragmentResult.first

        val couldVertexShaderCompile = vertexResult.second
        val couldFragmentShaderCompile = fragmentResult.second

        val anErrorhappened = !couldVertexShaderCompile || !couldFragmentShaderCompile

        if (anErrorhappened) {

            if(!couldVertexShaderCompile) {
                val log = glGetShaderInfoLog(vertexShader)
            }

            if(!couldFragmentShaderCompile)
            {
                val log = glGetShaderInfoLog(fragmentShader)
            }

            val errorShader = Utils.getErrorShaderCode()

            vertexResult = GetCompiledShader(GL_VERTEX_SHADER, errorShader.first);
            fragmentResult = GetCompiledShader(GL_FRAGMENT_SHADER, errorShader.second);

            vertexShader = vertexResult.first
            fragmentShader = fragmentResult.first
        }
        else
        {

        }

        program = glCreateProgram();

        glAttachShader(program, vertexShader)
        glAttachShader(program, fragmentShader)

        glLinkProgram(program)
    }

    private fun GetCompiledShader(type: Int, shaderCode: String): Pair<Int, Boolean> {

        var shaderID = glCreateShader(type);

        glShaderSource(shaderID, shaderCode)
        glCompileShader(shaderID)
        val result = IntArray(1);

        glGetShaderiv(shaderID, GL_COMPILE_STATUS, result, 0 )



        return Pair(shaderID, result[0] == 1)
    }

    lateinit var modelM: FloatArray

    fun TestRotation() {
        val modelID = glGetUniformLocation(program, "unity_ObjectToWorld")

        Matrix.rotateM(modelM, 0, 0.3f, 0.0f, 1.0f, 0.0f)

        glUniformMatrix4fv(modelID, 1, false, modelM, 0)
    }

    fun GetProgram(): Int {
        return program
    }
}
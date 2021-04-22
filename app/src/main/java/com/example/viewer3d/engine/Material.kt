package com.example.viewer3d.engine

import android.opengl.GLES20.*
import android.opengl.Matrix
import com.example.viewer3d.MainActivity

class Material(val shader: Shader) {

    val program = shader.program

//    var MainColor: Vec4 = Vec4()
//        set(color) {
//            val program = shader.program
//
//            if (program != 0) {
//                val u_color = glGetUniformLocation(program, "_Color")
//                glUniform4f(u_color, color.r, color.g, color.b, color.a)
//            }
//        }

    private var textures: MutableList<Texture>? = null

    init {
        textures = mutableListOf()
    }

    fun bind(model: FloatArray, view: FloatArray, projection: FloatArray) {
        shader.bind()
        setUniforms(model, view, projection)

        bindTextures()
    }


    // for shadow mapping,
    fun bind(model: FloatArray, view: FloatArray, projection: FloatArray, shader: Shader) {
        shader.bind()
        setUniforms(model, view, projection)

        bindTextures()
    }


    private fun bindTextures() {
        for (i in textures!!.indices) {
            textures!![i].bind(i)
        }
    }

    private fun setUniforms(model: FloatArray, view: FloatArray, projection: FloatArray) {

        //        var UNITY_MATRIX_MVP = glGetUniformLocation(program, "UNITY_MATRIX_MVP")
//        var UNITY_MATRIX_MV = glGetUniformLocation(program, "UNITY_MATRIX_MV")
//        var UNITY_MATRIX_V = glGetUniformLocation(program, "UNITY_MATRIX_V")
//        var UNITY_MATRIX_P = glGetUniformLocation(program, "UNITY_MATRIX_P")
//        var UNITY_MATRIX_T_MV = glGetUniformLocation(program, "UNITY_MATRIX_T_MV")
//        var UNITY_MATRIX_IT_MV = glGetUniformLocation(program, "UNITY_MATRIX_IT_MV")
//        var unity_ObjectToWorld = glGetUniformLocation(program, "unity_ObjectToWorld")
//        var unity_WorldToObject = glGetUniformLocation(program, "unity_WorldToObject")
//
//        var _ScreenParams = glGetUniformLocation(program, "_ScreenParams")
//        var _WorldSpaceCameraPos = glGetUniformLocation(program, "_WorldSpaceCameraPos")
//        var _ProjectionParams = glGetUniformLocation(program, "_ProjectionParams")
//        var _ZBufferParams = glGetUniformLocation(program, "_ZBufferParams")
//
//        var _WorldSpaceLightPos0 = glGetUniformLocation(program, "_WorldSpaceLightPos0")
//        var _LightColor0 = glGetUniformLocation(program, "_LightColor0")
//
//        var _Time = glGetUniformLocation(program, "_Time")
//        var unity_DeltaTime = glGetUniformLocation(program, "unity_DeltaTime")



        Matrix.multiplyMM(MVP, 0, projection, 0, view, 0)
        Matrix.multiplyMM(MVP, 0, MVP, 0, model, 0)

        Matrix.multiplyMM(MV, 0, view, 0, model, 0)

        Matrix.invertM(InvModel, 0, model, 0)

        set("UNITY_MATRIX_MVP", MVP);
        set("UNITY_MATRIX_MV", MV);
        set("UNITY_MATRIX_V", view);
        set("UNITY_MATRIX_P", projection);
        set("unity_WorldToObject", InvModel);
        set("unity_ObjectToWorld", model);
        set("_ScreenParams", Vec4(MainActivity.width.toFloat(), MainActivity.height.toFloat(), 1f + 1f / MainActivity.width.toFloat(), 1f + 1f / MainActivity.height.toFloat()))
    }

    val MVP = FloatArray(16)
    val MV = FloatArray(16)

    val InvModel = FloatArray(16)

    fun addTexture(texture: Texture) {
        textures!!.add(texture)
    }

    fun removeTexture(texture: Texture) {
        textures!!.remove(texture)
    }

    fun set(uniformName: String, matrix: FloatArray) {
        val uniformLocation = glGetUniformLocation(shader.program, uniformName)
        glUniformMatrix4fv(uniformLocation, 1, false, matrix, 0)
    }

    fun set(uniformName: String, vec4: Vec4) {
        val uniformLocation = glGetUniformLocation(shader.program, uniformName)
        glUniform4f(uniformLocation, vec4.x, vec4.y, vec4.z, vec4.w)
    }

    fun set(uniformName: String, vec3: Vec3) {
        val uniformLocation = glGetUniformLocation(shader.program, uniformName)
        glUniform3f(uniformLocation, vec3.x, vec3.y, vec3.z)
    }

    fun set(uniformName: String, float: Float) {
        val uniformLocation = glGetUniformLocation(shader.program, uniformName)
        glUniform1f(uniformLocation, float)
    }
}
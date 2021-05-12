package com.reynarz.minityeditor.engine

import android.opengl.GLES20.*
import android.opengl.Matrix
import com.reynarz.minityeditor.views.MainActivity
import kotlinx.serialization.Serializable



class Material(val shader: Shader) {
    //val materialData = MaterialData()

    var id = ""

    val program = shader.program

    val MVP = FloatArray(16)
    val MV = FloatArray(16)

    val InvModel = FloatArray(16)

//    var MainColor: Vec4 = Vec4()
//        set(color) {
//            val program = shader.program
//
//            if (program != 0) {
//                val u_color = glGetUniformLocation(program, "_Color")
//                glUniform4f(u_color, color.r, color.g, color.b, color.a)
//            }
//        }

    var textures: MutableList<Texture>? = null


    init {
        textures = mutableListOf()
    }

    fun bind(model: FloatArray?, view: FloatArray?, projection: FloatArray?) {
        shader.bind()
        setUniforms(model, view, projection)

        bindTextures()
    }


    // for shadow mapping,
    fun bind(model: FloatArray?, view: FloatArray?, projection: FloatArray?, lightViewM: FloatArray?) {
        shader.bind()
        setUniforms(model, view, projection)
        set("_LIGHT", lightViewM)

        bindTextures()
    }

    private val minValidSlot = 2
    private fun bindTextures() {

        for (slot in textures!!.indices) {

            val offsetSlot = slot+minValidSlot
            textures!![slot].bind(offsetSlot)

            set("_tex${slot}", offsetSlot)
        }
    }

    private fun unbindTextures(){
        for (slot in textures!!.indices) {

            glActiveTexture(GL_TEXTURE0 +slot)
            glBindTexture(GL_TEXTURE_2D, 0)
        }
    }

    private fun setUniforms(model: FloatArray?, view: FloatArray?, projection: FloatArray?) {

//        var UNITY_MATRIX_T_MV = glGetUniformLocation(program, "UNITY_MATRIX_T_MV")
//        var UNITY_MATRIX_IT_MV = glGetUniformLocation(program, "UNITY_MATRIX_IT_MV")
//


        Matrix.multiplyMM(MVP, 0, projection, 0, view, 0)
        Matrix.multiplyMM(MVP, 0, MVP, 0, model, 0)

        Matrix.multiplyMM(MV, 0, view, 0, model, 0)

        Matrix.invertM(InvModel, 0, model, 0)

        val nearPlane = 1f
        val farPlane = 500f
        val t = OpenGLRenderer.fakeTimeScale // need the current time of the app.
        val deltaTime = OpenGLRenderer.fakeDeltaTime // need current delta time of the app.

        set("UNITY_MATRIX_MVP", MVP)
        set("UNITY_MATRIX_MV", MV)
        set("UNITY_MATRIX_M", model)
        set("UNITY_MATRIX_V", view)
        set("UNITY_MATRIX_P", projection)
        set("unity_CameraProjection", projection)
        set("unity_WorldToObject", InvModel)
        set("unity_ObjectToWorld", model)
        set("_ScreenParams", vec4(MainActivity.width.toFloat(), MainActivity.height.toFloat(), 1f + 1f / MainActivity.width.toFloat(), 1f + 1f / MainActivity.height.toFloat()))



        set("_ZBufferParams", vec4(1f - farPlane / nearPlane, farPlane / nearPlane, (1f - farPlane / nearPlane) / farPlane, (farPlane / nearPlane) / farPlane))
        set("_ProjectionParams", vec4(1f, nearPlane, farPlane, 1f / farPlane))
        set("_LightColor0", vec4())
        set("_WorldSpaceLightPos0", vec4())
        set("_Time", vec4(t / 20f, t, t * 2, t * 3))
        set("unity_DeltaTime", vec4(deltaTime, 1f / deltaTime, 0f/*smoothDt*/, 0f/*1/smoothDt*/))

        if (view != null)
            set("_WorldSpaceCameraPos", vec3(view[3], view[7], view[11]))
    }


    fun set(uniformName: String, matrix: FloatArray?) {
        val uniformLocation = glGetUniformLocation(shader.program, uniformName)
        glUniformMatrix4fv(uniformLocation, 1, false, matrix, 0)
    }

    fun set(uniformName: String, vec4: vec4) {
        val uniformLocation = glGetUniformLocation(shader.program, uniformName)
        glUniform4f(uniformLocation, vec4.x, vec4.y, vec4.z, vec4.w)
    }

    fun set(uniformName: String, vec3: vec3) {
        val uniformLocation = glGetUniformLocation(shader.program, uniformName)
        glUniform3f(uniformLocation, vec3.x, vec3.y, vec3.z)
    }

    fun set(uniformName: String, float: Float) {
        val uniformLocation = glGetUniformLocation(shader.program, uniformName)
        glUniform1f(uniformLocation, float)
    }

    fun set(uniformName: String, int: Int) {
        val uniformLocation = glGetUniformLocation(shader.program, uniformName)
        glUniform1i(uniformLocation, int)
    }

    fun unBind() {
        unbindTextures()
        glUseProgram(0)
    }
}
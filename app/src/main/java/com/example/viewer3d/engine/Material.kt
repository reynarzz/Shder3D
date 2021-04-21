package com.example.viewer3d.engine

import android.opengl.GLES20.*
import glm_.vec3.Vec3
import glm_.vec4.*

class Material(val shader: Shader) {

    var MainColor: Vec4 = Vec4()
        set(color) {
            val program = shader.GetProgram()

            if (program != 0) {
                val u_color = glGetUniformLocation(program, "_Color")
                glUniform4f(u_color, color.r, color.g, color.b, color.a)
            }
        }

    private var textures: MutableList<Texture>? = null

    init {
        textures = mutableListOf()
    }

    fun Bind(camera: Camera) {
        shader.Bind(camera)
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
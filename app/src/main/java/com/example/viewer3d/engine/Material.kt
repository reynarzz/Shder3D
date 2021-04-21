package com.example.viewer3d.engine
import android.opengl.GLES20.*
import glm_.vec4.*

class Material(val shader: Shader) {

    var MainColor : Vec4 = Vec4()
    set(color)
    {
        val program = shader.GetProgram()

        if(program != 0)
        {
            val u_color = glGetUniformLocation(program, "_Color")
            glUniform4f(u_color, color.r, color.g, color.b, color.a)
        }
    }

    private var textures : MutableList<Texture>? = null

    init   {
        textures = mutableListOf()
    }

    fun Bind(camera : Camera){
        shader.Bind(camera)
    }
}
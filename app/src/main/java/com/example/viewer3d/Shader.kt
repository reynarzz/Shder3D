package com.example.viewer3d

import android.opengl.GLES20.*

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

    public fun Bind() : Int {
        glUseProgram(program)
        return program
    }
}
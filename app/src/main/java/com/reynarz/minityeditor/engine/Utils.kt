package com.reynarz.minityeditor.engine

class Utils {
    companion object {
        fun getScreenSizeQuad(): Mesh {
            var screenQuadVerts: FloatArray = listOf(-1f, -1f, 0f,
                    -1f, 1f, 0f,
                    1f, 1f, 0f,
                    1f, -1f, 0f).toFloatArray()

            var screenQuadIndex = listOf(0, 1, 2,
                    0, 2, 3).toIntArray()

            val screenQuadUV = listOf(0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f).toFloatArray()

            return Mesh(screenQuadVerts, screenQuadIndex, screenQuadUV)
        }

        fun getQuad(size: Float): Mesh {
            var screenQuadVerts: FloatArray = listOf(-size, -size, 0f,
                    -size, size, 0f,
                    size, size, 0f,
                    size, -size, 0f).toFloatArray()

            var screenQuadIndex = listOf(0, 1, 2,
                    0, 2, 3).toIntArray()

            val screenQuadUV = listOf(0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f).toFloatArray()

            return Mesh(screenQuadVerts, screenQuadIndex, screenQuadUV)
        }

        fun getPlane(size: Float): Mesh {
            var screenPlaneVerts: FloatArray = listOf(-size, 0f, -size,
                    -size, 0f, size,
                    size, 0f, size,
                    size, 0f, -size).toFloatArray()

            var screenPlaneIndex = listOf(0, 1, 2,
                    0, 2, 3).toIntArray()

            val screenPlaneUV = listOf(0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f).toFloatArray()

            return Mesh(screenPlaneVerts, screenPlaneIndex, screenPlaneUV)
        }

        fun getErrorShaderCode(): Pair<String, String> {
            var vertexTex = """
            
            attribute vec4 _VERTEX_; 
            uniform mat4 UNITY_MATRIX_MVP;
            
            void main() 
            {
               gl_Position = UNITY_MATRIX_MVP * _VERTEX_;
            }"""

            var fragTex = """precision mediump float; 

            void main()
            {
                gl_FragColor = vec4(1., 0., 1., 1.); //pink
            }"""
            return Pair(vertexTex, fragTex)
        }

        fun getDefaultMaterial() : Material {

            var vertexTex = """ 
            
attribute vec4 _VERTEX_; 
           
attribute vec2 _UV_;
varying vec2 _uv;
varying vec4 pos;
uniform mat4 UNITY_MATRIX_MVP;
uniform mat4 UNITY_MATRIX_P;
uniform mat4 UNITY_MATRIX_V;
uniform mat4 unity_ObjectToWorld;
void main() 
{
   _uv = _UV_;
   
   pos = UNITY_MATRIX_P * UNITY_MATRIX_V * unity_ObjectToWorld * _VERTEX_;
   
   gl_Position = pos;
}"""

            var fragTex = """
            
precision mediump float; 
varying vec4 pos;

varying vec2 _uv;

uniform sampler2D sTexture;

void main()
{
    gl_FragColor = texture2D(sTexture, _uv);
}"""


            val shader = Shader(vertexTex, fragTex)
            val mat = Material(shader)

            return mat
        }

        fun getUnlitMaterial() : Material {

            var vertexTex = """ 
            
attribute vec4 _VERTEX_; 
           
attribute vec2 _UV_;
varying vec2 _uv;
varying vec4 pos;
uniform mat4 UNITY_MATRIX_MVP;
void main() 
{
   _uv = _UV_;
   
   pos = UNITY_MATRIX_MVP* _VERTEX_;
   
   gl_Position = pos;
}"""

            var fragTex = """
            
precision mediump float; 
varying vec4 pos;

varying vec2 _uv;

uniform sampler2D sTexture;

void main()
{
    gl_FragColor = vec4(0.75);
}"""

            val shader = Shader(vertexTex, fragTex)
            val mat = Material(shader)

            return mat
        }
    }

    class ShaderFileUtils {
        companion object {
            fun processInclude(include: String, shaderCode: String): String {

                var shader = shaderCode

                shaderCode.reader().forEachLine {
                    if (!it.contains("//") && it.contains("#")) {

                        var lowerCase = it.toLowerCase()

                        if (lowerCase.contains("unity.h")) {

                            shader = shaderCode.replace(it, include)
                        }
                    }
                }

                return shader;
            }
        }
    }
}
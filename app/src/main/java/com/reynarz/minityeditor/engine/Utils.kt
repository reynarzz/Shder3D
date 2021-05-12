package com.reynarz.minityeditor.engine

import android.content.res.AssetManager
import com.reynarz.minityeditor.models.ComponentType
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import androidx.appcompat.app.AppCompatActivity
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.models.MaterialConfig
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder


class Utils {
    companion object {
        fun getScreenSizeQuad(): Mesh {
            var screenQuadVerts: FloatArray = listOf(
                -1f, -1f, 0f,
                -1f, 1f, 0f,
                1f, 1f, 0f,
                1f, -1f, 0f
            ).toFloatArray()

            var screenQuadIndex = listOf(
                0, 1, 2,
                0, 2, 3
            ).toIntArray()

            val screenQuadUV = listOf(0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f).toFloatArray()

            return Mesh(screenQuadVerts, screenQuadIndex, screenQuadUV, FloatArray(1))
        }

        fun getQuad(size: Float): Mesh {
            var screenQuadVerts: FloatArray = listOf(
                -size, -size, 0f,
                -size, size, 0f,
                size, size, 0f,
                size, -size, 0f
            ).toFloatArray()

            var screenQuadIndex = listOf(
                0, 1, 2,
                0, 2, 3
            ).toIntArray()

            val screenQuadUV = listOf(0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f).toFloatArray()

            return Mesh(screenQuadVerts, screenQuadIndex, screenQuadUV, FloatArray(1))
        }

        fun getPlane(size: Float): Mesh {
            var screenPlaneVerts: FloatArray = listOf(
                -size, 0f, -size,
                -size, 0f, size,
                size, 0f, size,
                size, 0f, -size
            ).toFloatArray()

            var screenPlaneIndex = listOf(
                0, 1, 2,
                0, 2, 3
            ).toIntArray()

            val screenPlaneUV = listOf(0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f).toFloatArray()

            return Mesh(screenPlaneVerts, screenPlaneIndex, screenPlaneUV, FloatArray(1))
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

        fun getErrorMaterial(): Material {
            val shader = getErrorShaderCode()

            return Material(Shader(shader.first, shader.second))
        }

        fun getUnlitShader(unlitAmount: Float): Pair<String, String> {
            var vertexTex = """ 
            
attribute vec4 _VERTEX_; 
           
attribute vec2 _UV_;
varying vec2 _uv;
varying vec4 pos;
uniform mat4 UNITY_MATRIX_MVP;

void main() 
{
   _uv = _UV_;
   
   pos = UNITY_MATRIX_MVP * _VERTEX_;
   
   gl_Position = pos;
}"""

            var fragTex = """
            
precision mediump float; 
varying vec4 pos;

varying vec2 _uv;

uniform sampler2D _tex0;

void main()
{
    gl_FragColor = vec4(${unlitAmount}, ${unlitAmount}, ${unlitAmount}, 1.);
}"""
            return Pair(vertexTex, fragTex)
        }

        fun getTexturizedShader(): Pair<String, String> {
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

            return Pair(vertexTex, fragTex)
        }

        fun getDefaultMaterial(): Material {

            val texturized = getTexturizedShader()

            val shader = Shader(texturized.first, texturized.second)

            return Material(shader)
        }

        fun getUnlitMaterial(unlitAmount: Float): Material {

            val unlitShader = getUnlitShader(unlitAmount)

            val shader = Shader(unlitShader.first, unlitShader.second)

            return Material(shader)
        }

        fun getOutlineMaterial(): Material {

            var vertexTex = """
            
            attribute vec4 _VERTEX_; 
            uniform mat4 UNITY_MATRIX_MVP;
            attribute vec3 _NORMAL_;
            
            void main() 
            {
                vec4 pos = _VERTEX_;
                pos.xyz += _NORMAL_ * 0.4;
                
               gl_Position = UNITY_MATRIX_MVP * pos;
            }"""

            var fragTex = """precision mediump float; 

            void main()
            {
                gl_FragColor = vec4(1.0); 
            }"""

            val shader = Shader(vertexTex, fragTex)

            return Material(shader)
        }

        fun getGroundShadersCode(): Pair<String, String> {

            val groundGridVertex = """
     attribute vec4 _VERTEX_;
attribute vec2 _UV_;

uniform mat4 UNITY_MATRIX_MVP;
varying vec3 _pixelPos;
varying vec2 _uv;

void main()
{
	_uv = _UV_ - 0.5;
	_pixelPos = _VERTEX_.xyz;
	gl_Position = UNITY_MATRIX_MVP * _VERTEX_;
}
    """

            val groundGridFragment = """
        precision mediump float; 
        varying vec2 _uv;
uniform vec3 _diffuse_;

uniform vec3 _WorldSpaceCameraPos;
varying vec3 _pixelPos;

void main()
{
    float maxDist = 250.;

    //float alpha = (maxDist - length(_pixelPos - _WorldSpaceCameraPos));

    float thickness = 0.1;
    float spacing = 1.;

    if (fract(_pixelPos.x / spacing) < thickness || fract(_pixelPos.z / spacing) < thickness)
    {
        if(_pixelPos.z > -thickness && _pixelPos.z < thickness)
        {
            gl_FragColor = vec4(1.0, 0., 0., 0.7);
        }
        else if(_pixelPos.x > -thickness && _pixelPos.x < thickness)
        {
            gl_FragColor = vec4(0., 0.2, 1., 0.9);
        }
        else
        {
        
        //gl_FragColor = vec4(alpha);
           // gl_FragColor = vec4(vec3(0.5), smoothstep(alpha, 0.0, 0.2));
            gl_FragColor = vec4(vec3(0.13),  1.);
        }
    }
    else
    {
        discard;
    }

//gl_FragColor = vec4(0.3);
}
"""

            return Pair(groundGridVertex, groundGridFragment)
        }

        fun getScreenQuadShaderCode(): Pair<String, String> {
            val screenQuadVertexCode = """
            
attribute vec4 _VERTEX_; 
           
attribute vec2 _UV_;
varying vec2 _uv;
varying vec4 pos;

void main() 
{
_uv = _UV_;
gl_Position = vec4( _VERTEX_.x,  _VERTEX_.y, 0, 1);
}"""
            var screenFragTex = """
            
precision mediump float; 
varying vec4 pos;

varying vec2 _uv;

uniform sampler2D _MainTex;

void main()
{
    gl_FragColor = texture2D(_MainTex, _uv);
}
"""
            return Pair(screenQuadVertexCode, screenFragTex)
        }

        fun getShadowMappedShader(): Pair<String, String> {

            val vertexCode = """
                
                            
                attribute vec4 _VERTEX_; 
                           
                attribute vec2 _UV_;
                varying vec2 _uv;
                varying vec4 pos;
                uniform mat4 UNITY_MATRIX_MVP;
                uniform mat4 unity_ObjectToWorld;
                uniform mat4 _LIGHT;

                varying mat4 lightM;
                varying vec3 fragPos;
                varying vec4 fragPosLS;


                void main() 
                {
                   _uv = _UV_;
                   fragPos = vec4(unity_ObjectToWorld * _VERTEX_).xyz;

                  lightM = _LIGHT;

                   pos = UNITY_MATRIX_MVP * _VERTEX_;
                   fragPosLS = lightM * vec4(fragPos,1.);
                   gl_Position = pos;
                }

            """.trimIndent()

            var fragCode = """
                            
precision mediump float; 
varying vec4 pos;

varying vec2 _uv;

uniform sampler2D _tex0;
uniform sampler2D _SHADOWMAP;
varying vec4 fragPosLS;

float shadow(vec4 lpos)
{
  vec3 proj = lpos.xyz/lpos.w;
  proj = proj*0.5+0.5;
  float closestD = texture2D(_SHADOWMAP, proj.xy).r;
  float current = proj.z;

  float shadow = 0.;
   
   if(current-0.004 > closestD)
   {
   shadow = 1.;
   }
   else
   {
   shadow = 0.;
   }
  
  if(proj.z > 1.0)
      shadow = 0.0;
      
  return shadow;
 
}

void main()
{
float shadow = clamp(1.- shadow(fragPosLS) + 0.7, 0., 1.);

vec4 color = vec4(vec3(shadow), 1.);

 gl_FragColor =color * texture2D(_tex0,_uv);
}
            """.trimIndent()

            return Pair(vertexCode, fragCode)
        }

        fun componentTypeToID(componentType: ComponentType): Int {
            return when (componentType) {
                ComponentType.Transform -> R.layout.transform_fragment_view
                ComponentType.MeshRenderer -> R.layout.mesh_renderer_fragment_view
            }
        }

        fun getBitmapFromPath(path: String): Bitmap {
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            return BitmapFactory.decodeFile(path, options)
        }

        fun processInclude(include: String, shaderCode: String): String {

            var shader = shaderCode

            shaderCode.reader().forEachLine {
                if (!it.contains("//") && it.contains("#")) {

                    var lowerCase = it.toLowerCase()

                    if (lowerCase.contains("minity")) {

                        shader = shaderCode.replace(it, include)
                    } else {
                        // remove the entire line.
                        shader = shaderCode.replace(it, "")
                    }
                }
            }

            return shader
        }

        fun processMinityInclude(activity: AppCompatActivity, shaderCode: String): String {
            val include = getInclude(activity.assets, "includes/minity.h")

            return processInclude(include, shaderCode)
        }

        fun processMaterialConfig(shader: String): MaterialConfig {
            val matConfig = MaterialConfig()

            shader.reader().forEachLine { line ->

                if (line.contains("#")) {

                    val lower = removeExeciveWhiteSpace(line.toLowerCase())
                    println("procesed: " + lower)
                    if (lower.contains("blend")) {
                        matConfig.gl_blendingEnabled = true

                        val code = lower.split(" ")
                        var currentFactor = 0

                        for (i in code) {
                            val gl_factor = blendStringCodeToInt(i)
                            if (gl_factor != -1 && currentFactor < 2) {

                                if (currentFactor == 0) {
                                    matConfig.gl_srcFactor = gl_factor
                                    currentFactor++
                                } else if (currentFactor == 1) {
                                    matConfig.gl_dstFactor = gl_factor
                                    break
                                }
                            }
                        }
                    }
                }
            }

            return matConfig
        }

        private fun blendStringCodeToInt(code: String): Int {

            return when (code) {
                "zero" -> GLES20.GL_ZERO
                "one" -> GLES20.GL_ONE
                "srccolor" -> GLES20.GL_SRC_COLOR
                "oneminussrccolor" -> GLES20.GL_ONE_MINUS_SRC_COLOR
                "dstcolor" -> GLES20.GL_DST_COLOR
                "oneminusdstcolor" -> GLES20.GL_ONE_MINUS_DST_COLOR
                "srcalpha" -> GLES20.GL_SRC_ALPHA
                "oneminussrcalpha" -> GLES20.GL_ONE_MINUS_SRC_ALPHA
                "dstalpha" -> GLES20.GL_DST_ALPHA
                "constantcolor" -> GLES20.GL_CONSTANT_COLOR
                "oneminusconstantcolor" -> GLES20.GL_ONE_MINUS_CONSTANT_COLOR
                "constantalpha" -> GLES20.GL_CONSTANT_ALPHA
                "oneminusconstantalpha" -> GLES20.GL_ONE_MINUS_CONSTANT_ALPHA

                else -> -1
            }
        }

        private fun removeExeciveWhiteSpace(string: String): String {
            val final = StringBuilder()

            for (i in 0 until string.length) {

                if (string[i] != ' ' || (string[i] == ' ' && string.getOrNull(i - 1) != ' ')) {
                    final.append(string[i])
                }
            }

            return final.toString()
        }

        fun getInclude(assets: AssetManager, include: String): String {
            val inStream = assets.open(include)
            val reader = BufferedReader(InputStreamReader(inStream))
            return reader.readText()
        }
    }
}
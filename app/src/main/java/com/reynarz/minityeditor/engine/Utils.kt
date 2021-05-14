package com.reynarz.minityeditor.engine

import android.content.res.AssetManager
import com.reynarz.minityeditor.models.ComponentType
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.opengl.GLES20
import androidx.appcompat.app.AppCompatActivity
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.models.MaterialConfig
import com.reynarz.minityeditor.models.RenderQueue
import com.reynarz.minityeditor.views.ShaderEditorFragment
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.util.*


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

        fun getPickupShader(): Pair<String, String> {
            var vertexTex = """ 
            
attribute vec4 _VERTEX_; 
uniform mat4 UNITY_MATRIX_MVP;

void main() 
{
   gl_Position = UNITY_MATRIX_MVP * _VERTEX_;
}"""

            var fragTex = """
            
precision mediump float; 
uniform vec4 _pickUpColor_;

void main()
{
    gl_FragColor = _pickUpColor_;
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

                        shader = shader.replace(it, include)
                    } else {
                        // remove the entire line.
                        println("to Remove: " + it)
                        shader = shader.replace(it, "")
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

                    val code = lower.split(" ")

                    if (lower.contains("blend")) {
                        blendOptions(code, matConfig)
                    } else if (lower.contains("ztest")) {
                        zTestOptions(code, matConfig)
                    } else if (lower.contains("zwrite")) {
                        zwriteOptions(code, matConfig)
                    } else if (lower.contains("queue")) {
                        queueOptions(code, matConfig)
                    }
                }
            }

            return matConfig
        }

        private fun blendOptions(code: List<String>, matConfig: MaterialConfig) {

            matConfig.gl_blendingEnabled = true


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

        private fun zwriteOptions(code: List<String>, matConfig: MaterialConfig) {
            for (i in code) {

                val glDepthFunc = zwriteCodeToInt(i)

                if (glDepthFunc == 0) {
                    matConfig.gl_depthTestEnabled = false
                    break
                } else if (glDepthFunc == 1) {
                    matConfig.gl_depthTestEnabled = true
                    break
                }
            }
        }

        private fun zTestOptions(code: List<String>, matConfig: MaterialConfig) {

            for (i in code) {
                val glDepthFunc = depthTestFuncCodeToInt(i)

                if (glDepthFunc != -1) {
                    matConfig.gl_depthFunc = glDepthFunc
                    break
                }
            }
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

        private fun depthTestFuncCodeToInt(code: String): Int {
            return when (code) {
                "never" -> GLES20.GL_NEVER
                "less" -> GLES20.GL_LESS
                "lequal" -> GLES20.GL_LEQUAL
                "equal" -> GLES20.GL_EQUAL
                "greater" -> GLES20.GL_GREATER
                "notequal" -> GLES20.GL_NOTEQUAL
                "gequal" -> GLES20.GL_GEQUAL
                "always" -> GLES20.GL_ALWAYS
                else -> -1
            }
        }

        private fun queueOptions(code: List<String>, matConfig: MaterialConfig) {
            for (i in code) {
                val queue = queueCodeToEnum(i)

                if (queue != -1) {
                    matConfig.renderQueue = queue
                    break
                }
            }
        }

        private fun queueCodeToEnum(code: String): Int {
            return when (code) {
                "background" -> RenderQueue.Background.queueValue
                "geometry" -> RenderQueue.Geometry.queueValue
                "alphatest" -> RenderQueue.AlphaTest.queueValue
                "transparent" -> RenderQueue.Transparent.queueValue
                "overlay" -> RenderQueue.Overlay.queueValue

                else -> -1
            }
        }

        private fun zwriteCodeToInt(code: String): Int {
            return when (code) {
                "off" -> 0
                "on" -> 1
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

        fun getPickingRGBLookUpTable(size: Int): Array<Int> {
            return Array(size * 3) {
                (Random().nextFloat() * 255).toInt()
            }
        }

        class WordToHightlight {
            var word = ""
            var startIndex = 0
            var endIndex = 0
        }

         fun getWordsFromText(text: String): List<WordToHightlight> {
            val words = mutableListOf<WordToHightlight>()

            val stringBuilder = StringBuilder()
            var startIndex = 0

            for (i in 0 until text.length) {
                if (text[i].isLetterOrDigit() && !text[i].isWhitespace() || text[i] == '_' || text[i] == '#') {

                    val prevChar = text.getOrNull(i - 1)
                    if (prevChar != null && !prevChar.isLetterOrDigit() && prevChar != '_' && prevChar != '#') {
                        startIndex = i
                        //println("startIndex here!")
                    }

                    stringBuilder.append(text[i])
                } else {
                    if (stringBuilder.length > 0) {
                        words.add(WordToHightlight().also {
                            it.word = stringBuilder.toString()
                            it.startIndex = startIndex
                            it.endIndex = i
                        })
                        stringBuilder.clear()
                    }
                }
            }

            return words
        }

        private val keyword = "#9a89ff"
        private val dataType = "#9a89ff"
        private val internalVariables = "#ff6767"
        private val functions = "#65e47a"

        var shaderColorHightlight = mapOf(
            "float" to Color.parseColor(dataType),
            "#minity" to Color.parseColor(functions),
            "#Minity" to Color.parseColor(functions),
            "samplerCube" to Color.parseColor(dataType),
            "sampler2D" to Color.parseColor(dataType),
            "bool" to Color.parseColor(dataType),
            "true" to Color.parseColor(dataType),
            "false" to Color.parseColor(dataType),
            "int" to Color.parseColor(dataType),
            "vec2" to Color.parseColor(dataType),
            "vec3" to Color.parseColor(dataType),
            "vec4" to Color.parseColor(dataType),
            "bvec2" to Color.parseColor(dataType),
            "bvec2" to Color.parseColor(dataType),
            "bvec3" to Color.parseColor(dataType),
            "bvec4" to Color.parseColor(dataType),
            "ivec2" to Color.parseColor(dataType),
            "ivec3" to Color.parseColor(dataType),
            "ivec4" to Color.parseColor(dataType),
            "mat2" to Color.parseColor(dataType),
            "mat3" to Color.parseColor(dataType),
            "mat4" to Color.parseColor(dataType),
            "if" to Color.parseColor(keyword),
            "else" to Color.parseColor(keyword),
            "uniform" to Color.parseColor(keyword),
            "return" to Color.parseColor(keyword),
            "gl_FragColor" to Color.parseColor(internalVariables),
            "gl_Position" to Color.parseColor(internalVariables),
            "gl_FragCoord" to Color.parseColor(internalVariables),
            "_UV_" to Color.parseColor(internalVariables),
            "_LIGHT" to Color.parseColor(internalVariables),
            "_SHADOWMAP" to Color.parseColor(internalVariables),
            "_tex0" to Color.parseColor(internalVariables),
            "_tex1" to Color.parseColor(internalVariables),
            "_tex2" to Color.parseColor(internalVariables),
            "_tex3" to Color.parseColor(internalVariables),
            "_tex4" to Color.parseColor(internalVariables),
            "_tex5" to Color.parseColor(internalVariables),
            "_tex6" to Color.parseColor(internalVariables),
            "_tex7" to Color.parseColor(internalVariables),
            "_tex8" to Color.parseColor(internalVariables),
            "void" to Color.parseColor(keyword),
            "varying" to Color.parseColor(keyword),
            "precision" to Color.parseColor(keyword),
            "lowp" to Color.parseColor(keyword),
            "mediump" to Color.parseColor(keyword),
            "highp" to Color.parseColor(keyword),
            "const" to Color.parseColor(keyword),
            "struct" to Color.parseColor(keyword),
            "attribute" to Color.parseColor(keyword),
            "clamp" to Color.parseColor(functions),
            "texture2D" to Color.parseColor(functions),
            "radians" to Color.parseColor(functions),
            "degrees" to Color.parseColor(functions),
            "pow" to Color.parseColor(functions),
            "exp" to Color.parseColor(functions),
            "exp2" to Color.parseColor(functions),
            "log2" to Color.parseColor(functions),
            "sqrt" to Color.parseColor(functions),
            "inversesqrt" to Color.parseColor(functions),
            "mod" to Color.parseColor(functions),
            "min" to Color.parseColor(functions),
            "max" to Color.parseColor(functions),
            "mix" to Color.parseColor(functions),
            "step" to Color.parseColor(functions),
            "smoothstep" to Color.parseColor(functions),
            "length" to Color.parseColor(functions),
            "distance" to Color.parseColor(functions),
            "dot" to Color.parseColor(functions),
            "cross" to Color.parseColor(functions),
            "faceforward" to Color.parseColor(functions),
            "reflect" to Color.parseColor(functions),
            "refract" to Color.parseColor(functions),
            "matrixCompMult" to Color.parseColor(functions),
            "lessThan" to Color.parseColor(functions),
            "lessThanEqual" to Color.parseColor(functions),
            "greaterThan" to Color.parseColor(functions),
            "greaterThanEqual" to Color.parseColor(functions),
            "equal" to Color.parseColor(functions),
            "notEqual" to Color.parseColor(functions),
            "any" to Color.parseColor(functions),
            "all" to Color.parseColor(functions),
            "not" to Color.parseColor(functions),
            "textureCube" to Color.parseColor(functions),
            "normalize" to Color.parseColor(functions),
            "fract" to Color.parseColor(functions),
            "floor" to Color.parseColor(functions),
            "ceil" to Color.parseColor(functions),
            "sign" to Color.parseColor(functions),
            "abs" to Color.parseColor(functions),
            "log" to Color.parseColor(functions),
            "sin" to Color.parseColor(functions),
            "asin" to Color.parseColor(functions),
            "cos" to Color.parseColor(functions),
            "acos" to Color.parseColor(functions),
            "tan" to Color.parseColor(functions),
            "atan" to Color.parseColor(functions))
    }
}
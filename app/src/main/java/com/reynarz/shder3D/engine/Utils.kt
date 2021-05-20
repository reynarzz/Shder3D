package com.reynarz.shder3D.engine

import android.content.res.AssetManager
import com.reynarz.shder3D.models.ComponentType
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.opengl.GLES20
import androidx.appcompat.app.AppCompatActivity
import com.reynarz.shder3D.R
import com.reynarz.shder3D.models.MaterialConfig
import com.reynarz.shder3D.models.MaterialData
import com.reynarz.shder3D.models.RenderQueue
import org.koin.java.KoinJavaComponent.get
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
            attribute vec2 _UV_;
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

            return Material(Shader(shader.first, shader.second)).also {
                it.materialData = get(MaterialData::class.java)
            }
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
attribute vec2 _UV_;
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
            attribute vec2 _UV_;
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
uniform mat4 unity_ObjectToWorld;
varying vec3 _pixelPos;
varying vec2 _uv;

void main()
{
	_uv = _UV_ - 0.5;
	_pixelPos =  (unity_ObjectToWorld * _VERTEX_).xyz;
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
    float maxDist = 180.;

    float alpha = clamp(length(_pixelPos - _WorldSpaceCameraPos), 0., maxDist);

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
            gl_FragColor = vec4(vec3(0.13),  1.);
        }
        
        gl_FragColor =  mix(gl_FragColor, vec4(vec3(0.2),  1.), alpha/maxDist);
    }
    else
    {
        discard;
    }
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
 gl_Position = vec4(_VERTEX_.x,  _VERTEX_.y, 0, 1);
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

            var fragCode = """#minity
                            
precision highp float; 
varying vec4 pos;

varying vec2 _uv;

uniform sampler2D _tex0;
varying vec4 fragPosLS;


void main()
{
float shadow = clamp(1.- shadow(fragPosLS) + 0.7, 0., 1.);

vec4 color = vec4(vec3(shadow), 1.);

 gl_FragColor = color * texture2D(_tex0,_uv);
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

                if (line.contains("#") && !line.contains("//")) {

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
                    } else if (lower.contains("nobackpassed")) {
                        matConfig.hiddeFromBackPass = true
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
        private val dataTypeColor = Color.parseColor(dataType)
        private val functionsColor = Color.parseColor(functions)
        private val keywordsColor = Color.parseColor(keyword)


        var shaderColorHightlight = mapOf(
            "float" to dataTypeColor,
            "#minity" to functionsColor,
            "#Minity" to functionsColor,
            "samplerCube" to dataTypeColor,
            "sampler2D" to dataTypeColor,
            "bool" to dataTypeColor,
            "true" to dataTypeColor,
            "false" to dataTypeColor,
            "for" to dataTypeColor,
            "while" to dataTypeColor,
            "int" to dataTypeColor,
            "vec2" to dataTypeColor,
            "vec3" to dataTypeColor,
            "vec4" to dataTypeColor,
            "bvec2" to dataTypeColor,
            "bvec2" to dataTypeColor,
            "bvec3" to dataTypeColor,
            "bvec4" to dataTypeColor,
            "ivec2" to dataTypeColor,
            "ivec3" to dataTypeColor,
            "ivec4" to dataTypeColor,
            "mat2" to dataTypeColor,
            "mat3" to dataTypeColor,
            "mat4" to dataTypeColor,
            "if" to keywordsColor,
            "else" to keywordsColor,
            "uniform" to keywordsColor,
            "return" to keywordsColor,
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
            "void" to keywordsColor,
            "varying" to keywordsColor,
            "precision" to keywordsColor,
            "lowp" to keywordsColor,
            "mediump" to keywordsColor,
            "highp" to keywordsColor,
            "const" to keywordsColor,
            "struct" to keywordsColor,
            "attribute" to keywordsColor,
            "clamp" to functionsColor,
            "texture2D" to functionsColor,
            "radians" to functionsColor,
            "degrees" to functionsColor,
            "pow" to functionsColor,
            "exp" to functionsColor,
            "exp2" to functionsColor,
            "log2" to functionsColor,
            "sqrt" to functionsColor,
            "inversesqrt" to functionsColor,
            "mod" to functionsColor,
            "min" to functionsColor,
            "max" to functionsColor,
            "mix" to functionsColor,
            "step" to functionsColor,
            "smoothstep" to functionsColor,
            "length" to functionsColor,
            "distance" to functionsColor,
            "dot" to functionsColor,
            "cross" to functionsColor,
            "faceforward" to functionsColor,
            "reflect" to functionsColor,
            "refract" to functionsColor,
            "matrixCompMult" to functionsColor,
            "lessThan" to functionsColor,
            "lessThanEqual" to functionsColor,
            "greaterThan" to functionsColor,
            "greaterThanEqual" to functionsColor,
            "equal" to functionsColor,
            "notEqual" to functionsColor,
            "any" to functionsColor,
            "all" to functionsColor,
            "not" to functionsColor,
            "textureCube" to functionsColor,
            "normalize" to functionsColor,
            "fract" to functionsColor,
            "floor" to functionsColor,
            "ceil" to functionsColor,
            "sign" to functionsColor,
            "abs" to functionsColor,
            "log" to functionsColor,
            "sin" to functionsColor,
            "asin" to functionsColor,
            "cos" to functionsColor,
            "acos" to functionsColor,
            "tan" to functionsColor,
            "atan" to functionsColor
        )
    }
}
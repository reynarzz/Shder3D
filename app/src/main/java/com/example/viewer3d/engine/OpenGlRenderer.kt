package com.example.viewer3d.engine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import com.example.viewer3d.MainActivity
import java.io.ByteArrayOutputStream
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class OpenGlRenderer(val context: Context) : GLSurfaceView.Renderer {

    private lateinit var vertexShaderCode: String
    private lateinit var fragmentShaderCode: String

    //texture2D(sTexture, _uv) * vec4(linearize_depth(gl_FragCoord.z, 1.0, 30.0));
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
    }

    var changed = false

    var camera = Camera()
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

uniform sampler2D sTexture;

void main()
{
    gl_FragColor = texture2D(sTexture, _uv) ;
}"""

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
    """.trimIndent()

    val groundGridFragment = """
        precision mediump float; 
        varying vec2 _uv;
uniform vec3 _diffuse_;

uniform vec3 _cameraWorldPos_;
varying vec3 _pixelPos;

void main()
{
    float maxDist = 100.;

   // float alpha = (maxDist - length(_pixelPos - _cameraWorldPos_));

    float thickness = 0.01;
    float spacing = 100.;

//    if (fract(_pixelPos.x / spacing) < thickness || fract(_pixelPos.z / spacing) < thickness)
//    {
//        if(int(_pixelPos.z) == 0)
//        {
//            gl_FragColor = vec4(1.0, 0., 0., 0.7);
//        }
//        else if(int(_pixelPos.x) == 0)
//        {
//            gl_FragColor = vec4(0., 0.2, 1., 0.9);
//        }
//        else
//        {
//           // gl_FragColor = vec4(vec3(1.), clamp(alpha, 0.0, 0.2));
//            gl_FragColor = vec4(vec3(1.),  0.2);
//        }
//    }
//    else
//    {
//        discard;
//        //gl_FragColor = vec4(1.);
//    }

gl_FragColor = vec4(0.3);
}
"""

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)
        // glEnable(GL_TEXTURE_2D)
        //glEnable(GL_BLEND)
        //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        camera = Camera()
        camera.updateProjection(width, height)
        camera.updateView()
        //CoffeeRestaurant
        //SimpleScene

        frameBuffer = FrameBuffer(MainActivity.width, MainActivity.height)
        shader = Shader(vertexShaderCode, fragmentShaderCode)
        quadShader = Shader(screenQuadVertexCode, screenFragTex)

        groundGridShader = Shader(groundGridVertex, groundGridFragment)
        groundPlane = Utils.getPlane(2000f)
        screenQuadMesh = Utils.getScreenSizeQuad()

        ObjParser(context, "models/girl.obj").also {

            var data = it.getModelData()

            val mesh = Mesh(data.mVertices, data.mIndices, data.mUVs)


            loadedMeshes.add(mesh)

            // Texture
            val girlTexture = Texture(context,"textures/girltex_small.jpg")
            girlTex = girlTexture.textureID
        }
    }

    var girlTex = 0

    // Loads a texture into OpenGL


    // Unloads a texture from OpenGL
    private fun unloadTexture(textureId: Int) {
        val textures = IntArray(1)
        textures[0] = textureId
        glDeleteTextures(1, textures, 0)
    }

    var loadedMeshes = mutableListOf<Mesh>()
    lateinit var shader: Shader
    lateinit var quadShader: Shader


    // Commands to do in the render thread.
    fun PushCommand(/*A delegate here to call a command*/) {
    }

    fun setShaders(vertexCode: String, fragmentCode: String) {
        fragmentShaderCode = fragmentCode
        vertexShaderCode = vertexCode

        changed = true
    }

    var lStartTime = System.currentTimeMillis().toFloat()

    var prevMil = 0f
    lateinit var frameBuffer: FrameBuffer

    lateinit var screenQuadMesh: Mesh
    var groundPlane: Mesh? = null

    var groundGridShader: Shader? = null

    override fun onDrawFrame(gl: GL10?) {
        frameBuffer.bind()
        glEnable(GL_DEPTH_TEST)

        glClearColor(0.2f, 0.2f, 0.2f, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        val lEndTime = System.currentTimeMillis().toFloat()

        val output = (lEndTime - lStartTime).toFloat()

        lStartTime = lEndTime
        //shader.setDeltaTimeTest(lEndTime.toFloat(), output)

        if (changed) {
            changed = false

            shader.replaceShaders(vertexShaderCode, fragmentShaderCode)
        }

        // glViewport(0,0,MainActivity.width/8,MainActivity.height/8)

        glBindTexture(GL_TEXTURE_2D, girlTex)

        for (mesh in loadedMeshes) {

            mesh.Bind_Test(shader.Bind(camera))

            glDrawElements(GL_TRIANGLES, mesh.indices.size, GL_UNSIGNED_INT, mesh.indexBuffer)
        }
        shader.TestRotation()

        groundPlane!!.Bind_Test(groundGridShader!!.Bind(camera))
        glDrawElements(GL_TRIANGLES, groundPlane!!.indices.size, GL_UNSIGNED_INT, groundPlane!!.indexBuffer)

        frameBuffer.unBind()

        glDisable(GL_DEPTH_TEST)
        glClear(GL_COLOR_BUFFER_BIT)

        screenQuadMesh.Bind_Test(quadShader.Bind(camera))

        glBindTexture(GL_TEXTURE_2D, frameBuffer.colorTexture)

        glViewport(0, 0, MainActivity.width, MainActivity.height)

        glDrawElements(GL_TRIANGLES, screenQuadMesh.indices.size, GL_UNSIGNED_INT, screenQuadMesh.indexBuffer)
    }
}
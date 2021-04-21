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
    private lateinit  var fragmentShaderCode  :String

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


        ObjParser(context, "models/girl.obj").also {

            var data = it.getModelData()

            val mesh = Mesh(data.mVertices, data.mIndices, data.mUVs)




            screenQuadMesh = Utils.getScreenSizeQuad()
            screenQuadMesh.Bind_Test(quadShader.Bind(camera))
            quadShader.unBind()

            mesh.Bind_Test(shader.Bind(camera))
            shader.unBind()

            loadedMeshes.add(mesh)

            // Texture
            girlTex = loadTexture("textures/girltex_small.jpg")
        }
    }

    var girlTex = 0
    // Loads a texture into OpenGL
    private fun loadTexture(path: String): Int {

        var bitmap = doInBackground(path)

        val imageArray = imageToBitmap(bitmap)
        bitmap = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.size)


         bitmap = createFlippedBitmap(bitmap, false, true)
        val textures = IntArray(1)
        glGenTextures(1, textures, 0)

        glBindTexture(GL_TEXTURE_2D, textures[0])
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        return textures[0]
    }

    fun createFlippedBitmap(source: Bitmap, xFlip: Boolean, yFlip: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.postScale(if (xFlip) -1f else 1f, if (yFlip) -1f else 1f, source.width / 2f, source.height / 2f)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
    // Unloads a texture from OpenGL
    private fun unloadTexture(textureId: Int) {
        val textures = IntArray(1)
        textures[0] = textureId
        glDeleteTextures(1, textures, 0)
    }

     var loadedMeshes = mutableListOf<Mesh>()
    lateinit var shader: Shader
    lateinit var quadShader: Shader

    fun doInBackground(path: String): Bitmap {

        val imageAsset = context.assets.open(path)
        //val `in` = java.net.URL(imageURL).openStream()
        val image = BitmapFactory.decodeStream(imageAsset)


        return Bitmap.createBitmap(image)
    }

    fun imageToBitmap(bitmap: Bitmap) : ByteArray {

        val stream = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

        return stream.toByteArray()
    }

    // Commands to do in the render thread.
    fun PushCommand(/*A delegate here to call a command*/){
    }

    fun setShaders(vertexCode: String, fragmentCode: String){
        fragmentShaderCode = fragmentCode
        vertexShaderCode = vertexCode

        changed = true
    }

    var lStartTime = System.currentTimeMillis().toFloat()

    var prevMil = 0f
    lateinit var frameBuffer :FrameBuffer

    lateinit var screenQuadMesh: Mesh

    override fun onDrawFrame(gl: GL10?) {
        frameBuffer.bind()
        glEnable(GL_DEPTH_TEST)

        glClearColor(0.2f,0.2f,0.2f, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        val lEndTime = System.currentTimeMillis().toFloat()

        val output = (lEndTime - lStartTime).toFloat()

        lStartTime = lEndTime
        //shader.setDeltaTimeTest(lEndTime.toFloat(), output)

        if(changed)
        {
            changed = false

            shader.replaceShaders(vertexShaderCode, fragmentShaderCode)
        }

        glBindTexture(GL_TEXTURE_2D, girlTex)



        for(mesh in loadedMeshes)
        {

            mesh.Bind_Test(shader.Bind(camera))
            glDrawElements(GL_TRIANGLES, mesh.indices.size, GL_UNSIGNED_INT, mesh.indexBuffer)
        }

        frameBuffer.unBind()

        glDisable(GL_DEPTH_TEST)
        glClear(GL_COLOR_BUFFER_BIT)

        screenQuadMesh.Bind_Test(quadShader.Bind(camera))

        glBindTexture(GL_TEXTURE_2D, frameBuffer.colorTexture[0])

        glDrawElements(GL_TRIANGLES, screenQuadMesh.indices.size, GL_UNSIGNED_INT, screenQuadMesh.indexBuffer)
    }
}
package com.example.viewer3d.engine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import java.io.ByteArrayOutputStream
import java.util.*
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

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)
        glEnable(GL_TEXTURE_2D)
        //glEnable(GL_BLEND)
        //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        camera = Camera()
        camera.updateProjection(width, height)
        camera.updateView()
        //CoffeeRestaurant
        //SimpleScene

        shader = Shader(vertexShaderCode, fragmentShaderCode)


        ObjParser(context, "models/girl.obj").also {

            var data = it.getModelData()

            val mesh = Mesh(data.mVertices, data.mIndices, data.mUVs)
            //val quad = Utils.getScreenSizeQuad()

            //loadedMeshes.add(quad)

            var program = shader.Bind(camera)
            //quad.Bind_Test(program)
            mesh.Bind_Test(program)
            loadedMeshes.add(mesh)

            // Texture
            loadTexture("textures/girltex_small.jpg")
        }
    }


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
    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        val lEndTime = System.currentTimeMillis().toFloat()

        val output = (lEndTime - lStartTime).toFloat()

        lStartTime = lEndTime
        shader.setDeltaTimeTest(lEndTime.toFloat(), output)

        if(changed)
        {
            changed = false

            shader.replaceShaders(vertexShaderCode, fragmentShaderCode)

            val program = shader.Bind(camera)

            loadedMeshes[0].Bind_Test(program)

            val uvAttrib = glGetAttribLocation(program, "_UV_")

            glVertexAttribPointer(uvAttrib, 2, GL_FLOAT, true, 2 * 4, loadedMeshes[0].uvBuffer)
            glEnableVertexAttribArray(uvAttrib)
        }

        shader.TestRotation()

        for(mesh in loadedMeshes) {

            glDrawElements(GL_TRIANGLES, mesh.indices.size, GL_UNSIGNED_INT, mesh.indexBuffer)
        }
    }
}
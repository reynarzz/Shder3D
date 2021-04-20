package com.example.viewer3d.engine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import com.mokiat.data.front.parser.IOBJParser
import com.mokiat.data.front.parser.OBJModel
import com.mokiat.data.front.parser.OBJParser
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class OpenGlRenderer(val context: Context) : GLSurfaceView.Renderer {

    private val vertexShaderCode =
        "attribute vec4 vPosition;" +
                "uniform mat4 _VP_;" +
                "uniform mat4 _M_;" +
                "uniform sampler2D _texture;" +
                "attribute vec2 _UV_;" +
                "varying vec2 _uv;" +
                "varying  vec4 pos;" +
                "void main() {" +
                "pos = vPosition;" +
                "_uv = _UV_;" +
                "gl_Position = _VP_ * _M_ * vPosition;" +
                "}"

    private var fragmentShaderCode = """
            precision mediump float;
            uniform vec4 vColor;
            varying vec4 pos;
            varying vec2 _uv;
            uniform sampler2D sTexture;
            
            float linearize_depth(float d,float zNear,float zFar)
            {
                 return (2.0 * zNear) / (zFar + zNear - d * (zFar - zNear));
            }
            void main()
            {
                gl_FragColor = texture2D(sTexture, _uv);
            }
            """

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
            loadedMeshes.add(mesh)

            var program = shader.Bind(camera)
            mesh.Bind_Test(program)

            // Texture
            var bitmap = doInBackground("textures/girltex_small.jpg")

            val imageArray = imageToBitmap(bitmap)
            bitmap = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.size)

            loadTexture(bitmap)
            val uvAttrib = glGetAttribLocation(program, "_UV_")

            glVertexAttribPointer(uvAttrib, 2, GL_FLOAT, true, 2 * 4, mesh.uvBuffer)
            glEnableVertexAttribArray(uvAttrib)
        }
    }

    fun createFlippedBitmap(source: Bitmap, xFlip: Boolean, yFlip: Boolean): Bitmap? {
        val matrix = Matrix()
        matrix.postScale(if (xFlip) -1f else 1f, if (yFlip) -1f else 1f, source.width / 2f, source.height / 2f)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
    // Loads a texture into OpenGL
    private fun loadTexture(bit: Bitmap): Int {

        val bitmap = createFlippedBitmap(bit, false, true)
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

    fun PushCommand(/*A delegate here to call a command*/){
    }

    fun setFragmentShader(fragmentCode: String){
        fragmentShaderCode = fragmentCode
        changed = true
//
//        shader.replaceFragmentShader(fragmentCode)
//
//        val program = shader.Bind(camera)
//        loadedMeshes[0].Bind_Test(program)
//
//        val uvAttrib = glGetAttribLocation(program, "_UV_")
//
////        glVertexAttribPointer(uvAttrib, 2, GL_FLOAT, true, 2 * 4, loadedMeshes[0].uvBuffer)
////        glEnableVertexAttribArray(uvAttrib)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        if(changed)
        {
            changed = false

            shader.replaceFragmentShader(fragmentShaderCode)

            val program = shader.Bind(camera)
            loadedMeshes[0].Bind_Test(program)

            val uvAttrib = glGetAttribLocation(program, "_UV_")

            glVertexAttribPointer(uvAttrib, 2, GL_FLOAT, true, 2 * 4, loadedMeshes[0].uvBuffer)
            glEnableVertexAttribArray(uvAttrib)
        }

        shader.TestRotation()

        for(mesh in loadedMeshes){
            glDrawElements(GL_TRIANGLES, mesh.indices.size, GL_UNSIGNED_INT, mesh.indexBuffer)
        }
    }
}
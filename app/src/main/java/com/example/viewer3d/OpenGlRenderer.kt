package com.example.viewer3d

import android.content.Context
import android.graphics.*
import android.media.Image
import android.media.ImageReader
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import androidx.core.graphics.set
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.nio.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class OpenGlRenderer(val context: Context) : GLSurfaceView.Renderer {

    private val vertexShaderCode =
        "attribute vec4 vPosition;" +
                "uniform mat4 _VP_;"+
                "uniform mat4 _M_;"+
                "uniform sampler2D _texture;"+
                "attribute vec2 _UV_;"+
                "varying vec2 _uv;"+
                "varying  vec4 pos;"+
                "void main() {" +
                "pos = vPosition;"+
                "_uv = _UV_;"+
                "gl_Position = _VP_ * _M_ * vPosition;" +
                "}"

    private val fragmentShaderCode = "precision mediump float;"+
    "uniform vec4 vColor;"+
            "varying vec4 pos;"+
            "varying vec2 _uv;"+
            "uniform sampler2D _texture;"+
            "float linearize_depth(float d,float zNear,float zFar)"+
            "{"+
            "return (2.0 * zNear) / (zFar + zNear - d * (zFar - zNear));"+
            "}"+
    "void main()"+
    "{"+
        "gl_FragColor = texture2D(_texture, _uv) + vec4(0.2);"+
    "}"
//texture2D(_texture, _uv) * vec4(linearize_depth(gl_FragCoord.z, 1.0, 30.0));
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

    }


    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

        var camera = Camera()
        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)

        camera.updateProjection(width, height)
        camera.updateView()
        //CoffeeRestaurant
        //SimpleScene
        MyObjParser(context, "models/cube.obj").also {

            var data = it.getModelData()

                mesh = Mesh(data.mVertices, data.mIndices)
                shader = Shader(vertexShaderCode, fragmentShaderCode)

                var program = shader.Bind(camera)
                mesh.Bind_Test(program)

            // Texture
            val bitmap = doInBackground("textures/girlsmooth.png")

            val imageArray = imageToBitmap(bitmap)

            val loc = glGetAttribLocation(program, "_UV_")

            val imageBuffer = ByteBuffer.allocateDirect(imageArray.size *8).run{
                order(ByteOrder.BIG_ENDIAN)
                asFloatBuffer().apply {
                    put(imageArray)
                    position(0)
                }
            }

            //glGenTextures(GL_TEXTURE_2D, imageBuffer)

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)


            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA4, bitmap!!.width, bitmap.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageBuffer)
            //glBindTexture(GL_TEXTURE_2D, 0)


            glActiveTexture(GL_TEXTURE0)

            val uvBuffer = ByteBuffer.allocateDirect(data.mUVs.size * 4).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().run {
                    put(data.mUVs)
                    position(0)
                }
            }

            glVertexAttribPointer(loc, 2, GL_FLOAT, true, 2*4 , uvBuffer)
            glEnableVertexAttribArray(loc)

        }
    }



    lateinit var mesh : Mesh
    lateinit var shader : Shader

    fun doInBackground(path: String) : Bitmap? {

            val imageAsset = context.assets.open(path)
            //val `in` = java.net.URL(imageURL).openStream()
        val image = BitmapFactory.decodeStream(imageAsset)



       return Bitmap.createBitmap(image)//

    }

    private fun imageToBitmap(bitmap: Bitmap?): ByteArray {
        //val bitmap = (image.drawable as BitmapDrawable).bitmap

        val stream = ByteArrayOutputStream()
        //bitmap?.reconfigure(512, 512, Bitmap.Config.ARGB_8888)

        bitmap?.compress(Bitmap.CompressFormat.PNG, 0, stream)
//        for (x in 0 until bitmap!!.width){
//
//            for (y in 0 until bitmap!!.height){
//                bitmap?.setPixel(x, y, Color.rgb(1, 0, 0))
//            }
//        }

        return stream.toByteArray()
    }

    override fun onDrawFrame(gl: GL10?)
    {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        shader.TestRotation()

        glDrawElements(GL_TRIANGLES, mesh.indices.size, GL_UNSIGNED_INT, mesh.indexBuffer)
    }
}
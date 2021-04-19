package com.example.viewer3d.engine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import com.mokiat.data.front.parser.IOBJParser
import com.mokiat.data.front.parser.OBJModel
import com.mokiat.data.front.parser.OBJParser
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
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

    private val fragmentShaderCode = """
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
                gl_FragColor = texture2D(sTexture, vec2(_uv.x, 1.-_uv.y));
            }
            """

    //texture2D(sTexture, _uv) * vec4(linearize_depth(gl_FragCoord.z, 1.0, 30.0));
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

    }

fun getMeshes(path : String) : List<Mesh> {

    val vertices = mutableListOf<Float>()
    val index = mutableListOf<Int>()
    val tempIndex = mutableListOf<Int>()
    val uv = mutableListOf<Float>()
    val meshes = mutableListOf<Mesh>()

    // Open a stream to your OBJ resource
    val inStream = context.assets.open(path)
    BufferedReader(InputStreamReader(inStream)).use {
        val parser: IOBJParser = OBJParser()
        val model: OBJModel = parser.parse(it)

        for (mObj in model.objects) {

            for (mesh in mObj.meshes) {
                var intIndex = 0
                vertices.clear()
                index.clear()
                uv.clear()
                for (face in mesh.faces) {
                    tempIndex.clear()

                    for (reference in face.references) {
                        val vertex = model.getVertex(reference)

                        vertices.add(vertex.x)
                        vertices.add(vertex.y)
                        vertices.add(vertex.z)

                        if(reference.hasVertexIndex()){
                            tempIndex.add(reference.vertexIndex)
                        }

                        if (reference.hasNormalIndex()) {
                            val normal = model.getNormal(reference)
                        }

                        if (reference.hasTexCoordIndex()) {
                            val texcoord = model.getTexCoord(reference)
                            uv.add(texcoord.u)
                            uv.add(texcoord.v)
                        }
                    }

                    for (i in 1 until  tempIndex.size-1){
                        index.add(tempIndex[0])
                        index.add(tempIndex[i])
                        index.add(tempIndex[i+1])
                    }
                }
                meshes.add(Mesh(vertices.toFloatArray(), index.toIntArray(), uv.toFloatArray()))
            }
        }
    }

    return meshes
}



    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

        var camera = Camera()
        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)
        glEnable(GL_TEXTURE_2D)
        //glEnable(GL_BLEND)
        //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        camera.updateProjection(width, height)
        camera.updateView()
        //CoffeeRestaurant
        //SimpleScene

        loadedMeshes = mutableListOf<Mesh>()
        // loadedMeshes = getMeshes("models/cube.obj")

//        loadedMeshes.also {
//            shader = Shader(vertexShaderCode, fragmentShaderCode)
//
//            for (mesh in it){
//
//                var program = shader.Bind(camera)
//                mesh.Bind_Test(program)
//
//                // Texture
//                var bitmap = doInBackground("textures/girltex2.jpg")
//
//                val imageArray = imageToBitmap(bitmap)
//                bitmap = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.size)
//
//                loadTexture(bitmap)
//                val uvAttrib = glGetAttribLocation(program, "_UV_")
//
//                glVertexAttribPointer(uvAttrib, 2, GL_FLOAT, true, 2 * 4, mesh.uvBuffer)
//                glEnableVertexAttribArray(uvAttrib)
//            }
//
//        }
        ObjParser(context, "models/girl.obj").also {

            var data = it.getModelData()

            val mesh = Mesh(data.mVertices, data.mIndices, data.mUVs)
            shader = Shader(vertexShaderCode, fragmentShaderCode)
            loadedMeshes.add(mesh)

            var program = shader.Bind(camera)
            mesh.Bind_Test(program)

            // Texture
            var bitmap = doInBackground("textures/girltex2.jpg")

            val imageArray = imageToBitmap(bitmap)
            bitmap = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.size)

            loadTexture(bitmap)
            val uvAttrib = glGetAttribLocation(program, "_UV_")

            glVertexAttribPointer(uvAttrib, 2, GL_FLOAT, true, 2 * 4, mesh.uvBuffer)
            glEnableVertexAttribArray(uvAttrib)
 //Open a stream to your OBJ resource



            val imageBuffer = ByteBuffer.allocateDirect(imageArray.size * 4).apply {
                order(ByteOrder.nativeOrder())
                put(imageArray)
                position(0)
            }


//            var buffer = IntArray(1)
//
//            glGenTextures(1, buffer, 0)
//            glBindTexture(GL_TEXTURE_2D, buffer[0])
//
//            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
//            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
//            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
//            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
//            GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)

            //glActiveTexture(GL_TEXTURE0)

//            val uvBuffer = ByteBuffer.allocateDirect(data.mUVs.size * 4).run {
//                order(ByteOrder.nativeOrder())
//                asFloatBuffer().apply {
//                    put(data.mUVs)
//                    position(0)
//                }
//            }

        }
    }

    // Loads a texture into OpenGL
    private fun loadTexture(bitmap: Bitmap): Int {
        val textures = IntArray(1)
        glGenTextures(1, textures, 0)
        val textureWidth = bitmap.width
        val textureHeight = bitmap.height
        glBindTexture(GL_TEXTURE_2D, textures[0])
        GLUtils.texImage2D(GL_TEXTURE_2D, 0,bitmap, 0)
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

    lateinit var loadedMeshes : MutableList<Mesh>
    lateinit var shader: Shader

    fun doInBackground(path: String): Bitmap {

        val imageAsset = context.assets.open(path)
        //val `in` = java.net.URL(imageURL).openStream()
        val image = BitmapFactory.decodeStream(imageAsset)

        return Bitmap.createBitmap(image)
    }

    fun imageToBitmap(bitmap: Bitmap): ByteArray {

        val stream = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

        return stream.toByteArray()
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        shader.TestRotation()

        for(mesh in loadedMeshes){
            glDrawElements(GL_TRIANGLES, mesh.indices.size, GL_UNSIGNED_INT, mesh.indexBuffer)

        }
    }
}
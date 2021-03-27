package com.example.viewer3d

import android.opengl.GLES20.*
import java.lang.Exception
import java.nio.*

const val FLOAT_BYTES = 4
const val COORDS_PER_VERTEX = 3

class Mesh(vertices : FloatArray, var indices: IntArray) {


     lateinit var vertexBuffer: FloatBuffer
          private set
     lateinit var indexBuffer: IntBuffer
          private set

     private val vertexStride: Int = COORDS_PER_VERTEX * FLOAT_BYTES

     init {

          vertexBuffer = ByteBuffer.allocateDirect(vertices.size * FLOAT_BYTES).run {

               // use the device hardware's native byte order
               order(ByteOrder.nativeOrder())

               // create a floating point buffer from the ByteBuffer
               asFloatBuffer().apply {

                    // add the coordinates to the FloatBuffer
                    put(vertices)

                    // set the buffer to read the first coordinate
                    position(0)
               }
          }

          indexBuffer = ByteBuffer.allocateDirect(indices.size * FLOAT_BYTES).run {
               order(ByteOrder.nativeOrder())
               asIntBuffer().apply {
                    put(indices)
                    position(0)
               }
          }
     }

     fun Bind_Test(program: Int){

          glGetAttribLocation(program, "vPosition").also {

               glEnableVertexAttribArray(it)

               glVertexAttribPointer(
                    it,
                    COORDS_PER_VERTEX,
                    GL_FLOAT,
                    false,
                    vertexStride,
                    vertexBuffer
               )
          }
     }
}
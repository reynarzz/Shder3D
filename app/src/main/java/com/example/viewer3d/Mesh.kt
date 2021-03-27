package com.example.viewer3d

import android.opengl.GLES20.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

const val FLOAT_BYTES = 4

class Mesh(vertices : FloatArray, var indices: IntArray) {


     public var vertexBuffer: FloatBuffer
          private set
     public var indexBuffer: IntBuffer
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

          indexBuffer = ByteBuffer.allocateDirect(3 * 4).run {
               order(ByteOrder.nativeOrder())
              asIntBuffer().apply {
                    put(indices)
                    position(0)
               }
          }
     }

     public fun Bind_Test(program: Int){

          glGetAttribLocation(program, "vPosition").also {

               // Enable a handle to the triangle vertices
               glEnableVertexAttribArray(it)

               // Prepare the triangle coordinate data
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
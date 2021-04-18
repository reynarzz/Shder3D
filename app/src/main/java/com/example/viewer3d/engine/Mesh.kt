package com.example.viewer3d.engine

import android.opengl.GLES20.*
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

               order(ByteOrder.nativeOrder())
               asFloatBuffer().apply {
                    put(vertices)
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
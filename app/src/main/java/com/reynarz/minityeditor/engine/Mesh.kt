package com.reynarz.minityeditor.engine

import android.opengl.GLES20.*
import java.nio.*

const val FLOAT_BYTES = 4
const val COORDS_PER_VERTEX = 3

class Mesh(vertices : FloatArray, indices: IntArray, uv : FloatArray) {

      var vertexBuffer: FloatBuffer
          private set
      var indexBuffer: IntBuffer
          private set
       var uvBuffer : FloatBuffer
          private set

     val indicesCount = indices.size

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

          uvBuffer = ByteBuffer.allocateDirect(uv.size * 4).run {
               order(ByteOrder.nativeOrder())
               asFloatBuffer().apply {
                    put(uv)
                    position(0)
               }
          }
     }

     fun bind(program: Int){

          glGetAttribLocation(program, "_VERTEX_").also {

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

          val uvAttrib = glGetAttribLocation(program, "_UV_")

          glVertexAttribPointer(uvAttrib, 2, GL_FLOAT, true, 2 * 4, uvBuffer)
          glEnableVertexAttribArray(uvAttrib)
     }
}
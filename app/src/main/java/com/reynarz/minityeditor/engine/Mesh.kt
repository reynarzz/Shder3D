package com.reynarz.minityeditor.engine

import android.opengl.GLES20.*
import java.nio.*

const val FLOAT_BYTES = 4
const val COORDS_PER_VERTEX = 3

class Mesh(val vertices: FloatArray, val indices: IntArray, val uv: FloatArray, val normals: FloatArray) {

    var vertexBuffer: FloatBuffer? = null

    var indexBuffer: IntBuffer? = null
    var uvBuffer: FloatBuffer? = null
    var normalsBuffer: FloatBuffer? = null

    val indicesCount = indices.size
    val vertexCount = vertices.size

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

        normalsBuffer = ByteBuffer.allocateDirect(normals.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(normals)
                position(0)
            }
        }
    }

    fun bind(program: Int) {

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

        val normalsAttrib = glGetAttribLocation(program, "_NORMAL_")
        glVertexAttribPointer(normalsAttrib, 3, GL_FLOAT, true, 3 * 4, normalsBuffer)
        glEnableVertexAttribArray(normalsAttrib)
    }
}
package com.example.viewer3d

import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import java.nio.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

const val COORDS_PER_VERTEX = 2

class OpenGlRenderer : GLSurfaceView.Renderer {
    var verticesCount : Int = 0

    private val vertexShaderCode =
        "attribute vec4 vPosition;" +
                "void main() {" +
                "  gl_Position = vPosition;" +
                "}"

    private val fragmentShaderCode =
        "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vec4(1.0);" +
                "}"

    var program = 0
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

        Tutorial();
    }
    var triangleCoords = floatArrayOf(     // in counterclockwise order:
        0.0f, 0.622008459f,      // top
        -0.5f, -0.311004243f,    // bottom left
        0.5f, -0.311004243f     // bottom right
    )

    lateinit var vertexBuffer:FloatBuffer

    fun Tutorial() {

//        vertexBuffer =
//                // (number of coordinate values * 4 bytes per float)
//            ByteBuffer.allocateDirect(triangleCoords.size * 4).run {
//                // use the device hardware's native byte order
//                order(ByteOrder.nativeOrder())
//
//                // create a floating point buffer from the ByteBuffer
//                asFloatBuffer().apply {
//                    // add the coordinates to the FloatBuffer
//                    put(triangleCoords)
//                    // set the buffer to read the first coordinate
//                    position(0)
//                }
//            }

                // (number of coordinate values * 4 bytes per float)

        vertexBuffer = ByteBuffer.allocateDirect(triangleCoords.size * 4).run {
                // use the device hardware's native byte order
                order(ByteOrder.nativeOrder())

                // create a floating point buffer from the ByteBuffer
                asFloatBuffer().apply {
                    // add the coordinates to the FloatBuffer
                    put(triangleCoords)
                    // set the buffer to read the first coordinate
                    position(0)
                }
            }

        val vertexShader: Int = loadShader(GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GL_FRAGMENT_SHADER, fragmentShaderCode)

        // create empty OpenGL ES Program
        program = glCreateProgram().also {

            // add the vertex shader to program
            glAttachShader(it, vertexShader)

            // add the fragment shader to program
            glAttachShader(it, fragmentShader)

            // creates OpenGL ES program executables
            glLinkProgram(it)
        }
    }

    fun loadShader(type: Int, shaderCode: String): Int {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        return glCreateShader(type).also { shader ->

            // add the source code to the shader and compile it
            glShaderSource(shader, shaderCode)
            glCompileShader(shader)
        }
    }

    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0

    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    fun draw() {
        // Add program to OpenGL ES environment
        glUseProgram(program)

        // get handle to vertex shader's vPosition member
        positionHandle = glGetAttribLocation(program, "vPosition").also {

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

            // get handle to fragment shader's vColor member
//            mColorHandle = glGetUniformLocation(program, "vColor").also { colorHandle ->
//
//                // Set color for drawing the triangle
//                glUniform4fv(colorHandle, 1, color, 0)
//            }

            // Draw the triangle
            glDrawArrays(GL_TRIANGLES, 0, vertexCount)

            // Disable vertex array
            glDisableVertexAttribArray(it)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_DEPTH_BUFFER_BIT or GL_COLOR_BUFFER_BIT)

        draw();
    }
}
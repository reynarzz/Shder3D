package com.reynarz.minityeditor.engine

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import com.reynarz.minityeditor.views.MainActivity
import kotlin.math.sqrt

class OpenGLView(context: Context, attributeSet: AttributeSet) :
    GLSurfaceView(context, attributeSet) {

    val sensibility = 0.75f

    companion object{
        var xPixel = 0f
        var yPixel = 0f

        fun setTouchPixelPos(x : Float, y : Float){
            xPixel = x
            yPixel = y
        }
    }
    var renderer = OpenGlRenderer(context)

    init {
        setEGLContextClientVersion(2)
        setPreserveEGLContextOnPause(true)
        setRenderer(renderer)
        //renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    private var prevX = 0f
    private var prevY = 0f
    private var prevZoomDist = 0.0f

    fun dot(a: vec3, b: vec3): Float {
        return a.x * b.x + a.y * b.y + a.z + b.z
    }

    fun getDistance(a: vec3, b: vec3): Float {
        val diff = vec3(b.x - a.x, b.y - a.y, b.z - a.z)

        return sqrt(dot(diff, diff))
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {


        val action = event!!.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                // prevents weird jumping
                prevX = event!!.x
                prevY = event!!.y
            }

            MotionEvent.ACTION_POINTER_UP -> {
                prevX = event!!.x
                prevY = event!!.y
                Log.d("two fingers", "Up")
            }

            MotionEvent.ACTION_UP ->
            {
                prevX = event!!.x
                prevY = event!!.y
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                event.getPointerId(0)
                val finger1 = vec3(

                    (event.getX(0) / MainActivity.width.toFloat() - 0.5f) * 2f,
                    (event.getY(0) / MainActivity.height.toFloat() - 0.5f) * 2f,
                    0f
                )
                val finger2 = vec3(
                    (event.getX(1) / MainActivity.width.toFloat() - 0.5f) * 2f,
                    (event.getY(1) / MainActivity.height.toFloat() - 0.5f) * 2f,
                    0f
                )



                Log.d("two fingers", "(${finger1.x}), (${finger2.x})")

                prevZoomDist = getDistance(finger1, finger2)
            }


            MotionEvent.ACTION_MOVE -> {


                if (event.pointerCount == 1) {
                    val dx = event!!.x - prevX
                    val dy = event!!.y - prevY

                    renderer.rot = vec3(renderer.rot.x + dx, renderer.rot.y + dy, 0f)

                    prevX = event!!.x
                    prevY = event!!.y
                }

                //--Log.d("moving", dx.toString())

                if (event.pointerCount > 1) {

                    val finger1 = vec3(
                        (event.getX(0) / MainActivity.width.toFloat() - 0.5f) * 2f,
                        (event.getY(0) / MainActivity.height.toFloat() - 0.5f) * 2f,
                        0f
                    )
                    val finger2 = vec3(
                        (event.getX(1) / MainActivity.width.toFloat() - 0.5f) * 2f,
                        (event.getY(1) / MainActivity.height.toFloat() - 0.5f) * 2f,
                        0f
                    )

                    renderer.zoom += (getDistance(finger1, finger2) - prevZoomDist) * 0.01f
                }
            }
        }

        setTouchPixelPos(event!!.x, event!!.y)

        return true
    }
}
package com.reynarz.minityeditor.engine

import android.content.Context
import android.opengl.GLSurfaceView

import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import com.reynarz.minityeditor.engine.passes.RenderPass
import com.reynarz.minityeditor.engine.passes.ShadowPass
import com.reynarz.minityeditor.engine.passes.SceneMatrices
import com.reynarz.minityeditor.views.MainActivity
import kotlin.math.round
import kotlin.math.sqrt

class OpenGLView(context: Context, attributeSet: AttributeSet) :
    GLSurfaceView(context, attributeSet) {

    val sensibility = 0.75f

    companion object {
        var xPixel = 0f
        var yPixel = 0f

        fun setTouchPixelPos(x: Float, y: Float) {
            xPixel = x
            yPixel = y
        }
    }

    var renderer = OpenGLRenderer(context)

    init {
        //renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY


        setEGLContextClientVersion(2)
        setPreserveEGLContextOnPause(true)
        setRenderer(renderer)
    }



    private var startX = 0f
    private var startY = 0f

    private var unchangedStartX = 0f
    private var unchangedStartY = 0f

    private var startZoomDist = 0.0f

    fun dot(a: vec3, b: vec3): Float {
        return a.x * b.x + a.y * b.y + a.z + b.z
    }

    fun getDistance(a: vec3, b: vec3): Float {
        //--dispat
        // GlobalScope.launch(Dispatchers.) {  }
        val diff = getDiff(a, b)
        return sqrt(dot(diff, diff))
    }

    fun getDiff(a: vec3, b: vec3): vec3 {
        return vec3(b.x - a.x, b.y - a.y, b.z - a.z)
    }

    private var moved = false
    private var usingTwoFingers = false
    private var deltaTwo = vec3

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        val action = event!!.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                // prevents weird jumping
                startX = event!!.x
                startY = event!!.y

                unchangedStartX = event!!.x
                unchangedStartY = event!!.y

                renderer.twoFingersNormalizedDir = vec3()
            }

            MotionEvent.ACTION_POINTER_UP -> {
                startX = event!!.x
                startY = event!!.y

                unchangedStartX = 0f
                unchangedStartY = 0f

                renderer.twoFingersDir = vec3()
                renderer.twoFingersNormalizedDir = vec3()
                renderer.twoFingersNormalizedDirPrev = vec3()

                Log.d("two fingers", "Up")
            }

            MotionEvent.ACTION_UP -> {

//                startX = event!!.x
//                startY = event!!.y


                if (!moved)
                    renderer.addRenderCommand {
                        renderer.pickUpPass(round(startX).toInt(), round(startY).toInt())
                    }
                moved = false
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

                startZoomDist = getDistance(finger1, finger2)
            }


            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 1) {
                    val dx = event.getX(0) - startX
                    val dy = event.getY(0) - startY

                    if (!usingTwoFingers)
                        renderer.rot = vec3(renderer.rot.x + dx, renderer.rot.y + dy, 0f)

                    startX = event!!.x
                    startY = event!!.y
                    moved = true
                }

                usingTwoFingers = event.pointerCount > 1
                //--Log.d("moving", dx.toString())

                val currentPointer = vec3(event.getX(0), event.getY(0), 0f)
                val startPointerPos = vec3(unchangedStartX, unchangedStartY, 0f)
                //val dist = getDistance(currentPointer, startPointerPos)

                // Movement
                if (usingTwoFingers) {
                    var direction = getDiff(currentPointer, startPointerPos)

                    direction.x = -direction.x
                    renderer.twoFingersDir = vec3(direction.x, direction.y, 0f)
                    renderer.twoFingersNormalizedDir = direction.normalize()

                    //println("Finger1Dist: " + dist)
                    println("dir: " + direction.toString())
                }

                if (usingTwoFingers) {

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
                    moved = true
                    renderer.zoom += (getDistance(finger1, finger2) - startZoomDist) * 0.1f
                }
            }
        }

        setTouchPixelPos(event!!.x, event!!.y)

        return true
    }
}
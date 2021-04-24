package com.reynarz.minityeditor.engine

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent

class OpenGLView(context: Context, attributeSet: AttributeSet) : GLSurfaceView(context, attributeSet) {

    var renderer = OpenGlRenderer(context)

    init {
         setEGLContextClientVersion(2)
         setPreserveEGLContextOnPause(true)
         setRenderer(renderer)
        //renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    override fun onTouchEvent(e: MotionEvent?): Boolean {

        when(e!!.action) {
            MotionEvent.ACTION_DOWN -> {
                // prevents weird jumping
                prevX = e!!.x
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = e!!.x - prevX

                renderer.xRot += dx

                prevX = e!!.x
Log.d("moving", dx.toString())

            }

        }

        return true
    }

    private var prevX = 0f
    private var prevY = 0f


    fun Refresh(){
        refreshDrawableState()
    }
}
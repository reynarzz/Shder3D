package com.example.viewer3d.engine

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent

class OpenGLView(context: Context, attributeSet: AttributeSet) : GLSurfaceView(context, attributeSet) {

    var renderer = OpenGlRenderer(context)

    init {
         setEGLContextClientVersion(2)
         setPreserveEGLContextOnPause(true)
         setRenderer(renderer)
    }

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        return super.onTouchEvent(e)

    }
}
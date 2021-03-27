package com.example.viewer3d

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent

class OpenGLView(context: Context, attributeSet: AttributeSet) : GLSurfaceView(context, attributeSet) {
    init {
         setEGLContextClientVersion(2)
         setPreserveEGLContextOnPause(true)
         setRenderer(OpenGlRenderer())
    }

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        return super.onTouchEvent(e)

    }
}
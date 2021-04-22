package com.example.viewer3d.engine

import android.opengl.Matrix
import com.example.viewer3d.engine.components.Component

class Camera : Component() {

     var projectionM = FloatArray(16)
         private set

    var viewM = FloatArray(16)
         private set

    init {
        Matrix.setIdentityM(viewM, 0)
        Matrix.setIdentityM(projectionM, 0)
    }

    fun updateProjection(width : Int, height: Int) {
        Matrix.perspectiveM(projectionM,0, 45.0f, width.toFloat() / height.toFloat(), 1.0f, 1000.0f)
    }

    fun updateView() {
        Matrix.translateM(viewM, 0,0.0f,-33.0f,-225.9f)
        Matrix.rotateM(viewM, 0, 0f, 1f, 0f,0f)
    }

}
package com.example.viewer3d

import android.opengl.Matrix

class Camera {

     var projectionM = FloatArray(16)
         private set

    var viewM = FloatArray(16)
         private set

    init {
        Matrix.setIdentityM(viewM, 0)
        Matrix.setIdentityM(projectionM, 0)
    }

    fun updateProjection(width : Int, height: Int) {
        Matrix.perspectiveM(projectionM,0, 45.0f, width.toFloat() / height.toFloat(), 1.0f, 100.0f)
    }

    fun updateView() {
        Matrix.translateM(viewM, 0,0.0f,0.0f,-5.9f)
    }

}
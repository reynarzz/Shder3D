package com.example.viewer3d

import android.opengl.Matrix

class Camera {

     var projectionM = FloatArray(16)
         private set

    var viewM = FloatArray(16)
         private set

    init {

    }

    public fun updateProjection(width : Int, height: Int) {
        Matrix.setIdentityM(projectionM, 0)

        Matrix.perspectiveM(projectionM,0, 45.0f, width.toFloat() / height.toFloat(), 1.0f, 100.0f)
    }

    public fun updateView() {
        Matrix.setIdentityM(viewM, 0)
        Matrix.translateM(viewM, 0,0.0f,0.0f,-25.9f)
    }

}
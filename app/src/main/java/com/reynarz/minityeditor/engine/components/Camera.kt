package com.reynarz.minityeditor.engine.components

import android.opengl.Matrix

class Camera : Component() {

     var projectionM = FloatArray(16)
         private set

    val viewM = transform.modelM!!

    init {
        Matrix.setIdentityM(projectionM, 0)
    }

    fun updateProjection(width : Int, height: Int) {
        Matrix.perspectiveM(projectionM,0, 45.0f, width.toFloat() / height.toFloat(), 1.0f, 1000.0f)
    }
}
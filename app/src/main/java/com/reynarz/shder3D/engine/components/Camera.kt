package com.reynarz.shder3D.engine.components

import android.opengl.Matrix

class Camera : Component() {

    val projectionM = FloatArray(16)

    val projectionMInv = FloatArray(16)

    val viewM = transform.modelM!!
    val viewMInv = transform.modelMInv!!

    init {
        Matrix.setIdentityM(projectionM, 0)
        Matrix.setIdentityM(projectionMInv, 0)


        //eye = camera pos
        //center
        //Matrix.setLookAtM()
    }

    fun updateProjection(width: Int, height: Int) {
        Matrix.perspectiveM(
            projectionM,
            0,
            45.0f,
            width.toFloat() / height.toFloat(),
            1.0f,
            500.0f
        )

        Matrix.invertM(projectionMInv, 0, projectionM , 0)
    }
}
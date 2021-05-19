package com.reynarz.shder3D.engine.lights

import android.opengl.Matrix
import com.reynarz.shder3D.engine.vec3

class DirectionalLight(normalizedDirection: vec3) : Light() {

    private var lightProjectionM = FloatArray(16)
    private var lightViewProj = FloatArray(16)
    private var lightView = FloatArray(16)

    init {

        //transform.position = normalizedDirection
        // projection.
        Matrix.orthoM(lightProjectionM, 0, -70f, 70f, -70f, 70f, 1f, 100.0f)
        Matrix.setLookAtM(lightView, 0, -1f, 4f, -2f, 0f,0f,0f, 0f, 1f, 0f)
        Matrix.translateM(lightView, 0, 5f, -55f, 30f)

        Matrix.multiplyMM(lightViewProj, 0, lightProjectionM, 0, lightView, 0)

      //  Matrix.translateM(lightView, 0, 10f, 10f, 30f)
    }

    fun getLightViewMatrix(): FloatArray {
        return lightView
    }

    fun getProjectionM(): FloatArray {
        return lightProjectionM
    }

    fun getViewProjLight() : FloatArray{
        return lightViewProj
    }
}
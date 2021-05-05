package com.reynarz.minityeditor.engine.lights

import android.opengl.Matrix
import com.reynarz.minityeditor.engine.vec3

class DirectionalLight(normalizedDirection: vec3) : Light() {

    private var lightProjectionM = FloatArray(16)
    private var lightViewProj = FloatArray(16)
    private var lightView = FloatArray(16)

    init {

        transform.position = normalizedDirection
        // projection.
        Matrix.orthoM(lightProjectionM, 0, -10f, 10f, -10f, 10f, 0.1f, 17.5f)

        Matrix.multiplyMM(lightViewProj, 0, getLightViewProjectionMatrix(), 0, getLightViewMatrix(), 0)

        Matrix.setLookAtM(lightView, 0, -2f, 4f, -1f, 0f,0f,0f, 0f, 1f, 0f)

    }

    fun getLightViewMatrix(): FloatArray {
        return lightView
    }

    fun getLightViewProjectionMatrix(): FloatArray {
        return lightViewProj
    }
}
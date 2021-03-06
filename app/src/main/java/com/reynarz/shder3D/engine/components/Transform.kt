package com.reynarz.shder3D.engine.components

import android.opengl.Matrix
import com.reynarz.shder3D.engine.vec3
import kotlin.math.round

class Transform { // : Component()

    private var _position = vec3()
    private var _rotation = vec3()
    private var _scale = vec3()

    var position = vec3()
        get() {
            return _position
        }
        set(value) {
            field = value
            _position = value
            Matrix.setIdentityM(translationM, 0)
            Matrix.translateM(translationM, 0, _position.x, _position.y, _position.z)

            updateModelMatrix()
        }

    var eulerAngles = vec3()
        get() {
            return _rotation
        }
        set(value) {
            field = value
            _rotation = value

            Matrix.setIdentityM(rotationM, 0)

            if (round(_rotation.x).toInt() != 0)
                Matrix.rotateM(rotationM, 0, _rotation.x, 1f, 0f, 0f)

            if (round(_rotation.y).toInt() != 0)
                Matrix.rotateM(rotationM, 0, _rotation.y, 0f, 1f, 0f)

            if (round(_rotation.z).toInt() != 0)
                Matrix.rotateM(rotationM, 0, _rotation.z, 0f, 0f, 1f)

            updateModelMatrix()
        }

    var scale = vec3()
        get() {
            return _scale
        }
        set(value) {
            field = value
            _scale = value

            Matrix.setIdentityM(scaleM, 0)
            Matrix.scaleM(scaleM, 0, _scale.x, _scale.y, _scale.z)

            updateModelMatrix()
        }

    var modelM: FloatArray
        //private set

    var modelMInv: FloatArray? = null
        private set

    private var translationM: FloatArray? = null
    private var rotationM: FloatArray? = null
    private var scaleM: FloatArray? = null

    init {
        modelM = FloatArray(16)

        translationM = FloatArray(16)
        rotationM = FloatArray(16)
        scaleM = FloatArray(16)
        modelMInv = FloatArray(16)

        Matrix.setIdentityM(modelM, 0)

        restartTransform()
    }

    fun restartTransform() {
        Matrix.setIdentityM(translationM, 0)
        Matrix.setIdentityM(rotationM, 0)
        Matrix.setIdentityM(scaleM, 0)

        updateModelMatrix()
    }

    private fun updateModelMatrix() {
        Matrix.multiplyMM(modelM, 0, translationM, 0, rotationM, 0)
        Matrix.multiplyMM(modelM, 0, modelM, 0, scaleM, 0)
        Matrix.invertM(modelMInv, 0, modelM, 0)
    }
}
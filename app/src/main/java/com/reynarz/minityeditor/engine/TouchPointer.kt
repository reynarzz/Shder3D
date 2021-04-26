package com.reynarz.minityeditor.engine

import android.opengl.Matrix
import android.util.Log
import com.reynarz.minityeditor.engine.components.Camera
import com.reynarz.minityeditor.views.MainActivity

class TouchPointer(val camera: Camera) {

    var clipCoords : FloatArray? = null
    val normalizedVec = FloatArray(4)

    init {
        clipCoords = FloatArray(4)
    }

    fun getWorldPosRay(xPixel: Float, yPixel: Float): vec3 {
        val normalized = getNormalizedPos(xPixel, yPixel)

        normalizedVec[0] = normalized.x
        normalizedVec[1] = normalized.y
        normalizedVec[2] = 1f
        normalizedVec[3] = -1f

        Matrix.multiplyMV(clipCoords, 0, camera.projectionMInv, 0, normalizedVec, 0)
        Matrix.multiplyMV(clipCoords, 0, camera.viewMInv, 0, clipCoords, 0)

        val result = vec3(clipCoords!![0], 0f, clipCoords!![2])
        result.normalize()

        Log.d("normalized", "(${normalized.x}, ${normalized.y})")
        Log.d("Ray", "${result.x}, ${result.y}, ${result.z}")

        return result
    }

    fun getNormalizedPos(xPixel: Float, yPixel: Float): vec2 {
        val x = ((xPixel / MainActivity.width.toFloat()) - 0.5f) * 2f
        val y = ((yPixel / MainActivity.height.toFloat()) - 0.5f) * 2f

        return vec2(x, -y)
    }
}
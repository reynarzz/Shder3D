package com.reynarz.shder3D.engine.data

import android.util.Log
import com.reynarz.shder3D.models.ShaderData

class ShaderDataBase {

    var shadersIdToData: Map<String, ShaderData?>
        private set

    init {
        shadersIdToData = mapOf()
    }

    fun getShaderData(shaderId: String): ShaderData? {
        Log.d("got valid shader", "not sure")
        return shadersIdToData.getOrElse(shaderId, null!!)
    }
}
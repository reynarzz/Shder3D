package com.reynarz.minityeditor.engine.data

import com.reynarz.minityeditor.engine.Utils
import com.reynarz.minityeditor.models.MaterialData
import com.reynarz.minityeditor.models.ShaderData
import java.util.*

class DataFactory {

    companion object {
        lateinit var instance: DataFactory
            private set
    }

    fun getNewShaderData(shaderName: String): ShaderData {
        val shader = ShaderData(shaderName, UUID.randomUUID().toString())

        val unlit = Utils.getUnlitShader()

        shader.vertexShader = unlit.first
        shader.fragmentShader = unlit.second

        return shader
    }

    fun getNewMaterialData(materialName: String): MaterialData {
        val materialData = MaterialData(UUID.randomUUID().toString(), getNewShaderData("shader"))
        materialData.name = materialName

        return materialData
    }
}
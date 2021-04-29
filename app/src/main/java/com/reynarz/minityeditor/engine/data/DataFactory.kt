package com.reynarz.minityeditor.engine.data

import com.reynarz.minityeditor.models.MaterialData
import com.reynarz.minityeditor.models.ShaderData
import java.util.*

class DataFactory {
    object Singleton {

    }

    companion object {
        lateinit var instance: DataFactory
            private set
    }

    fun getNewShaderData(shaderName: String): ShaderData {
        return ShaderData(shaderName, UUID.randomUUID().toString())
    }

    fun getNewMaterialData(materialName: String): MaterialData {
        val materialData = MaterialData(UUID.randomUUID().toString())
        materialData.name = materialName

        return materialData
    }
}
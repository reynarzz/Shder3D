package com.reynarz.minityeditor.engine.data

import com.reynarz.minityeditor.engine.ModelData
import com.reynarz.minityeditor.engine.CustomObjParser

class ModelsDataBase {
    private val parser = CustomObjParser()

    fun getModels(modelPath: String): List<ModelData> {
        // return ObjParser(modelPath).getModelData()

        return parser.getModels(modelPath)
    }
}
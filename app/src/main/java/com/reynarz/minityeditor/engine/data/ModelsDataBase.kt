package com.reynarz.minityeditor.engine.data

import com.reynarz.minityeditor.engine.ModelData
import com.reynarz.minityeditor.engine.ObjParser

class ModelsDataBase {
    var modelsMap : Map<String, ModelData>

    init {
        modelsMap = mapOf()

    }

    fun getModel(modelPath: String) : ModelData {
        return ObjParser(modelPath).getModelData()
    }
}
package com.reynarz.minityeditor.engine.data

import com.reynarz.minityeditor.engine.ModelData
import com.reynarz.minityeditor.engine.CustomObjParser

class ModelsDataBase {
    var modelsMap : Map<String, ModelData>

    init {
        modelsMap = mapOf()

    }

    fun getModels(modelPath: String) : List<ModelData> {
      // return ObjParser(modelPath).getModelData()

        return CustomObjParser().getModels(modelPath)
    }
}
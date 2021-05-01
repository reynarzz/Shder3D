package com.reynarz.minityeditor.engine.data

import com.reynarz.minityeditor.engine.ModelData
import com.reynarz.minityeditor.engine.ObjParser
import com.reynarz.minityeditor.files.CustomObjParser

class ModelsDataBase {
    var modelsMap : Map<String, ModelData>

    init {
        modelsMap = mapOf()

    }

    fun getModel(modelPath: String) : ModelData {
      // return ObjParser(modelPath).getModelData()

        return CustomObjParser().getModel(modelPath)
    }
}
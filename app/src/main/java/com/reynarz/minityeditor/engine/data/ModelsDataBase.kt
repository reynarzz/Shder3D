package com.reynarz.minityeditor.engine.data

import com.reynarz.minityeditor.engine.ModelData

class ModelsDataBase {
    var modelsMap : Map<String, ModelData>

    init {
        modelsMap = mapOf()

    }

    fun getModel(modelDataId: String) : ModelData {
        return
    }
}
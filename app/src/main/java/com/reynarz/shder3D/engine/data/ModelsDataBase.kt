package com.reynarz.shder3D.engine.data

import com.reynarz.shder3D.engine.ModelData
import com.reynarz.shder3D.engine.CustomObjParser

class ModelsDataBase {
    private val parser = CustomObjParser()

    fun getModels(modelPath: String): List<ModelData> {
        // return ObjParser(modelPath).getModelData()

        return parser.getModels(modelPath)
    }
}
package com.reynarz.minityeditor.engine

data class ModelData(
    var mVertices: FloatArray,
    var mNormals: FloatArray,
    var mUVs: FloatArray,
    var mIndices: IntArray,
    var bounds: Bounds
){
    /*

    var mAmbientColour: FloatArray,
    var mDiffuseColour: FloatArray,
    var mSpecularColour: FloatArray,
    var mSpecularExponent: FloatArray,
    var mDissolve: FloatArray,*/
    var materialName = ""
    var modelName = ""
}
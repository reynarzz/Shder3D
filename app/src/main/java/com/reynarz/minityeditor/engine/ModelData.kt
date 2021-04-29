package com.reynarz.minityeditor.engine

data class ModelData(
    var mVertices: FloatArray,
    var mNormals: FloatArray,
    var mUVs: FloatArray,
    var mIndices: IntArray,
    var mAmbientColour: FloatArray,
    var mDiffuseColour: FloatArray,
    var mSpecularColour: FloatArray,
    var mSpecularExponent: FloatArray,
    var mDissolve: FloatArray,
    var bounds: Bounds
)
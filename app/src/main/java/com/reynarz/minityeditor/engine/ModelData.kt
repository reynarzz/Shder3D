package com.reynarz.minityeditor.engine

data class ModelData(
    val mVertices: FloatArray,
    val mNormals: FloatArray,
    val mUVs: FloatArray,
    val mIndices: IntArray,
    val mAmbientColour: FloatArray,
    val mDiffuseColour: FloatArray,
    val mSpecularColour: FloatArray,
    val mSpecularExponent: FloatArray,
    val mDissolve: FloatArray,
    val bounds: Bounds
) {
}
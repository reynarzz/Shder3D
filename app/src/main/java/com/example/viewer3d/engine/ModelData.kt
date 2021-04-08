package com.example.viewer3d.engine

data class ModelData(val mVertices: FloatArray,
                     val mNormals: FloatArray,
                     val mUVs: FloatArray,
                     val mIndices: IntArray,
                     val mAmbientColour: FloatArray,
                     val mDiffuseColour: FloatArray,
                     val mSpecularColour: FloatArray,
                     val mSpecularExponent: FloatArray,
                     val mDissolve: FloatArray) {
}
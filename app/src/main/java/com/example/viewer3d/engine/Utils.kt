package com.example.viewer3d.engine

 class Utils {
     companion object {
         fun getScreenSizeQuad() : Mesh{
             var screenQuadVerts: FloatArray = listOf(-1f, -1f, 0f,
                     -1f, 1f,0f,
                     1f, 1f,0f,
                     1f, -1f,0f).toFloatArray()

             var screenQuadIndex = listOf(0, 1, 2,
                     0, 2, 3).toIntArray()

             val screenQuadUV = listOf(0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f).toFloatArray()

             return Mesh(screenQuadVerts, screenQuadIndex, screenQuadUV)
         }
     }

}
package com.example.viewer3d.engine

import android.content.Context
import android.content.res.AssetManager
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.InputStreamReader

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

     class ShaderUtils {
         companion object {
             fun processInclude( include: String, shaderCode : String) : String {

                 var shader = shaderCode

                 shaderCode.reader().forEachLine {
                     if(!it.contains("//") && it.contains("#include")){

                         var lowerCase = it.toLowerCase()

                         if(lowerCase.contains("unity.h")){

                             shader = shaderCode.replace(it, include)
                         }
                     }
                 }

                 return shader;
             }
         }
     }
}
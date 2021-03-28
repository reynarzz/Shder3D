package com.example.viewer3d

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

class MyObjParser(context: Context, file: String) {

    private val mFinalVertices = mutableListOf<Float>()
    private val mFinalNormals = mutableListOf<Float>()
    private val mFinalUVs = mutableListOf<Float>()
    private val mFinalIndices = mutableListOf<Int>()

    /**
     * Init - parses the file in assets that matches the file string passed.
     */
    init {
        val inStream = context.assets.open(file)
        val reader = BufferedReader(InputStreamReader(inStream))

        val vertices = ArrayList<Triple<Float, Float, Float>>()
        val normals = ArrayList<Triple<Float, Float, Float>>()
        val uvs = ArrayList<Pair<Float, Float>>()

        val faceMap = HashMap<Triple<Int, Int?, Int?>, Int>()
        var nextIndex: Int = 0

        reader.forEachLine {

            if(it.startsWith("v "))
            {
                var splited = it.split(" ").toMutableList()

                splited.retainAll(){ it.toFloatOrNull() != null}

                mFinalVertices.add(splited[0].toFloat())
                mFinalVertices.add(splited[1].toFloat())
                mFinalVertices.add(splited[2].toFloat())
            }

            if(it.startsWith("f ")) {
                var splited = it.split(" ").toMutableList()
                splited.removeAll{ it.isEmpty() }
                var faceIndex = mutableListOf<Int>()

                for (i in 0 until splited.size - 1)
                {
                    var number = ""

                    for (c in splited[i+1]){
                        if(c != '/')
                        number += c
                        else
                            break
                    }

                    faceIndex.add(number.toInt()-1)
                }

                for (i in 0 until  faceIndex.size-1){
                    mFinalIndices.add(faceIndex[0])
                    mFinalIndices.add(faceIndex[i])
                    mFinalIndices.add(faceIndex[i+1])
                }
            }
        }
    }

    /**
     * returns the Model data which has been parsed
     */
    fun getModelData(): ModelData {
        return ModelData(mFinalVertices.toFloatArray(),
                mFinalNormals.toFloatArray(),
                mFinalUVs.toFloatArray(),
                mFinalIndices.toIntArray(),
                emptyArray<Float>().toFloatArray(),
                emptyArray<Float>().toFloatArray(),
                emptyArray<Float>().toFloatArray(),
                emptyArray<Float>().toFloatArray(),
                emptyArray<Float>().toFloatArray())
    }
}
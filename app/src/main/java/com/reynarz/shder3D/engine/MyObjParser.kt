package com.reynarz.shder3D.engine

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

class MyObjParser(context: Context, file: String) {

    private val mFinalVertices = mutableListOf<Float>()
    private val mFinalNormals = mutableListOf<Float>()
    private val finalUVs = mutableListOf<Float>()
    private val finalIndices = mutableListOf<Int>()

    /**
     * Init - parses the file in assets that matches the file string passed.
     */
    init {
        val inStream = context.assets.open(file)
        val reader = BufferedReader(InputStreamReader(inStream))

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

                for (i in 1 until  faceIndex.size-1){
                    finalIndices.add(faceIndex[0])
                    finalIndices.add(faceIndex[i])
                    finalIndices.add(faceIndex[i+1])
                }
            }

            if ( it.startsWith( "vt" ) ) {

                val split = it.split( " " )

                // split[0] is the prefix
                val uv = Pair(split[1].toFloat(), split[2].toFloat())
                finalUVs.add(split[1].toFloat())
                finalUVs.add(split[2].toFloat())
            }

//            if(it.startsWith("vt ")) {
//                var splited = it.split(" ").toMutableList()
//
//                splited.retainAll(){ it.toFloatOrNull() != null}
//
//                finalUVs.add(splited[0].toFloat())
//                finalUVs.add(splited[1].toFloat())
//
//            }
        }
    }

    /**
     * returns the Model data which has been parsed
     */
//    fun getModelData(): ModelData {
//        return ModelData(mFinalVertices.toFloatArray(),
//                mFinalNormals.toFloatArray(),
//                finalUVs.toFloatArray(),
//                finalIndices.toIntArray(),
//                emptyArray<Float>().toFloatArray(),
//                emptyArray<Float>().toFloatArray(),
//                emptyArray<Float>().toFloatArray(),
//                emptyArray<Float>().toFloatArray(),
//                emptyArray<Float>().toFloatArray())
//    }

//    fun getMeshes(path: String) : List<Mesh> {
//
//        val vertices = mutableListOf<Float>()
//        val index = mutableListOf<Int>()
//        val tempIndex = mutableListOf<Int>()
//        val uv = mutableListOf<Float>()
//        val meshes = mutableListOf<Mesh>()
//
//        // Open a stream to your OBJ resource
//        val inStream = context.assets.open(path)
//        BufferedReader(InputStreamReader(inStream)).use {
//            val parser: IOBJParser = OBJParser()
//            val model: OBJModel = parser.parse(it)
//
//            for (mObj in model.objects) {
//
//                for (mesh in mObj.meshes) {
//                    var intIndex = 0
//                    vertices.clear()
//                    index.clear()
//                    uv.clear()
//                    for (face in mesh.faces) {
//                        tempIndex.clear()
//
//                        for (reference in face.references) {
//                            val vertex = model.getVertex(reference)
//
//                            vertices.add(vertex.x)
//                            vertices.add(vertex.y)
//                            vertices.add(vertex.z)
//
//                            if(reference.hasVertexIndex()){
//                                tempIndex.add(reference.vertexIndex)
//                            }
//
//                            if (reference.hasNormalIndex()) {
//                                val normal = model.getNormal(reference)
//                            }
//
//                            if (reference.hasTexCoordIndex()) {
//                                val texcoord = model.getTexCoord(reference)
//                                uv.add(texcoord.u)
//                                uv.add(texcoord.v)
//                            }
//                        }
//
//                        for (i in 1 until  tempIndex.size-1){
//                            index.add(tempIndex[0])
//                            index.add(tempIndex[i])
//                            index.add(tempIndex[i + 1])
//                        }
//                    }
//                    meshes.add(Mesh(vertices.toFloatArray(), index.toIntArray(), uv.toFloatArray()))
//                }
//            }
//        }
//
//        return meshes
//    }
}
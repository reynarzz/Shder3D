package com.reynarz.minityeditor.engine

import android.content.Context
import com.reynarz.minityeditor.files.FileManager
import java.io.BufferedReader
import java.io.InputStreamReader



class ObjParser {

    private val mFinalVertices = mutableListOf<Float>()
    private val mFinalNormals = mutableListOf<Float>()
    private val mFinalUVs = mutableListOf<Float>()
    private val mFinalIndices = mutableListOf<Int>()

    var minX = 0f
    var minY = 0f
    var minZ = 0f

    var maxX = 0f
    var maxY = 0f
    var maxZ = 0f

    var bounds: Bounds? = null

    /**
     * Init - parses the file in assets that matches the file string passed.
     */
    constructor(context: Context, file: String) {
        val inStream = context.assets.open(file)
        val reader = BufferedReader(InputStreamReader(inStream))

        parseObj(reader)
    }

    constructor(fullPath: String) {
        val reader = FileManager().readFile(fullPath)

        parseObj(reader)
    }

    fun calcBoundingBox(x: Float, y: Float, z: Float) {
        if (x < minX) {
            minX = x
        }
        if (x > maxX) {
            maxX = x
        }


        if (y < minY) {
            minY = y
        }
        if (y > maxY) {
            maxY = y
        }


        if (z < minZ) {
            minZ = z
        }
        if (z > maxZ) {
            maxZ = z
        }
    }

    fun parseObj(reader: BufferedReader) {

        val vertices = ArrayList<Triple<Float, Float, Float>>()
        val normals = ArrayList<Triple<Float, Float, Float>>()
        val uvs = ArrayList<Pair<Float, Float>>()

        val faceMap = HashMap<Triple<Int, Int?, Int?>, Int>()
        var nextIndex: Int = 0



        reader.forEachLine { line ->

            if (line.startsWith("v ")) {

                val split = line.split(" ")

                // split[0] is the prefix
                val vertex = Triple(split[1].toFloat(), split[2].toFloat(), split[3].toFloat())

                calcBoundingBox(split[1].toFloat(), split[2].toFloat(), split[3].toFloat())

                vertices.add(vertex)
            } else if (line.startsWith("vn")) {

                val split = line.split(" ")

                // split[0] is the prefix
                val normal = Triple(split[1].toFloat(), split[2].toFloat(), split[3].toFloat())
                normals.add(normal)
            } else if (line.startsWith("vt")) {

                val split = line.split(" ")

                // split[0] is the prefix
                val uv = Pair(split[1].toFloat(), split[2].toFloat())
                uvs.add(uv)
            } else if (line.startsWith("f ")) {

                // perform the first split
                val split = line.split(" ")

                // get the first vertex, uv and normal
                val vun1 = split[1].split("/")
                val point1 = Triple(vun1[0].toInt(), vun1[1].toIntOrNull(), vun1[2].toIntOrNull())

                val pointInFaceMap1 = faceMap.get(point1)

                if (null != pointInFaceMap1) {

                    mFinalIndices.add(pointInFaceMap1)

                } else {

                    // add this sequence to the map
                    faceMap.put(point1, nextIndex)

                    // add vertex data
                    mFinalVertices.add(vertices[point1.first - 1].first)
                    mFinalVertices.add(vertices[point1.first - 1].second)
                    mFinalVertices.add(vertices[point1.first - 1].third)

                    // add uvs if present
                    if (null != point1.second) {
                        mFinalUVs.add(uvs[point1.second!! - 1].first)
                        mFinalUVs.add(uvs[point1.second!! - 1].second)
                    }

                    // add normals if present
                    if (null != point1.third) {
                        mFinalNormals.add(normals[point1.third!! - 1].first)
                        mFinalNormals.add(normals[point1.third!! - 1].second)
                        mFinalNormals.add(normals[point1.third!! - 1].third)
                    }

                    // and finally add the index
                    mFinalIndices.add(nextIndex++)
                }

                // get the second vertex, uv and normal
                val vun2 = split[2].split("/")
                val point2 = Triple(vun2[0].toInt(), vun2[1].toIntOrNull(), vun2[2].toIntOrNull())
                val pointInFaceMap2 = faceMap.get(point2)

                if (null != pointInFaceMap2) {

                    mFinalIndices.add(pointInFaceMap2)

                } else {

                    // add this sequence to the map
                    faceMap.put(point2, nextIndex)

                    // add vertex data
                    mFinalVertices.add(vertices[point2.first - 1].first)
                    mFinalVertices.add(vertices[point2.first - 1].second)
                    mFinalVertices.add(vertices[point2.first - 1].third)

                    // add uvs if present
                    if (null != point2.second) {
                        mFinalUVs.add(uvs[point2.second!! - 1].first)
                        mFinalUVs.add(uvs[point2.second!! - 1].second)
                    }

                    // add normals if present
                    if (null != point2.third) {
                        mFinalNormals.add(normals[point2.third!! - 1].first)
                        mFinalNormals.add(normals[point2.third!! - 1].second)
                        mFinalNormals.add(normals[point2.third!! - 1].third)
                    }

                    // and finally add the index
                    mFinalIndices.add(nextIndex++)
                }

                // get the third vertex, uv and normal
                val vun3 = split[3].split("/")
                val point3 = Triple(vun3[0].toInt(), vun3[1].toIntOrNull(), vun3[2].toIntOrNull())
                val pointInFaceMap3 = faceMap.get(point3)

                if (null != pointInFaceMap3) {

                    mFinalIndices.add(pointInFaceMap3)

                } else {

                    // add this sequence to the map
                    faceMap.put(point3, nextIndex)

                    // add vertex data
                    mFinalVertices.add(vertices[point3.first - 1].first)
                    mFinalVertices.add(vertices[point3.first - 1].second)
                    mFinalVertices.add(vertices[point3.first - 1].third)

                    // add uvs if present
                    if (null != point3.second) {
                        mFinalUVs.add(uvs[point3.second!! - 1].first)
                        mFinalUVs.add(uvs[point3.second!! - 1].second)
                    }

                    // add normals if present
                    if (null != point3.third) {
                        mFinalNormals.add(normals[point3.third!! - 1].first)
                        mFinalNormals.add(normals[point3.third!! - 1].second)
                        mFinalNormals.add(normals[point3.third!! - 1].third)
                    }

                    // and finally add the index
                    mFinalIndices.add(nextIndex++)
                }
            }
        }

        bounds = Bounds(minX, minY, minZ, maxX, maxY, maxZ)
    }

    fun getModelData(): ModelData {
        return ModelData(
            mFinalVertices.toFloatArray(),
            mFinalNormals.toFloatArray(),
            mFinalUVs.toFloatArray(),
            mFinalIndices.toIntArray(),
            emptyArray<Float>().toFloatArray(),
            emptyArray<Float>().toFloatArray(),
            emptyArray<Float>().toFloatArray(),
            emptyArray<Float>().toFloatArray(),
            emptyArray<Float>().toFloatArray(),
            bounds!!
        )
    }
}
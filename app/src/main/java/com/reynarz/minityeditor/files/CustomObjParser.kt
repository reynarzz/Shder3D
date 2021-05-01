package com.reynarz.minityeditor.files

import android.util.Log
import com.reynarz.minityeditor.engine.Bounds
import com.reynarz.minityeditor.engine.ModelData
import com.reynarz.minityeditor.engine.vec2
import com.reynarz.minityeditor.engine.vec3
import java.io.InputStreamReader

class CustomObjParser {

    class Vertex {
        var position = vec3()
        var uv = vec2()
        var normal = vec3()
        var indice = 0
    }

    val fullVerticesData = mutableListOf<Vertex>()

    fun getModel(modelPath: String): ModelData {

        val verticesFinal = mutableListOf<Float>()
        val normalsFinal = mutableListOf<Float>()
        val uvFinal = mutableListOf<Float>()
        val indicesFinal = mutableListOf<Int>()

        val vPositions = mutableListOf<vec3>()
        val normals = mutableListOf<vec3>()
        val uv = mutableListOf<vec2>()
        val indices = mutableListOf<Int>()


        val modelFile = FileManager().readFileText(modelPath)

        val reader = InputStreamReader(modelFile.byteInputStream())

       // Log.d("Total lines", modelFile)

        var indice = 0

        for (it in reader.readLines()) {

            if (it.startsWith("v ")) {

                var splitted = it.split(" ")
                val position = vec3(splitted[1].toFloat(), splitted[2].toFloat(), splitted[3].toFloat())

                Log.d("position", "(${position.x}, ${position.y}, ${position.z})")
                vPositions.add(position)

            }

            if (it.startsWith("vt ")) {
                var splitted = it.split(" ")

                val uvCoord = vec2(splitted[1].toFloat(), splitted[2].toFloat())
                Log.d("UV", "(${uvCoord.x}, ${uvCoord.y})")

                uv.add(uvCoord)
            }

            if (it.startsWith("vn ")) {
                var splitted = it.split(" ")
                val normal = vec3(splitted[1].toFloat(), splitted[2].toFloat(), splitted[3].toFloat())

                Log.d("normals", "(${normal.x}, ${normal.y}, ${normal.z})")

                normals.add(normal)
            }

            if (it.startsWith("f ")) {

                val split1 = it.split(" ").toMutableList()
                split1.removeAt(0)

                for (v1Content in split1) {

                    var value = v1Content.split("/")

                    val vertexIndex = value[0].toInt()-1
                    val uvIndex = value[1].toInt()-1

                    Log.d("Indexes ${value.size}", "vertex: ${vertexIndex+1}, uv: ${uvIndex+1}")
                    val vertex = Vertex()
                    vertex.position = vPositions[vertexIndex]
                    vertex.uv = uv[uvIndex]

                    vertex.indice = indice++

                    if (value.size > 2) {
                        val normalIndex = value[2].toInt()
                        vertex.normal = normals[normalIndex-1]
                    }

                    fullVerticesData.add(vertex)
                }

                //Log.d("position", "(${normal.x}, ${normal.y}, ${normal.z})")

            }
        }

        for (vertex in fullVerticesData) {
            verticesFinal.add(-vertex.position.x)
            verticesFinal.add(vertex.position.y)
            verticesFinal.add(-vertex.position.z)

            uvFinal.add(vertex.uv.x)
            uvFinal.add(vertex.uv.y)

            //normals


            //indices
            indices.add(vertex.indice)

        }
        return ModelData(verticesFinal.toFloatArray(), normalsFinal.toFloatArray(), uvFinal.toFloatArray(), indices.toIntArray(), Bounds(0f, 0f, 0f, 0f, 0f, 0f))
    }
}
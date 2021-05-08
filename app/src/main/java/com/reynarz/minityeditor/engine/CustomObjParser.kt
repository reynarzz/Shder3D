package com.reynarz.minityeditor.engine

import com.reynarz.minityeditor.files.FileManager

class CustomObjParser {

    var minX = 0f
    var minY = 0f
    var minZ = 0f

    var maxX = 0f
    var maxY = 0f
    var maxZ = 0f

    var bounds: Bounds? = null

    class Vertex {
        var materialName = ""
        var position = vec3()
        var uv = vec2()
        var normal = vec3()
        var index = 0
    }

    val fullVerticesData = mutableListOf<MutableList<Vertex>>()

    fun getModels(modelPath: String): List<ModelData> {



        val vPositions = mutableListOf<vec3>()
        val normals = mutableListOf<vec3>()
        val uv = mutableListOf<vec2>()
        val indices = mutableListOf<Int>()
        val currentVertices = mutableListOf<Vertex>()
        val triangulatedVertex = mutableListOf<Vertex>()

        val modelFile = FileManager().readFile(modelPath)

        // Log.d("Total lines", modelFile)

        var indice = 0
        var currentMatName = ""

        for (it in modelFile.readLines()) {

            if (it.startsWith("v ")) {

                var splitted = it.split(" ")//.toMutableList()
                // splitted.retainAll(){ it.toFloatOrNull() != null}

                val position = vec3(splitted[1].toFloat(), splitted[2].toFloat(), splitted[3].toFloat())

                calcBoundingBox(position.x, position.y, position.z)
                //Log.d("position", "(${position.x}, ${position.y}, ${position.z})")
                vPositions.add(position)

            }

            if (it.startsWith("vt ")) {
                var splitted = it.split(" ")

                val uvCoord = vec2(splitted[1].toFloat(), splitted[2].toFloat())
                //Log.d("UV", "(${uvCoord.x}, ${uvCoord.y})")

                uv.add(uvCoord)
            }

            if (it.startsWith("vn ")) {
                var splitted = it.split(" ")
                val normal = vec3(splitted[1].toFloat(), splitted[2].toFloat(), splitted[3].toFloat())

                //Log.d("normals", "(${normal.x}, ${normal.y}, ${normal.z})")

                normals.add(normal)
            }

            if (it.startsWith("usemtl ")) {
                val matName = it.split(" ")[1]

                if (currentMatName != matName) {
                    currentMatName = matName

                    fullVerticesData.add(mutableListOf())
                }
            }

            if (it.startsWith("f ")) {

                val faces = it.split(" ").toMutableList()
                faces.removeAt(0)
                currentVertices.clear()
                triangulatedVertex.clear()

                for (face in faces) {

                    var value = face.split("/")

                    val vertexIndex = value[0].toInt() - 1
                    val uvIndex = value[1].toInt() - 1

                    //Log.d("Indexes ${value.size}", "vertex: ${vertexIndex + 1}, uv: ${uvIndex + 1}")

                    val vertex = Vertex()

                    vertex.position = vPositions[vertexIndex]
                    vertex.uv = uv[uvIndex]

                    vertex.index = indice++
                    vertex.materialName = currentMatName

                    if (value.size > 2) {
                        val normalIndex = value[2].toInt() - 1
                        vertex.normal = normals[normalIndex]
                    }

                    //currentVertices.add(vertex)
                    fullVerticesData[fullVerticesData.size - 1].add(vertex)
                }

//                if (currentVertices.size == 4) {
//                    val b1 = vec3().getDistance(currentVertices[0].position, currentVertices[1].position)
//                    val h1 = vec3().getDistance(currentVertices[1].position, currentVertices[2].position)
//
//                    val b2 = vec3().getDistance(currentVertices[0].position, currentVertices[2].position)
//                    val h2 = vec3().getDistance(currentVertices[2].position, currentVertices[3].position)
//
//                    val area1 = (b1 * h1) / 2f
//                    val area2 = (b2 * h2) / 2f
//
//                    if (area1 < area2) {
//                        triangulatedVertex.add(currentVertices[0])
//                        triangulatedVertex.add(currentVertices[1])
//                        triangulatedVertex.add(currentVertices[2])
//
//                        triangulatedVertex.add(currentVertices[0])
//                        triangulatedVertex.add(currentVertices[2])
//                        triangulatedVertex.add(currentVertices[3])
//                    } else {
//                        triangulatedVertex.add(currentVertices[3])
//                        triangulatedVertex.add(currentVertices[1])
//                        triangulatedVertex.add(currentVertices[0])
//
//                        triangulatedVertex.add(currentVertices[3])
//                        triangulatedVertex.add(currentVertices[2])
//                        triangulatedVertex.add(currentVertices[1])
//                    }
//
//                    for (v in triangulatedVertex) {
//                        fullVerticesData.add(v)
//                    }
//
//                } else {
//                    for (v in currentVertices) {
//                verticesGroup.add(v)
//                    }
//                }

                //Log.d("position", "(${normal.x}, ${normal.y}, ${normal.z})")
            }
        }

        val models = mutableListOf<ModelData>()
        // triangulate here!
        for (verticesGroup in fullVerticesData) {

            val verticesFinal = mutableListOf<Float>()
            val normalsFinal = mutableListOf<Float>()
            val uvFinal = mutableListOf<Float>()
            //val indicesFinal = mutableListOf<Int>()

            for (vertex in verticesGroup) {
                verticesFinal.add(vertex.position.x)
                verticesFinal.add(vertex.position.y)
                verticesFinal.add(vertex.position.z)

                uvFinal.add(vertex.uv.x)
                uvFinal.add(vertex.uv.y)

                //normals
                normalsFinal.add(vertex.normal.x)
                normalsFinal.add(vertex.normal.y)
                normalsFinal.add(vertex.normal.z)

                //indices
                indices.add(vertex.index)
            }

            models.add(ModelData(verticesFinal.toFloatArray(), normalsFinal.toFloatArray(), uvFinal.toFloatArray(), indices.toIntArray(), Bounds(minX, minY, minZ, maxX, maxY, maxZ)).also {
                it.materialName = verticesGroup[0].materialName
            })
        }

        println("""${modelPath}: ${models.size}""")
        return models
    }

//    fun getModel(modelPath: String): ModelData {
//
//        val verticesFinal = mutableListOf<Float>()
//        val normalsFinal = mutableListOf<Float>()
//        val uvFinal = mutableListOf<Float>()
//        val indicesFinal = mutableListOf<Int>()
//
//        val vPositions = mutableListOf<vec3>()
//        val normals = mutableListOf<vec3>()
//        val uv = mutableListOf<vec2>()
//        val indices = mutableListOf<Int>()
//        val currentVertices = mutableListOf<Vertex>()
//        val triangulatedVertex = mutableListOf<Vertex>()
//
//        val modelFile = FileManager().readFile(modelPath)
//
//        // Log.d("Total lines", modelFile)
//
//        var indice = 0
//
//        for (it in modelFile.readLines()) {
//
//            if (it.startsWith("v ")) {
//
//                var splitted = it.split(" ")//.toMutableList()
//                // splitted.retainAll(){ it.toFloatOrNull() != null}
//
//                val position = vec3(splitted[1].toFloat(), splitted[2].toFloat(), splitted[3].toFloat())
//
//                calcBoundingBox(position.x, position.y, position.z)
//                //Log.d("position", "(${position.x}, ${position.y}, ${position.z})")
//                vPositions.add(position)
//
//            }
//
//            if (it.startsWith("vt ")) {
//                var splitted = it.split(" ")
//
//                val uvCoord = vec2(splitted[1].toFloat(), splitted[2].toFloat())
//                //Log.d("UV", "(${uvCoord.x}, ${uvCoord.y})")
//
//                uv.add(uvCoord)
//            }
//
//            if (it.startsWith("vn ")) {
//                var splitted = it.split(" ")
//                val normal = vec3(splitted[1].toFloat(), splitted[2].toFloat(), splitted[3].toFloat())
//
//                //Log.d("normals", "(${normal.x}, ${normal.y}, ${normal.z})")
//
//                normals.add(normal)
//            }
//
//            if (it.startsWith("f ")) {
//
//                val faces = it.split(" ").toMutableList()
//                faces.removeAt(0)
//                currentVertices.clear()
//                triangulatedVertex.clear()
//
//                for (face in faces) {
//
//                    var value = face.split("/")
//
//                    val vertexIndex = value[0].toInt() - 1
//                    val uvIndex = value[1].toInt() - 1
//
//                    //Log.d("Indexes ${value.size}", "vertex: ${vertexIndex + 1}, uv: ${uvIndex + 1}")
//
//                    val vertex = Vertex()
//
//                    vertex.position = vPositions[vertexIndex]
//                    vertex.uv = uv[uvIndex]
//
//                    vertex.index = indice++
//
//                    if (value.size > 2) {
//                        val normalIndex = value[2].toInt() - 1
//                        vertex.normal = normals[normalIndex]
//                    }
//
//                    currentVertices.add(vertex)
//                    //fullVerticesData.add(vertex)
//                }
//
//                if (currentVertices.size == 4) {
//                    val b1 = vec3().getDistance(currentVertices[0].position, currentVertices[1].position)
//                    val h1 = vec3().getDistance(currentVertices[1].position, currentVertices[2].position)
//
//                    val b2 = vec3().getDistance(currentVertices[0].position, currentVertices[2].position)
//                    val h2 = vec3().getDistance(currentVertices[2].position, currentVertices[3].position)
//
//                    val area1 = (b1 * h1) / 2f
//                    val area2 = (b2 * h2) / 2f
//
//                    if (area1 < area2) {
//                        triangulatedVertex.add(currentVertices[0])
//                        triangulatedVertex.add(currentVertices[1])
//                        triangulatedVertex.add(currentVertices[2])
//
//                        triangulatedVertex.add(currentVertices[0])
//                        triangulatedVertex.add(currentVertices[2])
//                        triangulatedVertex.add(currentVertices[3])
//                    } else {
//                        triangulatedVertex.add(currentVertices[3])
//                        triangulatedVertex.add(currentVertices[1])
//                        triangulatedVertex.add(currentVertices[0])
//
//                        triangulatedVertex.add(currentVertices[3])
//                        triangulatedVertex.add(currentVertices[2])
//                        triangulatedVertex.add(currentVertices[1])
//                    }
//
//                    for (v in triangulatedVertex) {
//                        fullVerticesData.add(v)
//                    }
//
//                } else {
//                    for (v in currentVertices) {
//                        fullVerticesData.add(v)
//                    }
//                }
//
//                //Log.d("position", "(${normal.x}, ${normal.y}, ${normal.z})")
//            }
//        }
//
//        // triangulate here!
//        for (vertex in fullVerticesData) {
//            verticesFinal.add(vertex.position.x)
//            verticesFinal.add(vertex.position.y)
//            verticesFinal.add(vertex.position.z)
//
//            uvFinal.add(vertex.uv.x)
//            uvFinal.add(vertex.uv.y)
//
//            //normals
//            normalsFinal.add(vertex.normal.x)
//            normalsFinal.add(vertex.normal.y)
//            normalsFinal.add(vertex.normal.z)
//
//            //indices
//            indices.add(vertex.index)
//        }
//
//        return ModelData(verticesFinal.toFloatArray(), normalsFinal.toFloatArray(), uvFinal.toFloatArray(), indices.toIntArray(), Bounds(minX, minY, minZ, maxX, maxY, maxZ))
//    }

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
}
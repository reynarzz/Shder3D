package com.reynarz.minityeditor.engine

class Bounds(
    val minX: Float,
    val minY: Float,
    val minZ: Float,
    val maxX: Float,
    val maxY: Float,
    val maxZ: Float
) {

    val min = vec3(minX, minY, minZ)
    var verts: FloatArray? = null
    var indices: IntArray? = null

    init {
        val verts = mutableListOf(

            minX, minY, maxZ,
            minX, minY, minZ,
            maxX, minY, minZ,
            maxX, minY, maxZ,

            minX, maxY, maxZ,
            minX, maxY, minZ,
            maxX, maxY, minZ,
            maxX, maxY, maxZ,
        )

        val indices = mutableListOf(
            0, 1,
            1, 2,
            2, 3,
            3, 0,

            4, 5,
            5, 6,
            6, 7,
            7, 4,

            0, 4,
            1, 5,
            6, 2,
            7, 3
        )

        this.verts = verts.toFloatArray()
        this.indices = indices.toIntArray()
    }
}
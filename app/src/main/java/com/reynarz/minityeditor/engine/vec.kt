package com.reynarz.minityeditor.engine

import kotlinx.serialization.Serializable
import kotlin.math.sqrt

@Serializable
class vec4 {
    var x: Float = 0f
    var y: Float = 0f
    var z: Float = 0f
    var w: Float = 0f

    constructor(x: Float, y: Float, z: Float, w: Float) {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
    }

    constructor() {

    }
}

@Serializable
class vec3 {

    var x: Float = 0f
    var y: Float = 0f
    var z: Float = 0f

    constructor(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    constructor(def: Float) {
        this.x = def
        this.y = def
        this.z = def
    }

    constructor() {

    }

    fun normalize(): vec3 {
        val dist = getDistance(vec3(), this)
        x /= dist
        y /= dist
        z /= dist

        return this
    }

    fun dot(a: vec3, b: vec3): Float {
        return a.x * b.x + a.y * b.y + a.z + b.z
    }

    fun getDistance(a: vec3, b: vec3): Float {
        val diff = vec3(b.x - a.x, b.y - a.y, b.z - a.z)

        return sqrt(dot(diff, diff))
    }

    override fun toString(): String {
        return "(${x}, ${y}, ${z})"
    }
}

@Serializable
class vec2 {

    var x: Float = 0f
    var y: Float = 0f


    constructor(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    constructor() {
        this.x = 0f
        this.y = 0f
    }

    constructor(v: Float) {
        this.x = v
        this.y = v
    }

    override fun toString(): String {
        return "(${x}, ${y})"
    }
}
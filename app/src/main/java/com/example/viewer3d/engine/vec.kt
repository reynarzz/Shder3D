package com.example.viewer3d.engine

class Vec4 {
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


class Vec3 {

    var x: Float = 0f
    var y: Float = 0f
    var z: Float = 0f
    var w: Float = 0f

    constructor(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    constructor() {

    }
}
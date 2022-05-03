package com.reynarz.shder3D.engine.lights

import com.reynarz.shder3D.engine.components.Component
import com.reynarz.shder3D.engine.vec4

open class Light : Component() {
    var color = vec4(1f, 1f, 0.5f, 1f)
}
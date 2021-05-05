package com.reynarz.minityeditor.engine.lights

import com.reynarz.minityeditor.engine.components.Component
import com.reynarz.minityeditor.engine.vec3
import com.reynarz.minityeditor.engine.vec4

open class Light : Component() {
    var color = vec4(1f, 1f, 0.5f, 1f)
}
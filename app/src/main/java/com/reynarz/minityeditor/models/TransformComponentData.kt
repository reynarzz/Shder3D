package com.reynarz.minityeditor.models

import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.engine.vec3
import kotlinx.serialization.Serializable

@Serializable
class TransformComponentData() : ComponentData("Transform", ComponentType.Transform) {
    var position = vec3()
    var eulerAngles = vec3()
    var scale = vec3(1f)
}
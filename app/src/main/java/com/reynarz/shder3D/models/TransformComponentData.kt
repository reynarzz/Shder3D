package com.reynarz.shder3D.models

import com.reynarz.shder3D.engine.vec3
import kotlinx.serialization.Serializable

@Serializable
class TransformComponentData(var cName:String = "Transform") : ComponentData(cName, ComponentType.Transform) {
    var position = vec3()
    var eulerAngles = vec3()
    var scale = vec3(1f)
}
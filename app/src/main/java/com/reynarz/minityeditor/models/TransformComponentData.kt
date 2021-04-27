package com.reynarz.minityeditor.models

import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.engine.vec3

class TransformComponentData : ComponentData("Entity Transform", R.layout.transform_fragment_view) {
    var position = vec3()
    var eulerAngles = vec3()
    var scale = vec3()
}
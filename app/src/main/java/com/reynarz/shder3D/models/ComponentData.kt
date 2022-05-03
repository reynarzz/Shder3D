package com.reynarz.shder3D.models

import kotlinx.serialization.Serializable


@Serializable
open class ComponentData(var name: String, var componentType: ComponentType) {

}
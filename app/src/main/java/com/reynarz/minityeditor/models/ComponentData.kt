package com.reynarz.minityeditor.models

import kotlinx.serialization.Serializable

@Serializable
open class ComponentData(var name: String, var componentViewID: Int) {
}
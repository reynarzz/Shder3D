package com.reynarz.minityeditor.engine.components

import kotlinx.serialization.Serializable

@Serializable
open class Entity {
     var name : String = "Entity"
     var entityID : String = ""
}
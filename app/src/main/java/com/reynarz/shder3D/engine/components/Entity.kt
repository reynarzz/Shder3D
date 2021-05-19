package com.reynarz.shder3D.engine.components

import kotlinx.serialization.Serializable

@Serializable
open class Entity {
     var name : String = "Entity"
     var entityID : String = ""
}
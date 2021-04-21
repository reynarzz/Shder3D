package com.example.viewer3d.engine.components

open class Component : Entity() {

    var transform: Transform? = null
        protected set

    var enabled = true
}
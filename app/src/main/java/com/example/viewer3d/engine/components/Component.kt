package com.example.viewer3d.engine.components

open class Component : Entity() {

    val transform : Transform = Transform() // the scene object has the instance of the transform and itself (see bellow)
    //val sceneObject : SceneObject = SceneObject()

    var enabled = true

}
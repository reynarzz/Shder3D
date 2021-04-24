package com.reynarz.minityeditor.engine.components

class SceneEntity : Entity() {
    private var components: MutableList<Component>? = null
    private var _transform: Transform? = null

    var testMeshRenderer : MeshRenderer? = null

    var isActive = true
        private set

//    val transform: Transform
//        get() {
//            return _transform!!
//        }

    init {
        components = mutableListOf()
        _transform = Transform()

        //components.add(_transform!!)
    }

//    fun <T : Component> getComponent(): T {
//
//        for(component in components!!){
//            if(component is checkType<T>())
//        }
//
//        return components!![0] as T
//    }

    fun <T : Component> addComponent(): T? {
        // how to create an instance of a generic.
//        components.add( T())

        return null
    }

    fun setActive(active: Boolean) {
        isActive = active
    }
}
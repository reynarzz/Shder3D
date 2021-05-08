package com.reynarz.minityeditor.engine.components


class SceneEntity : Entity() {

    private var components: MutableList<Component>? = null
    private var _transform: Transform? = null

    var isActive = true

//    val transform: Transform
//        get() {
//            return _transform!!
//        }

    init {
        components = mutableListOf()
        _transform = Transform()
    }
    lateinit var meshRenderer_Test:MeshRenderer
    /*
    * Get a component attached to this scene entity.
    * */
    fun <T> getComponent(classType: Class<T>): T? where T : Component? {
        for (i in components!!) {

            if (classType.isInstance(i)) {
                return i as T
            }
        }

        return null
    }

    // The component needs a default empty constructor so the instance can be created.
    fun <T> addComponent(type: Class<T>): T where T : Component? {
        val instance = type.newInstance()

        components!!.add(instance as Component)

        return instance
    }
}
package com.reynarz.shder3D.engine.components

import com.reynarz.shder3D.engine.vec3
import com.reynarz.shder3D.models.SceneEntityData


class SceneEntity(val entityData: SceneEntityData) : Entity() {

    private var components: MutableList<Component>? = null
    private var _transform: Transform? = null
    val colorID = vec3()

    init {
        components = mutableListOf()
        _transform = Transform()
    }

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
package com.reynarz.minityeditor.engine.components

import android.os.Build
import com.reynarz.minityeditor.engine.Mesh


class SceneEntity : Entity() {

    private var components: MutableList<Component>? = null
    private var _transform: Transform? = null

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

    fun <T> getComponent(classType: Class<T>): T where T : Component? {
        for (i in components!!) {

            if (classType.isInstance(i)) {
                return i as T
            }
        }

        return null as T
    }

    fun <T> addComponent(type: Class<T>): T where T : Component? {
        val component = type.newInstance() as Component

        components!!.add(component)

        return component as T
    }

    fun setActive(active: Boolean) {
        isActive = active
    }
}
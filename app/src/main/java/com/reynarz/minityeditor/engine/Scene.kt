package com.reynarz.minityeditor.engine

import com.reynarz.minityeditor.engine.components.Camera
import com.reynarz.minityeditor.engine.components.SceneEntity

class Scene {

    val entities : MutableList<SceneEntity> = mutableListOf()

    enum class CurrentCamera {
        EDITOR_CAMERA,
        PREVIEW_CAMERA
    }

    var editorCamera: Camera? = null
        private set

    var previewCamera: Camera? = null
        private set

    init {
        editorCamera = Camera()
        previewCamera = Camera()
        entities
    }

    fun addSceneEntity(sceneEntity: SceneEntity) {
        entities.add(sceneEntity)
    }

    fun removeSceneEntity(sceneEntity: SceneEntity) {
        entities.remove(sceneEntity)
    }


}
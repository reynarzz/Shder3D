package com.reynarz.minityeditor.engine

import com.reynarz.minityeditor.engine.components.Camera

class Scene {

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
    }

    fun bind() {


    }
}
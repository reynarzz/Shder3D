package com.example.viewer3d.engine

import android.opengl.GLES20
import android.opengl.Matrix

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
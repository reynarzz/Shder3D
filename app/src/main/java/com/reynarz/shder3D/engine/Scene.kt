package com.reynarz.shder3D.engine

import android.util.Log
import com.reynarz.shder3D.engine.components.Camera
import com.reynarz.shder3D.engine.components.SceneEntity
import com.reynarz.shder3D.engine.lights.DirectionalLight

class Scene {

    val entities: MutableList<SceneEntity> = mutableListOf()
    val directionalLight = DirectionalLight(vec3(1f, -1f, 1f).normalize())

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

    fun removeSceneEntity(sceneEntity: SceneEntity?) {
        entities.remove(sceneEntity)
    }

    fun removeSceneEntityByID(id: String?) {
        var entity: SceneEntity? = null

        for (i in entities) {
            if (i.entityID == id) {
                entity = i
                break
            }
        }

        if (entity != null)
            entities.remove(entity)
        else {
            Log.d("weird error", "trying to delete an entity")
        }
    }

    fun getEntityById(id: String?): SceneEntity? {

        for (i in entities) {
            if (i.entityID == id) {
                return i
            }
        }
        Log.d("Problems", "Problems with the entity id")
        return null
    }

}
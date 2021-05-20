package com.reynarz.shder3D

import com.reynarz.shder3D.engine.QueuedRenderableMesh
import com.reynarz.shder3D.engine.Scene
import com.reynarz.shder3D.files.FileManager
import com.reynarz.shder3D.models.MaterialData
import com.reynarz.shder3D.models.ProjectData
import com.reynarz.shder3D.models.SceneEntityData
import org.koin.java.KoinJavaComponent.get

// This will be between the data source and the viewmodel
class MinityProjectRepository {

    var initializedData = false
    lateinit var colorsPickupTableRBG: Array<Int>
    private var projectData: ProjectData? = null
    var selectedMaterial: MaterialData? = null
    var selectedTextureSlot = 0

    var selectedSceneEntity: SceneEntityData? = null
    var scene: Scene? = null

    val queuedRenderers = mutableMapOf<Int, MutableList<QueuedRenderableMesh>>()

    fun getMeshOfEntityID(entityID: String, meshIndex: Int): QueuedRenderableMesh? {
        for (key in queuedRenderers.keys) {
            for (queuedMesh in queuedRenderers[key]!!) {
                if (queuedMesh.entityID == entityID && queuedMesh.meshindexInsideEntity == meshIndex) {
                    return queuedMesh
                }
            }
        }

        return null
    }

    fun getProjectData(): ProjectData {

        if (projectData == null) {
            val fileManager: FileManager = get(FileManager::class.java)
            projectData = fileManager.loadProject()
        }
        return projectData!!
    }
}
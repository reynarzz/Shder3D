package com.reynarz.minityeditor

import com.reynarz.minityeditor.files.FileManager
import com.reynarz.minityeditor.models.MaterialData
import com.reynarz.minityeditor.models.ProjectData
import com.reynarz.minityeditor.models.SceneEntityData
import org.koin.java.KoinJavaComponent.get

// This will be between the data source and the viewmodel
class MinityProjectRepository() {

    lateinit var colorsPickupTableRBG: Array<Float>
    private var projectData: ProjectData? = null
    var selectedMaterial: MaterialData? = null
    var selectedTextureSlot = 0

    var selectedSceneEntity: SceneEntityData? = null

    fun getProjectData(): ProjectData {

        if (projectData == null) {
            val fileManager: FileManager = get(FileManager::class.java)
            projectData = fileManager.loadProject()
        }
        return projectData!!
    }
}
package com.reynarz.minityeditor

import com.reynarz.minityeditor.files.FileManager
import com.reynarz.minityeditor.models.ProjectData
import org.koin.java.KoinJavaComponent.get


// This will be between the data source and the viewmodel
class MinityProjectRepository() {

    private var projectData: ProjectData? = null

    fun getProjectData(): ProjectData {

        if (projectData === null) {
            val fileManager : FileManager = get(FileManager::class.java)
            projectData = fileManager.loadProject()
        }
        return projectData!!
    }
}
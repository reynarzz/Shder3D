package com.reynarz.minityeditor

import com.reynarz.minityeditor.models.ProjectData


// This will be the singleton where all the data as the main liveData of the scene is.
class MinityProjectRepository  constructor(var projectData: ProjectData) {
    companion object {
        @Volatile
        private var instance: MinityProjectRepository? = null

        fun getInstance(projectData: ProjectData) = instance ?: synchronized(this) {
            instance ?: MinityProjectRepository(projectData).also { instance = it }
        }
    }

    fun getEntities() = projectData.sceneEntities
    fun getProjectName() = projectData.projectName
    fun getSelectedEntity() = projectData.selectedEntityIndex
}
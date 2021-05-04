package com.reynarz.minityeditor.viewmodels

import androidx.lifecycle.ViewModel
import com.reynarz.minityeditor.DefaultNavigator
import com.reynarz.minityeditor.MinityProjectRepository
import com.reynarz.minityeditor.models.ProjectData
import com.reynarz.minityeditor.views.MainActivity
import org.koin.java.KoinJavaComponent.get

class SceneViewModel(private val defaultNavigator: DefaultNavigator) : ViewModel() {

    fun onDeleteSceneEntity() {
        val repository = get<MinityProjectRepository>(MinityProjectRepository::class.java)
        val project = repository.getProjectData()

        project.sceneEntities.remove(repository.selectedSceneEntity)
        val entity = repository.selectedSceneEntity
        repository.selectedSceneEntity = null

        MainActivity.instance.removeSceneEntity(entity)
    }
}
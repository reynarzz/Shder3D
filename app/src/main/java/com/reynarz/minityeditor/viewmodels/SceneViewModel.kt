package com.reynarz.minityeditor.viewmodels

import androidx.lifecycle.ViewModel
import com.reynarz.minityeditor.DefaultNavigator
import com.reynarz.minityeditor.MinityProjectRepository
import com.reynarz.minityeditor.models.SceneEntityData
import com.reynarz.minityeditor.views.MainActivity
import org.koin.java.KoinJavaComponent.get

class SceneViewModel(private val defaultNavigator: DefaultNavigator) : ViewModel() {

    lateinit var onAboutToDeleteEntity: (SceneEntityData) -> Unit

    fun onDeleteSceneEntity() {
        val repository = get<MinityProjectRepository>(MinityProjectRepository::class.java)
        val project = repository.getProjectData()

        onAboutToDeleteEntity(repository.selectedSceneEntity!!)


        project.sceneEntities.remove(repository.selectedSceneEntity)
        val entity = repository.selectedSceneEntity
        repository.selectedSceneEntity = null
    }
}
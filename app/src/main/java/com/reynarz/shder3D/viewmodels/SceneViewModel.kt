package com.reynarz.shder3D.viewmodels

import androidx.lifecycle.ViewModel
import com.reynarz.shder3D.DefaultNavigator
import com.reynarz.shder3D.MinityProjectRepository
import com.reynarz.shder3D.models.SceneEntityData
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
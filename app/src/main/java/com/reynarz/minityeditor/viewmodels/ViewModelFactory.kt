package com.reynarz.minityeditor.viewmodels

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.reynarz.minityeditor.models.SceneEntityData

class ViewModelFactory(private val activity: AppCompatActivity) {

     fun getSceneEntityViewModel(sceneEntityData: SceneEntityData) : SceneEntityViewModel {
        val viewModel = ViewModelProvider(activity).get(SceneEntityViewModel::class.java)

        viewModel.entityName.value = sceneEntityData.name
        viewModel.visible.value = sceneEntityData.visible
        viewModel.selected.value = sceneEntityData.selected

        viewModel.componentsData.value = mutableListOf()
        viewModel.componentsData.value!!.add(sceneEntityData.transformData)
        viewModel.componentsData.value!!.add(sceneEntityData.meshRendererData)

        return viewModel
    }

     fun getHierarchyViewModel(sceneEntities: MutableList<SceneEntityViewModel>): HierarchyViewModel {
        val hierarchyViewModel = ViewModelProvider(activity).get(HierarchyViewModel::class.java)

        hierarchyViewModel.entitiesInScene.value = sceneEntities

        return hierarchyViewModel
    }
}
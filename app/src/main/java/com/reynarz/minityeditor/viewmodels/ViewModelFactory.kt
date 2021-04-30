package com.reynarz.minityeditor.viewmodels

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.reynarz.minityeditor.models.SceneEntityData

class ViewModelFactory(private val activity: AppCompatActivity) {

     fun getInspectorEntityViewModel() : InspectorViewModel {
         return ViewModelProvider(activity).get(InspectorViewModel::class.java)
    }

     fun getHierarchyViewModel(sceneEntities: MutableList<SceneEntityData>): HierarchyViewModel {
        val hierarchyViewModel = ViewModelProvider(activity).get(HierarchyViewModel::class.java)

        hierarchyViewModel.entitiesInScene.value = sceneEntities

        return hierarchyViewModel
    }
}
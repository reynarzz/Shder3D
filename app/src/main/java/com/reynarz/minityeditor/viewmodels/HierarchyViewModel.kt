package com.reynarz.minityeditor.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reynarz.minityeditor.DefaultNavigator
import com.reynarz.minityeditor.MinityProjectRepository
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.models.ProjectData
import com.reynarz.minityeditor.models.SceneEntityData
import org.koin.java.KoinJavaComponent.get

class HierarchyViewModel(private val navigator: DefaultNavigator) : ViewModel() {

    val selectedEntityIndex = MutableLiveData<Int>()

    val entitiesInScene = MutableLiveData<MutableList<SceneEntityData>>()
    val defaultSceneEntitiesInScene = MutableLiveData<MutableList<SceneEntityData>>()

    fun closeHierarchy() {
        navigator.goBack(R.id.btn_closeHierarchy)
    }

    fun setData(projectData: ProjectData) {

        entitiesInScene.value = mutableListOf()
        defaultSceneEntitiesInScene.value = projectData.defaultSceneEntities

        for (i in projectData.sceneEntities) {
            entitiesInScene.value!!.add(i)
        }
    }

    fun openCameraInspector() {
        val repository = get<MinityProjectRepository>(MinityProjectRepository::class.java)

        // 0 is the Camera.
        repository.selectedSceneEntity = defaultSceneEntitiesInScene.value!![0]


        navigator.navigateTo(R.id.btn_closeHierarchy, R.id.action_hierarchyFragmentView_to_inspectorFragmentView2)
    }
}
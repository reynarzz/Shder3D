package com.reynarz.minityeditor.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reynarz.minityeditor.DefaultNavigator
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.models.ProjectData
import com.reynarz.minityeditor.models.SceneEntityData

class HierarchyViewModel(private val navigator: DefaultNavigator) : ViewModel() {

    val selectedEntityIndex = MutableLiveData<Int>()

    val entitiesInScene = MutableLiveData<MutableList<SceneEntityData>>()

    fun closeHierarchy() {
        navigator.goBack(R.id.btn_closeHierarchy)
    }

    fun setData(projectData: ProjectData) {

        entitiesInScene.value = mutableListOf()
        for (i in projectData.sceneEntities) {
            entitiesInScene.value!!.add(i)
        }
    }
}
package com.reynarz.minityeditor.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reynarz.minityeditor.models.SceneEntityData

class HierarchyViewModel : ViewModel() {

    val selectedEntityIndex : MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }

    val entitiesInScene: MutableLiveData<MutableList<SceneEntityViewModel>> by lazy {
        MutableLiveData<MutableList<SceneEntityViewModel>>()
    }
}
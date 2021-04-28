package com.reynarz.minityeditor.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reynarz.minityeditor.models.SceneEntityData

class HierarchyViewModel : ViewModel() {

    val entitiesInScene: MutableLiveData<MutableList<SceneEntityData>> by lazy {
        MutableLiveData<MutableList<SceneEntityData>>()
    }
}
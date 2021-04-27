package com.reynarz.minityeditor.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reynarz.minityeditor.models.ComponentData
import com.reynarz.minityeditor.models.MeshRendererComponentData
import com.reynarz.minityeditor.models.TransformComponentData

class SceneEntityViewModel : ViewModel() {

    val selected: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val visible: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val componentsData : MutableLiveData<MutableList<ComponentData>> by lazy {
        MutableLiveData<MutableList<ComponentData>>()
    }
}
package com.reynarz.minityeditor.viewmodels

import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reynarz.minityeditor.MinityProjectRepository
import com.reynarz.minityeditor.models.ComponentData

class InspectorViewModel(private val minityProjectRepository: MinityProjectRepository) : ViewModel() {

    val entityName = MutableLiveData<String>()

    val selected: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val visible: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val componentsData: MutableLiveData<MutableList<ComponentData>> by lazy {
        MutableLiveData<MutableList<ComponentData>>()
    }

    fun onEntityNameChanged() {

    }

    fun onCloseInspector() {

    }

    fun getEntityName() = minityProjectRepository.getEntities()[minityProjectRepository.getSelectedEntity()]
}
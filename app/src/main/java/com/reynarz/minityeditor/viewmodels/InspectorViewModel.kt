package com.reynarz.minityeditor.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reynarz.minityeditor.DefaultNavigator
import com.reynarz.minityeditor.MinityProjectRepository
import com.reynarz.minityeditor.models.ComponentData

class InspectorViewModel(
    private val minityProjectRepository: MinityProjectRepository,
    private val defaultNavigator: DefaultNavigator
) : ViewModel() {

    val entityName = MutableLiveData<String>()

    val selected = MutableLiveData<Boolean>()

    val visible = MutableLiveData<Boolean>()

    val componentsData = MutableLiveData<MutableList<ComponentData>>()

    fun onCloseInspector() {
        // navigation pop
        defaultNavigator.goBack()
    }

    fun onEntityActiveChanged() {
        Log.d("Checked", visible.value.toString())
    }

    fun getEntityName() = minityProjectRepository.getEntities()[minityProjectRepository.getSelectedEntity()]
}
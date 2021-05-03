package com.reynarz.minityeditor.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reynarz.minityeditor.DefaultNavigator
import com.reynarz.minityeditor.MinityProjectRepository
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.models.ComponentData
import com.reynarz.minityeditor.models.MeshRendererComponentData
import com.reynarz.minityeditor.models.SceneEntityData
import com.reynarz.minityeditor.models.TransformComponentData
import org.koin.java.KoinJavaComponent.get


class InspectorViewModel(
    private val defaultNavigator: DefaultNavigator
) : ViewModel() {

    val entityName = MutableLiveData<String>()

    val active = MutableLiveData<Boolean>()

    val componentsData = MutableLiveData<MutableList<ComponentData>>()

    fun onCloseInspector() {
        // navigation pop
        defaultNavigator.goBack(R.id.btn_closeInspector)
    }

    fun onEntityActiveChanged() {
        Log.d("Checked", active.value.toString())
    }

    fun setData(entityData: SceneEntityData) {

        componentsData.value = mutableListOf()
        componentsData.value!!.add(entityData!!.transformData)
        componentsData.value!!.add(entityData!!.meshRendererData)

        entityName.value = entityData!!.name
        active.value = entityData!!.active
    }
}
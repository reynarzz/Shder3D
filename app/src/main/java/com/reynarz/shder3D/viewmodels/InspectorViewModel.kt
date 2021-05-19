package com.reynarz.shder3D.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reynarz.shder3D.DefaultNavigator
import com.reynarz.shder3D.R
import com.reynarz.shder3D.models.ComponentData
import com.reynarz.shder3D.models.SceneEntityData

class InspectorViewModel(
    private val defaultNavigator: DefaultNavigator
) : ViewModel() {

    val entityName = MutableLiveData<String>()

    //val active = MutableLiveData<Boolean>()

    val componentsData = MutableLiveData<MutableList<ComponentData>>()

    fun onCloseInspector() {
        // navigation pop
        defaultNavigator.goBack(R.id.btn_closeInspector)
    }

//    fun onEntityActiveChanged() {
//        Log.d("Checked", active.value.toString())
//    }

    fun setData(entityData: SceneEntityData) {

        componentsData.value = mutableListOf()
        componentsData.value!!.add(entityData!!.transformData)
        componentsData.value!!.add(entityData!!.meshRendererData)

        entityName.value = entityData!!.name
        //active.value = entityData!!.active
    }
}
package com.reynarz.minityeditor.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reynarz.minityeditor.DefaultNavigator
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.models.ShaderData

class ShaderEditorViewModel(private val navigator: DefaultNavigator) : ViewModel() {
    val vertexShader = MutableLiveData<String>()
    val fragmentShader = MutableLiveData<String>()
    var showEditor = false

    fun onCompile() {
        Log.d("Changed", "Compile")
    }

    fun onHideShowEditor() {
        Log.d("Changed", "Show editor $showEditor")
    }

    fun onCloseEditor() {
        navigator.goBack(R.id.btn_closeShaderWindow)
    }

    fun setData(shaderData: ShaderData) {
        vertexShader.value = shaderData.vertexShader
        fragmentShader.value = shaderData.fragmentShader
    }
}
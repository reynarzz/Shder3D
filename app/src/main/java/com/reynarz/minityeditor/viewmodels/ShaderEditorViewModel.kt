package com.reynarz.minityeditor.viewmodels

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reynarz.minityeditor.DefaultNavigator
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.models.ShaderData

class ShaderEditorViewModel(private val navigator: DefaultNavigator) : ViewModel() {
    val vertexShader = MutableLiveData<String>()
    val fragmentShader = MutableLiveData<String>()
    var showEditor = MutableLiveData<Int>()
    val showFragmentShader = MutableLiveData<Boolean>()

    private var showingEditor = true

    fun onCompile() {

        Log.d("Changed", "Compile")
    }

    fun onHideShowEditor() {
        showingEditor = !showingEditor

        showEditor.value = if(showingEditor) View.VISIBLE else View.INVISIBLE

        Log.d("Changed", "Show editor ${showEditor.value}")
    }

    fun onCloseEditor() {
        navigator.goBack(R.id.btn_closeShaderWindow)
    }

    fun setData(shaderData: ShaderData) {
        vertexShader.value = shaderData.vertexShader
        fragmentShader.value = shaderData.fragmentShader
        showEditor.value = View.INVISIBLE
    }
}
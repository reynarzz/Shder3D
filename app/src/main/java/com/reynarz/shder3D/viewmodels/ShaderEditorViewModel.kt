package com.reynarz.shder3D.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reynarz.shder3D.DefaultNavigator
import com.reynarz.shder3D.R
import com.reynarz.shder3D.models.ShaderData

class ShaderEditorViewModel(private val navigator: DefaultNavigator) : ViewModel() {

    var vertexShader = MutableLiveData<String>()
    var fragmentShader = MutableLiveData<String>()
    val showEditor = MutableLiveData<Boolean>()
    var showFragmentShader = MutableLiveData<Boolean>()

    private var showingEditor = true
    lateinit var onCompileShader: () -> Unit
    lateinit var onHideOrShowEditor: () -> Unit

    fun onCompile() {
        onCompileShader()

        Log.d("Changed", "Compile")
    }

    fun onHideShowEditor() {

        onHideOrShowEditor()
        // showEditor.value = false
        //  Log.d("Changed", "Show editor ${showEditor.value}")
    }

    fun onCloseEditor() {
        navigator.goBack(R.id.btn_closeShaderWindow)
    }

    fun setData(shaderData: ShaderData) {
        vertexShader.value = shaderData.vertexShader
        fragmentShader.value = shaderData.fragmentShader
        showEditor.value = true
    }
}
package com.reynarz.minityeditor

import android.app.Activity
import androidx.navigation.findNavController

class DefaultNavigator {

    fun goBack(id : Int) {
        _activity?.findNavController(id)?.popBackStack()
    }

    fun goToShaderEditor() {
        _activity?.findNavController(R.id.btn_closeInspector)?.navigate(R.id.action_inspectorFragmentView_to_shaderEditorFragment)
    }

    private var _activity: Activity? = null

     var activity:Activity?
    get(){
        return _activity
    }
    set(value){
        _activity = value
    }
}
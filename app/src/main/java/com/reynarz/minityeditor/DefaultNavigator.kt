package com.reynarz.minityeditor

import android.app.Activity
import androidx.navigation.findNavController

class DefaultNavigator {

    fun goBack() {
        _activity?.findNavController(R.id.btn_closeInspector)?.popBackStack()
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
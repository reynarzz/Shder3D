package com.reynarz.minityeditor.viewmodels

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.reynarz.minityeditor.MinityProjectRepository
import com.reynarz.minityeditor.models.SceneEntityData
import com.reynarz.minityeditor.views.HierarchyFragmentView
import com.reynarz.minityeditor.views.MainActivity


class ViewModelFactory(private val minityProjectRepository: MinityProjectRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        if (modelClass.isInstance(InspectorViewModel::class.java)) {
//            return InspectorViewModel(minityProjectRepository) as T
//        }
//        else if(modelClass.isInstance(HierarchyViewModel::class.java)) {
//            return HierarchyFragmentView() as T
//
//        }
//        return null as T

        return InspectorViewModel(minityProjectRepository) as T
    }
}
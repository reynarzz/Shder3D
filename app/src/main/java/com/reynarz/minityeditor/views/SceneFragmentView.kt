package com.reynarz.minityeditor.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.config.Configurations
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.models.MeshRendererComponentData
import com.reynarz.minityeditor.models.SceneEntityData
import com.reynarz.minityeditor.models.TransformComponentData
import com.reynarz.minityeditor.viewmodels.HierarchyViewModel
import com.reynarz.minityeditor.viewmodels.SceneEntityViewModel

class SceneFragmentView() : Fragment(R.layout.scene_view_fragment) {
    var inspectorVM: ViewModel? = null
    var fileManagerVM: ViewModel? = null
    var hierarchyVM: HierarchyViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        view.findViewById<Button>(R.id.btn_openHierarchy).setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction().apply {

                if (hierarchyVM === null) {
                    Log.d("is null", "true")
                }
                val hierarchyfragment = HierarchyFragmentView()

                hierarchyfragment.viewModel = hierarchyVM!!

                replace(R.id.mainFragment, hierarchyfragment)
                commit()
            }
        }

        setAddModelButton()
       val testViewModel =  ViewModelProvider(this).get(SceneEntityViewModel::class.java)

        testViewModel.selected.value = false
        testViewModel.visible.value = true
        testViewModel.entityName.value = "RandomName"
        testViewModel.componentsData.value = mutableListOf()
        testViewModel.componentsData.value!!.add(TransformComponentData())
        testViewModel.componentsData.value!!.add(MeshRendererComponentData())

        setEditModelButton(testViewModel)
    }

    private fun setAddModelButton() {
        val addModel = view!!.findViewById<Button>(R.id.btn_addModelToScene)

        addModel.setOnClickListener {

            val intent = Intent(activity, FilePickerActivity::class.java)

            intent.putExtra(
                FilePickerActivity.CONFIGS, Configurations.Builder()
                    .setCheckPermission(true)

                    .setShowFiles(true)
                    .setShowImages(false)
                    .setShowVideos(false)
                    .setShowAudios(false)
                    .setSkipZeroSizeFiles(true)
                    .setSuffixes(".obj")
                    .build()
            )

           activity!!.startActivityForResult(intent, 1)
        }
    }

    private fun setEditModelButton(sceneEntityViewModel: SceneEntityViewModel) {
        val editModel = view!!.findViewById<Button>(R.id.btn_editModelComponents)

        val inspectorFragment = InspectorFragmentView()

        editModel.setOnClickListener {
            inspectorFragment.sceneEntityViewModel = sceneEntityViewModel

            activity!!.supportFragmentManager.beginTransaction().apply {
                replace(R.id.mainFragment, inspectorFragment)
                commit()
                //remove()
            }
        }
    }

    fun setViewModels(inspectorVM: ViewModel, fileManagerVM: ViewModel) {
        this.inspectorVM = inspectorVM

    }
}
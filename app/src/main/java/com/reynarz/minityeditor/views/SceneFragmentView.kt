package com.reynarz.minityeditor.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.config.Configurations
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.models.SceneEntityData
import com.reynarz.minityeditor.viewmodels.InspectorViewModel

class SceneFragmentView : Fragment(R.layout.scene_view_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHierarchyButton(view)

        setAddModelButton()
        setSaveButton()
        setEditModelButton(view)
    }

    private fun setHierarchyButton(view: View) {

        requireView().findViewById<Button>(R.id.btn_openHierarchy).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_sceneTo_Hierarchy)
        }

        val hierarchy = HierarchyFragmentView()

//        view!!.findViewById<Button>(R.id.btn_openHierarchy).setOnClickListener {
//            activity!!.supportFragmentManager.beginTransaction().apply {
//
//                replace(R.id.mainFragment, hierarchy)
//                commit()
//            }
//        }
    }

    private fun setSaveButton() {
        val saveButton = requireView()!!.findViewById<Button>(R.id.btn_saveProject)
        saveButton.setOnClickListener {
            MainActivity.instance.saveAllData()

        }
    }

    private fun setAddModelButton() {
        val addModel = requireView()!!.findViewById<Button>(R.id.btn_addModelToScene)

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

            requireActivity()!!.startActivityForResult(intent, 1)
        }
    }

    private fun setEditModelButton(view : View) {

        val editModel = requireView()!!.findViewById<Button>(R.id.btn_editModelComponents)

        fun populateSceneEntityViewModel(viewModel: InspectorViewModel, sceneEntityData: SceneEntityData) {

            viewModel.entityName.value = sceneEntityData.name
            viewModel.visible.value = sceneEntityData.active
            viewModel.selected.value = sceneEntityData.isSelected

            viewModel.componentsData.value = mutableListOf()
            viewModel.componentsData.value!!.add(sceneEntityData.transformData)
            viewModel.componentsData.value!!.add(sceneEntityData.meshRendererData)
        }

        editModel.setOnClickListener {
            //--populateSceneEntityViewModel(MainActivity.inspectorViewModel, (activity as MainActivity).selectedSceneEntity!!)
            //MainActivity.instance.openInspectorWindow()
            Navigation.findNavController(view).navigate(R.id.action_sceneTo_Inspector)
        }
    }
}
package com.reynarz.minityeditor.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.config.Configurations
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.models.SceneEntityData
import com.reynarz.minityeditor.viewmodels.InspectorViewModel

class SceneFragmentView : Fragment(R.layout.scene_view_fragment) {
    var fileManagerVM: ViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHierarchyButton()

        setAddModelButton()

        setEditModelButton()
    }

    private fun setHierarchyButton() {

        view!!.findViewById<Button>(R.id.btn_openHierarchy).setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction().apply {

                val hierarchyfragment = HierarchyFragmentView()

                replace(R.id.mainFragment, hierarchyfragment)
                commit()
            }
        }
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

    private fun setEditModelButton() {
        val editModel = view!!.findViewById<Button>(R.id.btn_editModelComponents)

        val inspectorFragment = InspectorFragmentView()

        fun populateSceneEntityViewModel(viewModel: InspectorViewModel, sceneEntityData: SceneEntityData) {

            viewModel.entityName.value = sceneEntityData.name
            viewModel.visible.value = sceneEntityData.visible
            viewModel.selected.value = sceneEntityData.selected

            viewModel.componentsData.value = mutableListOf()
            viewModel.componentsData.value!!.add(sceneEntityData.transformData)
            viewModel.componentsData.value!!.add(sceneEntityData.meshRendererData)


        }

        editModel.setOnClickListener {
            populateSceneEntityViewModel(MainActivity.inspectorViewModel, (activity as MainActivity).selectedSceneEntity!!)

            activity!!.supportFragmentManager.beginTransaction().apply {
                replace(R.id.mainFragment, inspectorFragment)
                commit()
                //remove()
            }
        }


    }
}

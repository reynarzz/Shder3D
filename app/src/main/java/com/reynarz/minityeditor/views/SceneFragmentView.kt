package com.reynarz.minityeditor.views

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.config.Configurations
import com.reynarz.minityeditor.MinityProjectRepository
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.databinding.SceneViewFragmentBinding
import com.reynarz.minityeditor.files.FileManager
import com.reynarz.minityeditor.models.SceneEntityData
import com.reynarz.minityeditor.viewmodels.InspectorViewModel
import com.reynarz.minityeditor.viewmodels.SceneViewModel
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel

class SceneFragmentView : Fragment(R.layout.scene_view_fragment) {

    private lateinit var binding: SceneViewFragmentBinding
    private val viewModel: SceneViewModel by viewModel()

    private val repository: MinityProjectRepository = get()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.scene_view_fragment, container, false)



        binding.viewmodel = viewModel
        return binding.root

    }

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

        binding.clEditSelectedEntityContainer.visibility = if(repository.selectedSceneEntity == null) View.GONE else View.VISIBLE

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
            val filemanager = get<FileManager>()
            filemanager.saveCurrentProject()
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

    private fun setEditModelButton(view: View) {

        // use bindings
        val editModel = requireView()!!.findViewById<Button>(R.id.btn_editModelComponents)



        editModel.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_sceneTo_Inspector)
        }
    }
}
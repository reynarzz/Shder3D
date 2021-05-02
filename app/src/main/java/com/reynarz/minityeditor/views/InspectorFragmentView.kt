package com.reynarz.minityeditor.views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.databinding.InspectorViewBinding
import com.reynarz.minityeditor.models.ComponentData
import com.reynarz.minityeditor.models.SceneEntityData
import com.reynarz.minityeditor.viewmodels.InspectorViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class InspectorFragmentView : Fragment() {

    private val inspectorVM: InspectorViewModel by viewModel()

    private lateinit var binding: InspectorViewBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.inspector_view, null, false)

        binding.viewmodel = inspectorVM

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inspectorVM.entityName.observe(viewLifecycleOwner, {
            Toast.makeText(requireActivity(), it, Toast.LENGTH_LONG)
            Log.d("Change to", it)
        })



//        inspectorVM.entityName.value = "reynardo"



//        val checkBox = view.findViewById<AppCompatCheckBox>(R.id.cb_activeEntity)
//        val entityName = view.findViewById<EditText>(R.id.et_entityName)
//
//        val selectedSceneData = (activity as MainActivity).selectedSceneEntity
//
//        val inspectorViewModel = MainActivity.inspectorViewModel
//        initComponentsData(selectedSceneData!!)
//
//        checkBox.isChecked = inspectorViewModel!!.visible.value!!
//
//        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
//            inspectorViewModel!!.visible.value = isChecked
//            selectedSceneData!!.active = isChecked
//        }
//
//        entityName.setText(inspectorViewModel!!.entityName.value!!)
//
//        entityName.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                inspectorViewModel!!.entityName.value = s.toString()
//            }
//
//            override fun afterTextChanged(s: Editable?) {}
//        })
//
//        inspectorViewModel!!.entityName.observe(viewLifecycleOwner, {
//            selectedSceneData!!.name = it
//
//        })
//
//        val adapter = InspectorRecycleViewAdapter(inspectorViewModel!!)
//        val componentsRecyclerView = view.findViewById<RecyclerView>(R.id.rv_componentsRecycleView)
//
//        componentsRecyclerView.adapter = adapter
//        componentsRecyclerView.layoutManager = LinearLayoutManager(context)
//
//        view.findViewById<Button>(R.id.btn_closeInspector).setOnClickListener {
//            (activity as MainActivity).openSceneWindow()
//        }
//
//        inspectorViewModel!!.componentsData.observe(viewLifecycleOwner, {
//
//            for (i in it) {
//                when (i.componentViewID) {
//                    R.layout.transform_fragment_view -> {
//                        val transform = i as TransformComponentData
//
//                        selectedSceneData!!.transformData = transform
//                    }
//
//                    R.layout.mesh_renderer_fragment_view -> {
//                        val meshRendererData = i as MeshRendererComponentData
//
//                        selectedSceneData.meshRendererData = meshRendererData
//
//                        MainActivity.instance.updateMaterials(selectedSceneData)
//                    }
//                }
//            }
//        })
    }

    private fun initComponentsData(sceneEntityData: SceneEntityData) {
        // val viewModel = MainActivity.inspectorViewModel

        val components = mutableListOf<ComponentData>()

        components.add(sceneEntityData.transformData)
        components.add(sceneEntityData.meshRendererData)

        // viewModel.componentsData.value = components
        // viewModel.entityName.value = sceneEntityData.name
    }
}
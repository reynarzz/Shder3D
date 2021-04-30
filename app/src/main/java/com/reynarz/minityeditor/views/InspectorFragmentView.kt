package com.reynarz.minityeditor.views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.models.ComponentData
import com.reynarz.minityeditor.models.MeshRendererComponentData
import com.reynarz.minityeditor.models.SceneEntityData
import com.reynarz.minityeditor.models.TransformComponentData
import java.util.*


class InspectorFragmentView : Fragment(R.layout.inspector_view) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val checkBox = view.findViewById<AppCompatCheckBox>(R.id.cb_activeEntity)
        val entityName = view.findViewById<EditText>(R.id.et_entityName)

        val selectedSceneData = (activity as MainActivity).selectedSceneEntity

        val inspectorViewModel = MainActivity.inspectorViewModel
        initComponentsData(selectedSceneData!!)

        checkBox.isChecked = inspectorViewModel!!.visible.value!!

        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            inspectorViewModel!!.visible.value = isChecked
            selectedSceneData!!.active = isChecked
        }

        entityName.setText(inspectorViewModel!!.entityName.value!!)

        entityName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inspectorViewModel!!.entityName.value = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        inspectorViewModel!!.entityName.observe(this, {
            selectedSceneData!!.name = it

        })

        val adapter = InspectorRecycleViewAdapter(inspectorViewModel!!)
        val componentsRecyclerView = view.findViewById<RecyclerView>(R.id.rv_componentsRecycleView)

        componentsRecyclerView.adapter = adapter
        componentsRecyclerView.layoutManager = LinearLayoutManager(context)

        view.findViewById<Button>(R.id.btn_closeInspector).setOnClickListener {
            (activity as MainActivity).openSceneWindow()
        }

        inspectorViewModel!!.componentsData.observe(this, {

            for (i in it) {
                when (i.componentViewID) {
                    R.layout.transform_fragment_view -> {
                        val transform = i as TransformComponentData

                        selectedSceneData!!.transformData = transform
                    }

                    R.layout.mesh_renderer_fragment_view -> {
                        val meshRendererData = i as MeshRendererComponentData

                        selectedSceneData.meshRendererData = meshRendererData

                    }
                }
            }
        })
    }

    private fun initComponentsData(sceneEntityData: SceneEntityData) {
        val viewModel = MainActivity.inspectorViewModel

        val components = mutableListOf<ComponentData>()

        components.add(sceneEntityData.transformData)
        components.add(sceneEntityData.meshRendererData)

        viewModel.componentsData.value = components
        viewModel.entityName.value = sceneEntityData.name
    }
}
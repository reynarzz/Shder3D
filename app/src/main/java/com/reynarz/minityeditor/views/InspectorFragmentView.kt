package com.reynarz.minityeditor.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reynarz.minityeditor.DefaultNavigator
import com.reynarz.minityeditor.MinityProjectRepository
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.databinding.InspectorViewBinding
import com.reynarz.minityeditor.models.ComponentData
import com.reynarz.minityeditor.models.MeshRendererComponentData
import com.reynarz.minityeditor.models.SceneEntityData
import com.reynarz.minityeditor.models.TransformComponentData
import com.reynarz.minityeditor.viewmodels.InspectorViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.KoinJavaComponent.get

class InspectorFragmentView : Fragment() {

    private val viewModel: InspectorViewModel by viewModel()
    private val navigator : DefaultNavigator = get(DefaultNavigator::class.java)

    private lateinit var binding: InspectorViewBinding
    private lateinit var entityData: SceneEntityData

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val projectData = get<MinityProjectRepository>(MinityProjectRepository::class.java).getProjectData()

        entityData = if (projectData.sceneEntities.size != 0)
            projectData.sceneEntities[projectData.selectedEntityIndex]
        else
            get(SceneEntityData::class.java)  // (the "else" is for testing, remove it!)

        viewModel.setData(entityData)

        binding = DataBindingUtil.inflate(inflater, R.layout.inspector_view, null, false)

        binding.viewmodel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.entityName.observe(viewLifecycleOwner, {
            entityData!!.name = it
            Log.d("Change to", it)
        })

        viewModel.active.observe(viewLifecycleOwner, {
            entityData.active = it
        })

        val adapter = InspectorRecycleViewAdapter(viewModel, navigator)
        val componentsRecyclerView = view.findViewById<RecyclerView>(R.id.rv_componentsRecycleView)

        componentsRecyclerView.adapter = adapter
        componentsRecyclerView.layoutManager = LinearLayoutManager(context)


        viewModel!!.componentsData.observe(viewLifecycleOwner, {

            for (i in it) {
                when (i.componentViewID) {
                    R.layout.transform_fragment_view -> {
                        val transform = i as TransformComponentData

                        entityData!!.transformData = transform
                    }

                    R.layout.mesh_renderer_fragment_view -> {
                        val meshRendererData = i as MeshRendererComponentData

                        entityData.meshRendererData = meshRendererData

                        MainActivity.instance.updateMaterials(entityData)
                    }
                }
            }
        })
    }
}
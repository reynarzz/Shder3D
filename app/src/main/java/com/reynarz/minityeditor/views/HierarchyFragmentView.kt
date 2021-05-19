package com.reynarz.minityeditor.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reynarz.minityeditor.MinityProjectRepository
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.databinding.HierarchyFragmentViewBinding
import com.reynarz.minityeditor.viewmodels.HierarchyViewModel
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel

class HierarchyFragmentView : Fragment(R.layout.hierarchy_fragment_view) {
    private val viewModel: HierarchyViewModel by viewModel()
    private lateinit var binding: HierarchyFragmentViewBinding
    private lateinit var repository: MinityProjectRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.hierarchy_fragment_view, container, false)

        repository = get()

        val projectData = repository.getProjectData()

        viewModel.setData(projectData)
        binding.viewmodel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.selectedEntityIndex.observe(viewLifecycleOwner, {
            val entity = viewModel.entitiesInScene.value!!.getOrNull(it)

            Log.d("Entity is null", (entity === null).toString())

            repository.selectedSceneEntity = entity
        })

        if (viewModel.entitiesInScene.value!!.size == 0) {
            binding.rvHierarcy.visibility = View.GONE
        }

        binding.rvHierarcy.apply {
            adapter = HierarchyRecyclerAdapter(viewModel)
            layoutManager = LinearLayoutManager(context)
        }
    }
}
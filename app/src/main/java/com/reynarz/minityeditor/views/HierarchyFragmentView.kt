package com.reynarz.minityeditor.views

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.viewmodels.HierarchyViewModel
import org.koin.android.ext.android.get

class HierarchyFragmentView : Fragment(R.layout.hierarchy_fragment_view) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = (activity as MainActivity)
        val viewModel = get<HierarchyViewModel>()

        //test
        viewModel.entitiesInScene.value = mutableListOf()

        viewModel.selectedEntityIndex.observe(viewLifecycleOwner, {
            val entity = viewModel.entitiesInScene.value!!.getOrNull(it)

            Log.d("Entity is null", (entity === null).toString())

            mainActivity.setSelectedEntity(entity)
        })

        view.findViewById<RecyclerView>(R.id.rv_hierarcy).apply {
            adapter = HierarchyRecyclerAdapter(viewModel)
            layoutManager = LinearLayoutManager(context)
        }

        view.findViewById<Button>(R.id.btn_closeHierarchy).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_hierarchyTo_scene)
            mainActivity.openSceneWindow()
        }
    }
}
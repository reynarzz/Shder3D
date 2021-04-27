package com.reynarz.minityeditor.views

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.engine.data.ComponentData

class InspectorFragmentView : Fragment(R.layout.inspector_view) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = mutableListOf(
            ComponentData("Entity Transform", R.layout.transform_fragment_view),
            ComponentData("Mesh Renderer", R.layout.mesh_renderer_fragment_view)
        )

        val adapter = InspectorRecycleViewAdapter(data)
        val componentsRecyclerView = view.findViewById<RecyclerView>(R.id.rv_componentsRecycleView)

        componentsRecyclerView.adapter = adapter
        componentsRecyclerView.layoutManager = LinearLayoutManager(context)

        view.findViewById<Button>(R.id.btn_closeInspector).setOnClickListener {
            val transaction = activity!!.supportFragmentManager.beginTransaction()
                transaction.remove(this)
            transaction.commit()

        }
    }
}
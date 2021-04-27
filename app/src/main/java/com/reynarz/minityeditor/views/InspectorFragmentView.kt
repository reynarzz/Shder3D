package com.reynarz.minityeditor.views

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.models.TransformComponentData
import com.reynarz.minityeditor.viewmodels.SceneEntityViewModel


class InspectorFragmentView(private val sceneEntityViewModel: SceneEntityViewModel) :
    Fragment(R.layout.inspector_view) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sceneEntityViewModel.componentsData.observe(this, {

            for (i in it) {
                when (i.componentID) {
                    R.layout.transform_fragment_view -> {
                        val transform = i as TransformComponentData

                        Log.d(
                            "Transform changed",
                            "(${transform.position.x}, ${transform.position.y}, ${transform.position.z})"
                        )
                    }
                }
            }
        })

        val adapter = InspectorRecycleViewAdapter(sceneEntityViewModel)
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
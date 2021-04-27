package com.reynarz.minityeditor.views

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reynarz.minityeditor.R

class HierarchyFragmentView : Fragment(R.layout.hierarchy_fragment_view) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<RecyclerView>(R.id.rv_hierarcy).apply {
            adapter = HierarchyRecyclerAdapter()
            layoutManager = LinearLayoutManager(context)
        }

        view.findViewById<Button>(R.id.btn_closeHierarchy).setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction().also {
                it.remove(this)
                it.commit()
            }
        }
    }
}
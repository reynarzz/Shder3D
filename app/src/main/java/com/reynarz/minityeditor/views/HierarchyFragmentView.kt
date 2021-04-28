package com.reynarz.minityeditor.views

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.viewmodels.HierarchyViewModel

class HierarchyFragmentView() : Fragment(R.layout.hierarchy_fragment_view) {

     var viewModel: HierarchyViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<RecyclerView>(R.id.rv_hierarcy).apply {
            adapter = HierarchyRecyclerAdapter(viewModel!!)
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
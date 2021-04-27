package com.reynarz.minityeditor.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reynarz.minityeditor.R

class HierarchyRecyclerAdapter : RecyclerView.Adapter<HierarchyRecyclerAdapter.HierarchyViewHolder>() {
    class HierarchyViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HierarchyViewHolder {

        return HierarchyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.hierarchy_items_view, parent, false))
    }

    override fun onBindViewHolder(holder: HierarchyViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 10
    }
}
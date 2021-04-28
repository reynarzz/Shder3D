package com.reynarz.minityeditor.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.viewmodels.HierarchyViewModel

class HierarchyRecyclerAdapter(private val viewModel: HierarchyViewModel) : RecyclerView.Adapter<HierarchyRecyclerAdapter.HierarchyViewHolder>() {
    class HierarchyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HierarchyViewHolder {

        return HierarchyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.hierarchy_items_view, parent, false))
    }

    override fun onBindViewHolder(holder: HierarchyViewHolder, position: Int) {
        val entityToggles = viewModel.entitiesInScene.value!!


        holder.itemView.apply {

            val toggle = findViewById<ToggleButton>(R.id.t_btn_entityHierarchyToggle)
            toggle.isChecked = entityToggles[position].selected

            toggle.text = entityToggles[position].name
            toggle.textOff = toggle.text
            toggle.textOn = toggle.text

            toggle.setOnCheckedChangeListener { _, isSelected ->
                entityToggles[position].selected = isSelected

                viewModel.entitiesInScene.value = entityToggles
            }
        }
    }

    override fun getItemCount(): Int {
        return viewModel.entitiesInScene.value!!.size
    }
}
package com.reynarz.shder3D.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.reynarz.shder3D.R
import com.reynarz.shder3D.viewmodels.HierarchyViewModel

class HierarchyRecyclerAdapter(private val viewModel: HierarchyViewModel) : RecyclerView.Adapter<HierarchyRecyclerAdapter.HierarchyViewHolder>() {
    class HierarchyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    val toggles = mutableListOf<ToggleButton>()
    private var lastEnabledToggle = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HierarchyViewHolder {

        return HierarchyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.hierarchy_items_view, parent, false))
    }

    override fun onBindViewHolder(holder: HierarchyViewHolder, position: Int) {
        val entityToggles = viewModel.entitiesInScene.value!!

        holder.itemView.apply {

            val toggle = findViewById<ToggleButton>(R.id.t_btn_entityHierarchyToggle)
            val activeToggle = findViewById<ToggleButton>(R.id.tb_enableDisableEntity)

            toggles.add(toggle)

            toggle.isChecked = entityToggles[position].isSelected
            activeToggle.isChecked = entityToggles[position].active

            if (toggle.isChecked) {
                lastEnabledToggle = position
            }

            toggle.text = entityToggles[position].name
            toggle.textOff = toggle.text
            toggle.textOn = toggle.text

            toggle.setOnCheckedChangeListener { _, isSelected ->
                entityToggles[position].isSelected = isSelected

                if (position != lastEnabledToggle) {
                    if (lastEnabledToggle != -1)
                        toggles[lastEnabledToggle].isChecked = false
                }

                lastEnabledToggle = if (isSelected) position else -1

                viewModel.selectedEntityIndex.value = lastEnabledToggle
                viewModel.entitiesInScene.value = entityToggles
            }

            activeToggle.setOnCheckedChangeListener { _, checked ->
                entityToggles[position].active = checked
            }
        }
    }

    override fun getItemCount(): Int {
        return viewModel.entitiesInScene.value!!.size
    }
}
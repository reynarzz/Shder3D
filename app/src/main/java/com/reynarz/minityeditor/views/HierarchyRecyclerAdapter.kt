package com.reynarz.minityeditor.views

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.viewmodels.HierarchyViewModel
import java.time.LocalDate

class HierarchyRecyclerAdapter(private val viewModel: HierarchyViewModel) : RecyclerView.Adapter<HierarchyRecyclerAdapter.HierarchyViewHolder>() {
    class HierarchyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    val toggles = mutableListOf<ToggleButton>()
    private var lastEnabledToggle = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HierarchyViewHolder {

        return HierarchyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.hierarchy_items_view, parent, false))
    }

    override fun onBindViewHolder(holder: HierarchyViewHolder, position: Int) {
        val entityToggles = viewModel.entitiesInScene.value!!

        var index = position

        holder.itemView.apply {

            val toggle = findViewById<ToggleButton>(R.id.t_btn_entityHierarchyToggle)
            toggles.add(toggle)

            toggle.isChecked = entityToggles[position].selected

            if (toggle.isChecked) {
                lastEnabledToggle = position
            }

            toggle.text = entityToggles[position].name
            toggle.textOff = toggle.text
            toggle.textOn = toggle.text

            toggle.setOnCheckedChangeListener { _, isSelected ->
                entityToggles[position].selected = isSelected

                if (position != lastEnabledToggle) {
                    if (lastEnabledToggle != -1)
                        toggles[lastEnabledToggle].isChecked = false
                }

                if (isSelected)
                    lastEnabledToggle = position
                else
                    lastEnabledToggle = -1

                viewModel.selectedEntityIndex.value = lastEnabledToggle
                viewModel.entitiesInScene.value = entityToggles
            }
        }
    }

    override fun getItemCount(): Int {
        return viewModel.entitiesInScene.value!!.size
    }
}
package com.reynarz.minityeditor.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.models.MaterialData

class MaterialTexturesAdapter(private val materialData: MaterialData) : RecyclerView.Adapter<MaterialTexturesAdapter.MaterialTexturesViewHolder>() {
    class MaterialTexturesViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialTexturesViewHolder {
        return MaterialTexturesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.texture_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: MaterialTexturesViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return materialData.texturesData.size
    }
}
package com.reynarz.minityeditor.views

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.models.MeshRendererComponentData

class MeshRendererMaterialsAdapter(private val meshRendererComponentData: MeshRendererComponentData) : RecyclerView.Adapter<MeshRendererMaterialsAdapter.MaterialsViewHolder>() {
    class MaterialsViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialsViewHolder {
        return MaterialsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.mesh_renderer_material_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: MaterialsViewHolder, position: Int) {

        holder.itemView.apply {

            val editMatButton = findViewById<Button>(R.id.btn_editMaterialShaderFromInspector)
            val removeMatButton = findViewById<Button>(R.id.btn_removeMaterial)

            editMatButton.setOnClickListener {
                var meshRendererData = meshRendererComponentData.materialsData[position]
                Log.d("Edit mat", position.toString())
                //meshRendererData.shaderId
            }

            removeMatButton.setOnClickListener {
                Log.d("Delete mat", position.toString())

            }
        }
    }

    override fun getItemCount(): Int {
        return meshRendererComponentData.materialsData.size
    }
}
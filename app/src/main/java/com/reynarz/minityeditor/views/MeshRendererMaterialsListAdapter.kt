package com.reynarz.minityeditor.views

import android.opengl.GLES20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reynarz.minityeditor.DefaultNavigator
import com.reynarz.minityeditor.MinityProjectRepository
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.models.MeshRendererComponentData
import com.reynarz.minityeditor.models.TextureData
import org.koin.java.KoinJavaComponent.get

class MeshRendererMaterialsListAdapter(private val meshRendererComponentData: MeshRendererComponentData, private val navigator: DefaultNavigator) : RecyclerView.Adapter<MeshRendererMaterialsListAdapter.MaterialsViewHolder>() {
    class MaterialsViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialsViewHolder {
        return MaterialsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.mesh_renderer_material_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: MaterialsViewHolder, position: Int) {

        holder.itemView.apply {
            var materialData = meshRendererComponentData.materialsData[position]

            // Texture RecyclerView
            val rvTexture = findViewById<RecyclerView>(R.id.rv_textureList)
            rvTexture.adapter = MaterialTexturesAdapter(materialData)
            rvTexture.layoutManager = GridLayoutManager(holder.itemView.context, 3)

            // Add Texture Slot
            val addTextureSlotButton = findViewById<Button>(R.id.btn_addTextureSlot)
            addTextureSlotButton.setOnClickListener {

                if (materialData.texturesData.size < GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS) {
                    materialData.texturesData.add(TextureData(materialData.texturesData.size.toString()))
                    rvTexture.adapter?.notifyDataSetChanged()
                }
            }

            // Remove Texture Slot
            val removeTextureSlotButton = findViewById<Button>(R.id.btn_removeTextureSlot)
            removeTextureSlotButton.setOnClickListener {

                materialData.texturesData.removeAt(materialData.texturesData.size - 1)
                rvTexture.adapter?.notifyDataSetChanged()
            }


            val materialNameText = findViewById<TextView>(R.id.tv_meshRendererMaterialName)
            materialNameText.text = materialData.name

            val editMatButton = findViewById<Button>(R.id.btn_editMaterialShaderFromInspector)
            val removeMatButton = findViewById<Button>(R.id.btn_removeMaterial)

            val minityRepository: MinityProjectRepository = get(MinityProjectRepository::class.java)


            editMatButton.setOnClickListener {
                minityRepository.selectedMaterial = materialData

                navigator.goToShaderEditor()

                // MainActivity.instance!!.openShaderWindow(meshRendererData)

                Log.d("Edit mat", position.toString())
            }

            removeMatButton.setOnClickListener {
                meshRendererComponentData.materialsData.removeAt(position)

                MainActivity.instance.removeMaterial(minityRepository.selectedSceneEntity, position)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return meshRendererComponentData.materialsData.size
    }
}
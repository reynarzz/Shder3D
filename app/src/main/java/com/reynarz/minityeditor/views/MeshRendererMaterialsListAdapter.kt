package com.reynarz.minityeditor.views

import android.opengl.GLES20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reynarz.minityeditor.DefaultNavigator
import com.reynarz.minityeditor.MinityProjectRepository
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.models.MaterialData
import com.reynarz.minityeditor.models.MeshRendererComponentData
import com.reynarz.minityeditor.models.TextureData
import com.reynarz.minityeditor.viewmodels.InspectorViewModel
import org.koin.java.KoinJavaComponent.get

class MeshRendererMaterialsListAdapter(private val onMaterialAdded: () -> Unit,  private val meshRendererComponentData: MeshRendererComponentData, private val navigator: DefaultNavigator) : RecyclerView.Adapter<MeshRendererMaterialsListAdapter.MaterialsViewHolder>() {
    class MaterialsViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialsViewHolder {
        return MaterialsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.mesh_renderer_material_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: MaterialsViewHolder, position: Int) {

        holder.itemView.apply {
            var materialData = meshRendererComponentData.materialsData.getOrNull(position)

            val addMaterialUI = findViewById<ConstraintLayout>(R.id.cl_addMaterialContainerUI)
            val materialUI = findViewById<ConstraintLayout>(R.id.cl_materialUI)

            if (materialData == null) {
                addMaterialUI.visibility = View.VISIBLE
                materialUI.visibility = View.GONE
            } else {
                addMaterialUI.visibility = View.GONE
                materialUI.visibility = View.VISIBLE
            }

            val rvTexture = findViewById<RecyclerView>(R.id.rv_textureList)

            if (materialData != null) {
                // Texture RecyclerView
                rvTexture.adapter = MaterialTexturesAdapter(materialData)
                rvTexture.layoutManager = GridLayoutManager(holder.itemView.context, 3)
            }

            // Add Texture Slot
            val addTextureSlotButton = findViewById<Button>(R.id.btn_addTextureSlot)

            addTextureSlotButton.setOnClickListener {

                //bad, use glGetInt in another place.
                if (materialData?.texturesData?.size!! < GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS) {
                    materialData?.texturesData.add(TextureData(materialData.texturesData.size.toString()))
                    rvTexture.adapter?.notifyDataSetChanged()
                }
            }

            // Remove Texture Slot
            val removeTextureSlotButton = findViewById<Button>(R.id.btn_removeTextureSlot)
            removeTextureSlotButton.setOnClickListener {

                materialData?.texturesData?.removeAt(materialData.texturesData.size - 1)
                rvTexture.adapter?.notifyDataSetChanged()
            }

            val materialNameText = findViewById<TextView>(R.id.tv_meshRendererMaterialName)
            materialNameText.text = materialData?.name

            val editMatButton = findViewById<Button>(R.id.btn_editMaterialShaderFromInspector)
            val removeMatButton = findViewById<Button>(R.id.btn_removeMaterial)

            val minityRepository: MinityProjectRepository = get(MinityProjectRepository::class.java)

            editMatButton.setOnClickListener {
                minityRepository.selectedMaterial = materialData

                navigator.goToShaderEditor()

                // MainActivity.instance!!.openShaderWindow(meshRendererData)

                Log.d("Edit mat", position.toString())
            }

            // Remove material.
            removeMatButton.setOnClickListener {

                // no, clean material slot.
                meshRendererComponentData.materialsData[position] = null

                MainActivity.instance.removeMaterial(minityRepository.selectedSceneEntity, position)
                notifyDataSetChanged()
            }


            findViewById<Button>(R.id.btn_newMaterial).setOnClickListener {

                val mat: MaterialData = get(MaterialData::class.java)

                // Add to the view model data, //bad.

                mat.name = "Error"

                val repo = get<MinityProjectRepository>(MinityProjectRepository::class.java)

                //println("materials-add count:" + materialsData.size.toString())

                //apply the data to view model first to notify.

                repo.selectedSceneEntity!!.meshRendererData.materialsData[position] = mat


                MainActivity.instance.updateMaterial(repo.selectedSceneEntity, position) {

                    meshRendererComponentData.materialsData[position] = mat
                }

                onMaterialAdded()

                //update the adapter list.
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return meshRendererComponentData.materialsData.size
    }
}
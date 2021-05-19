package com.reynarz.shder3D.views

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.config.Configurations
import com.reynarz.shder3D.MinityProjectRepository
import com.reynarz.shder3D.R
import com.reynarz.shder3D.models.MaterialData
import org.koin.java.KoinJavaComponent.get

class MaterialTexturesAdapter(private val materialData: MaterialData) : RecyclerView.Adapter<MaterialTexturesAdapter.MaterialTexturesViewHolder>() {
    class MaterialTexturesViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

    private var repository: MinityProjectRepository = get(MinityProjectRepository::class.java)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialTexturesViewHolder {
        return MaterialTexturesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.texture_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: MaterialTexturesViewHolder, position: Int) {
        holder.itemView.apply {
            val textureData = materialData.texturesData[position]

            val cardViewContainer = findViewById<CardView>(R.id.cv_cardViewContainer)
            val setTextureContainer = findViewById<ConstraintLayout>(R.id.cl_setTextureContainer)
            val setTextureButton = setTextureContainer.findViewById<Button>(R.id.btn_setTexture)
            val removeTextureButton = findViewById<Button>(R.id.btn_removeTexture)
            val textureImagePreview = cardViewContainer.findViewById<ImageView>(R.id.iv_texturePreview)


            fun enableDisableViews() {
                cardViewContainer.visibility = if (textureData.path.isNullOrEmpty()) View.GONE else View.VISIBLE
                removeTextureButton.visibility = cardViewContainer.visibility
                setTextureContainer.visibility = if (textureData.path.isNullOrEmpty()) View.VISIBLE else View.GONE

                textureImagePreview.setImageBitmap(textureData.previewBitmap)
            }

            enableDisableViews()

            setTextureButton.setOnClickListener {

                repository.selectedTextureSlot = position
                repository.selectedMaterial = materialData

                val intent = Intent(MainActivity.instance, FilePickerActivity::class.java)

                intent.putExtra(
                    FilePickerActivity.CONFIGS, Configurations.Builder()
                        .setCheckPermission(true)

                        .setShowFiles(false)
                        .setShowImages(true)
                        .setShowVideos(false)
                        .setShowAudios(false)
                        .setSkipZeroSizeFiles(true)
                        .setMaxSelection(1)
                        // .setSuffixes(".obj")
                        .build()
                )

                MainActivity.instance.startActivityForResult(intent, 2)
            }

            // the texture has to be removed from the renderer.
            removeTextureButton.setOnClickListener {
                textureData.path = null
                textureData.previewBitmap = null

                enableDisableViews()
            }
        }
    }

    override fun getItemCount(): Int {
        return materialData.texturesData.size
    }
}
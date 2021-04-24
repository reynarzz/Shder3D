package com.reynarz.minityeditor.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.models.FileItemType
import com.reynarz.minityeditor.models.FileData

class GridAdapterView : RecyclerView.Adapter<GridAdapterView.GridViewHolder>() {
    class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val data = mutableListOf<FileData>()

    init {
        data.add(FileData("Models", R.drawable.folder, FileItemType.Directory))
        data.add(FileData("Textures", R.drawable.folder, FileItemType.Directory))
        data.add(FileData("Materials", R.drawable.folder, FileItemType.Directory))
        data.add(FileData("Shaders", R.drawable.folder, FileItemType.Directory))
        data.add(FileData("Scenes", R.drawable.folder, FileItemType.Directory))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        return GridViewHolder(
            // Bind the view that the recycle view will show.
            LayoutInflater.from(parent.context)
                .inflate(R.layout.file_manager_items_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {

        val item = data[position]

        holder.itemView.apply {
            val fileButton = findViewById<Button>(R.id.btn_fileItem)
            val fileIcon = findViewById<ImageView>(R.id.iv_fileItemIcon)
            val fileName = findViewById<TextView>(R.id.tv_fileItemName)

            fileButton.setOnClickListener {
                when(item.itemType){
                    FileItemType.Directory -> { data.clear() } // go to the shader.
                    FileItemType.Shader -> { /*Open Shader editor*/ } // edit the shader.
                    FileItemType.Material -> {/*Open Material editor*/} // adjust shader variables.
                    FileItemType.Texture -> { /*Open texture editor*/ } // change repeat mode, clamping, filtering.
                    FileItemType.Model -> { /*Open model options { Send to scene, }*/}
                }

                notifyDataSetChanged()
            }

            fileIcon.setImageResource(item.fileIcon)
            fileName.text = item.fileName
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
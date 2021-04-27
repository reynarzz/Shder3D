package com.reynarz.minityeditor.views

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.models.ComponentData
import com.reynarz.minityeditor.models.TransformComponentData
import com.reynarz.minityeditor.viewmodels.SceneEntityViewModel

class InspectorRecycleViewAdapter(private val viewModel: SceneEntityViewModel) :
    RecyclerView.Adapter<InspectorRecycleViewAdapter.InspectorViwHolder>() {

    class InspectorViwHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InspectorViwHolder {
        return InspectorViwHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.component_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: InspectorViwHolder, position: Int) {

        val value = viewModel.componentsData.value

        val componentData = value!![position]

        holder.itemView.apply {

            findViewById<TextView>(R.id.tv_componentName).text = value!![position].name

            val frame = holder.itemView.findViewById<FrameLayout>(R.id.fragment_componentContent)

            val view = LayoutInflater.from(holder.itemView.context)
                .inflate(componentData.componentID, null, false)

            frame.addView(view)

            when (componentData.componentID) {
                R.layout.transform_fragment_view -> {

                    val transform = componentData as TransformComponentData

                    setEditTextTransformListeners(findViewById(R.id.et_xPos)) {
                        transform.position.x = it
                        viewModel.componentsData.value = value
                    }

                    setEditTextTransformListeners(findViewById(R.id.et_yPos)) {
                        transform.position.y = it
                        viewModel.componentsData.value = value
                    }

                    setEditTextTransformListeners(findViewById(R.id.et_zPos)) {

                        transform.position.z = it
                        viewModel.componentsData.value = value
                    }
                }

                R.layout.mesh_renderer_fragment_view -> {

                }
            }
        }
    }

    private fun setEditTextTransformListeners(editText: EditText, block: (Float) -> Unit) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var number = 1f

                val str = s.toString()
                if (!str.isNotBlank() && !str.isNullOrEmpty()) {
                    number = s.toString().toFloat()
                }

                block(number)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun getItemCount(): Int {
        return viewModel.componentsData.value!!.size
    }
}
package com.reynarz.minityeditor.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.engine.data.ComponentData

class InspectorRecycleViewAdapter(private val componentData: MutableList<ComponentData>) :
    RecyclerView.Adapter<InspectorRecycleViewAdapter.InspectorViwHolder>() {

    class InspectorViwHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InspectorViwHolder {
        return InspectorViwHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.component_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: InspectorViwHolder, position: Int) {

        holder.itemView.apply {
            findViewById<TextView>(R.id.tv_componentName).text = componentData[position].name

            val frame = holder.itemView.findViewById<FrameLayout>(R.id.fragment_componentContent)

            val view = LayoutInflater.from(holder.itemView.context)
                .inflate(componentData[position].componentID, null, false)

            frame.addView(view)
        }
    }

    override fun getItemCount(): Int {
        return componentData.size
    }
}
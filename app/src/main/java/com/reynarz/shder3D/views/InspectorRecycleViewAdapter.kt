package com.reynarz.shder3D.views

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reynarz.shder3D.DefaultNavigator
import com.reynarz.shder3D.R
import com.reynarz.shder3D.engine.Utils
import com.reynarz.shder3D.models.*
import com.reynarz.shder3D.viewmodels.InspectorViewModel

class InspectorRecycleViewAdapter(private val viewModel: InspectorViewModel, private val navigator: DefaultNavigator) :
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
                .inflate(Utils.componentTypeToID(componentData.componentType), null, false)

            frame.addView(view)

            when (componentData.componentType) {
                ComponentType.Transform -> {
                    setTransformListeners(this, componentData as TransformComponentData, value)
                }
                ComponentType.MeshRenderer -> {

                    val meshRendererComponentData = componentData as MeshRendererComponentData
                    val adapter = MeshRendererMaterialsListAdapter({ viewModel.componentsData.value = value }, meshRendererComponentData, navigator)

                    val castShadowsCheckBox = findViewById<CheckBox>(R.id.cb_castShadows_meshRenderer)

                    castShadowsCheckBox.isChecked = meshRendererComponentData.castShadows

                    castShadowsCheckBox.setOnCheckedChangeListener { _, checked ->
                        meshRendererComponentData.castShadows = checked
                    }

                    // possible deprecation (add something )
//                    val addMaterialButton = findViewById<Button>(R.id.btn_addToComponentList)
//                    addMaterialButton.visibility = View.VISIBLE

                    val rvMaterials = findViewById<RecyclerView>(R.id.rv_meshRendererMaterials)

                    rvMaterials.adapter = adapter
                    rvMaterials.layoutManager = LinearLayoutManager(holder.itemView.context)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return viewModel.componentsData.value!!.size
    }

    private fun setTransformListeners(view: View, transform: TransformComponentData, value: MutableList<ComponentData>) {

        view.apply {
            val xPos = findViewById<EditText>(R.id.et_xPos)
            val yPos = findViewById<EditText>(R.id.et_yPos)
            val zPos = findViewById<EditText>(R.id.et_zPos)

            val xRot = findViewById<EditText>(R.id.et_xRot)
            val yRot = findViewById<EditText>(R.id.et_yRot)
            val zRot = findViewById<EditText>(R.id.et_zRot)

            val xScale = findViewById<EditText>(R.id.et_xScale)
            val yScale = findViewById<EditText>(R.id.et_yScale)
            val zScale = findViewById<EditText>(R.id.et_zScale)

            xPos.setText(transform.position.x.toString())
            yPos.setText(transform.position.y.toString())
            zPos.setText(transform.position.z.toString())

            xRot.setText(transform.eulerAngles.x.toString())
            yRot.setText(transform.eulerAngles.y.toString())
            zRot.setText(transform.eulerAngles.z.toString())

            xScale.setText(transform.scale.x.toString())
            yScale.setText(transform.scale.y.toString())
            zScale.setText(transform.scale.z.toString())

            // Position
            setEditTextTransformListeners(xPos) {
                transform.position.x = it
                viewModel.componentsData.value = value
            }
            setEditTextTransformListeners(yPos) {
                transform.position.y = it
                viewModel.componentsData.value = value
            }
            setEditTextTransformListeners(zPos) {

                transform.position.z = it
                viewModel.componentsData.value = value
            }

            // Rotation
            setEditTextTransformListeners(findViewById(R.id.et_xRot)) {
                transform.eulerAngles.x = it
                viewModel.componentsData.value = value
            }
            setEditTextTransformListeners(findViewById(R.id.et_yRot)) {
                transform.eulerAngles.y = it
                viewModel.componentsData.value = value
            }
            setEditTextTransformListeners(findViewById(R.id.et_zRot)) {

                transform.eulerAngles.z = it
                viewModel.componentsData.value = value
            }

            // Scale
            setEditTextTransformListeners(findViewById(R.id.et_xScale)) {
                transform.scale.x = it
                viewModel.componentsData.value = value
            }
            setEditTextTransformListeners(findViewById(R.id.et_yScale)) {
                transform.scale.y = it
                viewModel.componentsData.value = value
            }
            setEditTextTransformListeners(findViewById(R.id.et_zScale)) {

                transform.scale.z = it
                viewModel.componentsData.value = value
            }
        }
    }


    private fun setEditTextTransformListeners(editText: EditText, block: (Float) -> Unit) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var number = 0f

                val str = s.toString()

                if (!str.isNullOrEmpty()) {
                    number = s.toString().toFloat()
                }

                block(number)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
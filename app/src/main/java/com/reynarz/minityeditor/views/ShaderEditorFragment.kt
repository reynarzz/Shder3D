package com.reynarz.minityeditor.views

import android.content.res.AssetManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.engine.OpenGLRenderer
import com.reynarz.minityeditor.engine.Utils
import com.reynarz.minityeditor.models.MaterialData
import java.io.BufferedReader
import java.io.InputStreamReader

class ShaderEditorFragment : Fragment(R.layout.shader_editor_fragment_view) {

    var materialData: MaterialData? = null

    lateinit var renderer: OpenGLRenderer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val shaderData = materialData!!.shaderData

        val compileButton = view.findViewById<Button>(R.id.buttonCompile)
        val closeShaderWindow = view.findViewById<Button>(R.id.btn_closeShaderWindow)
        val codeEditTex = view.findViewById<EditText>(R.id.et_fragmentCode)

        val showHideButton = view.findViewById<Button>(R.id.btn_showHide)
        val switchShaderType = view.findViewById<Button>(R.id.btn_switchShaderView)

        closeShaderWindow.setOnClickListener {
            MainActivity.instance.openInspectorWindow()
        }

        val include1 = getInclude(activity!!.assets, "includes/unity.h")

        codeEditTex.setText(shaderData.fragmentShader)

        var fragShaderFocused = true

        switchShaderType.setOnClickListener {
            fragShaderFocused = !fragShaderFocused

            if (fragShaderFocused) {
                codeEditTex.setText(shaderData.fragmentShader)
            } else {
                codeEditTex.setText(shaderData.vertexShader)
            }
        }

        showHideButton.setOnClickListener {
            setPreviewMode()
        }

        compileButton.setOnClickListener {

            if (fragShaderFocused) {
                shaderData.fragmentShader = codeEditTex.editableText.toString()
            } else {
                shaderData.vertexShader = codeEditTex.editableText.toString()
            }

            renderer.setReplaceShadersCommand(
                Utils.ShaderFileUtils.processInclude(include1, shaderData.vertexShader),
                Utils.ShaderFileUtils.processInclude(include1, shaderData.fragmentShader)
            )

            var compilationMessageCallback = { message: String ->
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }

            compilationMessageCallback("Compiled!")
        }
    }

    private fun getInclude(assets: AssetManager, include: String): String {
        val inStream = assets.open(include)
        val reader = BufferedReader(InputStreamReader(inStream))
        return reader.readText()
    }

    private fun setPreviewMode() {
        val codeContainer = view!!.findViewById<ConstraintLayout>(R.id.codeContainer)
        val background = view!!.findViewById<ImageView>(R.id.iv_backgroundImage)
        val switchShaderTypeContainer = view!!.findViewById<ConstraintLayout>(R.id.cl_switchShaderContainer)
        val compileContainer = view!!.findViewById<ConstraintLayout>(R.id.cl_compileShaderContainer)
        val closeShaderWindowContainer = view!!.findViewById<ConstraintLayout>(R.id.cl_closeShaderWindowContainer)

        background.visibility = if (background.visibility === View.VISIBLE) View.INVISIBLE else View.VISIBLE

        codeContainer.visibility = background.visibility
        switchShaderTypeContainer.visibility = background.visibility
        compileContainer.visibility = background.visibility
        closeShaderWindowContainer.visibility = background.visibility
    }
}
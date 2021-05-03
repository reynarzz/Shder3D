package com.reynarz.minityeditor.views

import android.content.res.AssetManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.reynarz.minityeditor.MinityProjectRepository
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.databinding.ShaderEditorFragmentViewBinding
import com.reynarz.minityeditor.engine.OpenGLRenderer
import com.reynarz.minityeditor.engine.Utils
import com.reynarz.minityeditor.models.MaterialData
import com.reynarz.minityeditor.models.ShaderData
import com.reynarz.minityeditor.viewmodels.ShaderEditorViewModel
import java.io.BufferedReader
import java.io.InputStreamReader
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.android.ext.android.*

class ShaderEditorFragment : Fragment(R.layout.shader_editor_fragment_view) {

    private lateinit var binding: ShaderEditorFragmentViewBinding
    private val shaderViewModel: ShaderEditorViewModel by viewModel()
    private lateinit var shaderData: ShaderData
   // lateinit var renderer: OpenGLRenderer

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.shader_editor_fragment_view, null, false)

        val minityRepository: MinityProjectRepository = get()
        shaderData = minityRepository.selectedMaterial.shaderData

        shaderViewModel.setData(shaderData)

        binding.viewmodel = shaderViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val compileButton = view.findViewById<Button>(R.id.buttonCompile)
        val closeShaderWindow = view.findViewById<Button>(R.id.btn_closeShaderWindow)
        //val codeEditTex = view.findViewById<EditText>(R.id.et_fragmentCode)

        val showHideButton = view.findViewById<Button>(R.id.btn_showHide)
        val switchShaderType = view.findViewById<Button>(R.id.btn_switchShaderView)

        val include1 = getInclude(requireActivity().assets, "includes/unity.h")

        shaderViewModel.onCompileShader = {
            shaderData.vertexShader = shaderViewModel.vertexShader.value!!
            shaderData.fragmentShader = shaderViewModel.fragmentShader.value!!

            var compilationMessageCallback = { message: String ->
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }

            compilationMessageCallback("Compiled and saved")
           MainActivity.instance.openGLView.renderer.setReplaceShadersCommand(
                Utils.processInclude(include1, shaderData.vertexShader),
                Utils.processInclude(include1, shaderData.fragmentShader)
            )
        }
//        var fragShaderFocused = true
//
//        switchShaderType.setOnClickListener {
//            fragShaderFocused = !fragShaderFocused
//
//            if (fragShaderFocused) {
//                codeEditTex.setText(shaderData.fragmentShader)
//            } else {
//                codeEditTex.setText(shaderData.vertexShader)
//            }
//        }
////
//        binding.btnShowHide.setOnClickListener {
//            binding.codeContainer.visibility = View.INVISIBLE
//        }
//
//        compileButton.setOnClickListener {
//
//            if (fragShaderFocused) {
//                shaderData.fragmentShader = codeEditTex.editableText.toString()
//            } else {
//                shaderData.vertexShader = codeEditTex.editableText.toString()
//            }
//
//            renderer.setReplaceShadersCommand(
//                Utils.ShaderFileUtils.processInclude(include1, shaderData.vertexShader),
//                Utils.ShaderFileUtils.processInclude(include1, shaderData.fragmentShader)
//            )
//
//            var compilationMessageCallback = { message: String ->
//                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
//            }
//
//            compilationMessageCallback("Compiled!")
//        }
    }

    private fun getInclude(assets: AssetManager, include: String): String {
        val inStream = assets.open(include)
        val reader = BufferedReader(InputStreamReader(inStream))
        return reader.readText()
    }

    private fun setPreviewMode() {
//        val codeContainer = view!!.findViewById<ConstraintLayout>(R.id.codeContainer)
//        val background = view!!.findViewById<ImageView>(R.id.iv_backgroundImage)
//        val switchShaderTypeContainer = view!!.findViewById<ConstraintLayout>(R.id.cl_switchShaderContainer)
//        val compileContainer = view!!.findViewById<ConstraintLayout>(R.id.cl_compileShaderContainer)
//        val closeShaderWindowContainer = view!!.findViewById<ConstraintLayout>(R.id.cl_closeShaderWindowContainer)
//
//        background.visibility = if (background.visibility === View.VISIBLE) View.INVISIBLE else View.VISIBLE
//
//        codeContainer.visibility = background.visibility
//        switchShaderTypeContainer.visibility = background.visibility
//        compileContainer.visibility = background.visibility
//        closeShaderWindowContainer.visibility = background.visibility
    }
}
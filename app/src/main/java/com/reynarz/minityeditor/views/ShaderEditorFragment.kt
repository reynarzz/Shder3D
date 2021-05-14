package com.reynarz.minityeditor.views

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.reynarz.minityeditor.MinityProjectRepository
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.databinding.ShaderEditorFragmentViewBinding
import com.reynarz.minityeditor.engine.Utils
import com.reynarz.minityeditor.models.MaterialData
import com.reynarz.minityeditor.models.ShaderData
import com.reynarz.minityeditor.viewmodels.ShaderEditorViewModel
import org.koin.android.ext.android.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.StringBuilder


class ShaderEditorFragment : Fragment(R.layout.shader_editor_fragment_view) {

    private lateinit var binding: ShaderEditorFragmentViewBinding
    private val shaderViewModel: ShaderEditorViewModel by viewModel()
    private lateinit var shaderData: ShaderData
    private var materialData: MaterialData? = null
    private var spanableString = SpannableStringBuilder()

    // lateinit var renderer: OpenGLRenderer

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.shader_editor_fragment_view, null, false)

        val minityRepository: MinityProjectRepository = get()
        materialData = minityRepository.selectedMaterial
        shaderData = materialData?.shaderData!!

        shaderViewModel.setData(shaderData)

        binding.viewmodel = shaderViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val include1 = Utils.getInclude(requireActivity().assets, "includes/minity.h")

        shaderViewModel.onHideOrShowEditor = {
            setPreviewMode()
        }

        shaderViewModel.onCompileShader = {
            shaderData.vertexShader = shaderViewModel.vertexShader.value!!
            shaderData.fragmentShader = shaderViewModel.fragmentShader.value!!

            val materialConfig = Utils.processMaterialConfig(shaderData.fragmentShader)
            materialData?.materialConfig = materialConfig

            MainActivity.instance.openGLView.renderer.setReplaceShadersCommand(
                Utils.processInclude(include1, shaderData.vertexShader),
                Utils.processInclude(include1, shaderData.fragmentShader)
            )
        }

        binding.btnSwitchShaderView.setOnCheckedChangeListener { _, checked ->
            binding.etVertexCode.visibility = if (checked) View.VISIBLE else View.GONE
            binding.etFragmentCode.visibility = if (!checked) View.VISIBLE else View.GONE
        }

        shaderViewModel.fragmentShader.observe(viewLifecycleOwner, {

            if (!changed)
                colorText(it)
            else {
                changed = false
            }
        })
    }

    private var changed = false

    private class WordToHightlight {
        var word = ""
        var startIndex = 0
        var endIndex = 0
    }

    private fun colorText(text: String) {
        changed = true
        spanableString.clear()
        spanableString.append(text)
        println("Changed")

        val words = getWords(text)

        for (word in words) {
            //println("w: " + word.word + ", s: " + word.startIndex + ", e: " + word.endIndex)
            if (Utils.shaderColorHightlight.containsKey(word.word)) {
                println("w: " + word.word + ", s: " + word.startIndex + ", e: " + word.endIndex)
                val color = Utils.shaderColorHightlight[word.word]

                spanableString.setSpan(ForegroundColorSpan(color!!), word.startIndex, word.endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            } else if (word.word.isDigitsOnly()) {
                spanableString.setSpan(ForegroundColorSpan(Color.parseColor("#4ac3d4")), word.startIndex, word.endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

            }
        }

        val cursorCurrentPos = binding.etFragmentCode.selectionEnd
        binding.etFragmentCode.text = spanableString
        binding.etFragmentCode.setSelection(cursorCurrentPos)
    }

    private fun getWords(text: String): List<WordToHightlight> {
        val words = mutableListOf<WordToHightlight>()

        val stringBuilder = StringBuilder()
        var startIndex = 0

        for (i in 0 until text.length) {
            if (text[i].isLetterOrDigit() && !text[i].isWhitespace() || text[i] == '_') {

                val prevChar = text.getOrNull(i - 1)
                if (prevChar != null && !prevChar.isLetterOrDigit() && prevChar != '_') {
                    startIndex = i
                    //println("startIndex here!")
                }

                stringBuilder.append(text[i])
            } else {
                if (stringBuilder.length > 0) {
                    words.add(WordToHightlight().also {
                        it.word = stringBuilder.toString()
                        it.startIndex = startIndex
                        it.endIndex = i
                    })
                    stringBuilder.clear()
                }
            }
        }

        return words
    }

    private fun setPreviewMode() {
        val codeContainer = requireView().findViewById<ConstraintLayout>(R.id.codeContainer)
        val background = requireView().findViewById<ImageView>(R.id.iv_backgroundImage)
        val switchShaderTypeContainer = requireView().findViewById<ConstraintLayout>(R.id.cl_switchShaderContainer)
        val compileContainer = requireView().findViewById<ConstraintLayout>(R.id.cl_compileShaderContainer)
        val closeShaderWindowContainer = requireView().findViewById<ConstraintLayout>(R.id.cl_closeShaderWindowContainer)

        background.visibility = if (background.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE

        codeContainer.visibility = background.visibility
        switchShaderTypeContainer.visibility = background.visibility
        compileContainer.visibility = background.visibility
        closeShaderWindowContainer.visibility = background.visibility
    }
}
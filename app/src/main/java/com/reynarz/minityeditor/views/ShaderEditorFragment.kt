package com.reynarz.minityeditor.views

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
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
import kotlinx.coroutines.*
import org.koin.android.ext.android.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class ShaderEditorFragment : Fragment(R.layout.shader_editor_fragment_view) {

    private lateinit var binding: ShaderEditorFragmentViewBinding
    private val shaderViewModel: ShaderEditorViewModel by viewModel()
    private lateinit var shaderData: ShaderData
    private var materialData: MaterialData? = null

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

//        shaderViewModel.fragmentShader.observe(viewLifecycleOwner, {
//            if (!changed)
//                colorText(it, binding.etFragmentCode)
//            else {
//                changed = false
//            }
//        })

        val fragListener = WatchListener(this, binding.etFragmentCode)
        val vertexListener = WatchListener(this, binding.etVertexCode)

        binding.etFragmentCode.addTextChangedListener(fragListener)
        binding.etVertexCode.addTextChangedListener(vertexListener)

//        shaderViewModel.vertexShader.observe(viewLifecycleOwner, {
//            if (!changed)
//                colorText(it, binding.etVertexCode)
//            else {
//                changed = false
//            }
//        })
    }

    private class WatchListener(private val editor: ShaderEditorFragment, private val editText: EditText) : TextWatcher {
        private var prevText = ""
        private var changedText = ""
        private var spanableString = SpannableStringBuilder()

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            prevText = s.toString()

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                if (!changed) {
//                    colorText(s.toString(), start, count, binding.etFragmentCode)
//
//                } else {
//                    changed = false
//                }
            if (!changed) {
                editor.colorText(s.toString(), start, count, editText, spanableString)
                changed = true

            } else {
                changed = false
            }

        }

        private var changed = false

        override fun afterTextChanged(s: Editable?) {

        }
    }


    // this needs optimization
    private fun colorText(text: String, start: Int, count: Int, editText: EditText, spannableString: SpannableStringBuilder) {



        fun processText(): SpannableStringBuilder {

            spannableString.clear()
            spannableString.clearSpans()

            spannableString.append(text)

            //val subString = text.subSequence(0, start + count)
            val words = Utils.getWordsFromText(text)

            for (word in words) {
                // println("w: " + word.word + ", s: " + word.startIndex + ", e: " + word.endIndex)
                if (Utils.shaderColorHightlight.containsKey(word.word)) {
                    //println("w: " + word.word + ", s: " + word.startIndex + ", e: " + word.endIndex)
                    val color = Utils.shaderColorHightlight[word.word]

                    spannableString.setSpan(ForegroundColorSpan(color!!), word.startIndex, word.endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                } else if (word.word.isDigitsOnly()) {
                    spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#4ac3d4")), word.startIndex, word.endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

                }
            }

            return spannableString
        }

        GlobalScope.launch(Dispatchers.Main)
        {
            val result = withContext(Dispatchers.Default) {
                processText()
            }

            //--editText.text.replace(0, editText.text.length, result)
        }
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
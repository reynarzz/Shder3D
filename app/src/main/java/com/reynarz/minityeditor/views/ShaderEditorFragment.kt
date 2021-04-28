package com.reynarz.minityeditor.views

import android.content.res.AssetManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.engine.OpenGLView
import com.reynarz.minityeditor.engine.SceneObjectManager
import com.reynarz.minityeditor.engine.Utils
import java.io.BufferedReader
import java.io.InputStreamReader

class ShaderEditorFragment : Fragment(R.layout.shader_editor_fragment_view) {

    private var sceneObjManager: SceneObjectManager? = null

    lateinit var openGLView: OpenGLView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button = view.findViewById<Button>(R.id.buttonCompile)
        val codeEditTex = view.findViewById<EditText>(R.id.et_fragmentCode)

        val showHideButton = view.findViewById<Button>(R.id.btn_showHide)
        val codeContainer = view.findViewById<ConstraintLayout>(R.id.codeContainer)
        val viewShader = view.findViewById<Button>(R.id.btn_switchShaderView)


        // vertexTex = Utils.ShaderUtils.processInclude(baseContext, vertexTex)

        var vertexTex = """#Unity.h 
            
attribute vec4 _VERTEX_; 
           
attribute vec2 _UV_;
varying vec2 _uv;
varying vec4 pos;

void main() 
{
   _uv = _UV_;
   
   float angle = 50.;
   mat4 rotY = mat4(vec4(cos(angle), 0., -sin(angle), 0.),
                    vec4(0., 1., 0., 0.),
                    vec4(sin(angle), 0., cos(angle), 0.),
                    vec4(0., 0., 0., 1.));
   
   pos = UnityObjectToClipPos(rotY * _VERTEX_);
   
   gl_Position = pos;
}"""

        var fragTex = """#Unity.h 
            
precision mediump float; 
varying vec4 pos;

varying vec2 _uv;

uniform sampler2D sTexture;

void main()
{
    gl_FragColor = tex2D(sTexture, _uv);
}"""


        val include1 = getInclude(activity!!.assets, "includes/unity.h")
        openGLView.renderer.setShaders(
            Utils.ShaderFileUtils.processInclude(include1, vertexTex),
            Utils.ShaderFileUtils.processInclude(include1, fragTex)
        )

        codeEditTex.setText(fragTex)

        var fragShaderFocused = true
        viewShader.setOnClickListener {
            fragShaderFocused = !fragShaderFocused

            if (fragShaderFocused) {
                codeEditTex.setText(fragTex)
            } else {
                codeEditTex.setText(vertexTex)
            }
        }

        fun enableDisableShaderEditor() {
            codeContainer.getViewById(R.id.iv_backgroundImage).isEnabled =
                !codeContainer.getViewById(R.id.iv_backgroundImage).isEnabled
            codeContainer.getViewById(R.id.et_fragmentCode).isEnabled =
                !codeContainer.getViewById(R.id.et_fragmentCode).isEnabled
            codeContainer.alpha = 1 - codeContainer.alpha
        }

        showHideButton.setOnClickListener {
            enableDisableShaderEditor()
        }

        enableDisableShaderEditor()

        button.setOnClickListener {

            if (fragShaderFocused) {
                fragTex = codeEditTex.editableText.toString()
            } else {
                vertexTex = codeEditTex.editableText.toString()
            }

            openGLView.renderer.setShaders(
                Utils.ShaderFileUtils.processInclude(include1, vertexTex),
                Utils.ShaderFileUtils.processInclude(include1, fragTex)
            )

            Toast.makeText(activity, "Compiled", Toast.LENGTH_SHORT).show()
            openGLView.clearFocus()
        }

        sceneObjManager = SceneObjectManager(activity, openGLView.renderer)
    }

    private fun getInclude(assets: AssetManager, include: String): String {
        val inStream = assets.open(include)
        val reader = BufferedReader(InputStreamReader(inStream))
        return reader.readText()
    }
}
package com.example.viewer3d

import android.content.Context
import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.viewer3d.engine.OpenGLView
import com.example.viewer3d.engine.Utils
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var openGLView: OpenGLView
    companion object {

         private lateinit var context: Context

        fun setContext(con: Context) {
            context=con
        }

        fun getContext() :  Context{
            return context;
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MainActivity.setContext(baseContext)

        openGLView = findViewById(R.id.OpenGLView_activity)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val button = findViewById<Button>(R.id.buttonCompile)
        val codeEditTex = findViewById<EditText>(R.id.et_fragmentCode)
        
        val showHideButton = findViewById<Button>(R.id.btn_showHide)
        val codeContainer = findViewById<ConstraintLayout>(R.id.codeContainer)
        val viewShader = findViewById<Button>(R.id.btn_switchShaderView)

        var vertexTex = """#include Unity.h 
            
attribute vec4 _VERTEX_; 
           
uniform mat4 _VP_;
uniform mat4 _M_;

uniform sampler2D _texture;

attribute vec2 _UV_;
varying vec2 _uv;
varying  vec4 pos;

void main() 
{
   pos = _VERTEX_;
   _uv = _UV_;
   gl_Position = _VP_ * _M_ * _VERTEX_;
}"""

        var fragTex = """precision mediump float; 
            
uniform vec4 vColor;
varying vec4 pos;
varying vec2 _uv;

uniform sampler2D sTexture;

void main()
{
    gl_FragColor = texture2D(sTexture, _uv);
}"""

       // vertexTex = Utils.ShaderUtils.processInclude(baseContext, vertexTex)


       val include1 = getInclude(assets,"includes/unity.h")
        openGLView.renderer.setShaders(Utils.ShaderUtils.processInclude(include1,vertexTex), Utils.ShaderUtils.processInclude(include1,fragTex))

        codeEditTex.setText(fragTex)

        var fragShaderFocused = true
        viewShader.setOnClickListener{
            fragShaderFocused = !fragShaderFocused

            if(fragShaderFocused){
                codeEditTex.setText(fragTex)
            }
            else {
                codeEditTex.setText(vertexTex)

            }
        }

        showHideButton.setOnClickListener {
            codeContainer.getViewById(R.id.iv_backgroundImage).isEnabled = !codeContainer.getViewById(R.id.iv_backgroundImage).isEnabled
            codeContainer.getViewById(R.id.et_fragmentCode).isEnabled = !codeContainer.getViewById(R.id.et_fragmentCode).isEnabled
            codeContainer.alpha = 1-codeContainer.alpha
            }


        button.setOnClickListener {

            if(fragShaderFocused) {
                fragTex =  codeEditTex.editableText.toString()
            }
            else {
                vertexTex =  codeEditTex.editableText.toString()
            }

            openGLView.renderer.setShaders(Utils.ShaderUtils.processInclude(include1,vertexTex), Utils.ShaderUtils.processInclude(include1,fragTex))
            Toast.makeText(this, "Compiled", Toast.LENGTH_SHORT).show()
            openGLView.clearFocus()
        }
    }

    private fun getInclude(assets: AssetManager, include:String) : String {
        val inStream = assets.open(include)
        val reader = BufferedReader(InputStreamReader(inStream))
        return reader.readText()
    }

    override fun onResume() {
        super.onResume()
        openGLView.onResume()
    }

    override fun onPause() {
        super.onPause()
        openGLView.onPause()
    }



}
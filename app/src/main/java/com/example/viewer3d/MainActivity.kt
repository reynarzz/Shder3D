package com.example.viewer3d

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.viewer3d.engine.OpenGLView

class MainActivity : AppCompatActivity() {

    private lateinit var openGLView: OpenGLView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openGLView = findViewById(R.id.OpenGLView_activity)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val button = findViewById<Button>(R.id.buttonCompile)
        val codeEditTex = findViewById<EditText>(R.id.et_fragmentCode)
        
        val showHideButton = findViewById<Button>(R.id.btn_showHide)
        val codeContainer = findViewById<ConstraintLayout>(R.id.codeContainer)
        val viewShader = findViewById<Button>(R.id.btn_switchShaderView)


        var vertexTex = """attribute vec4 _VERTEX_; 
            
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

        codeEditTex.setText(fragTex)
        button.setOnClickListener {

            if(fragShaderFocused) {
                fragTex = codeEditTex.editableText.toString()
            }
            else {
                vertexTex = codeEditTex.editableText.toString()
            }

            openGLView.renderer.setShaders(vertexTex, fragTex)
            Toast.makeText(this, "Compiled", Toast.LENGTH_SHORT).show()
            openGLView.clearFocus()

        }
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
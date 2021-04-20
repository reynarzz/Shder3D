package com.example.viewer3d

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.viewer3d.engine.OpenGLView
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var openGLView: OpenGLView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openGLView = findViewById(R.id.OpenGLView_activity)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val button = findViewById<Button>(R.id.buttonCompile)
        val fragmentTex = findViewById<EditText>(R.id.et_fragmentCode)
        
        val showHideButton = findViewById<Button>(R.id.btn_showHide)
        val codeContainer = findViewById<ConstraintLayout>(R.id.codeContainer)
        val viewShader = findViewById<Button>(R.id.btn_switchShaderView)

        viewShader.setOnClickListener{
            

        }

        showHideButton.setOnClickListener {
            codeContainer.getViewById(R.id.iv_backgroundImage).isEnabled = !codeContainer.getViewById(R.id.iv_backgroundImage).isEnabled
            codeContainer.getViewById(R.id.et_fragmentCode).isEnabled = !codeContainer.getViewById(R.id.et_fragmentCode).isEnabled
            codeContainer.alpha = 1-codeContainer.alpha
            }

        fragmentTex.setText(
                """precision mediump float;
uniform vec4 vColor;
varying vec4 pos;
varying vec2 _uv;
uniform sampler2D sTexture;

float linearize_depth(float d,float zNear,float zFar)
{
     return (2.0 * zNear) / (zFar + zNear - d * (zFar - zNear));
}
void main()
{
    gl_FragColor = texture2D(sTexture, _uv);
}""")

      //
        button.setOnClickListener{
            openGLView.renderer.setFragmentShader(fragmentTex.editableText.toString())
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
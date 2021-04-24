package com.reynarz.minityeditor.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.engine.OpenGLView
import com.reynarz.minityeditor.engine.Utils
import com.reynarz.minityeditor.files.FileManager
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader


class MainActivity : AppCompatActivity() {

    private lateinit var openGLView: OpenGLView

    companion object {
        var width = 0
        var height = 0

        private fun setScreenSize(width: Int, height: Int) {
            Companion.width = width
            Companion.height = height
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        setScreenSize(displayMetrics.widthPixels, displayMetrics.heightPixels)

        openGLView = findViewById(R.id.OpenGLView_activity)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val button = findViewById<Button>(R.id.buttonCompile)
        val codeEditTex = findViewById<EditText>(R.id.et_fragmentCode)

        val showHideButton = findViewById<Button>(R.id.btn_showHide)
        val codeContainer = findViewById<ConstraintLayout>(R.id.codeContainer)
        val viewShader = findViewById<Button>(R.id.btn_switchShaderView)

        requestPermissions()

        val file = FileManager(contentResolver)
        file.writeTest()
        file.readTest()

        val recycleView = findViewById<RecyclerView>(R.id.rv_fileManagerView)

        val adapter = GridAdapterView()
        val gridLayoutManager = GridLayoutManager(this, 4)

        gridLayoutManager.orientation = GridLayoutManager.VERTICAL
        gridLayoutManager.reverseLayout = false

        recycleView.layoutManager = gridLayoutManager
        recycleView.adapter = adapter


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

        // vertexTex = Utils.ShaderUtils.processInclude(baseContext, vertexTex)


        val include1 = getInclude(assets, "includes/unity.h")
        openGLView.renderer.setShaders(
            Utils.ShaderUtils.processInclude(include1, vertexTex),
            Utils.ShaderUtils.processInclude(include1, fragTex)
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

        showHideButton.setOnClickListener {
            codeContainer.getViewById(R.id.iv_backgroundImage).isEnabled =
                !codeContainer.getViewById(R.id.iv_backgroundImage).isEnabled
            codeContainer.getViewById(R.id.et_fragmentCode).isEnabled =
                !codeContainer.getViewById(R.id.et_fragmentCode).isEnabled
            codeContainer.alpha = 1 - codeContainer.alpha
        }


        button.setOnClickListener {

            if (fragShaderFocused) {
                fragTex = codeEditTex.editableText.toString()
            } else {
                vertexTex = codeEditTex.editableText.toString()
            }

            openGLView.renderer.setShaders(
                Utils.ShaderUtils.processInclude(include1, vertexTex),
                Utils.ShaderUtils.processInclude(include1, fragTex)
            )
            Toast.makeText(this, "Compiled", Toast.LENGTH_SHORT).show()
            openGLView.clearFocus()
        }
        val intent = Intent()
            .setAction(Intent.ACTION_OPEN_DOCUMENT)
            .setType("application/*")

        startActivityForResult(Intent.createChooser(intent, "Select a file"), 123)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code OPEN_DIRECTORY_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code OPEN_DIRECTORY_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.
        if (resultCode === RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"
            if (data != null && data!!.getData() != null) {

                val fileInputStream = contentResolver.openInputStream(data.getData()!!)
                val reader = InputStreamReader(fileInputStream)

            } else {
                Log.d("File Info", "File uri not found {}")
            }
        } else {
            Log.d("File Info", "User cancelled file browsing {}")
        }
    }

    private fun getInclude(assets: AssetManager, include: String): String {
        val inStream = assets.open(include)
        val reader = BufferedReader(InputStreamReader(inStream))
        return reader.readText()
    }

    private fun hasWriteExternalPermission() =
        ActivityCompat.checkSelfPermission(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    private fun hasReadExternalPermission() = ActivityCompat.checkSelfPermission(
        this, Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED


    private fun requestPermissions() {
        val permissionRequest = mutableListOf<String>()

        if (!hasWriteExternalPermission()) {
            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (!hasReadExternalPermission()) {
            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (permissionRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionRequest.toTypedArray(), 0)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 0 && grantResults.isNotEmpty()) {
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                    Log.d("Permission Request", "${permissions[i]} granted")
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        mutableListOf(permissions[i]).toTypedArray(),
                        0
                    )
                }
            }
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
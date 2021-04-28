package com.reynarz.minityeditor.views
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.model.MediaFile
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.engine.OpenGLView
import com.reynarz.minityeditor.models.SceneEntityData
import com.reynarz.minityeditor.viewmodels.ViewModelFactory


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

        openGLView = findViewById(R.id.OpenGLView_activity)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        setScreenSize(displayMetrics.widthPixels, displayMetrics.heightPixels)

        window.setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)



        val viewModelFactory = ViewModelFactory(this)

        val hierarhyViewModel = viewModelFactory.getHierarchyViewModel(mutableListOf(SceneEntityData("First entity")))

        val sceneFragment = SceneFragmentView()
        sceneFragment.hierarchyVM = hierarhyViewModel

        // editModel(viewModelFactory.getSceneEntityViewModel(SceneEntityData("Yeah")))
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainFragment, sceneFragment)
            commit()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // add a new Obj
        if (requestCode == 1 && data != null) {
            val files =
                data!!.getParcelableArrayListExtra<MediaFile>(FilePickerActivity.MEDIA_FILES)

            for (path in files!!) {
                openGLView.renderer.loadNewObjCommand(path.path)
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
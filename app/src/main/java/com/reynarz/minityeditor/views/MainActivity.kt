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
import com.reynarz.minityeditor.viewmodels.HierarchyViewModel
import com.reynarz.minityeditor.viewmodels.InspectorViewModel
import com.reynarz.minityeditor.viewmodels.ViewModelFactory


class MainActivity : AppCompatActivity() {

    private lateinit var openGLView: OpenGLView
    private val sceneFragment = SceneFragmentView()
    var selectedSceneEntity: SceneEntityData? = null
        private set

    companion object {
        var width = 0
            private set
        var height = 0
            private set

        lateinit var hierarchyViewModel: HierarchyViewModel
            private set

        lateinit var inspectorViewModel: InspectorViewModel
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openGLView = findViewById(R.id.OpenGLView_activity)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        width = displayMetrics.widthPixels
        height = displayMetrics.heightPixels

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setViewModels()
        showSceneFragment()
    }

    private fun setViewModels() {
        val viewModelFactory = ViewModelFactory(this)

        hierarchyViewModel = viewModelFactory.getHierarchyViewModel(getSceneEntitiesData())
        inspectorViewModel = viewModelFactory.getInspectorEntityViewModel()
    }

    private fun getSceneEntitiesData(): MutableList<SceneEntityData> {

        val entity1 = SceneEntityData("FirstEntity")
        val entity2 = SceneEntityData("AnotherSceneEntity")
        val entity3 = SceneEntityData("ThirdEntityHere")

        return mutableListOf(entity1, entity2, entity3)
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

    fun showSceneFragment() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainFragment, sceneFragment)
            commit()
        }
    }

    fun setSelectedEntity(sceneEntityData: SceneEntityData?) {
        selectedSceneEntity = sceneEntityData
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
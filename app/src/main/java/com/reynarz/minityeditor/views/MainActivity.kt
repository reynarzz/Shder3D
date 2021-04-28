package com.reynarz.minityeditor.views

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.model.MediaFile
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.engine.OpenGLView
import com.reynarz.minityeditor.models.MeshRendererComponentData
import com.reynarz.minityeditor.models.SceneEntityData
import com.reynarz.minityeditor.models.TransformComponentData
import com.reynarz.minityeditor.viewmodels.HierarchyViewModel
import com.reynarz.minityeditor.viewmodels.SceneEntityViewModel
import com.reynarz.minityeditor.viewmodels.ViewModelFactory


class MainActivity : AppCompatActivity() {

    private lateinit var openGLView: OpenGLView
    private val sceneFragment = SceneFragmentView()
    var selectedSceneEntity: SceneEntityViewModel? = null

        private set

    companion object {
        var width = 0
            private set
        var height = 0
            private set

        lateinit var hierarchyViewModel: HierarchyViewModel
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

        val viewModelFactory = ViewModelFactory(this)

        hierarchyViewModel = viewModelFactory.getHierarchyViewModel(getEntitiesViewModels(viewModelFactory))

        sceneFragment.hierarchyVM = hierarchyViewModel

        showSceneFragment()
    }

    private fun getEntitiesViewModels(viewModelFactory : ViewModelFactory) :MutableList<SceneEntityViewModel>{

        val testViewModel = ViewModelProvider(this).get(SceneEntityViewModel::class.java)

        testViewModel.selected.value = false
        testViewModel.visible.value = true
        testViewModel.entityName.value = "RandomName"
        testViewModel.componentsData.value = mutableListOf()
        testViewModel.componentsData.value!!.add(TransformComponentData())
        testViewModel.componentsData.value!!.add(MeshRendererComponentData())

        return mutableListOf(testViewModel)
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

    fun setSelectedEntity(sceneEntityViewModel: SceneEntityViewModel?) {
        selectedSceneEntity = sceneEntityViewModel
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
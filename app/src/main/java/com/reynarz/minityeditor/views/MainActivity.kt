package com.reynarz.minityeditor.views

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.model.MediaFile
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.engine.OpenGLView
import com.reynarz.minityeditor.engine.SceneObjectManager
import com.reynarz.minityeditor.engine.data.DataFactory
import com.reynarz.minityeditor.engine.data.ShaderDataBase
import com.reynarz.minityeditor.files.FileManager
import com.reynarz.minityeditor.models.MaterialData
import com.reynarz.minityeditor.models.SceneEntityData
import com.reynarz.minityeditor.viewmodels.HierarchyViewModel
import com.reynarz.minityeditor.viewmodels.InspectorViewModel
import com.reynarz.minityeditor.viewmodels.ViewModelFactory


class MainActivity : AppCompatActivity() {

    private lateinit var openGLView: OpenGLView
    private val sceneFragment = SceneFragmentView()
    private val shaderFragment = ShaderEditorFragment()
    private val inspectorFragment = InspectorFragmentView()
    private val fileManager = FileManager()

    private val sceneEntitiesDataInScene = mutableListOf<SceneEntityData>()

    lateinit var shaderDataBase: ShaderDataBase

    var dataFactory = DataFactory()

    var selectedSceneEntity: SceneEntityData? = null
        private set

    companion object {
        lateinit var instance: MainActivity
            private set
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

        initAllData()

        instance = this
        openGLView = findViewById(R.id.OpenGLView_activity)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        width = displayMetrics.widthPixels
        height = displayMetrics.heightPixels

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setViewModels()
        openSceneWindow()
    }

    private fun initAllData() {

        // set the shaderData.
        shaderDataBase = fileManager.loadShaderDatabase()
    }

    private fun setViewModels() {
        val viewModelFactory = ViewModelFactory(this)

        hierarchyViewModel = viewModelFactory.getHierarchyViewModel(sceneEntitiesDataInScene)
        inspectorViewModel = viewModelFactory.getInspectorEntityViewModel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Add new obj to database, and add it to the hierarchy immediately.
        if (requestCode == 1 && data != null) {
            val files = data!!.getParcelableArrayListExtra<MediaFile>(FilePickerActivity.MEDIA_FILES)

            for (path in files!!) {
                openGLView.renderer.addRenderCommand {

                    // create the data
                    val entity1 = SceneEntityData("Entity1")
                    entity1.entityID = path.path

                    // add the data to the list
                    sceneEntitiesDataInScene.add(entity1)

                    // load the object
                    SceneObjectManager(baseContext, openGLView.renderer).testLoadObject(entity1)
                }
            }
        }
    }

    fun openSceneWindow() {
        changeMainFragment(sceneFragment)
    }

    fun openShaderWindow(materialData: MaterialData) {
        shaderFragment.openGLView = openGLView
        shaderFragment.materialData = materialData

        changeMainFragment(shaderFragment)
    }

    fun openInspectorWindow() {
        changeMainFragment(inspectorFragment)
    }

    private fun changeMainFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainFragment, fragment)
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
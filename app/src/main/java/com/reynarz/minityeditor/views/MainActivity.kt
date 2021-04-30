package com.reynarz.minityeditor.views

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
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
    private lateinit var sceneObjectManager: SceneObjectManager

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

        instance = this
        openGLView = findViewById(R.id.OpenGLView_activity)

        sceneObjectManager = SceneObjectManager(baseContext, openGLView.renderer)
        shaderFragment.renderer = openGLView.renderer

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        width = displayMetrics.widthPixels
        height = displayMetrics.heightPixels

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        initAllData()

        setViewModels()
        openSceneWindow()
    }

    private fun initAllData() {
        // set the shaderData.
        shaderDataBase = fileManager.loadShaderDatabase()

        loadAllData()
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
                val entity = SceneEntityData("Entity1")
                entity.entityModelPath = path.path
                loadEntity(entity)
            }
        }
    }

    private fun loadEntity(entity: SceneEntityData) {


        openGLView.renderer.addRenderCommand {

            // add the data to the list, why inside here works? (if i put it outside the command doesn't work)
            sceneEntitiesDataInScene.add(entity)

            // load the object
            sceneObjectManager.testLoadObject(entity)
        }
    }

    fun openSceneWindow() {
        changeMainFragment(sceneFragment)
    }

    fun openShaderWindow(materialData: MaterialData) {
        shaderFragment.renderer = openGLView.renderer
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

        if (selectedSceneEntity != null) {

            openGLView.renderer.selectedEntityID = selectedSceneEntity!!.entityID
        } else {
            openGLView.renderer.selectedEntityID = "-1"
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

    fun saveAllData() {
        fileManager.saveEntities(sceneEntitiesDataInScene)
    }

    private fun loadAllData() {
        val entities = fileManager.loadEntities().sceneEntitiesDataInScene

        for (entity in entities) {

            if (entity.isSelected) {
                setSelectedEntity(entity)

            }

            loadEntity(entity)
            updateMaterials(entity)

        }
    }

    fun updateMaterials(sceneEntityData: SceneEntityData?) {
        openGLView.renderer.addRenderCommand {
            sceneObjectManager.addMaterial(sceneEntityData!!)
        }
    }
}
package com.reynarz.minityeditor.views

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.model.MediaFile
import com.reynarz.minityeditor.DefaultNavigator
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.engine.OpenGLView
import com.reynarz.minityeditor.engine.SceneObjectManager
import com.reynarz.minityeditor.engine.data.ShaderDataBase
import com.reynarz.minityeditor.files.FileManager
import com.reynarz.minityeditor.models.MaterialData
import com.reynarz.minityeditor.models.SceneEntityData
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private lateinit var openGLView: OpenGLView
    private lateinit var sceneFragment: SceneFragmentView
    private val shaderFragment = ShaderEditorFragment()
    private val fileManager = FileManager()

    private lateinit var sceneObjectManager: SceneObjectManager


    private val sceneEntitiesDataInScene = mutableListOf<SceneEntityData>()

    lateinit var shaderDataBase: ShaderDataBase

    var selectedSceneEntity: SceneEntityData? = null
        private set
    private val navigator: DefaultNavigator by inject()

    companion object {
        lateinit var instance: MainActivity
            private set

        var width = 0
            private set
        var height = 0
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        instance = this
        navigator.activity = this
        openGLView = findViewById(R.id.OpenGLView_activity)
        sceneFragment = SceneFragmentView()

        sceneObjectManager = SceneObjectManager(baseContext, openGLView.renderer)
        shaderFragment.renderer = openGLView.renderer

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        width = displayMetrics.widthPixels
        height = displayMetrics.heightPixels
        //Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

//        initAllData()
////
//        setViewModels()
//        openSceneWindow()
    }

    private fun initAllData() {
        // set the shaderData.
        shaderDataBase = fileManager.loadShaderDatabase()

        loadAllData()
    }

    private fun setViewModels() {
        //val viewModelFactory = ViewModelFactory(this)

        //hierarchyViewModel = viewModelFactory.getHierarchyViewModel(sceneEntitiesDataInScene)
        //inspectorViewModel = viewModelFactory.getInspectorEntityViewModel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Add new obj to database, and add it to the hierarchy immediately.
        if (requestCode == 1 && data != null) {
            val files = data!!.getParcelableArrayListExtra<MediaFile>(FilePickerActivity.MEDIA_FILES)

            for (path in files!!) {
                val entity = get<SceneEntityData>()
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
            sceneObjectManager.addMaterial(entity)
        }
    }

    fun openSceneWindow() {
        changeMainFragment(sceneFragment)
    }

    fun openShaderWindow(materialData: MaterialData) {
//        shaderFragment.renderer = openGLView.renderer
//        shaderFragment.materialData = materialData
//
//        changeMainFragment(shaderFragment)
    }

    private fun changeMainFragment(fragment: Fragment) {

        // android 5 problem
//        supportFragmentManager.beginTransaction().apply {
//            replace(R.id.mainFragment, fragment)
//            commit()
//        }
    }

    fun setSelectedEntity(sceneEntityData: SceneEntityData?) {
        selectedSceneEntity = sceneEntityData

        if (selectedSceneEntity != null) {

            openGLView.renderer.selectEntityID(selectedSceneEntity!!.entityID)
        } else {
            openGLView.renderer.selectEntityID(null)
        }
    }


    fun saveAllData() {
        fileManager.saveEntities(sceneEntitiesDataInScene)
    }

    private fun loadAllData() {
        //val entities = fileManager.loadProject().sceneEntitiesDataInScene

//        for (entity in entities) {
//
//            if (entity.isSelected) {
//                setSelectedEntity(entity)
//            }
//
//            loadEntity(entity)
//        }
    }

    fun updateMaterials(sceneEntityData: SceneEntityData?) {
        openGLView.renderer.addRenderCommand {
            sceneObjectManager.addMaterial(sceneEntityData!!)
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
package com.reynarz.minityeditor.views

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.model.MediaFile
import com.reynarz.minityeditor.DefaultNavigator
import com.reynarz.minityeditor.MinityProjectRepository
import com.reynarz.minityeditor.R
import com.reynarz.minityeditor.engine.OpenGLView
import com.reynarz.minityeditor.engine.SceneObjectManager
import com.reynarz.minityeditor.engine.Utils
import com.reynarz.minityeditor.models.SceneEntityData
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    lateinit var openGLView: OpenGLView

    private lateinit var sceneObjectManager: SceneObjectManager

    private val navigator: DefaultNavigator by inject()

    companion object {
        lateinit var instance: MainActivity
            private set
        var time = System.currentTimeMillis()
        var width = 0
            private set
        var height = 0
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println("Current MainActivity thread: " + Thread.currentThread().name)

        instance = this
        navigator.activity = this
        openGLView = findViewById(R.id.OpenGLView_activity)

        sceneObjectManager = SceneObjectManager(this, openGLView.renderer)
        //shaderFragment.renderer = openGLView.renderer

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        width = displayMetrics.widthPixels
        height = displayMetrics.heightPixels
        //Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        initAllData()


    }

    private fun initAllData() {
        loadAllData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Add new obj to database, and add it to the hierarchy immediately.
        if (data != null) {
            val files = data!!.getParcelableArrayListExtra<MediaFile>(FilePickerActivity.MEDIA_FILES)

            if (requestCode == 1) {
                onModelSelectedToLoad(files)
            } else if (requestCode == 2) {
                onTextureSelected(files)
            }
        }
    }

    private fun onModelSelectedToLoad(files: ArrayList<MediaFile>?) {
        for (path in files!!) {
            val entity = get<SceneEntityData>()
            entity.entityModelPath = path.path


            val repository: MinityProjectRepository = get()
            repository.getProjectData().sceneEntities.add(entity)

            Log.d("total entities count", repository.getProjectData().sceneEntities.size.toString())

            for (i in repository.getProjectData().sceneEntities) {
                Log.d("Patht", i.entityModelPath)

            }

            loadCustomEntity(entity)
        }
    }

    private fun onTextureSelected(files: ArrayList<MediaFile>?) {

        // just one texture per slot
        val textureMedia = files?.get(0)

        val repository: MinityProjectRepository = get()
        val currentTextureSlot = repository.selectedMaterial?.texturesData!![repository.selectedTextureSlot]

        currentTextureSlot.path = textureMedia?.path!!
        openGLView.renderer.setTextureCommand(currentTextureSlot)

        Log.d("texture selected ${repository.selectedTextureSlot}", textureMedia?.path?.toString()!!)
    }

    private fun loadAllData() {
        val repository: MinityProjectRepository = get()
        val project = repository.getProjectData()

        repository.colorsPickupTableRBG = Utils.getPickingRGBLookUpTable(200)

        loadCameraEntity(project.defaultSceneEntities[0])

        // revisit this in case pick up crash.


//        for(defEntities in project.defaultSceneEntities){
//        }

        for (entity in project.sceneEntities) {
            loadCustomEntity(entity)
        }
    }

    private fun loadCameraEntity(entity: SceneEntityData) {
        openGLView.renderer.addRenderCommand {

            sceneObjectManager.recreateCameraEntity(entity)
        }
    }

    private fun loadCustomEntity(entity: SceneEntityData) {

        openGLView.renderer.addRenderCommand {

            sceneObjectManager.testLoadObject(entity)
        }
    }

    fun updateMaterial(sceneEntityData: SceneEntityData?, matIndex: Int) {
        openGLView.renderer.addRenderCommand {
            sceneObjectManager.addMaterial(sceneEntityData!!, matIndex)
        }
    }

    fun updateTransform(sceneEntityData: SceneEntityData?) {
        openGLView.renderer.setTransform(sceneEntityData!!)
    }

    fun removeMaterial(sceneEntityData: SceneEntityData?, matIndex: Int) {
        openGLView.renderer.addRenderCommand {
            sceneObjectManager.removeMaterial(sceneEntityData, matIndex)
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

    override fun onBackPressed() {

        super.onBackPressed()
    }

    fun removeSceneEntity(entity: SceneEntityData?) {
        openGLView.renderer.addRenderCommand {
            openGLView.renderer.scene.removeSceneEntityByID(entity?.entityID)
        }
    }
}
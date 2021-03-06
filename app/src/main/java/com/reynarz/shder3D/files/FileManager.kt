package com.reynarz.shder3D.files

import android.os.Build
import android.os.Environment
import android.util.Log
import com.reynarz.shder3D.MinityProjectRepository
import com.reynarz.shder3D.engine.Utils
import com.reynarz.shder3D.engine.data.ShaderDataBase
import com.reynarz.shder3D.models.*
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.koin.java.KoinJavaComponent.get

class FileManager {

    val minityRootFolder = ".MinityEditor"
    val minityShadersFolderName = "Shaders"
    val minityEntitiesFolderName = "Entities"

    val minityShadersFolderFullPath = "${minityRootFolder}${File.separator}Shaders"
    val minityMaterialsFolder = "${minityRootFolder}${File.separator}Shaders"

    companion object {
        lateinit var instance: FileManager
            private set
    }

    init {
        instance = this
    }


    fun writeTest() {
        val directory = File(
            Environment.getExternalStorageDirectory().absolutePath + File.separator,
            minityRootFolder
        )

        val file = File(directory, "Scene1.minity")

        directory.mkdir()

        //var j = Gson()
        //val json = j.toJson(MaterialData())
        //val materialData = j.fromJson("", MaterialData::class.java)

        file.createNewFile()
        file.writeText("hellow")
        //createOrGetFile(file, "0_engine3D_.txt", "")
    }

    fun readTest() {
        val sdcardPath = Environment.getExternalStorageDirectory().absolutePath + File.separator

        val file = File(sdcardPath + minityRootFolder, "Scene1.minity")
        val text = file.readText()
        Log.d("text of:", text)
    }

    fun readFile(fullPath: String): BufferedReader {
        //val file = File(fullPath)

        //Log.d("obj log", file.readText())
        return BufferedReader(InputStreamReader(FileInputStream(fullPath)))
    }

    fun readFileText(fullpath: String): String {
        return File(fullpath).readText()
    }

    fun loadShaderDatabase(): ShaderDataBase {
//        val file = getFile(minityShadersFolderName, "ShaderDatabase.data")
//
//        return if (file.exists()) {
//            Gson().fromJson(file.toString(), ShaderDataBase::class.java)
//        } else {
//
//        }
//
        return ShaderDataBase()
    }

    private fun getFile(directoy: String, file: String): File {

        val root = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File(Environment.getExternalStorageDirectory().absolutePath + File.separator, minityRootFolder)
        } else {
            File(Environment.getExternalStorageDirectory().absolutePath + File.separator, minityRootFolder)

        }

        if(!root.exists()){
            root.mkdir()
            println("root dir created")
        }

        return File(Environment.getExternalStorageDirectory().absolutePath + File.separator + minityRootFolder + File.separator + directoy, file)
    }

    fun saveEntities(sceneEntitiesDataInScene: MutableList<SceneEntityData>) {
        val directory = getFile(minityRootFolder, "$minityEntitiesFolderName")
        val file = File(directory, "$minityEntitiesFolderName.txt")

        if (!directory.exists()) {
            directory.mkdir()

            file.createNewFile()
        }

        file.writeText(Json.encodeToString((sceneEntitiesDataInScene)))
    }

    fun saveCurrentProject() {
        val directory = getFile("", "$minityEntitiesFolderName")
        val file = File(directory, "$minityEntitiesFolderName.txt")

        if (!directory.exists()) {
            directory.mkdir()

            file.createNewFile()
        }

        val repository: MinityProjectRepository = get(MinityProjectRepository::class.java)

        val project = repository.getProjectData()

        //test
        file.writeText(Json.encodeToString(project))
        println("Project saved")
    }

    fun loadProject(): ProjectData {
        val directory = getFile("", "$minityEntitiesFolderName")
        val file = File(directory, "$minityEntitiesFolderName.txt")
        Log.d("loadproject", "load")

        return if (file.exists()) {
           return Json.decodeFromString(file.readText())

        } else {
            ProjectData("randomName").also {
                var cameraSceneEntity = SceneEntityData("Camera", TransformComponentData(), MeshRendererComponentData().also {
                    it.name = "Image Effects"
                    val screenQuadShaderCode = Utils.getScreenQuadShaderCode()
                    val shaderData = get<ShaderData>(ShaderData::class.java)

                    shaderData.vertexShader = screenQuadShaderCode.first
                    shaderData.fragmentShader = screenQuadShaderCode.second

                    it.materialsData.add(MaterialData("Screen Material", shaderData))
                })
                var directionalLightEntity = SceneEntityData("DirectionalLight", TransformComponentData(), MeshRendererComponentData())

                cameraSceneEntity.entityType = EntityType.Editor
                directionalLightEntity.entityType = EntityType.Editor

                it.defaultSceneEntities.add(cameraSceneEntity)
                it.defaultSceneEntities.add(directionalLightEntity)
            }
        }

    }
}
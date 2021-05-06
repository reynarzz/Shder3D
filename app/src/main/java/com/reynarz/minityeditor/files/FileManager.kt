package com.reynarz.minityeditor.files

import android.os.Environment
import android.util.Log
import com.reynarz.minityeditor.MinityProjectRepository
import com.reynarz.minityeditor.engine.Utils
import com.reynarz.minityeditor.engine.data.ShaderDataBase
import com.reynarz.minityeditor.models.*
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
        return File(Environment.getExternalStorageDirectory().absolutePath + File.separator + minityRootFolder + File.separator + directoy, file)
    }

    fun saveEntities(sceneEntitiesDataInScene: MutableList<SceneEntityData>) {
        val directory = getFile("", "$minityEntitiesFolderName")
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
    }

    fun loadProject(): ProjectData {
        val directory = getFile("", "$minityEntitiesFolderName")
        val file = File(directory, "$minityEntitiesFolderName.txt")
        Log.d("loadproject", "load")

        return if (file.exists()) {
            val project = Json.decodeFromString<ProjectData>(file.readText())


            var cameraSceneEntity = SceneEntityData("Camera", TransformComponentData(), MeshRendererComponentData().also {
                val screenQuadShaderCode = Utils.getScreenQuadShaderCode()
                val shaderData = get<ShaderData>(ShaderData::class.java)

                shaderData.vertexShader = screenQuadShaderCode.first
                shaderData.fragmentShader = screenQuadShaderCode.second

                it.materialsData.add(MaterialData("Screen Material", shaderData))
            })

            var directionalLightEntity = SceneEntityData("DirectionalLight", TransformComponentData(), MeshRendererComponentData())
            project.defaultSceneEntities.add(cameraSceneEntity)
            project.defaultSceneEntities.add(directionalLightEntity)
            return project
            //  Log.d("Matfound", obj[0].meshRendererData.materialsData.size.toString())

        } else {
            ProjectData("randomName").also {
                var cameraSceneEntity = SceneEntityData("Camera", TransformComponentData(), MeshRendererComponentData().also {
                    val screenQuadShaderCode = Utils.getScreenQuadShaderCode()
                    val shaderData = get<ShaderData>(ShaderData::class.java)

                    shaderData.vertexShader = screenQuadShaderCode.first
                    shaderData.fragmentShader = screenQuadShaderCode.second

                    it.materialsData.add(MaterialData("Screen Material", shaderData))
                })
                var directionalLightEntity = SceneEntityData("DirectionalLight", TransformComponentData(), MeshRendererComponentData())


                it.defaultSceneEntities.add(cameraSceneEntity)
                it.defaultSceneEntities.add(directionalLightEntity)
            }
        }

    }
}
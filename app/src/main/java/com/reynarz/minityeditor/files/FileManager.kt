package com.reynarz.minityeditor.files

import android.content.ContentResolver
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.reynarz.minityeditor.engine.data.ShaderDataBase
import com.reynarz.minityeditor.models.ShaderData
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

class FileManager {

    val minityRootFolder = ".MinityEditor"
    val minityShadersFolderName = "Shaders"

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
        val file = File(fullPath)

        //Log.d("obj log", file.readText())
        return BufferedReader(InputStreamReader(FileInputStream(fullPath)))
    }

    fun loadShaderDatabase(): ShaderDataBase {
        val file = getFile(minityShadersFolderName, "ShaderDatabase.data")

        return if (file.exists()) {
            Gson().fromJson(file.toString(), ShaderDataBase::class.java)
        } else {
            ShaderDataBase()
        }
    }

    private fun getFile(directoy: String, file: String): File {
        return File(Environment.getExternalStorageDirectory().absolutePath + File.separator + minityRootFolder + File.separator + directoy, file)
    }

//    fun <T> deserializeData(data: String): T{
//        val gson = Gson()
//        return gson.fromJson<T>(data, T)
//    }

    //fun readObj(fullPath: String) = File(fullPath).readLines()
}
package com.reynarz.minityeditor.files

import android.content.ContentResolver
import android.os.Environment
import android.util.Log
import java.io.File

class FileManager(private val contentResolver: ContentResolver) {

    val minityRootFolder = "MinityEditor"

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

        val file = File(sdcardPath +minityRootFolder,"Scene1.minity" )
        val text = file.readText()
        Log.d( "text of:", text)
    }

}
package com.example.viewer3d.files

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.example.viewer3d.engine.data.MaterialData
import com.google.gson.Gson
import java.io.File
import java.io.FileDescriptor
import java.io.IOException

class FileManager(private val contentResolver: ContentResolver) {

    val minityRootFolder = ".MinityEditor"

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
package com.example.viewer3d.engine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.opengl.GLES20.*
import android.opengl.GLUtils
import java.io.ByteArrayOutputStream

class Texture {
    private var _textureID: IntArray? = null

    val textureID: Int
        get() {
            return _textureID!![0]
        }

//    constructor(bitmap: Bitmap) {
//
//    }

    constructor(internalFormat : Int, format: Int, type:Int, width : Int, height: Int, filter: Int){
        // Generate a texture to hold the colour buffer

        _textureID = IntArray(1)

        glGenTextures(1, _textureID, 0);
        glBindTexture(GL_TEXTURE_2D, _textureID!![0]);

        // Width and height do not have to be a power of two
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat,
                width, height,
                0, format, type, null);

//      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
//      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);

    }
    constructor(context: Context, path: String) {
        loadTexture(context, path)
    }

    private fun loadTexture(context: Context, path: String) {

        var bitmap = doInBackground(context, path)

        val imageArray = imageToBitmap(bitmap)
        bitmap = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.size)

        bitmap = createFlippedBitmap(bitmap, false, true)

         _textureID = IntArray(1)
        glGenTextures(1, _textureID, 0)

        glBindTexture(GL_TEXTURE_2D, _textureID!![0])
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
    }

    private fun createEmptyTexture() {

    }

    fun createFlippedBitmap(source: Bitmap, xFlip: Boolean, yFlip: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.postScale(if (xFlip) -1f else 1f, if (yFlip) -1f else 1f, source.width / 2f, source.height / 2f)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun doInBackground(context: Context, path: String): Bitmap {

        val imageAsset = context.assets.open(path)
        //val `in` = java.net.URL(imageURL).openStream()
        val image = BitmapFactory.decodeStream(imageAsset)

        return Bitmap.createBitmap(image)
    }

    fun imageToBitmap(bitmap: Bitmap): ByteArray {

        val stream = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

        return stream.toByteArray()
    }

}
package com.reynarz.shder3D.engine

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

    //TODO
//    constructor(bitmap: Bitmap) {
//
//    }

    constructor(internalFormat: Int, format: Int, type: Int, width: Int, height: Int, filter: Int, clampParams: Int) {
        // Generate a texture to hold the colour buffer

        _textureID = IntArray(1)

        glGenTextures(1, _textureID, 0)
        glBindTexture(GL_TEXTURE_2D, _textureID!![0])

        // Width and height do not have to be a power of two
        glTexImage2D(
            GL_TEXTURE_2D, 0, internalFormat,
            width, height,
            0, format, type, null
        )

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, clampParams)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, clampParams)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter)

        glBindTexture(GL_TEXTURE_2D, 0)
    }

    constructor(context: Context, path: String) {

        var bitmap = doInBackground(context, path)

        loadTexture(bitmap, GL_CLAMP_TO_EDGE)
    }

    constructor(bitmap: Bitmap, clampingGLParam: Int) {
        loadTexture(bitmap, clampingGLParam)
    }

    private fun loadTexture(bitmap: Bitmap, clampingGLParam: Int) {
        var editableBitmap = bitmap

        val imageArray = bitmapToImageArray(editableBitmap)

        editableBitmap = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.size)

        editableBitmap = createFlippedBitmap(editableBitmap, false, true)

        _textureID = IntArray(1)
        glGenTextures(1, _textureID, 0)

        glBindTexture(GL_TEXTURE_2D, _textureID!![0])
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, clampingGLParam)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, clampingGLParam)

        GLUtils.texImage2D(GL_TEXTURE_2D, 0, editableBitmap, 0)
    }

    fun unloadTexture(textureId: Int) {
        val textures = IntArray(1)
        textures[0] = textureId
        glDeleteTextures(1, textures, 0)
    }

    private fun createFlippedBitmap(source: Bitmap, xFlip: Boolean, yFlip: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.postScale(
            if (xFlip) -1f else 1f,
            if (yFlip) -1f else 1f,
            source.width / 2f,
            source.height / 2f
        )
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun doInBackground(context: Context, path: String): Bitmap {

        val imageAsset = context.assets.open(path)
        //val `in` = java.net.URL(imageURL).openStream()
        val image = BitmapFactory.decodeStream(imageAsset)

        return Bitmap.createBitmap(image)
    }

    private fun bitmapToImageArray(bitmap: Bitmap): ByteArray {

        val stream = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

        return stream.toByteArray()
    }

    //other things are using the texture slot 0 and 1 (shadow map etc..)
    fun bind(textureIndex: Int) {
        glActiveTexture(GL_TEXTURE0 + textureIndex)
        glBindTexture(GL_TEXTURE_2D, textureID)
    }

    fun unbind() {
        glBindTexture(GL_TEXTURE_2D, 0)
    }
}
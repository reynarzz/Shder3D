package com.reynarz.shder3D.models

enum class FileItemType {
    Model, Texture, Material, Shader, Scene, Directory
}

data class FileData(val fileName: String, var fileIcon: Int, val itemType: FileItemType)
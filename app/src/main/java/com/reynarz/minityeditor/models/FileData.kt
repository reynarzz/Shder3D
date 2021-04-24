package com.reynarz.minityeditor.models

enum class FileItemType {
    Model, Texture, Material, Shader, Scene, Directory
}

data class FileData(val fileName: String, var fileIcon: Int, val itemType: FileItemType)
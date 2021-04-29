package com.reynarz.minityeditor.models

data class ShaderData(var shaderName: String, val shaderID: String) {
    var vertexShader = String()
    var fragmentShader = String()
}
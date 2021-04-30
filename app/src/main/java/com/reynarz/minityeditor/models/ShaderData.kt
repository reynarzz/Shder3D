package com.reynarz.minityeditor.models

import kotlinx.serialization.Serializable

@Serializable
data class ShaderData(var shaderName: String, val shaderID: String) {
    var vertexShader = String()
    var fragmentShader = String()
}
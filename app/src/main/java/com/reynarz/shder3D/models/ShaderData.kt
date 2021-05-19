package com.reynarz.shder3D.models

import kotlinx.serialization.Serializable

@Serializable
data class ShaderData(var shaderName: String, var shaderID: String) {
    var vertexShader = String()

    var fragmentShader = String()
}
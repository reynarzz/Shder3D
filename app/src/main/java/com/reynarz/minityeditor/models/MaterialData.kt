package com.reynarz.minityeditor.models

import android.graphics.Bitmap
import android.opengl.GLES20
import com.reynarz.minityeditor.engine.vec3
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class MaterialData(var materialDataId: String, var shaderData: ShaderData) {
    //    //Ka
//    var ambientColor = vec3()
//
//    //Kd
//    var diffuseColor = vec3()
//
//    //Ks
//    var specularColor = vec3()
//
//    //Ns
//    var specularHighlight = 0f
//
//    //Ni (indexRefraction)
//    var opticalDensity = 0f
//
//    //d (alpha)
//    var disolve = 0f
    var name = "Material"

    //--var texturesDataID = mutableListOf<String>()
    var texturesData = mutableListOf<TextureData>()

    var materialConfig = MaterialConfig()
}

@Serializable
class MaterialConfig {
    var gl_blendingEnabled = false
    var gl_srcFactor = 0
    var gl_dstFactor = 0

    var gl_depthTestEnabled = true
    var gl_depthFunc = GLES20.GL_LEQUAL

    var gl_cullEnabled = false
    var gl_cullFace = GLES20.GL_BACK

    // Queue {Background, }                                 // ver2
    // Blend {SRCALPHA/OneMinusSRCAlpha, etc..}
    // ZWrite/DepthTesting {ON/Off} (Depth testing)         // ver2
    // Cull {Front/Back/Off} (Cull)                         // ver2
    // ZTest/Depth func {LEQUAL/etc..} // Depth func        // ver2
    //Stencil
}
package com.reynarz.minityeditor.models

import android.graphics.Bitmap
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


}
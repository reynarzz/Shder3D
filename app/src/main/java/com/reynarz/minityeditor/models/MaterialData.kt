package com.reynarz.minityeditor.models

import com.reynarz.minityeditor.engine.vec3
import com.reynarz.minityeditor.engine.components.*

class MaterialData(val materialDataId: String) : Entity() {

    //Ka
    var ambientColor = vec3()

    //Kd
    var diffuseColor = vec3()

    //Ks
    var specularColor = vec3()

    //Ns
    var specularHighlight = 0f

    //Ni (indexRefraction)
    var opticalDensity = 0f

    //d (alpha)
    var disolve = 0f

    var shaderId = String()
    var texturesDataID = mutableListOf<Int>()
}
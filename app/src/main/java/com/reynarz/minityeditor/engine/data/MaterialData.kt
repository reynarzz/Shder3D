package com.reynarz.minityeditor.engine.data

import com.reynarz.minityeditor.engine.vec3
import com.reynarz.minityeditor.engine.components.*

class MaterialData : Entity() {

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

    var shaderId = Int
    var texturesDataID = mutableListOf<Int>()
}
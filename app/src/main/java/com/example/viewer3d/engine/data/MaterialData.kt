package com.example.viewer3d.engine.data

import com.example.viewer3d.engine.Vec3
import com.example.viewer3d.engine.components.*

class MaterialData : Entity() {

    //Ka
    var ambientColor = Vec3()

    //Kd
    var diffuseColor = Vec3()

    //Ks
    var specularColor = Vec3()

    //Ns
    var specularHighlight = 0f

    //Ni (indexRefraction)
    var opticalDensity = 0f

    //d (alpha)
    var disolve = 0f

    var shaderId = Int
    var texturesDataID = mutableListOf<Int>()
}
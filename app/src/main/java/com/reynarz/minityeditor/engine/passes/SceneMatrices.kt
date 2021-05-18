package com.reynarz.minityeditor.engine.passes

import com.reynarz.minityeditor.engine.Scene


class SceneMatrices(scene: Scene) {
    val directionalLightProjM = scene.directionalLight.getProjectionM()
    val directionalLightVIewM = scene.directionalLight.getLightViewMatrix()
    val directionalLightVIewProjM = scene.directionalLight.getViewProjLight()

    val cameraProjM = scene.editorCamera?.projectionM
    val cameraViewM = scene.editorCamera?.viewM
    val cameraProjMInvP = scene.editorCamera?.projectionMInv
}
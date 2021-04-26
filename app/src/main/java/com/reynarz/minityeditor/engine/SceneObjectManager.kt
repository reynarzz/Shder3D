package com.reynarz.minityeditor.engine

import android.content.Context
import com.reynarz.minityeditor.engine.components.MeshRenderer
import com.reynarz.minityeditor.engine.components.SceneEntity

class SceneObjectManager(
    private val context: Context?,
    private val openGlRenderer: OpenGlRenderer
) {

    fun testLoadObject(objectPath: String) {

        val rawObj = ObjParser(objectPath)

        var objData = rawObj.getModelData()
        val mesh = Mesh(objData.mVertices, objData.mIndices, objData.mUVs)

        val mat = Utils.getDefaultMaterial()
        mat.addTexture(Texture(context!!, "textures/girltex_small.jpg"))

        val renderer = MeshRenderer(mesh, mat)

        val sceneEntity = SceneEntity()
        sceneEntity.name = "Girl object"
        sceneEntity.testMeshRenderer = renderer


        val bounding = boundingBoxTest(objData.boundingBox)

        val sceneEntity2 = SceneEntity()
        sceneEntity2.name = "Bounds"
        sceneEntity2.testMeshRenderer = bounding


        openGlRenderer.scene.entities.add(sceneEntity)
        openGlRenderer.scene.entities.add(sceneEntity2)
    }

    fun boundingBoxTest(boundingBox: BoundingBox): MeshRenderer {

        return MeshRenderer(Mesh(boundingBox.verts!!, IntArray(1), FloatArray(1)), Utils.getUnlitMaterial())
    }

}
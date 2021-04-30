package com.reynarz.minityeditor.engine

import android.content.Context
import com.reynarz.minityeditor.engine.components.MeshRenderer
import com.reynarz.minityeditor.engine.components.SceneEntity
import com.reynarz.minityeditor.engine.data.ModelsDataBase
import com.reynarz.minityeditor.models.SceneEntityData

class SceneObjectManager(
    private val context: Context?,
    private val openGLRenderer: OpenGLRenderer
) {

    fun testLoadObject(sceneEntityData: SceneEntityData) {

        val dataBase = ModelsDataBase()
        val modelData = dataBase.getModel(sceneEntityData.entityID)

        var objData = modelData
        val mesh = Mesh(objData.mVertices, objData.mIndices, objData.mUVs)

//        val mat = Utils.getDefaultMaterial()
//        mat.addTexture(Texture(context!!, "textures/girltex_small.jpg"))

        val renderer = MeshRenderer(mesh, null)

        val sceneEntity = SceneEntity()
        sceneEntity.name = sceneEntityData.name
        sceneEntity.testMeshRenderer = renderer

//        val bounding = boundingBoxTest(objData.bounds)
//
//        val boundingBox = SceneEntity()
//        boundingBox.name = "Bounds"
//        boundingBox.testMeshRenderer = bounding
//        openGLRenderer.scene.entities.add(boundingBox)

        openGLRenderer.scene.entities.add(sceneEntity)
    }

    fun boundingBoxTest(bounds: Bounds): MeshRenderer {

        var cube = ObjParser(context!!, "models/cube.obj").getModelData()

        val mesh = Mesh(bounds.verts!!, bounds.indices!!, FloatArray(1))
        //val mesh2 = Mesh(cube.mVertices, cube.mIndices, cube.mUVs)

        return MeshRenderer(mesh, Utils.getUnlitMaterial())
    }
}
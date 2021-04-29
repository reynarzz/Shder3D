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
       // val rawObj = ObjParser(objectPath)

        var objData = modelData//rawObj.getModelData()
        val mesh = Mesh(objData.mVertices, objData.mIndices, objData.mUVs)

        val mat = Utils.getDefaultMaterial()
        mat.addTexture(Texture(context!!, "textures/girltex_small.jpg"))

        val renderer = MeshRenderer(mesh, mat)

        val sceneEntity = SceneEntity()
        sceneEntity.name = "Girl object"
        sceneEntity.testMeshRenderer = renderer


        val bounding = boundingBoxTest(objData.bounds)

        val sceneEntity2 = SceneEntity()
        sceneEntity2.name = "Bounds"
        sceneEntity2.testMeshRenderer = bounding


        openGLRenderer.scene.entities.add(sceneEntity)
        openGLRenderer.scene.entities.add(sceneEntity2)
    }

    fun boundingBoxTest(bounds: Bounds): MeshRenderer {

        var cube = ObjParser(context!!, "models/cube.obj").getModelData()

        val mesh  = Mesh(bounds.verts!!, bounds.indices!!, FloatArray(1))
        //val mesh2 = Mesh(cube.mVertices, cube.mIndices, cube.mUVs)

        return MeshRenderer(mesh, Utils.getUnlitMaterial())
    }

}
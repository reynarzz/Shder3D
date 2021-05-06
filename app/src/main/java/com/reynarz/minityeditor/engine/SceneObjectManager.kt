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

        //        val mat = Utils.getDefaultMaterial()
//        mat.addTexture(Texture(context!!, "textures/girltex_small.jpg"))

        // val renderer = MeshRenderer(mesh, null)

        val sceneEntity = SceneEntity()

        sceneEntity.entityID = sceneEntityData.entityID
        sceneEntity.name = sceneEntityData.name
        val addedRenderer = sceneEntity.addComponent(MeshRenderer::class.java)

        addedRenderer.transform.position = sceneEntityData.transformData.position
        addedRenderer.transform.eulerAngles = sceneEntityData.transformData.eulerAngles
        addedRenderer.transform.scale = sceneEntityData.transformData.scale


        if (!sceneEntityData.entityModelPath.isEmpty()) {
            val dataBase = ModelsDataBase()
            val modelData = dataBase.getModel(sceneEntityData.entityModelPath)

            var objData = modelData
            val mesh = Mesh(objData.mVertices, objData.mIndices, objData.mUVs, objData.mNormals)
            addedRenderer!!.mesh = mesh
        } else {
            //empty mesh testing
            addedRenderer!!.mesh = Mesh(FloatArray(1), IntArray(1), FloatArray(1), FloatArray(1))
        }


        //addedRenderer!!.material = mat

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

        val mesh = Mesh(bounds.verts!!, bounds.indices!!, FloatArray(1), FloatArray(1))
        //val mesh2 = Mesh(cube.mVertices, cube.mIndices, cube.mUVs)

        return MeshRenderer(mesh, Utils.getUnlitMaterial(0.75f))
    }

    fun addMaterial(sceneEntityData: SceneEntityData) {

        val entity = openGLRenderer.scene.getEntityById(sceneEntityData.entityID)
        val materialData = sceneEntityData.meshRendererData.materialsData.getOrNull(0)

        if (materialData != null && entity != null) {
            val shaderData = materialData.shaderData
            val material = Material(Shader(shaderData.vertexShader, shaderData.fragmentShader))

            entity.getComponent(MeshRenderer::class.java)!!.material = material

            for (textureData in materialData.texturesData) {

                if (textureData.path != null) {
                    val bitmap = Utils.getBitmapFromPath(textureData.path!!)
                    material.textures?.add(Texture(bitmap))

                    textureData.previewBitmap = bitmap
                }
            }
        }
    }

    fun removeMaterial(sceneEntityData: SceneEntityData) {
        val entity = openGLRenderer.scene.getEntityById(sceneEntityData.entityID)
        entity?.getComponent(MeshRenderer::class.java)!!.material = null
    }

    fun recreateCameraEntity(sceneEntityData: SceneEntityData) {
        val cameraEntity = SceneEntity()
        val meshRenderer = cameraEntity.addComponent(MeshRenderer::class.java)
        meshRenderer.mesh = Utils.getScreenSizeQuad()

        for (materialData in sceneEntityData.meshRendererData.materialsData) {

            val material = Material(Shader(materialData.shaderData.vertexShader, materialData.shaderData.fragmentShader))
            meshRenderer.materials.add(material)
        }

        openGLRenderer.cameraEntity = cameraEntity
    }
}
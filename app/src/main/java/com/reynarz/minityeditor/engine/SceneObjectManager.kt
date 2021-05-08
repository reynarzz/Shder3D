package com.reynarz.minityeditor.engine

import androidx.appcompat.app.AppCompatActivity
import com.reynarz.minityeditor.engine.components.MeshRenderer
import com.reynarz.minityeditor.engine.components.SceneEntity
import com.reynarz.minityeditor.engine.data.ModelsDataBase
import com.reynarz.minityeditor.models.SceneEntityData

class SceneObjectManager(
    private val activity: AppCompatActivity?,
    private val openGLRenderer: OpenGLRenderer
) {

    fun testLoadObject(sceneEntityData: SceneEntityData) {

        //        val mat = Utils.getDefaultMaterial()
//        mat.addTexture(Texture(context!!, "textures/girltex_small.jpg"))

        // val renderer = MeshRenderer(mesh, null)

        val sceneEntity = SceneEntity()

        sceneEntity.isActive = sceneEntityData.active
        sceneEntity.entityID = sceneEntityData.entityID
        sceneEntity.name = sceneEntityData.name
        val addedRenderer = sceneEntity.addComponent(MeshRenderer::class.java)

        addedRenderer.transform.position = sceneEntityData.transformData.position
        addedRenderer.transform.eulerAngles = sceneEntityData.transformData.eulerAngles
        addedRenderer.transform.scale = sceneEntityData.transformData.scale

        if (sceneEntityData.entityModelPath.isNotEmpty()) {
            val dataBase = ModelsDataBase()
            val modelsData = dataBase.getModels(sceneEntityData.entityModelPath)
            val meshes = mutableListOf<Mesh>()

            for (model in modelsData) {
                meshes.add(Mesh(model.mVertices, model.mIndices, model.mUVs, model.mNormals))
            }

            addedRenderer!!.meshes = meshes
        } else {
            //empty mesh testing
            addedRenderer!!.meshes = mutableListOf(Mesh(FloatArray(1), IntArray(1), FloatArray(1), FloatArray(1)))
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

//    fun boundingBoxTest(bounds: Bounds): MeshRenderer {
//
//        var cube = ObjParser(activity!!.baseContext!!, "models/cube.obj").getModelData()
//
//        val mesh = Mesh(bounds.verts!!, bounds.indices!!, FloatArray(1), FloatArray(1))
//        //val mesh2 = Mesh(cube.mVertices, cube.mIndices, cube.mUVs)
//
//        return MeshRenderer(mesh, Utils.getUnlitMaterial(0.75f))
//    }

    fun addMaterial(sceneEntityData: SceneEntityData) {

        val entity = openGLRenderer.scene.getEntityById(sceneEntityData.entityID)

        for (materialData in sceneEntityData.meshRendererData.materialsData) {
            if (entity != null) {
                val shaderData = materialData.shaderData

                val vertex = Utils.processMinityInclude(activity!!, shaderData.vertexShader)
                val fragment = Utils.processMinityInclude(activity!!, shaderData.fragmentShader)


                val material = Material(Shader(vertex, fragment))

                entity.getComponent(MeshRenderer::class.java)!!.materials.add(material)

                for (textureData in materialData.texturesData) {

                    if (textureData.path != null) {
                        val bitmap = Utils.getBitmapFromPath(textureData.path!!)
                        material.textures?.add(Texture(bitmap))

                        textureData.previewBitmap = bitmap
                    }
                }
            }
        }
    }

    fun removeMaterial(sceneEntityData: SceneEntityData, index: Int) {

        val entity = openGLRenderer.scene.getEntityById(sceneEntityData.entityID)
        entity?.getComponent(MeshRenderer::class.java)!!.materials.removeAt(index)
    }

    // this contains duplicated code
    fun recreateCameraEntity(sceneEntityData: SceneEntityData) {
        val cameraEntity = SceneEntity()
        val meshRenderer = cameraEntity.addComponent(MeshRenderer::class.java)
        meshRenderer.meshes = mutableListOf(Utils.getScreenSizeQuad())

        for (materialData in sceneEntityData.meshRendererData.materialsData) {
            val vertex = Utils.processMinityInclude(activity!!, materialData.shaderData.vertexShader)
            val fragment = Utils.processMinityInclude(activity!!, materialData.shaderData.fragmentShader)

            val material = Material(Shader(vertex, fragment))
            meshRenderer.materials.add(material)

            for (textureData in materialData.texturesData) {

                if (textureData.path != null) {
                    val bitmap = Utils.getBitmapFromPath(textureData.path!!)
                    material.textures?.add(Texture(bitmap))

                    textureData.previewBitmap = bitmap
                }
            }
        }

        openGLRenderer.cameraEntity = cameraEntity
    }
}
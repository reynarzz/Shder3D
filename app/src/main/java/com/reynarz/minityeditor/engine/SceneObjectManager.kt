package com.reynarz.minityeditor.engine

import android.opengl.GLES20
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.reynarz.minityeditor.engine.components.MeshRenderer
import com.reynarz.minityeditor.engine.components.SceneEntity
import com.reynarz.minityeditor.engine.data.ModelsDataBase
import com.reynarz.minityeditor.models.MaterialData
import com.reynarz.minityeditor.models.SceneEntityData
import org.koin.java.KoinJavaComponent.get

class SceneObjectManager(
    private val activity: AppCompatActivity?,
    private val openGLRenderer: OpenGLRenderer
) {

    fun testLoadObject(sceneEntityData: SceneEntityData) {

        val sceneEntity = SceneEntity()

        sceneEntity.name = sceneEntityData.name
        sceneEntity.entityID = sceneEntityData.entityID
        sceneEntity.isActive = sceneEntityData.active

        val addedRenderer = sceneEntity.addComponent(MeshRenderer::class.java)

        addedRenderer.transform.position = sceneEntityData.transformData.position
        addedRenderer.transform.eulerAngles = sceneEntityData.transformData.eulerAngles
        addedRenderer.transform.scale = sceneEntityData.transformData.scale

        if (sceneEntityData.entityModelPath.isNotEmpty()) {
            val dataBase = ModelsDataBase()
            val modelsData = dataBase.getModels(sceneEntityData.entityModelPath)
            val meshes = mutableListOf<Mesh>()

            var index = 0
            for (model in modelsData) {
                meshes.add(Mesh(model.mVertices, model.mIndices, model.mUVs, model.mNormals))

                if (sceneEntityData.meshRendererData.materialsData.size - 1 < index) {

                    val materialData = get<MaterialData>(MaterialData::class.java)
                    //materialData.name = model.modelName
                    sceneEntityData.meshRendererData.materialsData.add(null)
                }

                index++
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

        addMaterials(sceneEntityData)
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

    private fun addMaterials(sceneEntityData: SceneEntityData) {
        val entity = openGLRenderer.scene.getEntityById(sceneEntityData.entityID)
        val meshRenderer = entity?.getComponent(MeshRenderer::class.java)

        for ((index, materialData) in sceneEntityData.meshRendererData.materialsData.withIndex()) {
            addMaterial(meshRenderer, materialData, index)
        }
    }

    fun addMaterial(sceneEntityData: SceneEntityData?, matIndex: Int) {
        val entity = openGLRenderer.scene.getEntityById(sceneEntityData!!.entityID)
        val meshRenderer = entity?.getComponent(MeshRenderer::class.java)
        println("add1")
        val material = sceneEntityData.meshRendererData.materialsData[matIndex]
        addMaterial(meshRenderer, material, matIndex)
    }

    private fun addMaterial(meshRenderer: MeshRenderer?, materialData: MaterialData?, matIndex: Int) {
        if (meshRenderer != null) {
            if (materialData != null) {
                val shaderData = materialData?.shaderData

                val vertex = Utils.processMinityInclude(activity!!, shaderData.vertexShader)
                val fragment = Utils.processMinityInclude(activity!!, shaderData.fragmentShader)

                val material = Material(Shader(vertex, fragment))
                material.id = materialData.materialDataId

                if (meshRenderer!!.materials.size - 1 < matIndex) {
                    meshRenderer!!.materials.add(material)
                } else {
                    meshRenderer!!.materials[matIndex] = material
                }

                for (textureData in materialData.texturesData) {

                    if (textureData.path != null) {
                        val bitmap = Utils.getBitmapFromPath(textureData.path!!)
                        material.textures?.add(Texture(bitmap, GLES20.GL_REPEAT))

                        textureData.previewBitmap = bitmap
                    }
                }
            } else {
                if (meshRenderer!!.materials.size - 1 < matIndex) {
                    meshRenderer!!.materials.add(null)
                } else {
                    meshRenderer!!.materials[matIndex] = null
                }
            }
        }
    }

    fun removeMaterial(sceneEntityData: SceneEntityData?, index: Int) {
        Log.d("materials-deleteAt: ", index.toString())

        val entity = openGLRenderer.scene.getEntityById(sceneEntityData?.entityID)
        entity?.getComponent(MeshRenderer::class.java)!!.materials[index] = null
    }

    // This contains duplicated code
    fun recreateCameraEntity(sceneEntityData: SceneEntityData) {
        val cameraEntity = SceneEntity()
        val meshRenderer = cameraEntity.addComponent(MeshRenderer::class.java)
        meshRenderer.meshes = mutableListOf(Utils.getScreenSizeQuad())

        for (materialData in sceneEntityData.meshRendererData.materialsData) {
            val vertex = Utils.processMinityInclude(activity!!, materialData?.shaderData?.vertexShader!!)
            val fragment = Utils.processMinityInclude(activity!!, materialData?.shaderData?.fragmentShader!!)

            val material = Material(Shader(vertex, fragment))
            material.id = materialData?.materialDataId!!
            meshRenderer.materials.add(material)

            for (textureData in materialData.texturesData) {
                if (textureData.path != null) {
                    val bitmap = Utils.getBitmapFromPath(textureData.path!!)
                    material.textures?.add(Texture(bitmap, GLES20.GL_REPEAT))

                    textureData.previewBitmap = bitmap
                }
            }
        }

        openGLRenderer.cameraEntity = cameraEntity
    }
}
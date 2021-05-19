package com.reynarz.minityeditor.engine

import android.opengl.GLES20
import androidx.appcompat.app.AppCompatActivity
import com.reynarz.minityeditor.engine.components.MeshRenderer
import com.reynarz.minityeditor.engine.components.SceneEntity
import com.reynarz.minityeditor.engine.data.ModelsDataBase
import com.reynarz.minityeditor.models.MaterialData
import com.reynarz.minityeditor.models.SceneEntityData

class SceneObjectManager(
    private val activity: AppCompatActivity?,
    private val openglView: OpenGLView
) {

    fun testLoadObject(sceneEntityData: SceneEntityData) {

        println("to here")
        val sceneEntity = SceneEntity(sceneEntityData)

        sceneEntity.name = sceneEntityData.name
        sceneEntity.entityID = sceneEntityData.entityID

        val addedRenderer = sceneEntity.addComponent(MeshRenderer::class.java)

        addedRenderer.transform.position = sceneEntityData.transformData.position
        addedRenderer.transform.eulerAngles = sceneEntityData.transformData.eulerAngles
        addedRenderer.transform.scale = sceneEntityData.transformData.scale

        //   GlobalScope.launch(Dispatchers.Main) {
        if (sceneEntityData.entityModelPath.isNotEmpty()) {

            val dataBase = ModelsDataBase()
            val meshes = mutableListOf<Mesh>()
            var modelsData: List<ModelData>? = null

            //   withContext(Dispatchers.Default) {
            modelsData = dataBase.getModels(sceneEntityData.entityModelPath)
            println("Loaded")
            //     }

            println("To process")

            var index = 0
            for (model in modelsData!!) {
                meshes.add(Mesh(model.mVertices, model.mIndices, model.mUVs, model.mNormals).also { it.meshName = model.modelName })

                if (sceneEntityData.meshRendererData.materialsData.size - 1 < index) {

//                        val materialData = get<MaterialData>(MaterialData::class.java)
//                        materialData.name = model.modelName
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

// bug
        //  withContext(Dispatchers.Main) {
        //openGLRenderer.addRenderCommand {
        println("to add")
        //sceneEntity.entityData.meshRendererData.materialsData[0].materialConfig.renderQueue


        openglView.renderer.scene.entities.add(sceneEntity)

        addMaterials(sceneEntityData)
        println("Add materials")

        //  }
        //}
        //}
        // TESTING REFACTOR THIS
        for (i in addedRenderer.meshes.indices) {
            println("to add")
            openglView.renderer.newRenderer?.addToRenderQueue(QueuedRenderableMesh(sceneEntity, addedRenderer.transform.modelM, addedRenderer.meshes[i], addedRenderer.materials[i]))
        }
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
        val entity = openglView.renderer.scene.getEntityById(sceneEntityData.entityID)
        val meshRenderer = entity?.getComponent(MeshRenderer::class.java)

        for ((index, materialData) in sceneEntityData.meshRendererData.materialsData.withIndex()) {
            addMaterial(meshRenderer, materialData, index)
        }
    }

    fun addMaterial(sceneEntityData: SceneEntityData?, matIndex: Int) {
        val entity = openglView.renderer.scene.getEntityById(sceneEntityData!!.entityID)
        val meshRenderer = entity?.getComponent(MeshRenderer::class.java)

        val materialData = sceneEntityData.meshRendererData.materialsData[matIndex]
        addMaterial(meshRenderer, materialData, matIndex)

//        for (i in addedRenderer.meshes.indices) {
//            println("to add")
//            openglView.renderer.newRenderer?.addToRenderQueue(QueuedRenderableMesh(sceneEntity, addedRenderer.transform.modelM, addedRenderer.meshes[i], addedRenderer.materials[i]))
//        }
    }

    private fun addMaterial(meshRenderer: MeshRenderer?, materialData: MaterialData?, matIndex: Int) {
        if (meshRenderer != null) {
            if (materialData != null) {
                val shaderData = materialData?.shaderData

                val vertex = Utils.processMinityInclude(activity!!, shaderData.vertexShader)
                val fragment = Utils.processMinityInclude(activity!!, shaderData.fragmentShader)

                val material = Material(Shader(vertex, fragment))
                material.id = materialData.materialDataId
                material.materialData = materialData

                println("Meshes: " + meshRenderer.meshes.size)

                if (meshRenderer.meshes.getOrNull(matIndex) != null) {
                    materialData.name = meshRenderer.meshes.getOrNull(matIndex)?.meshName!!
                    println("Name: " + materialData.name)
                }

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

        val entity = openglView.renderer.scene.getEntityById(sceneEntityData?.entityID)
        entity?.getComponent(MeshRenderer::class.java)!!.materials[index] = null
    }

    // This contains duplicated code
    fun recreateCameraEntity(sceneEntityData: SceneEntityData) {
        val cameraEntity = SceneEntity(sceneEntityData)
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

        openglView.renderer.cameraEntity = cameraEntity
    }
}
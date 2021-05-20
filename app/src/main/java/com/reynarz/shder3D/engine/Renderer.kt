package com.reynarz.shder3D.engine

import android.opengl.GLES20
import com.reynarz.shder3D.MinityProjectRepository
import com.reynarz.shder3D.engine.components.MeshRenderer
import com.reynarz.shder3D.engine.passes.RenderPass
import com.reynarz.shder3D.engine.passes.RenderPassFrameBuffers
import com.reynarz.shder3D.engine.passes.SceneMatrices
import org.koin.java.KoinJavaComponent.get

class Renderer(private val sceneMatrices: SceneMatrices) {
    private var errorMaterial: Material
    private var renderKeysOrdered = listOf<Int>()

    //bad
    val repository = get<MinityProjectRepository>(MinityProjectRepository::class.java)

    private val phases = mutableListOf<RenderPass>()
    private val passFrameBuffers = RenderPassFrameBuffers()

    private val beforeDrawScreenQuadRenderers = mutableListOf<MeshRenderer?>()
    var target: ScreenQuad? = null

    val forNowCommands_REMOVE = mutableListOf<() -> Unit>()

    init {

        errorMaterial = Utils.getErrorMaterial()

        getEditorStuff_Remove()
    }

    fun getEditorStuff_Remove() {
        val groundShaderCode = Utils.getGroundShadersCode()

        val material = Material(Shader(groundShaderCode.first, groundShaderCode.second))

        val plane = Utils.getPlane(2000f)
        //plane.bind(material.program)
        val meshRenderer = MeshRenderer(mutableListOf(plane), material)

        beforeDrawScreenQuadRenderers!!.add(meshRenderer)
    }

    fun addToRenderBeforeQueued(meshRenderer: MeshRenderer) {
        beforeDrawScreenQuadRenderers.add(meshRenderer)
    }

    fun addToRenderQueue(queuedMesh: QueuedRenderableMesh) {

        if (!repository.queuedRenderers.containsKey(queuedMesh?.renderQueue)) {
            repository.queuedRenderers[queuedMesh.renderQueue] = mutableListOf()
        }

        repository.queuedRenderers[queuedMesh.renderQueue]?.add(queuedMesh)
    }

//    fun replaceRenderQueue(queuedMesh: QueuedRenderableMesh) {
//        removeRendererOfQueue(queuedMesh)
//
//        // add to the new queue.
//        if (!rendersMap.containsKey(newRenderQueue)) {
//            rendersMap.set(newRenderQueue, mutableListOf())
//        }
//    }

    fun removeRendererOfQueue(queueValue: Int, entityID: String) {
        forNowCommands_REMOVE.add {
            val entities = repository.queuedRenderers[queueValue]!!

            if (repository.queuedRenderers.containsKey(queueValue)) {
                for (i in entities.size - 1 downTo 0) {
                    val entity = entities[i]
                    if (entity.entityID == entityID) {
                        entities.remove(entity)
                    }
                }
            }
        }
    }

    fun removeCompleteEntity(entityID: String) {
        forNowCommands_REMOVE.add {
            repository.removeCompleteEntity(entityID)
        }
    }

    fun addPass(pass: RenderPass) {
        phases.add(pass)
    }

    fun removePass(pass: RenderPass) {
        phases.remove(pass)
    }

    private fun command_REmoveTHIS() {
        for (i in forNowCommands_REMOVE) {
            i()
        }
        forNowCommands_REMOVE.clear()
    }

    fun draw() {

        command_REmoveTHIS()

        if (renderKeysOrdered.size != repository.queuedRenderers.size) {
            renderKeysOrdered = repository.queuedRenderers.keys.sortedBy { it }
        }

        for (p in phases) {
            p.clear()
        }




        for (p in phases) {
            for (key in renderKeysOrdered) {
                println("Key: " + key)
                p.renderPass(repository.queuedRenderers[key]!!, sceneMatrices, errorMaterial!!, passFrameBuffers)
            }
        }

        if (passFrameBuffers.mainFrameBufferPass != null){
            // editor pass
            passFrameBuffers.mainFrameBufferPass?.bind()
            for (i in beforeDrawScreenQuadRenderers.indices) {
                beforeDrawScreenQuadRenderers[i]?.bind(sceneMatrices.cameraViewM, sceneMatrices.cameraProjM, errorMaterial, i)
                GLES20.glDrawElements(GLES20.GL_TRIANGLES, beforeDrawScreenQuadRenderers[i]!!.meshes[i].indicesCount, GLES20.GL_UNSIGNED_INT, beforeDrawScreenQuadRenderers[i]!!.meshes[i].indexBuffer)
            }
            passFrameBuffers.mainFrameBufferPass?.unBind()

            //draw in target (screen quad)
            target?.draw(passFrameBuffers.mainFrameBufferPass!!, errorMaterial!!)

        }
    }
}
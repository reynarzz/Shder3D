package com.reynarz.shder3D.engine

import com.reynarz.shder3D.MinityProjectRepository
import com.reynarz.shder3D.engine.components.MeshRenderer
import com.reynarz.shder3D.engine.passes.RenderPass
import com.reynarz.shder3D.engine.passes.RenderPassFrameBuffers
import com.reynarz.shder3D.engine.passes.SceneMatrices
import org.koin.java.KoinJavaComponent.get

class Renderer(private val sceneMatrices: SceneMatrices) {
    private var errorMaterial: Material

    //bad
    val repository = get<MinityProjectRepository>(MinityProjectRepository::class.java)

    private val phases = mutableListOf<RenderPass>()
    private val passFrameBuffers = RenderPassFrameBuffers()

    private val beforeMeshRenderers = mutableListOf<MeshRenderer?>()
    var target: ScreenQuad? = null

    val forNowCommands_REMOVE = mutableListOf<() -> Unit>()

    init {

        errorMaterial = Utils.getErrorMaterial()
    }

    fun addToRenderBeforeQueued(meshRenderer: MeshRenderer) {
        beforeMeshRenderers.add(meshRenderer)
    }

    fun addToRenderQueue(queuedMesh: QueuedRenderableMesh) {

        forNowCommands_REMOVE.add {
            if (!repository.queuedRenderers.containsKey(queuedMesh?.RenderQueue)) {
                repository.queuedRenderers[queuedMesh.RenderQueue] = mutableListOf()
            }
            repository.queuedRenderers[queuedMesh.RenderQueue]?.add(queuedMesh)
        }
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

        for (i in beforeMeshRenderers.indices) {
            beforeMeshRenderers[i]?.bind(sceneMatrices.cameraViewM, sceneMatrices.cameraProjM, errorMaterial, i)
        }

        for (p in phases) {
            for (key in repository.queuedRenderers.keys) {

                //println("Key: " + key)
                p.renderPass(repository.queuedRenderers[key]!!, sceneMatrices, errorMaterial!!, passFrameBuffers)
            }
        }

        if (passFrameBuffers.mainFrameBufferPass != null)
            target?.draw(passFrameBuffers.mainFrameBufferPass!!, errorMaterial!!)
    }
}
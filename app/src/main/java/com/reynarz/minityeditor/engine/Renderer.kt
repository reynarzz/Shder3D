package com.reynarz.minityeditor.engine

import com.reynarz.minityeditor.MinityProjectRepository
import com.reynarz.minityeditor.engine.components.MeshRenderer
import com.reynarz.minityeditor.engine.passes.RenderPass
import com.reynarz.minityeditor.engine.passes.RenderPassFrameBuffers
import com.reynarz.minityeditor.engine.passes.SceneMatrices
import org.koin.java.KoinJavaComponent.get

class Renderer(private val sceneMatrices: SceneMatrices) {
    private var errorMaterial: Material
    //bad
    val repository = get<MinityProjectRepository>(MinityProjectRepository::class.java)

    private val phases = mutableListOf<RenderPass>()
    private val passFrameBuffers = RenderPassFrameBuffers()

    private val beforeMeshRenderers = mutableListOf<MeshRenderer?>()
    var target: ScreenQuad? = null

    init {

        errorMaterial = Utils.getErrorMaterial()
    }

    fun addToRenderBeforeQueued(meshRenderer: MeshRenderer) {
        beforeMeshRenderers.add(meshRenderer)
    }

    fun addToRenderQueue(queuedMesh: QueuedRenderableMesh) {

        if (!repository.queuedRenderers.containsKey(queuedMesh?.RenderQueue)) {
            repository.queuedRenderers[queuedMesh.RenderQueue] = mutableListOf()
        }
        repository.queuedRenderers[queuedMesh.RenderQueue]?.add(queuedMesh)
    }

//    fun replaceRenderQueue(queuedMesh: QueuedRenderableMesh) {
//        removeRendererOfQueue(queuedMesh)
//
//        // add to the new queue.
//        if (!rendersMap.containsKey(newRenderQueue)) {
//            rendersMap.set(newRenderQueue, mutableListOf())
//        }
//    }

    fun removeRendererOfQueue(queuedMesh: QueuedRenderableMesh) {
        if (repository.queuedRenderers.containsKey(queuedMesh.RenderQueue)) {
            // remove from the old render queue.
            repository.queuedRenderers[queuedMesh.RenderQueue]?.remove(queuedMesh)

            // If there is not entities in this queue, clear,
            if (repository.queuedRenderers[queuedMesh.RenderQueue]?.size == 0) {
                repository.queuedRenderers.remove(queuedMesh.RenderQueue)
            }
        }
    }

    fun addPass(pass: RenderPass) {
        phases.add(pass)
    }

    fun removePass(pass: RenderPass) {
        phases.remove(pass)
    }

    fun draw() {

        for (i in beforeMeshRenderers.indices) {
            beforeMeshRenderers[i]?.bind(sceneMatrices.cameraViewM, sceneMatrices.cameraProjM, errorMaterial, i)
        }

        for (p in phases) {
            for (key in repository.queuedRenderers.keys) {
                println("Key: " + key)
                p.renderPass(repository.queuedRenderers[key]!!, sceneMatrices, errorMaterial!!, passFrameBuffers)
            }
        }

        if (passFrameBuffers.mainFrameBufferPass != null)
            target?.draw(passFrameBuffers.mainFrameBufferPass!!, errorMaterial!!)
    }
}
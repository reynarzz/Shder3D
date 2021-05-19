package com.reynarz.minityeditor.engine

import com.reynarz.minityeditor.engine.components.MeshRenderer
import com.reynarz.minityeditor.engine.passes.RenderPass
import com.reynarz.minityeditor.engine.passes.RenderPassFrameBuffers
import com.reynarz.minityeditor.engine.passes.SceneMatrices

class Renderer(private val sceneMatrices: SceneMatrices) {
    private var errorMaterial: Material
    private val rendersMap = mutableMapOf<Int, MutableList<QueuedRenderableMesh>>()
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

        if (!rendersMap.containsKey(queuedMesh?.RenderQueue)) {
            rendersMap[queuedMesh.RenderQueue] = mutableListOf()
        }
        rendersMap[queuedMesh.RenderQueue]?.add(queuedMesh)
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
        if (rendersMap.containsKey(queuedMesh.RenderQueue)) {
            // remove from the old render queue.
            rendersMap[queuedMesh.RenderQueue]?.remove(queuedMesh)

            // If there is not entities in this queue, clear,
            if (rendersMap[queuedMesh.RenderQueue]?.size == 0) {
                rendersMap.remove(queuedMesh.RenderQueue)
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
            for (key in rendersMap.keys) {
                //println("Key: " + key)
                p.renderPass(rendersMap[key]!!, sceneMatrices, errorMaterial!!, passFrameBuffers)
            }
        }

        target?.draw(passFrameBuffers.mainFrameBufferPass!!, errorMaterial!!)
    }
}
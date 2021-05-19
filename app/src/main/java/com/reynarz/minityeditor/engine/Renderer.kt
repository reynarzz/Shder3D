package com.reynarz.minityeditor.engine

import com.reynarz.minityeditor.engine.components.SceneEntity
import com.reynarz.minityeditor.engine.passes.RenderPass
import com.reynarz.minityeditor.engine.passes.SceneMatrices

class Renderer(private val sceneMatrices: SceneMatrices) {
    private lateinit var errorMaterial: Material
    private val rendersMap = mutableMapOf<Int, MutableList<QueuedRenderableMesh>>()
    private val phases = mutableListOf<RenderPass>()

    init {
        errorMaterial = Utils.getErrorMaterial()
    }

    fun addToRenderQueue(queuedMesh: QueuedRenderableMesh) {

        println("add----------------------------------------")

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
        for (p in phases) {
            for (key in rendersMap.keys) {
                println("Key: " + key)
                p.renderPass(rendersMap[key]!!, sceneMatrices, errorMaterial!!)
            }
        }
    }
}
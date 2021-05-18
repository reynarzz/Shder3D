package com.reynarz.minityeditor.engine

import com.reynarz.minityeditor.engine.components.SceneEntity
import com.reynarz.minityeditor.engine.passes.RenderPass
import com.reynarz.minityeditor.engine.passes.SceneMatrices

class Renderer(private val sceneMatrices: SceneMatrices) {
    private var errorMaterial: Material
    private val rendersMap = mutableMapOf<Int, MutableList<SceneEntity?>>()
    private val prePhases = mutableListOf<RenderPass>()


    init {
        errorMaterial = Utils.getErrorMaterial()
    }

    fun addToRenderQueue(sceneEntity: SceneEntity?, renderQueue: Int) {

        if (!rendersMap.containsKey(renderQueue)) {
            rendersMap.set(renderQueue, mutableListOf())
        }

        rendersMap[renderQueue]?.add(sceneEntity)
    }

    fun replaceRenderQueue(sceneEntity: SceneEntity?, oldRenderQueue: Int, newRenderQueue: Int) {

        removeRendererOfQueue(sceneEntity, oldRenderQueue)

        // add to the new queue.
        if (!rendersMap.containsKey(newRenderQueue)) {
            rendersMap.set(newRenderQueue, mutableListOf())
        }
    }

    fun removeRendererOfQueue(sceneEntity: SceneEntity?, rendererQueue: Int) {
        if (rendersMap.containsKey(rendererQueue)) {
            // remove from the old render queue.
            rendersMap[rendererQueue]?.remove(sceneEntity)

            // If there is not entities in this queue, clear,
            if (rendersMap[rendererQueue]?.size == 0) {
                rendersMap.remove(rendererQueue)
            }
        }
    }

    fun addPrePass(pass: RenderPass) {
        prePhases.add(pass)
    }

    fun removePrePass(pass: RenderPass) {
        prePhases.remove(pass)
    }

    fun draw() {
        for (p in prePhases) {
            for (key in rendersMap.keys) {

                p.renderPass(rendersMap[key]!!, sceneMatrices, errorMaterial)
            }
        }
    }
}
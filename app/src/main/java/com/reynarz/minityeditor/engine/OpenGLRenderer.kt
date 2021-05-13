package com.reynarz.minityeditor.engine

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.reynarz.minityeditor.MinityProjectRepository
import com.reynarz.minityeditor.engine.components.MeshRenderer
import com.reynarz.minityeditor.engine.components.SceneEntity
import com.reynarz.minityeditor.engine.components.Transform
import com.reynarz.minityeditor.models.*
import com.reynarz.minityeditor.views.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.get
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.abs


class OpenGLRenderer(val context: Context) : GLSurfaceView.Renderer {

    var onEntitySelected: ((Boolean) -> Unit)? = null

    var twoFingersDir = vec3()
    private lateinit var cameraTransformData: TransformComponentData
    lateinit var touchPointer: TouchPointer
    private var sceneEntitiesData: List<SceneEntityData>? = null
    lateinit var colorPickerPixelBuffer: ByteBuffer

    val scene: Scene = Scene()
    var rot = vec3(0f, 0f, 0f)
    var zoom = 1f
    var twoFingersNormalizedDir = vec3()
    var twoFingersNormalizedDirPrev = vec3()

    var initialized = false
    lateinit var cameraEntity: SceneEntity

    private val rendererCommands = mutableListOf<() -> Unit>()
    private lateinit var errorMaterial: Material
    private var editorObjs: MutableList<MeshRenderer>? = null

    var lStartTime = System.currentTimeMillis().toFloat()

    lateinit var mainFrameBuffer: FrameBuffer
    lateinit var shadowMapFrameBuffer: FrameBuffer
    lateinit var colorPickerFrameBuffer: FrameBuffer

    private lateinit var lightTransform: Transform

    private lateinit var lightObj: MeshRenderer

    private lateinit var pickupMaterial: Material

    private val repository: MinityProjectRepository = get(MinityProjectRepository::class.java)
    private val rendersMap = mutableMapOf<Int, MutableList<SceneEntity?>>()
    private val moveMultiplier = 0.1f
    private val rotateMultiplier = 0.4f

    private val selectedEntity: SceneEntity?
        get() {
            var entity: SceneEntity? = null
            var entityData = repository.selectedSceneEntity

            if (entityData?.entityType == EntityType.User) {
                entity = scene.getEntityById(entityData?.entityID)
            } else if (entityData?.entityType == EntityType.Editor) {
                entity = cameraEntity
            }
            if (entityData != null)
                entity?.isActive = entityData.active

            return entity
        }

    private lateinit var outlineMaterial: Material

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

    }


    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

        if (!initialized) {
            editorObjs = mutableListOf()

            outlineMaterial = Utils.getUnlitMaterial(1f)
            errorMaterial = Utils.getErrorMaterial()
            pickupMaterial = Material(Utils.getPickupShader().run {
                Shader(first, second)
            })

            colorPickerPixelBuffer = ByteBuffer.allocateDirect(16).run {
                order(ByteOrder.nativeOrder())
            }

            glEnable(GL_DEPTH_TEST)
            glDepthFunc(GL_LEQUAL)
            //glEnable(GL_BLEND)
            //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

            cameraTransformData = repository.getProjectData().cameraTransformData

            scene!!.editorCamera!!.updateProjection(width, height)
            scene!!.editorCamera?.transform?.position = cameraTransformData.position
            rot = cameraTransformData.eulerAngles
            zoom = cameraTransformData.scale.x

            mainFrameBuffer = FrameBuffer(MainActivity.width, MainActivity.height)
            mainFrameBuffer.genNormalFrameBuffer(GL_REPEAT)

            shadowMapFrameBuffer = FrameBuffer(MainActivity.width, MainActivity.height)
            shadowMapFrameBuffer.genBufferForDepth()


            colorPickerFrameBuffer = FrameBuffer(MainActivity.width, MainActivity.height)
            colorPickerFrameBuffer.genNormalFrameBuffer(GL_CLAMP_TO_EDGE)

            initialized = true

            touchPointer = TouchPointer(scene!!.editorCamera!!)
            sceneEntitiesData = repository.getProjectData().sceneEntities

            println("Current Opengl thread: " + Thread.currentThread().name)
            getEditorStuff_Test()

//            var lightEntity = SceneEntity()
//            val meshRenderer = lightEntity.addComponent(MeshRenderer::class.java)
//            meshRenderer.mesh = Utils.getQuad(2f)
//            meshRenderer.materials.add(Utils.getErrorMaterial())
//            meshRenderer.transform.modelM = scene.directionalLight.getLightViewMatrix()
//
//            lightTransform = meshRenderer.transform
//            scene.entities.add(lightEntity)
        }
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

    fun getEditorStuff_Test() {
        val groundShaderCode = Utils.getGroundShadersCode()

        val material = Material(Shader(groundShaderCode.first, groundShaderCode.second))

        val plane = Utils.getPlane(2000f)
        //plane.bind(material.program)
        val meshRenderer = MeshRenderer(mutableListOf(plane), material)

        editorObjs!!.add(meshRenderer)
    }

    fun setReplaceShadersCommand(vertexCode: String, fragmentCode: String) {
        addRenderCommand {
            println(fragmentCode)
            val meshRenderer = selectedEntity!!.getComponent(MeshRenderer::class.java)!!

            var selectedMaterial: Material? = null

            for (i in meshRenderer.materials) {

                if (i != null && repository.selectedMaterial != null && i!!.id == repository.selectedMaterial?.materialDataId) {
                    selectedMaterial = i
                }
            }

            Log.d("replace shader", (selectedMaterial != null).toString())


            selectedMaterial!!.shader.replaceShaders(vertexCode, fragmentCode)
        }
    }

    private fun getSelectedMat(): Material? {
        val meshRenderer = selectedEntity!!.getComponent(MeshRenderer::class.java)!!

        var selectedMaterial: Material? = null

        for (i in meshRenderer.materials) {

            if (i != null && repository.selectedMaterial != null && i!!.id == repository.selectedMaterial?.materialDataId) {
                selectedMaterial = i
            }
        }

        return selectedMaterial
    }

    fun setTextureCommand(textureData: TextureData) {

        addRenderCommand {

            val bitmap = Utils.getBitmapFromPath(textureData.path!!)

            val mat = getSelectedMat()

            println(mat!!.id)

            if (mat?.textures?.size!! > repository.selectedTextureSlot) {
                mat?.textures!![repository.selectedTextureSlot] = Texture(bitmap, GL_REPEAT)
            } else {
                mat?.textures?.add(Texture(bitmap, GL_REPEAT))
            }

            textureData.previewBitmap = bitmap
        }
    }

    fun setTransform(sceneEntityData: SceneEntityData) {
        addRenderCommand {
            val transform = selectedEntity?.getComponent(MeshRenderer::class.java)?.transform
            transform?.position = sceneEntityData.transformData.position
            transform?.eulerAngles = sceneEntityData.transformData.eulerAngles
            transform?.scale = sceneEntityData.transformData.scale

        }
    }

    fun addRenderCommand(command: () -> Unit) {
        rendererCommands.add(command)
    }

    private fun runCommands() {

        for (command in rendererCommands) {
            command()
        }

        if (rendererCommands.size > 0) {

            rendererCommands.clear()
        }
    }

    fun deleteEntity(it: SceneEntityData) {
        scene.removeSceneEntityByID(it.entityID)
    }


    fun pickUpPass(xPixel: Int, yPixel: Int, test: Boolean = false) {
        glViewport(0, 0, colorPickerFrameBuffer.width, colorPickerFrameBuffer.height)

        colorPickerFrameBuffer.bind()
        //glPixelStorei(GL_UNPACK_ALIGNMENT, 2)

        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)

        glClearColor(0.0f, 0.0f, 0.0f, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        val viewM = scene!!.editorCamera!!.viewM
        val projM = scene!!.editorCamera!!.projectionM

        var pickUpColorIndex = 0



        for (i in 0 until scene!!.entities.size) {

            val entity = scene.entities[i]

            if (sceneEntitiesData!![i].active) {
                val renderer = entity.getComponent(MeshRenderer::class.java)

                for (meshIndex in 0 until renderer!!.meshes.size) {

                    renderer?.bindWithMaterial(viewM, projM, pickupMaterial, meshIndex)

                    entity.colorID.x = repository.colorsPickupTableRBG[pickUpColorIndex + 0].toFloat()
                    entity.colorID.y = repository.colorsPickupTableRBG[pickUpColorIndex + 1].toFloat()
                    entity.colorID.z = repository.colorsPickupTableRBG[pickUpColorIndex + 2].toFloat()

                    val r = entity.colorID.x / 255f
                    val g = entity.colorID.y / 255f
                    val b = entity.colorID.z / 255f

                    pickupMaterial.set("_pickUpColor_", r, g, b, 1f)

                    val mesh = renderer.meshes[meshIndex]

                    glDrawElements(GL_TRIANGLES, mesh.indicesCount, GL_UNSIGNED_INT, mesh.indexBuffer)

                    renderer.unBind()
                }
            }

            pickUpColorIndex += 3
        }
        val flipedY = MainActivity.height - yPixel

        if (!test)
            glReadPixels(xPixel, flipedY, 1, 1, GL_RGBA, GL_UNSIGNED_BYTE, colorPickerPixelBuffer)

        var r = colorPickerPixelBuffer.get(0).toInt()
        var g = colorPickerPixelBuffer.get(1).toInt()
        var b = colorPickerPixelBuffer.get(2).toInt()

        if (r < 0) {
            r = (255 + r)
        }

        if (g < 0) {
            g = (255 + g)
        }

        if (b < 0) {
            b = (255 + b)
        }

        if (!test) {
            // println("final: " + r + ", " + g + ", " + b)
            for (i in 0 until scene.entities.size) {
                val entity = scene.entities[i]

                if (sceneEntitiesData!![i].active) {
                    if (abs(entity.colorID.x - r) <= 2
                        && abs(entity.colorID.y - g) <= 2
                        && abs(entity.colorID.z - b) <= 2
                    ) {
//                        MainActivity.instance.lifecycleScope.launch {
//                            Toast.makeText(MainActivity.instance.baseContext, "Select: " + sceneEntitiesData!![i].name, Toast.LENGTH_SHORT).show()
//                        }

                        sceneEntitiesData!![i].isSelected = true

                        if (repository.selectedSceneEntity != null) {
                            repository.selectedSceneEntity?.isSelected = false
                        }

                        repository.selectedSceneEntity = sceneEntitiesData!![i]
                        println(repository?.selectedSceneEntity?.name)

                        MainActivity.instance.lifecycleScope.launch {
                            onEntitySelected!!(sceneEntitiesData!![i].isSelected)
                        }
                        break
                    } else {
                        sceneEntitiesData!![i].isSelected = false
                        repository.selectedSceneEntity = null
                        MainActivity.instance.lifecycleScope.launch {
                            onEntitySelected!!(false)
                        }
                    }
                } else {
                    sceneEntitiesData!![i].isSelected = false
                    repository.selectedSceneEntity = null
                    MainActivity.instance.lifecycleScope.launch {
                        onEntitySelected!!(false)
                    }
                }
            }
        }

        //if (!test)
        // println(xPixel.toString() + ", " + flipedY.toString() + "| " + colorPickerPixelBuffer.get(0).toString() + ", " + colorPickerPixelBuffer.get(1).toString() + ", " + colorPickerPixelBuffer.get(2).toString() + ", " + colorPickerPixelBuffer.get(3).toString())

        colorPickerFrameBuffer.unBind()
        //mainFrameBuffer.bind()


    }

    private fun shadowPass() {

        glViewport(0, 0, shadowMapFrameBuffer.width, shadowMapFrameBuffer.height)

        shadowMapFrameBuffer.bind()

        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)
        glClear(GL_STENCIL_BUFFER_BIT)

        glClearColor(0.0f, 0.0f, 0.0f, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        for (i in 0 until scene!!.entities.size) {

            // sometimes the user can destroy an entity in another thread!, this is a patch! but it can cause problems.
            val entity = scene.entities.getOrNull(i)

            if (entity != null && sceneEntitiesData!![i].active) {
                val renderer = entity.getComponent(MeshRenderer::class.java)

                for (meshIndex in 0 until renderer!!.meshes.size) {

                    renderer?.bind(scene!!.directionalLight.getLightViewMatrix(), scene!!.directionalLight.getProjectionM(), errorMaterial, meshIndex)

                    val mesh = renderer.meshes[meshIndex]

                    glDrawElements(GL_TRIANGLES, mesh.indicesCount, GL_UNSIGNED_INT, mesh.indexBuffer)

                    renderer.unBind()
                }
            }
        }

        shadowMapFrameBuffer.unBind()

        glViewport(0, 0, shadowMapFrameBuffer.width, shadowMapFrameBuffer.height)
    }

    companion object {
        var fakeTimeScale = 0f
            private set

        var fakeDeltaTime = 0f
            private set
    }

    override fun onDrawFrame(gl: GL10?) {
        runCommands()

        // pickUpPass(0, 0, true)
        shadowPass()


        mainFrameBuffer.bind()
        glViewport(0, 0, MainActivity.width, MainActivity.height)

        fakeTimeScale += 1 * 0.01f
        glEnable(GL_DEPTH_TEST)

        glClearColor(0.2f, 0.2f, 0.2f, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        val delta = vec3(twoFingersDir.x - twoFingersNormalizedDirPrev.x, twoFingersDir.y - twoFingersNormalizedDirPrev.y, 0f)

        twoFingersNormalizedDirPrev = vec3(twoFingersDir.x, twoFingersDir.y, 0f)

        val pos = scene!!.editorCamera!!.transform.position
        scene!!.editorCamera!!.transform.position = vec3(
            pos.x + delta.x * moveMultiplier,
            pos.y + delta.y * moveMultiplier, -100f
        )

        scene!!.editorCamera!!.transform.eulerAngles = vec3(rot.y * rotateMultiplier, rot.x * rotateMultiplier, rot.z * rotateMultiplier)
        //println(delta)
        scene!!.editorCamera!!.transform.scale = vec3(zoom * moveMultiplier, zoom * moveMultiplier, zoom * moveMultiplier)

        cameraTransformData.position = scene!!.editorCamera!!.transform.position
        cameraTransformData.eulerAngles = rot
        cameraTransformData.scale.x = zoom


        val viewM = scene!!.editorCamera!!.viewM
        val projM = scene!!.editorCamera!!.projectionM

        for (i in 0 until scene!!.entities.size) {

            val entity = scene.entities.getOrNull(i)

            if (entity != null && sceneEntitiesData!![i].active) {
                val renderer = entity.getComponent(MeshRenderer::class.java)
                val materialsData = sceneEntitiesData!![i].meshRendererData.materialsData

                for (meshIndex in 0 until renderer!!.meshes.size) {

                    glActiveTexture(GL_TEXTURE0)
                    glBindTexture(GL_TEXTURE_2D, shadowMapFrameBuffer.depthTexture)

                    if (renderer?.materials?.getOrNull(meshIndex) != null) {
                        val depthUniform = glGetUniformLocation(renderer!!.materials[meshIndex]!!.shader.program, "_SHADOWMAP")
                        glUniform1i(depthUniform, 0)
                    }

                    setApplyMaterialConfig(materialsData.getOrNull(meshIndex)?.materialConfig)

                    renderer.bindShadow(viewM, projM, errorMaterial, scene.directionalLight.getViewProjLight(), meshIndex)

                    val mesh = renderer.meshes[meshIndex]



                    glDrawElements(GL_TRIANGLES, mesh.indicesCount, GL_UNSIGNED_INT, mesh.indexBuffer)
                    renderer.unBind()
                }
            }

            //selected entity outline.
            if (selectedEntity != null && entity === selectedEntity && selectedEntity?.isActive!!) {

                val renderer = selectedEntity!!.getComponent(MeshRenderer::class.java)
                glLineWidth(1.3f)

                glEnable(GL_DEPTH_TEST)
                for (meshIndex in 0 until renderer!!.meshes.size) {
                    val mesh = renderer.meshes[meshIndex]
                    renderer!!.bindWithMaterial(viewM, projM, outlineMaterial, meshIndex)
                    glDrawElements(GL_LINES, mesh.indicesCount, GL_UNSIGNED_INT, mesh.indexBuffer)
                }


            }
        }

        mainFrameBuffer.unBind()

        screenQuad()
    }


    private fun setApplyMaterialConfig(materialConfig: MaterialConfig?) {

        if (materialConfig != null) {
            // Blending
            if (materialConfig.gl_blendingEnabled) {
                glEnable(GL_BLEND)
                glBlendFunc(materialConfig.gl_srcFactor, materialConfig.gl_dstFactor)
            } else {
                glDisable(GL_BLEND)
            }

            // Depth test
            if (materialConfig.gl_depthTestEnabled) {
                glEnable(GL_DEPTH_TEST)
                glDepthFunc(materialConfig.gl_depthFunc)
            } else {
                glDisable(GL_DEPTH_TEST)
                glDepthFunc(GL_LEQUAL)
            }

            // Culling
            if (materialConfig.gl_cullEnabled) {
                glEnable(GL_CULL_FACE)
                glCullFace(materialConfig.gl_cullFace)
            } else {
                glDisable(GL_CULL_FACE)
            }
        }
    }

    val identityM = FloatArray(16).also {
        Matrix.setIdentityM(it, 0)
    }

    fun editorRenderingBack() {
        // Editor Axis Plane
        val viewM = scene!!.editorCamera!!.viewM
        val projM = scene!!.editorCamera!!.projectionM

        mainFrameBuffer.bind()

        for (obj in editorObjs!!) {

            for (meshIndex in 0 until obj!!.meshes.size) {
                obj.bind(viewM, projM, errorMaterial, meshIndex)

                if (obj.meshes[meshIndex].indicesCount > 0)
                    glDrawElements(GL_TRIANGLES, obj.meshes[meshIndex].indicesCount, GL_UNSIGNED_INT, obj.meshes[meshIndex].indexBuffer)
            }
        }
        mainFrameBuffer.unBind()

    }

    fun screenQuad() {

        editorRenderingBack()

        // Screen Quad
        glDisable(GL_DEPTH_TEST)
        glClear(GL_COLOR_BUFFER_BIT or GL_COLOR_BUFFER_BIT)

        val meshRenderer = cameraEntity.getComponent(MeshRenderer::class.java)

        meshRenderer?.bind(identityM, identityM, errorMaterial, 0)

        // this should be binded differently
        glActiveTexture(GL_TEXTURE7)
        glBindTexture(GL_TEXTURE_2D, mainFrameBuffer.colorTexture)
        //glBindTexture(GL_TEXTURE_2D, colorPickerFrameBuffer.colorTexture)

        glActiveTexture(GL_TEXTURE8)
        glBindTexture(GL_TEXTURE_2D, mainFrameBuffer.depthTexture)


        val grabPass = glGetUniformLocation(meshRenderer!!.materials[0]!!.program, "_MainTex")
        glUniform1i(grabPass, 7)

        val depthUniform = glGetUniformLocation(meshRenderer!!.materials[0]!!.program, "_CameraDepthTexture")
        glUniform1i(depthUniform, 8)


        glViewport(0, 0, MainActivity.width, MainActivity.height)

        glDrawElements(GL_TRIANGLES, meshRenderer?.meshes!![0].indicesCount, GL_UNSIGNED_INT, meshRenderer?.meshes!![0].indexBuffer)
        meshRenderer.unBind()
    }


}

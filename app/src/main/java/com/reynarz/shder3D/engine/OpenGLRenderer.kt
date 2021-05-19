package com.reynarz.shder3D.engine

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.reynarz.shder3D.MinityProjectRepository
import com.reynarz.shder3D.engine.components.MeshRenderer
import com.reynarz.shder3D.engine.components.SceneEntity
import com.reynarz.shder3D.engine.passes.CompositePass
import com.reynarz.shder3D.engine.passes.SceneMatrices
import com.reynarz.shder3D.engine.passes.ScreenPass
import com.reynarz.shder3D.engine.passes.ShadowPass
import com.reynarz.shder3D.models.*
import com.reynarz.shder3D.views.MainActivity
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
    lateinit var colorPickerPixelBuffer: ByteBuffer

    var newRenderer: Renderer? = null

    var scene: Scene = Scene()
        get() {
            if (repository.scene == null) {
                repository.scene = Scene()
            }
            return repository?.scene!!
        }

    var rot = vec3(0f, 0f, 0f)
    var zoom = 1f
    var twoFingersNormalizedDir = vec3()
    var twoFingersNormalizedDirPrev = vec3()

    var initialized = false
    lateinit var cameraEntity: SceneEntity

    private val rendererCommands = mutableListOf<() -> Unit>()
    private lateinit var errorMaterial: Material
    private var editorObjs: MutableList<MeshRenderer>? = null

    lateinit var mainFrameBuffer: FrameBuffer
    lateinit var shadowMapFrameBuffer: FrameBuffer
    lateinit var colorPickerFrameBuffer: FrameBuffer


    private lateinit var pickupMaterial: Material

    private val repository: MinityProjectRepository = get(MinityProjectRepository::class.java)

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

            return entity
        }

    private lateinit var outlineMaterial: Material

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

    }

    // order of phases is important
    private fun setupPhases(renderer: Renderer) {
        val screenPass = ScreenPass()
        val shadowPass = ShadowPass()
        val compositePass = CompositePass()

        renderer.addPass(shadowPass)
        renderer.addPass(screenPass)  //right before composite.
        renderer.addPass(compositePass)
    }


    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

        newRenderer = Renderer(SceneMatrices(scene))

        setupPhases(newRenderer!!)

        //if (!initialized) {
        repository.scene = scene
        editorObjs = mutableListOf()

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

        mainFrameBuffer = FrameBuffer()
        mainFrameBuffer.genNormalFrameBuffer(MainActivity.width, MainActivity.height, GL_REPEAT)

        shadowMapFrameBuffer = FrameBuffer()
        shadowMapFrameBuffer.genBufferForDepth(MainActivity.width, MainActivity.height)


        colorPickerFrameBuffer = FrameBuffer()
        colorPickerFrameBuffer.genNormalFrameBuffer(MainActivity.width, MainActivity.height, GL_CLAMP_TO_EDGE)

        initialized = true

        touchPointer = TouchPointer(scene!!.editorCamera!!)

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

        MainActivity.instance.lifecycleScope.launch {
            last_time = System.nanoTime()
        }
        // }
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

            println("mat idtoChange: " + mat!!.id)

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

        colorPickerFrameBuffer.bind()
        glViewport(0, 0, colorPickerFrameBuffer.width, colorPickerFrameBuffer.height)

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

            if (entity.entityData.active) {
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
            var someSelected = false
            // println("final: " + r + ", " + g + ", " + b)
            for (i in 0 until scene.entities.size) {
                val entity = scene.entities[i]

                if (entity.entityData.active) {
                    if (abs(entity.colorID.x - r) <= 2
                        && abs(entity.colorID.y - g) <= 2
                        && abs(entity.colorID.z - b) <= 2
                    ) {

                        entity.entityData.isSelected = true
                        someSelected = true

                        if (repository.selectedSceneEntity != null && repository.selectedSceneEntity != entity.entityData) {
                            repository.selectedSceneEntity!!.isSelected = false
                        }

                        repository.selectedSceneEntity = entity.entityData

                        MainActivity.instance.lifecycleScope.launch {
                            onEntitySelected!!(entity.entityData.isSelected)
                        }
                    } else {
                        entity.entityData.isSelected = false
                    }
                } else {
                    entity.entityData.isSelected = false
                }
            }

            if (!someSelected) {
                MainActivity.instance.lifecycleScope.launch {
                    onEntitySelected!!(false)
                    repository.selectedSceneEntity = null
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

            if (entity != null && entity.entityData.active && entity.entityData.meshRendererData.castShadows) {
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
        var time = 0f
            private set

        var deltaTime = 0f
            private set
    }

    var last_time = System.nanoTime()

    var t = 0f
    var delta_time = 0f

    override fun onDrawFrame(gl: GL10?) {

        runCommands()

        newRenderer?.draw()

//
//        //pickUpPass(0, 0, true)
//        shadowPass()
//
//        // this could have bad perfomance
        MainActivity.instance.lifecycleScope.launch {

            val time = System.nanoTime()
            delta_time = ((time - last_time) / 1000000000f)
            last_time = time

            t += delta_time
            //println("t: " + t + ", dt: " + delta_time)
        }
//
//        mainFrameBuffer.bind()
//        glViewport(0, 0, MainActivity.width, MainActivity.height)
//
        time = t
        deltaTime = delta_time
//
//        glEnable(GL_DEPTH_TEST)
//
//        glClearColor(0.2f, 0.2f, 0.2f, 1f)
//        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
//
        val delta = vec3(twoFingersDir.x - twoFingersNormalizedDirPrev.x, twoFingersDir.y - twoFingersNormalizedDirPrev.y, 0f)
//
        twoFingersNormalizedDirPrev = vec3(twoFingersDir.x, twoFingersDir.y, 0f)
//
        val pos = scene!!.editorCamera!!.transform.position
        scene!!.editorCamera!!.transform.position = vec3(
            pos.x + delta.x * moveMultiplier,
            pos.y + delta.y * moveMultiplier, -100f
        )
//
        scene!!.editorCamera!!.transform.eulerAngles = vec3(rot.y * rotateMultiplier, rot.x * rotateMultiplier, rot.z * rotateMultiplier)
        //println(delta)
        scene!!.editorCamera!!.transform.scale = vec3(zoom * moveMultiplier, zoom * moveMultiplier, zoom * moveMultiplier)

        cameraTransformData.position = scene!!.editorCamera!!.transform.position
        cameraTransformData.eulerAngles = rot
        cameraTransformData.scale.x = zoom

        if (newRenderer?.target == null) {
            println("target")
            val meshRenderer = cameraEntity.getComponent(MeshRenderer::class.java)
            newRenderer?.target = ScreenQuad(meshRenderer!!)
        }

//
//
//        val viewM = scene!!.editorCamera!!.viewM
//        val projM = scene!!.editorCamera!!.projectionM
//
//        for (i in 0 until scene!!.entities.size) {
//
//            val entity = scene.entities.getOrNull(i)
//
//            if (entity != null && entity.entityData.active) {
//                val renderer = entity.getComponent(MeshRenderer::class.java)
//                val materialsData = entity.entityData.meshRendererData.materialsData
//
//                for (meshIndex in 0 until renderer!!.meshes.size) {
//
//                    glActiveTexture(GL_TEXTURE0)
//                    glBindTexture(GL_TEXTURE_2D, shadowMapFrameBuffer.depthTexture)
//
//                    if (renderer?.materials?.getOrNull(meshIndex) != null) {
//                        val depthUniform = glGetUniformLocation(renderer!!.materials[meshIndex]!!.shader.program, "_SHADOWMAP")
//                        glUniform1i(depthUniform, 0)
//                    }
//
//                    setApplyMaterialConfig_GL(materialsData.getOrNull(meshIndex)?.materialConfig)
//
//                    renderer.bindShadow(viewM, projM, errorMaterial, scene.directionalLight.getViewProjLight(), meshIndex)
//
//                    val mesh = renderer.meshes[meshIndex]
//
//                    glDrawElements(GL_TRIANGLES, mesh.indicesCount, GL_UNSIGNED_INT, mesh.indexBuffer)
//                    renderer.unBind()
//                }
//            }
//
//            //Selected entity outline.
//            if (selectedEntity != null && entity === selectedEntity && entity?.entityData?.active!!) {
//
//                val renderer = selectedEntity!!.getComponent(MeshRenderer::class.java)
//                glLineWidth(1.3f)
//
//                glEnable(GL_DEPTH_TEST)
//
//                for (meshIndex in 0 until renderer!!.meshes.size) {
//                    val mesh = renderer.meshes[meshIndex]
//                    renderer!!.bindWithMaterial(viewM, projM, outlineMaterial, meshIndex)
//                    glDrawElements(GL_LINES, mesh.indicesCount, GL_UNSIGNED_INT, mesh.indexBuffer)
//                }
//            }
//        }
//
//        mainFrameBuffer.unBind()

        //    screenQuad()
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

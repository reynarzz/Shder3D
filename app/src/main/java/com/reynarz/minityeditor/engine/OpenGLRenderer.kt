package com.reynarz.minityeditor.engine

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.reynarz.minityeditor.MinityProjectRepository
import com.reynarz.minityeditor.engine.components.MeshRenderer
import com.reynarz.minityeditor.engine.components.SceneEntity
import com.reynarz.minityeditor.engine.components.Transform
import com.reynarz.minityeditor.models.EntityType
import com.reynarz.minityeditor.models.SceneEntityData
import com.reynarz.minityeditor.models.TextureData
import com.reynarz.minityeditor.models.TransformComponentData
import com.reynarz.minityeditor.views.MainActivity
import org.koin.java.KoinJavaComponent.get
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class OpenGLRenderer(val context: Context) : GLSurfaceView.Renderer {

    private lateinit var cameraTransformData: TransformComponentData
    lateinit var touchPointer: TouchPointer

    val scene: Scene = Scene()
    var rot = vec3(0f, 0f, 0f)
    var zoom = 1f
    var initialized = false
    lateinit var cameraEntity: SceneEntity

    private val rendererCommands = mutableListOf<() -> Unit>()
    private lateinit var errorMaterial: Material
    private var editorObjs: MutableList<MeshRenderer>? = null

    var lStartTime = System.currentTimeMillis().toFloat()

    lateinit var mainFrameBuffer: FrameBuffer
    lateinit var shadowMapFrameBuffer: FrameBuffer
    private lateinit var lightTransform: Transform

    private lateinit var lightObj: MeshRenderer

    private val repository: MinityProjectRepository = get(MinityProjectRepository::class.java)

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

    init {

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
    }


    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

        if (!initialized) {
            editorObjs = mutableListOf()

            outlineMaterial = Utils.getUnlitMaterial(1f)
            errorMaterial = Utils.getErrorMaterial()

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
            mainFrameBuffer.genNormalFrameBuffer()

            shadowMapFrameBuffer = FrameBuffer(MainActivity.width, MainActivity.height)
            shadowMapFrameBuffer.genBufferForDepth()

            initialized = true

            touchPointer = TouchPointer(scene!!.editorCamera!!)

            println("Current Opengl thread: " + Thread.currentThread().name)
            getEditorStuff_Test()

            var lightEntity = SceneEntity()
            val meshRenderer = lightEntity.addComponent(MeshRenderer::class.java)
            meshRenderer.mesh = Utils.getQuad(2f)
            meshRenderer.materials.add(Utils.getErrorMaterial())
            meshRenderer.transform.modelM = scene.directionalLight.getLightViewMatrix()

            lightTransform = meshRenderer.transform
            //scene.editorCamera!!.transform.modelM = scene.directionalLight.getLightViewMatrix()

            scene.entities.add(lightEntity)
        }
    }

    fun getEditorStuff_Test() {
        val groundShaderCode = Utils.getGroundShadersCode()

        val material = Material(Shader(groundShaderCode.first, groundShaderCode.second))

        val plane = Utils.getPlane(2000f)
        //plane.bind(material.program)
        val meshRenderer = MeshRenderer(plane, material)

        editorObjs!!.add(meshRenderer)
    }

    fun setReplaceShadersCommand(vertexCode: String, fragmentCode: String) {

        addRenderCommand {

            val material = selectedEntity!!.getComponent(MeshRenderer::class.java)!!.materials[0]
            Log.d("replace shader", (material != null).toString())

            material!!.shader.replaceShaders(vertexCode, fragmentCode)
        }
    }

    fun setTextureCommand(textureData: TextureData) {

        addRenderCommand {

            val meshRenderer = selectedEntity?.getComponent(MeshRenderer::class.java)

            val bitmap = Utils.getBitmapFromPath(textureData.path!!)
            if (meshRenderer?.materials!![0]?.textures!!.size > repository.selectedTextureSlot) {
                meshRenderer?.materials[0]?.textures!![repository.selectedTextureSlot] = Texture(bitmap)
            } else {
                meshRenderer?.materials!![0]?.textures?.add(Texture(bitmap))
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

    private fun shadowPass() {

        glViewport(0, 0, shadowMapFrameBuffer.width, shadowMapFrameBuffer.height)

        shadowMapFrameBuffer.bind()

        glEnable(GL_DEPTH_TEST)
        glDisable(GL_STENCIL_TEST)
        glClear(GL_STENCIL_BUFFER_BIT)

        glClearColor(0.0f, 0.0f, 0.0f, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)


        for (entity in scene!!.entities) {


            val renderer = entity.getComponent(MeshRenderer::class.java)

            renderer?.bind(scene.directionalLight.getLightViewMatrix(), scene.directionalLight.getProjectionM(), errorMaterial)
            glDrawElements(GL_TRIANGLES, renderer!!.mesh.indicesCount, GL_UNSIGNED_INT, renderer!!.mesh.indexBuffer)

        }
        shadowMapFrameBuffer.unBind()

        glViewport(0, 0, shadowMapFrameBuffer.width, shadowMapFrameBuffer.height)

    }

    override fun onDrawFrame(gl: GL10?) {
        runCommands()

        shadowPass()

        mainFrameBuffer.bind()

        glDisable(GL_STENCIL_TEST)
        glClear(GL_STENCIL_BUFFER_BIT)

        glEnable(GL_DEPTH_TEST)

        glClearColor(0.2f, 0.2f, 0.2f, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        val lEndTime = System.currentTimeMillis().toFloat()

        val output = (lEndTime - lStartTime).toFloat()

        lStartTime = lEndTime
        //shader.setDeltaTimeTest(lEndTime.toFloat(), output)

        scene!!.editorCamera!!.transform.position = vec3(0f, 0f, -100f)
        scene!!.editorCamera!!.transform.eulerAngles = vec3(rot.y, rot.x, rot.z)
        scene!!.editorCamera!!.transform.scale = vec3(zoom, zoom, zoom)

        cameraTransformData.position = scene!!.editorCamera!!.transform.position
        cameraTransformData.eulerAngles = rot
        cameraTransformData.scale.x = zoom


        val viewM = scene!!.editorCamera!!.viewM
        val projM = scene!!.editorCamera!!.projectionM
        // val ray = touchPointer.getWorldPosRay(OpenGLView.xPixel, OpenGLView.yPixel)

        glViewport(0, 0, MainActivity.width, MainActivity.height)

        for (entity in scene!!.entities) {

            if (entity.isActive) {

//            if (entity.testMeshRenderer != null) {
//                entity.testMeshRenderer!!.bind(viewM, projM, errorMaterial)
//            }

                val renderer = entity.getComponent(MeshRenderer::class.java)

                renderer?.bindShadow(viewM, projM, errorMaterial, scene.directionalLight.getViewProjLight())
//Shadow Map test
                glActiveTexture(GL_TEXTURE2)
                glBindTexture(GL_TEXTURE_2D, shadowMapFrameBuffer.depthTexture)

                if (renderer?.materials?.getOrNull(0) != null) {
                    val depthUniform = glGetUniformLocation(renderer!!.materials[0]!!.shader.program, "_SHADOWMAP")
                    glUniform1i(depthUniform, 2)
                }


                if (selectedEntity != null && entity === selectedEntity) {
                    // glDisable(GL_DEPTH_TEST)
                    glEnable(GL_STENCIL_TEST)
                    glStencilFunc(GL_ALWAYS, 1, 0xff)
                    glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE)
                    glStencilMask(0xff)
                }

                glDrawElements(GL_TRIANGLES, renderer!!.mesh.indicesCount, GL_UNSIGNED_INT, renderer!!.mesh.indexBuffer)

                if (entity.name != "Bounds") {
                    //glDrawElements(GL_TRIANGLES, entity.testMeshRenderer!!.indicesCount, GL_UNSIGNED_INT, entity.testMeshRenderer!!.indexBuffer)

                    // glDisable(GL_DEPTH_TEST)
                    if (selectedEntity != null && entity === selectedEntity) {
                        glEnable(GL_STENCIL_TEST)
                        //glDisable(GL_DEPTH_TEST)

                        glStencilFunc(GL_NOTEQUAL, 1, 0xff); // Pass test if stencil value is 1
                        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
                        glStencilMask(0x00)

                        val renderer = selectedEntity!!.getComponent(MeshRenderer::class.java)
//                        val scale = vec3(renderer!!.transform.scale.x, renderer!!.transform.scale.y, renderer!!.transform.scale.z)
//
//                        renderer!!.transform.scale = vec3(scale.x + 0.02f, scale.y + 0.02f, scale.z + 0.02f)
                        //renderer!!.bind(viewM, projM, outlineMaterial)
                        renderer!!.bindWithMaterial(viewM, projM, outlineMaterial)

                        glDrawElements(GL_LINES, renderer!!.mesh.indicesCount, GL_UNSIGNED_INT, renderer.mesh.indexBuffer)

                        //renderer.transform.scale = scale

                        glDisable(GL_STENCIL_TEST)
                        glEnable(GL_DEPTH_TEST)
                    }

                } else {
//                glDrawElements(
//                    GL_LINES, entity.testMeshRenderer!!.indicesCount, GL_UNSIGNED_INT, entity.testMeshRenderer!!.indexBuffer
//                )
                }
            }
        }
        mainFrameBuffer.unBind()




        editorRendering()

    }

    val identityM = FloatArray(16).also {
        Matrix.setIdentityM(it, 0)
    }

    fun editorRendering() {

        // Editor Axis Plane
        val viewM = scene!!.editorCamera!!.viewM
        val projM = scene!!.editorCamera!!.projectionM

        mainFrameBuffer.bind()

        for (obj in editorObjs!!) {
            obj.bind(viewM, projM, errorMaterial)

            if (obj.mesh.indicesCount > 0)
                glDrawElements(GL_TRIANGLES, obj.mesh.indicesCount, GL_UNSIGNED_INT, obj.mesh.indexBuffer)
        }
        mainFrameBuffer.unBind()

        // Screen Quad
        glDisable(GL_DEPTH_TEST)
        glClear(GL_COLOR_BUFFER_BIT or GL_COLOR_BUFFER_BIT)

        val meshRenderer = cameraEntity.getComponent(MeshRenderer::class.java)

        meshRenderer?.bind(identityM, identityM, errorMaterial)

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, mainFrameBuffer.colorTexture)

        glActiveTexture(GL_TEXTURE1)
        glBindTexture(GL_TEXTURE_2D, mainFrameBuffer.depthTexture)


        val grabPass = glGetUniformLocation(meshRenderer!!.materials[0]!!.program, "_MainTex")
        glUniform1i(grabPass, 0)

        val depthUniform = glGetUniformLocation(meshRenderer!!.materials[0]!!.program, "_CameraDepthTexture")
        glUniform1i(depthUniform, 1)



        glViewport(0, 0, MainActivity.width, MainActivity.height)

        glDrawElements(GL_TRIANGLES, meshRenderer?.mesh!!.indicesCount, GL_UNSIGNED_INT, meshRenderer?.mesh!!.indexBuffer)
    }
}

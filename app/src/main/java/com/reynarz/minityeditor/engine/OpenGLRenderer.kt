package com.reynarz.minityeditor.engine

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.util.Log
import com.reynarz.minityeditor.views.MainActivity
import com.reynarz.minityeditor.engine.components.MeshRenderer
import com.reynarz.minityeditor.engine.components.SceneEntity
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class OpenGLRenderer(val context: Context) : GLSurfaceView.Renderer {

    lateinit var touchPointer: TouchPointer

    val scene: Scene = Scene()
    var rot = vec3(0f, 0f, 0f)
    var zoom = 1f
    var initialized = false

    private val rendererCommands = mutableListOf<() -> Unit>()
    private lateinit var errorMaterial: Material
    private var editorObjs: MutableList<MeshRenderer>? = null

    var lStartTime = System.currentTimeMillis().toFloat()

    lateinit var mainFrameBuffer: FrameBuffer
    private var quadShader: Shader? = null
    var screenQuadMesh: Mesh? = null

    private var selectedEntity: SceneEntity? = null
    private lateinit var unlitMat: Material

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
    }


    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

        if (!initialized) {
            editorObjs = mutableListOf()

            unlitMat = Utils.getUnlitMaterial(1f)
            errorMaterial = Utils.getErrorMaterial()

            glEnable(GL_DEPTH_TEST)
            glDepthFunc(GL_LEQUAL)
            //glEnable(GL_BLEND)
            //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

            scene!!.editorCamera!!.updateProjection(width, height)

            mainFrameBuffer = FrameBuffer(MainActivity.width, MainActivity.height)

            val screenQuadShaderCode = Utils.getScreenQuadShaderCode()

            quadShader = Shader(screenQuadShaderCode.first, screenQuadShaderCode.second)

            initialized = true
            screenQuadMesh = Utils.getScreenSizeQuad()

            touchPointer = TouchPointer(scene!!.editorCamera!!)

            getEditorStuff_Test()
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

            val material = selectedEntity!!.getComponent(MeshRenderer::class.java)!!.material
            Log.d("replace shader", (material != null).toString())

            material!!.shader.replaceShaders(vertexCode, fragmentCode)
        }
    }


    fun selectEntityID(entityID: String?) {
        if (entityID != null) {
            selectedEntity = scene!!.getEntityById(entityID!!)
        } else {
            selectedEntity = null
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


    override fun onDrawFrame(gl: GL10?) {
        runCommands()
        Log.d("time", MainActivity.time.toString())
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


        //the camera should have as well a 'viewProjectionM'

        scene!!.editorCamera!!.transform.eulerAngles = vec3(rot.y, rot.x, rot.z)
        scene!!.editorCamera!!.transform.position = vec3(0f, 0f, -100f)
        scene!!.editorCamera!!.transform.scale = vec3(zoom, zoom, zoom)

//        entity.testMeshRenderer!!.transform
//        entity.testMeshRenderer!!.transform.scale = Vec3(zoom, zoom, zoom)
//
        val viewM = scene!!.editorCamera!!.viewM
        val projM = scene!!.editorCamera!!.projectionM
        // val ray = touchPointer.getWorldPosRay(OpenGLView.xPixel, OpenGLView.yPixel)


        for (entity in scene!!.entities) {


//            if (entity.testMeshRenderer != null) {
//                entity.testMeshRenderer!!.bind(viewM, projM, errorMaterial)
//            }

            val renderer = entity.getComponent(MeshRenderer::class.java)

            if (renderer != null) {
                Log.d("Bind", "Binded")
                renderer.bind(viewM, projM, errorMaterial)
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
                    val scale = renderer!!.transform.scale

                    renderer!!.transform.scale = vec3(scale.x + 0.05f, scale.y + 0.05f, scale.z + 0.05f)
                    renderer!!.bindWithMaterial(viewM, projM, unlitMat)

                    glDrawElements(GL_TRIANGLES, renderer!!.mesh.indicesCount, GL_UNSIGNED_INT, renderer.mesh.indexBuffer)

                    renderer.transform.scale = vec3(1f, 1f, 1f)

                    glDisable(GL_STENCIL_TEST)
                    glEnable(GL_DEPTH_TEST)

                }

            } else {
//                glDrawElements(
//                    GL_LINES, entity.testMeshRenderer!!.indicesCount, GL_UNSIGNED_INT, entity.testMeshRenderer!!.indexBuffer
//                )
            }
        }

        for (obj in editorObjs!!) {
            obj.bind(viewM, projM, errorMaterial)

            if (obj.mesh.indicesCount > 0)
                glDrawElements(GL_TRIANGLES, obj.mesh.indicesCount, GL_UNSIGNED_INT, obj.mesh.indexBuffer)
        }

        mainFrameBuffer.unBind()


        // Screen Quad
        glDisable(GL_DEPTH_TEST)
        glClear(GL_COLOR_BUFFER_BIT)

        val prog = quadShader!!.bind()

        screenQuadMesh!!.bind(prog)

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, mainFrameBuffer.colorTexture)

        glActiveTexture(GL_TEXTURE1)
        glBindTexture(GL_TEXTURE_2D, mainFrameBuffer.depthTexture)


        val depthUniform = glGetUniformLocation(prog, "_CameraDepthTexture")
        glUniform1i(depthUniform, 1)

        glViewport(0, 0, MainActivity.width, MainActivity.height)

        glDrawElements(
            GL_TRIANGLES,
            screenQuadMesh!!.indicesCount,
            GL_UNSIGNED_INT,
            screenQuadMesh!!.indexBuffer
        )
    }

}

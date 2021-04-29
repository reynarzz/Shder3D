package com.reynarz.minityeditor.engine

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import com.reynarz.minityeditor.views.MainActivity
import com.reynarz.minityeditor.engine.components.MeshRenderer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class OpenGLRenderer(val context: Context) : GLSurfaceView.Renderer {

    private lateinit var vertexShaderCode: String
    private lateinit var fragmentShaderCode: String
    lateinit var touchPointer: TouchPointer

    val scene: Scene = Scene()
    var rot = vec3(0f, 0f, 0f)
    var zoom = 1f

    private val rendererCommands = mutableListOf<() -> Unit>()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

    }

    var changed = false

    val screenQuadVertexCode = """
            
attribute vec4 _VERTEX_; 
           
attribute vec2 _UV_;
varying vec2 _uv;
varying vec4 pos;

void main() 
{
   _uv = _UV_;
   gl_Position = vec4( _VERTEX_.x,  _VERTEX_.y, 0, 1);
}"""
    var screenFragTex = """
            
precision mediump float; 
varying vec4 pos;

varying vec2 _uv;

uniform sampler2D sTexture;
uniform sampler2D _CameraDepthTexture;

float LinearizeDepth(float depth)
{
    float far = 1000.0;
    float near = 0.1f;
    
    float z = (depth * 2.0 - 1.0); 
    return (2.0 * near * far) / (far + near - z * (far - near));
}

void main()
{
    float far = 1000.0;

    float depth = LinearizeDepth(texture2D(_CameraDepthTexture, _uv ).r)/(far-900.);
    gl_FragColor = vec4(depth);
    
    gl_FragColor = texture2D(sTexture, _uv);
}
"""

    val groundGridVertex = """
     attribute vec4 _VERTEX_;
attribute vec2 _UV_;

uniform mat4 UNITY_MATRIX_MVP;
varying vec3 _pixelPos;
varying vec2 _uv;

void main()
{
	_uv = _UV_ - 0.5;
	_pixelPos = _VERTEX_.xyz;
	gl_Position = UNITY_MATRIX_MVP * _VERTEX_;
}
    """.trimIndent()

    val groundGridFragment = """
        precision mediump float; 
        varying vec2 _uv;
uniform vec3 _diffuse_;

uniform vec3 _WorldSpaceCameraPos;
varying vec3 _pixelPos;

void main()
{
    float maxDist = 250.;

    //float alpha = (maxDist - length(_pixelPos - _WorldSpaceCameraPos));

    float thickness = 0.05;
    float spacing = 10.;

    if (fract(_pixelPos.x / spacing) < thickness || fract(_pixelPos.z / spacing) < thickness)
    {
        if(int(_pixelPos.z) == 0)
        {
            gl_FragColor = vec4(1.0, 0., 0., 0.7);
        }
        else if(int(_pixelPos.x) == 0)
        {
            gl_FragColor = vec4(0., 0.2, 1., 0.9);
        }
        else
        {
        
        //gl_FragColor = vec4(alpha);
           // gl_FragColor = vec4(vec3(0.5), smoothstep(alpha, 0.0, 0.2));
            gl_FragColor = vec4(vec3(0.13),  1.);
        }
    }
    else
    {
        discard;
    }

//gl_FragColor = vec4(0.3);
}
"""
    var initialized = false

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

        if (!initialized) {
            editorObjs = mutableListOf()

            glEnable(GL_DEPTH_TEST)
            glDepthFunc(GL_LEQUAL)
            //glEnable(GL_BLEND)
            //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

            scene!!.editorCamera!!.updateProjection(width, height)

            mainFrameBuffer = FrameBuffer(MainActivity.width, MainActivity.height)

            quadShader = Shader(screenQuadVertexCode, screenFragTex)

            initialized = true
            screenQuadMesh = Utils.getScreenSizeQuad()

            touchPointer = TouchPointer(scene!!.editorCamera!!)

            getEditorStuff_Test()
        }
    }

    fun getEditorStuff_Test() {
        val material = Material(Shader(groundGridVertex, groundGridFragment))
        val plane = Utils.getPlane(2000f)
        //plane.bind(material.program)
        val meshRenderer = MeshRenderer(plane, material)

        editorObjs!!.add(meshRenderer)

    }

    fun setShaders(vertexCode: String, fragmentCode: String) {
        fragmentShaderCode = fragmentCode
        vertexShaderCode = vertexCode

        changed = true
    }

    private var editorObjs: MutableList<MeshRenderer>? = null

    var lStartTime = System.currentTimeMillis().toFloat()

    lateinit var mainFrameBuffer: FrameBuffer
    private var quadShader: Shader? = null
    var screenQuadMesh: Mesh? = null

    val selectedObjID_Test = 0

    private val objsToLoad = mutableListOf<String>()

    fun loadNewObjCommand(filePath: String) {
        objsToLoad.add(filePath)
    }

    fun addRenderCommand(command : () -> Unit){
        rendererCommands.add(command)
    }



    private fun onPushedOutsideCommands() {
        for (command in rendererCommands)
        {
            command()
            rendererCommands.remove(command)
        }
    }

    override fun onDrawFrame(gl: GL10?) {

        onPushedOutsideCommands()

        mainFrameBuffer.bind()

        glEnable(GL_DEPTH_TEST)

        glClearColor(0.2f, 0.2f, 0.2f, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        val lEndTime = System.currentTimeMillis().toFloat()

        val output = (lEndTime - lStartTime).toFloat()

        lStartTime = lEndTime
        //shader.setDeltaTimeTest(lEndTime.toFloat(), output)

        if (changed) {
            changed = false

            // i need the renderer ID as well to only update the correct shader.
//            scene!!.entities[selectedObjID_Test].testMeshRenderer!!.material.shader.replaceShaders(
//                vertexShaderCode,
//                fragmentShaderCode
//            )
        }

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
            if (entity.testMeshRenderer != null) {
                entity.testMeshRenderer!!.bind(viewM, projM)
            }

            if (entity.name != "Bounds") {
                glDrawElements(
                    GL_TRIANGLES,
                    entity.testMeshRenderer!!.indicesCount,
                    GL_UNSIGNED_INT,
                    entity.testMeshRenderer!!.indexBuffer
                )
            } else {
                glDrawElements(
                    GL_LINES,
                    entity.testMeshRenderer!!.indicesCount,
                    GL_UNSIGNED_INT,
                    entity.testMeshRenderer!!.indexBuffer
                )
            }
        }

        for (obj in editorObjs!!) {
            obj.bind(viewM, projM)

            if (obj.indicesCount > 0)
                glDrawElements(GL_TRIANGLES, obj.indicesCount, GL_UNSIGNED_INT, obj.indexBuffer)
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
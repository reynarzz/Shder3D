package com.example.viewer3d.engine

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import com.example.viewer3d.MainActivity
import com.example.viewer3d.engine.components.MeshRenderer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class OpenGlRenderer(val context: Context) : GLSurfaceView.Renderer {

    private lateinit var vertexShaderCode: String
    private lateinit var fragmentShaderCode: String
    private var scene: Scene? = null

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

void main()
{
    gl_FragColor = texture2D(sTexture, _uv) ;
}"""

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

uniform vec3 _cameraWorldPos_;
varying vec3 _pixelPos;

void main()
{
    float maxDist = 100.;

   // float alpha = (maxDist - length(_pixelPos - _cameraWorldPos_));

    float thickness = 0.01;
    float spacing = 100.;

//    if (fract(_pixelPos.x / spacing) < thickness || fract(_pixelPos.z / spacing) < thickness)
//    {
//        if(int(_pixelPos.z) == 0)
//        {
//            gl_FragColor = vec4(1.0, 0., 0., 0.7);
//        }
//        else if(int(_pixelPos.x) == 0)
//        {
//            gl_FragColor = vec4(0., 0.2, 1., 0.9);
//        }
//        else
//        {
//           // gl_FragColor = vec4(vec3(1.), clamp(alpha, 0.0, 0.2));
//            gl_FragColor = vec4(vec3(1.),  0.2);
//        }
//    }
//    else
//    {
//        discard;
//        //gl_FragColor = vec4(1.);
//    }

gl_FragColor = vec4(0.3);
}
"""

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

        renderingObjs = mutableListOf()
        editorObjs = mutableListOf()

        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)
        // glEnable(GL_TEXTURE_2D)
        //glEnable(GL_BLEND)
        //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        scene = Scene()

        scene!!.editorCamera!!.updateProjection(width, height)
        scene!!.editorCamera!!.updateView()

        //CoffeeRestaurant
        //SimpleScene

        frameBuffer = FrameBuffer(MainActivity.width, MainActivity.height)

        quadShader = Shader(screenQuadVertexCode, screenFragTex)

        getGirl_Test()
        getEditorStuff_Test()

        screenQuadMesh = Utils.getScreenSizeQuad()
    }

    fun getGirl_Test() {
        ObjParser(context, "models/girl.obj").also {

            var data = it.getModelData()

            val mesh = Mesh(data.mVertices, data.mIndices, data.mUVs)
            val mat = Material(Shader(vertexShaderCode, fragmentShaderCode))
            mat.addTexture(Texture(context, "textures/girltex_small.jpg"))

            val renderer = MeshRenderer(mesh, mat)
            renderingObjs!!.add(renderer)

        }
    }

    fun getEditorStuff_Test() {
        val material = Material(Shader(groundGridVertex, groundGridFragment))
        val meshRenderer = MeshRenderer(Utils.getPlane(2000f),material )

        editorObjs!!.add(meshRenderer)

    }

    // Loads a texture into OpenGL


    // Unloads a texture from OpenGL


    // Commands to do in the render thread.
    fun PushCommand(/*A delegate here to call a command*/) {

    }

    fun setShaders(vertexCode: String, fragmentCode: String) {
        fragmentShaderCode = fragmentCode
        vertexShaderCode = vertexCode

        changed = true
    }

    private var renderingObjs : MutableList<MeshRenderer>? = null
    private var editorObjs : MutableList<MeshRenderer>? = null

    var lStartTime = System.currentTimeMillis().toFloat()

    lateinit var frameBuffer: FrameBuffer
    private var quadShader : Shader? = null
    var screenQuadMesh : Mesh? = null

    val selectedObjID_Test = 0

    override fun onDrawFrame(gl: GL10?) {
        frameBuffer.bind()
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
            renderingObjs!![selectedObjID_Test].material.shader.replaceShaders(vertexShaderCode, fragmentShaderCode)
        }


        //the camera should have as well a 'viewProjectionM'
        val viewM = scene!!.editorCamera!!.viewM
        val projM = scene!!.editorCamera!!.projectionM

        for (renderer in renderingObjs!!) {

            renderer.bind(viewM, projM)
            glDrawElements(GL_TRIANGLES, renderer.indicesCount, GL_UNSIGNED_INT, renderer.indexBuffer)
        }

        for (obj in editorObjs!!)
        {
            obj.bind(viewM, projM)

            glDrawElements(GL_TRIANGLES, obj.indicesCount, GL_UNSIGNED_INT, obj.indexBuffer)
        }

        frameBuffer.unBind()


        // Screen Quad
        glDisable(GL_DEPTH_TEST)
        glClear(GL_COLOR_BUFFER_BIT)

        screenQuadMesh!!.bind(quadShader!!.bind())

        glBindTexture(GL_TEXTURE_2D, frameBuffer.colorTexture)

        glViewport(0, 0, MainActivity.width, MainActivity.height)

        glDrawElements(GL_TRIANGLES, screenQuadMesh!!.indicesCount, GL_UNSIGNED_INT, screenQuadMesh!!.indexBuffer)
    }
}
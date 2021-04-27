package com.reynarz.minityeditor.engine

import android.opengl.GLES20.*

class FrameBuffer(private val width : Int, private val height: Int) {

    private var frameBuffer : IntArray = IntArray(1)

    val colorTexture : Int
    val depthTexture : Int
    var stencilTexture = -1

    init {
        // Create a frame buffer
        glGenFramebuffers(1, frameBuffer, 0);

        colorTexture = Texture(GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE, width, height, GL_NEAREST).textureID
        depthTexture = Texture(GL_DEPTH_COMPONENT, GL_DEPTH_COMPONENT,GL_UNSIGNED_SHORT, width, height, GL_NEAREST).textureID

        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer[0])

        // Associate the textures with the FBO.
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
                GL_TEXTURE_2D, colorTexture, 0);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT,
                GL_TEXTURE_2D, depthTexture, 0);

        // Check FBO status.
        val status = glCheckFramebufferStatus(GL_FRAMEBUFFER);

        if (status == GL_FRAMEBUFFER_COMPLETE) {
            // Success
        }
        unBind()
    }


//    fun createPickingBuffer_TEST() {
//        glGenFramebuffers(1, frameBuffer, 0)
//        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer[0])
//
//        stencilTexture = Texture(0, )
//
//        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_STENCIL_ATTACHMENT, GL_TEXTURE_2D, stencilTexture, 0)
//        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0)
//
//        unBind()
//    }

    fun bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer[0])
    }

    fun unBind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }
}
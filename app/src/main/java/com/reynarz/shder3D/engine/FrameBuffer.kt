package com.reynarz.shder3D.engine

import android.opengl.GLES20.*

class FrameBuffer {

    private var frameBuffer: IntArray = IntArray(1)

    var colorTexture = -1
    var depthTexture = -1
    var stencilTexture = -1

    private var _width = 0;
    private var _height = 0;

    var width: Int = 0
        get() {
            return _width
        }

    var height: Int = 0
        get() {
            return _height
        }

    fun genNormalFrameBuffer(width: Int, height: Int, clampParams: Int) {
        _width = width
        _height = height

        // Create a frame buffer
        glGenFramebuffers(1, frameBuffer, 0);

        colorTexture = Texture(GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE, _width, _height, GL_LINEAR, clampParams).textureID
        depthTexture = Texture(GL_DEPTH_COMPONENT, GL_DEPTH_COMPONENT, GL_UNSIGNED_SHORT, _width, _height, GL_LINEAR, clampParams).textureID

        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer[0])

        // Associate the textures with the FBO.
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorTexture, 0)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0)

        // Check FBO status.
        val status = glCheckFramebufferStatus(GL_FRAMEBUFFER);

        if (status == GL_FRAMEBUFFER_COMPLETE) {
            // Success
        }

        unBind()
    }

    fun genBufferForDepth(width: Int, height: Int) {
        _width = width
        _height = height

        glGenFramebuffers(1, frameBuffer, 0)
        depthTexture = Texture(GL_DEPTH_COMPONENT, GL_DEPTH_COMPONENT, GL_UNSIGNED_SHORT, _width, _height, GL_LINEAR, GL_CLAMP_TO_EDGE).textureID

        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer[0])
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0)
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

        if (colorTexture > -1)
            glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, colorTexture)

        if (depthTexture > -1)
            glActiveTexture(GL_TEXTURE1)
        glBindTexture(GL_TEXTURE_2D, depthTexture)
    }

    fun unBind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0)

        if (colorTexture > -1)
            glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, 0)

        if (depthTexture > -1)
            glActiveTexture(GL_TEXTURE1)
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    fun genBufferForColor() {

        glGenFramebuffers(1, frameBuffer, 0)
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer[0])

        colorTexture = Texture(GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE, width, height, GL_NEAREST, GL_CLAMP_TO_EDGE).textureID

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorTexture, 0)
        unBind()
    }
}
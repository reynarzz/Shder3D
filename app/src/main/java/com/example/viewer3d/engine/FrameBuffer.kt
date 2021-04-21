package com.example.viewer3d.engine

import android.opengl.GLES20.*

class FrameBuffer(width : Int, height: Int) {

    private var frameBuffer : IntArray = IntArray(1)

    val colorTexture : Int
    val depthTexture : Int

    init {
        // Create a frame buffer
        glGenFramebuffers(1, frameBuffer, 0);

        val colorTex = Texture(GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE, width, height, GL_NEAREST)

        colorTexture = colorTex.textureID

        //colorTexture = IntArray(1)
        val depthTex = Texture(GL_DEPTH_COMPONENT, GL_DEPTH_COMPONENT,GL_UNSIGNED_SHORT, width, height, GL_NEAREST)
        depthTexture = depthTex.textureID

//        // Generate a texture to hold the colour buffer
//        glGenTextures(1, colorTexture, 0);
//        glBindTexture(GL_TEXTURE_2D, colorTexture[0]);
//
//        // Width and height do not have to be a power of two
//        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA,
//                width, height,
//                0, GL_RGBA, GL_UNSIGNED_BYTE, null);
//
////      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
////      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        // Probably just paranoia
        //glBindTexture(GL_TEXTURE_2D, 1);

        // Create a texture to hold the depth buffer
//        glGenTextures(1, depthTexture, 0);
//        glBindTexture(GL_TEXTURE_2D, depthTexture[0]);
//
//        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT,
//                width, height,
//                0, GL_DEPTH_COMPONENT, GL_UNSIGNED_SHORT, null);
//
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//
//       // glBindTexture(GL_TEXTURE_2D, 1);

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

    fun bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer[0])
    }

    fun unBind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }
}
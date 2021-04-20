package com.example.viewer3d.engine

class FrameBuffer {
    fun frameBufferCode(){
//        // Create a frame buffer
//        glGenFramebuffers( 1, &(frame_buffer ) );
//
//        // Generate a texture to hold the colour buffer
//        glGenTextures(1, &(colour_texture) );
//        glBindTexture(GL_TEXTURE_2D, colour_texture);
//        // Width and height do not have to be a power of two
//        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA,
//                pixelWidth, pixelHeight,
//                0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);
//
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//
//        // Probably just paranoia
//        glBindTexture(GL_TEXTURE_2D, 0);
//
//        // Create a texture to hold the depth buffer
//        glGenTextures(1, &(depth_texture) );
//        glBindTexture(GL_TEXTURE_2D, depth_texture);
//
//        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT,
//                pixelWidth, pixelHeight,
//                0, GL_DEPTH_COMPONENT, GL_UNSIGNED_SHORT, NULL);
//
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//
//        glBindTexture(GL_TEXTURE_2D, 0);
//
//        glBindFramebuffer(GL_FRAMEBUFFER, frame_buffer);
//
//        // Associate the textures with the FBO.
//
//        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
//                GL_TEXTURE_2D, colour_texture, 0);
//
//        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT,
//                GL_TEXTURE_2D, depth_texture, 0);
//
//        // Check FBO status.
//        GLenum status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
//
//        if ( status == GL_FRAMEBUFFER_COMPLETE )
//        {
//            // Success
//        }
    }
}
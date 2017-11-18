package com.example.rendertexture.Data;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.example.rendertexture.FBORender;
import com.example.rendertexture.R;
import com.example.rendertexture.Util.ShaderProgram;
import com.example.rendertexture.Util.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;

/**
 * Created by Jameskun on 2017/11/18.
 */

public class Square {

    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mTexCoordHandle;
    private int mMVPMatrixHandle;
    private int mTextureUnitHandle;

    private int particletexture;

    static final int COORDS_PER_VERTEX = 5;
    static float squareCoords[] = {
            -0.5f,  0.5f, 0.0f, 0.0f,1.0f,
            -0.5f, -0.5f, 0.0f, 0.0f,0.0f,
             0.5f, -0.5f, 0.0f, 1.0f,0.0f,
             0.5f,  0.5f, 0.0f, 1.0f,1.0f};

    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 };

    private final int vertexStride = COORDS_PER_VERTEX * 4;


    public Square(Context context) {

        ByteBuffer bb = ByteBuffer.allocateDirect(
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        mProgram = ShaderProgram.createProgram(context,"squareVS.glsl","squareFS.glsl");
        particletexture = TextureHelper.loadTexture(context, R.drawable.left);
    }

    public void draw(float[] mvpMatrix) {

        IntBuffer framebuffer = IntBuffer.allocate(1);
        IntBuffer texture = IntBuffer.allocate(1);

        GLES20.glGenFramebuffers(1, framebuffer);
        GLES20.glGenTextures(1, texture);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.get(0));
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, 480, 480,
                0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, null);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);

        // bind the framebuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer.get(0));
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, texture.get(0), 0);

        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if(status == GLES20.GL_FRAMEBUFFER_COMPLETE)
        {
            GLES20.glUseProgram(mProgram);
            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            GLES20.glEnableVertexAttribArray(mPositionHandle);

            mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_TextureCoordinates");
            GLES20.glEnableVertexAttribArray(mTexCoordHandle);

            vertexBuffer.position(0);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                    vertexStride, vertexBuffer);

            vertexBuffer.position(3);
            GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false,
                    vertexStride, vertexBuffer);


            mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            FBORender.checkGlError("glGetUniformLocation");

            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
            FBORender.checkGlError("glUniformMatrix4fv");

            mTextureUnitHandle =  glGetUniformLocation(mProgram,"u_TextureUnit");

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, particletexture);
            glUniform1i(mTextureUnitHandle, 0);

            GLES20.glDrawElements(
                    GLES20.GL_TRIANGLES, drawOrder.length,
                    GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

            GLES20.glDisableVertexAttribArray(mPositionHandle);

            // render to window system provided framebuffer
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            GLES20.glUseProgram(mProgram);

            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            GLES20.glEnableVertexAttribArray(mPositionHandle);

            mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_TextureCoordinates");
            GLES20.glEnableVertexAttribArray(mTexCoordHandle);

            vertexBuffer.position(0);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                    vertexStride, vertexBuffer);

            vertexBuffer.position(3);
            GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false,
                    vertexStride, vertexBuffer);


            mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            FBORender.checkGlError("glGetUniformLocation");

            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
            FBORender.checkGlError("glUniformMatrix4fv");

            mTextureUnitHandle =  glGetUniformLocation(mProgram,"u_TextureUnit");

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture.get(0));
            glUniform1i(mTextureUnitHandle, 0);

            GLES20.glDrawElements(
                    GLES20.GL_TRIANGLES, drawOrder.length,
                    GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

            GLES20.glDisableVertexAttribArray(mPositionHandle);

        }

        // cleanup
        GLES20.glDeleteFramebuffers(1, framebuffer);
        GLES20.glDeleteTextures(1, texture);
    }
}

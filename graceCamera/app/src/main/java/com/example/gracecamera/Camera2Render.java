package com.example.gracecamera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.example.gracecamera.Data.PreviewQuad;
import com.example.gracecamera.Data.WhiteBlackEffect;
import com.example.gracecamera.Program.PreviewProgram;
import com.example.gracecamera.Program.WhiteBlackProgram;
import com.example.gracecamera.Util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_FRAMEBUFFER_COMPLETE;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_RGB;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT_5_6_5;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glCheckFramebufferStatus;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glViewport;

/**
 * Created by 123 on 2017/11/19.
 */

public class Camera2Render implements GLSurfaceView.Renderer {

    private static final String TAG = "Camera2Render";

    private Context mContex;
    Camera2View mCamera2View;
    CameraV2 mCameraV2;

    private boolean bIsPreviewStarted = false;
    private int mTextureId;
    private SurfaceTexture mSurfaceTexture;

    private float[]  mTransformMatrix = new float[16];
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    private PreviewQuad mPreviewQuad;
    private PreviewProgram mPreviewProgram;

    private WhiteBlackEffect mWhiteBlackEffect;
    private WhiteBlackProgram mWhiteBlackProgram;

    private int[] mFrameBuffer = new int[1];
    private int[] mTexture = new int[1];

    public void init(Camera2View camera2View,CameraV2 cameraV2,Context context){
        mContex = context;
        mCamera2View = camera2View;
        mCameraV2 = cameraV2;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 1.0f, 0.0f, 0.0f);

        mTextureId = TextureHelper.CreateTextureObject();
        mPreviewQuad = new PreviewQuad();
        mPreviewProgram = new PreviewProgram(mContex,"previewVertexShader.glsl","previewFragmentShader.glsl");

        mWhiteBlackEffect = new WhiteBlackEffect();
        mWhiteBlackProgram = new WhiteBlackProgram(mContex,"blackWhiteEffectVS.glsl","blackWhiteEffectFS.glsl");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

        //frame buffer config
        glGenFramebuffers(1,mFrameBuffer,0);
        glBindFramebuffer(GL_FRAMEBUFFER, mFrameBuffer[0]);

        glGenTextures(1,mTexture,0);
        glBindTexture(GL_TEXTURE_2D, mTexture[0]);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_SHORT_5_6_5, null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        GLES20.glFramebufferTexture2D(GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, mTexture[0], 0);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
        {
            Log.e(TAG,"Frame buffer config fail.");
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        glBindFramebuffer(GL_FRAMEBUFFER, mFrameBuffer[0]);

        if (mSurfaceTexture != null){
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(mTransformMatrix);
        }

        if(!bIsPreviewStarted){
            bIsPreviewStarted = initSurfaceTexture();
        }

        glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        drawPreview();

        //draw to window
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        drawEffect();
    }

    private void drawPreview(){
        mPreviewProgram.useProgram();
        mPreviewProgram.setUniforms(mTransformMatrix,mTextureId);
        mPreviewQuad.draw(mPreviewProgram);
    }

    private void drawEffect(){
        mWhiteBlackProgram.useProgram();
        mWhiteBlackProgram.setUniforms(mMVPMatrix,mTexture[0]);
        mWhiteBlackEffect.draw(mWhiteBlackProgram);
    }

    private boolean initSurfaceTexture() {
        if (mCameraV2 == null || mCamera2View == null) {
            Log.e(TAG, "mCamera or mGLSurfaceView is null!");
            return false;
        }

        mSurfaceTexture = new SurfaceTexture(mTextureId);
        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                mCamera2View.requestRender();
            }
        });
        mCameraV2.setPreviewTexture(mSurfaceTexture);
        mCameraV2.startPreview();
        return true;
    }

}

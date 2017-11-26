package com.example.gracecamera.Program;

import android.content.Context;
import android.opengl.GLES11Ext;

import com.example.gracecamera.Util.ShaderHelper;
import com.example.gracecamera.Util.TextResourceReader;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE1;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static com.example.gracecamera.Util.LoggerConfig.checkGlError;

/**
 * Created by 123 on 2017/11/26.
 */

public class CameraPreviewProgram {

    //Attribute
    private final int aPositionLocation;
    private final int aTexCoordLocation;

    //Uniform
    private final int uMVPMatrixLocation;
    private final int uTextureMatrixLocation;
    private final int uTextureSampler;

    protected final int mProgram;

    public CameraPreviewProgram(Context context,String vertexPath,String fragmentPath){

        mProgram = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(context,vertexPath),
                TextResourceReader.readTextFileFromResource(context,fragmentPath));

        aPositionLocation = glGetAttribLocation(mProgram,"aPosition");
        checkGlError("glGetAttribLocation aPosition");

        aTexCoordLocation = glGetAttribLocation(mProgram,"aTextureCoordinate");
        checkGlError("glGetAttribLocation aTextureCoordinate");

        uMVPMatrixLocation = glGetUniformLocation(mProgram,"uMVPMatrix");
        checkGlError("glGetUniformLocation uMVPMatrix");

        uTextureMatrixLocation = glGetUniformLocation(mProgram,"uTextureMatrix");
        checkGlError("glGetUniformLocation uTextureMatrix");

        uTextureSampler = glGetUniformLocation(mProgram,"uTextureSampler");
        checkGlError("glGetUniformLocation uTextureSampler");
    }

    public void setUniform(float[] mvpMatrix,float[] textureMatrix,int textureId){

        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0);
        checkGlError("glUniformMatrix4fv uMVPMatrixLocation");

        glUniformMatrix4fv(uTextureMatrixLocation, 1, false, textureMatrix, 0);
        checkGlError("glUniformMatrix4fv uTextureMatrixLocation");

        glActiveTexture(GL_TEXTURE0);
        checkGlError("glActiveTexture texture");
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        checkGlError("glBindTexture texture");
        glUniform1i(uTextureSampler, 0);
        checkGlError("glUniform1i texture");
    }

    public void useProgram(){
        glUseProgram(mProgram);
        checkGlError("useProgram");
    }

    public int getPositionAttributeLocation(){
        return aPositionLocation;
    }

    public int getTexCoordAttributeLocation(){
        return aTexCoordLocation;
    }
}

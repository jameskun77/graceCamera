package com.example.gracecamera.Program;

import android.content.Context;

import com.example.gracecamera.Util.ShaderHelper;
import com.example.gracecamera.Util.TextResourceReader;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static com.example.gracecamera.Util.LoggerConfig.checkGlError;

/**
 * Created by 123 on 2017/11/26.
 */

public class PreviewProgram {

    //uniform
    private final int mTextureLocation;
    private final int mMVPMtrixLocation;



    protected final int mProgram;

    public PreviewProgram(Context context, String vertexPath, String fragmentPath){

        mProgram = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(context,vertexPath),
                TextResourceReader.readTextFileFromResource(context,fragmentPath));

        mTextureLocation = glGetUniformLocation(mProgram,"uTextureUnit");
        checkGlError("glGetUniformLocation texture1");

        mMVPMtrixLocation = glGetUniformLocation(mProgram,"uMVPMatrix");
        checkGlError("glGetUniformLocation mMVPMatrix");

    }

    public void useProgram(){
        glUseProgram(mProgram);
        checkGlError("useProgram");
    }

    public void setUniform(float[] matrix,int textureId1){
        glUniformMatrix4fv(mMVPMtrixLocation, 1, false, matrix, 0);

        glActiveTexture(GL_TEXTURE0);
        checkGlError("glActiveTexture texture");
        glBindTexture(GL_TEXTURE_2D, textureId1);
        checkGlError("glBindTexture texture");
        glUniform1i(mTextureLocation, 0);
        checkGlError("glUniform1i texture");
    }
}

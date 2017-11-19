package com.example.gracecamera.Program;

import android.content.Context;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by 123 on 2017/11/19.
 */

public class PreviewProgram extends ShaderProgram {

    //Attribute
    private final int aPositionLocation;
    private final int aTexCoordLocation;

    //uniform
    private final int uTextureUnitLocation;
    private final int uTextureMatrixLocation;


    public PreviewProgram(Context context, String vertexPath, String fragmentPath){

        super(context,vertexPath,fragmentPath);

        aPositionLocation = glGetAttribLocation(mProgram,aPosition);
        glEnableVertexAttribArray(aPositionLocation);
        aTexCoordLocation = glGetAttribLocation(mProgram,aTextureCoordinates);
        glEnableVertexAttribArray(aTexCoordLocation);

        uTextureUnitLocation = glGetUniformLocation(mProgram,uTextureUnit);
        uTextureMatrixLocation = glGetUniformLocation(mProgram,uTextureMatrix);
    }

    public void setUniforms(float[] textureMatrix, int textureId) {
        glUniformMatrix4fv(uTextureMatrixLocation, 1, false, textureMatrix, 0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation(){
        return aPositionLocation;
    }

    public int getTexCoordAttributeLocation(){
        return aTexCoordLocation;
    }
}

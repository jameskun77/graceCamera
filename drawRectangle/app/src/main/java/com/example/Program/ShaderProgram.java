package com.example.Program;

import android.content.Context;

import com.example.Util.ShaderHelper;
import com.example.Util.TextResourceReader;

import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glUseProgram;

/**
 * Created by 123 on 2017/11/25.
 */

public class ShaderProgram {

    //attribute
    protected static final String aPosition = "aPos";

    //Attribute
    private final int aPositionLocation;

    protected final int mProgram;

    public ShaderProgram(Context context, String vertexPath, String fragmentPath){
        mProgram = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(context,vertexPath),
                TextResourceReader.readTextFileFromResource(context,fragmentPath));

        aPositionLocation = glGetAttribLocation(mProgram,aPosition);
        glEnableVertexAttribArray(aPositionLocation);
    }

    public void useProgram(){
        glUseProgram(mProgram);
    }

    public int getPositionAttributeLocation(){
        return aPositionLocation;
    }

}

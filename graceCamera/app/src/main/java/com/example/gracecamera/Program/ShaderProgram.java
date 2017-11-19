package com.example.gracecamera.Program;

import android.content.Context;

import com.example.gracecamera.Util.ShaderHelper;
import com.example.gracecamera.Util.TextResourceReader;

import static android.opengl.GLES20.glUseProgram;

/**
 * Created by 123 on 2017/11/19.
 */

public class ShaderProgram {

    //uniform
    protected static final String uMVPMatrix = "uMVPMatrix";
    protected static final String uTextureUnit = "uTextureSampler";
    protected static final String uTextureMatrix = "uTextureMatrix";

    //attribute
    protected static final String aPosition = "aPosition";
    protected static final String aTextureCoordinates = "aTextureCoordinate";

    protected final int mProgram;

    public ShaderProgram(Context context, String vertexPath, String fragmentPath){
        mProgram = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(context,vertexPath),
                TextResourceReader.readTextFileFromResource(context,fragmentPath));
    }

    public void useProgram(){
        glUseProgram(mProgram);
    }
}

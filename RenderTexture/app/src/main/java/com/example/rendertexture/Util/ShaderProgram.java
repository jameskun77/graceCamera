package com.example.rendertexture.Util;

import android.content.Context;
import android.content.res.Resources;

import com.example.rendertexture.FBORender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glUseProgram;

/**
 * Created by Jameskun on 2017/11/18.
 */

public class ShaderProgram {

    public static String readTextFileFromResource(Context context, String str){
        StringBuilder body = new StringBuilder();

        try {
            InputStream inputStream =
                    context.getResources().getAssets().open(str);
            InputStreamReader inputStreamReader =
                    new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String nextLine;
            while ((nextLine = bufferedReader.readLine()) != null)
            {
                body.append(nextLine);
                body.append('\n');
            }

        }catch (IOException e){
            throw new RuntimeException("Could not open resource: " + str,e);

        }catch (Resources.NotFoundException nfe){
            throw new RuntimeException("Resource not found: " + str,nfe);
        }

        return body.toString();
    }

    public static int createProgram(Context context, String vertexPath, String fragmentPath){
        int vertexShader,fragmentShader,program;

        vertexShader = glCreateShader(GL_VERTEX_SHADER);
        FBORender.checkGlError("glCreateShader");
        if (vertexShader == 0) {
            throw new RuntimeException("Create Vertex Shader Failed!" + glGetError());
        }
        glShaderSource(vertexShader, readTextFileFromResource(context,vertexPath));
        glCompileShader(vertexShader);

        fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        if(fragmentShader == 0){
            throw new RuntimeException("Create Fragment Shader Failed!" + glGetError());
        }
        glShaderSource(fragmentShader, readTextFileFromResource(context,fragmentPath));
        glCompileShader(fragmentShader);

        program = glCreateProgram();
        if (program == 0) {
            throw new RuntimeException("Create Program Failed!" + glGetError());
        }
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);

        glUseProgram(program);
        return program;
    }
}

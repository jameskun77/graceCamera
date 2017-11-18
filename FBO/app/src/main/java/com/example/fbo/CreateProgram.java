package com.example.fbo;

import android.content.Context;

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

public class CreateProgram {

    public static int createProgram(Context context, String vertexPath, String fragmentPath){
        int vertexShader,fragmentShader,program;

        vertexShader = glCreateShader(GL_VERTEX_SHADER);
        if (vertexShader == 0) {
            throw new RuntimeException("Create Vertex Shader Failed!" + glGetError());
        }
        glShaderSource(vertexShader, ShaderReader.readTextFileFromResource(context,vertexPath));
        glCompileShader(vertexShader);

        fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        if(fragmentShader == 0){
            throw new RuntimeException("Create Fragment Shader Failed!" + glGetError());
        }
        glShaderSource(fragmentShader, ShaderReader.readTextFileFromResource(context,fragmentPath));
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

package com.example;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.example.Data.Triangle;
import com.example.Program.TriangleProgram;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

/**
 * Created by 123 on 2017/11/25.
 */

public class RectangleRender implements GLSurfaceView.Renderer {

    private static final String TAG = "DrawRectRender";

    private Triangle mTriangle;
    private TriangleProgram mTriangleProgram;

    private Context mContex;

    public RectangleRender(Context context){
        mContex = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        mTriangle = new Triangle();
        mTriangleProgram = new TriangleProgram(mContex,"triangleES3_VS.glsl","triangleES3_FS.glsl");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        drawTriangle();
    }

    void drawTriangle(){
        mTriangleProgram.useProgram();
        mTriangle.draw(mTriangleProgram);
    }
}

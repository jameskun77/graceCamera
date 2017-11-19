package com.example.gracecamera;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by 123 on 2017/11/19.
 */

public class Camera2View extends GLSurfaceView {

    private Camera2Render mCamera2Render;

    public Camera2View(Context context){
        super(context);
    }

    public void init(CameraV2 camera,Context context)
    {
        setEGLContextClientVersion(2);
        mCamera2Render = new Camera2Render();
        mCamera2Render.init(this,camera,context);

        setRenderer(mCamera2Render);
    }
}

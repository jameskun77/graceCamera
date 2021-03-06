package com.example.gracecamera;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

/**
 * Created by 123 on 2017/11/26.
 */

public class CameraActivity extends Activity implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "CameraActivity_TAG";

    private GLSurfaceView mGLSurfaceView;
    private CameraRender mCameraRender;

    private CameraV2 mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //开启相机权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 10);
        }

        setContentView(R.layout.activity_main);
        mGLSurfaceView = (GLSurfaceView)findViewById(R.id.glsurfaceview) ;
        ((SeekBar) findViewById(R.id.seekBarSkin)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.seekBarWhite)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.seekBarSharp)).setOnSeekBarChangeListener(this);

        //check system support es2.0
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        int supportEsVersion = configurationInfo.reqGlEsVersion;
        final boolean supportEs2 = supportEsVersion >= 0x20000;

        if(supportEs2){
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            mCamera = new CameraV2(this);
            mCamera.setupCamera(dm.widthPixels,dm.heightPixels);

            if(!mCamera.openCamera()){
                Log.e(TAG,"mCamera.openCamera fail");
                return;
            }

            mGLSurfaceView.setEGLContextClientVersion(2);
            mCameraRender = new CameraRender();
            mCameraRender.init(CameraActivity.this,mGLSurfaceView,mCamera);

            mGLSurfaceView.setRenderer(mCameraRender);
        }
        else {
            Toast.makeText(this,"This device does not support OpenGL ES 2.0.",
                    Toast.LENGTH_LONG).show();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    protected void onPause(){
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onProgressChanged(final  SeekBar seekBar,final int progress,
                                  final boolean fromUser){
        //Log.i(TAG,"onProgressChanged");
        switch (seekBar.getId()){
            case R.id.seekBarSkin:
            {
                float yuhua = 1.01f + progress/40.0f;
                mCameraRender.setYuHuaParam(yuhua);
            }
            break;
            case R.id.seekBarWhite:
            {
                float white = -1.0f + progress/30.0f;
                mCameraRender.setWhiteParam(white );
            }
            break;
            case R.id.seekBarSharp:
            {
                float sharp = 0.05f + (float)(progress)/ 100.0f * 0.60f;
                mCameraRender.setSharpParam(sharp);
            }
            break;
        }
    }

    @Override
    public void onStartTrackingTouch(final SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
    }
}

package com.example.gracecamera;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Created by 123 on 2017/11/19.
 */

public class CameraPreviewActivity extends Activity {

    private static final String TAG = "Activity";

    private Camera2View mCamera2View;
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

        mCamera2View = new Camera2View(this);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mCamera = new CameraV2(this);
        mCamera.setupCamera(dm.widthPixels,dm.heightPixels);

        if(!mCamera.openCamera()){
            Log.e(TAG,"mCamera.openCamera fail");
            return;
        }

        mCamera2View.init(mCamera,CameraPreviewActivity.this);
        setContentView(mCamera2View);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCamera2View.onResume();
    }

    protected void onPause(){
        super.onPause();
        mCamera2View.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

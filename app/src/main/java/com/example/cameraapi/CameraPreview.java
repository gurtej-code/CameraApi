package com.example.cameraapi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;

import static android.content.ContentValues.TAG;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Activity mActivity;

    public CameraPreview(Context context, Camera camera){
        super(context);
        mCamera=camera;
        mActivity=(Activity)context;

        mHolder=getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Camera.Parameters params=mCamera.getParameters();
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        }catch (IOException ex){
            Log.d(TAG,"Error Setting Camera Preview"+ex.getMessage());
        }


        if(params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
        {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        mCamera.setParameters(params);

        if(this.getResources().getConfiguration().orientation!=Configuration.ORIENTATION_LANDSCAPE){
            params.set("rotation",90);
            mCamera.setDisplayOrientation(90);
            params.setRotation(90);
        }else {
            params.set("orientation","landscape");
            int angle=DisplayOrientation();
            mCamera.setDisplayOrientation(angle);
            params.setRotation(angle);
        }
    }

    private int DisplayOrientation() {
        int rotation=mActivity.getWindowManager().getDefaultDisplay().getRotation();
        int degree=0;
        switch (rotation){
            case Surface.ROTATION_0:
                degree=0;
                break;
            case  Surface.ROTATION_270:
                degree=180;
                break;
        }
        return degree;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(mHolder.getSurface()==null){
            return;
        }
        try {
        }catch (Exception e){}


        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        }catch (Exception e){
            Log.d(TAG,"Error Starting Camera Preview"+e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }
}

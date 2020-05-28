package com.example.cameraapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private boolean check;
    private Camera mCamera;
    private CameraPreview mPreview;
    FloatingActionButton button,button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=findViewById(R.id.button);
        button1=findViewById(R.id.rotate);

        check=checkCameraHardware(this);
        if(check==true){
            mCamera=getCameraInstance();
            mPreview=new CameraPreview(this,mCamera);
            FrameLayout preview=(FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);

        }else{
            Toast.makeText(this,"This is not Working",Toast.LENGTH_LONG).show();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCamera!=null){
                    mCamera.takePicture(null,null,mPicture);
              }
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCameraPrivew();
            }
        });
    }

    private void switchCameraPrivew() {
        mCamera.stopPreview();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            releaseInstance();
        }
    }

    private Camera.PictureCallback mPicture= new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile=getOutputMediaFile();

            if(pictureFile==null){
                Log.d("Picture","Error Creating media file , check storage permission");
                return;
            }
            try {

                FileOutputStream fos=new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();

                Toast.makeText(MainActivity.this,"Image Captured",Toast.LENGTH_LONG).show();
                mCamera.startPreview();

            } catch (FileNotFoundException e) {
                Log.d("File","File Not Found"+e.getMessage());
            } catch (IOException e){
                Log.d("AccessingFile","Error in Accessing File"+e.getMessage());
            }
            galleryAddPic(pictureFile.toString());
        }
    };

    private static File getOutputMediaFile() {
        String isPresent = Environment.getExternalStorageState();
        if (!isPresent.equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }else{
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "MyCameraApp");
            if(!mediaStorageDir.exists()){
                if(!mediaStorageDir.mkdirs()){
                    Log.d("MyCameraApp", "failed to create directory");
                    return null;
                }
            }
            String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile=new File(mediaStorageDir.getPath()+File.separator+"IMG_"+timeStamp+".jpg");
            return mediaFile;
        }
    }

    private Camera getCameraInstance() {
        Camera c=null;
        try {
            c=Camera.open();
        }catch (Exception e){

        }
        return c;
    }

    private boolean checkCameraHardware(Context context) {
        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            return true;
        }else{
            return false;
        }
    }

    private void galleryAddPic(String currentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}

package com.igr.camscanner.com.igr.camscanner.camerawork;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.igr.camscanner.R;
import com.igr.camscanner.com.igr.camscanner.custom.CustomProgressDialog;
import com.igr.camscanner.com.igr.camscanner.db.MyPrefs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by DEVEN SINGH on 1/20/2015.
 */
public class ImageCaptureActivity extends Activity implements View.OnClickListener {

    ImageButton captureImage;
    ImageButton settingButton;
    protected static final int MEDIA_TYPE_IMAGE = 1427;
    private CameraPreview mPreview;
    private FrameLayout preview;
    private Camera mCamera;
    Camera.Parameters dCP;
    MyPrefs myPrefs;
    File pictureFile = null;
    CustomProgressDialog customProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_layout);
        //  mCamera = getCameraInstance();
        initPalletes();
        customProgressDialog = new CustomProgressDialog(ImageCaptureActivity.this, R.drawable.progress_img);
    }

    private Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            alertCameraServiceFailed(e.getMessage());
        }
        return c;
    }

    private void alertCameraServiceFailed(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ImageCaptureActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog));
        builder.setTitle("Alert")
                .setMessage(message + "." + "Please restart your device.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {

        int result = ImageCaptureActivity.getCameraDisplayOrientation(activity,
                cameraId, camera);
        if (android.os.Build.VERSION.SDK_INT <= 14) {
            camera.stopPreview();
            camera.setDisplayOrientation(result);
            camera.startPreview();
        } else {
            camera.setDisplayOrientation(result);
        }
    }

    public static int getCameraDisplayOrientation(Activity activity,
                                                  int cameraId, android.hardware.Camera camera) {
        Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }

        return result;
    }

    //returns current exif orientation of the image being captured, by taking the fixed orientation of the camera and
    //subtracting the current device orientation
    private int getScreenOrientation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(0, info);

        int orientation = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                orientation = 0;
                break;
            case Surface.ROTATION_90:
                orientation = 90;
                break;
            case Surface.ROTATION_180:
                orientation = 180;
                break;
            case Surface.ROTATION_270:
                orientation = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + orientation) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - orientation + 360) % 360;
        }

        switch (result) {
            case 0:
                orientation = ExifInterface.ORIENTATION_NORMAL;
                break;
            case 90:
                orientation = ExifInterface.ORIENTATION_ROTATE_90;
                break;
            case 180:
                orientation = ExifInterface.ORIENTATION_ROTATE_180;
                break;
            case 270:
                orientation = ExifInterface.ORIENTATION_ROTATE_270;
                break;
            default:
                Log.e("PHOTO", "Unknown screen orientation. Defaulting to " +
                        "portrait.");
                orientation = ExifInterface.ORIENTATION_UNDEFINED;
                break;
        }

        return orientation;
    }

    private void saveImageOrientation() {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(pictureFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("orientation   " + String.valueOf(getScreenOrientation()));
        if (exif != null) {
            exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(getScreenOrientation()));
            try {
                exif.saveAttributes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void initPalletes() {
        myPrefs = new MyPrefs(getApplicationContext());
//        customProgressDialog=new CustomProgressDialog(getApplicationContext(),R.drawable.progress_img);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        captureImage = (ImageButton) findViewById(R.id.captureImg);
        settingButton = (ImageButton) findViewById(R.id.setBt);
        captureImage.setOnClickListener(this);
    }

    boolean isDeviceZoomSupported() {
        dCP = mCamera.getParameters();
        if (dCP.isZoomSupported()) {
            System.out.println("Yaahoooo!! Zoom supported.");
            return true;
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
        preview.removeView(mPreview);
        mPreview = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCameraInstance();
        }
        try {
            if (mPreview == null) {
                mPreview = new CameraPreview(getApplicationContext(), mCamera, this);
                preview.addView(mPreview);
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            // mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
        if (mPreview != null) {
            mPreview.getHolder().removeCallback(mPreview);
        }

    }


    //*****************************Image Capture**************************************//


    Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            new SavePhotoTask().execute(data);
//            mCamera.startPreview();
//            captureImage.setEnabled(true);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.captureImg:
                mCamera.takePicture(null, null, mPicture);
                break;
        }
    }

    class SavePhotoTask extends AsyncTask<byte[], String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customProgressDialog.show();
        }

        @Override
        protected String doInBackground(byte[]... data) {


            try {
                pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                System.out.println("picture path " + pictureFile.getAbsolutePath());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            if (pictureFile == null) {

                return null;
            }

            byte[] photoData = data[0];
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(photoData);
                fos.close();
                saveImageOrientation();
            } catch (FileNotFoundException e) {
                Log.d("DEV", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("DEV", "Error accessing file: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            customProgressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Saved",
                    Toast.LENGTH_SHORT).show();
            Intent iImgEditor = new Intent(getApplicationContext(), ImageEditor.class);
            iImgEditor.putExtra("ImagePath", pictureFile.getAbsolutePath());
            ImageCaptureActivity.this.finish();
            startActivity(iImgEditor);
        }
    }


    private File getOutputMediaFile(int type) throws IOException {

        File mediaFile;
        File dir = new File(Environment.getExternalStorageDirectory(),
                "/M_CamScanner");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File imgFolder = new File(dir.getAbsolutePath(), "/.Images");
        if (!imgFolder.exists()) {
            imgFolder.mkdirs();
        }
        if (type == 1427) {
            File imgFolder1 = new File(imgFolder.getAbsolutePath(), "/.Main");
            if (!imgFolder1.exists()) {
                imgFolder1.mkdirs();
            }
            mediaFile = new File(imgFolder1.getAbsolutePath() + File.separator
                    + "CamScanner_Img" + myPrefs.getDocCount() + ".jpg");
            myPrefs.setDocCount(myPrefs.getDocCount() + 1);
        } else {
            return null;
        }

        return mediaFile;
    }
}


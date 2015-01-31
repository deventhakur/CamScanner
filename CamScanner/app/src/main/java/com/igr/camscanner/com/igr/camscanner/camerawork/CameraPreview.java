package com.igr.camscanner.com.igr.camscanner.camerawork;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.igr.camscanner.R;

import java.io.IOException;

/**
 * Class for camera preview
 *
 * @author - Deven Singh will provide current live preview
 */

public class CameraPreview extends SurfaceView implements
        SurfaceHolder.Callback, Camera.OnZoomChangeListener {

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Activity activity;
    Camera.Parameters dCP;
    SeekBar seekBar;

    public CameraPreview(Context context) {
        super(context);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public CameraPreview(Context context, Camera camera, Activity activity) {
        super(context);

        this.activity = activity;
        this.mCamera = camera;
        seekBar = (SeekBar) activity.findViewById(R.id.seek_bar);
        this.mHolder = getHolder();
        this.mHolder.addCallback(this);
        this.mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mCamera.setZoomChangeListener(this);
    }

    public void surfaceCreated(SurfaceHolder holder) {

        try {
            CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
            mCamera.setParameters(parameters);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {

        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        if (mHolder.getSurface() == null) {
            return;
        }

        try {

            ImageCaptureActivity.setCameraDisplayOrientation(activity,
                    Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
        } catch (Exception e) {

        }

        try {
            zoomControl();
        } catch (Exception e) {

        }

    }

    void zoomControl() {
        dCP = mCamera.getParameters();
        if (dCP.isZoomSupported() && dCP.isSmoothZoomSupported()) {
            mCamera.startSmoothZoom(1);
        } else if (dCP.isZoomSupported() && !dCP.isSmoothZoomSupported()) {
            dCP.setZoom(1);
            mCamera.setParameters(dCP);
        }
        seekBar.setProgress(1);
        seekBar.setMax(dCP.getMaxZoom());
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (fromUser) {
                    if (dCP.isZoomSupported() && dCP.isSmoothZoomSupported()) {
                        try {
                            mCamera.startSmoothZoom(progress);
                        } catch (IllegalStateException ex) {

                        } catch (Exception exc) {

                        }
                    } else if (dCP.isZoomSupported()
                            && !dCP.isSmoothZoomSupported()) {
                        dCP.setZoom(progress);
                        mCamera.setParameters(dCP);
                    }
                }
            }
        });
    }

    @Override
    public void onZoomChange(int zoomValue, boolean stopped, Camera camera) {
        System.out.println("Stop "+zoomValue);
    }
}

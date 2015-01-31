package com.igr.camscanner.com.igr.camscanner.camerawork;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Toast;

import com.edmodo.cropper.CropImageView;
import com.igr.camscanner.R;
import com.igr.camscanner.com.igr.camscanner.custom.CustomProgressDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by DEVEN SINGH on 1/22/2015.
 */
public class ImageEditor extends Activity implements View.OnClickListener, PopupWindow.OnDismissListener {

    ImageView editImage;
    Bundle extras;
    CropImageView cropImageView;
    Bitmap capturedImage;
    ImageButton cropImageBt;
    ImageButton closeBt;
    ImageButton rotateLeftBt;
    ImageButton rotateRightBt;
    ImageButton tuningBt;
    ImageButton acceptImg;
    LinearLayout llCrop;
    LinearLayout llCropped;
    int dpWidth;
    int dpHeight;
    PopupWindow popupWindow;
    Bitmap croppedImageBitmap;
    //Bitmap newBitmap;

    CustomProgressDialog customProgressDialog;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editor);
        extras = getIntent().getExtras();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        dpWidth = displayMetrics.widthPixels;
        dpHeight = displayMetrics.heightPixels;
        initPalletes();
        setBitmapToCropImageView();
        customProgressDialog = new CustomProgressDialog(ImageEditor.this, R.drawable.progress_img);
    }

    private void setBitmapToCropImageView() {
        if (extras != null) {
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(extras.getString("ImagePath"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = getRotation(exif.getAttribute(ExifInterface.TAG_ORIENTATION));
            final BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
            //If set to a value > 1,requests the decoder to sub sample the
            //original image, returning a smaller image to save memory.
//            options.inSampleSize = calculateInSampleSize(options,options.outWidth,options.outHeight);
//            options.inJustDecodeBounds = false;
            options.inSampleSize=2;
            capturedImage = BitmapFactory.decodeFile(extras.getString("ImagePath"), options);
            System.out.println("bitmap size: "+capturedImage.getWidth()+"  "+capturedImage.getHeight());
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);
            capturedImage = Bitmap.createBitmap(capturedImage, 0, 0, capturedImage.getWidth(), capturedImage.getHeight(), matrix, true);
            cropImageView.setImageBitmap(capturedImage);
            cropImageView.setAspectRatio(1, 1);

        }
    }


//    public int calculateInSampleSize(
//            BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        // Raw height and width of image
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth) {
//
//            final int halfHeight = height / 2;
//            final int halfWidth = width / 2;
//
//            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
//            // height and width larger than the requested height and width.
//            while ((halfHeight / inSampleSize) > reqHeight
//                    && (halfWidth / inSampleSize) > reqWidth) {
//                inSampleSize *= 2;
//            }
//        }
// System.out.println("size "+inSampleSize);
//        return inSampleSize;
//    }

    private int getRotation(String attribute) {
        int orienation = 0;
        switch (attribute) {
            case "1":
                orienation = 0;
                break;
            case "6":
                orienation = 90;
                break;
            case "3":
                orienation = 180;
                break;
            case "8":
                orienation = 270;
                break;
            default:
                orienation = 0;
                break;
        }
        return orienation;
    }

    private void initPalletes() {
        editImage = (ImageView) findViewById(R.id.editImage);
        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        cropImageBt = (ImageButton) findViewById(R.id.cropBt);
        closeBt = (ImageButton) findViewById(R.id.closeBt);
        rotateLeftBt = (ImageButton) findViewById(R.id.rotateLeftBt);
        rotateRightBt = (ImageButton) findViewById(R.id.rotateRightBt);
        tuningBt = (ImageButton) findViewById(R.id.tuningButton);
        acceptImg= (ImageButton) findViewById(R.id.accept);
        llCrop = (LinearLayout) findViewById(R.id.ll_crop);
        llCropped = (LinearLayout) findViewById(R.id.ll_cropped);
        cropImageBt.setOnClickListener(this);
        closeBt.setOnClickListener(this);
        rotateRightBt.setOnClickListener(this);
        rotateLeftBt.setOnClickListener(this);
        tuningBt.setOnClickListener(this);
        acceptImg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cropBt:
                setCroppedImageToEditImageView();
                llCrop.setVisibility(View.GONE);
                llCropped.setVisibility(View.VISIBLE);
                break;
            case R.id.closeBt:
                finish();
                break;
            case R.id.rotateLeftBt:
                cropImageView.rotateImage(270);
                break;
            case R.id.rotateRightBt:
                cropImageView.rotateImage(90);
                break;
            case R.id.tuningButton:
                enhancementPopUp();
                break;
            case R.id.accept:
                new SaveEditedImageTask().execute();
                break;
        }
    }

    private void saveImageBitmap() {
        File dir = new File(Environment.getExternalStorageDirectory(),
                "/M_CamScanner");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File imgFolder = new File(dir.getAbsolutePath(), "/.Images");
        if (!imgFolder.exists()) {
            imgFolder.mkdirs();
        }
        try {
            FileOutputStream out = new FileOutputStream(imgFolder+File.separator+getFileName());
            getBitmapFromImage().compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFileName() {
        String path=extras.getString("ImagePath");
        String name=path.substring(path.indexOf(".Main/") + 6);
        return name;
    }

    private Bitmap getBitmapFromImage() {
        return ((BitmapDrawable)editImage.getDrawable()).getBitmap();
    }

    BitmapWorkerTask bitmapWorkerTask;

    private void enhancementPopUp() {
        try {
            LayoutInflater inflater = (LayoutInflater) ImageEditor.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.contrast_brightness_window, null);
            popupWindow = new PopupWindow(layout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setOutsideTouchable(true);
            popupWindow.setOnDismissListener(ImageEditor.this);
            popupWindow.showAtLocation(layout, Gravity.BOTTOM, 66, 66);
            final SeekBar seekBarContrast = (SeekBar) layout.findViewById(R.id.seekBarContrast);
            final SeekBar seekBarBright = (SeekBar) layout.findViewById(R.id.seekBarBrightness);
            seekBarContrast.setMax(10);
            seekBarBright.setMax(510);
            seekBarContrast.setProgress((1));
            seekBarBright.setProgress((255));
            seekBarContrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // newBitmap = changeBitmapContrastBrightness(croppedImageBitmap, (float) (progress - 1), (float) (seekBarBright.getProgress() - 255));
//                    editImage.setImageBitmap(changeBitmapContrastBrightness(croppedImageBitmap, (float) (progress - 1), (float) (seekBarBright.getProgress() - 255)));
                    bitmapWorkerTask = new BitmapWorkerTask((float) (progress - 1), (float) (seekBarBright.getProgress() - 255));
                    bitmapWorkerTask.execute(croppedImageBitmap);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            seekBarBright.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    //  newBitmap = changeBitmapContrastBrightness(croppedImageBitmap, (float)(seekBarContrast.getProgress()-1), (float)(progress-255));
//                    editImage.setImageBitmap(changeBitmapContrastBrightness(croppedImageBitmap, (float)(seekBarContrast.getProgress()-1), (float)(progress-255)));
                    bitmapWorkerTask = new BitmapWorkerTask((float) (seekBarContrast.getProgress() - 1), (float) (progress - 255));
                    bitmapWorkerTask.execute(croppedImageBitmap);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Bitmap changeBitmapContrastBrightness(Bitmap croppedImageBitmap, float contrast, float brightness) {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(croppedImageBitmap.getWidth(), croppedImageBitmap.getHeight(), croppedImageBitmap.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(croppedImageBitmap, 0, 0, paint);

        return ret;
    }


    private void setCroppedImageToEditImageView() {
        croppedImageBitmap = cropImageView.getCroppedImage();
        cropImageView.setVisibility(View.GONE);
        editImage.setVisibility(View.VISIBLE);
        editImage.setImageBitmap(croppedImageBitmap);
    }


    @Override
    public void onDismiss() {

    }

    class BitmapWorkerTask extends AsyncTask<Bitmap, Void, Bitmap> {
        private float contrast = 0.0f;
        private float brightness = 0.0f;

        public BitmapWorkerTask(float contrast, float brightness) {
            this.contrast = contrast;
            this.brightness = brightness;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Bitmap... params) {

            return changeBitmapContrastBrightness(params[0], contrast, brightness);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                editImage.setImageBitmap(bitmap);
            }
        }
    }

    class SaveEditedImageTask extends AsyncTask<Void,Void,Void>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customProgressDialog.show();
        }


        @Override
        protected Void doInBackground(Void... params) {
            saveImageBitmap();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            customProgressDialog.dismiss();
            Toast.makeText(ImageEditor.this,"Image saved",Toast.LENGTH_SHORT).show();
        }
    }
}
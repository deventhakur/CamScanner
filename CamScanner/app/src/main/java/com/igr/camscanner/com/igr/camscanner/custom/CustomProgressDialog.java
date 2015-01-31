package com.igr.camscanner.com.igr.camscanner.custom;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.igr.camscanner.R;

/**
 * Created by DEVEN SINGH on 1/22/2015.
 */
public class CustomProgressDialog extends Dialog{

    private ImageView progressImg;
    
    public CustomProgressDialog(Context context, int progressImageID) {
        super(context, R.style.CustomProgressDialog);
        WindowManager.LayoutParams windowManager = getWindow().getAttributes();
        windowManager.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(windowManager);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        progressImg = new ImageView(context);
        progressImg.setImageResource(progressImageID);
        layout.addView(progressImg, params);
        addContentView(layout, params);
    }

    @Override
    public void show() {
        super.show();
        RotateAnimation anim = new RotateAnimation(0.0f, 360.0f , Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(3000);
        progressImg.setAnimation(anim);
        progressImg.startAnimation(anim);
    }
}

package com.kevin.android.redorblack.utility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Keep;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.hujiaweibujidao.wava.Techniques;
import com.github.hujiaweibujidao.wava.YoYo;
import com.kevin.android.redorblack.R;
import com.simmorsal.recolor_project.ReColor;

@Keep
public class Animations {

    Context mContext;
    LinearLayout redOrBlackTextLayout;
    TextView redText;
    TextView blackText;


    public Animations(Context mContext, LinearLayout redOrBlackTextLayout, TextView redText, TextView blackText) {
        this.mContext = mContext;
        this.redOrBlackTextLayout = redOrBlackTextLayout;
        this.redText = redText;
        this.blackText = blackText;
    }

    public static int randomNumberRange() {
        double number;

        number = Math.random() * 15;

        return (int) number;
    }

    // automatically change colors of the red or black textviews randomly
    public void colorChanges() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (redOrBlackTextLayout.getVisibility() != View.VISIBLE) {
                    return;
                }
                ReColor reColor = new ReColor(mContext);
                if (Math.random() >= .5) {
                    if (redText.getCurrentTextColor() == mContext.getResources().getColor(R.color.red)) {
                        reColor.setTextViewColor(redText, Integer.toHexString(mContext.getResources().getColor(R.color.red)), Integer.toHexString(mContext.getResources().getColor(R.color.black)), 800);
                    } else {
                        reColor.setTextViewColor(redText, Integer.toHexString(mContext.getResources().getColor(R.color.black)), Integer.toHexString(mContext.getResources().getColor(R.color.red)), 800);
                    }
                } else {
                    if (blackText.getCurrentTextColor() == mContext.getResources().getColor(R.color.red)) {
                        reColor.setTextViewColor(blackText, Integer.toHexString(mContext.getResources().getColor(R.color.red)), Integer.toHexString(mContext.getResources().getColor(R.color.black)), 800);
                    } else {
                        reColor.setTextViewColor(blackText, Integer.toHexString(mContext.getResources().getColor(R.color.black)), Integer.toHexString(mContext.getResources().getColor(R.color.red)), 800);
                    }
                }
                handler.postDelayed(this, randomNumberRange() * 1000);
            }
        }, 1000);
    }

    // create the pulse animation on a view
    public void pulseAnimation(View view) {
        YoYo.with(Techniques.Pulse).duration(600)
                .interpolate(new AccelerateDecelerateInterpolator())
                .listen(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                    }
                })
                .playOn(view);
    }


}

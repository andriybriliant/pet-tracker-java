package com.apps.pettracker.utils;

import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

public class Animations {
    public static Animation LogsItemHoldAnimation(Float scale){
        Animation scaleAnimation = new ScaleAnimation(1F, scale, 1F, scale, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        scaleAnimation.setDuration(100);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setRepeatCount(1);
        return scaleAnimation;
    }

    public static Animation loadingIconLoadAnimation(){
        Animation rotationAnimation = new RotateAnimation(0F, 360F, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        rotationAnimation.setRepeatCount(Animation.INFINITE);
        rotationAnimation.setDuration(800);
        return rotationAnimation;
    }
}


package com.hurray.landlord.animation;

import com.hurray.landlord.utils.LogUtil;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class ImageTweenFrameAnim implements GeneralFrameAnim {

    private static final String TAG = "ImageTweenFrameAnim";

    private TweenAnim mTweenAnim;

    private FrameAnim mFrameAnim;

    private FrameAnimListener mFrameAnimListener;

    public ImageTweenFrameAnim(Context context, ImageView frameView, View tweenView, int tweenResId) {
        mTweenAnim = new TweenAnim(context, tweenView, tweenResId);
        mTweenAnim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                LogUtil.d(TAG, "onAnimationStart");
                mFrameAnim.onStart();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                LogUtil.d(TAG, "onAnimationRepeat");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                LogUtil.d(TAG, "onAnimationEnd");
                if (mTweenAnim.isInfinite()) {
                    mTweenAnim.onStart();
                } else {
                    mTweenAnim.onStop();
                    mFrameAnim.onStop();
                }
            }
        });

        mFrameAnim = new FrameAnim(frameView);
        mFrameAnim.setFrameAnimListener(new FrameAnimListener() {
            @Override
            public void onStop() {
                LogUtil.d(TAG, "FrameAnim onStop");
                if (mFrameAnimListener != null)
                    mFrameAnimListener.onStop();
            }

            @Override
            public void onStart() {
                LogUtil.d(TAG, "FrameAnim onStart");
                if (mFrameAnimListener != null)
                    mFrameAnimListener.onStart();
            }

            @Override
            public void onFrameAnimResUpdated(ImageView iv) {
                LogUtil.d(TAG, "FrameAnim onFrameAnimResUpdated");
                if (mFrameAnimListener != null)
                    mFrameAnimListener.onFrameAnimResUpdated(iv);
            }
        });

    }

    public void setFrameAnimListener(FrameAnimListener l) {
        mFrameAnimListener = l;
    }

    @Override
    public void onStart() {
        mTweenAnim.onStart();
    }

    @Override
    public void onStop() {
        mTweenAnim.onStop();
    }

    @Override
    public void updateFrameAnimRes(int resId) {
        mFrameAnim.updateFrameAnimRes(resId);
    }

    @Override
    public void setAutoClear(boolean shouldClear) {
        mFrameAnim.setAutoClear(shouldClear);
    }

    @Override
    public void onDestroy() {
        mTweenAnim.onDestroy();
        mFrameAnim.onDestroy();
    }

}


package com.hurray.landlord.animation;

import com.hurray.landlord.utils.LogUtil;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class FrameAnim implements GeneralFrameAnim {

    private static final String TAG = "FrameAnim";

    private int mLastResId;

    private boolean mAutoClear;

    private Handler mHandler = new Handler();

    private WeakReference<ImageView> mImageViewRef;

    private WeakReference<AnimationDrawable> mAnimDrawableRef;

    private FrameAnimListener mFrameAnimListener;

    public FrameAnim(ImageView imageView) {
        mImageViewRef = new WeakReference<ImageView>(imageView);
        mAutoClear = true;
    }

    public void setAutoClear(boolean autoClear) {
        mAutoClear = autoClear;
    }

    public void setFrameAnimListener(FrameAnimListener l) {
        mFrameAnimListener = l;
    }

    public void updateFrameAnimRes(int resId) {
        if (mLastResId != resId) {
            mLastResId = resId;
            mImageViewRef.get().setBackgroundResource(0);
            mImageViewRef.get().setBackgroundResource(resId);
            mAnimDrawableRef = new WeakReference<AnimationDrawable>(
                    (AnimationDrawable) mImageViewRef.get().getBackground());
            if (mAnimDrawableRef == null) {
                LogUtil.d(TAG, "----------mAnimDrawable NULL----------");
            }

            if (mImageViewRef != null) {
                mImageViewRef.get().setImageResource(0);
                if (mFrameAnimListener != null) {
                    mFrameAnimListener.onFrameAnimResUpdated(mImageViewRef.get());
                }
            }
        }
    }

    @Override
    public void onStart() {
        pause();

        mImageViewRef.get().setVisibility(View.VISIBLE);

        if (mAnimDrawableRef.get() != null) {
            if (mAnimDrawableRef.get().isOneShot()) {

                int frameNum = mAnimDrawableRef.get().getNumberOfFrames();
                int duration = 0;
                for (int i = 0; i < frameNum; i++) {
                    duration += mAnimDrawableRef.get().getDuration(i);
                }
                LogUtil.d(TAG, "duration=" + duration);
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        onStop();
                    }
                }, duration);
            }

            mAnimDrawableRef.get().start();
        }

        if (mFrameAnimListener != null)
            mFrameAnimListener.onStart();

        LogUtil.d(TAG, this.toString() + " start");
    }

    @Override
    public void onStop() {
        mImageViewRef.get().setVisibility(View.GONE);

        pause();

        if (mAutoClear) {
            clear();
        }

        if (mFrameAnimListener != null)
            mFrameAnimListener.onStop();

        LogUtil.d(TAG, this.toString() + " stop");
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        mFrameAnimListener = null;
    }

    private void pause() {
        if (mAnimDrawableRef != null &&
                mAnimDrawableRef.get() != null &&
                mAnimDrawableRef.get().isRunning())
            mAnimDrawableRef.get().stop();
    }

    private void clear() {
        mImageViewRef.get().setImageResource(0);
        mImageViewRef.get().setBackgroundResource(0);
        mLastResId = 0;
    }

}

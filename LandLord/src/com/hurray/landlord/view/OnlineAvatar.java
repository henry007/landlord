
package com.hurray.landlord.view;

import com.hurray.landlord.R;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.ResAvatarUtil;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class OnlineAvatar extends FrameLayout {

    private static final String TAG = "OnlineAvatar";

    private static final int START_EMOTION_DELAY = 100;

    private static final int SHOW_EMOTION_DELAY = 2000;

    private static final int LAYER_IDX_BASE = 0;

    private static final int LAYER_IDX_CLOTHES = 1;

    private static final int LAYER_IDX_HAND = 2;

    private static final int LAYER_NUM = 3;

    private WeakReference<ImageView> mAvatarImage;

    private WeakReference<ImageView> mFaceImage;

    private WeakReference<LayerDrawable> mAvatarLayer;

    private Drawable[] mDrawables;

    private boolean mIsMan;

    private int mClothesId;

    private int mHairId;

    private boolean mIsShowing;

    private String mPackagekName;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            
            if (!mIsShowing)
                return;

            showFaceFromUiThread(msg.what);
        }
    };

    private Runnable mRunShowDefaultEmotion = new Runnable() {
        @Override
        public void run() {
            if (!mIsShowing)
                return;

            showFaceFromUiThread(0);
        }
    };

    // public OnlineAvatar(Context context) {
    // super(context);
    // initViews(context);
    // initData();
    // }

    public OnlineAvatar(Context context, AttributeSet attrs) {
        super(context, attrs);

        mIsShowing = false;

        initViews(context);
        initData();
    }

    private void initViews(Context context) {

        mPackagekName = context.getPackageName();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.online_avatar, this);
        mAvatarImage = new WeakReference<ImageView>((ImageView) view.findViewById(R.id.avatar));
        mFaceImage = new WeakReference<ImageView>((ImageView) view.findViewById(R.id.face));
        mDrawables = new Drawable[LAYER_NUM];

        // mRandom = new Random();
        // setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // showEmotion(mRandom.nextInt(6) + 1);
        // setHair(mRandom.nextInt(4));
        // setClothes(mRandom.nextInt(4));
        // }
        // });
    }

    private void initData() {
        mIsMan = true;
        mClothesId = 0;
        mHairId = 0;
    }

    public void onStart() {
        mIsShowing = true;
        LogUtil.d("Demo", "onStart:mIsShowing--"+mIsShowing);
        updateAll();
    }

    public void onStop() {
        mIsShowing = false;
        LogUtil.d("Demo", "onStop:mIsShowing--"+mIsShowing);
        releaseAll();
    }

    public void onDestroy() {
        mDrawables[LAYER_IDX_BASE] = null;
        mDrawables[LAYER_IDX_CLOTHES] = null;
        mDrawables[LAYER_IDX_HAND] = null;
        mDrawables = null;
    }

    public void setSex(boolean isMan) {
        if (mIsMan != isMan) {
            mIsMan = isMan;
            if (mIsShowing)
                updateAll();
        }
    }

    public void setClothes(int clothesId) {
        if (mClothesId != clothesId) {
//            if (ResAvatarUtil.getResAvatarClothesFile(mIsMan, clothesId).exists()) {
                mClothesId = clothesId;
//            } else {
//                mClothesId = 0;
//            }

            if (mIsShowing) {
                
                if(mClothesId>=0){
                    mDrawables[LAYER_IDX_CLOTHES] = (ResAvatarUtil.getResAvatarClothesDrawable(
                            getResources(),
                            mIsMan, mClothesId, mPackagekName));
                
                

                mAvatarLayer = new WeakReference<LayerDrawable>(new LayerDrawable(mDrawables));

                mAvatarImage.get().setBackgroundDrawable(mAvatarLayer.get());
                }
            }
        }
    }

    public void setHair(int hairId) {
        if (mHairId != hairId) {
//            if (ResAvatarUtil.getResAvatarHairFile(mIsMan, hairId).exists()) {
                mHairId = hairId;
//            } else {
//                mHairId = 0;
//            }

            if (mIsShowing)
                mFaceImage.get().setImageDrawable(
                        ResAvatarUtil.getResAvatarHairDrawable(getResources(),
                                mIsMan, mHairId, mPackagekName));
        }
    }

    public void showEmotion(int emotionId) {
        LogUtil.d(TAG, "setEmotion= " + emotionId);
        if (mIsShowing) {
            if (ResAvatarUtil.isEmotionExist(mIsMan, emotionId, getResources(), mPackagekName))
                mHandler.sendEmptyMessage(emotionId);
        }
    }

    private void updateAll() {
        mDrawables[LAYER_IDX_BASE] = (ResAvatarUtil
                .getResAvatarBaseDrawable(getResources(), mIsMan, mPackagekName));
        mDrawables[LAYER_IDX_CLOTHES] = (ResAvatarUtil.getResAvatarClothesDrawable(getResources(),
                mIsMan, mClothesId, mPackagekName));
        mDrawables[LAYER_IDX_HAND] = ResAvatarUtil.getResAvatarHandDrawable(getResources(), mIsMan,
                mPackagekName);
        mAvatarLayer = new WeakReference<LayerDrawable>(new LayerDrawable(mDrawables));
        mAvatarImage.get().setBackgroundDrawable(mAvatarLayer.get());
        mFaceImage.get().setImageDrawable(ResAvatarUtil.getResAvatarHairDrawable(getResources(),
                mIsMan, mHairId, mPackagekName));
        mFaceImage.get().setBackgroundDrawable(ResAvatarUtil.getResAvatarEmotionAnimDrawable(
                getResources(), mIsMan, 0, mPackagekName));
        showFaceFromUiThread(0);
    }

    private void releaseAll() {
        hideFaceFromUiThread();

        mFaceImage.get().setImageDrawable(null);
        mFaceImage.get().setBackgroundDrawable(null);
        mAvatarImage.get().setBackgroundDrawable(null);
        mAvatarLayer = null;

        mDrawables[LAYER_IDX_BASE] = null;
        mDrawables[LAYER_IDX_CLOTHES] = null;
        mDrawables[LAYER_IDX_HAND] = null;
    }

    private void showFaceFromUiThread(int emotionId) {
        mFaceImage.get().setBackgroundDrawable(ResAvatarUtil.getResAvatarEmotionAnimDrawable(
                getResources(), mIsMan,
                emotionId, mPackagekName));

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimationDrawable ad = (AnimationDrawable) mFaceImage.get().getBackground();
                if (ad != null)
                    ad.start();
            }
        }, START_EMOTION_DELAY);

        if (emotionId != 0) {
            mHandler.removeCallbacks(mRunShowDefaultEmotion);
            mHandler.postDelayed(mRunShowDefaultEmotion, SHOW_EMOTION_DELAY);
        }
    }

    private void hideFaceFromUiThread() {
        AnimationDrawable ad = (AnimationDrawable) mFaceImage.get().getBackground();
        if (ad != null && ad.isRunning())
            ad.stop();
    }

    // public interface OnAvatarChanageListener {
    // void onAvatarChanged(int hairId, int clothesId);
    // }
    //
    // public void setOnAvatarChanageListener(OnAvatarChanageListener l) {
    // mOnAvatarChanageListener = l;
    // }

    // private void invokeOnAvatarChanageListener(int hairId, int clothesId) {
    // if (mOnAvatarChanageListener != null) {
    // mOnAvatarChanageListener.onAvatarChanged(hairId, clothesId);
    // }
    // }

}

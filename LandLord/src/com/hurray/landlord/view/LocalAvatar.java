
package com.hurray.landlord.view;

import com.hurray.landlord.R;
import com.hurray.landlord.animation.FrameAnim;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class LocalAvatar extends FrameLayout implements EmotionAction {

    private static final int SHOW_FACE_DELAY = 2000;
    private static final int ONSTART_DELAY = 100;

    private static final int RES_IDX_NO_FACE = 0;
    private static final int RES_IDX_DEFAULT = 1;
    private static final int RES_IDX_ANGER = 2;
    private static final int RES_IDX_CRY = 3;
    private static final int RES_IDX_CUTE = 4;
    private static final int RES_IDX_LAUGH = 5;
    private static final int RES_IDX_LUCK = 6;
    private static final int RES_IDX_PROUD = 7;

    private static final int[] B1_RES = {
            R.drawable.b_noface1,
            R.anim.b1_emotion0_frame,
            R.anim.b1_anger_frame,
            R.anim.b1_cry_frame,
            R.anim.b1_cute_frame,
            R.anim.b1_laugh_frame,
            R.anim.b1_luck_frame,
            R.anim.b1_proud_frame
    };
    private static final int[] B2_RES = {
            R.drawable.b_noface2,
            R.anim.b2_emotion0_frame,
            R.anim.b2_anger_frame,
            R.anim.b2_cry_frame,
            R.anim.b2_cute_frame,
            R.anim.b2_laugh_frame,
            R.anim.b2_luck_frame,
            R.anim.b2_proud_frame
    };
    private static final int[] B3_RES = {
            R.drawable.b_noface3,
            R.anim.b3_emotion0_frame,
            R.anim.b3_anger_frame,
            R.anim.b3_cry_frame,
            R.anim.b3_cute_frame,
            R.anim.b3_laugh_frame,
            R.anim.b3_luck_frame,
            R.anim.b3_proud_frame
    };
    private static final int[] B4_RES = {
            R.drawable.b_noface4,
            R.anim.b4_emotion0_frame,
            R.anim.b4_anger_frame,
            R.anim.b4_cry_frame,
            R.anim.b4_cute_frame,
            R.anim.b4_laugh_frame,
            R.anim.b4_luck_frame,
            R.anim.b4_proud_frame
    };

    private ImageView mBase;

    private ImageView mFace;

    private FrameAnim mFaceAnim;

    private int[] mResArr;

    private boolean mIsShowing;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mIsShowing)
                return;

            showFace(mResArr[msg.what], false);
        }
    };

    private Runnable mRunShowDefaultFace = new Runnable() {
        @Override
        public void run() {
            if (!mIsShowing)
                return;
            
            showFace(mResArr[RES_IDX_DEFAULT], true);
        }
    };

    // public LocalAvatar(Context context) {
    // super(context);
    // initViews(context);
    // }

    public LocalAvatar(Context context, AttributeSet attrs) {
        super(context, attrs);

        mIsShowing = false;

        initViews(context);
    }

    public void onStart() {
        mBase.setVisibility(View.INVISIBLE);
        mBase.setImageResource(mResArr[RES_IDX_NO_FACE]);
        mHandler.postDelayed(mRunShowDefaultFace, ONSTART_DELAY);
        
        mIsShowing = true;
    }

    public void onStop() {
        mIsShowing = false;
        
        mFaceAnim.onStop();
        mBase.setImageResource(0);
        mFace.setBackgroundResource(0);
    }

    public void onDestroy() {
        mBase.setImageResource(0);
        mBase.setBackgroundResource(0);
        mFace.setImageResource(0);
        mFace.setBackgroundResource(0);
        mFaceAnim.onDestroy();

        mBase = null;
        mFace = null;
        mFaceAnim = null;
    }

    private void initViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.local_avatar, this);
        mBase = (ImageView) view.findViewById(R.id.base);
        mFace = (ImageView) view.findViewById(R.id.face);
        mFaceAnim = new FrameAnim(mFace);
    }

    public void setBeautyId(int beautyId) {
        setResArr(getBeautyResArr(beautyId));
    }

    private void setResArr(int[] resArr) {
        mResArr = resArr;
    }

    private int[] getBeautyResArr(int beautyId) {
        switch (beautyId) {
            case 0:
                return B1_RES;
            case 1:
                return B2_RES;
            case 2:
                return B3_RES;
            case 3:
                return B4_RES;
            default:
                return null;
        }
    }

    private void showFace(int resId, boolean isDefault) {
        mBase.setVisibility(View.VISIBLE);
        mFace.setVisibility(View.VISIBLE);

        mFaceAnim.updateFrameAnimRes(resId);
        mFaceAnim.onStart();

        if (!isDefault) {
            mHandler.removeCallbacks(mRunShowDefaultFace);
            mHandler.postDelayed(mRunShowDefaultFace, SHOW_FACE_DELAY);
        }
    }

    private void showFace(int resIdx) {
        mHandler.sendEmptyMessage(resIdx);
    }

    @Override
    public void onDefault() {
        showFace(RES_IDX_DEFAULT);
    }

    @Override
    public void onAnger() {
        showFace(RES_IDX_ANGER);
    }

    @Override
    public void onCry() {
        showFace(RES_IDX_CRY);
    }

    @Override
    public void onCute() {
        showFace(RES_IDX_CUTE);
    }

    @Override
    public void onLaugh() {
        showFace(RES_IDX_LAUGH);
    }

    @Override
    public void onLuck() {
        showFace(RES_IDX_LUCK);
    }

    @Override
    public void onProud() {
        showFace(RES_IDX_PROUD);
    }

}

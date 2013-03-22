
package com.hurray.landlord.animation;

import com.hurray.landlord.utils.LogUtil;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

public class TweenAnim implements GeneralAnim {

    private static final String TAG = "TweenAnim";

    private Animation mTweenAnim;

    private View mView;

    public TweenAnim(Context context, View view, int tweenResId) {
        mView = view;
        mTweenAnim = AnimationUtils.loadAnimation(context, tweenResId);
    }

    public void setAnimationListener(AnimationListener l) {
        mTweenAnim.setAnimationListener(l);
    }

    public boolean isInfinite() {
        return (mTweenAnim.getRepeatCount() == Animation.INFINITE);
    }

    public void setInfinite() {
        mTweenAnim.setRepeatCount(Animation.INFINITE);
    }

    @Override
    public void onStart() {
        LogUtil.d(TAG, this.toString() + " start");
        if (mView != null) {
            mView.setVisibility(View.VISIBLE);
            if (mTweenAnim != null)
                mView.startAnimation(mTweenAnim);
        }
    }

    @Override
    public void onStop() {
        LogUtil.d(TAG, this.toString() + "stop");
        if (mView != null) {
            mView.setVisibility(View.GONE);
            mView.clearAnimation();
        }
    }

    @Override
    public void onDestroy() {
        mView = null;
        mTweenAnim = null;
    }

}

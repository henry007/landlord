
package com.hurray.landlord.activities;

import com.hurray.landlord.R;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class AfterFailActivity extends Activity {

    ImageView mGirl, mFail, mLine;

    AnimationDrawable mGirlAnim, mLineAnim;

    Animation mFailure;

    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.after_fail);

        initView();
        startAnim();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAnimAndRelease();
    }

    public void startAnim() {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                mGirlAnim.start();
                mLineAnim.start();
                mFail.startAnimation(mFailure);

            }
        }, 500);
    }

    public void initView() {
        mGirl = (ImageView) findViewById(R.id.fail_girl);
        mGirlAnim = (AnimationDrawable) mGirl.getBackground();

        mLine = (ImageView) findViewById(R.id.fail_line);
        mLineAnim = (AnimationDrawable) mLine.getBackground();
        mLine.setVisibility(View.INVISIBLE);

        mFail = (ImageView) findViewById(R.id.failure);
        mFail.setVisibility(View.INVISIBLE);

        mFailure = AnimationUtils.loadAnimation(this, R.anim.fail_anim);

        setAnimListener();
    }
    
    private void stopAnimAndRelease() {
        if (mGirlAnim.isRunning())
            mGirlAnim.stop();
        if (mLineAnim.isRunning())
            mLineAnim.stop();
        if(mFailure.hasStarted()){
            mFailure.cancel();
            mFailure.setAnimationListener(null);
        }
        
        mFail.clearAnimation();

        mGirl.setImageResource(0);
        mGirl.setBackgroundResource(0);
        mLine.setImageResource(0);
        mLine.setBackgroundResource(0);
        mFail.setImageResource(0);
        mFail.setBackgroundResource(0);
        
        mGirl = null; 
        mFail = null; 
        mLine = null;
        mGirlAnim = null;
        mLineAnim = null;
        mFailure = null;
    }

    public void finishActivity() {
        setResult(RESULT_OK);
        finish();
    }

    public void setAnimListener() {

        mFailure.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                mFail.setVisibility(View.VISIBLE);
                mLine.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finishActivity();
            }
        });

    }

}

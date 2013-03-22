
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

public class AfterWinActivity extends Activity {

    ImageView mFlower;

    ImageView mGirl;
    
    ImageView mWinner;

    AnimationDrawable mFlowerAnim, mGrilAnim, mWinFrame;

    Animation mWinTween;
    
    Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.after_win);

        initView();
        startAnimation();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAnimAndRelease();
    }

    public void initView() {

        mFlower = (ImageView) findViewById(R.id.firework);
        mFlower.setVisibility(View.INVISIBLE);
        mFlowerAnim = (AnimationDrawable) mFlower.getBackground();

        mGirl = (ImageView) findViewById(R.id.girl);
        mGrilAnim = (AnimationDrawable) mGirl.getBackground();

        mWinner = (ImageView) findViewById(R.id.winner);
        mWinner.setVisibility(View.INVISIBLE);
        mWinFrame = (AnimationDrawable) mWinner.getBackground();
        
        initWinTweenAnim();
    }

    private void initWinTweenAnim() {
        mWinTween = AnimationUtils.loadAnimation(this, R.anim.win_tween);
        mWinTween.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mFlower.setVisibility(View.VISIBLE);
                mGirl.setVisibility(View.VISIBLE);
                mWinner.setVisibility(View.VISIBLE);
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
    
    private void stopAnimAndRelease() {
        
        if (mFlowerAnim.isRunning())
            mFlowerAnim.stop();
        if (mGrilAnim.isRunning())
            mGrilAnim.stop();
        if (mWinFrame.isRunning())
            mWinFrame.stop();
        if(mWinTween.hasStarted()){
            
            mWinTween.cancel();
            
        }
        mWinner.clearAnimation();
        
        mFlower.setImageResource(0);
        mFlower.setBackgroundResource(0);
        mGirl.setImageResource(0);
        mGirl.setBackgroundResource(0);
        mWinner.setImageResource(0);
        mWinner.setBackgroundResource(0);
    }

    protected void finishActivity() {        
        setResult(RESULT_OK);
        AfterWinActivity.this.finish();
    }

    public void startAnimation() {

        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                mFlowerAnim.start();
                mGrilAnim.start();
                mWinner.startAnimation(mWinTween);

            }
        }, 100);

    }

}

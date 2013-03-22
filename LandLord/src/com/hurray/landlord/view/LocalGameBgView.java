
package com.hurray.landlord.view;

import com.hurray.landlord.R;
import com.hurray.landlord.animation.BoatAnimation;
import com.hurray.landlord.game.local.Effects;
import com.hurray.landlord.utils.LogUtil;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class LocalGameBgView extends FrameLayout {

    private static final String TAG = "LocalGameBgView";

    private int mGameBgId;

    private LayoutInflater mInflater;

    private Handler mHandler = new Handler();

    private LocalGameBgHandler mLocalGameBgHandler;

    public LocalGameBgView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mGameBgId = Effects.getGameBg();

        mLocalGameBgHandler = createLocalGameBgHandler(mGameBgId);
        mLocalGameBgHandler.initViews(context);
    }

    private LocalGameBgHandler createLocalGameBgHandler(int gameBgId) {
        switch (gameBgId) {
            case 1:
                return new LocalGameBg1();
            case 2:
                return new LocalGameBg2();
            default: // 0
                return new LocalGameBg0();
        }
    }

    public void onStart() {
        mLocalGameBgHandler.onStart();
    }

    public void onStop() {
        mLocalGameBgHandler.onStop();
    }

    public void onDestroy() {
        mLocalGameBgHandler.onDestroy();
        mLocalGameBgHandler = null;
    }

    public void onLoadSensitiveRes() {
        mLocalGameBgHandler.onLoadSensitiveRes();
    }

    public void onReleaseSensitiveRes() {
        mLocalGameBgHandler.onReleaseSensitiveRes();
    }

    abstract private static class LocalGameBgHandler {

        abstract protected void initViews(Context context);

        abstract public void onStart();

        abstract public void onStop();

        abstract public void onDestroy();

        abstract public void onLoadSensitiveRes();

        abstract public void onReleaseSensitiveRes();
    }

    private class LocalGameBg0 extends LocalGameBgHandler {

        private ImageView mBg;

        @Override
        protected void initViews(Context context) {
            View view = mInflater.inflate(R.layout.local_game_bg0, LocalGameBgView.this);
            mBg = (ImageView) view.findViewById(R.id.game_bg0);
        }

        @Override
        public void onStart() {
            mBg.setBackgroundResource(R.drawable.local_game_bg0);

        }

        @Override
        public void onStop() {
            mBg.setBackgroundResource(0);
        }

        @Override
        public void onDestroy() {
            mBg.setImageResource(0);
            mBg.setBackgroundResource(0);
            mBg = null;
        }

        @Override
        public void onLoadSensitiveRes() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onReleaseSensitiveRes() {
            // TODO Auto-generated method stub

        }

    }

    private class LocalGameBg1 extends LocalGameBgHandler {

        private ImageView mBg;

        private ImageView mButterFly0;
        private ImageView mButterFly1;

        private AnimationDrawable mButterFly0Anim;
        private AnimationDrawable mButterFly1Anim;

        @Override
        protected void initViews(Context context) {
            View view = mInflater.inflate(R.layout.local_game_bg1, LocalGameBgView.this);
            mBg = (ImageView) view.findViewById(R.id.game_bg1);
            mButterFly0 = (ImageView) view.findViewById(R.id.butterfly0);
            mButterFly1 = (ImageView) view.findViewById(R.id.butterfly1);
        }

        @Override
        public void onStart() {
            mBg.setBackgroundResource(R.drawable.local_game_bg1);
        }

        @Override
        public void onStop() {
            mBg.setBackgroundResource(0);
        }

        @Override
        public void onDestroy() {
            mButterFly0Anim.stop();
            mButterFly1Anim.stop();

            mBg.setImageResource(0);
            mBg.setBackgroundResource(0);
            mButterFly0.setImageResource(0);
            mButterFly0.setBackgroundResource(0);
            mButterFly1.setImageResource(0);
            mButterFly1.setBackgroundResource(0);

            mBg = null;
            mButterFly0 = null;
            mButterFly1 = null;
            mButterFly0Anim = null;
            mButterFly1Anim = null;
        }

        @Override
        public void onLoadSensitiveRes() {
            mButterFly0.setBackgroundResource(R.anim.butterfly0_frame);
            mButterFly1.setBackgroundResource(R.anim.butterfly1_frame);
            mButterFly0Anim = (AnimationDrawable) mButterFly0.getBackground();
            mButterFly1Anim = (AnimationDrawable) mButterFly1.getBackground();

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mButterFly0Anim.start();
                    mButterFly1Anim.start();
                }
            }, 100);

        }

        @Override
        public void onReleaseSensitiveRes() {
            mButterFly0Anim.stop();
            mButterFly1Anim.stop();
            mButterFly0.setBackgroundResource(0);
            mButterFly1.setBackgroundResource(0);
        }

    }

    private class LocalGameBg2 extends LocalGameBgHandler {

        private ImageView mBg;

        private View mBoatAnim;
        private ImageView mBoatFrame;
        private ImageView mBoatStill;
        private BoatAnimation mBoatAnimation;

        private ImageView mLight0;
        private ImageView mLight1;
        private ImageView mLight2;

        private ImageView mWater0;
        private ImageView mWater1;

        private Animation mWater0Anim;

        private Animation mWater1Anim;

        @Override
        protected void initViews(Context context) {
            View view = mInflater.inflate(R.layout.local_game_bg2, LocalGameBgView.this);
            mBg = (ImageView) view.findViewById(R.id.game_bg2);
            mBoatAnim = view.findViewById(R.id.boat_anim);
            mBoatFrame = (ImageView) view.findViewById(R.id.boat_frame);
            mBoatStill = (ImageView) view.findViewById(R.id.boat_still);
            mBoatAnimation = new BoatAnimation(context, mBoatAnim, mBoatFrame, mBoatStill);

            mLight0 = (ImageView) view.findViewById(R.id.light0);
            mLight1 = (ImageView) view.findViewById(R.id.light1);
            mLight2 = (ImageView) view.findViewById(R.id.light2);

            mWater0 = (ImageView) view.findViewById(R.id.water0);
            mWater1 = (ImageView) view.findViewById(R.id.water1);
        }

        @Override
        public void onStart() {
            mBg.setBackgroundResource(R.drawable.local_game_bg2);
        }

        @Override
        public void onStop() {
            mBg.setBackgroundResource(0);
        }

        @Override
        public void onDestroy() {
            mBg.setImageResource(0);
            mBg.setBackgroundResource(0);

            mBoatAnim.clearAnimation();
            mBoatAnim.setBackgroundResource(0);

            mBoatFrame.clearAnimation();
            mBoatFrame.setImageResource(0);
            mBoatFrame.setBackgroundResource(0);

            mBoatStill.clearAnimation();
            mBoatStill.setImageResource(0);
            mBoatStill.setBackgroundResource(0);

            mBoatAnimation.onStop();
            mBoatAnimation.onDestroy();

            mLight0.clearAnimation();
            mLight0.setImageResource(0);
            mLight0.setBackgroundResource(0);
            mLight1.clearAnimation();
            mLight1.setImageResource(0);
            mLight1.setBackgroundResource(0);
            mLight2.clearAnimation();
            mLight2.setImageResource(0);
            mLight2.setBackgroundResource(0);

            mWater0.clearAnimation();
            mWater0.setImageResource(0);
            mWater0.setBackgroundResource(0);
            mWater1.clearAnimation();
            mWater1.setImageResource(0);
            mWater1.setBackgroundResource(0);

            mWater0Anim.cancel();
            mWater1Anim.cancel();

            mBg = null;
            mBoatAnim = null;
            mBoatFrame = null;
            mBoatStill = null;

            mBoatAnimation = null;

            mLight0 = null;
            mLight1 = null;
            mLight2 = null;
            mWater0 = null;
            mWater1 = null;

            mWater0Anim = null;
            mWater1Anim = null;
        }

        @Override
        public void onLoadSensitiveRes() {
            mBoatAnimation.updateFrameAnimRes(R.anim.boat_frame);
            mBoatAnimation.onStart();
            startLightAnim();
            startWaterAnim();
        }

        @Override
        public void onReleaseSensitiveRes() {
            mBoatAnimation.onStop();
            clearLightAnim();
            clearWaterAnim();
        }

        private void startLightAnim() {
            mLight0.setImageResource(R.drawable.local_game_bg2_light0);
            mLight1.setImageResource(R.drawable.local_game_bg2_light1);
            mLight2.setImageResource(R.drawable.local_game_bg2_light2);

            RotateAnimation anim0 = new RotateAnimation(-135, 90, Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 1.0f);
            anim0.setDuration(5000);
            anim0.setRepeatCount(Animation.INFINITE);
            anim0.setRepeatMode(Animation.REVERSE);
            mLight0.setAnimation(anim0);
            anim0.start();

            RotateAnimation anim1 = new RotateAnimation(135, -90, Animation.RELATIVE_TO_SELF, 1.0f,
                    Animation.RELATIVE_TO_SELF, 1.0f);
            anim1.setDuration(6000);
            anim1.setRepeatCount(Animation.INFINITE);
            anim1.setRepeatMode(Animation.REVERSE);
            mLight1.setAnimation(anim1);
            anim1.start();

            RotateAnimation anim2 = new RotateAnimation(-180, 90, Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 1.0f);
            anim2.setDuration(4500);
            anim2.setRepeatCount(Animation.INFINITE);
            anim2.setRepeatMode(Animation.REVERSE);
            mLight2.setAnimation(anim2);
            anim2.start();
        }

        private void clearLightAnim() {
            mLight0.clearAnimation();
            mLight1.clearAnimation();
            mLight2.clearAnimation();

            mLight0.setImageResource(0);
            mLight1.setImageResource(0);
            mLight2.setImageResource(0);
        }

        private void startWaterAnim() {
            mWater0.setImageResource(R.drawable.water0);
            mWater1.setImageResource(R.drawable.water1);

            mWater0Anim = AnimationUtils.loadAnimation(getContext(), R.anim.water0_tween);
            mWater0.setAnimation(mWater0Anim);
            mWater0Anim.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    LogUtil.d(TAG, "mWater0Anim onAnimationEnd");
                    mWater0Anim = AnimationUtils.loadAnimation(getContext(), R.anim.water0_tween);
                    mWater0.setAnimation(mWater0Anim);
                    mWater0Anim.setAnimationListener(this);
                    mWater0Anim.start();
                }

                @Override
                public void onAnimationStart(Animation animation) {
                    LogUtil.d(TAG, "mWater0Anim onAnimationStart");
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    LogUtil.d(TAG, "mWater0Anim onAnimationRepeat");
                }
            });
            mWater0Anim.start();

            mWater1Anim = AnimationUtils.loadAnimation(getContext(), R.anim.water1_tween);
            mWater1.setAnimation(mWater1Anim);
            mWater1Anim.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    LogUtil.d(TAG, "mWater1Anim onAnimationEnd");
                    mWater1Anim = AnimationUtils.loadAnimation(getContext(), R.anim.water1_tween);
                    mWater1.setAnimation(mWater1Anim);
                    mWater1Anim.setAnimationListener(this);
                    mWater1Anim.start();
                }

                @Override
                public void onAnimationStart(Animation animation) {
                    LogUtil.d(TAG, "mWater1Anim onAnimationStart");
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    LogUtil.d(TAG, "mWater1Anim onAnimationRepeat");
                }

            });
            mWater1Anim.start();
        }

        private void clearWaterAnim() {
            mWater0.clearAnimation();
            mWater1.clearAnimation();

            mWater0.setImageResource(0);
            mWater1.setImageResource(0);
        }

    }
}

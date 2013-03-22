
package com.hurray.landlord.activities;

import com.hurray.landlord.R;
import com.hurray.landlord.net.socket.srv.ServerMatchResult;
import com.hurray.landlord.utils.LogUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CongratulationActivity extends Activity {

    public static final String SERVER_MATCH_RESULT = "srv_match_result";

    // private TextView mTvYellowAnimGold, mTvPinkAnimExp, mTvGreenAnimPoint;

    private TextView mTvYellowStillGold, mTvPinkStillExp;

    private Animation mAnimYellow, mAnimPink, mAnimGreen;

    private TextAnimListener mTextAnimListener1, mTextAnimListener2, mTextAnimListener3;

    // private AnimationDrawable mYellowMissailAnim, mPinkMissailAnim,
    // mGreenMissailAnim;
    private AnimationDrawable mStarsAnim;
    // private ImageView mIvYellowMissail, mIvGreenMissail, mIvPinkMissail;
    private ImageView mIvStars;

    private Button mBtnBack;

    private Button mBtnRank;

    private TextView mTVRankContent;

    private ImageView mIvCongratulation = null;

    private static ServerMatchResult mMatchResult;

    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.congratulation);
        try {
        	initView();
			fillData();
		} catch (Exception e) {
			e.printStackTrace();
		}

        // setShader();
        // startTextViewAnim();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        startImageViewAnim();
    }

    private void startImageViewAnim() {
        // mHandler.post(new Runnable() {
        //
        // @Override
        // public void run() {
        // mYellowMissailAnim.start();
        // }
        // });
        //
        // mHandler.postDelayed(new Runnable() {
        //
        // @Override
        // public void run() {
        // mGreenMissailAnim.start();
        // }
        // }, 250);
        //
        // mHandler.postDelayed(new Runnable() {
        //
        // @Override
        // public void run() {
        // mPinkMissailAnim.start();
        // }
        // }, 500);

        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mStarsAnim != null)
                    mStarsAnim.start();
            }
        }, 1000);
    }

    private int mGold;
    private int mExp;
    private int mPoint;
    private int mRank;
    private int mGameType;
    private int inningNum;

    private void fillData() {
        Intent i = getIntent();
        if (mMatchResult == null) {
			mMatchResult = (ServerMatchResult) i
					.getSerializableExtra(SERVER_MATCH_RESULT);
		}
		i.removeExtra(SERVER_MATCH_RESULT);
        
        inningNum = i.getIntExtra("inningNum", -1);
        mGold = mMatchResult.getGold();
        mExp = mMatchResult.getCurrExp();
        mPoint = mMatchResult.getPoint();
        mRank = mMatchResult.getRank();
        mGameType = i.getIntExtra(OnlineGameActivity.GAME_TYPE, -1);
        
        LogUtil.d("Demo", "congratulationGameType:"+mGameType);
        
        LogUtil.d("Demo", "congratulationIningNum:"+inningNum);
        
        int isRised = mMatchResult.isRised();

        String txtGold;
        String txtExp;
        String txtPoint;
        String txtRank;

        mTVRankContent.setText("你获得了比赛第" + mRank + "名");
        mIvCongratulation.setVisibility(mRank == 1 ? View.VISIBLE : View.INVISIBLE);

        if (mGameType == 1) {
            mBtnRank.setVisibility(View.VISIBLE);
            String content = "你获得了比赛第" + mRank + "名";
            if(inningNum !=2){
                mTVRankContent.setText(isRised == 0||isRised == 2 ? "恭喜你晋级了\n"+content : "很遗憾你被淘汰了\n" + content);
            }
        }

        if (mGold > 0)
            txtGold = "+" + mGold;
        else if (mGold < 0)
            txtGold = "-" + mGold;
        else
            txtGold = "0";

        if (mExp > 0)
            txtExp = "+" + mExp;
        else if (mExp < 0)
            txtExp = "-" + mExp;
        else
            txtExp = "0";

        if (mPoint > 0)
            txtPoint = "+" + mPoint;
        else if (mPoint < 0)
            txtPoint = "-" + mPoint;
        else
            txtPoint = "0";

        // if (mGold > 0)
        // mTvYellowAnimGold.setText(txtGold);
        // else
        // mTvYellowAnimGold.setVisibility(View.GONE);
        //
        // if (mExp > 0)
        // mTvPinkAnimExp.setText(txtExp);
        // else
        // mTvPinkAnimExp.setVisibility(View.GONE);
        //
        // if (mPoint > 0)
        // mTvGreenAnimPoint.setText(txtPoint);
        // else
        // mTvGreenAnimPoint.setVisibility(View.GONE);

        mTvYellowStillGold.setText(txtGold);
        mTvPinkStillExp.setText(txtExp);
//        mTvGreenStillPoint.setText(txtPoint);
    }

    // public void setShader() {
    //
    // Shader yellowShader = new LinearGradient(0, 0, 0,
    // mTvYellowAnimGold.getHeight(),
    // new int[] {
    // getResources().getColor(R.color.yellow1),
    // getResources().getColor(R.color.yellow2),
    // getResources().getColor(R.color.yellow2)
    // },
    // null, TileMode.CLAMP);
    // mTvYellowAnimGold.getPaint().setShader(yellowShader);
    //
    // Shader pinkShader = new LinearGradient(0, 0, 0,
    // mTvPinkAnimExp.getHeight(),
    // new int[] {
    // getResources().getColor(R.color.pink1),
    // getResources().getColor(R.color.pink2),
    // getResources().getColor(R.color.pink2)
    // },
    // null, TileMode.CLAMP);
    // mTvYellowAnimGold.getPaint().setShader(pinkShader);
    //
    // Shader greenShader = new LinearGradient(0, 0, 0,
    // mTvGreenAnimPoint.getHeight(),
    // new int[] {
    // getResources().getColor(R.color.green1),
    // getResources().getColor(R.color.green2),
    // getResources().getColor(R.color.green2)
    // },
    // null, TileMode.CLAMP);
    // mTvYellowAnimGold.getPaint().setShader(yellowShader);
    // mTvPinkAnimExp.getPaint().setShader(pinkShader);
    // mTvGreenAnimPoint.getPaint().setShader(greenShader);
    //
    // mTvYellowAnimGold.getPaint().setFakeBoldText(true);
    // mTvPinkAnimExp.getPaint().setFakeBoldText(true);
    // mTvGreenAnimPoint.getPaint().setFakeBoldText(true);
    // }

    private void initView() {

        // mIvYellowMissail = (ImageView) findViewById(R.id.missail_yellow);
        // mIvGreenMissail = (ImageView) findViewById(R.id.missail_green);
        // mIvPinkMissail = (ImageView) findViewById(R.id.missail_pink);
        mIvStars = (ImageView) findViewById(R.id.iv_stars);

        // mIvYellowMissail.setBackgroundResource(R.anim.missail_yellow);
        // mIvGreenMissail.setBackgroundResource(R.anim.missail_green);
        // mIvPinkMissail.setBackgroundResource(R.anim.missail_pink);
        mIvStars.setBackgroundResource(R.anim.anim_stars);

        // mYellowMissailAnim = (AnimationDrawable)
        // mIvYellowMissail.getBackground();
        // mGreenMissailAnim = (AnimationDrawable)
        // mIvGreenMissail.getBackground();
        // mPinkMissailAnim = (AnimationDrawable)
        // mIvPinkMissail.getBackground();
        mStarsAnim = (AnimationDrawable) mIvStars.getBackground();

        // mTvYellowAnimGold = (TextView) findViewById(R.id.tv_yellow_gold);
        // mTvPinkAnimExp = (TextView) findViewById(R.id.tv_pink_exp);
        // mTvGreenAnimPoint = (TextView) findViewById(R.id.tv_green_point);

        mTvYellowStillGold = (TextView) findViewById(R.id.gold_val);
        mTvPinkStillExp = (TextView) findViewById(R.id.exp_val);
//        mTvGreenStillPoint = (TextView) findViewById(R.id.point_val);

        mBtnRank = (Button) findViewById(R.id.btn_rank);

        mBtnRank.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(CongratulationActivity.this,
                        OnlineGameResultRankActivity.class);
                i.putExtra(OnlineGameResultRankActivity.GAME_RESULT_RANK, mMatchResult);
                startActivity(i);
            }
        });

        mBtnBack = (Button) findViewById(R.id.btn_back);

        mBtnBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        mIvCongratulation = (ImageView) findViewById(R.id.img_congratulation);

        mTVRankContent = (TextView) findViewById(R.id.tv_rank_content);

        // mTextAnimListener1 = new TextAnimListener(mTvYellowAnimGold);
        // mTextAnimListener2 = new TextAnimListener(mTvPinkAnimExp);
        // mTextAnimListener3 = new TextAnimListener(mTvGreenAnimPoint);

        mAnimYellow = AnimationUtils.loadAnimation(this, R.anim.anim_num);
        mAnimPink = AnimationUtils.loadAnimation(this, R.anim.anim_num);
        mAnimGreen = AnimationUtils.loadAnimation(this, R.anim.anim_num);

        mAnimYellow.setAnimationListener(mTextAnimListener1);
        mAnimPink.setAnimationListener(mTextAnimListener2);
        mAnimGreen.setAnimationListener(mTextAnimListener3);

        // mTvYellowAnimGold.setVisibility(View.INVISIBLE);
        // mTvPinkAnimExp.setVisibility(View.INVISIBLE);

    }

    // public void startTextViewAnim() {
    //
    // if (mGold > 0) {
    // mHandler.postDelayed(new Runnable() {
    //
    // @Override
    // public void run() {
    // mTvYellowAnimGold.startAnimation(mAnimYellow);
    // mTvYellowAnimGold.setVisibility(View.VISIBLE);
    // }
    // }, 1000);
    // }
    //
    // if (mPoint > 0) {
    // mHandler.postDelayed(new Runnable() {
    //
    // @Override
    // public void run() {
    // mTvGreenAnimPoint.startAnimation(mAnimGreen);
    // mTvGreenAnimPoint.setVisibility(View.VISIBLE);
    // }
    // }, 1300);
    // }
    //
    // if (mExp > 0) {
    // mHandler.postDelayed(new Runnable() {
    //
    // @Override
    // public void run() {
    // mTvPinkAnimExp.startAnimation(mAnimPink);
    // mTvPinkAnimExp.setVisibility(View.VISIBLE);
    // }
    // }, 1050);
    // }
    //
    // }

    private void repleaseAll() {

        // if (mYellowMissailAnim.isRunning()) {
        // mYellowMissailAnim.stop();
        // }
        // if (mGreenMissailAnim.isRunning()) {
        // mGreenMissailAnim.stop();
        // }
        // if (mPinkMissailAnim.isRunning()) {
        // mPinkMissailAnim.stop();
        // }
        if (mStarsAnim.isRunning()) {
            mStarsAnim.stop();
        }

        // mPinkMissailAnim = null;
        // mGreenMissailAnim = null;
        // mYellowMissailAnim = null;
        mStarsAnim = null;
        mMatchResult = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        repleaseAll();
    }

    public static class TextAnimListener implements AnimationListener {

        private TextView mTextView;

        public TextAnimListener(TextView tv) {
            mTextView = tv;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (animation != null) {
                animation.reset();
            }

            mTextView.startAnimation(animation);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // tv.setVisibility(View.GONE);
        }
    }
}

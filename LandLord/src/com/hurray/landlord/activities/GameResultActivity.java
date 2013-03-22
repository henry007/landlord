
package com.hurray.landlord.activities;

import com.hurray.landlord.R;
import com.hurray.landlord.game.data.GameResult;
import com.hurray.landlord.utils.LogUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class GameResultActivity extends BaseActivity implements OnClickListener {

    public static final String GAME_RESULT = "game_result";

    public static final String AUTO_CLOSE = "auto_close";
    
    public static final String GAME_TYPE = "game_type";

    public static final int BTN_GOON = 1;

    public static final int BTN_EXIT = 2;

    private static final int AUTO_CLOSE_DELAY = 4000;

    private Handler mHandler;

    private Runnable mRunAutoClose;

    private int mGameType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result);

        fillData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        removeAutoCloseDelay();
    }

    private void fillData() {
        Intent i = getIntent();
        GameResult r = (GameResult) i.getSerializableExtra(GAME_RESULT);
        i.removeExtra(GAME_RESULT);
        
        mGameType = i.getIntExtra(GAME_TYPE, -1);
        
        LogUtil.d("Demo","GameResultActivity---gameType:"+mGameType);
        
        boolean isAutoClose = i.getBooleanExtra(AUTO_CLOSE, false);
        if (r != null) {

            TextView record1Name = (TextView) findViewById(R.id.record1_name);
            TextView record1Subtotal = (TextView) findViewById(R.id.record1_subtotal);
            TextView record1Total = (TextView) findViewById(R.id.record1_total);

            TextView record2Name = (TextView) findViewById(R.id.record2_name);
            TextView record2Subtotal = (TextView) findViewById(R.id.record2_subtotal);
            TextView record2Total = (TextView) findViewById(R.id.record2_total);

            TextView record3Name = (TextView) findViewById(R.id.record3_name);
            TextView record3Subtotal = (TextView) findViewById(R.id.record3_subtotal);
            TextView record3Total = (TextView) findViewById(R.id.record3_total);

            TextView leftGameNum = (TextView) findViewById(R.id.left_game_num);
            TextView winWinNum = (TextView) findViewById(R.id.win_win_num);

            record1Name.setText(r.mNameOfRecord1);
            record2Name.setText(r.mNameOfRecord2);
            record3Name.setText(r.mNameOfRecord3);

            if (r.mSubTotalOfRecord1 > 0)
                record1Subtotal.setText("+" + r.mSubTotalOfRecord1);
            else
                record1Subtotal.setText(String.valueOf(r.mSubTotalOfRecord1));

            if (r.mSubTotalOfRecord2 > 0)
                record2Subtotal.setText("+" + r.mSubTotalOfRecord2);
            else
                record2Subtotal.setText(String.valueOf(r.mSubTotalOfRecord2));

            if (r.mSubTotalOfRecord3 > 0)
                record3Subtotal.setText("+" + r.mSubTotalOfRecord3);
            else
                record3Subtotal.setText(String.valueOf(r.mSubTotalOfRecord3));

            if (r.mTotalOfRecord1 > 0)
                record1Total.setText("+" + r.mTotalOfRecord1);
            else
                record1Total.setText(String.valueOf(r.mTotalOfRecord1));

            if (r.mTotalOfRecord2 > 0)
                record2Total.setText("+" + r.mTotalOfRecord2);
            else
                record2Total.setText(String.valueOf(r.mTotalOfRecord2));

            if (r.mTotalOfRecord3 > 0)
                record3Total.setText("+" + r.mTotalOfRecord3);
            else
                record3Total.setText(String.valueOf(r.mTotalOfRecord3));

            leftGameNum.setText(getString(R.string.left_game_num, r.mLeftGameNum));
            winWinNum.setText(getString(R.string.win_win_num, r.mWinNum, r.mLoseNum, r.mWinWinNum));

            if (r.mLeftGameNum <= 0) {
                findViewById(R.id.btn_goon).setVisibility(View.GONE);
                findViewById(R.id.btn_exit).setVisibility(View.VISIBLE);
            }

            boolean isLastGame = (r.mLeftGameNum == 0);
            initButtons(isAutoClose, isLastGame);
            startAutoCloseDelay(isAutoClose, isLastGame);

        } else {
            finish();
        }

    }

    private void initButtons(boolean autoClose, boolean isLastGame) {
        View btnGoon = findViewById(R.id.btn_goon);
        View btnExit = findViewById(R.id.btn_exit);

        if (autoClose) {
            btnGoon.setVisibility(View.GONE);
            if (isLastGame) {
                btnExit.setVisibility(View.VISIBLE);
                btnExit.setOnClickListener(this);
            } else {
                btnExit.setVisibility(View.GONE);
            }
        } else {
            btnExit.setOnClickListener(this);
            btnGoon.setOnClickListener(this);
        }
        if(mGameType==1){
            
            btnExit.setVisibility(View.GONE);
            
        }
    }

    private void startAutoCloseDelay(boolean autoClose, boolean isLastGame) {
        if (mGameType == 1 ||( autoClose && !isLastGame)) {
            mHandler = new Handler();

            mRunAutoClose = new Runnable() {

                @Override
                public void run() {
                    setResult(BTN_GOON);
                    finish();
                }
            };

            mHandler.removeCallbacks(mRunAutoClose);
            mHandler.postDelayed(mRunAutoClose, mGameType==1?2000:AUTO_CLOSE_DELAY);
        }
    }

    private void removeAutoCloseDelay() {
        if (mRunAutoClose != null && mHandler != null)
            mHandler.removeCallbacks(mRunAutoClose);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_goon: {
                setResult(BTN_GOON);
                finish();
            }
                break;
            case R.id.btn_exit:{
                setResult(BTN_EXIT);
                finish();
            }
                break;
        }

    }

}

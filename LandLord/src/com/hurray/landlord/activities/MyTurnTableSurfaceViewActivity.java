
package com.hurray.landlord.activities;

import com.hurray.landlord.Constants;
import com.hurray.landlord.R;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.ToastUtil;
import com.hurray.landlord.view.TurnTableView;
import com.hurray.landlord.view.TurnTableView.OnTurntableListener;
import com.hurray.landlord.view.TurnTableView.TurntableAward;
import com.hurray.lordserver.protocol.message.base.BaseMessage;
import com.hurray.lordserver.protocol.message.prize.BeGetPrizeReq;
import com.hurray.lordserver.protocol.message.prize.BeGetPrizeResp;
import com.hurray.lordserver.protocol.message.prize.BeGetPrizeResp.BeGetPrizeInfo;
import com.hurray.lordserver.protocol.message.prize.GainPrizeReq;
import com.hurray.lordserver.protocol.message.prize.SendPrizeReq;
import com.hurray.lordserver.protocol.message.prize.SendPrizeResp;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class MyTurnTableSurfaceViewActivity extends BaseNetActivity implements OnTurntableListener {

    private static final String TAG = "MyTurnTableSurfaceViewActivity";

    private boolean hasTreasure = false;

    private boolean mSwappedInterface = false; //用户切出去了
    
    private boolean flag = true;

    private boolean popView = false;

    private int selectedNum = -1;
    private long winningNum = -1;

    private TurntableAward[] awardinfos = null;

    private TurnTableView mTurnTable;

    private SendPrizeResp.TurntableInfo[] infos = null;

    private ImageView iv;

    private AnimationDrawable ad;

    private GestureDetector mGestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d("Demo", "onCreate");

        if (Constants.DEBUG_OFFLINE_TURNTABLE_SKIP_NETWORK) {

            initGesture();

            initView();

        } else {

            setContentView(R.layout.common_progressbar);

            doSend(new SendPrizeReq());

        }

    }

    @Override
    public void onReceived(BaseMessage response) {
        super.onReceived(response);

        if (response instanceof SendPrizeResp) {

            SendPrizeResp resp = (SendPrizeResp) response;

            selectedNum = resp.getSelectedNum();

            winningNum = resp.getWinningNum();

            infos = resp.getTurntableInfo();

            initView();

            initGesture();
        }

    }

    private void initView() {

        setContentView(R.layout.turntable_surfaceview);

        iv = (ImageView) findViewById(R.id.hand_anim);

        ad = (AnimationDrawable) iv.getBackground();
        
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                ad.start();

            }
        }, 100);

        mTurnTable = (TurnTableView) findViewById(R.id.sv);

        if (!Constants.DEBUG_OFFLINE_TURNTABLE_SKIP_NETWORK) {

            mTurnTable.setAwardList(infos);

        }

//        mTurnTable.setZOrderOnTop(true);
//
//        mTurnTable.getHolder().setFormat(PixelFormat.TRANSLUCENT);
//
//        mTurnTable.startThread();
    }

    private void initGesture() {
        mGestureDetector = new GestureDetector(new SimpleOnGestureListener() {

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                if ((e1.getY() - e2.getY() <= -10) && flag == true)
                {

                    if (mTurnTable.isShown()) {

                        if (Constants.DEBUG_OFFLINE_TURNTABLE_SKIP_NETWORK) {
                            mTurnTable
                                    .startRotate(MyTurnTableSurfaceViewActivity.this,
                                            8);

                        } else {

                            if (selectedNum != -1) {

                                mTurnTable
                                        .startRotate(MyTurnTableSurfaceViewActivity.this,
                                                selectedNum + 1);

                            }
                        }
                    }

                    ad.stop();

                    iv.setVisibility(View.INVISIBLE);

                    flag = false;
                }

                return true;
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mSwappedInterface){
            
            mTurnTable.mDrawAgain = true;
            
            mSwappedInterface = false;
        }
        LogUtil.d("Demo", "onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onTurntableDone(int result) {

        if (result == 0) {

            GainPrizeReq req = new GainPrizeReq();

            req.setType(0);

            req.setWinningNumber(winningNum);

            doSend(req);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            super.removeListener();

            setResult(RESULT_OK);

            finish();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (event.getRepeatCount() == 0) {
                    super.removeListener();
                    setResult(RESULT_OK);
                    this.finish();
                }
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mGestureDetector == null) {

            return true;
        }

        return mGestureDetector.onTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTurnTable != null) {

            mTurnTable.release();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSwappedInterface = true;
        LogUtil.d("Demo", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}


package com.hurray.landlord.activities;

import com.hurray.landlord.R;
import com.hurray.lordserver.protocol.message.SysCommonPush;
import com.hurray.lordserver.protocol.message.base.BaseMessage;
import com.hurray.lordserver.protocol.message.prize.GainPrizeReq;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class OnlinePrizeActivity extends BaseNetActivity {

    private TextView mTips = null;

    private Button mBtnOK = null;

    private String mInfo = null;

    private boolean mRefreshView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GainPrizeReq req = new GainPrizeReq();

        req.setType(1);

        doSend(req);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        super.addListener();
    }

    private void initView() {
        
        setContentView(R.layout.online_prize_layout);

        mTips = (TextView) findViewById(R.id.tips_incompelete_payment2);
        
        mTips.setText(mInfo);

        mBtnOK = (Button) findViewById(R.id.btn_ok);

        mBtnOK.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                
                setResult(RESULT_OK);

                OnlinePrizeActivity.this.finish();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onReceived(BaseMessage response) {
        super.onReceived(response);

        if (response instanceof SysCommonPush) {

            SysCommonPush push = (SysCommonPush) response;

            if (push.getCode() == SysCommonPush.ONLINE_PRIZE__CODE) {

                mInfo = push.getInfo();
                
                StringBuffer sb = new StringBuffer(mInfo);
                
                sb.insert(sb.length()/2, "\n");
                
                mInfo = sb.toString();

                initView();
                
                super.removeListener();

            }

        }

    }

}

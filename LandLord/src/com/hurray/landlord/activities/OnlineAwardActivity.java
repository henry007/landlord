 
package com.hurray.landlord.activities;

import com.hurray.landlord.R;
import com.hurray.landlord.entity.MyPreferrences.AccountPreferrence;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class OnlineAwardActivity extends BaseNetActivity {

    private Button mButtonBack;

    private TextView giftValue = null;

    private TextView giftDesc = null;

    private ImageView giftType = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_reward4money);
        initView();
    }

    private void initView() {

        AccountPreferrence apf = AccountPreferrence.getSingleton();

        mButtonBack = (Button) findViewById(R.id.btn_ok);

        giftDesc = (TextView) findViewById(R.id.desc);

        giftType = (ImageView) findViewById(R.id.gift_type);

        giftValue = (TextView) findViewById(R.id.award_value);


        int tempMoneyGold = apf.getMoneyGoldVal(0);

        int tempMoneyHeart = apf.getMoneyHeartVal(0);
        
        StringBuffer sb = new StringBuffer(apf.getAwardDesc(""));
        
        if(!sb.toString().equals("")){
            
            sb.insert(sb.capacity()/2, "\n");
        }

        giftDesc.setText(sb.toString());

        if (tempMoneyGold != 0) {

            giftType.setBackgroundResource(R.drawable.img_money_big);
            giftValue.setText(" X"+" "+tempMoneyGold);

        }

//        if (tempMoneyHeart != 0) {
//
//            giftType.setBackgroundResource(R.drawable.img_heart_big);
//            giftValue.setText(" X"+" "+tempMoneyHeart);
//        }
        
        

        mButtonBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // GainPrizeReq req = new GainPrizeReq();
                // req.setType(1);
                // doSend(req);
                // setResult(RESULT_OK);
                OnlineAwardActivity.this.finish();

            }
        });
    };

}

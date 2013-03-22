
package com.hurray.landlord.activities;

import com.hurray.landlord.R;
import com.hurray.landlord.entity.Gender;
import com.hurray.landlord.entity.UserInfo;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class PopPersonInfoActivity extends BaseActivity {

    public static final String USER_INFO = "USER_INFO";

    private UserInfo mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_individual_info);

        findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });

        mUserInfo = (UserInfo) getIntent().getSerializableExtra(USER_INFO);
        if (mUserInfo != null) {
            TextView name = (TextView) findViewById(R.id.tv_user_nickname);
            name.setText(mUserInfo.getNickname());

            TextView level = (TextView) findViewById(R.id.tv_user_level);
            level.setText(String.valueOf(mUserInfo.getLevel()));

            TextView gender = (TextView) findViewById(R.id.tv_user_gender);
            if (mUserInfo.getGender() == Gender.FEMALE) {
                gender.setText("女");
            } else {
                gender.setText("男");
            }

            TextView records = (TextView) findViewById(R.id.tv_user_records);
            records.setText("" + mUserInfo.getVictoryNumber() + "胜" + mUserInfo.getFailureNumber() + "负");

            TextView goldNum = (TextView) findViewById(R.id.tv_user_gold_num);
            goldNum.setText("" + mUserInfo.getGold());

            TextView hongTao = (TextView) findViewById(R.id.tv_user_hong_tao);
            hongTao.setText("" + mUserInfo.getMoney());

            TextView integral = (TextView) findViewById(R.id.tv_user_integral);
            integral.setText("" + mUserInfo.getScore());

            TextView title = (TextView) findViewById(R.id.tv_user_title);
            title.setText("" + mUserInfo.getRank());

        } else {
            finish();
        }
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        
        finish();
    }

}

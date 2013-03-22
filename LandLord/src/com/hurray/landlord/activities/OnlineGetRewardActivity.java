
package com.hurray.landlord.activities;

import com.hurray.landlord.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class OnlineGetRewardActivity extends BaseNetActivity {

    private Button mButtonBack;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_reward4money);

        mButtonBack = (Button) findViewById(R.id.btn_ok);

        mButtonBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                OnlineGetRewardActivity.this.finish();

            }
        });
    };

}

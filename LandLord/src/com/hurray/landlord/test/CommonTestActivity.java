
package com.hurray.landlord.test;

import com.hurray.landlord.activities.BaseNetActivity;

import android.os.Bundle;

public class CommonTestActivity extends BaseNetActivity {

    private static final String TAG = "CommonTestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       /* super.onCreate(savedInstanceState);
        setContentView(R.layout.common_test_activity);

        LogUtil.d(TAG, "----------------------------------");
        for (int i = 1; i <= 54; i++) {
            int clientId = CardIdAdapter.toClientCardId(i);
            LogUtil.d(TAG, "serverCardId = " + i + " clientCardId = " + clientId);
        }
        LogUtil.d(TAG, "----------------------------------");
        for (int i = 0; i < 54; i++) {
            int serverId = CardIdAdapter.toServerCardId(i);
            LogUtil.d(TAG, "clientCardId = " + i + " serverCardId = " + serverId);
        }
        LogUtil.d(TAG, "----------------------------------");*/

    }

}

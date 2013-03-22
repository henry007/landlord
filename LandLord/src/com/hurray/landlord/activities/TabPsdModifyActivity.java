
package com.hurray.landlord.activities;

import com.hurray.landlord.R;
import com.hurray.landlord.entity.MyPreferrences.AccountPreferrence;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.ToastUtil;
import com.hurray.lordserver.protocol.message.account.ModifyPasswordReq;
import com.hurray.lordserver.protocol.message.account.ModifyPasswordResp;
import com.hurray.lordserver.protocol.message.base.BaseMessage;
import com.hurray.lordserver.protocol.message.user.LogoutUserReq;
import com.hurray.lordserver.protocol.message.user.LogoutUserResp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Pattern;

public class TabPsdModifyActivity extends BaseNetActivity implements OnClickListener {

    private String TAG = "TabPsdModifyActivity";

    public static final String PASSWORD = "[a-zA-Z0-9]{6,18}";

    public static final String MAIL = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+";

    private Button mBtnCommit;

    private EditText mEdtNewPsd, mEdtOldPsd, mEdtNewPsd2;

    private String mOldPsdContent = null, mNewPsdContent1 = null, mNewPsdContent2 = null;

    private ModifyPasswordReq mModifyPasswordReq;

    private Animation mShakeAnim;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case R.string.modifyPsd_fail:

                    startShake(mEdtOldPsd);

                    LogUtil.i(TAG, "注册失败");

                    break;

                default:
                    break;
            }
            ToastUtil.show(getResources().getString(msg.what));

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individual_psd_modify);

        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        super.addListener();
    }

    private void initView() {

        mBtnCommit = (Button) findViewById(R.id.btn_password_change);

        mEdtNewPsd = (EditText) findViewById(R.id.edt_new_psd1);

        mEdtNewPsd2 = (EditText) findViewById(R.id.edt_new_psd2);

        mEdtOldPsd = (EditText) findViewById(R.id.edt_old_psd);

        mBtnCommit.setOnClickListener(this);

        // mBtnCommit.setEnabled(false);
    }

    public void startShake(EditText edt) {

        initShake();

        if (edt != null && mShakeAnim != null) {
            edt.startAnimation(mShakeAnim);
        }
    }

    public void initShake() {
        if (mShakeAnim == null) {
            mShakeAnim = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.shake);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEdtOldPsd.clearAnimation();
        mShakeAnim = null;
    }

    @Override
    public void onClick(View v) {

        mOldPsdContent = mEdtOldPsd.getText().toString();

        mNewPsdContent1 = mEdtNewPsd.getText().toString();

        mNewPsdContent2 = mEdtNewPsd2.getText().toString();

        // SharedPreferences sp = getSharedPreferences(Constants.PREFS_ACCOUNT,
        // 0);
        //
        // long uid = sp.getLong(Constants.USER_ID, 0);

        AccountPreferrence apf = AccountPreferrence.getSingleton();

        long uid = apf.getUserId(0);

        if (!Pattern.compile(PASSWORD).matcher(mOldPsdContent)
                .matches()) {
            startShake(mEdtOldPsd);
            ToastUtil.show(getResources().getString(R.string.login_password_verify));
            return;
        }
        if (!Pattern.compile(PASSWORD).matcher(mNewPsdContent1)
                .matches()) {
            startShake(mEdtNewPsd);
            ToastUtil.show(getResources().getString(R.string.login_password_verify));
            return;
        }

        if (!mNewPsdContent1.equals(mNewPsdContent2)) {
            startShake(mEdtNewPsd2);
            ToastUtil.show(getResources().getString(R.string.modifyPsd_diff_psd));
            return;
        }

        mModifyPasswordReq = new ModifyPasswordReq();

        mModifyPasswordReq.setOldPassword(mOldPsdContent);

        mModifyPasswordReq.setNewPassword(mNewPsdContent1);

        mModifyPasswordReq.setUid(uid);

        doSend(mModifyPasswordReq);

        mBtnCommit.setVisibility(View.INVISIBLE);

    };

    @Override
    public void onReceived(BaseMessage response) {

        if (response instanceof ModifyPasswordResp) {

            ModifyPasswordResp resp = (ModifyPasswordResp) response;

            LogUtil.i(TAG, "resp:" + resp.getResultCode());

            if (resp.isSucceeded()) {

                mHandler.sendEmptyMessage(R.string.modifyPsd_success);
                // zzy add for 持久化新的密码 by 2012-6-19 start
                AccountPreferrence apf = AccountPreferrence.getSingleton();
                apf.setPassword(mNewPsdContent1);
                // end

                doSend(new LogoutUserReq());
                super.removeListener();

            } else {

                switch (resp.getResultCode()) {

                    case ModifyPasswordResp.CHANGE_FAILED: {

                        mHandler.sendEmptyMessage(R.string.modifyPsd_fail);

                    }
                        break;

                    default:
                        break;
                }
            }

        }
        // if(response instanceof LogoutUserResp)
        // {
        // LogoutUserResp resp = (LogoutUserResp) response;
        // if(resp.isSuccess()){
        //
        // finish();
        //
        // }
        //
        // }

        if (!mBtnCommit.isShown()) {

            mBtnCommit.setVisibility(View.VISIBLE);

        }

    }
}

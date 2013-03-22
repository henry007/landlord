
package com.hurray.landlord.test;

import com.hurray.landlord.R;
import com.hurray.landlord.activities.BaseNetActivity;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.ToastUtil;
import com.hurray.lordserver.protocol.message.account.RegisterUserReq;
import com.hurray.lordserver.protocol.message.account.RegisterUserResp;
import com.hurray.lordserver.protocol.message.base.BaseMessage;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class TestRegisterActivity extends BaseNetActivity {
    private EditText mEmail;
    private EditText mPassword;
    private EditText mNickname;
    private Button mBtnOk;
    private Button mBtnCancel;

    private String email;
    private String password;
    private String nickname;

    private RegisterUserReq mRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_register_activity);

        mEmail = (EditText) findViewById(R.id.editTextEmail);
        mPassword = (EditText) findViewById(R.id.editTextPassword);
        mNickname = (EditText) findViewById(R.id.editTextNickname);

        mBtnOk = (Button) findViewById(R.id.buttonOk);
        mBtnOk.setOnClickListener(mOkListener);
        mBtnCancel = (Button) findViewById(R.id.buttonCancel);
        mBtnCancel.setOnClickListener(mCancelListener);
    }

    private View.OnClickListener mOkListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            email = mEmail.getText().toString();
            password = mPassword.getText().toString();
            nickname = mNickname.getText().toString();

            mRequest = new RegisterUserReq();

            mRequest.setEmail(email);
            mRequest.setPassword(password);
            mRequest.setNickname(nickname);
            mRequest.setSex(0);

            doSend(mRequest);
        }
    };

    private View.OnClickListener mCancelListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            finish();
        }
    };

    @Override
    public void onReceived(BaseMessage msg) {
        if (msg instanceof RegisterUserResp) {
            RegisterUserResp resp = (RegisterUserResp) msg;
            if (resp.isSucceeded()) {
                ToastUtil.show("注册成功！");
            } else {
                LogUtil.d("Fail: ", resp.getResultDesc().toString());
            }
        }
    }

    @Override
    public void onSent(boolean status, BaseMessage msg) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onConnected() {
        doSend(mRequest);
    }

    @Override
    public void onConnectError() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDisconnected() {
        // TODO Auto-generated method stub

    }
}

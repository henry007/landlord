
package com.hurray.landlord.activities;

import com.hurray.landlord.R;
import com.hurray.landlord.entity.MyPreferrences.AccountPreferrence;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.ToastUtil;
import com.hurray.lordserver.protocol.message.account.PasswordLoginReq;
import com.hurray.lordserver.protocol.message.account.PasswordLoginResp;
import com.hurray.lordserver.protocol.message.account.PersonCenterReq;
import com.hurray.lordserver.protocol.message.base.BaseMessage;
import com.hurray.lordserver.protocol.message.base.Response;
import com.hurray.lordserver.protocol.message.user.LogoutUserReq;

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

public class TabUserChangedActivity extends BaseNetActivity implements OnClickListener {

    private static final String TAG = "TabUserChangedActivity";

    public static final String PASSWORD = "[a-zA-Z0-9]{6,18}";

    public static final String MAIL = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+";

    private String mAccountContent;

    private String mPasswordContent;

    private EditText mAccount;

    private EditText mPassword;

    private Animation shake;

    private Button btnLogin;

    private PasswordLoginReq mRequest;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case R.string.login_change_success:

                    ToastUtil.show(R.string.login_change_success);

                    TabUserChangedActivity.this.finish();

                    break;

                case R.string.login_error:
                    mAccount.startAnimation(shake);
                    ToastUtil.show(R.string.login_error);
                    break;

                case R.string.login_exist:
                    mPassword.startAnimation(shake);
                    break;

                case R.string.unknow_error:
                    ToastUtil.show(R.string.unknow_error);
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
        setContentView(R.layout.individual_user_changed);

        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        super.addListener();

    }

    public void initView() {

        btnLogin = (Button) findViewById(R.id.btn_login_commit);

        mAccount = (EditText) findViewById(R.id.edt_user_login);

        mPassword = (EditText) findViewById(R.id.edt_user_psd);

        shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onReceived(BaseMessage msg) {

        if (msg instanceof PasswordLoginResp) {

            PasswordLoginResp resp = (PasswordLoginResp) msg;

            switch (resp.getResultCode()) {

                case PasswordLoginResp.LOGIN_PASSWORD_ERROR:
                    mHandler.sendEmptyMessage(R.string.login_error);
                    break;
                case PasswordLoginResp.LOGIN_USER_NOT_EXIST:
                    mHandler.sendEmptyMessage(R.string.login_exist);
                    break;

                case Response.RESULT_OK:

                    if (resp instanceof PasswordLoginResp) {

                        mHandler.sendEmptyMessage(R.string.login_change_success);

                        AccountPreferrence apf = AccountPreferrence.getSingleton();

                        apf.setEmail(mAccountContent);

                        apf.setPassword(mPasswordContent);
                    }
                    doSend(new LogoutUserReq());
                    super.removeListener();
                    break;
                case Response.RESULT_SERVER_ERROR:
                    ToastUtil.show("服务器繁忙");

                    break;
                default:
                    mHandler.sendEmptyMessage(R.string.unknow_error);
                    break;
            }

            btnLogin.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_commit: {

                AccountPreferrence apf = AccountPreferrence.getSingleton();

                mAccountContent = mAccount.getText().toString().trim();

                mPasswordContent = mPassword.getText().toString();

                mRequest = new PasswordLoginReq();

                // verify email address
                // if
                // (!Pattern.compile(MAIL).matcher(mAccountContent).matches()) {
                // mAccount.startAnimation(shake);
                // ToastUtil.show(getResources().getString(R.string.email_error));
                // return;
                // }

                // verify same account
                if (mAccountContent.equals(apf.getEmail(null))) {
                    mHandler.sendEmptyMessage(R.string.login_same_account);
                    ;
                    mAccount.startAnimation(shake);
                    ToastUtil.show(R.string.login_same_account);
                    return;
                }

                // verify password
                if (!Pattern.compile(PASSWORD).matcher(mPasswordContent).matches()) {
                    mPassword.startAnimation(shake);
                    ToastUtil.show(getResources().getString(R.string.login_password_verify));
                    return;
                }

                mRequest.setEmail(mAccountContent);

                mRequest.setPassword(mPasswordContent);

                doSend(mRequest);

                btnLogin.setVisibility(View.INVISIBLE);
            }
                break;

            default:
                break;
        }

    }

}

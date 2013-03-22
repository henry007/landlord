
package com.hurray.landlord.activities;

import com.hurray.landlord.R;
import com.hurray.landlord.entity.MyPreferrences.AccountPreferrence;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.ToastUtil;
import com.hurray.lordserver.protocol.message.account.RegisterUserReq;
import com.hurray.lordserver.protocol.message.account.RegisterUserResp;
import com.hurray.lordserver.protocol.message.base.BaseMessage;
import com.hurray.lordserver.protocol.message.base.Response;
import com.hurray.lordserver.protocol.message.user.LogoutUserReq;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.regex.Pattern;

public class OnlineRegisterActivity extends BaseNetActivity {

    private static final String PASSWORD = "[a-zA-Z0-9]{6,18}";

    private static final String[] EMAILS_SUFFIX = new String[] {
            "qq.com", "sina.com.cn", "hurray.com.cn", "gmail.com", "163.com", "126.com",
            "snda.com", "yahoo.com"
    };

    // private EditText mEmail;

    private EditText mPassword;
    // private EditText mConfirmPassword;
    private EditText mNickname;
    private Button mBtnRegist;
    private Button mBtnCancel;
    private CheckBox mMale;
    private CheckBox mFemale;
    // private String mEmailText;
    private String mPasswordText;
    // private String mConfirmPasswordText;
    private String mNicknameText;

    private Animation mShakeAnim;

    private RegisterUserReq mRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.online_register_activity);

        // mEmail = (EditText) findViewById(R.id.et_email);
        // mEmail.addTextChangedListener(new TextWatcher() {
        //
        // @Override
        // public void onTextChanged(CharSequence s, int start, int before, int
        // count) {
        //
        // String str = s.toString();
        // if (str.endsWith("@")) {
        // ArrayAdapter<String> adapter = getAutoCompleteAdapter(str);
        // mEmail.setAdapter(adapter);
        // mEmail.setThreshold(str.length());
        // }
        // }
        //
        // @Override
        // public void beforeTextChanged(CharSequence s, int start, int count,
        // int after) {
        //
        // }
        //
        // @Override
        // public void afterTextChanged(Editable s) {
        //
        // }
        // });

        mPassword = (EditText) findViewById(R.id.et_password);
        // mConfirmPassword = (EditText) findViewById(R.id.et_confirm_password);
        mNickname = (EditText) findViewById(R.id.et_nickname);

        mBtnRegist = (Button) findViewById(R.id.btn_regist);
        mBtnRegist.setOnClickListener(mRegistListener);
        mBtnCancel = (Button) findViewById(R.id.online_register_back);
        mBtnCancel.setOnClickListener(mCancelListener);
        mMale = (CheckBox) findViewById(R.id.cb_male);
        mMale.setOnClickListener(mMaleListener);
        mFemale = (CheckBox) findViewById(R.id.cb_female);
        mFemale.setOnClickListener(mFemaleListener);
        mFemale.setChecked(true);
        mShakeAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
    }

    @Override
    protected void onResume() {
        super.onResume();
        super.addListener();
    }

    private ArrayAdapter<String> getAutoCompleteAdapter(String str) {
        int len = EMAILS_SUFFIX.length;
        String[] guessEmails = new String[len];
        for (int i = 0; i < len; i++) {
            guessEmails[i] = str + EMAILS_SUFFIX[i];
        }

        return new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, guessEmails);
    }

    private View.OnClickListener mRegistListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            // mEmailText = mEmail.getText().toString();
            mPasswordText = mPassword.getText().toString();
            // mConfirmPasswordText = mConfirmPassword.getText().toString();
            mNicknameText = mNickname.getText().toString();

            // verify email address
            // if
            // (!Pattern.compile(TabUserChangedActivity.MAIL).matcher(mEmailText).matches())
            // {
            // mEmail.startAnimation(mShakeAnim);
            // ToastUtil.show(getResources().getString(R.string.email_error));
            // return;
            // }

            // verify password
            if (!Pattern.compile(PASSWORD).matcher(mPasswordText).matches()) {
                mPassword.startAnimation(mShakeAnim);
                ToastUtil.show(getResources().getString(R.string.password_verify));
                return;
            }

            // verify confirm password
            // if (!mPasswordText.equals(mConfirmPasswordText)) {
            // mPassword.startAnimation(mShakeAnim);
            // mConfirmPassword.startAnimation(mShakeAnim);
            // ToastUtil.show(getResources().getString(R.string.psd_not_equal_con_psd));
            // return;
            // }

            // verify nickname
            if (mNicknameText.trim().toString().equals("") || mNicknameText.trim().length() > 8) {
                mNickname.startAnimation(mShakeAnim);
                ToastUtil.show(getResources().getString(R.string.nick_name_too_long));
                return;
            }

            mBtnRegist.setVisibility(View.INVISIBLE);

            mRequest = new RegisterUserReq();
            int male = mMale.isChecked() ? RegisterUserReq.SEX_MALE
                    : RegisterUserReq.SEX_FEMALE;

            // mRequest.setEmail(mEmailText);
            mRequest.setPassword(mPasswordText);
            mRequest.setNickname(mNicknameText);
            mRequest.setSex(male);

            doSend(mRequest);
        }
    };

    private View.OnClickListener mCancelListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            setResult(RESULT_OK);
            finish();
        }
    };

    private View.OnClickListener mMaleListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            mMale.setChecked(true);
            mFemale.setChecked(false);
        }
    };

    private View.OnClickListener mFemaleListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            mMale.setChecked(false);
            mFemale.setChecked(true);
        }
    };

    @SuppressWarnings("static-access")
    @Override
    public void onReceived(BaseMessage msg) {

        if (msg instanceof RegisterUserResp) {

            mBtnRegist.setVisibility(View.VISIBLE);

            if (((RegisterUserResp) msg).getResultCode() == Response.RESULT_SERVER_ERROR) {
                ToastUtil.show("服务器繁忙");
                return;
            }

            RegisterUserResp resp = (RegisterUserResp) msg;
            if (resp.isSucceeded()) {
                // save data
                AccountPreferrence apf = AccountPreferrence.getSingleton();
                apf.setNickName(mNicknameText);
                // zzy modify for 持久化 密码 、用户、性别 by 2012-06-25 start
                apf.setPassword(mPasswordText);
                LogUtil.d("Demo", mPasswordText + "2222222222");
                // apf.setEmail(mEmailText);
                int sex = mRequest.getSex();
                if (RegisterUserReq.SEX_MALE == sex)
                    apf.setMale(true);
                else
                    apf.setMale(false);

                LogUtil.d("Demo",
                        apf.getNickName("") + "||" + apf.getPassword("") + "||" + apf.getEmail("")
                                + "||" + apf.isMale(false));
                apf.setEmail(resp.getEmail());
                doSend(new LogoutUserReq());
                super.removeListener();
                finish();
                LogUtil.d("Demo", "Register--->finish");
                ToastUtil.show(R.string.regist_success);

            } else {
                switch (resp.getResultCode()) {
                    case RegisterUserResp.ERROR_EMAIL_EXIST: {
                        ToastUtil.show(R.string.email_exist);
                        // mEmail.startAnimation(mShakeAnim);
                    }
                        break;
                    case RegisterUserResp.ERROR_NICKNAME_EXIST: {
                        ToastUtil.show(R.string.nick_name_exist);
                        mNickname.startAnimation(mShakeAnim);
                    }
                        break;
                    default: {
                        String desc = resp.getResultDesc();
                        if (desc != null && desc.length() > 0) {
                            ToastUtil.show(desc);
                        } else {
                            ToastUtil.show(R.string.unknow_error);
                        }
                    }
                        break;
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.d("Demo", "KeyCode-->" + keyCode);

        return super.onKeyDown(keyCode, event);
    }

}

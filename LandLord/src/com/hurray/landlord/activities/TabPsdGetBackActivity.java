
package com.hurray.landlord.activities;

import com.hurray.landlord.Constants;
import com.hurray.landlord.R;
import com.hurray.landlord.entity.MyPreferrences.AccountPreferrence;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.ToastUtil;
import com.hurray.lordserver.protocol.message.account.FetchPasswordReq;

import android.app.Activity;
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

public class TabPsdGetBackActivity extends BaseNetActivity implements OnClickListener {

    public static final String MAIL = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+";

    private String TAG = "TabPsdModifyActivity";

    private Button mBtnGetPsdBack;

    private EditText mEdtMail;

    private Animation shake;

    private String mMailContent;

    private FetchPasswordReq mRequest;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            LogUtil.d(TAG, "msg:" + msg.what);

            switch (msg.what) {

                case 0:
                    ToastUtil.show("信箱提交成功");
                    
                    if (getParent() == null) {
                        setResult(Activity.RESULT_OK);
                    }
                    else {
                        getParent().setResult(Activity.RESULT_OK);
                    }
                    

                    TabPsdGetBackActivity.this.finish();

                    break;
            }
            // ToastUtil.show(getResources().getString(msg.what));
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individual_psd_getback);

//        initView();

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    public void initView() {

        mBtnGetPsdBack = (Button) findViewById(R.id.btn_password_getback);

//        mEdtMail = (EditText) findViewById(R.id.edt_mail_content);

        shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

        mBtnGetPsdBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_password_getback: {

                mMailContent = mEdtMail.getText().toString().trim();

                mRequest = new FetchPasswordReq();

                // verify email address
                // if (!Pattern.compile(MAIL).matcher(mMailContent).matches()) {
                // mEdtMail.startAnimation(shake);
                // ToastUtil.show(getResources().getString(R.string.email_error));
                // return;
                // }

                mHandler.sendEmptyMessage(0);

                mRequest.setCheckEmail(mMailContent);

                doSend(mRequest);
            }
                break;
        }

    }
}

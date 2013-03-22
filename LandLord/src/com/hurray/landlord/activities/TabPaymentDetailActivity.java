package com.hurray.landlord.activities;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hurray.landlord.R;
import com.hurray.lordserver.protocol.message.base.BaseMessage;
import com.hurray.lordserver.protocol.message.pay.PayHandleReq;
import com.hurray.lordserver.protocol.message.pay.PayInfoReq;
import com.hurray.lordserver.protocol.message.pay.PayInfoResp;

public class TabPaymentDetailActivity extends BaseNetActivity {

	private ArrayAdapter<String> mAdapter;
	private Toast toast;
	private String[] frpID;
	private String goldDes;
	private int[] deposits;
	private int index;
	private int fee;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment_detail_layout);

		goldDes = getIntent().getStringExtra(TabPaymentActivity.KEY_FEE_GOLD);
		PayInfoReq req = new PayInfoReq();
		req.setFeeMoney(getIntent().getIntExtra(
				TabPaymentActivity.KEY_FEE_MONEY, 0));
		doSend(req);
		Button payBack = (Button)findViewById(R.id.pay_detail_back);
		payBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
	            finish();
			}
		});
	}

	@Override
	public void onReceived(BaseMessage response) {
		if (response instanceof PayInfoResp) {
			PayInfoResp resp = (PayInfoResp) response;
			if (resp.isSucceeded()) {
				initRespData(resp);
			}
		}
	}

	private void ToastText(String text) {
		if (toast == null) {
			toast = Toast.makeText(TabPaymentDetailActivity.this, text,
					Toast.LENGTH_SHORT);
		} else {
			toast.cancel();
			toast.setText(text);
		}
		toast.show();
	}

	private void initRespData(PayInfoResp resp) {
		index = 0;
		// 注意
		TextView title = (TextView) findViewById(R.id.pay_layout_title);
		title.setText(resp.getFeeInfo());
		// 面值
		fee = resp.getFeeMoney();
		TextView feeMoney = (TextView) findViewById(R.id.pay_layout_paymoney);
		feeMoney.setText("面值：" + fee + "元    充值金币：" + goldDes);

		// 下拉菜单
		Spinner mPaymentTypes = (Spinner) findViewById(R.id.sp1);
		// 渠道名数组
		String[] frpNames = new String[resp.getFeeInfoBodys().length];
		frpID = new String[resp.getFeeInfoBodys().length];
		deposits = new int[resp.getFeeInfoBodys().length];
		for (int i = 0; i < frpNames.length; ++i) {
			frpNames[i] = resp.getFeeInfoBodys()[i].frpName;
			frpID[i] = resp.getFeeInfoBodys()[i].frpId;
			deposits[i] = resp.getFeeInfoBodys()[i].depositType;
		}
		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, frpNames);

		mPaymentTypes.setAdapter(mAdapter);

		mPaymentTypes.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (position < frpID.length) {
					index = position;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		Button paySend = (Button) findViewById(R.id.pay_button);

		paySend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText editTextID = (EditText) findViewById(R.id.pay_edit_id);
				EditText editTextPWD = (EditText) findViewById(R.id.pay_edit_pwd);
				String id = editTextID.getText().toString();
				String pwd = editTextPWD.getText().toString();
				if (id == null || id.length() == 0) {
					ToastText("序列号不能为空！");
				} else if (pwd == null || pwd.length() == 0) {
					ToastText("密码不能为空！");
				} else {
					PayHandleReq payHandleReq = new PayHandleReq();
					payHandleReq.setDepositType(deposits[index]);
					payHandleReq.setFrpId(frpID[index]);
					payHandleReq.setCardAmt(fee);
					payHandleReq.setCardNo(id);
					payHandleReq.setCardPwd(pwd);
					doSend(payHandleReq);
					finish();
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(RESULT_OK);
            finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}

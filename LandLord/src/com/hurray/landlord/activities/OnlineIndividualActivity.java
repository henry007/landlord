package com.hurray.landlord.activities;

import com.hurray.landlord.R;
import com.hurray.landlord.entity.MyPreferrences.AccountPreferrence;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.view.VerticalTabWigdet;
import com.hurray.lordserver.protocol.message.base.BaseMessage;
import com.hurray.lordserver.protocol.message.card.SystemFlagReq;
import com.hurray.lordserver.protocol.message.card.SystemFlagResp;
import com.hurray.lordserver.protocol.message.error.AuthErrorResp;
import com.hurray.lordserver.protocol.message.user.LogoutUserResp;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class OnlineIndividualActivity extends NetTabActivity implements
		OnTabChangeListener {
	private TabHost tabhost;
	private TabHost.TabSpec actorInfo, personalAchieve, systemInfo, mobileBind,
			psdModify, psdGetBack, userChanged, payment;
	private VerticalTabWigdet mWidget;
	private ImageButton mBack;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.individual_info);
		if (getAndroidSDKVersion() > 13) {
			Window window = getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.width = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.95);
			lp.height = (int) (getWindowManager().getDefaultDisplay()
					.getHeight() * 0.9);
			window.setAttributes(lp);
		}
		doSend(new SystemFlagReq());
		initView();
		initSpecs();
	}

	private int getAndroidSDKVersion() {
		int version = 0;
		try {
			version = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
			LogUtil.e("Demo", e.toString());
		}
		return version;
	}

	@Override
	protected void onResume() {
		super.onResume();
		super.addListener();
		LogUtil.d("Demo", "Individual-onResume");
//		if (SoundSwitch.isMusicOn()) {
//			playBgMusic();
//		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.d("Demo", "Individual-onPause");
//		if (SoundSwitch.isMusicOn()) {
//			stopBgMusic();
//		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		LogUtil.d("Demo", "Individual-onStop");
	}

	@Override
	public void onReceived(BaseMessage response) {
		super.onReceived(response);
		if (response instanceof LogoutUserResp) {
			finish();
			super.removeListener();
		}
		if (response instanceof AuthErrorResp) {
			finish();
			super.removeListener();
		}
		if (response instanceof SystemFlagResp) {
			SystemFlagResp resp = (SystemFlagResp) response;
			int count = resp.getCount();
			if (count > 0) {
				View view = mWidget.getChildAt(2);
				view.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.btn_selfinfo2));
			}
			boolean ispay = getIntent().getBooleanExtra("pay", false);
			if(ispay){
				tabhost.setCurrentTab(7);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.d("Demo", "IndividualActivity----->>onDestroy!!!");
	}

	private void initView() {
		mBack = (ImageButton) findViewById(R.id.btn_back);
		mBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
				OnlineIndividualActivity.this.finish();
			}
		});
	}

	private void initSpecs() {
		tabhost = getTabHost();
		mWidget = (VerticalTabWigdet) findViewById(android.R.id.tabs);
		addTabs(TabActorInfoActivity.class, actorInfo, "1", "角色信息");
		addTabs(TabPersonalAchieveActivity.class, personalAchieve, "2", "个人成就");
		addTabs(TabSystemInfoActivity.class, systemInfo, "3", "系统信息");
		addTabs(TabMobileBindActivity.class, mobileBind, "4", "手机绑定");
		addTabs(TabPsdModifyActivity.class, psdModify, "5", "密码修改");
		addTabs(TabPsdGetBackActivity.class, psdGetBack, "6", "密码找回");
		addTabs(TabUserChangedActivity.class, userChanged, "7", "更换用户");
		addTabs(TabPaymentActivity.class, payment, "8", "充值");
		for (int i = 0; i < mWidget.getChildCount(); i++) {
			TextView tv = (TextView) mWidget.getChildAt(i).findViewById(
					android.R.id.title);
			tv.setGravity(Gravity.CENTER);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);// 设置字体的大小；
			tv.setTextColor(Color.WHITE);// 设置字体的颜色；
		}
		boolean isGuest = AccountPreferrence.getSingleton().isGuest(true);
		if (isGuest) {
			mWidget.getChildAt(4).setVisibility(View.GONE);
			mWidget.getChildAt(5).setVisibility(View.GONE);
		}
	}

	private void addTabs(Class<?> c, TabSpec spec, String specName,
			String indicatorName) {
		Intent intent = new Intent(this, c);
		spec = tabhost.newTabSpec(specName).setIndicator(indicatorName)
				.setContent(intent);
		tabhost.addTab(spec);
	}

	@Override
	public void onTabChanged(String tabId) {
	}

}

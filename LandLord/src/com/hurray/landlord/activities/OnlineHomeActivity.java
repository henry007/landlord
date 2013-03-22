
package com.hurray.landlord.activities;

import com.hurray.landlord.Constants;
import com.hurray.landlord.R;
import com.hurray.landlord.entity.MyPreferrences.AccountPreferrence;
import com.hurray.landlord.entity.OnlineHomeInfo;
import com.hurray.landlord.sdk.NoticeSend;
import com.hurray.landlord.sdk.SdkManagerJuZi;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.ToastUtil;
import com.hurray.landlord.view.OnlineAvatar;
import com.hurray.landlord.view.TextProgressBar;
import com.hurray.lordserver.protocol.message.SysCommonPush;
import com.hurray.lordserver.protocol.message.account.PersonCenterReq;
import com.hurray.lordserver.protocol.message.account.PersonCenterResp;
import com.hurray.lordserver.protocol.message.announcement.AnnouncementResp;
import com.hurray.lordserver.protocol.message.base.BaseMessage;
import com.hurray.lordserver.protocol.message.goods.ShowBagReq;
import com.hurray.lordserver.protocol.message.goods.ShowBagResp;
import com.hurray.lordserver.protocol.message.phone.PhoneInfoReq;
import com.hurray.lordserver.protocol.message.prize.BeGetPrizeReq;
import com.hurray.lordserver.protocol.message.prize.BeGetPrizeResp;
import com.hurray.lordserver.protocol.message.prize.BeGetPrizeResp.BeGetPrizeInfo;
import com.hurray.lordserver.protocol.message.rank.RankListReq;
import com.hurray.lordserver.protocol.message.shop.OpenShopReq;
import com.hurray.lordserver.protocol.message.shop.OpenShopResp;
import com.hurray.lordserver.protocol.message.user.LogoutUserReq;
import com.hurray.lordserver.protocol.message.user.LogoutUserResp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
//import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class OnlineHomeActivity extends NetTabActivity implements View.OnClickListener {

    private static final int STARTTREASUREANIM = 100;

    private static final int STARTAWARDANIM = 101;

    private static final int STOPTREASUREANIM = 102;

    private static final int STOPAWARDANIM = 103;

    private boolean hasTreasure = false;

    private boolean hasAward = false;

    private static final int LEVEL_IMAGE_IDS[] = {
            R.drawable.zero, R.drawable.one,
            R.drawable.two, R.drawable.three,
            R.drawable.four, R.drawable.five,
            R.drawable.six, R.drawable.seven,
            R.drawable.eight, R.drawable.nine
    };

    private ImageView mLevelTenNum;
    private ImageView mLevelBitNum;

    private TextView mName;

    private TextView mGold;

    private TextProgressBar mLevelBar;

    private Button mBtnSelfInfo;

    private Button mBtnBack;

    private Button mBtnPay;

    private ImageView mRegist;
    private ImageView mAward; // 奖
    private ImageView mTreasure; // 宝

    private AnimationDrawable mRegistAnim;
    private AnimationDrawable mAwardAnim;
    private AnimationDrawable mTreasureAnim;

    private OnlineAvatar mOnlineAvatr;
    private View mTabContent;
    private View mOnlineHomeLayout;

    public OnlineHomeInfo mInfo;

    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case STARTTREASUREANIM:

                    hasTreasure = true;
                    checkTreasure();
                    break;

                case STARTAWARDANIM:
                    hasAward = true;
                    checkAward();

                    break;

                case STOPTREASUREANIM:
                    hasTreasure = false;
                    mTreasure.setBackgroundResource(R.drawable.treasure0);
                    if (mTreasureAnim != null) {
                        mTreasureAnim.setOneShot(true);
                    }

                    break;

                case STOPAWARDANIM:
                    hasAward = false;
                    mAward.setBackgroundResource(R.drawable.award0);
                    if (mAwardAnim != null) {
                        mAwardAnim.setOneShot(true);
                    }

                    break;

                default:
                    break;
            }

        };

    };

//    private OnSharedPreferenceChangeListener mOnAccountChangeListener;

    private TabHost tabHost;

    private TabSpec spec;

    protected String curTab = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_home_activity);
    	
        initView();

        initTab();

        popAwardActivity();
        
        if (SdkManagerJuZi.power) {
			SdkManagerJuZi.getInstance().setSendListener(this);
			SdkManagerJuZi.getInstance().startThread();
		}
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    public OnlineHomeInfo getHomeInfo() {
        if (mInfo != null) {

            return mInfo;
        }

        return null;
    }

    private void popAwardActivity() {
        AccountPreferrence apf = AccountPreferrence.getSingleton();

        boolean popAward = apf.getAwardPopFlag(false);

        if (popAward == true) {

            startActivity(new Intent(OnlineHomeActivity.this, OnlineAwardActivity.class));

            apf.setAwardPopFlag(false);

        }
    }

    private void initView() {

        mInfo = new OnlineHomeInfo();

        mGold = (TextView) findViewById(R.id.tv_user_gold);
        mLevelTenNum = (ImageView) findViewById(R.id.iv_level_ten);
        mLevelBitNum = (ImageView) findViewById(R.id.iv_level_bit);
        mName = (TextView) findViewById(R.id.tv_online_name);
        mLevelBar = (TextProgressBar) findViewById(R.id.progressBar_exp);

        mBtnSelfInfo = (Button) findViewById(R.id.btn_selfinfo);
        mBtnSelfInfo.setOnClickListener(this);
        
        mBtnPay = (Button) findViewById(R.id.btn_pay);
        mBtnPay.setOnClickListener(this);

        mBtnBack = (Button) findViewById(R.id.btn_online_back);
        mBtnBack.setOnClickListener(this);

        mRegist = (ImageView) findViewById(R.id.btn_online_regist);
        mRegist.setOnClickListener(this);
        mRegistAnim = (AnimationDrawable) mRegist.getBackground();

        mAward = (ImageView) findViewById(R.id.btn_award);
        mAward.setOnClickListener(this);
        mAwardAnim = (AnimationDrawable) mAward.getBackground();

        mTreasure = (ImageView) findViewById(R.id.btn_treasure);
        mTreasure.setOnClickListener(this);
        mTreasureAnim = (AnimationDrawable) mTreasure.getBackground();

        mOnlineAvatr = (OnlineAvatar) findViewById(R.id.home_net_avatar);
        mOnlineAvatr.setSex(false);
        mOnlineAvatr.setClothes(1);
        mOnlineAvatr.setHair(1);
        mOnlineAvatr.setVisibility(View.INVISIBLE);

        mTabContent = findViewById(android.R.id.tabcontent);
        mOnlineHomeLayout = findViewById(R.id.online_home_layout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d("Demo", "Home-onStart");
        mOnlineAvatr.onStart();
        mOnlineHomeLayout.setBackgroundResource(R.drawable.bg_dance);
        mTabContent.setBackgroundResource(R.drawable.bg_online_tap);
        
        NoticeSend.getInstance().sendNotice(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mOnlineHomeLayout.setBackgroundResource(0);
        mTabContent.setBackgroundResource(0);
        mOnlineAvatr.onStop();
        LogUtil.d("Demo", "Home-onStop");
        System.gc();
//        if (SoundSwitch.isMusicOn()) {
//            stopBgMusic();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        addListener();
        doSend(new PersonCenterReq());
        doSend(new BeGetPrizeReq());
        
        String number = getTelephoneNumFromSystem(this);
        String IMEI = getTelephoneIMEIFromSystem(this);
        String UA = getTelephoneUAFromSystem(this);
        PhoneInfoReq req = new PhoneInfoReq();
        req.setMobilePhone(number == null ? "-1" : number);
        req.setImei(IMEI);
        req.setUa(UA);
        doSend(req);
        
        LogUtil.d("Demo", "Home-onResume");
//        if (SoundSwitch.isMusicOn()) {
//            playBgMusic();
//        }
    }
    
    private String getTelephoneNumFromSystem(Context context) {
		TelephonyManager telephoneManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephoneManager.getLine1Number() != null) {
			return telephoneManager.getLine1Number();
		} else {
			return null;
		}
	}

    private String getTelephoneIMEIFromSystem(Context context) {
		TelephonyManager telMg = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telMg.getDeviceId();
	}
    
    private String getTelephoneUAFromSystem(Context context){
		WebSettings settings = new WebView(context).getSettings();
		return settings.getUserAgentString();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.d("Demo", "Home-onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAnim();
        LogUtil.d("Demo", "Home-onPasue");
    }

    @Override
    public void onReceived(BaseMessage response) {
        super.onReceived(response);
        System.out.println(response.getName());
        if(response instanceof AnnouncementResp){
        	AnnouncementResp resp = (AnnouncementResp)response;
        	if(resp.getMessage() != null && resp.getMessage().length() > 0){
        		Intent i = new Intent(this, OnlineNoticeActivity.class);
                i.putExtra("notice", resp.getMessage());
                startActivity(i);
        	}
        } else if (response instanceof BeGetPrizeResp) {

            BeGetPrizeResp resp = (BeGetPrizeResp) response;

            BeGetPrizeInfo treasureInfo = resp.getBeGetPrizeInfo()[0];

            BeGetPrizeInfo awardInfo = resp.getBeGetPrizeInfo()[1];

            // 抽奖动画判断
            if (treasureInfo.flag == true && treasureInfo.type == 0) {

                mHandler.sendEmptyMessage(STARTTREASUREANIM);

            } else {
                mHandler.sendEmptyMessage(STOPTREASUREANIM);

            }
            // 连续登录领奖动画判断
            if (awardInfo.flag == true && awardInfo.type == 1) {

                mHandler.sendEmptyMessage(STARTAWARDANIM);

            } else {
                mHandler.sendEmptyMessage(STOPAWARDANIM);
            }
        } else if (response instanceof PersonCenterResp) {

            PersonCenterResp resp = (PersonCenterResp) response;

            fillData(resp);

        } else if (response instanceof LogoutUserResp) {

            LogoutUserResp resp = (LogoutUserResp) response;
            if (resp.isSuccess()) {

                mHandler.removeCallbacks(mLogout);

                finish();

            }
        } else if (response instanceof OpenShopResp) {

            Activity act = getLocalActivityManager().getActivity(
                    curTab);
            if (act instanceof OnlineStoreActivity && !curTab.equals("")) {

                OnlineStoreActivity act2 = (OnlineStoreActivity) getLocalActivityManager()
                        .getActivity(
                                curTab);

                act2.onReceived(response);

            }
        } else if (response instanceof ShowBagResp) {
            Activity act = getLocalActivityManager().getActivity(
                    curTab);
            if (act instanceof OnlineGoodsActivity && !curTab.equals("")) {

                OnlineGoodsActivity act2 = (OnlineGoodsActivity) getLocalActivityManager()
                        .getActivity(
                                curTab);

                act2.onReceived(response);
            }

        } else if (response instanceof SysCommonPush) {

            Activity act = getLocalActivityManager().getActivity(
                    curTab);
            if (act instanceof OnlineGoodsActivity && !curTab.equals("")) {

                OnlineGoodsActivity act2 = (OnlineGoodsActivity) getLocalActivityManager()
                        .getActivity(
                                curTab);

                act2.onReceived(response);
            }
        }
    }

    private void startGuestAnim() {

        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                if (mRegistAnim != null) {
                    mRegistAnim.start();
                }
            }
        }, 500);
    }

    private void checkAward() {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mAwardAnim != null) {

                    mAwardAnim.start();
                }
            }
        }, 500);
    }

    private void checkTreasure() {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mTreasureAnim != null) {

                    mTreasureAnim.start();
                }
            }
        }, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearAnim();
    }

    private void stopAnim() {
        if (mRegistAnim.isRunning())
            mRegistAnim.stop();

        if (mAwardAnim.isRunning())
            mAwardAnim.stop();

        if (mTreasureAnim.isRunning())
            mTreasureAnim.stop();
    }

    private void clearAnim() {
        stopAnim();

        mRegistAnim = null;
        mAwardAnim = null;
        mTreasureAnim = null;
    }

    private void fillData(PersonCenterResp resp) {

        mInfo.setNickName(resp.getNickName());

        mInfo.setCurExp(resp.getCurExperience());

        mInfo.setNextExp(resp.getNextExperience());

        mInfo.setLevel(resp.getLevel());

        mInfo.setMale(resp.getSex() == 0 ? true : false);

        mInfo.setGest(resp.isGuest());

        String temp = resp.getAvatar();

        String[] temps = temp.split(";");

        if (temps[0].substring(0, 1).equals("h")) {

            mInfo.setHairId(Integer.valueOf(temps[0].substring(1, temps[0].length())));

        }
        if (temps[1].substring(0, 1).equals("c")) {

            mInfo.setClothesId(Integer.valueOf(temps[1].substring(1, temps[1].length())));

        }

        if (mInfo.isGest()) {
            mRegist.setVisibility(View.VISIBLE);
            startGuestAnim();
        } else {
            mRegist.setVisibility(View.INVISIBLE);
        }

        mOnlineAvatr.setSex(mInfo.isMale());
        mOnlineAvatr.setHair(mInfo.getHairId());
        mOnlineAvatr.setClothes(mInfo.getClothesId());
        mOnlineAvatr.setVisibility(View.VISIBLE);

        mName.setText(mInfo.getNickName());
        int level = mInfo.getLevel();

        // end
        if (level >= 0 && level < 100) {
            int tenNumIdx = level / 10;
            if (tenNumIdx > LEVEL_IMAGE_IDS.length - 1) {
                tenNumIdx = LEVEL_IMAGE_IDS.length - 1;
            }
            if (tenNumIdx > 0) {
                mLevelTenNum.setBackgroundResource(LEVEL_IMAGE_IDS[tenNumIdx]);
                mLevelTenNum.setVisibility(View.VISIBLE);
            }

            int bitNumIdx = level % 10;
            mLevelBitNum.setBackgroundResource(LEVEL_IMAGE_IDS[bitNumIdx]);
        } else {
            ToastUtil.longShow("level=" + level);
        }

        int nextExp = mInfo.getNextExp();

        int currentExp = mInfo.getCurExp();

        mLevelBar.setMax(nextExp);
        mLevelBar.setProgress(currentExp);

        if (nextExp >= 0 && currentExp >= 0) {
            mLevelBar.setText("EXP: " + currentExp + "/" + nextExp);
        }

        mGold.setText(String.valueOf(resp.getMoneyGold()));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_online_regist: {

                mRegist.setVisibility(View.INVISIBLE);

                Intent i = new Intent(this, OnlineRegisterActivity.class);

                startActivityForResult(i, Constants.RESULT_FOR_ONLINE_REGISTER_ACTIVITY);
            }
                break;
            case R.id.btn_online_back: {
                doBack();
            }
                break;
            case R.id.btn_selfinfo: {

                mBtnSelfInfo.setVisibility(View.INVISIBLE);
                
                Intent i = new Intent(this, OnlineIndividualActivity.class);

                startActivityForResult(i, Constants.RESULT_FOR_ONLINE_INDIVIDUAL_ACTIVITY);
            }
                break;
            case R.id.btn_pay:{
            	mBtnSelfInfo.setVisibility(View.INVISIBLE);

                Intent i = new Intent(this, OnlineIndividualActivity.class);
                i.putExtra("pay", true);
                startActivityForResult(i, Constants.RESULT_FOR_ONLINE_INDIVIDUAL_ACTIVITY);
            }
            	break;
            case R.id.btn_award: {
                if (hasAward) {

                    Intent intent = new Intent(OnlineHomeActivity.this,
                            OnlinePrizeActivity.class);
                    startActivityForResult(intent,
                            Constants.RESULT_FOR_MYTURNTABLE_SURFACEVIEW_ACTIVITY);

                    mAwardAnim.stop();

                    mAward.setVisibility(View.INVISIBLE);

                }
            }
                break;
            case R.id.btn_treasure: {

                if (Constants.DEBUG_OFFLINE_TURNTABLE_SKIP_NETWORK) {

                    Intent intent = new Intent(OnlineHomeActivity.this,
                            MyTurnTableSurfaceViewActivity.class);

                    startActivityForResult(intent,
                            Constants.RESULT_FOR_MYTURNTABLE_SURFACEVIEW_ACTIVITY);

                } else {

                    if (!hasTreasure) {

                        ToastUtil.show("你今天抽过奖了！");

                    } else {
                        Intent intent = new Intent(OnlineHomeActivity.this,
                                MyTurnTableSurfaceViewActivity.class);

                        intent.putExtra("hasTreasure", hasTreasure);

                        startActivityForResult(intent,
                                Constants.RESULT_FOR_MYTURNTABLE_SURFACEVIEW_ACTIVITY);

                        mTreasureAnim.stop();

                        mTreasure.setVisibility(View.INVISIBLE);

                    }
                }
            }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LogUtil.d("Demo", "requestCode--->" + requestCode + "||" + "resultCode--->" + resultCode);

        switch (requestCode) {
            case Constants.RESULT_FOR_MYTURNTABLE_SURFACEVIEW_ACTIVITY:

                if (resultCode == -1) {

                    mTreasure.setVisibility(View.VISIBLE);

                }

                break;

            case Constants.RESULT_FOR_ONLINE_PRIZE_ACTIVITY:

                if (resultCode == -1) {

                    mAward.setVisibility(View.VISIBLE);

                }

                break;

            case Constants.RESULT_FOR_ONLINE_INDIVIDUAL_ACTIVITY:

                if (resultCode == -1) {

                    mBtnSelfInfo.setVisibility(View.VISIBLE);

                }

                break;

            case Constants.RESULT_FOR_ONLINE_REGISTER_ACTIVITY:

                if (resultCode == -1) {

                    mRegist.setVisibility(View.VISIBLE);

                }

                break;

            default:
                break;
        }
    }

    private void initTab() {
        Resources res = getResources();
        // TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
        // tabHost.setup(mlam);

        tabHost = getTabHost();
        Intent intent;

        intent = new Intent().setClass(this, OnlineGameListActivity.class);
        spec = tabHost.newTabSpec("game")
                .setIndicator(createTabView(res.getDrawable(
                        R.drawable.ic_tab_online_game_list)))
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, OnlineGoodsActivity.class);
        spec = tabHost.newTabSpec("goods")
                .setIndicator(createTabView(res.getDrawable(
                        R.drawable.ic_tab_online_goods)))
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, OnlineTopActivity.class);
        spec = tabHost.newTabSpec("top")
                .setIndicator(createTabView(res.getDrawable(
                        R.drawable.ic_tab_online_top)))
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, OnlineStoreActivity.class);
        spec = tabHost.newTabSpec("store")
                .setIndicator(createTabView(res.getDrawable(
                        R.drawable.ic_tab_online_store)))
                .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setOnTabChangedListener(new OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {

                curTab = tabId;

                // doSend(new PersonCenterReq());

                
                if(tabId.equals("top")){
                	doSend(new RankListReq());
                } 
                // 当点击物品时候，刷新物品列表
                else if (tabId.equals("goods")) {

                    OnlineGoodsActivity.clickClothes = -1;

                    OnlineGoodsActivity.clickHead = -1;

                    doSend(new ShowBagReq());
                }
                // 当离开商城时，还原人物原有模样
                else if (tabId.equals("store")) {

                    doSend(new OpenShopReq());

                }
                if (mInfo.getHairId() != -1 && mInfo.getClothesId() != -1) {

                    setAvartarSex(mInfo.isMale());

                    setAvartarHair(mInfo.getHairId());

                    setAvartarClothes(mInfo.getClothesId());

                }
            }
        });

        tabHost.setCurrentTab(0);

    }

    private View createTabView(final Drawable drawable) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_online_indicator, null);
        ImageView iv = (ImageView) view.findViewById(R.id.tab_online_icon);
        iv.setImageDrawable(drawable);
        return view;

    }

    public void changeAvatar(int hairId, int clothesId) {
        setAvartarHair(hairId);
        setAvartarClothes(clothesId);
    }

    public void setAvartarClothes(int clothesId) {
        mOnlineAvatr.setClothes(clothesId);
    }

    public void setAvartarHair(int hairId) {
        mOnlineAvatr.setHair(hairId);
    }

    public void setAvartarSex(boolean male) {
        mOnlineAvatr.setSex(male);
    }

    private void doBack() {
        NoticeSend.getInstance().reset();
        doSend(new LogoutUserReq());
        finish();
    }

    protected boolean onBack() {
        doBack();
        return true;
    }

    private Runnable mLogout = new Runnable() {

        @Override
        public void run() {

            finish();

        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (event.getRepeatCount() == 0) {
                    if (onBack()) {

                        return true;
                    }
                }
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

}

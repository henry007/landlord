
package com.hurray.landlord.activities;

import com.hurray.landlord.Constants;
import com.hurray.landlord.R;
import com.hurray.landlord.entity.MyPreferrences.AccountPreferrence;
import com.hurray.landlord.sdk.SdkManagerJuZi;
import com.hurray.landlord.server.ServerConstants;
import com.hurray.landlord.services.AppUpdateService;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.ManifestUtil;
import com.hurray.landlord.utils.PathUtil;
import com.hurray.landlord.utils.ResAvatarUtil;
import com.hurray.landlord.utils.SdcardUtil;
import com.hurray.landlord.utils.SoundSwitch;
import com.hurray.landlord.utils.ToastUtil;
import com.hurray.landlord.utils.ZipUtil;
import com.hurray.landlord.view.SoundPanel;
import com.hurray.landlord.view.StartMenuView;
import com.hurray.landlord.view.StartMenuView.OnStartMenuListener;
import com.hurray.lordserver.protocol.message.account.GuestLoginReq;
import com.hurray.lordserver.protocol.message.account.GuestLoginResp;
import com.hurray.lordserver.protocol.message.account.PasswordLoginReq;
import com.hurray.lordserver.protocol.message.account.PasswordLoginResp;
import com.hurray.lordserver.protocol.message.base.BaseMessage;
import com.hurray.lordserver.protocol.message.base.CheckVersionReq;
import com.hurray.lordserver.protocol.message.base.CheckVersionResp;
import com.hurray.lordserver.protocol.message.base.Response;
import com.hurray.lordserver.protocol.message.user.LogoutUserReq;
import com.mobclick.android.MobclickAgent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipException;

public class StartActivity extends BaseNetActivity implements OnStartMenuListener {

    private static final String TAG = "StartActivity";

    public static final int DIALOG_ABOUT = 1;

    public static final int DIALOG_HELP = 2;

    public static final int DIALOG_EXIT = 3;

    private static final int AVATAR_RES_BYTES = 1000000;

    private static final int TIME_OUT_SECS = 20000;

    private StartMenuView mStartMenu;

    // private ImageView mLightOne;

    // private ImageView mLightTwo;

    private ImageView mLightThree;

    private ImageView mLightFourth;

    // private ImageView mLightFive;

    // private ImageView mLightSix;

    // private ImageView mLeftBeauty;

    // private ImageView mRightBeauty;

    private View mLeftBeautyBg;

    private View mRightBeautyBg;

    // private AnimationDrawable leftAnimation;
    //
    // private AnimationDrawable rightAnimation;

    private View mRootLayout;

    private ImageView mImageTitle;

    private ImageView mImageCenterBeauty;

    private ProgressBar mProgressBar;

    private PhoneStateListener mPhoneStateListener;

    private Handler mHandler = new Handler();

    private boolean mAllowLoginOnlineGame = false;

    private boolean mMenuEnable = true;

    private Runnable mRunTimeOut = new Runnable() {

        @Override
        public void run() {

            synchronized (this) {
                mProgressBar.setVisibility(View.GONE);
                mAllowLoginOnlineGame = true;
                ToastUtil.show("请求超时，请检查网络");

                mMenuEnable = true;
            }
        }
    };

    private static final int START_ACTIVITY_DELAY = 300;

    private Runnable mRunStartOnlineHome = new Runnable() {
        @Override
        public void run() {
            startOnlineHome();
        }
    };

    private Runnable mRunStartOfflineHome = new Runnable() {

        @Override
        public void run() {
            startOfflineHome();
        }
    };

    private SoundPanel mSoundPanel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.start_activity);
        
        mSoundPanel = (SoundPanel) findViewById(R.id.sound_panel);
        
        showDebugExtraInfo();

        startListenTelephony();

        MobclickAgent.onError(this);
        MobclickAgent.setDebugMode(Constants.DEBUG);

        mRootLayout = findViewById(R.id.root_layout);
        mImageTitle = (ImageView) findViewById(R.id.iv_title);
        mImageCenterBeauty = (ImageView) findViewById(R.id.iv_center_beauty);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mStartMenu = (StartMenuView) findViewById(R.id.start_menu);
        mStartMenu.setOnStartMenuListener(this);

        initLightImageView();

        initBeautyView();

        playBgMusic(R.raw.bgm_soft);

        if (checkSdCard()) {
            bindAppUpdateService();
        } else {
            showSdcardAlertDialog();
        }

        if (SdkManagerJuZi.power) {
			SdkManagerJuZi.getInstance().setContext(this);
		}
    }

    private void showDebugExtraInfo() {
        TextView server = (TextView) findViewById(R.id.server);
        if (Constants.DEBUG_EXTRA_INFO) {
            String info = "";
            if (Constants.IS_61_MATCH_VER) {
                info += "六一比赛\n";
            }
            server.setText(info + "SRV:" + ServerConstants.HTTP_HOST + "\nVER:"
                    + ManifestUtil.getVersionName());
        } else {
            server.setVisibility(View.GONE);
        }
    }

    private void showSdcardAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        doExit();
                    }
                })
                .setNegativeButton("继续", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAllowLoginOnlineGame = true;
                        dialog.dismiss();
                    }
                })
                .setMessage(R.string.is_sdcard)
                .setOnKeyListener(new DialogInterface.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (KeyEvent.KEYCODE_BACK == keyCode && event.getRepeatCount() == 0) {

                            return true;
                        }
                        return false;
                    }
                });

        dialog.show();
    }

    private void initBeautyView() {
        mLeftBeautyBg = findViewById(R.id.left_beauty_bg);
        mRightBeautyBg = findViewById(R.id.right_beauty_bg);
    }

    private void initLightImageView() {
        mLightThree = (ImageView) findViewById(R.id.iv_light_three);
        mLightFourth = (ImageView) findViewById(R.id.iv_light_fourth);
    }

    private void startListenTelephony() {

        mPhoneStateListener = new PhoneStateListener() {

            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);

                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE: {
                        Log.d(TAG, "CALL_STATE_IDLE");
                        playBgMusic();
                        break;
                    }
                    case TelephonyManager.CALL_STATE_OFFHOOK: {
                        Log.d(TAG, "CALL_STATE_OFFHOOK");
                        stopBgMusic();
                        break;
                    }
                    case TelephonyManager.CALL_STATE_RINGING: {
                        Log.d(TAG, "CALL_STATE_RINGING");
                        stopBgMusic();
                        break;
                    }
                    default:
                        break;
                }
            }

        };

        TelephonyManager telephonyMgr = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyMgr.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void stopListenTelephony() {
        TelephonyManager telephonyMgr = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyMgr.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);

        mPhoneStateListener = null;
    }

    private boolean checkSdCard() {
        if (SdcardUtil.getSdcardAvailableSize() > 0)
            return true;
        else
            return false;
    }

    private boolean unzipFromAssetsToAvatar(String assetsfile) {
        boolean success = false;
        try {
            AssetManager am = getAssets();
            InputStream is = am.open(assetsfile);
            File f = new File(PathUtil.getZipPath() + File.separator + assetsfile);
            LogUtil.d(TAG, "f=" + f.getPath());
            f.createNewFile();
            ResAvatarUtil.inputstreamTofile(is, f);

            ZipUtil.upZipFile(f, PathUtil.getResAvatarPath());
            success = true;

        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return success;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSoundPanel.updateView();
        addListener();
        if(SoundSwitch.isMusicOn()){
            playBgMusic();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mRootLayout.setBackgroundResource(R.drawable.bg_dance);
        mImageCenterBeauty.setImageResource(R.drawable.center_beauty);
        mImageTitle.setImageResource(R.drawable.title);

        onStartLightAnimation();

        onStartBeautyAnim();
    }

    private void onStartBeautyAnim() {
        mLeftBeautyBg.setBackgroundResource(R.drawable.left_beauty1);
        mRightBeautyBg.setBackgroundResource(R.drawable.right_beauty1);
    }

    private void onStartLightAnimation() {
        mLightThree.setImageResource(R.drawable.light_3);
        mLightFourth.setImageResource(R.drawable.light_4);

        RotateAnimation animationThree = new RotateAnimation(90, -90, 0, 0);
        animationThree.setDuration(4500);
        animationThree.setRepeatCount(Animation.INFINITE);
        animationThree.setRepeatMode(Animation.REVERSE);
        mLightThree.setAnimation(animationThree);
        animationThree.start();

        RotateAnimation animationFourth = new RotateAnimation(-90, 90, 341, -60);
        animationFourth.setDuration(4000);
        animationFourth.setRepeatCount(Animation.INFINITE);
        animationFourth.setRepeatMode(Animation.REVERSE);
        mLightFourth.setAnimation(animationFourth);
        animationFourth.start();

    }

    @Override
    protected void onStop() {
        super.onStop();
        onStopBeautyAnim();

        onStopLightAnimation();

        mRootLayout.setBackgroundResource(0);
        mImageCenterBeauty.setImageResource(0);
        mImageTitle.setImageResource(0);

        System.gc();
    }

    private void onStopBeautyAnim() {
        mLeftBeautyBg.setBackgroundResource(0);
        mRightBeautyBg.setBackgroundResource(0);
    }

    private void onStopLightAnimation() {
        mLightThree.clearAnimation();
        mLightFourth.clearAnimation();
        mLightThree.setImageResource(0);
        mLightFourth.setImageResource(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    	if (SdkManagerJuZi.power) {
			SdkManagerJuZi.getInstance().onDestroy();
		}
		LogUtil.d("Demo", "StartActivity----->>onDestroy!!!");
        stopListenTelephony();
        stopBgMusic();
        stopMessageServer();
        unbindAppUpdateService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d("Demo", "onPause!!");
        mMenuEnable = true;
        removeListener();
//        if(SoundSwitch.isMusicOn()){
//            stopBgMusic();
//         }
    }

    @Override
    public void onMenuItemSelected(int index) {
        LogUtil.d(TAG, "index=" + index);

        synchronized (this) {
            if (!mMenuEnable) {
                return;
            }

            switch (index) {
                case StartMenuView.MENU_LOCAL: {
                    safeStartOfflineHome();
                }
                    break;
                case StartMenuView.MENU_NET: {
                    if (mAllowLoginOnlineGame) {
                        if (Constants.DEBUG_ONLINE_UI_SKIP_NETWORK) {
                            safeStartOnlineHome();
                        } else {
                            mMenuEnable = false;
                            doLogin();
                        }
                    } else {
                        ToastUtil.show("请稍后重试...");
                    }
                }
                    break;
                case StartMenuView.MENU_EXIT: {
                    doExit();
                }
                    break;
                case StartMenuView.MENU_HELP: {
                    safeShowDialog(DIALOG_HELP);
                }
                    break;
                case StartMenuView.MENU_ABOUT: {
//                    safeShowDialog(DIALOG_ABOUT);
                }
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void onReceived(BaseMessage msg) {

        if ((msg instanceof PasswordLoginResp || msg instanceof GuestLoginResp)) {

            synchronized (this) {
                
                mProgressBar.setVisibility(View.GONE);
                mHandler.removeCallbacks(mRunTimeOut);

                Response resp = (Response) msg;
                String resultDesc = resp.getResultDesc();
                if (resultDesc != null && resultDesc.length() > 0) {
                    ToastUtil.show(resultDesc);
                }
                if (resp.isSucceeded()) {
                    // lhx add for 补充baseRegisterResp by 2012-07-30 start

                    if (resp.getResultCode() == Response.RESULT_NETWORK_ERROR) {
                        ToastUtil.show("服务器繁忙");

                        return;
                    }

                    if (msg instanceof GuestLoginResp) {
                        AccountPreferrence apf = AccountPreferrence.getSingleton();
                        GuestLoginResp guestLoginResp = (GuestLoginResp) msg;

                        if (guestLoginResp.getResultCode() == Response.RESULT_NETWORK_ERROR) {
                            ToastUtil.show("服务器繁忙");

                            return;
                        }

                        apf.setNickName(guestLoginResp.getNickName());
                        apf.setLevel(1);
                        apf.setNextExp(guestLoginResp.getNextExp());
                        handleAvartarString(apf, guestLoginResp);
                        apf.setSessionId(guestLoginResp.getSessionId());
                        apf.setGuest(guestLoginResp.isGuest());
                        apf.setUserId(guestLoginResp.getUid());
                        apf.setEmail(guestLoginResp.getEmail());
                        apf.setPassword(guestLoginResp.getPassword());
                        apf.setMale(guestLoginResp.getSex() == 0 ? true : false);

                    }
                } else {
                    AccountPreferrence apf = AccountPreferrence.getSingleton();

                    apf.getEditor(Constants.PREFS_ACCOUNT).clear().commit();
                }
                mMenuEnable = true;
                safeStartOnlineHome();
            }

        } else if (msg instanceof CheckVersionResp) {
            mProgressBar.setVisibility(View.GONE);
            mHandler.removeCallbacks(mRunTimeOut);

            CheckVersionResp r = (CheckVersionResp) msg;
            if (r.isSucceeded()) {
                if (r.getType() == 0) {
                    mIsHasAppUpdate = r.isHasUpdate();
                    mIsForceAppUpdate = r.isForceUpdate();
                    mAppUpdateDesc = r.getInfo();
                    mAppUpdateUrl = r.getDownloadUrl();

                    LogUtil.d(TAG, "mIsHasAppUpdate=" + mIsHasAppUpdate);
                    LogUtil.d(TAG, "mIsForceAppUpdate=" + mIsForceAppUpdate);
                    LogUtil.d(TAG, "mAppUpdateDesc=" + mAppUpdateDesc);
                    LogUtil.d(TAG, "mAppUpdateUrl=" + mAppUpdateUrl);
                } else {
                    mIsHasAppUpdate = false;
                    mIsForceAppUpdate = false;
                    mAppUpdateDesc = null;
                    mAppUpdateUrl = null;

                    LogUtil.d(TAG, "服务器升级配置错误！！！");
                }

                showAppUpdateDialog();
            } else { // 万一检查失败，允许登录
                mAllowLoginOnlineGame = true;
            }
        }
    }

    private void handleAvartarString(AccountPreferrence apf, GuestLoginResp guestLoginResp) {
        String temp = guestLoginResp.getAvatar();

        String[] temps = temp.split(";");

        if (temps[0].length() > 0) {

            if (temps[0].substring(0, 1).equals("h")) {

                String arg1 = temps[0].trim().toString();

                apf.setHairId(Integer.valueOf(temps[0].trim().substring(1, arg1.length())));

            }
            if (temps[1].substring(0, 1).equals("c")) {

                String arg2 = temps[1].trim().toString();

                apf.setClothId(Integer.valueOf(temps[1].trim().substring(1, arg2.length())));

            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {
            case DIALOG_ABOUT: {
                final Dialog dialog = new Dialog(StartActivity.this, R.style.dialog);

                View view = LayoutInflater.from(StartActivity.this).inflate(
                        R.layout.about,
                        null);

                TextView text = (TextView) view.findViewById(R.id.text_about);
                String versionName = ManifestUtil.getVersionName();
                text.setText(getString(R.string.about, versionName));

                dialog.setContentView(view);
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                dialogWindow.setGravity(Gravity.CENTER);

                WindowManager m = getWindowManager();
                Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
                lp.width = (int) (d.getWidth() * 0.55); // 宽度
                dialogWindow.setAttributes(lp);

                ImageButton btnBack = (ImageButton) view.findViewById(R.id.btn_back);

                btnBack.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();

                    }
                });
                return dialog;

            }

            case DIALOG_HELP: {
                final Dialog dialog = new Dialog(StartActivity.this, R.style.dialog);

                View view = LayoutInflater.from(StartActivity.this).inflate(
                        R.layout.help,
                        null);

                dialog.setContentView(view);
                // dialog.show();

                ImageButton btnOk = (ImageButton) view.findViewById(R.id.btn_ok);

                btnOk.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();

                    }
                });

                return dialog;
            }

            default:
                break;
        }
        return null;
    }

    private void doLogin() {
        mProgressBar.setVisibility(View.VISIBLE);
        mHandler.postDelayed(mRunTimeOut, TIME_OUT_SECS);

        AccountPreferrence apf = AccountPreferrence.getSingleton();

        String email = apf.getEmail(null);
        String password = apf.getPassword(null);

        LogUtil.d("Demo", "StartActivity-->" +
                apf.getNickName("") + "||" + apf.getPassword("") + "||" + apf.getEmail("")
                + "||" + apf.isMale(false));

        BaseMessage logingReq;
        if (null == email || email.length() <= 0
                || null == password || password.length() <= 0) {
            logingReq = new GuestLoginReq();
            
            ApplicationInfo ai;
            try {
                ai = getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
                Bundle bundle = ai.metaData;
                String channel = bundle.getString("CHANNEL");
               ((GuestLoginReq)logingReq).setChannelId(Integer.valueOf(channel.substring(1)));
               LogUtil.d("Demo", "channel:"+channel);
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            logingReq = new PasswordLoginReq();
            ((PasswordLoginReq) logingReq).setEmail(email);
            ((PasswordLoginReq) logingReq).setPassword(password);
        }
        
        doSend(logingReq);
    }

    private void doExit() {
        this.finish();
    }

    @Override
    protected boolean onBack() {
        doExit();
        return true;
    }

    // App升级

    private AppUpdateService mService;

    private String mAppUpdateUrl;

    private String mAppUpdateDesc;

    private boolean mIsHasAppUpdate = false;

    private boolean mIsForceAppUpdate = false;

    private boolean mIsBound = false;

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((AppUpdateService.AppUpdateBinder) service).getService();
            Log.d(TAG, "onServiceConnected mService is null?" + (mService == null));

            if (mService != null) {
                checkAppUpdate();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            Log.d(TAG, "onServiceDisconnected");
        }

    };

    private void bindAppUpdateService() {
        if (Constants.APP_UPDATE_ON) {
            if (!mIsBound) {
                Intent i = new Intent(StartActivity.this, AppUpdateService.class);
                bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
                mIsBound = true;
            }
        } else {
            ToastUtil.longShow("此版本，未开启升级功能！");
            mAllowLoginOnlineGame = true;
        }
    }

    private void unbindAppUpdateService() {
        if (mIsBound) {
            unbindService(mServiceConnection);
            mIsBound = false;
        }
    }

    private void checkAppUpdate() {
        mProgressBar.setVisibility(View.VISIBLE);
        mHandler.postDelayed(mRunTimeOut, TIME_OUT_SECS);

        String channelId = ManifestUtil.getChannelId();
        String versionName = "v" + ManifestUtil.getVersionName();

        LogUtil.d(TAG, "channelId=" + channelId);
        LogUtil.d(TAG, "versionName=" + versionName);

        CheckVersionReq req = new CheckVersionReq();
        req.setChannel(channelId);
        req.setVersionCode(versionName);
        doSend(req);
    }

    private void showAppUpdateDialog() {
        if (mIsHasAppUpdate) {
            if (mIsForceAppUpdate) {
                showForceAppUpdateDialog();
            } else {
                showSuggestAppUpdateDialog();
            }
        } else {
            mAllowLoginOnlineGame = true;
        }
    }

    private void showSuggestAppUpdateDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setPositiveButton("升级", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startAppUpdate();
                        mAllowLoginOnlineGame = true;
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mAllowLoginOnlineGame = true;
                    }
                })
                .setMessage(mAppUpdateDesc)
                .setOnKeyListener(new DialogInterface.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (KeyEvent.KEYCODE_BACK == keyCode && event.getRepeatCount() == 0) {
                            return true;
                        }
                        return false;
                    }
                });

        dialog.show();
    }

    private void showForceAppUpdateDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setPositiveButton("升级", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startAppUpdate();
                        doExit();
                    }
                })
                .setNegativeButton("退出", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        doExit();
                    }
                })
                .setMessage(mAppUpdateDesc)
                .setOnKeyListener(new DialogInterface.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (KeyEvent.KEYCODE_BACK == keyCode && event.getRepeatCount() == 0) {
                            return true;
                        }
                        return false;
                    }
                });

        dialog.show();
    }

    private void startAppUpdate() {
        if (mService != null) {
            Log.d(TAG, "is running = " + mService.isRunning());
            if (!mService.isRunning()) {
                Intent i = new Intent(StartActivity.this, AppUpdateService.class);
                i.putExtra(AppUpdateService.APP_UPDATE_URL, mAppUpdateUrl);
                startService(i);
            } else {
                ToastUtil.show("升级已在运行");
            }
        }

        unbindAppUpdateService();
        mService = null;
    }

    private void startOnlineHome() {
        Intent i = new Intent(this, OnlineHomeActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade, R.anim.hold);
        LogUtil.d(TAG,
                "----------------------------------startOnlineHome-----------------------------------");
    }

    private void startOfflineHome() {
        Intent i = new Intent(this, OfflineHomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        overridePendingTransition(R.anim.fade, R.anim.hold);
    }

    // 250ms内的操作，忽略为1个
    private void safeStartOnlineHome() {
        mHandler.removeCallbacks(mRunStartOfflineHome);
        mHandler.removeCallbacks(mRunStartOnlineHome);
        mHandler.postDelayed(mRunStartOnlineHome, START_ACTIVITY_DELAY);
    }

    // 250ms内的操作，忽略为1个
    private void safeStartOfflineHome() {
        mHandler.removeCallbacks(mRunStartOfflineHome);
        mHandler.removeCallbacks(mRunStartOnlineHome);
        mHandler.postDelayed(mRunStartOfflineHome, START_ACTIVITY_DELAY);
    }
}

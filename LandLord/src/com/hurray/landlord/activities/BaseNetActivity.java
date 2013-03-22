
package com.hurray.landlord.activities;

import com.hurray.landlord.Constants;
import com.hurray.landlord.R;
import com.hurray.landlord.entity.MyPreferrences.AccountPreferrence;
import com.hurray.landlord.game.online.OnlineMessageAgent;
import com.hurray.landlord.game.online.OnlineMessageListener;
import com.hurray.landlord.server.MessageListener;
import com.hurray.landlord.server.MessageServerService;
import com.hurray.landlord.server.MessageServerWrapper;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.ToastUtil;
import com.hurray.lordserver.protocol.message.MessageFormatException;
import com.hurray.lordserver.protocol.message.MessageList;
import com.hurray.lordserver.protocol.message.SysCommonPush;
import com.hurray.lordserver.protocol.message.account.BaseRegisterResp;
import com.hurray.lordserver.protocol.message.account.GuestLoginReq;
import com.hurray.lordserver.protocol.message.account.PasswordLoginReq;
import com.hurray.lordserver.protocol.message.account.PasswordLoginResp;
import com.hurray.lordserver.protocol.message.account.PersonCenterResp;
import com.hurray.lordserver.protocol.message.account.RegisterUserReq;
import com.hurray.lordserver.protocol.message.base.BaseMessage;
import com.hurray.lordserver.protocol.message.base.NotificationPush;
import com.hurray.lordserver.protocol.message.base.Response;
import com.hurray.lordserver.protocol.message.error.AuthErrorResp;
import com.hurray.lordserver.protocol.message.error.ServerErrorResponse;
import com.hurray.lordserver.protocol.message.prize.ConLoginPrizePush;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.LinkedList;

abstract public class BaseNetActivity extends BaseActivity implements
        OnlineMessageAgent, OnlineMessageListener {

    private static final String TAG = "BaseNetActivity";

    public static boolean sHandleArthError = false;

    public static int sErrorCount = 0;

    private boolean mIsBound = false;

    private boolean mIsConnected = false;

    private boolean mIsListening = false;

    private String mSessionId;

    private MessageServerWrapper mService;

    protected MessageServiceConnection mConnection;

    private OnlineMessageListener mOnlineMessageListener;

    private LinkedList<BaseMessage> mSendQueue = new LinkedList<BaseMessage>();

    private MessageListener mListener = new MessageListener() {
        @Override
        public void onReceive(MessageList msgList) {
            LogUtil.d(TAG, BaseNetActivity.this + " onReceive " + msgList);

            for (final BaseMessage baseMessage : msgList) {
                if (!onPreReceived(baseMessage)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onReceived(baseMessage);
                        }
                    });
                }
            }

        }

        @Override
        public void onSentResult(boolean status, int[] holder) {
            LogUtil.d(TAG, " onSentResult status=" + status + " holder.length="
                    + holder.length);

        }
    };

    private class MessageServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            LogUtil.d(TAG, " onServiceConnected() connected");

            mService = new MessageServerWrapper(boundService);
            try {
                doAddListener();

                mIsConnected = true;
                sendMessageInQueue();
                if (!onPreConnected()) {
                    onConnected();
                }
            } catch (RemoteException e) {
                e.printStackTrace();

                if (!onPreConnectError()) {
                    onConnectError();
                }
            }
        }

        // Service与App同进程，永远不会被调用
        public void onServiceDisconnected(ComponentName name) {
            doPostDisconnect();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBindService();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // doBindService();

    }

    @Override
    protected void onStop() {
        super.onStop();
        // setBeat(5000);
        //
        // doUnbindService();

    }

    protected void stopMessageServer() {
        try {
            if (mService != null)
                mService.stop();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setBeat(long millisec) {
        try {
            if (mService != null)
                mService.setFrequency(millisec);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        setBeat(5000);

        doUnbindService();
    }

    protected void addListener() {
        try {
            doAddListener();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    protected void removeListener() {
        try {
            doRemoveListener();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void doAddListener() throws RemoteException {
        synchronized (mListener) {
            if (!mIsListening) {
                if (mService != null) {
                    mService.addListener(mListener);
                    mIsListening = true;
                }
            }
        }
    }

    private void doRemoveListener() throws RemoteException {
        synchronized (mListener) {
            if (mIsListening) {
                if (mService != null) {
                    mService.removeListener(mListener);
                    mIsListening = false;
                }
            }
        }
    }

    @Override
    public void doSend(BaseMessage msg) {
        synchronized (mSendQueue) {
            mSendQueue.add(msg);
            LogUtil.d(TAG, this + " doSend msg: " + msg.toJSONString());
        }

        if (mIsConnected) {
            sendMessageInQueue();
        }
    }

    @Override
    public void setOnlineMessageListener(OnlineMessageListener l) {
        mOnlineMessageListener = l;
    }

    @Override
    public void onReceived(BaseMessage response) {

    }

    @Override
    public void onSent(boolean status, BaseMessage msg) {

    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onConnectError() {

    }

    @Override
    public void onDisconnected() {

    }

    /**
     * @return true handled message, false unHandled message
     */
    private boolean onPreReceived(BaseMessage response) {
        if (response == null) {
            LogUtil.w(TAG, "onPreReceived null BaseMessage");
            return true;
        }

        // 通用错误提示
        Response res = (Response) response;
        if (!res.isSucceeded()) {
            String desc = res.getResultDesc();
            if (desc != null && desc.length() > 0) {
                ToastUtil.longShow(res.getResultDesc());
            }
            return false;
        }
        else if (response instanceof PasswordLoginResp) {
            PasswordLoginResp r = (PasswordLoginResp) response;
            AccountPreferrence apf = AccountPreferrence.getSingleton();

            apf.setSessionId(r.getSessionId());
            apf.setGuest(r.isGuest());

        } else if (response instanceof PersonCenterResp) {

            PersonCenterResp r = (PersonCenterResp) response;

            AccountPreferrence apf = AccountPreferrence.getSingleton();

            apf.setUserId(r.getUid());

            apf.setLevel(r.getLevel());

            apf.setCurrentExp(r.getCurExperience());

            apf.setNextExp(r.getNextExperience());

            apf.setNickName(r.getNickName());
            apf.setMale((r.getSex() == RegisterUserReq.SEX_MALE));
            apf.isGuest(r.isGuest());

            String temp = r.getAvatar();

            String[] avartars = temp
                    .split(com.hurray.lordserver.protocol.message.Constants.FIRST_SPLITE);

            for (String avartar : avartars) {
                String avartarType = avartar.substring(0, 1);
                String avartarId = avartar.substring(1, avartars.length);
                saveAvartarState(apf, avartarType, avartarId);
            }

        }
        else if (response instanceof ConLoginPrizePush) {

            ConLoginPrizePush resp = (ConLoginPrizePush) response;

            AccountPreferrence apf = AccountPreferrence.getSingleton();

            apf.setAwardPopFlag(true);

            apf.setAwardDesc(resp.getPrizeDesc());

            apf.setMoneyGoldVal(resp.getMoneyGold());

            apf.setMoneyHeartVal(resp.getMoneyHeart());

        }
        else if (response instanceof AuthErrorResp) {

            AuthErrorResp r = (AuthErrorResp) response;

            AccountPreferrence apf = AccountPreferrence.getSingleton();

            mSessionId = null;

            if (r.isCleanAccount()) {

                apf.getEditor(Constants.PREFS_ACCOUNT).clear();

            } else {
                apf.getEditor(Constants.PREFS_ACCOUNT).remove(Constants.SESSION_ID);
            }

            // lhx add for back键重新回到密码找回界面 by 2012-6-21
            if (!(BaseNetActivity.this instanceof StartActivity)) {

                if (BaseNetActivity.this instanceof OnlineRegisterActivity) {

                    LogUtil.d("Demo", "BaseNetActivity--->finish");

                }

                this.finish();

            }

            return true;
        }
        else if (response instanceof ServerErrorResponse) {
            ServerErrorResponse r = (ServerErrorResponse) response;

            ToastUtil.longShow(R.string.server_error);

            LogUtil.e(TAG, "ServerErrorResponse TimeStamp: " + r.getTimeStamp().toString());
            LogUtil.e(TAG, "ServerErrorResponse StackTrace: " + r.getStackTrace());
            return true;
        } else if (response instanceof NotificationPush) {
            NotificationPush r = (NotificationPush) response;
            String str = r.getMsg();
            if (str != null && str.length() > 0) {
                ToastUtil.longShowTop(str);
            }
            return true;
        } else {
            if (mOnlineMessageListener != null) {
                mOnlineMessageListener.onReceived(response);
                return true;
            }
        }

        return false;
    }

    private void saveAvartarState(AccountPreferrence apf, String avartarType, String avartarId) {
        if (avartarType.equals("h")) {
            apf.setHairId(Integer.valueOf(avartarId));
        } else if (avartarType.equals("c")) {
            apf.setClothId(Integer.valueOf(avartarId));
        }
    }

    /**
     * @return true handled, false unHandled
     */
    private boolean onPreSent(boolean status, BaseMessage msg) {
        if (mOnlineMessageListener != null) {
            mOnlineMessageListener.onSent(false, msg);
            return true;
        }
        return false;
    }

    private boolean onPreConnected() {
        if (mOnlineMessageListener != null) {
            mOnlineMessageListener.onConnected();
            return true;
        }
        return false;
    }

    private boolean onPreConnectError() {
        if (mOnlineMessageListener != null) {
            mOnlineMessageListener.onConnectError();
            return true;
        }
        return false;
    }

    private boolean onPreDisconnected() {
        if (mOnlineMessageListener != null) {
            mOnlineMessageListener.onDisconnected();
            return true;
        }
        return false;
    }

    // 子类使用网络相关类
    protected void doBindService() {
        mConnection = new MessageServiceConnection();
        Intent i = new Intent(this, MessageServerService.class);

        boolean ret = getApplicationContext().bindService(i, mConnection,
                Context.BIND_AUTO_CREATE);
        LogUtil.d(TAG, BaseNetActivity.this.toString()
                + " -----------doBindService()------------- bound with " + ret);
        mIsBound = ret;
        if (!ret) {
            if (!onPreConnectError()) {
                onConnectError();
            }
        }
    }

    private void doUnbindService() {
        LogUtil.d(TAG, BaseNetActivity.this.toString()
                + " --------------doUnbindService()----------------- mIsBound=" + mIsBound);
        if (mIsBound) {
            getApplicationContext().unbindService(mConnection);
            mIsBound = false;
        }

        doPostDisconnect();
    }

    private void doPostDisconnect() {
        LogUtil.d(TAG, " doPostDisconnect()");

        try {
            doRemoveListener();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        mService = null;

        mIsConnected = false;
        if (!onPreDisconnected()) {
            onDisconnected();
        }
    }

    private void sendMessageInQueue() {
        synchronized (mSendQueue) {
            while (mSendQueue.size() > 0) {
                sendSingleMessage(mSendQueue.remove(0));
            }
        }
    }

    private void sendSingleMessage(BaseMessage msg) {
        try {
            if (/*
                 * !(msg instanceof RegisterUserReq) &&
                 */!(msg instanceof PasswordLoginReq)
                    && !(msg instanceof GuestLoginReq)) {
                if (mSessionId == null || mSessionId.length() <= 0) {

                    AccountPreferrence apf = AccountPreferrence.getSingleton();

                    mSessionId = apf.getSessionId(null);
                }
                if (mSessionId == null) {
                    mSessionId = "";
                }
                msg.setSessionId(mSessionId);

            }
            mService.sendMessage(msg);
        } catch (RemoteException e) {
            e.printStackTrace();

            if (!onPreSent(false, msg)) {
                onSent(false, msg);
            }
        } catch (MessageFormatException e) {
            e.printStackTrace();

            if (!onPreSent(false, msg)) {
                onSent(false, msg);
            }
        }
    }
}

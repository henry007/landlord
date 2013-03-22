
package com.hurray.landlord.game.online;

import com.hurray.landlord.game.GameServer;
import com.hurray.landlord.game.OnGameEventListener;
import com.hurray.landlord.game.data.RoomInfo;
import com.hurray.landlord.net.socket.ClientMessage;
import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.clt.ClientGoon;
import com.hurray.landlord.net.socket.clt.ClientSignIn;
import com.hurray.landlord.net.socket.srv.ServerDeclareResult;
import com.hurray.landlord.net.socket.srv.ServerGameResult;
import com.hurray.landlord.net.socket.srv.ServerPlsReady;
import com.hurray.landlord.net.socket.srv.ServerRoomInfo;
import com.hurray.landlord.net.socket.srv.ServerShowResult;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.ToastUtil;
import com.hurray.lordserver.protocol.message.base.BaseMessage;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class OnlineServer implements GameServer {

    private static final String TAG = "OnlineServer";

    private static final int NOTIFY_SENT = 0;

    private static final int NOTIFY_RECEIVED = 1;

    private static final int LONG_SERVER_MESSAGE_DELAY = 2000;

    private static final int SUPER_LONG_SERVER_MESSAGE_DELAY = 4000;

    private OnlineMessageAgent mOnlineMessageAgent;

    private OnGameEventListener mOnGameEventListener;

    private MessageAdapter mMessageAdapter;

    private RoomInfo mRoomInfo;

    private boolean mRequesting = false;

    private LinkedList<ServerMessage> mReceivedQueue = new LinkedList<ServerMessage>();

    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NOTIFY_SENT: {
                    ClientMessage cltMsg = (ClientMessage) msg.obj;
                    if (mOnGameEventListener != null)
                        mOnGameEventListener.onMessageSent(cltMsg);
                }
                    break;
                case NOTIFY_RECEIVED: {
                    ServerMessage srvMsg = (ServerMessage) msg.obj;
                    if (mOnGameEventListener != null)
                        mOnGameEventListener.onMessageReceived(srvMsg);

                    synchronized (mReceivedQueue) {
                        mRequesting = false;
                    }
                    afterDelayHandleReceivedQueue(getAfterDelay(srvMsg));
                }
                    break;
            }
        };

    };

    private Runnable mRunHandleMsg = new Runnable() {

        @Override
        public void run() {
            handleReceivedQueue();
        }

    };

    private void addToReceivedQueue(ServerMessage srvMsg) {
        synchronized (mReceivedQueue) {
            mReceivedQueue.add(srvMsg);
        }
    }

    private void handleReceivedQueue() {
        synchronized (mReceivedQueue) {
            if (mReceivedQueue.size() > 0) {
                ServerMessage srvMsg = mReceivedQueue.remove(0);
                Message msg = mHandler.obtainMessage(NOTIFY_RECEIVED, srvMsg);
                mHandler.sendMessage(msg);
            } else {
                mRequesting = false;
            }
        }
    }

    private void afterDelayHandleReceivedQueue(long afterDelay) {
        synchronized (mReceivedQueue) {
            if (mRequesting) {
                return;
            }
            mRequesting = true;
        }

        mHandler.postDelayed(mRunHandleMsg, afterDelay);
    }

    private long getAfterDelay(ServerMessage srvMsg) {
        long specialDelay = 100;
        if (srvMsg instanceof ServerGameResult) {
            specialDelay = SUPER_LONG_SERVER_MESSAGE_DELAY;
        } else if (srvMsg instanceof ServerDeclareResult) {
            specialDelay = LONG_SERVER_MESSAGE_DELAY;
        } else if (srvMsg instanceof ServerShowResult) {
            specialDelay = LONG_SERVER_MESSAGE_DELAY;
        }
        return specialDelay;
    }

    public OnlineServer(RoomInfo roomInfo, OnlineMessageAgent agent) {

        mRoomInfo = roomInfo;

        mMessageAdapter = new MessageAdapter(roomInfo);

        mOnlineMessageAgent = agent;

        mOnlineMessageAgent.setOnlineMessageListener(new OnlineMessageListener() {

            @Override
            public void onSent(boolean status, BaseMessage msg) {
            }

            @Override
            public void onReceived(BaseMessage msg) {

                try {
                    ArrayList<ServerMessage> srvMsgList = mMessageAdapter
                            .fromBaseMessageToServerMessage(msg);
                    Iterator<ServerMessage> it = srvMsgList.iterator();
                    while (it.hasNext()) {
                        addToReceivedQueue(it.next());
                    }

                    afterDelayHandleReceivedQueue(0);
                } catch (UidException e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, "!!!UID NOT IN ROOM");
                }

            }

            @Override
            public void onDisconnected() {
                if (mOnGameEventListener != null)
                    mOnGameEventListener.onGameOver();
            }

            @Override
            public void onConnected() {
                if (mOnGameEventListener != null)
                    mOnGameEventListener.onGameStart(true);

            }

            @Override
            public void onConnectError() {
                if (mOnGameEventListener != null)
                    mOnGameEventListener.onGameStart(false);

                ToastUtil.show("Service start with false!check out!!");
            }
        });
    }

    @Override
    public void connect() {
    }

    @Override
    public void disconnect() {
        mOnlineMessageAgent = null;
        mOnGameEventListener = null;
    }

    @Override
    public void send(ClientMessage cltMsg) {

        if (handleRoomInfoMsg(cltMsg)) {
            return;
        } else if (handleClientGoon(cltMsg)) {
            return;
        }

        ArrayList<BaseMessage> baseMsgList = mMessageAdapter.fromClientMessageToBaseMessage(cltMsg);
        Iterator<BaseMessage> it = baseMsgList.iterator();
        while (it.hasNext()) {
            mOnlineMessageAgent.doSend(it.next());
        }

    }

    @Override
    public void setOnGameEventListener(OnGameEventListener listener) {
        mOnGameEventListener = listener;
    }

    private boolean handleRoomInfoMsg(ClientMessage cltMsg) {
        if (cltMsg instanceof ClientSignIn) {
            int gameType = mRoomInfo.getGameType();
            long roomId = mRoomInfo.getRoomId();
            String[] nickNames = mRoomInfo.getNickNames();
            int[] sexs = mRoomInfo.getSexs();
            int myPlayerId = mRoomInfo.getSelfPlayers().getPlayerId();

            mRoomInfo.getNickNames();
            ServerRoomInfo srvRoomInfo = new ServerRoomInfo(gameType, roomId, nickNames, sexs,
                    myPlayerId);
            addToReceivedQueue(srvRoomInfo);
            afterDelayHandleReceivedQueue(50);

            ServerPlsReady srvPlsReady = new ServerPlsReady(mRoomInfo.getPlsReadyTime());
            addToReceivedQueue(srvPlsReady);
            afterDelayHandleReceivedQueue(150);
            return true;
        }
        return false;
    }

    private boolean handleClientGoon(ClientMessage cltMsg) {
        if (cltMsg instanceof ClientGoon) {
            ServerPlsReady srvPlsReady = new ServerPlsReady(mRoomInfo.getPlsReadyTime());
            addToReceivedQueue(srvPlsReady);
            afterDelayHandleReceivedQueue(50);
            return true;
        }
        return false;
    }

}


package com.hurray.landlord.game.local;

import com.hurray.landlord.Constants;
import com.hurray.landlord.game.CardUtil;
import com.hurray.landlord.game.OnGameEventListener;
import com.hurray.landlord.game.Robot;
import com.hurray.landlord.game.data.PlayerContext;
import com.hurray.landlord.net.socket.ClientMessage;
import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.clt.ClientChat;
import com.hurray.landlord.net.socket.clt.ClientDeclareLord;
import com.hurray.landlord.net.socket.clt.ClientEmotion;
import com.hurray.landlord.net.socket.clt.ClientGoon;
import com.hurray.landlord.net.socket.clt.ClientGotCards;
import com.hurray.landlord.net.socket.clt.ClientMessageType;
import com.hurray.landlord.net.socket.clt.ClientReady;
import com.hurray.landlord.net.socket.clt.ClientRobLord;
import com.hurray.landlord.net.socket.clt.ClientRobot;
import com.hurray.landlord.net.socket.clt.ClientShowCards;
import com.hurray.landlord.net.socket.clt.ClientShowPass;
import com.hurray.landlord.net.socket.srv.ServerAllocCards;
import com.hurray.landlord.net.socket.srv.ServerChat;
import com.hurray.landlord.net.socket.srv.ServerDeclareResult;
import com.hurray.landlord.net.socket.srv.ServerEmotion;
import com.hurray.landlord.net.socket.srv.ServerGameResult;
import com.hurray.landlord.net.socket.srv.ServerLastDeclare;
import com.hurray.landlord.net.socket.srv.ServerLastRob;
import com.hurray.landlord.net.socket.srv.ServerMessageType;
import com.hurray.landlord.net.socket.srv.ServerPlsDeclare;
import com.hurray.landlord.net.socket.srv.ServerPlsFollow;
import com.hurray.landlord.net.socket.srv.ServerPlsReady;
import com.hurray.landlord.net.socket.srv.ServerPlsRob;
import com.hurray.landlord.net.socket.srv.ServerPlsShow;
import com.hurray.landlord.net.socket.srv.ServerRobot;
import com.hurray.landlord.net.socket.srv.ServerRoomInfo;
import com.hurray.landlord.net.socket.srv.ServerShowResult;
import com.hurray.landlord.net.socket.srv.ServerUpdatePlus;
import com.hurray.landlord.net.socket.srv.ServerUpdateRate;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.lordserver.protocol.message.card.GameEndPush;

import android.os.Handler;
import android.os.Message;

import java.util.List;

public class LandLordPlayer implements ClientMessageType, ServerMessageType {

    private static final String TAG = "LandLordPlayer";

    private static final int ROBOT_DELAY = 1000;

    private OnLandLordPlayerListener mOnLandLordPlayerListener;

    private OnGameEventListener mOnGameEventListener;

    // context information

    private PlayerContext mPlayerContext;

    private Robot mRobot;

    private boolean mIsRobot;

    private boolean mIsHuman;

    private ServerMessage mLastServerOrder;

    private Handler mHandler;

    // -----------------------------------------

    public LandLordPlayer(int playerId, boolean isHuman) {
        mPlayerContext = new PlayerContext();
        mPlayerContext.mInfoMyself.mSelfPlayerId = playerId;
        mPlayerContext.mInfoMyself.mLeftPlayerId = (playerId - 1 + Constants.PLAYER_NUM)
                % Constants.PLAYER_NUM;
        mPlayerContext.mInfoMyself.mRightPlayerId = (playerId + 1 + Constants.PLAYER_NUM)
                % Constants.PLAYER_NUM;

        mIsHuman = isHuman;
        mIsRobot = false;

        mRobot = new Robot(mPlayerContext);

        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    ServerMessage sMsg = (ServerMessage) msg.obj;
                    if (mOnGameEventListener != null) {
                        mOnGameEventListener.onMessageReceived(sMsg);
                    }
                }
            }

        };
    }

    public void close() {
        mHandler.removeMessages(0);
    }

    public void switchToRobot() {
        LogUtil.d(TAG, "switchToRobot-----------------");
        mIsRobot = true;
    }

    public void setOnLandLordPlayerListener(OnLandLordPlayerListener listener) {
        mOnLandLordPlayerListener = listener;
    }

    public void setOnGameEventListener(OnGameEventListener listener) {
        mOnGameEventListener = listener;
    }

    // -----------------------------------------
    // get 系列

    public int getPlayerId() {
        return mPlayerContext.mInfoMyself.mSelfPlayerId;
    }

    public String getNickName() {
        return mPlayerContext.mInfoMyself.mSelfNickName;
    }

    public int[] getSelfCards() {
        return mPlayerContext.mInfoMyself.mSelfCardIds;
    }

    public int[] getLeftCardsNum() {
        return mPlayerContext.mInfoCommon.mLeftCardNums;
    }

    // -----------------------------------------
    // 接受房间消息的接口

    public void setRoomInfo(int gameType, int roomId, String[] nickNames, int[] sex) {
        // save local
        mPlayerContext.mInfoCommon.mGameType = gameType;
        mPlayerContext.mInfoCommon.mRoomId = roomId;
        mPlayerContext.mInfoCommon.mNickNames = nickNames;

        int myPlayerId = getPlayerId();
        mPlayerContext.mInfoMyself.mSelfNickName = nickNames[myPlayerId];

        // notify remote
        if (isHuman()) {
            ServerRoomInfo sMsg = new ServerRoomInfo(gameType, roomId, nickNames, sex, myPlayerId);
            sentServerMessage(sMsg);
        }
    }

    public void plsReady(long timeLeft) {
        int myPlayerId = getPlayerId();

        mPlayerContext.mInfoCommon.mDeclareNum = 0;
        mPlayerContext.mInfoCommon.mRate = 1;

        if (isHuman()) {
            ServerPlsReady sMsg = new ServerPlsReady(timeLeft);
            sentServerMessage(sMsg);

            mLastServerOrder = sMsg;

            if (mIsRobot) {
                robotReady(myPlayerId);
            }
        } else {
            robotReady(myPlayerId);
        }
    }

    private void robotReady(final int myPlayerId) {
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(ROBOT_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mOnLandLordPlayerListener.ready(myPlayerId);
            }
        }.start();
    }

    public void allocCards(int[] cardIds) {
        // save local
        int[] copiedCardIds = CardUtil.copyCards(cardIds);
        CardUtil.sortDescending(copiedCardIds);
        mPlayerContext.mInfoMyself.mSelfCardIds = copiedCardIds;

        int myPlayerId = getPlayerId();
        // notify remote
        if (isHuman()) {
            ServerAllocCards sMsg = new ServerAllocCards(myPlayerId, cardIds);
            sentServerMessage(sMsg);
            
            mLastServerOrder = sMsg;
            
            if (mIsRobot) {
                robotGotCards(myPlayerId);
            }
            
        } else {
            robotGotCards(myPlayerId);
        }

    }
    
    private void robotGotCards(final int myPlayerId) {
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(ROBOT_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mOnLandLordPlayerListener.gotCards(myPlayerId);
            }
        }.start();
    }

    public void plsRobLord(int playerId, long timeLeft) {
        int myPlayerId = getPlayerId();
        // notify remote
        if (isHuman()) {
            ServerPlsRob sMsg = new ServerPlsRob(playerId, timeLeft);
            sentServerMessage(sMsg);

            mLastServerOrder = sMsg;

            if (mIsRobot) {
                robotRobLord(playerId, myPlayerId);
            }
        } else {
            robotRobLord(playerId, myPlayerId);
        }
    }

    private void robotRobLord(int playerId, final int myPlayerId) {
        if (playerId == myPlayerId) {
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(ROBOT_DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mOnLandLordPlayerListener.robLord(myPlayerId, mRobot.suggestRobLord());
                }
            }.start();
        }
    }

    public void lastRob(int playerId, boolean isRob) {
        // notify remote
        if (isHuman()) {
            ServerLastRob sMsg = new ServerLastRob(playerId, isRob);
            sentServerMessage(sMsg);
        }
    }

    public void plsDeclareLord(int playerId, long timeLeft) {
        int myPlayerId = getPlayerId();
        // notify remote
        if (isHuman()) { // 发送给socket
            ServerPlsDeclare sMsg = new ServerPlsDeclare(playerId, timeLeft);
            sentServerMessage(sMsg);

            mLastServerOrder = sMsg;

            if (mIsRobot) {
                robotDeclareLord(playerId, myPlayerId);
            }
        } else { // 机器人处理
            robotDeclareLord(playerId, myPlayerId);
        }
    }

    private void robotDeclareLord(int playerId, final int myPlayerId) {
        if (playerId == myPlayerId) {
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(ROBOT_DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    int suggestDeclareNum = mRobot.suggestDeclareLordNum();
                    int declareNum = mPlayerContext.mInfoCommon.mDeclareNum;
                    if (declareNum < suggestDeclareNum) {
                        mOnLandLordPlayerListener.declareLord(myPlayerId, suggestDeclareNum);
                    } else {
                        mOnLandLordPlayerListener.declareLord(myPlayerId, 0);
                    }
                }
            }.start();
        }
    }

    public void lastDeclare(int playerId, int declaredLordNum, int maxDeclareLordNum) {
        mPlayerContext.mInfoCommon.mDeclareNum = declaredLordNum;

        if (isHuman()) { // 发送给socket
            ServerLastDeclare sMsg = new ServerLastDeclare(playerId, declaredLordNum,
                    maxDeclareLordNum);
            sentServerMessage(sMsg);
        }
    }

    public void declareResult(boolean result, int finalDeclaredNum, int lordPlayerId,
            int[] bottomCards) {
        mPlayerContext.mInfoCommon.mLordPlayerId = lordPlayerId;
        mPlayerContext.mInfoCommon.mBottomCardIds = bottomCards;

        if (mPlayerContext.mInfoMyself.mSelfPlayerId == lordPlayerId) { // 地主的牌加入底牌
            int[] landLordCardIds = new int[Constants.LANDLORD_CARDS_NUM];
            int[] selfCardIds = mPlayerContext.mInfoMyself.mSelfCardIds;
            int i = 0;
            for (; i < selfCardIds.length; i++) {
                landLordCardIds[i] = selfCardIds[i];
            }

            int j = i;
            for (; i < landLordCardIds.length; i++) {
                landLordCardIds[i] = bottomCards[i - j];
            }

            CardUtil.sortDescending(landLordCardIds);
            CardUtil.printCards("landLordCardIds = ", landLordCardIds);

            mPlayerContext.mInfoMyself.mSelfCardIds = landLordCardIds;
        }

        for (int i = 0; i < Constants.PLAYER_NUM; i++) {
            if (lordPlayerId == i) {
                mPlayerContext.mInfoCommon.mLeftCardNums[i] = Constants.LANDLORD_CARDS_NUM;
            } else {
                mPlayerContext.mInfoCommon.mLeftCardNums[i] = Constants.PLAYER_CARDS_NUM;
            }
        }

        if (isHuman()) { // 发送给socket
            ServerDeclareResult sMsg = new ServerDeclareResult(result, finalDeclaredNum,
                    lordPlayerId, bottomCards);
            sentServerMessage(sMsg);
        }
    }

    // 出牌
    public void plsShowCards(int playerId, long timeLeft) {
        final int myPlayerId = getPlayerId();
        // notify remote
        if (isHuman()) { // 发送给socket
            ServerPlsShow sMsg = new ServerPlsShow(playerId, timeLeft);
            sentServerMessage(sMsg);

            mLastServerOrder = sMsg;

            if (mIsRobot) {
                robotShowCards(playerId, myPlayerId);
            }
        } else { // 机器人处理
            robotShowCards(playerId, myPlayerId);
        }
    }

    private void robotShowCards(int playerId, final int myPlayerId) {
        if (playerId == myPlayerId) {
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(ROBOT_DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int[] showCards = mRobot.suggestFirstShow();
                    mOnLandLordPlayerListener.showCards(myPlayerId, showCards);
                }
            }.start();
        }
    }

    // 跟牌
    public void plsFollowCards(int playerId, int currMaxShowCardsPlayerId, int[] currMaxShowCards,
            long timeLeft) {

        mPlayerContext.mInfoCommon.mPrevShowPlayerId = currMaxShowCardsPlayerId;
        mPlayerContext.mInfoCommon.mPrevShowCardIds = currMaxShowCards;

        int myPlayerId = getPlayerId();

        if (isHuman()) { // 发送给socket
            ServerPlsFollow sMsg = new ServerPlsFollow(playerId,
                    currMaxShowCardsPlayerId, currMaxShowCards, timeLeft);
            sentServerMessage(sMsg);

            mLastServerOrder = sMsg;

            if (mIsRobot) {
                robotFollowCards(playerId, myPlayerId);
            }
        } else { // 机器人处理
            robotFollowCards(playerId, myPlayerId);
        }
    }

    private void robotFollowCards(int playerId, final int myPlayerId) {
        if (playerId == myPlayerId) {
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(ROBOT_DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int[] followCards = mRobot.suggestFollowShow();
                    if (followCards != null) {
                        mOnLandLordPlayerListener.showCards(myPlayerId, followCards);
                    } else {
                        mOnLandLordPlayerListener.showPass(myPlayerId);
                    }
                }
            }.start();
        }
    }

    public void updateRate(int rate) {
        mPlayerContext.mInfoCommon.mRate = rate;

        if (isHuman()) {
            ServerUpdateRate sMsg = new ServerUpdateRate(rate);
            sentServerMessage(sMsg);
        }
    }

    public void updatePlus(int playerId, long plus, long currPlus) {
        mPlayerContext.mInfoCommon.mPlus[playerId] = plus;

        if (isHuman()) {
            ServerUpdatePlus sMsg = new ServerUpdatePlus(playerId, plus, currPlus);
            sentServerMessage(sMsg);
        }
    }

    public void showResult(int lastShowPlayerId, int[] lastShowCards, boolean isShow) {

        if (lastShowCards != null && lastShowCards.length > 0) { // 非过牌
            mPlayerContext.mInfoCommon.mPrevShowPlayerId = lastShowPlayerId;
            mPlayerContext.mInfoCommon.mPrevShowCardIds = lastShowCards;
            mPlayerContext.mInfoCommon.mLeftCardNums[lastShowPlayerId] -= lastShowCards.length;

            // 移出系统确认的牌
            if (lastShowPlayerId == mPlayerContext.mInfoMyself.mSelfPlayerId) {
                // TODO 优化效率
                if (lastShowCards != null && lastShowCards.length > 0) {
                    int[] selfCardIds = mPlayerContext.mInfoMyself.mSelfCardIds;
                    for (int i = 0; i < lastShowCards.length; i++) {
                        for (int j = 0; j < selfCardIds.length; j++) {
                            if (lastShowCards[i] == selfCardIds[j]) {
                                selfCardIds[j] = -1;
                            }
                        }
                    }

                    mPlayerContext.mInfoMyself.mSelfCardIds = CardUtil.copyCards(selfCardIds);
                }

            }

        }

        if (isHuman()) { // 发送给socket
            ServerShowResult sMsg = new ServerShowResult(lastShowPlayerId,
                        lastShowCards, isShow);
            sentServerMessage(sMsg);
        }

    }

    // marks 0 , 1 , 2 对应playerId
    public void gameOver(boolean isLordWin, int[] finalMarks, int[] totalMarks, int[] handsCount,
            List<int[]> finalCardsList) {
        int myPlayerId = mPlayerContext.mInfoMyself.mSelfPlayerId;
        if (isHuman()) {
            ServerGameResult sMsg = new ServerGameResult(isLordWin, finalMarks, totalMarks, handsCount, finalCardsList);
            sentServerMessage(sMsg);

            mLastServerOrder = sMsg;

            // if (mIsRobot) {
            // robotGoon(myPlayerId);
            // }
        } else {
            robotGoon(myPlayerId);
        }
    }

    private void robotGoon(final int myPlayerId) {
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(ROBOT_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mOnLandLordPlayerListener.goon(myPlayerId);
            }
        }.start();
    }

    public void chat(int playerId, String message, int chatId) {
        if (isHuman()) { // 发送给socket
            ServerChat sMsg = new ServerChat(playerId, message, chatId);
            sentServerMessage(sMsg);
        }
    }

    public void emotion(int playerId, int emotionId) {
        if (isHuman()) { // 发送给socket
            ServerEmotion sMsg = new ServerEmotion(playerId, emotionId);
            sentServerMessage(sMsg);
        }
    }

    // ---------------------------------------------------------------

    public void onGameStart() {
        if (mOnGameEventListener != null) {
            new Thread() {
                public void run() {
                    if (mOnGameEventListener != null)
                        mOnGameEventListener.onGameStart(true);
                }
            }.start();
        }
    }

    public void onGameOver() {
        if (mOnGameEventListener != null) {
            new Thread() {
                public void run() {
                    if (mOnGameEventListener != null)
                        mOnGameEventListener.onGameOver();
                }
            }.start();
        }
    }

    public void onReceivedClientMessage(ClientMessage cMsg) {
        LogUtil.d(TAG, "onMessageReceived=" + cMsg.getMsgType());

        switch (cMsg.getMsgType()) {
            case CLT_SIGN_IN: {
                LogUtil.d(TAG, "CLT_SIGN_IN");
            }
                break;
            case CLT_SIGN_OUT: {
                LogUtil.d(TAG, "CLT_SIGN_OUT");
            }
                break;
            case CLT_LEAVE_ROOM: {
                GameEndPush a;
                LogUtil.d(TAG, "CLT_LEAVE_ROOM");
            }
                break;
            case CLT_READY: {
                LogUtil.d(TAG, "CLT_READY");
                ClientReady msg = (ClientReady) cMsg;
                int playerId = msg.getPlayerId();
                mOnLandLordPlayerListener.ready(playerId);

                mLastServerOrder = null;
            }
                break;
            case CLT_GOT_CARDS: {
                LogUtil.d(TAG, "CLT_GOT_CARDS");
                ClientGotCards msg = (ClientGotCards) cMsg;
                int playerId = msg.getPlayerId();                
                mOnLandLordPlayerListener.gotCards(playerId);
                
                mLastServerOrder = null;
            }
                break;
            case CLT_DECLARE_LORD: {
                LogUtil.d(TAG, "CLT_DECLARE_LORD");
                ClientDeclareLord msg = (ClientDeclareLord) cMsg;
                int declareLordNum = msg.getDeclareLordNum();
                int playerId = msg.getPlayerId();
                mOnLandLordPlayerListener.declareLord(playerId, declareLordNum);

                mLastServerOrder = null;
            }
                break;
            case CLT_ROB_LORD: {
                LogUtil.d(TAG, "CLT_ROB_LORD");
                ClientRobLord msg = (ClientRobLord) cMsg;
                int playerId = msg.getPlayerId();
                boolean isRob = msg.isRob();
                mOnLandLordPlayerListener.robLord(playerId, isRob);

                mLastServerOrder = null;
            }
                break;
            case CLT_SHOW_CARDS: {
                LogUtil.d(TAG, "CLT_SHOW_CARDS");
                ClientShowCards msg = (ClientShowCards) cMsg;
                int playerId = msg.getPlayerId();
                int[] showCardIds = msg.getShowCardIds();
                mOnLandLordPlayerListener.showCards(playerId, showCardIds);

                mLastServerOrder = null;
            }
                break;
            case CLT_SHOW_PASS: {
                LogUtil.d(TAG, "CLT_SHOW_PASS");
                ClientShowPass msg = (ClientShowPass) cMsg;
                int playerId = msg.getPlayerId();
                mOnLandLordPlayerListener.showPass(playerId);

                mLastServerOrder = null;
            }
                break;
            case CLT_ROBOT: {
                LogUtil.d(TAG, "CLT_ROBOT");
                ClientRobot msg = (ClientRobot) cMsg;
                final int playerId = msg.getPlayerId();
                final boolean isRobot = msg.isRobot();

                if (mPlayerContext.mInfoMyself.mSelfPlayerId == playerId) {
                    mIsRobot = isRobot;

                    new Thread() {
                        public void run() {
                            try {
                                sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            ServerRobot sMsg = new ServerRobot(playerId, isRobot);
                            sentServerMessage(sMsg);

                            try {
                                sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            repeatLastServerOrder();
                        }

                    }.start();

                }

            }
                break;
            case CLT_HEART_BEAT: {
                LogUtil.d(TAG, "CLT_HEART_BEAT");
            }
                break;
            case CLT_CHAT: {
                LogUtil.d(TAG, "CLT_CHAT");
                ClientChat msg = (ClientChat) cMsg;
                int playerId = msg.getPlayerId();
                String message = msg.getMessage();
                int chatId = msg.getChatId();

                mOnLandLordPlayerListener.chat(playerId, message, chatId);

                new Thread() {
                    public void run() {

                        int nextId = mPlayerContext.mInfoMyself.mRightPlayerId;
                        String nextMsg = "hurry!!! what're u doing??";
                        mOnLandLordPlayerListener.chat(nextId, nextMsg, -1);

                        int prevId = mPlayerContext.mInfoMyself.mLeftPlayerId;
                        String prevMsg = "hahaha... loser!!!";

                        mOnLandLordPlayerListener.chat(prevId, prevMsg, -1);
                    }
                }.start();
            }
                break;

            case CLT_EMOTION: {
                LogUtil.d(TAG, "CLT_EMOTION");
                ClientEmotion msg = (ClientEmotion) cMsg;
                int playerId = msg.getPlayerId();
                final int emotionId = msg.getEmotionId();

                mOnLandLordPlayerListener.emotion(playerId, emotionId);

                new Thread() {
                    public void run() {
                        int nextId = mPlayerContext.mInfoMyself.mRightPlayerId;
                        mOnLandLordPlayerListener.emotion(nextId, emotionId);

                        int prevId = mPlayerContext.mInfoMyself.mLeftPlayerId;
                        mOnLandLordPlayerListener.emotion(prevId, emotionId);
                    }
                }.start();

            }
                break;

            case CLT_GOON:
                LogUtil.d(TAG, "CLT_READY");
                ClientGoon msg = (ClientGoon) cMsg;
                int playerId = msg.getPlayerId();
                mOnLandLordPlayerListener.goon(playerId);

                mLastServerOrder = null;
                break;
        }
    }

    private void repeatLastServerOrder() {
        if (mLastServerOrder != null) {
            switch (mLastServerOrder.getMsgType()) {
                case SRV_PLS_READY: {
                    ServerPlsReady msg = (ServerPlsReady) mLastServerOrder;
                    plsReady(msg.getTimeLeft());
                }
                    break;
                case SRV_PLS_DECLARE: {
                    ServerPlsDeclare msg = (ServerPlsDeclare) mLastServerOrder;
                    plsDeclareLord(msg.getPlayerId(), msg.getTimeLeft());
                }
                    break;
                case SRV_PLS_SHOW: {
                    ServerPlsShow msg = (ServerPlsShow) mLastServerOrder;
                    plsShowCards(msg.getPlayerId(), msg.getTimeLeft());
                }
                    break;
                case SRV_PLS_FOLLOW: {
                    ServerPlsFollow msg = (ServerPlsFollow) mLastServerOrder;
                    plsFollowCards(msg.getPlayerId(), msg.getMaxCardIdsPlayerId(), msg
                            .getMaxCardIds(), msg.getTimeLeft());
                }
                    break;
            }
        }
    }

    private void sentServerMessage(ServerMessage sMsg) {
        mHandler.sendMessageDelayed(mHandler.obtainMessage(0, sMsg), 150);
    }

    private boolean isHuman() {
        return mIsHuman;
    }

}

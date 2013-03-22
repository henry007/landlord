
package com.hurray.landlord.game.local;

import com.hurray.landlord.Constants;
import com.hurray.landlord.game.CardType;
import com.hurray.landlord.game.CardUtil;
import com.hurray.landlord.game.data.Sex;
import com.hurray.landlord.utils.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class LocalRoom extends Thread {

    private static final String TAG = "LocalRoom";

    private static final int RUN_WAITING_DELAY = 200;

    private static final int START_SHOW_CARDS_DELAY = 2000;

    private static final int START_DELCARE_LORD_DELAY = 4000;

    private static final int ROB_LORD_DELAY = 400;

    private static final int RUN_DECLARE_LORD_LOOP_DELAY = 200;

    private static final int REPORT_MARKS_DELAY = 1000;

    private static final int RUN_SHOW_CARDS_DELAY = 300;

    private static final int SHOW_GAME_RESULT_DELAY = 4000;

    // timer wait
    private static final int READY_TIME_OUT_DELAY = 30000;

    private static final int DECLARE_LORD_TIME_OUT_DELAY = 30000;

    private static final int SHOW_CARDS_TIME_OUT_DELAY = 30000;

    private SingleUserTimeOut mTimeOut;

    //

    private boolean mLoop = true;

    private List<LandLordPlayer> mPlayers;

    private int[] mAllCardIds = new int[Constants.TOTAL_CARDS_NUM];

    private int[] mBottomCardIds = new int[Constants.BOTTOM_CARDS_NUM];

    private boolean[] mReadys = new boolean[Constants.PLAYER_NUM];

    private boolean mAllReady;

    //
    
    private boolean[] mGotCards = new boolean[Constants.PLAYER_NUM];

    private boolean mAllGotCards;
    
    //

    private OnLandLordPlayerListener mOnLandLordPlayerListener;

    private ArrayList<GameStep> mSteps;

    //

    private boolean mIsFakeOnlineServer = false;

    public LocalRoom(List<LandLordPlayer> players) {
        mPlayers = players;

        initOnLandLordPlayerListener();

        initGameSteps(false);

        mTimeOut = new SingleUserTimeOut();
    }

    public LocalRoom(List<LandLordPlayer> players, boolean withoutRobLord) {
        mPlayers = players;

        initOnLandLordPlayerListener();

        initGameSteps(withoutRobLord);

        mTimeOut = new SingleUserTimeOut();
    }

    public void setFakeOnlineServer(boolean isFakeOnlineServer) {
        mIsFakeOnlineServer = isFakeOnlineServer;
    }

    private void initGameSteps(boolean withoutRobLord) {
        mSteps = new ArrayList<GameStep>();
        mSteps.add(new StepWaitReady());
        mSteps.add(new StepInitCards());
        mSteps.add(new StepShuffleCards());
        mSteps.add(new StepAllocateCards());
        mSteps.add(new StepWaitAllGotCards());
        mSteps.add(new StepDeclareLord());
        if (withoutRobLord) {
            mSteps.add(new StepSkipRobLord());
        } else {
            mSteps.add(new StepRobLord());
        }
        mSteps.add(new StepShowCards());
        mSteps.add(new StepReportMark());
    }

    private void initOnLandLordPlayerListener() {
        mOnLandLordPlayerListener = new OnLandLordPlayerListener() {

            @Override
            public void showPass(int playerId) {

                if (mShowCardsInfo.mCurrShowCardsPlayerId == playerId) {
                    LogUtil.d(TAG, "!!!!playerId=" + playerId + " showPass");
                    mTimeOut.reset();
                    mShowCardsInfo.mTempCurrShowCards = null;
                    mShowCardsInfo.mCurrShowCardsReceived = true;
                }

            }

            @Override
            public void showCards(int playerId, int[] showCards) {

                if (mShowCardsInfo.mCurrShowCardsPlayerId == playerId) {
                    LogUtil.d(TAG, "!!!!playerId=" + playerId + " showCards="
                            + CardUtil.toString(showCards));
                    mTimeOut.reset();
                    mShowCardsInfo.mTempCurrShowCards = showCards;
                    mShowCardsInfo.mCurrShowCardsReceived = true;
                }

            }

            @Override
            public void declareLord(int playerId, int callNum) {
                if (playerId == mDeclareLordInfo.mCurrDeclarePlayerId) {
                    LogUtil.d(TAG, "!!!!playerId=" + playerId + " declareLord callNum=" + callNum);
                    mTimeOut.reset();
                    mDeclareLordInfo.mTempDeclareNum = callNum;
                    mDeclareLordInfo.mCurrDeclareReceived = true;
                } else {
                    // TODO should not receive others' players', just ignore
                    LogUtil.d(TAG, "declareLord SHOULD NOT BE HERE !!!!!!!!!");
                }

            }

            @Override
            public void robLord(int playerId, boolean isRob) {
                if (playerId == mCurrRobPlayerId) {
                    LogUtil.d(TAG, "!!!!mCurrRobPlayerId=" + mCurrRobPlayerId);
                    mTimeOut.reset();
                    mCurrRobReceived = true;
                    mIsCurrRob = isRob;
                } else {
                    // TODO should not receive others' players', just ignore
                    LogUtil.d(TAG, "robLord SHOULD NOT BE HERE !!!!!!!!!");
                }

            }

            @Override
            public void ready(int playerId) {
                if (!mReadys[playerId]) {
                    mReadys[playerId] = true;

                    int readyCount = 0;
                    for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                        if (mReadys[i]) {
                            readyCount++;
                        }
                    }

                    if (readyCount == Constants.PLAYER_NUM) {
                        mAllReady = true;
                        LogUtil.d(TAG, "ALL PLAYER READY !!!");
                    }
                }

            }
            
            @Override
            public void gotCards(int playerId) {
                if (!mGotCards[playerId]) {
                    mGotCards[playerId] = true;

                    int readyCount = 0;
                    for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                        if (mGotCards[i]) {
                            readyCount++;
                        }
                    }

                    if (readyCount == Constants.PLAYER_NUM) {
                        mAllGotCards = true;
                        LogUtil.d(TAG, "ALL PLAYER GOT CARDS !!!");
                    }
                }
                
            }

            @Override
            public void chat(int playerId, String message, int chatId) {
                for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                    mPlayers.get(i).chat(playerId, message, chatId);
                }

            }

            @Override
            public void emotion(int playerId, int emotionId) {
                for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                    mPlayers.get(i).emotion(playerId, emotionId);
                }
            }

            @Override
            public void goon(int playerId) {
                if (!mGoons[playerId]) {
                    mGoons[playerId] = true;

                    int goonCount = 0;
                    for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                        if (mGoons[i]) {
                            goonCount++;
                        }
                    }

                    if (goonCount == Constants.PLAYER_NUM) {
                        mAllGoon = true;
                        LogUtil.d(TAG, "ALL PLAYER GOON !!!");
                    }
                }

            }

        };

        for (int i = 0; i < mPlayers.size(); i++) {
            mPlayers.get(i).setOnLandLordPlayerListener(mOnLandLordPlayerListener);
        }
    }

    // -----------------------------------------------

    private DeclareLordInfo mDeclareLordInfo = new DeclareLordInfo();

    private static class DeclareLordInfo implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = -6968485793289270265L;

        // 叫地主
        public static final int MAX_DECLARE_NUM = 3;

        public int mCurrDeclarePlayerId;

        public boolean mCurrDeclareReceived;

        public int mTempDeclareNum;

        public int mCurrDeclareNum;

        // 叫地主次数，最大3
        public int mDeclareCount;

        // 叫地主结果
        public int mMaxDeclarePlayerId;

        public int mMaxDeclareNum;
    }

    // -----------------------------------------------

    private ShowCardsInfo mShowCardsInfo = new ShowCardsInfo();

    private static class ShowCardsInfo implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 7160710840118797715L;

        public static final int THINKING_IDLE = 0;

        public static final int THINKING_SHOW_CARD = 1;

        public static final int THINKING_FOLLOW_CARD = 2;

        public int mPlayerState = THINKING_IDLE;

        public boolean mIsFirstShow;

        // 当前出牌人

        public int mCurrShowCardsPlayerId;

        public int[] mTempCurrShowCards;

        public boolean mCurrShowCardsReceived;

        // 目前桌面最大出牌

        public int mCurrMaxShowCardsPlayerId;

        public int[] mCurrMaxShowCards;

        public int mCurrPassCount; // 理论2人过牌，此轮结束

        // 上家出牌 或 过牌

        public int mPrevShowCardsPlayerId;

        public int[] mPrevShowCards;

        public int[] mHandsCount;

        public int[] mLeftCardsNum;
    }

    // ----------------------------------------------

    private int mRate = 1;

    private int[] mPlus = new int[] {
            0, 0, 0
    };

    private void doubleRate() {
        mRate *= 2;
    }

    private void resetRate() {
        mRate = 1;
    }

    private void resetPlus() {
        for (int i = 0; i < mPlus.length; i++) {
            mPlus[i] = 0;
        }
    }

    // -----------------------------------------------

    public void run() {

        int roundIndex = 0;
        int stepIndex = 0;
        int totalStep = mSteps.size();
        LogUtil.d(TAG, "totalStep=" + totalStep);

        while (mLoop) {

            if (roundIndex >= Constants.OFFLINE_GAME_ROUND_NUM) {
                mLoop = false;
                break;
            }

            GameStep step = mSteps.get(stepIndex);
            if (step.run()) {
                stepIndex++;
                if (stepIndex == totalStep) {
                    roundIndex++;
                }
                stepIndex %= totalStep;
                LogUtil.d(TAG, "stepIndex=" + stepIndex);
                LogUtil.d(TAG, "roundIndex=" + roundIndex);
            }
        }

        LogUtil.d(TAG, "mLoop=" + mLoop);
    }

    public void close() {
        for (LandLordPlayer p : mPlayers) {
            p.close();
        }

        mLoop = false;

        mTimeOut.setTimeOutNow();

        try {
            this.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LogUtil.d(TAG, "CLOSE ROOM------------------END--------------------");
    }

    private void sleepMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // -----------------------------------------------

    // TODO 等待用户准备
    private class StepWaitReady extends GameStep {

        @Override
        public void start() {
            notifyRoomInfo();

            resetReadys();

            plsReady();

            mTimeOut.reset();
        }

        @Override
        public boolean loop() {
            sleepMillis(RUN_WAITING_DELAY);

            if (mAllReady) {
                return true;
            }

            if (mTimeOut.isTimeOut()) {
                mTimeOut.reset();
                mLoop = false;

                LogUtil.d(TAG, "WAITING TIME OUT ROOM CLOSED!!!");
                return true;
            }

            return false;
        }

        private void notifyRoomInfo() {
            String[] nickNames;
            int[] sexs;
            if (mIsFakeOnlineServer) {
                nickNames = new String[] {
                        "我是自己", "测试女", "测试男"
                };
                sexs = new int[] {
                        Sex.MALE, Sex.FEMALE, Sex.MALE
                };
                for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                    mPlayers.get(i).setRoomInfo(1, 0, nickNames, sexs);
                }
            } else {
                nickNames = new String[] {
                        "", "", ""
                };
                sexs = new int[] {
                        Sex.MALE, Sex.FEMALE, Sex.FEMALE
                };
                for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                    mPlayers.get(i).setRoomInfo(1, 0, nickNames, sexs);
                }
            }
        }

        private void resetReadys() {
            mAllReady = false;
            for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                mReadys[i] = false;
            }
        }

        private void plsReady() {
            for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                LandLordPlayer p = mPlayers.get(i);
                p.plsReady(READY_TIME_OUT_DELAY);
            }
        }

    }

    private class StepInitCards extends GameStep {

        @Override
        public void start() {
            resetRate();
            resetPlus();
            initAllCards();
        }

        @Override
        public boolean loop() {
            return true;
        }

        private void initAllCards() {

            switch (Constants.CONTROL_CARDS) {
                case Constants.CARDS_RANDOM: {
                    // 正常
                    for (int i = 0; i < Constants.TOTAL_CARDS_NUM; i++) {
                        mAllCardIds[i] = i;
                    }
                }
                    break;
                case Constants.CARDS_ROCKET: {
                    // 测试炸弹飞机火箭
                    for (int i = 0; i < Constants.TOTAL_CARDS_NUM; i++) {
                        mAllCardIds[i] = Constants.TOTAL_CARDS_NUM - i - 1;
                    }
                }
                    break;
                case Constants.CARDS_SINGLE_DRAGON: {
                    // 测试单顺
                    mAllCardIds = new int[] {
                            0, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40, 44, 48, 2, 3, 6, 7, 10, 11,
                            14, 15, 18, 19, 22, 23, 26, 27, 30, 31, 34, 35, 38, 39, 42, 43, 46, 47,
                            50, 51, 1, 5, 9, 13, 17, 21, 25, 29, 33, 37, 41, 45, 49, 52, 53
                    };
                }
                    break;
                case Constants.CARDS_DOUBLE_DRAGON: {
                    // 测试双顺
                    mAllCardIds = new int[] {
                            33, 36, 37, 40, 41, 44, 45, 48, 49, 2, 3, 6, 7, 10, 11, 14,
                            0,
                            15, 18, 19, 22, 23, 26, 27, 30, 31, 34, 35, 38, 39, 42, 43,
                            46, 47,
                            1, 4, 5, 8, 9, 12, 13, 16, 17, 20, 21, 24, 25, 28, 29, 32,
                            50, 51, 52, 53
                    };
                }
                    break;
                case Constants.CARDS_TRIPLE_W_SINGLE: {
                    // 测试双顺
                    mAllCardIds = new int[] {

                            16, 17, 18, 35, 20, 21, 22, 39, 24, 25, 26, 43, 28, 29, 30, 47, 52,
                            
                            0, 1, 2, 19, 4, 5, 6, 23, 8, 9, 10, 27, 12, 13, 14, 31, 48,
                            
                            32, 33, 34, 3, 36, 37, 38, 7, 40, 41, 42, 11, 44, 45, 46, 15, 51,
                            
                            49 , 53,50 , 51
                    };
                }
                    break;

            }

            CardUtil.printCards("initAllCards: ", mAllCardIds);
        }
    }

    private class StepShuffleCards extends GameStep {

        @Override
        public void start() {
            if (Constants.CONTROL_CARDS == Constants.CARDS_RANDOM) {
                shuffleAllCards();
            }
        }

        @Override
        public boolean loop() {
            return true;
        }

        // 洗牌
        private void shuffleAllCards() {
            CardUtil.shuffle(mAllCardIds);
            CardUtil.printCards("shuffleAllCards: ", mAllCardIds);
        }

    }

    private class StepAllocateCards extends GameStep {

        @Override
        public void start() {
            resetGotCards();
            allocBottomCards();
            allocPlayerCards();
        }

        @Override
        public boolean loop() {
            return true;
        }

        private void allocBottomCards() {
            for (int i = 0; i < Constants.BOTTOM_CARDS_NUM; i++) {
                mBottomCardIds[Constants.BOTTOM_CARDS_NUM - i - 1] = mAllCardIds[Constants.TOTAL_CARDS_NUM
                        - i - 1];
            }

            CardUtil.sortDescending(mBottomCardIds);
            CardUtil.printCards("allocBottomCards: ", mBottomCardIds);
        }

        private void allocPlayerCards() {
            for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                int[] playerCardIds = new int[Constants.PLAYER_CARDS_NUM];
                for (int j = 0; j < Constants.PLAYER_CARDS_NUM; j++) {
                    playerCardIds[j] = mAllCardIds[i * Constants.PLAYER_CARDS_NUM + j];
                }

                // CardUtil.sortDescending(playerCardIds);

                LandLordPlayer p = mPlayers.get(i);
                p.allocCards(playerCardIds);

                CardUtil.printCards("allocPlayerCards playerId=" + i + " Cards:", playerCardIds);
            }
        }
        
        private void resetGotCards() {
            mAllGotCards = false;
            for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                mGotCards[i] = false;
            }
        }

    }
    
    private class StepWaitAllGotCards extends GameStep {

        @Override
        public void start() {
            mTimeOut.reset();
        }

        @Override
        public boolean loop() {
            sleepMillis(RUN_WAITING_DELAY);

            if (mAllGotCards) {
                return true;
            }

            if (mTimeOut.isTimeOut()) {
                mTimeOut.reset();
                mLoop = false;

                LogUtil.d(TAG, "WAITING TIME OUT ROOM CLOSED!!!");
                return true;
            }

            return false;
        }

    }    

    private class StepDeclareLord extends GameStep {

        private Random mRand = new Random();

        @Override
        public void start() {
            // 第一次叫地主
            LogUtil.d(TAG, "BEGIN DECLARE LORD");
            initDeclareLord();

            notifyDeclareLord();
            mDeclareLordInfo.mDeclareCount++;
            mTimeOut.setTimeOut(DECLARE_LORD_TIME_OUT_DELAY);
        }

        @Override
        public boolean loop() {
            return runDeclareLord();
        }

        private boolean runDeclareLord() {

            // 超时 当做不叫
            if (mTimeOut.isTimeOut()) {
                mTimeOut.reset();

                sleepMillis(START_DELCARE_LORD_DELAY);

                LogUtil.d(TAG, "DECLARE LORD TIME OUT, mTempDeclareNum SET 0");

                mDeclareLordInfo.mTempDeclareNum = 0;
                mDeclareLordInfo.mCurrDeclareReceived = true;
            }

            // 返回消息
            if (mDeclareLordInfo.mCurrDeclareReceived) {
                mDeclareLordInfo.mCurrDeclareReceived = false;

                // 更新上一家叫地主信息
                if (mDeclareLordInfo.mTempDeclareNum > mDeclareLordInfo.mMaxDeclareNum) {
                    mDeclareLordInfo.mCurrDeclareNum = mDeclareLordInfo.mTempDeclareNum;
                    mDeclareLordInfo.mTempDeclareNum = 0;
                } else {
                    mDeclareLordInfo.mCurrDeclareNum = 0; // 没有超过最大分，当做不叫
                }

                // 如果是最大分，更新最大分
                if (mDeclareLordInfo.mCurrDeclareNum > mDeclareLordInfo.mMaxDeclareNum) {
                    mDeclareLordInfo.mMaxDeclarePlayerId = mDeclareLordInfo.mCurrDeclarePlayerId;
                    mDeclareLordInfo.mMaxDeclareNum = mDeclareLordInfo.mCurrDeclareNum;
                }

                reportCurrDeclare();

                // 结束叫地主
                if (mDeclareLordInfo.mMaxDeclareNum == DeclareLordInfo.MAX_DECLARE_NUM
                        || mDeclareLordInfo.mDeclareCount == Constants.PLAYER_NUM) {
                    if (mDeclareLordInfo.mMaxDeclareNum <= 0 || mDeclareLordInfo.mMaxDeclareNum > 3) { // 没人叫地主
                        // // 方案一，重新叫地主
                        // reportDeclareLordResult();
                        // mDeclareCount = 0;
                        // mStatus = S_READY;

                        // 方案二，系统随机分配地主
                        mDeclareLordInfo.mMaxDeclareNum = 1;
                        mDeclareLordInfo.mMaxDeclarePlayerId = CardUtil.randLandLordId();
                        LogUtil.d(TAG, "randLandLordId=" + mDeclareLordInfo.mMaxDeclarePlayerId);

                    }

                    // reportDeclareLordResult();

                    sleepMillis(START_SHOW_CARDS_DELAY);

                    return true;

                } else if (mDeclareLordInfo.mDeclareCount < Constants.PLAYER_NUM) {
                    // 下一家叫地主
                    mDeclareLordInfo.mCurrDeclarePlayerId++;
                    mDeclareLordInfo.mCurrDeclarePlayerId %= Constants.PLAYER_NUM;

                    notifyDeclareLord();
                    mDeclareLordInfo.mDeclareCount++;
                    mTimeOut.reset();

                    return false;
                }
            }

            sleepMillis(RUN_DECLARE_LORD_LOOP_DELAY);

            return false;
        }

        private void initDeclareLord() {
            mRand.setSeed(System.currentTimeMillis());
            mDeclareLordInfo.mCurrDeclarePlayerId = (mRand.nextInt() % Constants.PLAYER_NUM + Constants.PLAYER_NUM)
                    % Constants.PLAYER_NUM;

            mDeclareLordInfo.mCurrDeclareReceived = false;
            mDeclareLordInfo.mTempDeclareNum = 0;
            mDeclareLordInfo.mCurrDeclareNum = 0;

            mDeclareLordInfo.mDeclareCount = 0;

            mDeclareLordInfo.mMaxDeclarePlayerId = -1;
            mDeclareLordInfo.mMaxDeclareNum = 0;
        }

        private void notifyDeclareLord() {
            LogUtil.d(TAG, "notifyDeclareLord mCurrDeclarePlayerId="
                    + mDeclareLordInfo.mCurrDeclarePlayerId);
            for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                LandLordPlayer p = mPlayers.get(i);
                p.plsDeclareLord(mDeclareLordInfo.mCurrDeclarePlayerId, DECLARE_LORD_TIME_OUT_DELAY);
            }
        }

        private void reportCurrDeclare() {
            LogUtil.d(TAG, "reportCurrDeclare mCurrDeclarePlayerId="
                    + mDeclareLordInfo.mCurrDeclarePlayerId
                    + " mCurrDeclareNum=" + mDeclareLordInfo.mCurrDeclareNum + " mMaxDeclareNum="
                    + mDeclareLordInfo.mMaxDeclareNum);
            for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                LandLordPlayer p = mPlayers.get(i);
                p.lastDeclare(mDeclareLordInfo.mCurrDeclarePlayerId,
                        mDeclareLordInfo.mCurrDeclareNum, mDeclareLordInfo.mMaxDeclareNum);
            }
        }

    }

    private void reportDeclareLordResult() {
        LogUtil.d(TAG, "+++reportDeclareLordResult mMaxDeclarePlayerId="
                + mDeclareLordInfo.mMaxDeclarePlayerId
                + " mMaxDeclareNum=" + mDeclareLordInfo.mMaxDeclareNum);
        boolean result = false;
        if (mDeclareLordInfo.mMaxDeclareNum > 0) {
            result = true;
        }

        for (int i = 0; i < Constants.PLAYER_NUM; i++) {
            LandLordPlayer p = mPlayers.get(i);
            p.declareResult(result, mDeclareLordInfo.mMaxDeclareNum,
                    mDeclareLordInfo.mMaxDeclarePlayerId, mBottomCardIds);
        }
    }

    private static final long ROB_LORD_TIME_OUT_DELAY = 30000;

    private int mCurrRobPlayerId;

    private boolean mCurrRobReceived;

    private boolean mIsCurrRob;

    private boolean[] mUsedRobRights = new boolean[Constants.PLAYER_NUM];

    private class StepRobLord extends GameStep {

        @Override
        protected void start() {
            initRobLord();
            doNextRobLord();
        }

        @Override
        protected boolean loop() {
            return runRobLord();
        }

        private void initRobLord() {
            for (int i = mUsedRobRights.length - 1; i >= 0; i--) {
                mUsedRobRights[i] = false;
            }

            mCurrRobReceived = false;
            mIsCurrRob = false;
            mCurrRobPlayerId = mDeclareLordInfo.mCurrDeclarePlayerId;
        }

        private boolean runRobLord() {

            // 超时 当做不抢
            if (mTimeOut.isTimeOut()) {
                mTimeOut.reset();

                LogUtil.d(TAG, "ROB LORD TIME OUT");

                mIsCurrRob = false;
                mCurrRobReceived = true;
            }

            if (mCurrRobReceived) {
                mCurrRobReceived = false;

                if (mIsCurrRob) {
                    mDeclareLordInfo.mMaxDeclarePlayerId = mCurrRobPlayerId;
                    mDeclareLordInfo.mMaxDeclareNum *= 2;
                }

                reportCurrRob();

                sleepMillis(ROB_LORD_DELAY);

                if (usedUpRobRights()) { // 结束抢地主
                    reportDeclareLordResult();

                    sleepMillis(ROB_LORD_DELAY);
                    return true;
                } else { // 继续抢
                    doNextRobLord();
                    return false;
                }

            }

            sleepMillis(RUN_DECLARE_LORD_LOOP_DELAY);

            return false;
        }

        private void doNextRobLord() {
            moveToNextNoneLordPlayer();
            notifyRobLord();
            mTimeOut.setTimeOut(ROB_LORD_TIME_OUT_DELAY);
        }

        private void moveToNextNoneLordPlayer() {
            mCurrRobPlayerId++;
            mCurrRobPlayerId %= Constants.PLAYER_NUM;
            if (mCurrRobPlayerId == mDeclareLordInfo.mMaxDeclarePlayerId) {
                mCurrRobPlayerId++;
                mCurrRobPlayerId %= Constants.PLAYER_NUM;
            }
        }

        private boolean usedUpRobRights() {
            for (int i = mUsedRobRights.length - 1; i >= 0; i--) {
                // 权限仍在,并且不是地主
                if (!mUsedRobRights[i] && i != mDeclareLordInfo.mMaxDeclarePlayerId) {
                    return false;
                }
            }

            return true;
        }

        private void notifyRobLord() {
            LogUtil.d(TAG, "notifyRobLord mCurrRobPlayerId=" + mCurrRobPlayerId);

            mUsedRobRights[mCurrRobPlayerId] = true;

            for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                LandLordPlayer p = mPlayers.get(i);
                p.plsRobLord(mCurrRobPlayerId, ROB_LORD_TIME_OUT_DELAY);
            }
        }

        private void reportCurrRob() {
            for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                LandLordPlayer p = mPlayers.get(i);
                p.lastRob(mCurrRobPlayerId, mIsCurrRob);
            }
        }

    }

    private class StepSkipRobLord extends GameStep {

        @Override
        protected void start() {
            reportDeclareLordResult();
        }

        @Override
        protected boolean loop() {
            return true;
        }

    }

    private class StepShowCards extends GameStep {

        // 玩家剩余牌数，首先剩余为0的，胜利

        private int[] mPlayersLeftCardsNum;

        @Override
        public void start() {
            initPlayersLeftCardsNum();
            initHandsCount();
            mShowCardsInfo.mCurrShowCardsPlayerId = mDeclareLordInfo.mMaxDeclarePlayerId;
            firstShowCardsReset();

            mShowCardsInfo.mLeftCardsNum = new int[Constants.PLAYER_NUM];
            for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                if (i == mDeclareLordInfo.mMaxDeclarePlayerId) {
                    mShowCardsInfo.mLeftCardsNum[i] = Constants.LANDLORD_CARDS_NUM;
                } else {
                    mShowCardsInfo.mLeftCardsNum[i] = Constants.PLAYER_CARDS_NUM;
                }
            }
        }

        @Override
        public boolean loop() {
            return runShowCards();
        }

        private boolean runShowCards() {
            // LogUtil.d(TAG, "runShowCards");
            // 新一轮开始
            if (mShowCardsInfo.mIsFirstShow) {
                mShowCardsInfo.mIsFirstShow = false;
                LogUtil.d(TAG, "-------NEW ROUND-------");
                notifyShowCards();
                mTimeOut.setTimeOut(SHOW_CARDS_TIME_OUT_DELAY);
                return false;
            }

            // // 方案一：超时 当做过牌
            // if (mTimeOut) {
            // mTimeOut = false;
            //
            // mTempCurrShowCards = null;
            // mCurrShowCardsReceived = true;
            // }

            // // 方案二：超时 交给机器人
            // if (mTimeOut) {
            // mTimeOut = false;
            //
            // LandLordPlayer p = mPlayers.get(mCurrShowCardsPlayerId);
            // p.switchToRobot();
            //
            // if (mPlayerState == THINKING_SHOW_CARD) {
            // LogUtil.d(TAG, "THINKING_SHOW_CARD mCurrShowCardsPlayerId="
            // + mCurrShowCardsPlayerId);
            // p.plsShowCards(mCurrShowCardsPlayerId,
            // SHOW_CARDS_TIME_OUT_DELAY);
            // } else if (mPlayerState == THINKING_FOLLOW_CARD) {
            // p.plsFollowCards(mCurrShowCardsPlayerId,
            // mCurrMaxShowCardsPlayerId,
            // mCurrMaxShowCards, SHOW_CARDS_TIME_OUT_DELAY);
            // }
            // }

            // 方案三：忽略超时
            if (mTimeOut.isTimeOut()) {
                mTimeOut.reset();
            }

            // 收到出牌结果
            if (mShowCardsInfo.mCurrShowCardsReceived) {
                mShowCardsInfo.mCurrShowCardsReceived = false;

                if (acceptCards(mShowCardsInfo.mTempCurrShowCards)) {
                    updatePlus(mShowCardsInfo.mTempCurrShowCards,
                            mShowCardsInfo.mCurrShowCardsPlayerId);

                    mShowCardsInfo.mPrevShowCardsPlayerId = mShowCardsInfo.mCurrShowCardsPlayerId;
                    mShowCardsInfo.mPrevShowCards = mShowCardsInfo.mTempCurrShowCards;
                    mShowCardsInfo.mTempCurrShowCards = null;

                    countHandsCount();
                    countLeftCardsNum();

                    if (mShowCardsInfo.mPlayerState == ShowCardsInfo.THINKING_SHOW_CARD)
                        reportShowResult(true);
                    else
                        reportShowResult(false);

                    if (mShowCardsInfo.mPrevShowCards == null) { // 过牌
                        mShowCardsInfo.mCurrPassCount++;
                        // 此轮结束，重新开始
                        if (mShowCardsInfo.mCurrPassCount == 2) {
                            mShowCardsInfo.mCurrShowCardsPlayerId = mShowCardsInfo.mCurrMaxShowCardsPlayerId;
                            firstShowCardsReset();
                            LogUtil.d(TAG, "-------NEXT ROUND-------");
                            return false;
                        }
                    } else { // 出牌 或 跟牌
                        mShowCardsInfo.mCurrPassCount = 0;
                        mShowCardsInfo.mCurrMaxShowCardsPlayerId = mShowCardsInfo.mPrevShowCardsPlayerId;
                        mShowCardsInfo.mCurrMaxShowCards = mShowCardsInfo.mPrevShowCards;
                        mPlayersLeftCardsNum[mShowCardsInfo.mPrevShowCardsPlayerId] -= mShowCardsInfo.mPrevShowCards.length;

                        LogUtil.d(TAG, "playerId:" + mShowCardsInfo.mPrevShowCardsPlayerId
                                + " leftCardsNum="
                                + mPlayersLeftCardsNum[mShowCardsInfo.mPrevShowCardsPlayerId]);

                        // is game over
                        if (mPlayersLeftCardsNum[mShowCardsInfo.mPrevShowCardsPlayerId] <= 0) {
                            return true;
                        }
                    }

                    // 让下一家跟牌
                    mShowCardsInfo.mCurrShowCardsPlayerId++;
                    mShowCardsInfo.mCurrShowCardsPlayerId %= Constants.PLAYER_NUM;
                    notifyFollowCards();
                    mTimeOut.setTimeOut(SHOW_CARDS_TIME_OUT_DELAY);

                } else { // 客户端有错误才会到这儿！
                    LogUtil.d(TAG, "CLIENT ERROR!!!!!!!!");
                    // reportShowResult(false);

                    // 当过牌 处理
                    mTimeOut.reset();
                    mShowCardsInfo.mTempCurrShowCards = null;
                    mShowCardsInfo.mCurrShowCardsReceived = true;
                }

            }

            sleepMillis(RUN_SHOW_CARDS_DELAY);
            return false;
        }

        private void updatePlus(int[] showCards, int playerId) {
            long plus = CardUtil.caculatePlus(showCards);
            if (plus > 0) {
                mPlus[playerId] += plus;
                notifyPlus(playerId, mPlus[playerId], plus);
            }
        }

        private void notifyPlus(int playerId, long plus, long currPlus) {
            for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                LandLordPlayer p = mPlayers.get(i);
                p.updatePlus(playerId, plus, currPlus);
            }
        }

        private void notifyShowCards() {
            mShowCardsInfo.mPlayerState = ShowCardsInfo.THINKING_SHOW_CARD;
            for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                LandLordPlayer p = mPlayers.get(i);
                p.plsShowCards(mShowCardsInfo.mCurrShowCardsPlayerId, SHOW_CARDS_TIME_OUT_DELAY);
            }
        }

        private void notifyFollowCards() {
            mShowCardsInfo.mPlayerState = ShowCardsInfo.THINKING_FOLLOW_CARD;
            for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                LandLordPlayer p = mPlayers.get(i);
                p.plsFollowCards(mShowCardsInfo.mCurrShowCardsPlayerId,
                        mShowCardsInfo.mCurrMaxShowCardsPlayerId,
                        mShowCardsInfo.mCurrMaxShowCards,
                        SHOW_CARDS_TIME_OUT_DELAY);
            }
        }

        private void reportShowResult(boolean isShow) {
            for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                LandLordPlayer p = mPlayers.get(i);
                p.showResult(mShowCardsInfo.mPrevShowCardsPlayerId,
                        mShowCardsInfo.mPrevShowCards, isShow);
            }
        }

        private void countHandsCount() {
            if (mShowCardsInfo.mPrevShowCardsPlayerId != -1) {
                if (mShowCardsInfo.mPrevShowCards != null
                        && mShowCardsInfo.mPrevShowCards.length > 0) {
                    mShowCardsInfo.mHandsCount[mShowCardsInfo.mPrevShowCardsPlayerId]++;
                }
            }

        }

        private void countLeftCardsNum() {
            if (mShowCardsInfo.mPrevShowCards != null) {
                mShowCardsInfo.mLeftCardsNum[mShowCardsInfo.mPrevShowCardsPlayerId] -= mShowCardsInfo.mPrevShowCards.length;
            }
        }

        private boolean acceptCards(int[] newShowCardIds) {
            int[] showCardIds = null;
            if (newShowCardIds != null) {
                showCardIds = CardUtil.copyCards(newShowCardIds);
            }

            if (mShowCardsInfo.mCurrMaxShowCards == null) { // 此轮第一个出牌
                boolean isShowCardsOk = isShowCardsOk(showCardIds);
                if (isShowCardsOk) {
                    checkIfDoubleRate(showCardIds);
                }
                return isShowCardsOk;
            } else { // 跟牌
                boolean isFollowCardsOk = isFollowCardsOk(showCardIds,
                        mShowCardsInfo.mCurrMaxShowCards);
                if (isFollowCardsOk && showCardIds != null) {
                    checkIfDoubleRate(showCardIds);
                }
                return isFollowCardsOk;
            }
        }

        // 检查出的牌是否合法
        private boolean isShowCardsOk(int[] newShowCards) {
            if (newShowCards != null) {
                int cardType = CardUtil.getCardType(newShowCards);
                if (CardType.LL_NONE != cardType) {
                    return true;
                }
            }

            return false;
        }

        // 检查跟的牌是否合法
        private boolean isFollowCardsOk(int[] newShowCards, int[] currMaxShowCards) {
            if (newShowCards == null) {
                return true;
            }

            return CardUtil.compare(newShowCards, currMaxShowCards);
        }

        private void checkIfDoubleRate(int[] newShowCards) {
            int cardType = CardUtil.getCardType(newShowCards);
            if (cardType == CardType.LL_FOUR_AS_BOMBER || cardType == CardType.LL_JOKER_AS_ROCKET) {
                LogUtil.d(TAG, "BOMBER OR ROCKET DOUBLE RATE");
                doubleRate();

                notifyRate(mRate);
            }
        }

        private void firstShowCardsReset() {
            mShowCardsInfo.mIsFirstShow = true;

            mShowCardsInfo.mTempCurrShowCards = null;
            mShowCardsInfo.mCurrShowCardsReceived = false;

            mShowCardsInfo.mCurrMaxShowCardsPlayerId = -1;
            mShowCardsInfo.mCurrMaxShowCards = null;
            mShowCardsInfo.mCurrPassCount = 0;

            mShowCardsInfo.mPrevShowCardsPlayerId = -1;
            mShowCardsInfo.mPrevShowCards = null;

            mShowCardsInfo.mPlayerState = ShowCardsInfo.THINKING_IDLE;
        }

        private void initPlayersLeftCardsNum() {
            mPlayersLeftCardsNum = new int[Constants.PLAYER_NUM];
            for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                if (i != mDeclareLordInfo.mMaxDeclarePlayerId) {
                    mPlayersLeftCardsNum[i] = Constants.PLAYER_CARDS_NUM;
                } else {
                    mPlayersLeftCardsNum[i] = Constants.LANDLORD_CARDS_NUM;
                }
            }
        }

        private void initHandsCount() {
            mShowCardsInfo.mHandsCount = new int[Constants.PLAYER_NUM];
            for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                mShowCardsInfo.mHandsCount[i] = 0;
            }
        }

    }

    private void notifyRate(int rate) {
        for (int i = 0; i < Constants.PLAYER_NUM; i++) {
            LandLordPlayer p = mPlayers.get(i);
            p.updateRate(rate);
        }
    }

    private boolean[] mGoons = new boolean[Constants.PLAYER_NUM];
    private boolean mAllGoon;

    private class StepReportMark extends GameStep {

        private int[] mMarks;

        private int[] mTotalMarks;

        public StepReportMark() {
            mMarks = new int[Constants.PLAYER_NUM];
            resetMarks();

            mTotalMarks = new int[Constants.PLAYER_NUM];
            resetTotalMarks();
        }

        @Override
        public void start() {
            mAllGoon = false;
            for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                mGoons[i] = false;
            }

            sleepMillis(REPORT_MARKS_DELAY);
            resetMarks();
            reportGameMarks();
            sleepMillis(SHOW_GAME_RESULT_DELAY);
            LogUtil.d(TAG, "!!!!END----------------------------------");
        }

        @Override
        public boolean loop() {
            if (mAllGoon) {
                return true;
            } else {
                sleepMillis(RUN_WAITING_DELAY);
                return false;
            }
        }

        private boolean isFarmerNeverShow() {
            for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                if (mDeclareLordInfo.mMaxDeclarePlayerId != i) {
                    if (mShowCardsInfo.mLeftCardsNum[i] < 17) {
                        return false;
                    }
                }
            }

            return true;
        }

        private boolean isLordShowOnce() {
            return (mShowCardsInfo.mHandsCount[mDeclareLordInfo.mMaxDeclarePlayerId] <= 1);
        }

        private void reportGameMarks() {
            boolean isLordWin = (mShowCardsInfo.mCurrMaxShowCardsPlayerId == mDeclareLordInfo.mMaxDeclarePlayerId);
            if (isLordWin) {

                if (isFarmerNeverShow()) {
                    LogUtil.d(TAG, "Farmer Never Show Cards");
                    doubleRate();

                    notifyRate(mRate);
                }

                for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                    if (i == mDeclareLordInfo.mMaxDeclarePlayerId) {
                        mMarks[i] = Constants.BASE_NUM * mRate * mDeclareLordInfo.mMaxDeclareNum
                                * 2 + mPlus[i];
                    } else {
                        mMarks[i] = -Constants.BASE_NUM * mRate * mDeclareLordInfo.mMaxDeclareNum
                                + mPlus[i];
                    }

                    mTotalMarks[i] += mMarks[i];
                }
            } else {

                if (isLordShowOnce()) {
                    LogUtil.d(TAG, "Lord Show Only Once");
                    doubleRate();

                    notifyRate(mRate);
                }

                for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                    if (i == mDeclareLordInfo.mMaxDeclarePlayerId) {
                        mMarks[i] = -Constants.BASE_NUM * mRate * mDeclareLordInfo.mMaxDeclareNum
                                * 2 + mPlus[i];
                    } else {
                        mMarks[i] = Constants.BASE_NUM * mRate * mDeclareLordInfo.mMaxDeclareNum
                                + mPlus[i];
                    }

                    mTotalMarks[i] += mMarks[i];
                }
            }

            List<int[]> finalCardsList = new LinkedList<int[]>();
            for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                LandLordPlayer p = mPlayers.get(i);
                finalCardsList.add(p.getSelfCards());
                CardUtil.printCards("reportGameMarks playerId=" + i + " finalCardsList=", p
                        .getSelfCards());
            }

            for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                LandLordPlayer p = mPlayers.get(i);
                p.gameOver(isLordWin, mMarks, mTotalMarks, mShowCardsInfo.mHandsCount,
                        finalCardsList);
                LogUtil.d(TAG, "isLordWin=" + isLordWin + " mMarks[0]=" + mMarks[0] + " mMarks[1]="
                        + mMarks[1] + " mMarks[2]=" + mMarks[2]);
            }

        }

        private void resetMarks() {
            for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                mMarks[i] = 0;
            }
        }

        private void resetTotalMarks() {
            for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                mTotalMarks[i] = 0;
            }
        }

    }

}

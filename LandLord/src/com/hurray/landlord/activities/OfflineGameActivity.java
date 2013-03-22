package com.hurray.landlord.activities;

import com.hurray.landlord.Constants;
import com.hurray.landlord.R;
import com.hurray.landlord.adapter.OfflineTopDbadapter;
import com.hurray.landlord.animation.BombAnimation;
import com.hurray.landlord.animation.DoubleDragonAnimation;
import com.hurray.landlord.animation.FrameAnim;
import com.hurray.landlord.animation.PlaneAnimation;
import com.hurray.landlord.animation.RocketAnimation;
import com.hurray.landlord.animation.SingleDragonAnimation;
import com.hurray.landlord.animation.TextAnimation;
import com.hurray.landlord.bitmaps.CardFgsBitmap;
import com.hurray.landlord.bitmaps.CardsBitmap;
import com.hurray.landlord.game.CardHelperHandler;
import com.hurray.landlord.game.CardType;
import com.hurray.landlord.game.CardUtil;
import com.hurray.landlord.game.GameServer;
import com.hurray.landlord.game.GameSoundUtil;
import com.hurray.landlord.game.GameSoundUtil.OnRepeatListener;
import com.hurray.landlord.game.OnGameEventListener;
import com.hurray.landlord.game.Robot;
import com.hurray.landlord.game.data.GameResult;
import com.hurray.landlord.game.data.PlayerContext;
import com.hurray.landlord.game.local.AchievesRec;
import com.hurray.landlord.game.local.AiContext;
import com.hurray.landlord.game.local.ChatAi;
import com.hurray.landlord.game.local.ChatAi.OnChatListener;
import com.hurray.landlord.game.local.EmotionAi;
import com.hurray.landlord.game.local.EmotionAi.OnEmotionListener;
import com.hurray.landlord.game.local.LocalServer;
import com.hurray.landlord.game.local.ScoreRec;
import com.hurray.landlord.game.ui.Player;
import com.hurray.landlord.game.ui.Player.OnPreShowCardListener;
import com.hurray.landlord.game.ui.UiConstants;
import com.hurray.landlord.net.socket.ClientMessage;
import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.clt.ClientDeclareLord;
import com.hurray.landlord.net.socket.clt.ClientGoon;
import com.hurray.landlord.net.socket.clt.ClientGotCards;
import com.hurray.landlord.net.socket.clt.ClientLeaveRoom;
import com.hurray.landlord.net.socket.clt.ClientReady;
import com.hurray.landlord.net.socket.clt.ClientRobLord;
import com.hurray.landlord.net.socket.clt.ClientRobot;
import com.hurray.landlord.net.socket.clt.ClientShowCards;
import com.hurray.landlord.net.socket.clt.ClientShowPass;
import com.hurray.landlord.net.socket.clt.ClientSignIn;
import com.hurray.landlord.net.socket.clt.ClientSignOut;
import com.hurray.landlord.net.socket.srv.ServerAllocCards;
import com.hurray.landlord.net.socket.srv.ServerDeclareResult;
import com.hurray.landlord.net.socket.srv.ServerGameResult;
import com.hurray.landlord.net.socket.srv.ServerLastDeclare;
import com.hurray.landlord.net.socket.srv.ServerLastRob;
import com.hurray.landlord.net.socket.srv.ServerMessageType;
import com.hurray.landlord.net.socket.srv.ServerPlayerReady;
import com.hurray.landlord.net.socket.srv.ServerPlsDeclare;
import com.hurray.landlord.net.socket.srv.ServerPlsFollow;
import com.hurray.landlord.net.socket.srv.ServerPlsRob;
import com.hurray.landlord.net.socket.srv.ServerPlsShow;
import com.hurray.landlord.net.socket.srv.ServerRobot;
import com.hurray.landlord.net.socket.srv.ServerRoomInfo;
import com.hurray.landlord.net.socket.srv.ServerShowResult;
import com.hurray.landlord.net.socket.srv.ServerUpdatePlus;
import com.hurray.landlord.net.socket.srv.ServerUpdateRate;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.SoundSwitch;
import com.hurray.landlord.utils.SoundUtil;
import com.hurray.landlord.utils.SoundUtil.OnSoundListener;
import com.hurray.landlord.utils.ToastUtil;
import com.hurray.landlord.view.BottomCardsView;
import com.hurray.landlord.view.LocalAvatar;
import com.hurray.landlord.view.LocalGameBgView;
import com.hurray.landlord.view.LocalGameTopView;
import com.hurray.landlord.view.LordCupViewHolder;
import com.hurray.landlord.view.SelfCardsView;
import com.hurray.landlord.view.ShowCardsView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class OfflineGameActivity extends BaseActivity implements OnClickListener,
        OnGameEventListener, ServerMessageType {

    public static final String SELECTED_LIST = "selected_list";

    private static final String TAG = "OfflineGameActivity";

    private static final int TEXT_SHOW_PEROID = 1800;

    private static final int REQ_WIN_RESULT = 800;
    private static final int REQ_FAIL_RESULT = 900;
    private static final int REQ_GAME_RESULT = 1000;

    private static final int UI_HIDE_ALL_BTN = 10;

    private static final int UI_SHOW_START_BTN_ONLY = 15;

    private static final int UI_SHOW_DECLARE_LORD_BTN_ONLY = 20;
    private static final int UI_HIDE_DECLARE_LORD_BTN = 21;

    private static final int UI_SHOW_ROB_LORD_BTN = 25;
    private static final int UI_HIDE_ROB_LORD_BTN = 26;

    private static final int UI_SHOW_FIRST_SHOW_GAME_BTN = 29;
    private static final int UI_SHOW_FOLLOW_GAME_BTN = 30;
    private static final int UI_HIDE_GAME_BTN = 31;

    private static final int UI_SHOW_BOTTOM_CARDS = 35;
    private static final int UI_HIDE_BOTTOM_CARDS = 36;

    private static final int UI_SHOW_RIGHT_TEXT = 40;
    private static final int UI_HIDE_RIGHT_TEXT = 41;

    private static final int UI_SHOW_LEFT_TEXT = 45;
    private static final int UI_HIDE_LEFT_TEXT = 46;

    private static final int UI_SHOW_SELF_TEXT = 48;
    private static final int UI_HIDE_SELF_TEXT = 49;

    private static final int UI_SHOW_BOMB_ANIM = 50;
    private static final int UI_SHOW_PLANE_ANIM = 51;
    private static final int UI_SHOW_ROCKET_ANIM = 52;
    private static final int UI_SHOW_DOUBLE_DRAGON_ANIM = 53;
    private static final int UI_SHOW_SINGLE_DRAGON_ANIM = 54;

    private static final int UI_SHOW_LEFT_PLUS_ANIM = 60;
    private static final int UI_SHOW_RIGHT_PLUS_ANIM = 61;
    private static final int UI_SHOW_SELF_PLUS_ANIM = 62;

    private static final int UI_RESET_LORD_CUP = 70;
    private static final int UI_UPDATE_LORD_CUP = 71;

    private static final int UI_UPDATE_LEFT_CARD_NUM = 80;

    private static final int UI_UPDATE_RIGHT_CARD_NUM = 90;

    private static final int UI_UPDATE_SELF_CARDS = 100;

    private String mLeftName;
    private String mRightName;

    private LocalAvatar mLeftAvatar;
    private LocalAvatar mRightAvatar;

    // --------------------- image anim ------------------

    private SingleDragonAnimation mSingleDragonAnimation;
    private DoubleDragonAnimation mDoubleDragonAnimation;
    private RocketAnimation mRocketAnimation;
    private PlaneAnimation mPlaneAnimation;
    private BombAnimation mBombAnimation;

    private ImageView mAnimImage;
    private ImageView mBombAnimImage;

    private View mRocketTweenView;
    private ImageView mRocketStillImage;
    private ImageView mRocketFrameImage;

    private View mPlaneTweenView;
    private ImageView mPlaneStillImage;
    private ImageView mPlaneFrameImage;

    private ArrayList<TextAnimation> mPlusAnims;

    // --------------------- image anim ------------------

    private LocalGameBgView mGameBgView;

    private ImageView mGameTable;

    private ShowCardsView mLeftShowCards, mRightShowCards, mSelfShowCards;

    private SelfCardsView mSelfCardsView;

    private TextView mLeftCardNum, mRightCardNum;

    private LocalGameTopView mGameTopView;

    private LinearLayout mGameButtons;
    private View mBuYaoGameBtn;

    private LinearLayout mDeclareLordButtons;

    private LinearLayout mRobLordButtons;

    private View mDeclareLord0, mDeclareLord1, mDeclareLord2, mDeclareLord3;

    private LordCupViewHolder mLordCupViewHolder;

    // private boolean mUserPlaying = true;

    private TextView mLeftText, mRightText, mSelfText;

    private TextView mMatchRecord, mRoundScore;

    private Handler mHandler;

    private Robot mTiShiRobot;

    private GameServer mGameServer;

    private PlayerContext mPlayerContext;

    /***
     * playerId和位置 2(prev left) 1(next right) 0(self)
     */

    private ArrayList<Player> mPlayers = new ArrayList<Player>(Constants.PLAYER_NUM);// 三个玩家

    private boolean mWarnedLessCards = false;

    private boolean mIsRobotControl = false;

    private ServerMessage mLastServerOrder;

    // ---------------------------------------------

    private AiContext mAiContext;
    private EmotionAi mEmotionAi;
    private ChatAi mChatAi;

    private GameResult mGameResult;

    // ---------------------------------------------

    private boolean mDestoryed = false;

    private Runnable mRunHideLeftText = new Runnable() {

        @Override
        public void run() {
            if (mDestoryed)
                return;
            update(UI_HIDE_LEFT_TEXT);
        }
    };

    private Runnable mRunHideRightText = new Runnable() {

        @Override
        public void run() {
            if (mDestoryed)
                return;
            update(UI_HIDE_RIGHT_TEXT);
        }
    };

    private Runnable mRunHideSelfText = new Runnable() {

        @Override
        public void run() {
            if (mDestoryed)
                return;
            update(UI_HIDE_SELF_TEXT);
        }
    };

    private Runnable mRunStartGame = new Runnable() {

        @Override
        public void run() {
            if (mDestoryed)
                return;
            //
            int playerId = mPlayerContext.mInfoMyself.mSelfPlayerId;
            ClientReady cMsg = new ClientReady(playerId);
            mGameServer.send(cMsg);
        }
    };

    private OnPreShowCardListener mOnPreShowCardListener = new OnPreShowCardListener() {

        @Override
        public void onPreShowCardChanged(Player p, int[] cardIds) {
            int playerId = p.getPlayerId();
            if (mPlayerContext.isPlayerSelf(playerId)) {
                mSelfShowCards.refreshView();
            } else if (mPlayerContext.isPlayerLeft(playerId)) {
                mLeftShowCards.refreshView();
            } else {
                mRightShowCards.refreshView();
            }
        }
    };

    private static final int ALLOC_CARD_PEROID = 100;

    private Runnable mRunAllocCards = new AllocCardsRunnable();

    private int mTipsIndex;

    private class AllocCardsRunnable implements Runnable, OnRepeatListener {

        private int mCardNum;

        private int[] mAllocCardIds;

        private boolean mGoonRepeat;

        private boolean mIsFinished;

        public synchronized void setAllocCardIds(int[] allocCardIds) {
            if (allocCardIds != null) {
                mAllocCardIds = allocCardIds;
                mGoonRepeat = true;
                mIsFinished = false;
            } else {
                mAllocCardIds = null;
                mGoonRepeat = false;
                mIsFinished = true;
            }
            mCardNum = 0;
        }

        public synchronized boolean isFinished() {
            return mIsFinished;
        }

        @Override
        public void run() {

            if (mDestoryed) {
                mGoonRepeat = false;
                return;
            }

            int len = mAllocCardIds.length;
            mCardNum++;
            if (mCardNum > 0 && mCardNum <= len) {
                mGoonRepeat = true;
                mHandler.postDelayed(this, ALLOC_CARD_PEROID);

                synchronized (this) {
                    int[] tempCardIds = new int[mCardNum];
                    for (int j = 0; j < mCardNum; j++) {
                        tempCardIds[j] = mAllocCardIds[j];
                    }

                    updateSelfCardIds(tempCardIds, false);
                }

            } else if (mCardNum < (len + 10)) {
                mGoonRepeat = false;
                mHandler.postDelayed(this, ALLOC_CARD_PEROID);
            } else { // end
                mGoonRepeat = false;
                CardUtil.sortDescending(mAllocCardIds);
                OfflineGameActivity.this.updateSelfCardIds(mAllocCardIds, true);

                this.setAllocCardIds(null);

                // runPlsDeclare();
                sendClientGotCards();
            }

        }

        @Override
        public boolean goonRepeat() {
            return mGoonRepeat;
        }

    }

    // ---------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_offline_game);

        mPlayerContext = new PlayerContext();
        mPlayerContext.initRound();
        mPlayerContext.resetWinLoseNum();

        initAi();
        initView();
        initAnim();
        initAvatar();
        initHandler();

        resetRoundScore();
        showMatchRecord();

        mGameServer = new LocalServer();
        mGameServer.setOnGameEventListener(this);
        mGameServer.connect();

        signIn();
        
     
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SoundSwitch.isMusicOn()){
            playBgMusic();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(SoundSwitch.isMusicOn()){
            stopBgMusic();
         }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mGameBgView.onStart();
        mGameTable.setImageResource(R.drawable.game_table);

        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                mLeftAvatar.onStart();
            }
        }, 100);

        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                mRightAvatar.onStart();
            }
        }, 100);

    }

    @Override
    protected void onStop() {
        super.onStop();

        mGameBgView.onStop();
        mGameTable.setImageResource(0);
        mLeftAvatar.onStop();
        mRightAvatar.onStop();

        System.gc();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDestoryed = true;

        SoundUtil.getSingleton().stopAndReleaseAll();
        
        if(SoundSwitch.isMusicOn()){
            playBgMusic(R.raw.bgm_soft);
        }

        removeAi();

        signOut();

        clearHandler();

        releaseGameServer();

        releaseAnimation();
        releaseViews();

        mOnPreShowCardListener = null;
        mRunStartGame = null;

        CardsBitmap.clearAll();
        CardFgsBitmap.clearAll();

        System.gc();
    }

    private void releaseGameServer() {
        mPlayerContext.reset();

        mGameServer.disconnect();
        mGameServer.setOnGameEventListener(null);
    }

    private void releaseViews() {

        mGameTopView.setOnBtnListener(null);
        mLeftAvatar.onDestroy();
        mRightAvatar.onDestroy();
        mGameBgView.onDestroy();
        mLordCupViewHolder.onDestroy();

        mGameTopView = null;
        mLeftAvatar = null;
        mRightAvatar = null;
        mGameBgView = null;
        mLordCupViewHolder = null;
    }

    private void releaseAnimation() {
        mSingleDragonAnimation.onDestroy();
        mDoubleDragonAnimation.onDestroy();
        mRocketAnimation.onDestroy();
        mPlaneAnimation.onDestroy();
        mBombAnimation.onDestroy();

        for (TextAnimation ta : mPlusAnims) {
            ta.onDestroy();
        }

        mSingleDragonAnimation = null;
        mDoubleDragonAnimation = null;
        mRocketAnimation = null;
        mPlaneAnimation = null;
        mBombAnimation = null;

        mPlusAnims.clear();
    }

    private void clearHandler() {
        mHandler.removeCallbacks(mRunHideLeftText);
        mHandler.removeCallbacks(mRunHideRightText);
        mHandler.removeCallbacks(mRunStartGame);
        mHandler.removeCallbacks(mRunAllocCards);

        mRunHideLeftText = null;
        mRunHideRightText = null;
        mRunStartGame = null;
        mRunAllocCards = null;
    }

    private void initAi() {
        mAiContext = new AiContext(mPlayerContext);

        mEmotionAi = new EmotionAi();
        mEmotionAi.setOnEmotionListener(new OnEmotionListener() {

            @Override
            public void onCry(int playerId) {
                if (mPlayerContext.isPlayerRight(playerId)) { // right
                    mRightAvatar.onCry();
                } else if (mPlayerContext.isPlayerLeft(playerId)) {
                    mLeftAvatar.onCry();
                }
            }

            @Override
            public void onCute(int playerId) {
                if (mPlayerContext.isPlayerRight(playerId)) { // right
                    mRightAvatar.onCute();
                } else if (mPlayerContext.isPlayerLeft(playerId)) {
                    mLeftAvatar.onCute();
                }
            }

            @Override
            public void onLaugh(int playerId) {
                if (mPlayerContext.isPlayerRight(playerId)) { // right
                    mRightAvatar.onLaugh();
                } else if (mPlayerContext.isPlayerLeft(playerId)) {
                    mLeftAvatar.onLaugh();
                }
            }

            @Override
            public void onLuck(int playerId) {
                if (mPlayerContext.isPlayerRight(playerId)) { // right
                    mRightAvatar.onLuck();
                } else if (mPlayerContext.isPlayerLeft(playerId)) {
                    mLeftAvatar.onLuck();
                }
            }

            @Override
            public void onProud(int playerId) {
                if (mPlayerContext.isPlayerRight(playerId)) { // right
                    mRightAvatar.onProud();
                } else if (mPlayerContext.isPlayerLeft(playerId)) {
                    mLeftAvatar.onProud();
                }
            }

            @Override
            public void onAnger(int playerId) {
                if (mPlayerContext.isPlayerRight(playerId)) { // right
                    mRightAvatar.onAnger();
                } else if (mPlayerContext.isPlayerLeft(playerId)) {
                    mLeftAvatar.onAnger();
                }
            }

        });

        mChatAi = new ChatAi(mPlayerContext);
        mChatAi.setOnChatListener(new OnChatListener() {

            @Override
            public void onChat(int playerId, String chat) {
                if (mPlayerContext.isPlayerRight(playerId)) { // right
                    showRightText(chat);
                } else if (mPlayerContext.isPlayerLeft(playerId)) {
                    showLeftText(chat);
                }
            }

        });

        mAiContext.addListener(mEmotionAi);
        mAiContext.addListener(mChatAi);
    }

    private void removeAi() {
        mAiContext.removeListener(mEmotionAi);
        mAiContext.removeListener(mChatAi);
    }

    private void initView() {

        mGameBgView = (LocalGameBgView) findViewById(R.id.game_bg);
        mGameTable = (ImageView) findViewById(R.id.game_table);

        mMatchRecord = (TextView) findViewById(R.id.match_record);
        mRoundScore = (TextView) findViewById(R.id.round_score);

        mLeftText = (TextView) findViewById(R.id.left_text);
        mLeftText.setVisibility(View.INVISIBLE);
        mRightText = (TextView) findViewById(R.id.right_text);
        mRightText.setVisibility(View.INVISIBLE);
        mSelfText = (TextView) findViewById(R.id.self_text);
        mSelfText.setVisibility(View.INVISIBLE);

        mDeclareLord0 = findViewById(R.id.buyaodizhu);
        mDeclareLord1 = findViewById(R.id.yifen);
        mDeclareLord2 = findViewById(R.id.erfen);
        mDeclareLord3 = findViewById(R.id.sanfen);

        mSelfCardsView = (SelfCardsView) findViewById(R.id.self_cards_view);

        mLeftShowCards = (ShowCardsView) findViewById(R.id.left_show_cards);
        mLeftShowCards.setWrapCards(true);
        mRightShowCards = (ShowCardsView) findViewById(R.id.right_show_cards);
        mRightShowCards.setWrapCards(true);
        mSelfShowCards = (ShowCardsView) findViewById(R.id.self_show_cards);
        mSelfShowCards.setWrapCards(true);

        mLeftShowCards.setName("LEFT ");
        mRightShowCards.setName("RIGHT ");
        mSelfShowCards.setName("SELF ");

        mGameTopView = (LocalGameTopView) findViewById(R.id.local_game_top_view);
        mGameTopView.setOnBtnListener(this);

        mLeftCardNum = (TextView) findViewById(R.id.left_card_number);
        mRightCardNum = (TextView) findViewById(R.id.right_card_number);

        mGameButtons = (LinearLayout) findViewById(R.id.gamebuttons);
        findViewById(R.id.chupai).setOnClickListener(this);
        findViewById(R.id.tishi).setOnClickListener(this);
        mBuYaoGameBtn = findViewById(R.id.buyao);
        mBuYaoGameBtn.setOnClickListener(this);

        mDeclareLordButtons = (LinearLayout) findViewById(R.id.declare_lord_btns);
        findViewById(R.id.yifen).setOnClickListener(this);
        findViewById(R.id.erfen).setOnClickListener(this);
        findViewById(R.id.sanfen).setOnClickListener(this);
        findViewById(R.id.buyaodizhu).setOnClickListener(this);

        mRobLordButtons = (LinearLayout) findViewById(R.id.rob_lord_btns);
        findViewById(R.id.rob_lord_btn).setOnClickListener(this);
        findViewById(R.id.rob_pass_btn).setOnClickListener(this);

        mLordCupViewHolder = new LordCupViewHolder();
        mLordCupViewHolder.mSelfLordCup = (ImageView) findViewById(R.id.self_lord_cup);
        mLordCupViewHolder.mSelfLordCupAnim = new FrameAnim(mLordCupViewHolder.mSelfLordCup);

        mLordCupViewHolder.mLeftLordLayout = findViewById(R.id.left_lord_layout);
        mLordCupViewHolder.mLeftLordArrow = (ImageView) findViewById(R.id.left_lord_arrow);
        mLordCupViewHolder.mLeftLordCup = (ImageView) findViewById(R.id.left_lord_cup);
        mLordCupViewHolder.mLeftLordCupAnim = new FrameAnim(mLordCupViewHolder.mLeftLordCup);

        mLordCupViewHolder.mRightLordLayout = findViewById(R.id.right_lord_layout);
        mLordCupViewHolder.mRightLordArrow = (ImageView) findViewById(R.id.right_lord_arrow);
        mLordCupViewHolder.mRightLordCup = (ImageView) findViewById(R.id.right_lord_cup);
        mLordCupViewHolder.mRightLordCupAnim = new FrameAnim(mLordCupViewHolder.mRightLordCup);
    }

    private void initAnim() {
        mAnimImage = (ImageView) findViewById(R.id.anim_image);
        mSingleDragonAnimation = new SingleDragonAnimation(mAnimImage);
        mDoubleDragonAnimation = new DoubleDragonAnimation(mAnimImage);

        mBombAnimImage = (ImageView) findViewById(R.id.bomb_anim);
        mBombAnimation = new BombAnimation(mBombAnimImage);

        mRocketTweenView = findViewById(R.id.rocket_tween);
        mRocketStillImage = (ImageView) findViewById(R.id.rocket_still);
        mRocketFrameImage = (ImageView) findViewById(R.id.rocket_frame);
        mRocketAnimation = new RocketAnimation(this, mRocketTweenView, mRocketFrameImage,
                mRocketStillImage);

        mPlaneTweenView = findViewById(R.id.plane_tween);
        mPlaneStillImage = (ImageView) findViewById(R.id.plane_still);
        mPlaneFrameImage = (ImageView) findViewById(R.id.plane_frame);
        mPlaneAnimation = new PlaneAnimation(this, mPlaneTweenView, mPlaneFrameImage,
                mPlaneStillImage);

        mPlusAnims = new ArrayList<TextAnimation>();
        mPlusAnims.add(new TextAnimation(this, (TextView) findViewById(R.id.my_plus)));
        mPlusAnims.add(new TextAnimation(this, (TextView) findViewById(R.id.right_plus)));
        mPlusAnims.add(new TextAnimation(this, (TextView) findViewById(R.id.left_plus)));
    }

    private void initAvatar() {
        mLeftAvatar = (LocalAvatar) findViewById(R.id.left_avatar);
        mRightAvatar = (LocalAvatar) findViewById(R.id.right_avatar);

        ArrayList<Integer> selectList = (ArrayList<Integer>) getIntent().getIntegerArrayListExtra(
                SELECTED_LIST);
        if (selectList.size() == 2) {

            int leftBeautyId = selectList.get(0);
            int rightBeautyId = selectList.get(1);

            mLeftAvatar.setBeautyId(leftBeautyId);
            mRightAvatar.setBeautyId(rightBeautyId);

            mLeftName = getString(getBeautyNameResId(leftBeautyId));
            mRightName = getString(getBeautyNameResId(rightBeautyId));

            TextView leftNameView = (TextView) findViewById(R.id.left_player_name);
            TextView rightNameView = (TextView) findViewById(R.id.right_player_name);
            leftNameView.setText(mLeftName);
            rightNameView.setText(mRightName);

            mPlayerContext.mInfoMyself.mLeftBeautyId = leftBeautyId;
            mPlayerContext.mInfoMyself.mRightBeautyId = rightBeautyId;

        } else {
            finish();
        }
    }

    // 珍妮、艾莉、小曼、安娜
    private int getBeautyNameResId(int selectListIndex) {
        switch (selectListIndex) {
            case 0:
                return R.string.beauty1_name;
            case 1:
                return R.string.beauty2_name;
            case 2:
                return R.string.beauty3_name;
            case 3:
                return R.string.beauty4_name;
            default:
                return 0;
        }
    }

    private void initHandler() {
        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                if (mDestoryed)
                    return;

                switch (msg.what) {
                    case UI_HIDE_ALL_BTN: // 隐藏全部按钮
                        mGameButtons.setVisibility(View.GONE);
                        mDeclareLordButtons.setVisibility(View.GONE);
                        mRobLordButtons.setVisibility(View.GONE);
                        break;
                    case UI_HIDE_DECLARE_LORD_BTN: // 隐藏叫地主按钮
                        mDeclareLordButtons.setVisibility(View.GONE);
                        break;
                    case UI_SHOW_ROB_LORD_BTN:
                        mRobLordButtons.setVisibility(View.VISIBLE);
                        break;
                    case UI_HIDE_ROB_LORD_BTN:
                        mRobLordButtons.setVisibility(View.GONE);
                        break;
                    case UI_SHOW_BOTTOM_CARDS: {
                        BottomCardsView bc = mGameTopView.bottomCardsView();
                        bc.setBottomCardIds(mPlayerContext.getBottomCardIds());
                        bc.setRateAndBase(mPlayerContext.getRate(), mPlayerContext.getDeclareNum());
                    }
                        break;
                    case UI_HIDE_BOTTOM_CARDS: {
                        BottomCardsView bc = mGameTopView.bottomCardsView();
                        bc.reset();
                    }
                        break;
                    case UI_SHOW_START_BTN_ONLY: // 显示开始按钮
                        mGameButtons.setVisibility(View.GONE);
                        mDeclareLordButtons.setVisibility(View.GONE);
                        break;
                    case UI_SHOW_DECLARE_LORD_BTN_ONLY: // 显示叫地主按钮
                        mGameButtons.setVisibility(View.GONE);
                        mDeclareLordButtons.setVisibility(View.VISIBLE);
                        int declareNum = mPlayerContext.mInfoCommon.mDeclareNum;
                        if (declareNum == 3) {
                            mDeclareLord0.setVisibility(View.VISIBLE);
                            mDeclareLord1.setVisibility(View.GONE);
                            mDeclareLord2.setVisibility(View.GONE);
                            mDeclareLord3.setVisibility(View.GONE);
                        } else if (declareNum == 2) {
                            mDeclareLord0.setVisibility(View.VISIBLE);
                            mDeclareLord1.setVisibility(View.GONE);
                            mDeclareLord2.setVisibility(View.GONE);
                            mDeclareLord3.setVisibility(View.VISIBLE);
                        } else if (declareNum == 1) {
                            mDeclareLord0.setVisibility(View.VISIBLE);
                            mDeclareLord1.setVisibility(View.GONE);
                            mDeclareLord2.setVisibility(View.VISIBLE);
                            mDeclareLord3.setVisibility(View.VISIBLE);
                        } else {
                            mDeclareLord0.setVisibility(View.VISIBLE);
                            mDeclareLord1.setVisibility(View.VISIBLE);
                            mDeclareLord2.setVisibility(View.VISIBLE);
                            mDeclareLord3.setVisibility(View.VISIBLE);
                        }

                        break;
                    case UI_SHOW_FIRST_SHOW_GAME_BTN: {
                        mBuYaoGameBtn.setVisibility(View.GONE);
                        mGameButtons.setVisibility(View.VISIBLE);
                    }
                        break;
                    case UI_SHOW_FOLLOW_GAME_BTN: {
                        mBuYaoGameBtn.setVisibility(View.VISIBLE);
                        mGameButtons.setVisibility(View.VISIBLE);
                    }
                        break;
                    case UI_HIDE_GAME_BTN:// 隐藏游戏按钮
                        mGameButtons.setVisibility(View.GONE);
                        break;
                    case UI_SHOW_BOMB_ANIM: {
                        mBombAnimation.updateFrameAnimRes(R.anim.bomb_frame);
                        mBombAnimation.onStart();
                    }
                        break;
                    case UI_SHOW_PLANE_ANIM: {
                        mPlaneAnimation.updateFrameAnimRes(R.anim.plane_frame);
                        mPlaneAnimation.onStart();
                    }
                        break;
                    case UI_SHOW_ROCKET_ANIM: {
                        mRocketAnimation.updateFrameAnimRes(R.anim.rocket_frame);
                        mRocketAnimation.onStart();
                    }
                        break;
                    case UI_SHOW_DOUBLE_DRAGON_ANIM: {
                        mDoubleDragonAnimation.updateFrameAnimRes(R.anim.double_dragon_frame);
                        mDoubleDragonAnimation.onStart();
                    }
                        break;
                    case UI_SHOW_SINGLE_DRAGON_ANIM: {
                        mSingleDragonAnimation.updateFrameAnimRes(R.anim.single_dragon_frame);
                        mSingleDragonAnimation.onStart();
                    }
                        break;
                    case UI_SHOW_LEFT_PLUS_ANIM:
                        mPlusAnims.get(2).setText((String) msg.obj);
                        mPlusAnims.get(2).onStart();
                        break;
                    case UI_SHOW_RIGHT_PLUS_ANIM:
                        mPlusAnims.get(1).setText((String) msg.obj);
                        mPlusAnims.get(1).onStart();
                        break;
                    case UI_SHOW_SELF_PLUS_ANIM:
                        mPlusAnims.get(0).setText((String) msg.obj);
                        mPlusAnims.get(0).onStart();
                        break;
                    case UI_RESET_LORD_CUP:
                        mLordCupViewHolder.reset();
                        break;
                    case UI_UPDATE_LORD_CUP:
                        mLordCupViewHolder.updateLordCupViews(mPlayerContext);
                        break;
                    case UI_SHOW_RIGHT_TEXT:
                        mRightText.setVisibility(View.VISIBLE);
                        mRightText.setText((String) msg.obj);
                        break;
                    case UI_HIDE_RIGHT_TEXT:
                        mRightText.setVisibility(View.INVISIBLE);
                        break;
                    case UI_SHOW_LEFT_TEXT:
                        mLeftText.setVisibility(View.VISIBLE);
                        mLeftText.setText((String) msg.obj);
                        break;
                    case UI_HIDE_LEFT_TEXT:
                        mLeftText.setVisibility(View.INVISIBLE);
                        break;
                    case UI_SHOW_SELF_TEXT:
                        mSelfText.setVisibility(View.VISIBLE);
                        mSelfText.setText((String) msg.obj);
                        break;
                    case UI_HIDE_SELF_TEXT:
                        mSelfText.setVisibility(View.INVISIBLE);
                        break;
                    case UI_UPDATE_LEFT_CARD_NUM: {
                        int prevNum = mPlayerContext.prevPlayerLeftCardNum();
                        if (prevNum >= 0) {
                            mLeftCardNum.setText(getString(R.string.cardnum, prevNum));
                            mLeftCardNum.setVisibility(View.VISIBLE);
                        } else {
                            mLeftCardNum.setText(null);
                            mLeftCardNum.setVisibility(View.INVISIBLE);
                        }
                    }
                        break;
                    // case UI_HIDE_LEFT_CARD_NUM: {
                    // mLeftCardNum.setVisibility(View.GONE);
                    // }
                    // break;
                    case UI_UPDATE_RIGHT_CARD_NUM: {
                        int nextNum = mPlayerContext.nextPlayerLeftCardNum();
                        if (nextNum >= 0) {
                            mRightCardNum.setText(getString(R.string.cardnum, nextNum));
                            mRightCardNum.setVisibility(View.VISIBLE);
                        } else {
                            mRightCardNum.setText(null);
                            mRightCardNum.setVisibility(View.INVISIBLE);
                        }
                    }
                        break;
                    // case UI_HIDE_RIGHT_CARD_NUM: {
                    // mRightCardNum.setVisibility(View.GONE);
                    // }
                    // break;
                    case UI_UPDATE_SELF_CARDS: {
                        mSelfCardsView.refreshView();
                    }
                        break;

                }
            }

        };
    }

    private void clearPlayers() {
        mPlayers.clear();
    }

    /***
     * playerId和位置 2(prev left) 1(next right) 0(self)
     */
    private void updatePlayers() {
        int selfPlayerId = mPlayerContext.mInfoMyself.mSelfPlayerId;
        int nextPlayerId = mPlayerContext.mInfoMyself.mRightPlayerId;
        int prevPlayerId = mPlayerContext.mInfoMyself.mLeftPlayerId;

        int selfSex = mPlayerContext.mInfoMyself.mSelfPlayerSex;
        int nextSex = mPlayerContext.mInfoMyself.mNextPlayerSex;
        int prevSex = mPlayerContext.mInfoMyself.mPrevPlayerSex;

        mPlayers.clear();

        Player p0 = new Player(selfPlayerId, selfSex);
        Player p1 = new Player(nextPlayerId, nextSex);
        Player p2 = new Player(prevPlayerId, prevSex);

        LogUtil.d(TAG, "0 selfPlayerId=" + selfPlayerId);
        LogUtil.d(TAG, "1 nextPlayerId=" + nextPlayerId);
        LogUtil.d(TAG, "2 prevPlayerId=" + prevPlayerId);

        mPlayers.add(0, p0);
        mPlayers.add(1, p1);
        mPlayers.add(2, p2);

        mSelfCardsView.setPlayerContext(mPlayerContext);
        mSelfCardsView.setPlayer(p0);

        mSelfShowCards.setPlayer(p0);
        p0.setOnPreShowCardListener(mOnPreShowCardListener);
        mRightShowCards.setPlayer(p1);
        p1.setOnPreShowCardListener(mOnPreShowCardListener);
        mLeftShowCards.setPlayer(p2);
        p2.setOnPreShowCardListener(mOnPreShowCardListener);
    }

    private void update(int what) {
        mHandler.sendEmptyMessage(what);
    }

    private void showLeftText(String text) {
        mHandler.sendMessage(mHandler.obtainMessage(UI_SHOW_LEFT_TEXT, text));
        mHandler.removeCallbacks(mRunHideLeftText);
        mHandler.postDelayed(mRunHideLeftText, TEXT_SHOW_PEROID);
    }

    private void showRightText(String text) {
        mHandler.sendMessage(mHandler.obtainMessage(UI_SHOW_RIGHT_TEXT, text));
        mHandler.removeCallbacks(mRunHideRightText);
        mHandler.postDelayed(mRunHideRightText, TEXT_SHOW_PEROID);
    }

    private void showSelfText(String text) {
        mHandler.sendMessage(mHandler.obtainMessage(UI_SHOW_SELF_TEXT, text));
        mHandler.removeCallbacks(mRunHideSelfText);
        mHandler.postDelayed(mRunHideSelfText, TEXT_SHOW_PEROID);
    }

    private void showTextAnim(int what, String text) {
        mHandler.sendMessage(mHandler.obtainMessage(what, text));
    }

    private void signIn() {
        LogUtil.d(TAG, "sendSignIn");
        ClientSignIn signIn = new ClientSignIn(1);
        mGameServer.send(signIn);
    }

    private void signOut() {
        LogUtil.d(TAG, "sendSignOut");
        ClientMessage signOut = new ClientSignOut();
        mGameServer.send(signOut);
    }

    private void startGame() {
        mHandler.postDelayed(mRunStartGame, 300);
    }

    private void goonGame() {
        int playerId = mPlayerContext.getSelfPlayerId();
        ClientGoon cMsg = new ClientGoon(playerId);
        mGameServer.send(cMsg);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buyaodizhu: {
                int playerId = mPlayerContext.getSelfPlayerId();
                ClientMessage cMsg = new ClientDeclareLord(playerId, 0);
                mGameServer.send(cMsg);

                update(UI_HIDE_DECLARE_LORD_BTN);
            }
                break;
            case R.id.sanfen: {
                int playerId = mPlayerContext.getSelfPlayerId();
                ClientMessage cMsg = new ClientDeclareLord(playerId, 3);
                mGameServer.send(cMsg);

                update(UI_HIDE_DECLARE_LORD_BTN);
            }
                break;
            case R.id.erfen: {
                int playerId = mPlayerContext.getSelfPlayerId();
                ClientMessage cMsg = new ClientDeclareLord(playerId, 2);
                mGameServer.send(cMsg);

                update(UI_HIDE_DECLARE_LORD_BTN);
            }
                break;
            case R.id.yifen: {
                int playerId = mPlayerContext.getSelfPlayerId();
                ClientMessage cMsg = new ClientDeclareLord(playerId, 1);
                mGameServer.send(cMsg);

                update(UI_HIDE_DECLARE_LORD_BTN);
            }
                break;
            case R.id.rob_lord_btn: {
                LogUtil.d(TAG, "R.id.rob_lord_btn");

                int playerId = mPlayerContext.getSelfPlayerId();
                ClientMessage cMsg = new ClientRobLord(playerId, true);
                mGameServer.send(cMsg);

                update(UI_HIDE_ROB_LORD_BTN);
            }
                break;
            case R.id.rob_pass_btn: {
                LogUtil.d(TAG, "R.id.rob_pass_btn");

                int playerId = mPlayerContext.getSelfPlayerId();
                ClientMessage cMsg = new ClientRobLord(playerId, false);
                mGameServer.send(cMsg);

                update(UI_HIDE_ROB_LORD_BTN);
            }
                break;

            case R.id.back: {
                leaveGame();
            }
                break;
            case R.id.chupai: {
                int[] showCardIds = mPlayerContext.selectedCardIds();
                boolean isFollow = mPlayerContext.isThinkingFollowCard();
                CardUtil.printCards("showCardIds: ", showCardIds);
                if (showCardIds != null) {
                    int playerId = mPlayerContext.getSelfPlayerId();
                    ClientMessage cMsg = new ClientShowCards(playerId, showCardIds, isFollow);
                    mGameServer.send(cMsg);
                    mTipsIndex = 0;

                    update(UI_HIDE_GAME_BTN);
                }
            }
                break;
            case R.id.tishi: {
                int[] suggestCardIds = null;
                ArrayList<int[]> tipsArray = null;

                if (mPlayerContext.isThinkingShowCard()) {
                    suggestCardIds = getTishiRobot().suggestFirstShow();
                } else if (mPlayerContext.isThinkingFollowCard()) {
                    tipsArray = CardHelperHandler.getCardHelper(mPlayerContext);
                }

                mPlayerContext.resetCardIdsSelect();
                if (suggestCardIds != null) {
                    mPlayerContext.setCardIdsSelect(suggestCardIds);
                } else if (tipsArray != null) {
                    mTipsIndex %= tipsArray.size();
                    mPlayerContext.setCardIdsSelect(tipsArray.get(mTipsIndex));
                    mTipsIndex++;
                } else {
                    ToastUtil.show(R.string.no_tips_available);

                    // // 过牌
                    // ClientMessage cMsg = new ClientShowPass(
                    // mPlayerContext.getSelfPlayerId());
                    // mGameServer.send(cMsg);
                }

                update(UI_UPDATE_SELF_CARDS);
            }
                break;
            case R.id.buyao: {
                if (mPlayerContext.isThinkingFollowCard()) {
                    int playerId = mPlayerContext.getSelfPlayerId();
                    ClientMessage cMsg = new ClientShowPass(playerId);
                    mGameServer.send(cMsg);
                    mTipsIndex = 0;
                    update(UI_HIDE_GAME_BTN);
                }
            }
                break;
            default:
                break;
        }
    }

    @Override
    public void onGameStart(boolean success) {
        LogUtil.d(TAG, "onGameStart: " + success);
        // if (success) {
        // //
        // } else {
        // // ToastUtil.show(R.string.cannot_connect_to_srv);
        //
        // finish();
        // }
    }

    @Override
    public void onGameOver() {
        LogUtil.d(TAG, "onGameOver");
        //
        // if (mUserPlaying) {
        // // ToastUtil.show(R.string.game_offline);
        // finish();
        // }
    }

    @Override
    public void onMessageSent(ClientMessage cMsg) {
        LogUtil.d(TAG, "onMessageSent: " + cMsg.getMsgType());
    }

    @Override
    public void onMessageReceived(ServerMessage sMsg) {
        LogUtil.d(TAG, "onMessageReceived: " + sMsg.getMsgType());

        if (mDestoryed) {
            return;
        }

        switch (sMsg.getMsgType()) {
            case SRV_WAIT_ROOM: {
                LogUtil.d(TAG, "SRV_WAIT_ROOM");
                // ServerWaitRoom msg = (ServerWaitRoom) sMsg;
            }
                break;
            case SRV_ROOM_INFO:
                onSrvRoomInfo(sMsg);
                break;
            case SRV_PLS_READY:
                onSrvPlsReady(sMsg);
                break;
            case SRV_PLAYER_READY:
                onSrvPlayerReady(sMsg);
                break;
            case SRV_ALLOC_CARDS:
                onSrvAllocCards(sMsg);
                break;
            case SRV_PLS_DECLARE:
                onSrvPlsDeclare(sMsg);
                break;
            case SRV_PLS_ROB:
                onSrvPlsRob(sMsg);
                break;
            case SRV_LAST_ROB:
                onSrvLastRob(sMsg);
                break;
            case SRV_LAST_DECLARE:
                onSrvLastDeclare(sMsg);
                break;
            case SRV_DECLARE_RESULT:
                onSrvDeclareResult(sMsg);
                break;
            case SRV_PLS_SHOW:
                onSrvPlsShow(sMsg);
                break;
            case SRV_PLS_FOLLOW:
                onSrvPlsFollow(sMsg);
                break;
            case SRV_UPDATE_RATE:
                onSrvUpdateRate(sMsg);
                break;
            case SRV_UPDATE_PLUS:
                onSrvUpdatePlus(sMsg);
                break;
            case SRV_ROBOT:
                onSrvRobot(sMsg);
                break;
            case SRV_SHOW_RESULT:
                onSrvShowResult(sMsg);
                break;
            case SRV_GAME_RESULT:
                onSrvGameResult(sMsg);
                break;
            case SRV_HEART_BEAT:
                LogUtil.d(TAG, "SRV_HEART_BEAT");
                break;
            case SRV_TIME_OUT:
                LogUtil.d(TAG, "SRV_TIME_OUT");
                break;
            default:
                break;
        }
    }

    private void onSrvRoomInfo(ServerMessage sMsg) {
        LogUtil.d(TAG, "SRV_ROOM_INFO");
        ServerRoomInfo msg = (ServerRoomInfo) sMsg;
        int selfPlayerId = msg.getMyPlayerId();
        String[] nickNames = msg.getNickNames();
        int[] sexs = msg.getSexs();
        long roomId = msg.getRoomId();

        for (int i = 0; i < nickNames.length; i++) {
            LogUtil.d(TAG, "nickname=" + nickNames[i]);
        }

        LogUtil.d(TAG, "roomId=" + roomId);

        clearPlayers();

        mPlayerContext.reset();

        int rightPlayerId = (selfPlayerId + 1 + Constants.PLAYER_NUM)
                % Constants.PLAYER_NUM;
        int leftPlayerId = (selfPlayerId - 1 + Constants.PLAYER_NUM)
                % Constants.PLAYER_NUM;

        mPlayerContext.setSelfPlayerId(selfPlayerId);
        mPlayerContext.setRightPlayerId(rightPlayerId);
        mPlayerContext.setLeftPlayerId(leftPlayerId);

        mPlayerContext.mInfoCommon.mNickNames = nickNames;
        mPlayerContext.mInfoMyself.mSelfNickName = nickNames[selfPlayerId];
        // 单机版从本地读取
        mPlayerContext.mInfoCommon.mNickNames[leftPlayerId] = mLeftName;
        mPlayerContext.mInfoCommon.mNickNames[rightPlayerId] = mRightName;

        // 设置性别信息
        mPlayerContext.mInfoMyself.mSelfPlayerSex = sexs[selfPlayerId];
        mPlayerContext.mInfoMyself.mNextPlayerSex = sexs[rightPlayerId];
        mPlayerContext.mInfoMyself.mPrevPlayerSex = sexs[leftPlayerId];

        mLeftAvatar.onDefault();
        mRightAvatar.onDefault();

        if (Constants.DEBUG_OFFLINE_GAME_USE_ROBOT) {
            letRobotControl();
        }
    }

    private void onSrvPlsReady(ServerMessage sMsg) {
        LogUtil.d(TAG, "SRV_PLS_READY");
        // ServerPlsReady msg = (ServerPlsReady) sMsg;
        // long timeLeft = msg.getTimeLeft();

        updatePlayers();

        update(UI_HIDE_BOTTOM_CARDS);
        update(UI_RESET_LORD_CUP);
        update(UI_HIDE_ALL_BTN);

        playBgMusic(R.raw.bgm_dynamic);
        mWarnedLessCards = false;

        for (int i = 0; i < Constants.PLAYER_NUM; i++) {
            mPlayers.get(i).clearPreShowCardIds();
        }

        mPlayerContext.mInfoCommon.mCurrShowPlayerId = mPlayerContext.mInfoMyself.mSelfPlayerId;

        mPlayerContext.mInfoCommon.resetLeftCardNum();
        mPlayerContext.mInfoCommon.mRate = -1;
        mPlayerContext.mInfoCommon.mDeclareNum = -1;

        startGame();

        onLoadSensitiveRes();

        if (!mIsRobotControl) {
            update(UI_SHOW_START_BTN_ONLY);
        } else {
            mLastServerOrder = sMsg;
        }
    }

    private void onSrvPlayerReady(ServerMessage sMsg) {
        LogUtil.d(TAG, "SRV_PLAYER_READY");
        ServerPlayerReady msg = (ServerPlayerReady) sMsg;
        int playerId = msg.getPlayerId();

        int myPlayerId = mPlayerContext.mInfoMyself.mSelfPlayerId;

        if (playerId == myPlayerId) {
            // TODO 修改我的准备状态
        }
    }

    private void onSrvAllocCards(ServerMessage sMsg) {
        LogUtil.d(TAG, "SRV_ALLOC_CARDS");
        ServerAllocCards msg = (ServerAllocCards) sMsg;
        int playerId = msg.getPlayerId();
        int[] allocCardIds = msg.getAllocCardIds();

        // 更新数据
        if (playerId == mPlayerContext.mInfoMyself.mSelfPlayerId) {
            playAllocCardAnim(allocCardIds);
        }
    }

    private void playAllocCardAnim(int[] allocCardIds) {
        AllocCardsRunnable allocCardsRunnable = (AllocCardsRunnable) mRunAllocCards;
        allocCardsRunnable.setAllocCardIds(allocCardIds);

        mHandler.post(mRunAllocCards);

        GameSoundUtil.playAllocCardsRepeat((OnRepeatListener) mRunAllocCards);
    }

    private void updateSelfCardIds(int[] selfCardIds, boolean playSound) {
        if (selfCardIds != null && selfCardIds.length > 0) {
            int cardsNum = selfCardIds.length;
            mPlayerContext.setSelfCardIds(selfCardIds);
            mPlayerContext.setSelfCardSelects(new boolean[cardsNum]);
            mPlayerContext.setSelfCardTouches(new boolean[cardsNum]);
            for (int i = 0; i < cardsNum; i++) {
                mPlayerContext.mInfoMyself.mSelects[i] = false;
                mPlayerContext.mInfoMyself.mToucheds[i] = false;
            }

            // CardUtil.printCards("selfCardIds: ", selfCardIds);

            // 修改界面参数
            UiConstants.updateSelfCardsGap(selfCardIds);
            update(UI_UPDATE_SELF_CARDS);

            if (playSound)
                GameSoundUtil.playAllocCards();
        } else {
            mPlayerContext.setSelfCardIds(null);
            update(UI_UPDATE_SELF_CARDS);
        }
    }

    private Runnable mRunPlsDeclare;

    private void onSrvPlsDeclare(ServerMessage sMsg) {
        LogUtil.d(TAG, "SRV_PLS_DECLARE");
        final ServerPlsDeclare msg = (ServerPlsDeclare) sMsg;
        final int playerId = msg.getPlayerId();
        final long timeLeft = msg.getTimeLeft();

        mRunPlsDeclare = new Runnable() {

            @Override
            public void run() {
                synchronized (this) {
                    mRunPlsDeclare = null;

                    if (mDestoryed)
                        return;

                    LogUtil.d(TAG, "SRV_PLS_DECLARE playerId=" + playerId
                            + " timeLeft=" + timeLeft);

                    mPlayerContext.mInfoCommon.mCurrShowPlayerId = playerId;

                    if (mPlayerContext.isPlayerSelf(playerId)) {
                        // 轮到我叫地主

                        if (!mIsRobotControl) {
                            update(UI_SHOW_DECLARE_LORD_BTN_ONLY);
                        } else {
                            mLastServerOrder = msg;
                        }
                    }

                }
            }
        };

        AllocCardsRunnable allocCardsRunnable = (AllocCardsRunnable) mRunAllocCards;
        if (allocCardsRunnable.isFinished()) {
            runPlsDeclare();
        }

    }

    private void runPlsDeclare() {
        if (mRunPlsDeclare != null)
            mHandler.post(mRunPlsDeclare);
    }

    private void sendClientGotCards() {
        int playerId = mPlayerContext.mInfoMyself.mSelfPlayerId;
        ClientGotCards cMsg = new ClientGotCards(playerId);
        mGameServer.send(cMsg);
    }

    private void onSrvPlsRob(ServerMessage sMsg) {
        LogUtil.d(TAG, "SRV_PLS_ROB");

        ServerPlsRob msg = (ServerPlsRob) sMsg;
        int playerId = msg.getPlayerId();
        long timeLeft = msg.getTimeLeft();

        LogUtil.d(TAG, "SRV_PLS_ROB playerId=" + playerId + " timeLeft=" + timeLeft);

        mPlayerContext.mInfoCommon.mCurrShowPlayerId = playerId;

        if (mPlayerContext.isPlayerSelf(playerId)) {
            // 轮到我抢地主

            if (!mIsRobotControl) {
                update(UI_SHOW_ROB_LORD_BTN);
            } else {
                mLastServerOrder = sMsg;
            }
        }
    }

    private void onSrvLastRob(ServerMessage sMsg) {
        LogUtil.d(TAG, "SRV_LAST_ROB");
        ServerLastRob msg = (ServerLastRob) sMsg;
        int playerId = msg.getPlayerId();
        boolean isRob = msg.isRob();

        LogUtil.d(TAG, "SRV_LAST_ROB playerId=" + playerId);

        showLastRobText(playerId, isRob);

        int sex = getSex(playerId);
        GameSoundUtil.playRobLord(isRob, sex);
    }

    private void showLastRobText(int playerId, boolean isRob) {
        if (mPlayerContext.isPlayerRight(playerId)) {
            if (isRob) {
                showRightText(getString(R.string.rob_lord));
            } else {
                showRightText(getString(R.string.rob_giveup));
            }
        } else if (mPlayerContext.isPlayerLeft(playerId)) {
            if (isRob) {
                showLeftText(getString(R.string.rob_lord));
            } else {
                showLeftText(getString(R.string.rob_giveup));
            }
        } else if (mPlayerContext.isPlayerSelf(playerId)) {
            if (isRob) {
                showSelfText(getString(R.string.rob_lord));
            } else {
                showSelfText(getString(R.string.rob_giveup));
            }
        }
    }

    private void onSrvLastDeclare(ServerMessage sMsg) {
        LogUtil.d(TAG, "SRV_LAST_DECLARE");
        ServerLastDeclare msg = (ServerLastDeclare) sMsg;

        int currDelcarePlayerId = msg.getCurrDeclarePlayerId();
        int currDeclareNum = msg.getCurrDeclareNum();
        int maxDeclareNum = msg.getMaxDeclareNum();

        LogUtil.d(TAG, "currDelcarePlayerId=" + currDelcarePlayerId);
        LogUtil.d(TAG, "currDeclareNum=" + currDeclareNum);
        LogUtil.d(TAG, "maxDeclareNum=" + maxDeclareNum);

        // 更新数据
        mPlayerContext.mInfoCommon.mRate = 1;
        mPlayerContext.mInfoCommon.mDeclareNum = maxDeclareNum;
        if (mPlayerContext.getSelfPlayerId() == currDelcarePlayerId) {
            mLastServerOrder = null;
        }

        showLastDelcareText(currDelcarePlayerId, currDeclareNum);

        int sex = getSex(currDelcarePlayerId);
        GameSoundUtil.playDeclareLord(currDeclareNum, sex);
    }

    private void showLastDelcareText(int currDelcarePlayerId, int currDeclareNum) {
        if (mPlayerContext.isPlayerRight(currDelcarePlayerId)) {
            if (currDeclareNum > 0) {
                showRightText(currDeclareNum + getString(R.string.fen));
            } else {
                showRightText(getString(R.string.bu_jiao));
            }
        } else if (mPlayerContext.isPlayerLeft(currDelcarePlayerId)) {
            if (currDeclareNum > 0) {
                showLeftText(currDeclareNum + getString(R.string.fen));
            } else {
                showLeftText(getString(R.string.bu_jiao));
            }
        } else if (mPlayerContext.isPlayerSelf(currDelcarePlayerId)) {
            if (currDeclareNum > 0) {
                showSelfText(currDeclareNum + getString(R.string.fen));
            } else {
                showSelfText(getString(R.string.bu_jiao));
            }
        }
    }

    private void onSrvDeclareResult(ServerMessage sMsg) {
        LogUtil.d(TAG, "SRV_DECLARE_RESULT");
        ServerDeclareResult msg = (ServerDeclareResult) sMsg;
        boolean result = msg.getResult();
        if (result) {
            int[] bottomCardIds = msg.getBottomCardIds();
            int delcareNum = msg.getFinalDeclareNum();
            int lordPlayerId = msg.getLordPlayerId();

            // 更新数据
            mPlayerContext.mInfoCommon.mLordPlayerId = lordPlayerId;
            mPlayerContext.mInfoCommon.mBottomCardIds = bottomCardIds;
            mPlayerContext.mInfoCommon.mRate = 1;
            mPlayerContext.mInfoCommon.mDeclareNum = delcareNum;

            if (mPlayerContext.mInfoMyself.mSelfPlayerId == lordPlayerId) { // 地主的牌加入底牌
                int[] landLordCardIds = new int[Constants.LANDLORD_CARDS_NUM];
                int[] selfCardIds = mPlayerContext.getSelfCardIds();
                int i = 0;
                for (; i < selfCardIds.length; i++) {
                    landLordCardIds[i] = selfCardIds[i];
                }

                int j = i;
                for (; i < landLordCardIds.length; i++) {
                    landLordCardIds[i] = bottomCardIds[i - j];
                }

                CardUtil.sortDescending(landLordCardIds);
                CardUtil.printCards("landLordCardIds = ", landLordCardIds);

                updateSelfCardIds(landLordCardIds, true);
            }

            for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                if (lordPlayerId == i) {
                    mPlayerContext.setLeftCardNum(i, Constants.LANDLORD_CARDS_NUM);
                } else {
                    mPlayerContext.setLeftCardNum(i, Constants.PLAYER_CARDS_NUM);
                }
            }

            update(UI_SHOW_BOTTOM_CARDS);
            update(UI_UPDATE_LEFT_CARD_NUM);
            update(UI_UPDATE_RIGHT_CARD_NUM);

            update(UI_UPDATE_LORD_CUP);
            // update(UI_HIDE_LEFT_TEXT);
            // update(UI_HIDE_RIGHT_TEXT);

        }
    }

    private void onSrvPlsShow(ServerMessage sMsg) {
        LogUtil.d(TAG, "SRV_PLS_SHOW");
        ServerPlsShow msg = (ServerPlsShow) sMsg;
        int playerId = msg.getPlayerId();
        // long timeLeft = msg.getTimeLeft();

        mPlayerContext.mInfoCommon.mCurrShowPlayerId = playerId;

        mPlayerContext.mInfoCommon.mPrevShowPlayerId = -1;
        mPlayerContext.mInfoCommon.mPrevShowCardIds = null;

        for (int i = 0; i < Constants.PLAYER_NUM; i++) {
            mPlayers.get(i).clearPreShowCardIds();
        }

        if (mPlayerContext.isPlayerSelf(playerId)) {
            mPlayerContext.mPlayerState = PlayerContext.THINKING_SHOW_CARD;
            // 轮到我出牌
            if (!mIsRobotControl) {
                update(UI_SHOW_FIRST_SHOW_GAME_BTN);
            } else {
                mLastServerOrder = sMsg;
            }

        } else if (mPlayerContext.isPlayerRight(playerId)) {
            // 其他家出牌
            update(UI_HIDE_RIGHT_TEXT);
        } else if (mPlayerContext.isPlayerLeft(playerId)) {
            // 其他家出牌
            update(UI_HIDE_LEFT_TEXT);
        }
    }

    private void onSrvPlsFollow(ServerMessage sMsg) {
        LogUtil.d(TAG, "SRV_PLS_FOLLOW");
        ServerPlsFollow msg = (ServerPlsFollow) sMsg;
        int playerId = msg.getPlayerId();
        // long timeLeft = msg.getTimeLeft();

        int[] maxCardIds = msg.getMaxCardIds();
        int maxCardIdsPlayerId = msg.getMaxCardIdsPlayerId();

        // 更新数据
        mPlayerContext.mInfoCommon.mCurrShowPlayerId = playerId;
        mPlayerContext.setPrevShowPlayerId(maxCardIdsPlayerId);
        mPlayerContext.setPrevShowCardIds(maxCardIds);

        mPlayers.get(maxCardIdsPlayerId).setPreShowCardIds(maxCardIdsPlayerId, maxCardIds);

        if (mPlayerContext.isPlayerSelf(playerId)) {
            mPlayerContext.mPlayerState = PlayerContext.THINKING_FOLLOW_CARD;
            // 轮到我跟牌
            mPlayers.get(playerId).setPreShowCardIds(playerId, null);

            if (!mIsRobotControl) {
                update(UI_SHOW_FOLLOW_GAME_BTN);
            } else {
                mLastServerOrder = sMsg;
            }
        } else if (mPlayerContext.isPlayerLeft(playerId)) {
            // 其他家跟牌
            update(UI_HIDE_LEFT_TEXT);

            mPlayers.get(playerId).setPreShowCardIds(playerId, null);
        } else if (mPlayerContext.isPlayerRight(playerId)) {
            update(UI_HIDE_RIGHT_TEXT);

            mPlayers.get(playerId).setPreShowCardIds(playerId, null);
        }
    }

    private void onSrvUpdateRate(ServerMessage sMsg) {
        ServerUpdateRate msg = (ServerUpdateRate) sMsg;
        int rate = msg.getRate();

        LogUtil.d(TAG, "SRV_UPDATE_RATE rate=" + rate);

        // 更新数据
        mPlayerContext.mInfoCommon.mRate = rate;

        update(UI_SHOW_BOTTOM_CARDS);
    }

    private void onSrvUpdatePlus(ServerMessage sMsg) {
        ServerUpdatePlus msg = (ServerUpdatePlus) sMsg;
        long plus = msg.getPlus();
        long currPlus = msg.getCurrPlus();
        int playerId = msg.getPlayerId();

        mPlayerContext.mInfoCommon.mPlus[playerId] = plus;

        LogUtil.d(TAG, "SRV_UPDATE_PLUS currPlus=" + currPlus + " plus=" + plus);

        // 加分动画
        if (mPlayerContext.isPlayerSelf(playerId)) {
            showTextAnim(UI_SHOW_SELF_PLUS_ANIM, "+" + currPlus);
        } else if (mPlayerContext.isPlayerRight(playerId)) {
            showTextAnim(UI_SHOW_RIGHT_PLUS_ANIM, "+" + currPlus);
        } else if (mPlayerContext.isPlayerLeft(playerId)) {
            showTextAnim(UI_SHOW_LEFT_PLUS_ANIM, "+" + currPlus);
        }
    }

    private void onSrvRobot(ServerMessage sMsg) {
        LogUtil.d(TAG, "SRV_ROBOT");
        ServerRobot msg = (ServerRobot) sMsg;
        int playerId = msg.getPlayerId();
        boolean isRobotControl = msg.isRobot();

        int selfPlayerId = mPlayerContext.mInfoMyself.mSelfPlayerId;
        if (playerId == selfPlayerId) {
            mIsRobotControl = isRobotControl;

            if (mIsRobotControl) {
                update(UI_HIDE_ALL_BTN);
            } else {
                repeatLastOrder();
            }
        }
    }

    private void onSrvShowResult(ServerMessage sMsg) {
        LogUtil.d(TAG, "SRV_SHOW_RESULT");
        ServerShowResult msg = (ServerShowResult) sMsg;
        final boolean isShow = msg.IsShow();
        final int lastShowPlayerId = msg.getLastShowPlayerId();
        final int[] lastShowCardIds = msg.getLastShowCardIds();

        if (mPlayerContext.isPlayerSelf(lastShowPlayerId)) {
            checkAchievesByShowCards(lastShowCardIds);
        }

        mPlayers.get(lastShowPlayerId).setPreShowCardIds(lastShowPlayerId,
                lastShowCardIds);

        CardUtil.printCards("SRV_SHOW_RESULT lastShowCardIds: ", lastShowCardIds);

        // 更新数据
        if (lastShowCardIds != null && lastShowCardIds.length > 0) { // 非过牌

            GameSoundUtil.playShowCards();

            int cardType = CardUtil.getCardType(lastShowCardIds);
            int sex = getSex(lastShowPlayerId);
            showCardTypeAnim(cardType);
            GameSoundUtil.playShowSound(this, lastShowCardIds, cardType, sex, isShow);

            mAiContext.doShowCardType(lastShowPlayerId, cardType);

            mAiContext.doShow(lastShowPlayerId, lastShowCardIds, cardType);

            if (lastShowPlayerId != mPlayerContext.mInfoCommon.mPrevShowPlayerId) {
                mAiContext.doFollowed(mPlayerContext.mInfoCommon.mPrevShowPlayerId,
                        lastShowPlayerId, lastShowCardIds, cardType);
            }

            mPlayerContext.mInfoCommon.mPrevShowPlayerId = lastShowPlayerId;
            mPlayerContext.mInfoCommon.mPrevShowCardIds = lastShowCardIds;
            mPlayerContext.mInfoCommon.mLeftCardNums[lastShowPlayerId] -= lastShowCardIds.length;

            if (leftCardNumDanger()) {
                if (!mWarnedLessCards) {
                    mWarnedLessCards = true;
                    playBgMusic(R.raw.bgm_hot);

                    mAiContext.doLeftCardNumDanger(lastShowPlayerId);
                }
            }

            // 移出系统确认的牌
            if (lastShowPlayerId == mPlayerContext.mInfoMyself.mSelfPlayerId) {
                int lastShowLen;
                int selfLen;
                if (lastShowCardIds != null && (lastShowLen = lastShowCardIds.length) > 0) {
                    int[] selfCardIds = mPlayerContext.getSelfCardIds();
                    if (selfCardIds != null && (selfLen = selfCardIds.length) > 0) {
                        for (int i = 0; i < lastShowLen; i++) {
                            for (int j = 0; j < selfLen; j++) {
                                if (lastShowCardIds[i] == selfCardIds[j]) {
                                    selfCardIds[j] = -1;
                                }
                            }
                        }
                        selfCardIds = CardUtil.copyCards(selfCardIds);

                        updateSelfCardIds(selfCardIds, false);
                    }
                }

            } else {
                if (mPlayerContext.isPlayerRight(lastShowPlayerId)) {
                    update(UI_UPDATE_RIGHT_CARD_NUM);
                } else {
                    update(UI_UPDATE_LEFT_CARD_NUM);
                }
            }

        } else { // 过牌

            boolean randPassType = GameSoundUtil.getRandPassType();
            int sex = getSex(lastShowPlayerId);
            showPassText(randPassType, lastShowPlayerId);
            GameSoundUtil.playPassSound(randPassType, sex);

            mAiContext.doPassed(lastShowPlayerId);

            mPlayerContext.mInfoCommon.mPrevShowPlayerId = lastShowPlayerId;
            mPlayerContext.mInfoCommon.mPrevShowCardIds = null;

        }

        // 更新界面
        update(UI_HIDE_GAME_BTN);

        mLastServerOrder = null;
    }

    private void onSrvGameResult(ServerMessage sMsg) {
        LogUtil.d(TAG, "SRV_GAME_RESULT");

        onReleaseSensitiveRes();

        ServerGameResult msg = (ServerGameResult) sMsg;
        final boolean lordWin = msg.isLordWin();
        int[] marks = msg.getFinalMarks();
        int[] totalMarks = msg.getTotalMarks();
        int[] handsCount = msg.getHandsCount();
        List<int[]> cardIdsList = msg.getFinalCardIdsList();

        int selfPlayerId = mPlayerContext.mInfoMyself.mSelfPlayerId;

        for (int i = 0; i < Constants.PLAYER_NUM; i++) {
            mPlayers.get(i).setPreShowCardIds(i, cardIdsList.get(i));
        }

        updateSelfCardIds(null, false);

        ScoreRec.setMatchRecord(marks[selfPlayerId]);
        showMatchRecord();

        ScoreRec.addToRoundScore(marks[selfPlayerId]);
        showRoundScore();

        update(UI_HIDE_LEFT_TEXT);
        update(UI_HIDE_RIGHT_TEXT);
        update(UI_HIDE_ALL_BTN);

        mAiContext.doResult(lordWin, mPlayerContext.mInfoCommon.mLordPlayerId);

        mGameResult = new GameResult();

        mGameResult.mSubTotalOfRecord1 = marks[0];
        mGameResult.mSubTotalOfRecord2 = marks[1];
        mGameResult.mSubTotalOfRecord3 = marks[2];

        mGameResult.mTotalOfRecord1 = totalMarks[0];
        mGameResult.mTotalOfRecord2 = totalMarks[1];
        mGameResult.mTotalOfRecord3 = totalMarks[2];

        mGameResult.mLeftGameNum = Constants.OFFLINE_GAME_ROUND_NUM
                - mPlayerContext.getRoundIndex() - 1;

        mGameResult.mNameOfRecord1 = getString(R.string.player);
        mGameResult.mNameOfRecord2 = mRightName;
        mGameResult.mNameOfRecord3 = mLeftName;

        checkAchievesByGameResult(handsCount[mPlayerContext.getSelfPlayerId()]);

        delayClearShowCards();

        delayReleaseShowCards();

        delayShowGameAnimation(lordWin);
    }

    private void clearShowCards() {

        for (int i = 0; i < Constants.PLAYER_NUM; i++) {
            mPlayers.get(i).clearPreShowCardIds();
        }

        update(UI_HIDE_BOTTOM_CARDS);

        mPlayerContext.setSelfCardSelects(null);
        mPlayerContext.setSelfCardTouches(null);
        update(UI_UPDATE_SELF_CARDS);
    }

    private void releaseShowCards() {
        CardsBitmap.clearAll();
        CardFgsBitmap.clearAll();

        System.gc();
    }

    private void delayClearShowCards() {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                clearShowCards();
            }

        }, 1500);
    }

    private void delayReleaseShowCards() {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                releaseShowCards();
            }

        }, 2000);
    }

    private void delayShowGameAnimation(final boolean lordWin) {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                showGameAnimation(lordWin);
            }

        }, 2500);
    }

    private void showGameAnimation(boolean lordWin) {
        // 新开一局
        if ((mPlayerContext.isSelfLandLord() && lordWin)
                || (!mPlayerContext.isSelfLandLord() && !lordWin)) {
            stopBgMusic();
            GameSoundUtil.playResult(true, new OnSoundListener() {

                @Override
                public void onError(int errCode) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onCompletion() {
                    playBgMusic();
                }
            });

            mPlayerContext.increaseWinWinNum();
            mPlayerContext.increaseWinNum();

            Intent i = new Intent(this, AfterWinActivity.class);
            startActivityForResult(i, REQ_WIN_RESULT);

        } else {
            stopBgMusic();
            GameSoundUtil.playResult(false, new OnSoundListener() {

                @Override
                public void onError(int errCode) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onCompletion() {
                    playBgMusic();
                }
            });

            mPlayerContext.resetWinWinNum();
            mPlayerContext.increaseLoseNum();

            Intent i = new Intent(this, AfterFailActivity.class);
            startActivityForResult(i, REQ_FAIL_RESULT);
        }

        mGameResult.mWinWinNum = mPlayerContext.getWinWinNum();
        mGameResult.mWinNum = mPlayerContext.getWinNum();
        mGameResult.mLoseNum = mPlayerContext.getLoseNum();
    }

    private void delayShowGameResult() {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                showGameResult();
            }
        }, 1000);
    }

    private void showGameResult() {
        Intent i = new Intent(this, GameResultActivity.class);
        i.putExtra(GameResultActivity.AUTO_CLOSE, mIsRobotControl);
        i.putExtra(GameResultActivity.GAME_RESULT, mGameResult);
        startActivityForResult(i, REQ_GAME_RESULT);

        mGameResult = null;
        clearLeftCardNum();
    }

    private void checkAchievesByShowCards(int[] showCards) {
        if (showCards == null || showCards.length <= 0)
            return;

        int cardType = CardUtil.getCardType(showCards);
        switch (cardType) {
            case CardType.LL_SINGLE_DRAGON:
                if (showCards.length >= 8) {
                    AchievesRec.setAchieved(AchievesRec.ACHIEVE_ID_FG1_SLWS, true);
                }
                break;
            case CardType.LL_DOUBLE_DRAGON:
                if (showCards.length >= 12) {
                    AchievesRec.setAchieved(AchievesRec.ACHIEVE_ID_BG2_FTZZ, true);
                }
                break;
            case CardType.LL_TRIPLE_DRAGON:
                if (showCards.length >= 12) {
                    AchievesRec.setAchieved(AchievesRec.ACHIEVE_ID_BG1_FXX, true);
                }
                break;
        }

    }

    private void checkAchievesByGameResult(int handsCount) {

        if (handsCount <= 2) {
            AchievesRec.setAchieved(AchievesRec.ACHIEVE_ID_KQS, true);
        }

        int totalRate = mPlayerContext.getRate() * mPlayerContext.getDeclareNum();
        if (totalRate >= 48) {
            AchievesRec.setAchieved(AchievesRec.ACHIEVE_ID_FG2_PLW, true);
        }

        if (mPlayerContext.getWinWinNum() >= Constants.OFFLINE_GAME_ROUND_NUM) {
            AchievesRec.setAchieved(AchievesRec.ACHIEVE_ID_DYJ, true);
        }
    }

    private boolean leftCardNumDanger() {
        int[] leftCardNums = mPlayerContext.getLeftCardNums();
        for (int i = leftCardNums.length - 1; i >= 0; i--) {
            if (leftCardNums[i] <= 5) {
                return true;
            }
        }

        return false;
    }

    private void clearLeftCardNum() {
        mPlayerContext.mInfoCommon.resetLeftCardNum();

        update(UI_UPDATE_LEFT_CARD_NUM);
        update(UI_UPDATE_RIGHT_CARD_NUM);
    }

    private void resetRoundScore() {
        ScoreRec.setRoundScore(0);
        mRoundScore.setText("0");
    }

    private void showMatchRecord() {
        mMatchRecord.setText(String.valueOf(ScoreRec.getMatchRecord()));
    }

    private void showRoundScore() {
        mRoundScore.setText(String.valueOf(ScoreRec.getRoundScore()));
    }

    private void repeatLastOrder() {
        if (mLastServerOrder == null) {
            return;
        }

        int selfPlayerId = mPlayerContext.mInfoMyself.mSelfPlayerId;
        switch (mLastServerOrder.getMsgType()) {
            case SRV_PLS_READY: {
                update(UI_SHOW_START_BTN_ONLY);
            }
                break;
            case SRV_PLS_DECLARE: {
                ServerPlsDeclare spd = (ServerPlsDeclare) mLastServerOrder;
                if (spd.getPlayerId() == selfPlayerId) {
                    update(UI_SHOW_DECLARE_LORD_BTN_ONLY);
                }
            }
                break;
            case SRV_PLS_SHOW: {
                ServerPlsShow sps = (ServerPlsShow) mLastServerOrder;
                if (sps.getPlayerId() == selfPlayerId) {
                    update(UI_SHOW_FIRST_SHOW_GAME_BTN);
                }
            }
                break;
            case SRV_PLS_FOLLOW: {
                ServerPlsFollow spf = (ServerPlsFollow) mLastServerOrder;
                if (spf.getPlayerId() == selfPlayerId) {
                    update(UI_SHOW_FOLLOW_GAME_BTN);
                }
            }
                break;
        }
    }

    private Robot getTishiRobot() {
        if (mTiShiRobot == null) {
            mTiShiRobot = new Robot(mPlayerContext);
        }
        return mTiShiRobot;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQ_WIN_RESULT:
            case REQ_FAIL_RESULT: {
                System.gc();

                delayShowGameResult();
            }
                break;
            case REQ_GAME_RESULT: {
                if (resultCode == GameResultActivity.BTN_EXIT) {
                    leaveGame();
                } else if (resultCode == GameResultActivity.BTN_GOON) {
                    onButtonGoon();
                }
            }
                break;

        }
    }

    private void onButtonGoon() {
        int roundIdx = mPlayerContext.getRoundIndex();
        if (roundIdx >= 0 && roundIdx < Constants.OFFLINE_GAME_ROUND_NUM - 1) {
            goonGame();
            mPlayerContext.increaseRound(Constants.OFFLINE_GAME_ROUND_NUM);
        } else { // roundIdx == 0 时round结束
            leaveGame();
        }
    }

    private void letRobotControl() {
        if (!mIsRobotControl) {
            LogUtil.d(TAG, "letRobotControl");
            int playerId = mPlayerContext.mInfoMyself.mSelfPlayerId;
            ClientMessage cMsg = new ClientRobot(playerId, true);
            mGameServer.send(cMsg);
        }
    }

    private void leaveGame() {
        ClientMessage cMsg = new ClientLeaveRoom();
        mGameServer.send(cMsg);

        int score = ScoreRec.getRoundScore();
        int minScore = OfflineTopDbadapter.getMinScore(this);
        if (score > minScore) {
            Intent i = new Intent(this, OfflineTopActivity.class);
            i.putExtra(OfflineTopDbadapter.KEY_SCORE, score);
            startActivity(i);
            overridePendingTransition(R.anim.fade, R.anim.hold);
        }

        finish();
    }

    private void onLoadSensitiveRes() {
        mGameBgView.onLoadSensitiveRes();
    }

    private void onReleaseSensitiveRes() {
        mGameBgView.onReleaseSensitiveRes();

        mSingleDragonAnimation.onStop();
        mDoubleDragonAnimation.onStop();
        mRocketAnimation.onStop();
        mPlaneAnimation.onStop();
        mBombAnimation.onStop();

        update(UI_RESET_LORD_CUP);
    }

    // --------- 出牌显示 ---------

    private int getSex(int playerId) {
        return mPlayers.get(playerId).getSex();
    }

    private void showPassText(boolean randPassType, int playerId) {
        String text;
        if (randPassType) {
            text = getString(R.string.bu_yao);
        } else {
            text = getString(R.string.yao_bu_qi);
        }

        if (mPlayerContext.isPlayerRight(playerId)) {
            showRightText(text);
        } else if (mPlayerContext.isPlayerLeft(playerId)) {
            showLeftText(text);
        } else if (mPlayerContext.isPlayerSelf(playerId)) {
            showSelfText(text);
        }
    }

    private void showCardTypeAnim(int cardType) {
        switch (cardType) {
            case CardType.LL_SINGLE:
                break;

            case CardType.LL_DOUBLE:
                break;

            case CardType.LL_TRIPLE:
                break;

            case CardType.LL_TRIPLE_W_SINGLE:
                break;

            case CardType.LL_TRIPLE_W_DOUBLE:
                break;

            case CardType.LL_SINGLE_DRAGON:
                update(UI_SHOW_SINGLE_DRAGON_ANIM);
                break;

            case CardType.LL_DOUBLE_DRAGON:
                update(UI_SHOW_DOUBLE_DRAGON_ANIM);
                break;

            case CardType.LL_TRIPLE_DRAGON:
            case CardType.LL_TRIPLE_DRAGON_W_SINGLE:
            case CardType.LL_TRIPLE_DRAGON_W_DOUBLE:
                update(UI_SHOW_PLANE_ANIM);
                break;

            case CardType.LL_FOUR_W_TWO_SINGLE:
                break;

            case CardType.LL_FOUR_W_TWO_DOUBLE:
                break;

            case CardType.LL_FOUR_AS_BOMBER:
                update(UI_SHOW_BOMB_ANIM);
                break;

            case CardType.LL_JOKER_AS_ROCKET:
                update(UI_SHOW_ROCKET_ANIM);
                break;

            default:
                break;
        }
    }

    protected boolean onBack() {
        leaveGame();
        return true;
    }

}

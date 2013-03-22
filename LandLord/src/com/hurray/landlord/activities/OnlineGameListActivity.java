
package com.hurray.landlord.activities;

import com.hurray.landlord.R;
import com.hurray.landlord.adapter.OnlineGameListAdapter;
import com.hurray.landlord.entity.GameInfo;
import com.hurray.landlord.game.data.RoomInfo;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.ToastUtil;
import com.hurray.lordserver.protocol.message.account.PersonCenterReq;
import com.hurray.lordserver.protocol.message.base.BaseMessage;
import com.hurray.lordserver.protocol.message.card.PlsReadyPush;
import com.hurray.lordserver.protocol.message.card.RoomInfoPush;
import com.hurray.lordserver.protocol.message.card.RoomInfoPush.PlayerInfo;
import com.hurray.lordserver.protocol.message.card.SignInGameReq;
import com.hurray.lordserver.protocol.message.card.SignInGameResp;
import com.hurray.lordserver.protocol.message.card.SignOffGameReq;
import com.hurray.lordserver.protocol.message.card.SignOffGameResp;
import com.hurray.lordserver.protocol.message.match.GameMatchReq;
import com.hurray.lordserver.protocol.message.match.GameMatchResp;
import com.hurray.lordserver.protocol.message.match.GameMatchResp.MatchInfo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class OnlineGameListActivity extends BaseNetActivity {

    private static final String TAG = "OnlineGameListActivity";
    
    // private static final int DIALOG_ONLINE_GAME_APPLY = 1;

    private static final int DIALOG_ONLINE_SIGN_IN_WAITING = 2;

    private static final int DIALOG_ONLINE_SIGN_IN_WAITING_PERSON = 3;

    private static final int ONE_SEC = 1000;

    private Dialog mSignInDialog;

    private TextView mWaitText;

    private TextView mWaitPeopleText;

    private static String mWaitPeopleTextContent;

    private ListView mGameListView;

    private TextView mOnlinePlayerNum;

    private OnlineGameListAdapter mGameListAdapter;

    private ArrayList<GameInfo> mGameList;

    private RoomInfoPush mRoomInfoPush;

    private Button mBtnApply;

    private int mCurrSelectMatchPos = -1;

    private int mLeftTime;

    private Handler mHandler = new Handler();

    private PowerManager.WakeLock mWakeLock;
    
    private Runnable mRunRefreshTime = new Runnable() {

        @Override
        public void run() {
            if (mLeftTime > 0) {
                mLeftTime--;
                refreshSignInDialogTimeText();
            } else {
                dismissSignInDialog();
            }

        }
    };

    private void refreshSignInDialogTimeText() {
        if (!this.isFinishing()) {
            showDialog(DIALOG_ONLINE_SIGN_IN_WAITING);
        }
        if (mSignInDialog != null) {
            mWaitText.setText(getString(R.string.waiting_room, mLeftTime));
            mHandler.postDelayed(mRunRefreshTime, ONE_SEC);
        }

        // refreshSignInWaitPeopleDialog();

    }

    private void refreshSignInWaitPeopleDialog() {
        if (!this.isFinishing()) {
            showDialog(DIALOG_ONLINE_SIGN_IN_WAITING_PERSON);
        }
        mWaitPeopleText.setText(mWaitPeopleTextContent);

    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_gamelist_activity);

        mBtnApply = (Button) findViewById(R.id.btn_online_apply);
        mBtnApply.setOnClickListener(mApplyClickListener);

        mOnlinePlayerNum = (TextView) findViewById(R.id.online_player_num);

        mGameListView = (ListView) findViewById(R.id.online_game_list);
        mGameListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mGameListAdapter.setClickIndex(position);

                for (int i = mGameList.size() - 1; i >= 0; i--) {
                    GameInfo gameInfo = mGameList.get(i);
                    if (position == i) {
                        gameInfo.setIsShowDesc(!gameInfo.isShowDesc());
                    } else {
                        gameInfo.setIsShowDesc(false);
                    }

                    if (gameInfo.isShowDesc()) {
                        // mOnlinePlayerNum.setText("在线" +
                        // gameInfo.getTotalNum() + "，美女"
                        // + gameInfo.getFaleNum());
                        mCurrSelectMatchPos = i;
                    }
                }

                boolean nothingSelected = true;
                for (GameInfo gi : mGameList) {
                    if (gi.isShowDesc()) {
                        nothingSelected = false;
                    }
                }
                if (nothingSelected) {
                    mCurrSelectMatchPos = -1;
                }

                mGameListAdapter.notifyDataSetChanged();
            }
        });
        
        if(mWaitPeopleTextContent != null){
        	refreshSignInWaitPeopleDialog();
        }
    }
    
    private void setWakeLock(){
    	if (mWakeLock == null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
					"My Tag");
		}
    	mWakeLock.acquire();
    }
    
    private void releaseWakeLock(){
    	if (mWakeLock != null) {
			mWakeLock.release();
		}
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        super.addListener();
        reqGameMatchList();
        // reqPersonCenter();
        setWakeLock();
    }

    @Override
    protected void onPause() {
        super.onPause();
        super.removeListener();
        releaseWakeLock();
    }

    @Override
    protected void onDestroy() {
        // unbind =false;
        super.onDestroy();
        dismissSignInDialog();
        mSignInDialog = null;
        mWakeLock = null;
    }

    private void reqGameMatchList() {
        // addListener();
        GameMatchReq req = new GameMatchReq();
        doSend(req);
    }

    private void reqPersonCenter() {
        // addListener();
        PersonCenterReq req = new PersonCenterReq();
        doSend(req);
    }

    private ArrayList<GameInfo> getGameList(GameMatchResp r) {
        mGameList = new ArrayList<GameInfo>();

        MatchInfo[] matchArray = r.getMatchInfo();
        int faleNum = r.getFaleOnline();
        int totalNum = r.getTotalOnline();
        for (MatchInfo m : matchArray) {
            GameInfo info = new GameInfo();
            info.setGameType(m.gameType);
            info.setName(m.gameName);
            info.setDesc(m.gameDesc);
            info.setIsShowDesc(false);

            info.setOnlineNum(totalNum);
            info.setFaleNum(faleNum);
            info.setGameInfo(m.gameInfo);

            info.setmMatchTime(m.limitTimeName);
            info.setmPlayerNum(m.gameOnlineNum);

            mGameList.add(info);
        }

        return mGameList;
    }

    private View.OnClickListener mApplyClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // showDialog(DIALOG_ONLINE_GAME_APPLY);

            LogUtil.d(TAG, "mCurrSelectMatchPos=" + mCurrSelectMatchPos);
            if (mCurrSelectMatchPos >= 0) {
                GameInfo info = mGameList.get(mCurrSelectMatchPos);
                int gameType = info.getGameType();
                signIn(gameType);
                // showDialog(DIALOG_ONLINE_SIGN_IN_WAITING);
            } else {
                ToastUtil.show("请选择比赛");
            }

        }
    };

    private MatchInfo[] matchInfos;

    private Dialog gameType2Diaolog;

    protected Dialog onCreateDialog(int id) {
        final Dialog dialog;
        View view;
        switch (id) {
            case DIALOG_ONLINE_SIGN_IN_WAITING: {
                dialog = new Dialog(OnlineGameListActivity.this, R.style.dialog){
                	@Override
                	public boolean onKeyDown(int keyCode, KeyEvent event) {
                		return true;
                	}
                };

                dismissSignInDialog();
                mSignInDialog = dialog;

                view = LayoutInflater.from(OnlineGameListActivity.this).inflate(
                        R.layout.dlg_sign_in_wait, null);

                mWaitText = (TextView) view.findViewById(R.id.wait_text);
                // mWaitText.setText(R.string.sign_in_ongoing);

                dialog.setContentView(view);
                return dialog;
            }

            case DIALOG_ONLINE_SIGN_IN_WAITING_PERSON: {
                if (gameType2Diaolog == null) {
                    gameType2Diaolog = new Dialog(OnlineGameListActivity.this, R.style.dialog){
                    	@Override
                    	public boolean onKeyDown(int keyCode, KeyEvent event) {
                    		return true;
                    	}
                    };
                }
                view = LayoutInflater.from(OnlineGameListActivity.this).inflate(
                        R.layout.dlg_sign_in_wait_person, null);

                mWaitPeopleText = (TextView) view.findViewById(R.id.tv_wait_people);
                final Button btnOK = (Button) view.findViewById(R.id.btn_ok);
                btnOK.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        SignOffGameReq req = new SignOffGameReq();

                        req.setGameType(2);

                        doSend(req);

                        btnOK.setVisibility(View.INVISIBLE);
                        
                        mWaitPeopleTextContent = null;
                    }
                });

                gameType2Diaolog.setContentView(view);
                return gameType2Diaolog;
            }

            default:
                return null;
        }
    }

    private void dismissSignInDialog4GameType2() {
        if (gameType2Diaolog != null && gameType2Diaolog.isShowing()) {
            try {
				gameType2Diaolog.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}
            mWaitPeopleTextContent = null;
        }
    }

    @Override
    public void onReceived(BaseMessage msg) {
        if (msg instanceof GameMatchResp) {
            GameMatchResp r = (GameMatchResp) msg;

            matchInfos = r.getMatchInfo();

            mOnlinePlayerNum.setText("在线" + r.getTotalOnline() + "，美女"
                    + r.getFaleOnline());

            if (r.isSucceeded()) {
                LogUtil.d("Demo", "mGameListAdapter:"+mGameListAdapter);
                if (mGameListAdapter == null) {
                    mGameListAdapter = new OnlineGameListAdapter(this,
                            getGameList(r));
                    mGameListView.setAdapter(mGameListAdapter);
                }
            } else {
                ToastUtil.show("获取赛制列表失败");
            }
        } else if (msg instanceof SignInGameResp) {

            SignInGameResp r = (SignInGameResp) msg;

            int ret = r.getSignInResult();

            int time = r.getWaitTime();

            String waitInfo = r.getInfo();

            if (SignInGameResp.SIGN_IN_SUCC == ret) {
                if (r.getMatchType() == 0) {
                    mLeftTime = time;
                    mHandler.removeCallbacks(mRunRefreshTime);
                    refreshSignInDialogTimeText();
                } else if (r.getMatchType() == 1) {
                    mWaitPeopleTextContent = waitInfo;
                    refreshSignInWaitPeopleDialog();
                }
            } else {
                ToastUtil.show(r.getResultDesc());
            }

        } else if (msg instanceof RoomInfoPush) {
            synchronized (this) {
                mRoomInfoPush = (RoomInfoPush) msg;
            }
        } else if (msg instanceof PlsReadyPush) {

            LogUtil.d("Demo", "PlsReadyPush");
            dismissSignInDialog();
            dismissSignInDialog4GameType2();
            // if(mRoomInfoPush.getGameType() ==2){
            // dismissDialog(DIALOG_ONLINE_SIGN_IN_WAITING_PERSON);
            // }

            synchronized (this) {
                if (mRoomInfoPush != null) {
                    PlsReadyPush plsReadyPush = (PlsReadyPush) msg;

                    PlayerInfo[] infoList = mRoomInfoPush.getPlayers();
                    long teamId = mRoomInfoPush.getTeamId();
                    int matchType = mRoomInfoPush.getMatchType();
                    int maxRound = mRoomInfoPush.getMaxRound();
                    int inningNum = mRoomInfoPush.getInningsNum();

                    LogUtil.d(TAG, "teamId=" + teamId);
                    if (RoomInfo.checkPositionType(infoList) && teamId >= 0 && maxRound > 0) {

                        RoomInfo roomInfo = new RoomInfo();
                        roomInfo.setPlsReadyTime(plsReadyPush.getWaitTime());
                        roomInfo.setRoomId(teamId);
                        roomInfo.setGameType(matchType);
                        roomInfo.setPlayers(infoList);
                        roomInfo.setMaxRound(maxRound);
                        roomInfo.setmInningNum(inningNum);
                        Intent i = new Intent(this, OnlineGameActivity.class);
                        i.putExtra(OnlineGameActivity.ROOM_INFO, roomInfo);
                        startActivity(i);
                        overridePendingTransition(R.anim.fade, R.anim.hold);

                        removeListener();

                    } else {

                        ToastUtil.longShow(R.string.room_info_illegal);
                    }

                    mRoomInfoPush = null;
                }
            }
        } else if (msg instanceof SignOffGameResp) {

            SignOffGameResp resp = (SignOffGameResp) msg;

            if (resp.getGameType() == 2) {

                Button btn = (Button) gameType2Diaolog.findViewById(R.id.btn_ok);

                btn.setVisibility(View.VISIBLE);

                dismissSignInDialog4GameType2();
            }
            ToastUtil.show(resp.getInfo());

        }

    };

    private void dismissSignInDialog() {
        if (mSignInDialog != null) {
            LogUtil.d("Demo", mSignInDialog.toString());
            try {
				mSignInDialog.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
    }

    private void signIn(int gameType) {
        SignInGameReq req = new SignInGameReq();
        req.setGameType(gameType);
        doSend(req);
    }

}

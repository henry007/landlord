
package com.hurray.landlord.activities;

import com.hurray.landlord.R;
import com.hurray.landlord.entity.RisedWaitActivityInfos;
import com.hurray.landlord.game.data.RoomInfo;
import com.hurray.landlord.game.online.MessageAdapter;
import com.hurray.landlord.net.socket.srv.ServerMatchResult;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.ToastUtil;
import com.hurray.lordserver.protocol.message.base.BaseMessage;
import com.hurray.lordserver.protocol.message.card.MatchEndPush;
import com.hurray.lordserver.protocol.message.card.PlsReadyPush;
import com.hurray.lordserver.protocol.message.card.RiseInRankWaitPush;
import com.hurray.lordserver.protocol.message.card.RoomInfoPush;
import com.hurray.lordserver.protocol.message.card.MatchEndPush.PlayersRank;
import com.hurray.lordserver.protocol.message.card.RoomInfoPush.PlayerInfo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ResourceBundle;

public class OnlineRisedWaitActivity extends BaseNetActivity {

    private RoomInfoPush mRoomInfoPush;

    private ImageView mImgLoading;

    private AnimationDrawable mAnimLoading;

    private TextView mTvTip = null;

    private TextView mTvPlayerNum = null;

    private int mGameType = -1;

    private int inningNum = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rised_wait);

        mTvTip = (TextView) findViewById(R.id.tv_tips_ipp5);

        mTvPlayerNum = (TextView) findViewById(R.id.tv_wait_player_num);

        mImgLoading = (ImageView) findViewById(R.id.anim_loading);

        mAnimLoading = (AnimationDrawable) mImgLoading.getBackground();

        Intent i = getIntent();

        RisedWaitActivityInfos infos = (RisedWaitActivityInfos) i.getSerializableExtra("wait_info");

        mGameType = i.getIntExtra(OnlineGameActivity.GAME_TYPE, -1);

        inningNum = i.getIntExtra("inningNum", -1);

        LogUtil.d("Demo", "risedActivity---gameType:" + mGameType);

        LogUtil.d("Demo", "risedActivity--IningNum:" + inningNum);

        if (infos != null) {

            mTvTip.setText(infos.getTips());

            mTvPlayerNum.setText(infos.getCompInfo());
        } else {

            ToastUtil.show("等待信息空");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        addListener();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {

            mAnimLoading.start();
        } else {

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mAnimLoading.start();
                }
            }, 2000);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        removeListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAnimLoading.stop();
        mImgLoading.setBackgroundResource(0);
    }

    @Override
    public void onReceived(BaseMessage msg) {

        if (msg instanceof RoomInfoPush) {
            synchronized (this) {
                mRoomInfoPush = (RoomInfoPush) msg;
            }
        } else if (msg instanceof PlsReadyPush) {
            synchronized (this) {
                if (mRoomInfoPush != null) {
                    PlsReadyPush plsReadyPush = (PlsReadyPush) msg;

                    PlayerInfo[] infoList = mRoomInfoPush.getPlayers();
                    long teamId = mRoomInfoPush.getTeamId();
                    int gameType = mRoomInfoPush.getGameType();
                    int maxRound = mRoomInfoPush.getMaxRound();
                    inningNum = mRoomInfoPush.getInningsNum();

                    if (RoomInfo.checkPositionType(infoList) && teamId >= 0 && maxRound > 0) {

                        RoomInfo roomInfo = new RoomInfo();
                        roomInfo.setPlsReadyTime(plsReadyPush.getWaitTime());
                        roomInfo.setRoomId(teamId);
                        roomInfo.setGameType(gameType);
                        roomInfo.setPlayers(infoList);
                        roomInfo.setMaxRound(maxRound);
                        roomInfo.setmInningNum(inningNum);
                        Intent i = new Intent(this, OnlineGameActivity.class);
                        i.putExtra(OnlineGameActivity.ROOM_INFO, roomInfo);
                        startActivity(i);
                        finish();
                        overridePendingTransition(R.anim.fade, R.anim.hold);
                        removeListener();

                    } else {
                        ToastUtil.longShow(R.string.room_info_illegal);
                    }

                    mRoomInfoPush = null;
                }
            }
        } else if (msg instanceof MatchEndPush) {

            MatchEndPush r = (MatchEndPush) msg;

            // ServerMatchResult
            int point = r.getPoint();
            int gold = r.getMoneyGold();
            int heart = r.getMoneyHeart();
            int exp = r.getCurrExp();
            int rank = r.getRank();
            // TODO lhx 排名界面信息

            int isRised = r.riseResult;

            PlayersRank[] rankInfos = r.getResults();

            ServerMatchResult srvMatchResult = new ServerMatchResult(point, gold, heart, exp, rank,
                    isRised, rankInfos);
            Intent i = new Intent(OnlineRisedWaitActivity.this,
                    CongratulationActivity.class);
            i.putExtra(CongratulationActivity.SERVER_MATCH_RESULT,
                    srvMatchResult);
            i.putExtra("inningNum", inningNum);
            i.putExtra(OnlineGameActivity.GAME_TYPE, mGameType);
            startActivity(i);
            finish();
        } else if (msg instanceof RiseInRankWaitPush) {
            RiseInRankWaitPush resp = (RiseInRankWaitPush) msg;
            mTvPlayerNum.setText(resp.getCompInfo());
        }
    }
}

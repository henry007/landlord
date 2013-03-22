
package com.hurray.landlord.activities;

import com.hurray.landlord.R;
import com.hurray.landlord.entity.Gender;
import com.hurray.landlord.entity.PlayersStatus;
import com.hurray.landlord.entity.UserInfo;
import com.hurray.lordserver.protocol.message.account.PersonCenterReq;
import com.hurray.lordserver.protocol.message.account.PersonCenterResp;
import com.hurray.lordserver.protocol.message.account.RegisterUserReq;
import com.hurray.lordserver.protocol.message.base.BaseMessage;
import com.hurray.lordserver.protocol.message.user.LogoutUserResp;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TabActorInfoActivity extends BaseNetActivity {

    private TextView mTvLevel, mTvGender, mTvRecord, mTvGold, mTvTitle,
            mTvNickName,mTvUserId;

    private UserInfo user;

    private Resources res;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:

                    mTvLevel.setText(String.valueOf(user.getLevel()));

                    mTvGender.setText(user.getGender() == Gender.MALE ? "男" :
                            "女");

                    mTvRecord.setText(user.getVictoryNumber()
                            + res.getString(R.string.user_sheng)
                            + user.getFailureNumber() +
                            res.getString(R.string.user_fu)
                            );

                    mTvGold.setText(String.valueOf(user.getGold()));

//                    mTvIngot.setText(String.valueOf(user.getMoney()));

//                    mTvIntegral.setText(String.valueOf(user.getScore()));

                    mTvTitle.setText(user.getRank());

                    mTvNickName.setText(user.getNickname());
                    
                    mTvUserId.setText(user.userId);

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addListener();
        
        setContentView(R.layout.common_progressbar);

        PersonCenterReq request = new PersonCenterReq();

        doSend(request);
    }

    @Override
    protected void onResume() {
        super.onResume();

        addListener();

        PersonCenterReq request = new PersonCenterReq();

        doSend(request);

    }

    private void initView() {

        res = getResources();

        mTvLevel = (TextView) findViewById(R.id.tv_user_level);
        mTvGender = (TextView) findViewById(R.id.tv_user_gender);
        mTvRecord = (TextView) findViewById(R.id.tv_user_records);
        mTvGold = (TextView) findViewById(R.id.tv_user_gold_num);
//        mTvIngot = (TextView) findViewById(R.id.tv_user_ingot);
//        mTvIntegral = (TextView) findViewById(R.id.tv_user_integral);
        mTvTitle = (TextView) findViewById(R.id.tv_user_title);
        mTvNickName = (TextView) findViewById(R.id.tv_user_nickname);
        mTvUserId = (TextView) findViewById(R.id.tv_user_id);

    }

    @Override
    public void onReceived(BaseMessage response) {
        super.onReceived(response);
        
        setContentView(R.layout.individual_actor_info);

        initView();
        if (response instanceof PersonCenterResp) {
            if (((PersonCenterResp) response).isSucceeded()) {
                PersonCenterResp resp = (PersonCenterResp) response;
                user = new UserInfo(resp.getUid());
                user.setUserId(resp.getEmail());
                user.setGold(resp.getMoneyGold());
                user.setMoney(resp.getMoneyHeart());
                user.setScore(resp.getScore());
                user.setLevel(resp.getLevel());
                user.setGender((resp.getSex() == RegisterUserReq.SEX_MALE) ?
                        Gender.MALE : Gender.FEMALE);
                user.setVictoryNumber(resp.getWinCount());
                user.setFailureNumber(resp.getLostCount());
                user.setNickname(resp.getNickName());
                
                switch (resp.getPlayStatus()) {
                    case PersonCenterResp.PLAY_STATUS_FREE:
                        user.setStatus(PlayersStatus.IDLE);
                        break;
                    case PersonCenterResp.PLAY_STATUS_IN_GAME:
                        user.setStatus(PlayersStatus.FIGHTING);
                        break;
                    case PersonCenterResp.PLAY_STATUS_WAITING:
                        user.setStatus(PlayersStatus.WAITING);
                        break;
                    default:
                        user.setStatus(PlayersStatus.WAITING);
                        break;
                }
                user.setRank(resp.getTitle()); // 头衔
                user.setExperience(resp.getCurExperience());
                user.setNextExperience(resp.getNextExperience());

                // mInformations = StringsAppendUtil.append(mInformations,
                // user);

                mHandler.sendEmptyMessage(0);
            }
            removeListener();
        }
       
        if (response instanceof LogoutUserResp) {

            finish();

        }
    }

}

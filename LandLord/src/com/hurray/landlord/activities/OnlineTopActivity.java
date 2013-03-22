
package com.hurray.landlord.activities;

import com.hurray.landlord.R;
import com.hurray.landlord.adapter.OnlineTopListAdapter;
import com.hurray.landlord.entity.Gender;
import com.hurray.lordserver.protocol.message.base.BaseMessage;
import com.hurray.lordserver.protocol.message.rank.RankListReq;
import com.hurray.lordserver.protocol.message.rank.RankListResp;
import com.hurray.lordserver.protocol.message.rank.RankListResp.RankListSubject;
import com.hurray.lordserver.protocol.message.rank.RankSystemReq;
import com.hurray.lordserver.protocol.message.rank.RankSystemResp;
import com.hurray.lordserver.protocol.message.rank.RankSystemResp.UserInfo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;


import java.util.ArrayList;

public class OnlineTopActivity extends BaseNetActivity {

	private RankListSubject[] randList;
    
    private Button mBtnLookOver;

//    private Button mBtnLevelTop;
//
//    private Button mBtnWealthTop;
    
    private ListView mListView;

    private OnlineTopListAdapter mAdapter;

    private TextView mTvWhat;

    private ArrayList<UserInfo> mArrayList;

    private boolean mIsLevelList;

    private ProgressBar mProgressBar;

    private int mCurrPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_top_activity);

        initView();

        initAdapter();
        
//        reqLevelRank();
    }

    @Override
    protected void onResume() {
        super.onResume();
        super.addListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        super.removeListener();
    }

    @Override
    public void onReceived(BaseMessage response) {
    	System.out.println(response.getName());
    	if(response instanceof RankListResp){
    		if (((RankListResp) response).isSucceeded()) {
				initButtonList(((RankListResp) response).getRankListSubject());
			}
    	} else if (response instanceof RankSystemResp) {
            RankSystemResp r = (RankSystemResp) response;
            if (r.isSucceeded()) {
            	if(this.randList != null){
            		setRand(r.getType());
            		updateArrayList(r.getUserInfo());
                    updateAdapter();
            	}
//                int type = r.getType();
//                if (type == Constants.RANK_LEVEL_TYPE) {
//                    if (mIsLevelList) {
//                        updateArrayList(r.getUserInfo());
//                        updateAdapter();
//                    }
//                } else if (type == Constants.RANK_MONEY_TYPE) {
//                    if (!mIsLevelList) {
//                        updateArrayList(r.getUserInfo());
//                        updateAdapter();
//                    }
//                }
            }

            // removeListener();
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void updateArrayList(UserInfo[] users) {
        mArrayList.clear();
        mCurrPosition = -1;
        for (int i = 0; i < users.length; i++) {
            mArrayList.add(users[i]);
        }
    }

    private void updateAdapter() {
        mAdapter.changeData(mArrayList, mIsLevelList);
        mAdapter.notifyDataSetChanged();
    }

//    private void reqLevelRank() {
//        reqRank(Constants.RANK_LEVEL_TYPE);
//    }
//
//    private void reqWealthRank() {
//        reqRank(Constants.RANK_MONEY_TYPE);
//    }
    
    private void reqRank(RankListSubject rank) {
//        if (Constants.RANK_LEVEL_TYPE == type) {
//            mTvWhat.setText("等级");
//            mIsLevelList = true;
//        } else {
//            mTvWhat.setText("财富");
//            mIsLevelList = false;
//        }
    	
        mArrayList.clear();
        mCurrPosition = -1;
        updateAdapter();

        mProgressBar.setVisibility(View.VISIBLE);

        RankSystemReq req = new RankSystemReq();
        req.setType(rank.getType());
        
        doSend(req);

    }
    
    private void setRand(int index){
    	mTvWhat.setText(randList[index].getTitle());
    }

    private void initView() {
        mBtnLookOver = (Button) findViewById(R.id.btn_online_look_over);
        mBtnLookOver.setOnClickListener(mLookOverListener);

//        	mBtnLevelTop = (Button) findViewById(R.id.btn_top_level);
//        	mBtnLevelTop.setOnClickListener(mLevelListener);
//        	
//        	mBtnWealthTop = (Button) findViewById(R.id.btn_top_wealth);
//        	mBtnWealthTop.setOnClickListener(mWealthListener);
        
        mTvWhat = (TextView) findViewById(R.id.iv_level_or_wealth);

        mProgressBar = (ProgressBar) findViewById(R.id.progess_bar);

        mListView = (ListView) findViewById(R.id.lv_online_top);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrPosition = position;
                // showPersonInfo();
                mAdapter.setmPos(mCurrPosition);
                mAdapter.notifyDataSetChanged();
            }
        });
        
    	doSend(new RankListReq());
    }
    
    private void initButtonList(final RankListSubject[] list){
    	if(list != null){
    		this.randList = list;
    		LinearLayout lly = (LinearLayout)findViewById(R.id.ll_btn_top);
    		lly.removeAllViews();
    		Button btn = null;
    		for (int i = 0; i < list.length; ++i) {
    			btn = new Button(OnlineTopActivity.this);
    			btn.setTextColor(Color.WHITE);
    			btn.setText(list[i].getName());
    			btn.setBackgroundResource(R.drawable.net_btn);
    			btn.setGravity(Gravity.CENTER);
    			btn.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
    					LayoutParams.WRAP_CONTENT, 1));
    			final int ind = i;
    			btn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						reqRank(list[ind]);
					}
				});
    			lly.addView(btn);
    		}
    		reqRank(list[0]);
    	} else {
    		System.out.println("按钮列表为空！！！");
    	}
    }

    private void initAdapter() {
        mArrayList = new ArrayList<UserInfo>();
        mAdapter = new OnlineTopListAdapter(getApplicationContext(), mArrayList, mIsLevelList);
        mListView.setAdapter(mAdapter);
    }

    private void showPersonInfo() {
        if (mCurrPosition >= 0 && mCurrPosition < mArrayList.size()) {
            Intent i = new Intent(OnlineTopActivity.this, PopPersonInfoActivity.class);
            UserInfo serverUser = mArrayList.get(mCurrPosition);
            com.hurray.landlord.entity.UserInfo localUser = new com.hurray.landlord.entity.UserInfo();
            localUser.setNickname(serverUser.nickname);
            if (serverUser.sex == 0) {
                localUser.setGender(Gender.MALE);
            } else {
                localUser.setGender(Gender.FEMALE);
            }
            localUser.setGold(serverUser.moneyGold);
            localUser.setMoney(serverUser.moneyHeart);
            localUser.setLevel(serverUser.level);
            localUser.setRank(serverUser.title);
            localUser.setScore(serverUser.point);
            localUser.setVictoryNumber(serverUser.winCount);
            localUser.setFailureNumber(serverUser.lostCount);
            localUser.setValue(serverUser.value);
            i.putExtra(PopPersonInfoActivity.USER_INFO, localUser);
            startActivity(i);
        }
    }

//    private OnClickListener mLevelListener = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            reqLevelRank();
//        }
//    };

//    private OnClickListener mWealthListener = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            reqWealthRank();
//        }
//    };

    private OnClickListener mLookOverListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showPersonInfo();
        }
    };

}

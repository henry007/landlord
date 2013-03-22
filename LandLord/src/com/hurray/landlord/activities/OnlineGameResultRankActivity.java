
package com.hurray.landlord.activities;

import com.hurray.landlord.R;
import com.hurray.landlord.entity.OnlineRankInfos;
import com.hurray.landlord.net.socket.srv.ServerMatchResult;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class OnlineGameResultRankActivity extends Activity {

    public static final String GAME_RESULT_RANK = "game_result_rank";

    private ListView mRankListView;

    private ImageButton mBack;

    private ListAdapter mRankAdapter;

    private OnlineRankInfos mRankInfos;

    private ServerMatchResult mMatchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result_rank);

        Intent intent = getIntent();

        // mRankInfos = (OnlineRankInfos)
        // intent.getSerializableExtra(GAME_RESULT_RANK);

        mMatchResult = (ServerMatchResult) intent.getSerializableExtra(GAME_RESULT_RANK);

        intent.removeExtra(GAME_RESULT_RANK);

        if (mMatchResult != null) {

            mRankAdapter = new ListAdapter();

            mRankListView = (ListView) findViewById(R.id.list_rank_info);

            mRankListView.setAdapter(mRankAdapter);

            mBack = (ImageButton) findViewById(R.id.btn_back);
            
            mBack.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                 finish();
                    
                }
            });
        }
    }

    private class ListAdapter extends BaseAdapter {

        public ListAdapter() {

        }

        @Override
        public int getCount() {
            return mMatchResult.getResults().length;
        }

        @Override
        public Object getItem(int position) {

            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {

                convertView = LayoutInflater.from(OnlineGameResultRankActivity.this).inflate(
                        R.layout.game_result_rank_listview, null);

                holder = new ViewHolder();

                holder.mNickNameTV = (TextView) convertView.findViewById(R.id.tv_nickname);

                holder.mRankTV = (TextView) convertView.findViewById(R.id.tv_rank);

                holder.mScoreTV = (TextView) convertView.findViewById(R.id.tv_score);

                convertView.setTag(holder);

            }

            else {

                holder = (ViewHolder) convertView.getTag();

            }

            holder.mNickNameTV.setText(mMatchResult.getResults()[position].nickName);

            holder.mRankTV.setText( mMatchResult.getResults()[position].rankInfo);

            holder.mScoreTV.setText(String.valueOf(mMatchResult.getResults()[position].score));

            return convertView;
        }

        private class ViewHolder {

            public TextView mRankTV, mNickNameTV, mScoreTV;
        }

    }
}


package com.hurray.landlord.adapter;

import com.hurray.landlord.R;
import com.hurray.landlord.entity.UserInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class OfflineTopListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;

    private ArrayList<UserInfo> mTopList;

    public OfflineTopListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setUserInfoList(ArrayList<UserInfo> topList) {
        mTopList = topList;
    }

    @Override
    public int getCount() {
        if (mTopList != null) {
            return mTopList.size();
        }
        return 0;
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
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.offline_top_list_item, null);

            holder = new ViewHolder();
            holder.mNameView = (TextView) convertView.findViewById(R.id.tv_name);
            holder.mScoreView = (TextView) convertView.findViewById(R.id.tv_score);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mTopList != null) {
            holder.mNameView.setText(mTopList.get(position).getNickname());
            holder.mScoreView.setText(mTopList.get(position).getScore().toString());
        } else {
            holder.mNameView.setText(null);
            holder.mScoreView.setText(null);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView mNameView;
        TextView mScoreView;
    }

}

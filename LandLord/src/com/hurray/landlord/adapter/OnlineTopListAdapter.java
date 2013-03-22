
package com.hurray.landlord.adapter;

import com.hurray.landlord.R;
import com.hurray.lordserver.protocol.message.rank.RankSystemResp.UserInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class OnlineTopListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;

    private Context mContext;

    private ArrayList<UserInfo> mTopList;

//    private boolean mIsLevelList;

    private int mPos;

    public OnlineTopListAdapter(Context context, ArrayList<UserInfo> arrayList,
            boolean isLevelList) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mTopList = arrayList;
//        mIsLevelList = isLevelList;
    }

    public void changeData(ArrayList<UserInfo> arrayList, boolean isLevelList) {
        mTopList = arrayList;
//        mIsLevelList = isLevelList;
    }

    @Override
    public int getCount() {
        return mTopList.size();
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
            convertView = mInflater.inflate(R.layout.online_top_list_item, null);
            holder = new ViewHolder();
            holder.mTextTopNum = (TextView) convertView.findViewById(R.id.tv_top_num);
            holder.mTextName = (TextView) convertView.findViewById(R.id.tv_top_name);
            holder.mTextLevelOrWealth = (TextView) convertView.findViewById(R.id.tv_top_level_or_wealth);
            holder.root =  (LinearLayout) convertView.findViewById(R.id.root_layout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        holder.root.setBackgroundResource(mPos==position ? R.drawable.online_game_list_item_normal : 0);
        UserInfo userInfo = mTopList.get(position);
        holder.mTextName.setText(userInfo.nickname);
        holder.mTextTopNum.setText(String.valueOf(position + 1));
//        if (mIsLevelList) {
//            holder.mTextLevelOrWealth.setText(String.valueOf(userInfo.level));
//        } else {
//            holder.mTextLevelOrWealth.setText(String.valueOf(userInfo.moneyGold));
//        }
        holder.mTextLevelOrWealth.setText(userInfo.value);
        return convertView;
    }

    private static class ViewHolder {
        TextView mTextTopNum;
        TextView mTextName;
        TextView mTextLevelOrWealth;
        LinearLayout root;
    }

    public void setmPos(int mPos) {
        this.mPos = mPos;
    }

}

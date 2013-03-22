package com.hurray.landlord.adapter;

import com.hurray.landlord.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class OfflineAchImageAdapter extends BaseAdapter {
    
	private Context mContext;
	
	private Integer[] mOfflineAchIds;

	public OfflineAchImageAdapter(Context ctx, ArrayList<Integer> achIds, ArrayList<Integer> selectAchIds) {
		mContext = ctx;
		mOfflineAchIds = getOfflineAchIds(achIds, selectAchIds);
	}

	@Override
	public int getCount() {
		return mOfflineAchIds.length;
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
		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(mContext);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		} else {
			imageView = (ImageView) convertView;
		}
		imageView.setImageResource(mOfflineAchIds[position]);
		
		return imageView;
	}
	
	public void changeAchIds(ArrayList<Integer> achIds, ArrayList<Integer> selectAchIds) {
		mOfflineAchIds = getOfflineAchIds(achIds, selectAchIds);
	}
	
	private Integer[] getOfflineAchIds(ArrayList<Integer> achIds, ArrayList<Integer> selectAchIds) {
		// 替换已获得成就
		for (int i = 0; i < achIds.size(); i++) {
			int achId = achIds.get(i);
			mListViewIds[achId+2] = mAchievedIds[achId];
		}
		// 替换已选择成就
		for (int i = 0; i < selectAchIds.size(); i++) {
			int achId = selectAchIds.get(i);
			mListViewIds[achId+2] = mSelectedIds[achId];
		}
		
		return mListViewIds;
	}
	
	// +2
    private Integer[] mListViewIds = {
            R.drawable.offline_lv_transparent,
            R.drawable.offline_lv_transparent,
            R.drawable.offline_ach_2_0,
            R.drawable.offline_ach_1_0,
           
            R.drawable.offline_ach_3_0,
            
            R.drawable.offline_ach_4_0,
            R.drawable.offline_ach_5_0, 
            R.drawable.offline_ach_6_0,
            
            R.drawable.offline_ach_7_0,
            R.drawable.offline_ach_8_0,
            
            R.drawable.offline_lv_transparent,
            R.drawable.offline_lv_transparent
    };
    
    // 索引对应AchievesRec中Id
    private Integer[] mAchievedIds = {
            R.drawable.offline_ach_2_1,
            R.drawable.offline_ach_1_1, 
            R.drawable.offline_ach_3_1,
            
            R.drawable.offline_ach_4_1,
            R.drawable.offline_ach_5_1,
            R.drawable.offline_ach_6_1,
            
            R.drawable.offline_ach_7_1, 
            R.drawable.offline_ach_8_1
    };
    
    // 索引对应AchievesRec中Id
    private Integer[] mSelectedIds = {
            R.drawable.offline_ach_2,
            R.drawable.offline_ach_1, 
            R.drawable.offline_ach_3,
            
            R.drawable.offline_ach_4,
            R.drawable.offline_ach_5,
            R.drawable.offline_ach_6,
            
            R.drawable.offline_ach_7, 
            R.drawable.offline_ach_8 
    };
}

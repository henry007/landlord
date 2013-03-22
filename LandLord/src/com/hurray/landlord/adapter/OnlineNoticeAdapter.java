package com.hurray.landlord.adapter;

import com.hurray.landlord.R;
import com.hurray.landlord.activities.OnlineNoticeActivity;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OnlineNoticeAdapter extends BaseAdapter {

	private OnlineNoticeActivity context;
	private LayoutInflater mInflater;
	private String[] title;
	public TextView[] retv;
	public OnlineNoticeAdapter(OnlineNoticeActivity context, String[] title){
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.title = title;
		this.retv = new TextView[title.length];
	}
	
	@Override
	public int getCount() {
		if(title != null){
			return title.length;
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
		convertView = mInflater.inflate(R.layout.online_notice_list_item, null);
		this.retv[position] = (TextView)convertView.findViewById(R.id.text_title);
		this.retv[position].setText(title[position]);
		if(position == context.indexPrd){
			this.retv[position].setTextColor(Color.WHITE);
		}
		return convertView;
	}
	
}

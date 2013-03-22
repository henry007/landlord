package com.hurray.landlord.activities;

import com.hurray.landlord.R;
import com.hurray.landlord.adapter.OnlineNoticeAdapter;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class OnlineNoticeActivity extends BaseNetActivity{
	
	public static final String key = "notice";
	
	private String[] title = null;
	private String[] text = null;
	
	private OnlineNoticeAdapter adapter = null;
	private ListView button = null;
	private TextView textView = null;
	private ImageButton back = null;
	public int indexPrd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.online_notice_activity);
		String data = getIntent().getStringExtra(key);
		parseData(data.replaceAll("；", ";"), "\\;", "\\#");
		indexPrd = 0;
		textView = (TextView)findViewById(R.id.textCon);
		textView.setText(text[indexPrd]);
		
		adapter = new OnlineNoticeAdapter(this, title);
		button = (ListView)findViewById(R.id.lw_title);
		button.setAdapter(adapter);
		button.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long index1) {
				if (adapter.retv[indexPrd] != null) {
					adapter.retv[indexPrd].setTextColor(Color.rgb(222, 90,
							173));
				}
				textView.setText(text[index]);
				TextView tv_title = (TextView)view.findViewById(R.id.text_title);
				tv_title.setTextColor(Color.WHITE);
//				tv_title.setTextColor(Color.rgb(0xde, 0x5a, 0xad));
				indexPrd = index;
			}
		});
		back = (ImageButton)findViewById(R.id.notice_back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				title = null;
				text = null;
	            finish();
			}
		});
//		if (adapter.retv[indexPrd] != null) {
//			adapter.retv[indexPrd].setTextColor(Color.WHITE);
//			adapter.retv[indexPrd].setTextColor(Color.rgb(0xe0, 0x8a, 0xb6));
//		}
	}
	
	private void parseData(String data, String split1, String split2){
		try {
			if (data != null && data.length() > 0) {
				data = data.replaceAll("［", "[");
				data = data.replaceAll("【", "[");
				data = data.replaceAll("］", "]");
				data = data.replaceAll("】", "]");
				String[] list = data.split(split2);
				title = new String[list.length];
				text = new String[list.length];
				int start, end;
				for (int i = 0; i < list.length; ++i) {
					start = list[i].indexOf("[");
					end = list[i].indexOf("]");
					title[i] = list[i].substring(start != -1 ? start + 1 : start, end);
					text[i] = list[i].substring(end + 1, list[i].length());
				}
				
//				String[] list = data.split(split2);
//				title = new String[list.length];
//				text = new String[list.length];
//				String[] temp = null;
//				for (int i = 0; i < list.length; ++i) {
//					temp = list[i].split(split1);
//					if (temp.length > 0) {
//						title[i] = temp[0];
//					}
//					if (temp.length > 1) {
//						text[i] = temp[1];
//					}
//				}
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == 4){
			finish();
		}
		return false;
	}
}

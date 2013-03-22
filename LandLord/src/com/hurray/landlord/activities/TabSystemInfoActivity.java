package com.hurray.landlord.activities;

import com.hurray.landlord.R;
import com.hurray.landlord.sdk.SdkManagerJuZi;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.ToastUtil;
import com.hurray.lordserver.protocol.message.Constants;
import com.hurray.lordserver.protocol.message.base.BaseMessage;
import com.hurray.lordserver.protocol.message.card.SystemBoxReq;
import com.hurray.lordserver.protocol.message.card.SystemBoxResp;
import com.hurray.lordserver.protocol.message.card.SystemBoxResp.SystemBoxResult;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TabSystemInfoActivity extends BaseNetActivity implements
		OnScrollListener {

	private ListView systemInfos;

	private static final String TAG = "TabSystemInfoActivity";

	private int itemSize = 0;

	private ListAdapter listAdapter;

	private View view;

	private Button loadMoreBtn;

	private int offset = 0;

	private int pageSize = 3;

	private ProgressBar bar = null;

	private ProgressBar mainBar = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.individual_system_info);

		mainBar = (ProgressBar) findViewById(R.id.proBar);

		view = getLayoutInflater().inflate(R.layout.system_infos_footerview,
				null);

		loadMoreBtn = (Button) view.findViewById(R.id.loadMoreButton);

		bar = (ProgressBar) view.findViewById(R.id.loadProBar);

		systemInfos = (ListView) findViewById(R.id.list_systeminfo);

		bar.setVisibility(View.GONE);

		loadMoreBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SystemBoxReq req = new SystemBoxReq();
				offset += pageSize;
				req.setOffset(offset);
				req.setPagesize(pageSize);
				doSend(req);
				loadMoreBtn.setVisibility(View.GONE);
				bar.setVisibility(View.VISIBLE);
				listAdapter.notifyDataSetChanged();
			}
		});

		SystemBoxReq req = new SystemBoxReq();

		req.setOffset(offset);

		req.setPagesize(pageSize);

		doSend(req);

		// startView();
	}

	@Override
	public void onReceived(BaseMessage response) {

		if (response instanceof SystemBoxResp) {

			mainBar.setVisibility(View.GONE);

			bar.setVisibility(View.GONE);

			SystemBoxResp resp = (SystemBoxResp) response;

			SystemBoxResult[] results = resp.getResults();

			if (results.length != pageSize) {

				if (itemSize == 0) {

					itemSize += results.length;

				} else {
					itemSize += results.length;
					// itemSize =+ pageSize;
					//
					// systemInfos.removeFooterView(view);

					// ToastUtil.show("去掉FOOTER按钮");
				}

			} else {

				itemSize += pageSize;

				loadMoreBtn.setVisibility(View.VISIBLE);

				loadMoreBtn.setText("查看更多...");

			}
			LogUtil.d(TAG, "itemSize:" + itemSize);

			if (listAdapter == null) {

				listAdapter = new ListAdapter(this);

				listAdapter.changeContent(results);

				listAdapter.changeItemSize(itemSize);

				systemInfos.addFooterView(view);

				systemInfos.setAdapter(listAdapter);

			} else {

				listAdapter.changeContent(results);

				listAdapter.changeItemSize(itemSize);

				listAdapter.notifyDataSetChanged();

			}
		}

	}

	private class ListAdapter extends BaseAdapter {

		private Context c;

		ViewHolder holder = null;

		List<SystemBoxResult> infoList = new ArrayList<SystemBoxResult>();

		// private int itemSize = 0;

		public void changeItemSize(int size) {

			itemSize = +size;
		}

		public void changeContent(SystemBoxResult[] infos) {

			for (SystemBoxResult info : infos) {
				infoList.add(info);
			}
			LogUtil.d(TAG, "infoList.size()-->" + infoList.size());
		}

		public ListAdapter(Context c) {
			this.c = c;
		}

		@Override
		public int getCount() {
			return itemSize;
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

			if (convertView == null) {

				holder = new ViewHolder();

				convertView = LayoutInflater.from(c).inflate(
						R.layout.system_info_listview, null);

				holder.leftOne = (TextView) convertView
						.findViewById(R.id.tv_time);

				holder.rightOne = (TextView) convertView
						.findViewById(R.id.tv_line_one);

				// holder.rightTwo = (TextView)
				// convertView.findViewById(R.id.tv_line_two);
				//
				// holder.rightThree = (TextView)
				// convertView.findViewById(R.id.tv_line_three);

				convertView.setTag(holder);

			}

			else {

				holder = (ViewHolder) convertView.getTag();

			}
			if (infoList.isEmpty()) {

				ToastUtil.show("infoList空了 !!");
			} else {
				// 2012-7-10 18:09:36

				// String time = infoList.get(position).time;

				try {
					String str = infoList.get(position).time;
					Date date = new Date(str);
					SimpleDateFormat df = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					holder.leftOne.setText(handleTime(df.format(date)));
				} catch (Exception e) {

					String str = infoList.get(position).time;

					StringBuffer sb1 = new StringBuffer();

					StringBuffer sb2 = new StringBuffer();

					StringBuffer sb3 = new StringBuffer();

					String[] strs = str.split("-");

					String[] ts = null;

					for (int i = 0; i < strs.length; i++) {
						if (i == 2) {
							ts = strs[i].split(" ");
							for (int j = 0; j < ts.length; j++) {
								// System.out.println("j"+j+"-->"+temps[j]);
								if (j == 1) {
									String[] temps2 = ts[j].split(":");
									// sb2.append(temps2[0] + ":" + temps2[1]);
									sb2.append(temps2[0] + "时");
								}
							}
						}
					}

					// sb1.append(strs[1] + "." + ts[0]);
					sb1.append(strs[1] + "月" + ts[0] + "日");
					sb3.append(sb1.toString());
					// sb3.append(" ");
					sb3.append(sb2.toString());

					String time = sb3.toString();

					holder.leftOne.setText(time);
				}

				String str0 = infoList.get(position).message.replaceAll(
						Constants.TEXT_FOLD_LINE_SPLITE, "\n");
				SpannableString msp = SdkManagerJuZi.getInstance().getSpannable(str0);
				if (msp != null) {
					holder.rightOne.setText(msp);
				} else {
					holder.rightOne.setText(str0);
				}

				// String temp = infoList.get(position).message;
				// String[] temps = temp.split(Constants.TEXT_FOLD_LINE_SPLITE);
				// switch (temps.length) {
				//
				// case 1:
				//
				// holder.rightOne.setText(temps[0]);
				//
				// break;
				//
				// case 2:
				//
				// holder.rightOne.setText(temps[0] + "\n" + temps[1]);
				//
				// break;
				//
				// case 3:
				//
				// holder.rightOne.setText(temps[0] + "\n" + temps[1] + "\n" +
				// temps[2]);
				//
				// break;
				//
				// default:
				//
				// holder.rightOne.setText(temp);
				//
				// break;
				// }

			}

			// holder.rightTwo.setText(exchangePasswords[position]);
			//
			// holder.rightThree.setText(exchangeRecords[position]);

			return convertView;
		}

		private String handleTime(String str) {
			StringBuffer sb1 = new StringBuffer();

			StringBuffer sb2 = new StringBuffer();

			StringBuffer sb3 = new StringBuffer();

			String[] strs = str.split("-");

			String[] ts = null;

			for (int i = 0; i < strs.length; i++) {
				if (i == 2) {
					ts = strs[i].split(" ");
					for (int j = 0; j < ts.length; j++) {
						// System.out.println("j"+j+"-->"+temps[j]);
						if (j == 1) {
							String[] temps2 = ts[j].split(":");
							// sb2.append(temps2[0] + ":" + temps2[1]);
							sb2.append(temps2[0] + "时");
						}
					}
				}

				// System.out.println(i + "-->" + strs[i]);
			}

			// sb1.append(strs[1] + "." + ts[0]);
			sb1.append(strs[1] + "月" + ts[0] + "日");
			sb3.append(sb1.toString());
			// sb3.append(" ");
			sb3.append(sb2.toString());

			String time = sb3.toString();
			return time;
		}

		private class ViewHolder {

			public TextView leftOne, rightOne;
			// , rightTwo, rightThree;

		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		// if (totalItemCount == itemSize) {
		//
		// systemInfos.removeFooterView(view);
		//
		// ToastUtil.show("数据加载完毕，去掉FOOTERVIEW！！");
		//
		// }

	}

}

package com.hurray.landlord.adapter;

import com.hurray.landlord.R;
import com.hurray.landlord.activities.OnlineGameListActivity;
import com.hurray.landlord.entity.GameInfo;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.ToastUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OnlineGameListAdapter extends BaseAdapter {
	private static final String name = "赛制名称"; 
	private ArrayList<GameInfo> mGameList;

	private LayoutInflater mInflater;

	private Context context;

	public int clickIndex = -1;

	public OnlineGameListAdapter(Context context, ArrayList<GameInfo> gameList) {
		mGameList = gameList;
		mInflater = LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public int getCount() {
		return mGameList.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.online_game_list_item,
					null);

			holder = new ViewHolder();
			holder.mGameName = (TextView) convertView
					.findViewById(R.id.tv_game_name);
			holder.mGameDesc = (TextView) convertView
					.findViewById(R.id.tv_game_desc);
			holder.mGameBtn = (ImageButton) convertView
					.findViewById(R.id.btn_game_detail);
			holder.mMatchTime = (TextView) convertView
					.findViewById(R.id.tv_match_time);
			holder.mPlayerNum = (TextView) convertView
					.findViewById(R.id.tv_player_num);
			holder.root = (LinearLayout) convertView
					.findViewById(R.id.root_layout);
			holder.ll_expand = (LinearLayout) convertView
					.findViewById(R.id.ll_expand);

			holder.mGameBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					int curPos = getsClickIndex();

					final Dialog dialog = new Dialog(context, R.style.dialog);

					View view = LayoutInflater.from(context).inflate(
							R.layout.dialog_deail_match_info, null);

					TextView mWaitText = (TextView) view
							.findViewById(R.id.tv_detail_match_info);

					TextView mTitle = (TextView) view
							.findViewById(R.id.tv_detail_match_info_title);
					
					Button btnClose = (Button) view
							.findViewById(R.id.btn_close);

					String tempContent = mGameList.get(curPos).getGameInfo();

					// tempContent = tempContent.replace("/n", "\n");
					//
					// tempContent = tempContent.replace(":", ":\n");
					//
					// mWaitText.setText(tempContent);

					String[] list = getTextList(tempContent);
					if(list != null){
						if(list[0] != null){
							mTitle.setText(list[0]);
						}
						if(list[1] != null){
							mWaitText.setText(list[1]);
						}
					}

					btnClose.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							dialog.dismiss();

						}
					});

					dialog.setContentView(view);

					LayoutParams lp = dialog.getWindow().getAttributes();

					lp.height = (int) (((Activity) context).getWindowManager()
							.getDefaultDisplay().getHeight() / 1.5);

					lp.width = (int) (((Activity) context).getWindowManager()
							.getDefaultDisplay().getWidth() / 1.5);

					dialog.getWindow().setAttributes(lp);

					dialog.show();

				}
			});

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		GameInfo gameInfo = mGameList.get(position);
		holder.mGameName.setText(gameInfo.getName());
		holder.mMatchTime.setText(gameInfo.getmMatchTime());
		holder.mPlayerNum.setText(gameInfo.getmPlayerNum() + "人");
		holder.root
				.setBackgroundResource(gameInfo.isShowDesc() ? R.drawable.bg_game_list_item
						: R.drawable.online_game_list_item_normal);
		if (gameInfo.isShowDesc()) {

			holder.mGameDesc.setVisibility(View.VISIBLE);
			holder.mGameDesc.setText(gameInfo.getDesc());
			holder.mGameBtn.setVisibility(View.VISIBLE);
			holder.ll_expand.setVisibility(View.VISIBLE);
			holder.mGameName.setTextColor(context.getResources().getColor(
					android.R.color.white));
			holder.mMatchTime.setTextColor(context.getResources().getColor(
					android.R.color.white));
			holder.mPlayerNum.setTextColor(context.getResources().getColor(
					android.R.color.white));

		} else {
			holder.mGameDesc.setVisibility(View.GONE);
			holder.mGameBtn.setVisibility(View.GONE);
			holder.ll_expand.setVisibility(View.GONE);
			holder.mGameName.setTextColor(context.getResources().getColor(
					R.color.common_color));
			holder.mMatchTime.setTextColor(context.getResources().getColor(
					R.color.common_color));
			holder.mPlayerNum.setTextColor(context.getResources().getColor(
					R.color.common_color));
		}

		return convertView;
	}

	public String[] getTextList(String text) {
		String[] res = null;
		try {
			String[] list = text.split("/n");
			if (list != null) {
				res = new String[2];
				StringBuffer result = new StringBuffer();
				String temp = null;
				int index = 0;
				for (int i = 0; i < list.length; ++i) {
					index = list[i].indexOf(":", 0);
					temp = list[i].substring(0, index);
					if (name.equals(temp)) {
						if (index + 1 < list[i].length()) {
							res[0] = list[i].substring(index + 1,
									list[i].length());
						}
					} else {
						result.append("【" + temp + "】");
						if (index + 1 < list[i].length()) {
							temp = list[i].substring(index + 1,
									list[i].length());
							// 如果是奖品类，有多个子串
							String[] jiangpin = temp.split("第");
							if (jiangpin.length > 1) {
								StringBuffer tem = new StringBuffer();
								boolean isFirstLine = true;
								for (int j = 0; j < jiangpin.length; ++j) {
									if (jiangpin[j].length() > 0) {
										if (isFirstLine) {
											jiangpin[j] = "第" + jiangpin[j];
											isFirstLine = false;
										} else {
											jiangpin[j] = "\n                第"
													+ jiangpin[j];
										}
										tem.append(jiangpin[j]);
									}
								}
								result.append(tem);
							} else {
								result.append(temp);
							}
						}
						result.append("\n");
					}
				}
				res[1] = result.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return res;
	}

	private static class ViewHolder {
		TextView mGameName;
		TextView mGameDesc;
		TextView mPlayerNum;
		TextView mMatchTime;
		ImageButton mGameBtn;
		LinearLayout root;
		LinearLayout ll_expand;
	}

	public int getsClickIndex() {
		return clickIndex;
	}

	public void setClickIndex(int sClickIndex) {
		clickIndex = sClickIndex;
	}

}

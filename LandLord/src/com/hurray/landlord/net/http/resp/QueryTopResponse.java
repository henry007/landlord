package com.hurray.landlord.net.http.resp;

import com.hurray.landlord.entity.Top;
import com.hurray.landlord.entity.UserInfo;
import com.hurray.landlord.net.http.HttpResponse;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class QueryTopResponse extends HttpResponse {
	private static final long serialVersionUID = 586630741742432680L;
	private ArrayList<UserInfo> mTopList = new ArrayList<UserInfo>();
	private Top mFlag;
	private Integer topFlag;

	public ArrayList<UserInfo> getTopList() {
		return mTopList;
	}

	public Top getmFlag() {
		return mFlag;
	}

	@Override
	public String unPack(byte[] respContent) {
		String respMes = null;
		try {
			respMes = new String(respContent, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		respMes = filter(respMes);

		JSONObject json = getRespJsonObject(respMes);
		if (null != json) {
			// TODO 排行榜字段？
			JSONArray users = getJsonArray(json, "");
			if (null != users) {
				jsonArr2List(users, mTopList);
			}
		}
		
		try {
			topFlag = json.getInt("topFlag");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return respMes;
	}

	private void jsonArr2List(JSONArray users, ArrayList<UserInfo> topList) {
		for (int i = 0; i < users.length(); i++) {
			JSONObject user = getJsonObj(users, i);
			UserInfo userInfo = new UserInfo();
			userInfo.setNickname(getJsonString(user, "nickName"));
			switch (topFlag) {
			case 0:
				mFlag = Top.LEVEL;
				userInfo.setLevel(getJsonInt(user, "level", 1));
				userInfo.setTopLevelNum(getJsonInt(user, "toplevel", 0));
				break;
			case 1:
				mFlag = Top.WEALTH;
				userInfo.setGold(getJsonInt(user, "gold", 1000));
				userInfo.setTopWealthNum(getJsonInt(user, "toprich", 0));
				break;
			}
			
			topList.add(userInfo);
		}
	}

}

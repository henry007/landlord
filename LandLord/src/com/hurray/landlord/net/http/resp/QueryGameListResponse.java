package com.hurray.landlord.net.http.resp;

import com.hurray.landlord.entity.GameInfo;
import com.hurray.landlord.net.http.HttpResponse;

import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class QueryGameListResponse extends HttpResponse {
	private static final long serialVersionUID = 7240631149622336543L;
	private ArrayList<GameInfo> mGameList = new ArrayList<GameInfo>();
	
	public ArrayList<GameInfo> getmGameList() {
		return mGameList;
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
			// TODO 获取游戏列表
		}
		return null;
	}

}

package com.hurray.landlord.net.http.req;

import com.hurray.landlord.net.http.HttpRequest;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class QueryFriendsRequst extends HttpRequest {
	private static final long serialVersionUID = -3278267922393597103L;
	private Integer mUserId;

	public QueryFriendsRequst(String url, Integer userId) {
		super(url);
		mUserId = userId;
	}

	@Override
	public StringEntity pack() throws UnsupportedEncodingException {
		JSONObject json = new JSONObject();
		try {
			json.put("huserid", mUserId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		StringEntity entity = new StringEntity(json.toString(), HTTP.UTF_8);
		entity.setContentType(mContentType);
		return entity;
	}

	@Override
	public String toString() {
		return "QueryFriendsRequst [mUserId=" + mUserId + ", mReqURL="
				+ mReqURL + "]";
	}

}

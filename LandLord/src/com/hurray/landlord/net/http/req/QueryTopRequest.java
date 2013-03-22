package com.hurray.landlord.net.http.req;

import com.hurray.landlord.entity.Top;
import com.hurray.landlord.net.http.HttpRequest;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class QueryTopRequest extends HttpRequest {
	private static final long serialVersionUID = -2066422660527633073L;
	private Top mFlag;
	private Integer topFlag;

	public QueryTopRequest(String url, Top flag) {
		super(url);
		mFlag = flag;
	}

	@Override
	public StringEntity pack() throws UnsupportedEncodingException {
		switch (mFlag) {
		case LEVEL:
			topFlag = 0;
			break;
		case WEALTH:
			topFlag = 1;
			break;
		}
		JSONObject json = new JSONObject();
		try {
			json.put("topFlag", topFlag);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		StringEntity entity = new StringEntity(json.toString(), HTTP.UTF_8);
		entity.setContentEncoding(mContentType);
		
		return entity;
	}

	@Override
	public String toString() {
		return "QueryTopRequest [mFlag=" + mFlag + ", topFlag=" + topFlag
				+ ", mReqURL=" + mReqURL + "]";
	}
	
}

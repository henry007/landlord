package com.hurray.landlord.net.http.req;

import com.hurray.landlord.net.http.HttpRequest;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class UpdateSignatureRequest extends HttpRequest {
	private static final long serialVersionUID = 6653486619051455796L;
	private String mSignature;

	public UpdateSignatureRequest(String url, String signature) {
		super(url);
		mSignature = signature;
	}

	@Override
	public StringEntity pack() throws UnsupportedEncodingException {
		JSONObject json = new JSONObject();
		try {
			json.put("signature", mSignature);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		StringEntity entity = new StringEntity(json.toString(), HTTP.UTF_8);
		entity.setContentType(mContentType);
		
		return entity;
	}

	@Override
	public String toString() {
		return "UpdateSignatureRequest [mSignature=" + mSignature
				+ ", mReqURL=" + mReqURL + "]";
	}

}

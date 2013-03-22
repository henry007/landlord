package com.hurray.landlord.net.http.req;

import com.hurray.landlord.net.http.HttpRequest;

import org.apache.http.entity.AbstractHttpEntity;

import java.io.UnsupportedEncodingException;

public class QueryGameListRequest extends HttpRequest {
	private static final long serialVersionUID = -1218627862801427474L;

	public QueryGameListRequest(String url) {
		super(url);
	}
	
	@Override
	public AbstractHttpEntity pack() throws UnsupportedEncodingException {
		return null;
	}

	@Override
	public String toString() {
		return "QueryGameListRequest [mReqURL=" + mReqURL + "]";
	}

}

package com.hurray.landlord.net.http.resp;

import com.hurray.landlord.net.http.HttpResponse;

import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class UpdateSignatureResponse extends HttpResponse {
	private static final long serialVersionUID = 795123078383429827L;
	private boolean updateSuccess;
	
	public boolean isUpdateSuccess() {
		return updateSuccess;
	}

	@Override
	public String unPack(byte[] respContent) {
		String respMes = null;
		try {
			respMes = new String(respContent, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		JSONObject json = getRespJsonObject(respMes);
		if (null != json) {
			//TODO 替换 更新签名成功与否字段
			updateSuccess = getJsonBoolean(json, "updateSuccess", false);
		}
		return respMes;
	}

}

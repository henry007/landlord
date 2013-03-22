
package com.hurray.landlord.net.http;

import com.hurray.landlord.net.json.JsonHelper;

/**
 * @author sunzhibin
 */
public abstract class HttpResponse extends JsonHelper implements java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1149487785795203650L;

    private static final String TAG = "HttpResponse";

    public static final int RESULT_CODE_HTTP_ERROR = -1;

    public static final int RESULT_CODE_UNKNOWN = -2;

    public static final int RESULT_CODE_UNKNOWN_HOST = -3;

    public static final int RESULT_CODE_PROTOCOL_ERROR = -4;

    public static final int RESULT_CODE_LOCAL_SDCARD_FULL = -5;

    public static final int RESULT_CODE_OK = 0;

    private int mResultCode;

    private String mErrorMessage;

    /**
     * @return the result code
     */
    public int getResultCode() {
        return mResultCode;
    }

    /**
     * @param resultCode the result code to set
     */
    public void setResultCode(int resultCode) {
        mResultCode = resultCode;
    }

    /**
     * @return the error message
     */
    public String getErrorMessage() {
        return mErrorMessage;
    }

    /**
     * @param errorMessage the error message to set
     */
    public void setErrorMessage(String errorMessage) {
        mErrorMessage = errorMessage;
    }

    /**
     * if response,need override
     * 
     * @param respContent
     */
    public abstract String unPack(byte[] respContent);

}

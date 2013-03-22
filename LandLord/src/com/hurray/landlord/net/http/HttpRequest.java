/**
 * 
 */

package com.hurray.landlord.net.http;

import com.hurray.landlord.Constants;

import org.apache.http.Header;
import org.apache.http.entity.AbstractHttpEntity;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 * @author sunzhibin
 */
public abstract class HttpRequest implements java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1641647699955802465L;

    public static final String GET = "GET";

    public static final String POST = "POST";

    /** the http request url */
    protected String mReqURL;

    /** the http request method,POST as default */
    protected String mReqMethod;

    protected String mContentType;

    /** the data to post */
    // private byte[] mReqData;

    /** the extra object */
    protected Object[] mExtraValue;

    // private RequestMessage reqMessage;

    protected boolean mZipped;

    protected int mRepeatTimes;

    @SuppressWarnings("unused")
    private HttpRequest() {

    }

    public HttpRequest(String url) {
        mReqURL = url;
        mReqMethod = POST;
        mContentType = Constants.CONTENT_TYPE_PARAMETERS;
        mExtraValue = null;
        mRepeatTimes = 0;
    }

    public boolean isZipped() {
        return mZipped;
    }

    public void setZipped(boolean zipped) {
        mZipped = zipped;
    }

    /**
     * @return the reqMessage
     */
    /*
     * public RequestMessage getReqMessage() { return reqMessage; }
     *//**
     * @param reqMessage the reqMessage to set
     */
    /*
     * public void setReqMessage(RequestMessage reqMessage) { this.reqMessage =
     * reqMessage; }
     */

    /**
     * @return the extraValue
     */
    public Object[] getExtraValue() {
        return mExtraValue;
    }

    /**
     * @param extraValue the extraValue to set
     */
    public void setExtraValue(Object[] extraValue) {
        this.mExtraValue = extraValue;
    }

    /**
     * @return the reqMethod
     */
    public String getReqMethod() {
        return mReqMethod;
    }

    /**
     * @param reqMethod the reqMethod to set
     */
    public void setReqMethod(String reqMethod) {
        this.mReqMethod = reqMethod;
    }

    /**
     * @return the reqData
     */
    /*
     * public byte[] getReqData() { String data = pack(); if (data != null) {
     * try { mReqData = data.getBytes("utf-8"); } catch
     * (UnsupportedEncodingException e) { // TODO Auto-generated catch block
     * e.printStackTrace(); } } return mReqData; }
     *//**
     * @param reqData the reqData to set
     */
    /*
     * public void setReqData(byte[] reqData) { mReqData = reqData; }
     */
    /**
     * @return the reqURL
     */
    public String getReqURL() {
        return mReqURL;
    }

    /**
     * @param reqURL the reqURL to set
     */
    public void setReqURL(String reqURL) {
        mReqURL = reqURL;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        return mContentType;
    }

    /**
     * @param contentType the contentType to set
     */
    public void setContentType(String contentType) {
        mContentType = contentType;
    }

    /**
     * @return the RepeatTimes
     */
    public int getRepeatTimes() {
        return mRepeatTimes;
    }

    /**
     * @param times the repeat times to be set
     */
    public void setRepeatTimes(int times) {
        mRepeatTimes = times;
    }

    /**
     * if request message,need override
     */
    public abstract AbstractHttpEntity pack() throws UnsupportedEncodingException,
            FileNotFoundException;

    public Header getHeader() {
        return null;
    }
}

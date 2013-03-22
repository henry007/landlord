package com.hurray.landlord.server.stub;

import com.hurray.landlord.server.ServerConstants;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.lordserver.protocol.message.MessageFormatException;
import com.hurray.lordserver.protocol.message.MessageList;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Author: Yizhou He
 * 4/26/12 15:12
 */
public class MessageHolder implements Parcelable {

    private String jsonContent;
    private String type;
    private int status;


    public static final Parcelable.Creator<MessageHolder> CREATOR = new
            Parcelable.Creator<MessageHolder>() {
                public MessageHolder createFromParcel(Parcel in) {
                    return new MessageHolder(in);
                }

                public MessageHolder[] newArray(int size) {
                    return new MessageHolder[size];
                }
            };
    private int size;
    private JSONObject jsonObj;

    public MessageHolder() {
    }


    public MessageHolder(MessageList msg) throws MessageFormatException {
        jsonContent = msg.toJSONString();
        type = msg.get(0).getName();
    }


    public MessageHolder(Parcel in) {
        readFromParcel(in);
    }

    private void readFromParcel(Parcel in) {
        type = in.readString();
        jsonContent = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(type);
        parcel.writeString(jsonContent);
    }

    public String getJsonContent() {
        return jsonContent;
    }

    public void setJsonContent(String jsonContent) {
        this.jsonContent = jsonContent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MessageList getMessages() throws MessageFormatException {
        try {
            return MessageList.parseFromJSON(jsonContent);
        } catch (MessageFormatException e) {
            LogUtil.e(ServerConstants.LOG_TAG, "can't parse json", e);
            throw e;
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSize() throws JSONException {
        return jsonObj.getJSONArray("list").length();
    }

    public JSONObject getJSONObject() throws JSONException {
        if (jsonObj == null) {
            jsonObj = new JSONObject(jsonContent);
        }
        return jsonObj;
    }
}

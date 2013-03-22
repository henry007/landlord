
package com.hurray.landlord.entity;

public class OldChatInfo {

    public static final int CHAT_UNDEFINE = -1;

    public static final int CHAT_MESSAGE = 0;

    public static final int CHAT_FACE = 1;

    public int mChatType = CHAT_UNDEFINE;
    
    public int mPlayerId;

    public String mMessage;

    public int mFaceId;

}

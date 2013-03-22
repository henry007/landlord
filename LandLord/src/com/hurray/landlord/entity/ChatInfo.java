
package com.hurray.landlord.entity;

import android.content.Context;

public class ChatInfo {

    public static final String[] SPEAKS_ARR = {
            "形势大好",
            "你盘盘输，开福利院的嘛",
            "什么都不说了，全是眼泪",
            "郁闷，这牌拆的零零散散的",
            "打牌要厚道一点",
            "告诉你，吃定你了",
            "给个出牌的机会吧",
            "人生就像愤怒的小鸟，当你失败时，总有几只猪在笑",
            "我诅咒你买方便面没作料"
    };

    public int mPlayerId;

    public int mChatId;

    public String mMessage;

    public String getMessageFromChatId(Context ctx) {

        if (mChatId >= 0 && mChatId < ChatInfo.SPEAKS_ARR.length) {
            return SPEAKS_ARR[mChatId];
        }

        return null;
    }

}

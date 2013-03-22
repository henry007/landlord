
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.Constants;
import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class ServerGameResult extends ServerMessage {
    /**
     * 
     */
    private static final long serialVersionUID = 3982138587567343806L;

    private boolean mLordWin;

    private int[] mMarks;

    private int[] mTotalMarks;

    private int[] mHandsCount;

    private List<int[]> mCardIdsList;

    public ServerGameResult(SocketStream stream) {
        super(stream);
    }

    public ServerGameResult(boolean lordWin, int[] marks, int[] totalMarks, int[] handsCount, List<int[]> cardIdsList) {
        super(SRV_GAME_RESULT);
        mLordWin = lordWin;
        mMarks = marks;
        mTotalMarks = totalMarks;
        mHandsCount = handsCount;
        mCardIdsList = cardIdsList;
    }

    public boolean isLordWin() {
        return mLordWin;
    }

    public int[] getFinalMarks() {
        return mMarks;
    }

    public int[] getTotalMarks() {
        return mTotalMarks;
    }

    public int[] getHandsCount() {
        return mHandsCount;
    }

    public List<int[]> getFinalCardIdsList() {
        return mCardIdsList;
    }

    @Override
    protected void unPackJson(String jsonString) {
        JSONObject json = getRespJsonObject(jsonString);
        if (json != null) {
            mLordWin = getJsonBoolean(json, "lord_win", false);

            unMarks(json);

            unTotalMarks(json);

            unHandsCount(json);

            unCardIdsList(json);
        }

    }

    private void unMarks(JSONObject json) {
        JSONArray marksArray = getJsonArray(json, "marks");
        if (marksArray != null) {
            int marksArrayLength = marksArray.length();
            mMarks = new int[marksArrayLength];
            for (int i = 0; i < marksArrayLength; i++) {
                JSONObject element = getJsonObj(marksArray, i);
                int m = getJsonInt(element, "m", -1);
                mMarks[i] = m;
            }
        }
    }

    private void unTotalMarks(JSONObject json) {
        JSONArray totalMarksArray = getJsonArray(json, "total_marks");
        if (totalMarksArray != null) {
            int totalMarksArrayLength = totalMarksArray.length();
            mTotalMarks = new int[totalMarksArrayLength];
            for (int i = 0; i < totalMarksArrayLength; i++) {
                JSONObject element = getJsonObj(totalMarksArray, i);
                int m = getJsonInt(element, "m", -1);
                mTotalMarks[i] = m;
            }
        }
    }

    private void unHandsCount(JSONObject json) {
        JSONArray hcArray = getJsonArray(json, "hands_count");
        if (hcArray != null) {
            int hcArrayLength = hcArray.length();
            mHandsCount = new int[hcArrayLength];
            for (int i = 0; i < hcArrayLength; i++) {
                JSONObject element = getJsonObj(hcArray, i);
                int m = getJsonInt(element, "h", -1);
                mHandsCount[i] = m;
            }
        }
    }

    private void unCardIdsList(JSONObject json) {
        mCardIdsList = new LinkedList<int[]>();

        for (int i = 0; i < Constants.PLAYER_NUM; i++) {
            JSONArray cardsArray = getJsonArray(json, "cards" + i);
            if (cardsArray != null) {
                int length = cardsArray.length();
                int[] cards = new int[length];
                for (int j = 0; j < length; j++) {
                    JSONObject element = getJsonObj(cardsArray, j);
                    cards[j] = getJsonInt(element, "c", -1);
                }
                mCardIdsList.add(cards);
            }

        }
    }
}

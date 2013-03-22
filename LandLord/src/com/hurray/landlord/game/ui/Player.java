
package com.hurray.landlord.game.ui;

import com.hurray.landlord.game.data.Sex;

public class Player {

    protected static final String TAG = "Player";

    protected int mPlayerId;

    protected int mSex;

    protected int[] mPreShowCardIds;

    protected OnPreShowCardListener mListener;

    public Player(int playerId, int sex) {
        mPlayerId = playerId;
        mPreShowCardIds = null;
        mSex = sex;
    }

    public void setOnPreShowCardListener(OnPreShowCardListener l) {
        mListener = l;
    }

    public boolean isNull() {
        return false;
    }

    public void setSex(int sex) {
        mSex = sex;
    }

    public void setPlayerId(int playerId) {
        mPlayerId = playerId;
    }

    public int getSex() {
        return mSex;
    }

    public int getPlayerId() {
        return mPlayerId;
    }

    public void setPreShowCardIds(int preShowCardsPlayerId, int[] preShowCardIds) {
        // LogUtil.d(TAG, toString() + " begin setPreShowCardIds");

        if (preShowCardsPlayerId == mPlayerId) {

            synchronized (this) {
                mPreShowCardIds = preShowCardIds;
            }

            if (mListener != null)
                mListener.onPreShowCardChanged(this, mPreShowCardIds);

        }

        // LogUtil.d(TAG, toString() + " after setPreShowCardIds");
    }

    public int[] getPreShowCardIds() {
        // LogUtil.d(TAG, toString() + " begin getPreShowCardIds");
        synchronized (this) {
            // LogUtil.d(TAG, toString() + " after getPreShowCardIds");
            int num = getPreShowCardIdsNum();
            int[] cardIds = new int[num];
            for (int i = 0; i < num; i++) {
                cardIds[i] = mPreShowCardIds[i];
            }

            return cardIds;
        }
    }

    public void clearPreShowCardIds() {
        // LogUtil.d(TAG, toString() + " begin clearPreShowCardIds");
        synchronized (this) {
            mPreShowCardIds = null;
        }

        if (mListener != null)
            mListener.onPreShowCardChanged(this, mPreShowCardIds);
        // LogUtil.d(TAG, toString() + " after clearPreShowCardIds");
    }

    private int getPreShowCardIdsNum() {
        // LogUtil.d(TAG, toString() + " begin getPreShowCardIdNum");
        if (mPreShowCardIds != null) {
            return mPreShowCardIds.length;
        }
        // LogUtil.d(TAG, toString() + " after getPreShowCardIdNum");
        return 0;
    }

    public static class NullPlayer extends Player {

        public NullPlayer() {
            super(-1, Sex.MALE);
        }

        public boolean isNull() {
            return true;
        }

        public void setPreShowCardIds(int preShowCardsPlayerId, int[] preShowCardIds) {
        }

        public void clearPreShowCardIds() {
            mPreShowCardIds = null;
        }

        public int getPreShowCardIdNum() {
            return 0;
        }

    }

    public interface OnPreShowCardListener {
        public void onPreShowCardChanged(Player p, int[] preShowCardIds);
    }

}

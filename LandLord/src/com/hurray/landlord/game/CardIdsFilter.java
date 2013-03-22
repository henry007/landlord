
package com.hurray.landlord.game;

public class CardIdsFilter {

    private int[] mCardIds;

    private int mEndIdx;

    public CardIdsFilter(int capacity) {
        mCardIds = new int[capacity];
        mEndIdx = 0;
    }

    public CardIdsFilter() {
        mCardIds = new int[10];
        mEndIdx = 0;
    }

    public void addCardIdFilterRepeatValue(int cardId) {
        boolean exist = false;
        for (int i = 0; i < mEndIdx; i++) {
            if (CardUtil.getCardValue(mCardIds[i]) == CardUtil.getCardValue(cardId)) {
                exist = true;
                break;
            }
        }

        if (!exist) {
            mCardIds[mEndIdx++] = cardId;
        }

    }

    public int size() {
        return mEndIdx;
    }

    public int getCardId(int idx) {
        if (idx < 0 || idx >= mEndIdx) {
            throw new ArrayIndexOutOfBoundsException("ArrayIndexOutOfBoundsException");
        }

        return mCardIds[idx];
    }

    public int[] getCardIds() {
        int[] cardIds = new int[mEndIdx];
        for (int i = 0; i < mEndIdx; i++) {
            cardIds[i] = mCardIds[i];
        }

        if (cardIds.length > 0) {
            return cardIds;
        }

        return null;
    }

}

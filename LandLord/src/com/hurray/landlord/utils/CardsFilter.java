
package com.hurray.landlord.utils;

import com.hurray.landlord.game.CardHelperAnalyzer;
import com.hurray.landlord.game.CardType;
import com.hurray.landlord.game.CardUtil;
import com.hurray.landlord.game.data.PlayerContext;

import android.R.array;

import java.util.ArrayList;

public class CardsFilter {

    private int[] selectedCards = null; // 抽取出来的牌

    private boolean[] selecteds = null;

    private PlayerContext mPlayerContext;

    private CardHelperAnalyzer analyzer;

    private int[] filteredCards;

    public CardsFilter(PlayerContext playerContext) {
        this.mPlayerContext = playerContext;
    }

    public boolean isFiltered() {

        selectedCards = getFilteredCards(mPlayerContext); // 选中牌

        if (selectedCards != null) {

            analyzer = new CardHelperAnalyzer();

            analyzer.analyze(selectedCards);

            int length = selectedCards.length;

            switch (length) {
                case 1:
                case 2:
                    int[] finalCards = getRuledCards(selectedCards);

                    mPlayerContext.resetCardIdsSelect();

                    mPlayerContext.setCardIdsSelect(finalCards);
                    return true;
                case 3:
                    return filteredDoubleCards();
                case 4:
                    return filteredBomberCards();
                default:
                    // TODO 过滤顺儿
                    ArrayList<int[]> singleDragon = analyzer.getVecSingleDragon();

                    if (!singleDragon.isEmpty()) {
                        filteredCards = singleDragon.get(0);

                        mPlayerContext.resetCardIdsSelect();

                        mPlayerContext.setCardIdsSelect(filteredCards);

                        releaseFilteredCards();

                        return true;
                    } else {
                        return filteredBomberCards();
                    }
            }

        }
        return false;
    }

    public int[] getRuledCards(int[] filteredCards) {

        int[] preSelectedCards = mPlayerContext.justGetSelectedCards();

        int[] nowSelectedCards = filteredCards;
        int[] newArray = null;
        if (preSelectedCards == null) {
           return nowSelectedCards;
        } else {
            newArray = mergeArray(preSelectedCards, nowSelectedCards);
        }
        CardUtil.sortDescending(newArray);

        boolean isRuled = CardUtil.getCardType(newArray) == CardType.LL_NONE ? false : true;

        if (isRuled)
            return newArray;
        else
            return nowSelectedCards;
    }

    public int[] mergeArray(int[] aArray, int[] bArray) {
        int[] cArray = new int[aArray.length + bArray.length];

        for (int i = 0; i < aArray.length; i++) {
            cArray[i] = aArray[i];
        }
        int extendIndex = aArray.length;

        for (int i = 0; i < bArray.length; i++) {
            cArray[extendIndex] = bArray[i];
            extendIndex++;
        }

        return cArray;
    }

    private boolean filteredBomberCards() {
        ArrayList<int[]> bomberCards = analyzer.getVecBomber();

        if (!bomberCards.isEmpty()) {

            filteredCards = bomberCards.get(0);

            refreshSelfCards();

            releaseFilteredCards();

            return true;
        } else {
            return filteredThripleCards();
        }
    }

    private boolean filteredThripleCards() {
        ArrayList<int[]> thripleCards = analyzer.getVecTriple();

        if (!thripleCards.isEmpty()) {

            filteredCards = thripleCards.get(0);

            refreshSelfCards();

            return true;
        } else {
            return filteredDoubleCards();
        }
    }

    private void refreshSelfCards() {

        int[] finalCards = getRuledCards(filteredCards);

        mPlayerContext.resetCardIdsSelect();

        mPlayerContext.setCardIdsSelect(finalCards);
    }

    private boolean filteredDoubleCards() {
        ArrayList<int[]> doubleCards = analyzer.getVecDouble();

        if (!doubleCards.isEmpty()) {

            filteredCards = doubleCards.get(0);

            refreshSelfCards();

            releaseFilteredCards();

            return true;

        } else {

            return false;
        }
    }

    private void releaseFilteredCards() {
        filteredCards = null;
    }

    private int[] getFilteredCards(PlayerContext mPlayerContext) {
        boolean[] touches = mPlayerContext.getSelfCardTouches();
        int[] selfCards = mPlayerContext.getSelfCardIds();

        if (touches != null && selfCards != null) {
            int[] filteredCards = null;
            int num = 0;
            for (int i = 0; i < touches.length; i++) {
                if (touches[i]) {
                    num++;
                }
            }
            filteredCards = new int[num];
            int index = 0;

            for (int i = 0; i < touches.length; i++) {
                if (touches[i]) {
                    filteredCards[index] = selfCards[i];
                    index++;
                }
            }
            return filteredCards;
        }
        return null;
    }

    public int[] getSelectedCards() {
        return selectedCards;
    }

    public void setSelectedCards(int[] selectedCards) {
        this.selectedCards = selectedCards;
    }

    public int[] getFilteredCards() {
        return filteredCards;
    }

}

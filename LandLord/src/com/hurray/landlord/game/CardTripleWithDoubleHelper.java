
package com.hurray.landlord.game;

import com.hurray.landlord.game.data.PlayerContext;

import java.util.ArrayList;

public class CardTripleWithDoubleHelper extends CardHelper {

    public CardTripleWithDoubleHelper(PlayerContext ctx) {
        super(ctx);
    }

    @Override
    protected ArrayList<int[]> suggestCards() {
        // 3带2
        if (selfCardIds.length < 5) {
            return null;
        }
        boolean foundTriple = false;
        int[] tripleWithDouble;
        tmpList = analyzer.getVecTriple();
        int size = tmpList.size();
        for (int i = 0; i < size; i++) {
            int[] triple = tmpList.get(i);
            tripleWithDouble = new int[] {
                    -1, -1, -1, -1, -1
            };
            int val = CardUtil.getCardValue(triple[0]);
            if (val > preCardValue) {
                for (int j = 0; j < triple.length; j++) {
                    tripleWithDouble[j] = triple[j];
                    foundTriple = true;
                }
                selectedList.add(tripleWithDouble);
            }
        }
        // 没有三张满足条件
        if (!foundTriple) {
            return null;
        }

        // 再找对牌合成3带2
        tmpList = analyzer.getVecDouble();
        size = tmpList.size();
        int[] doubleLL = null;
        if (size > 0) {
            doubleLL = tmpList.get(0);
        }

        // 不能用太大的对牌,但是如果leftCardNumPercent > 90，就出吧
        if (doubleLL != null && (CardUtil.getCardValue(doubleLL[0]) < 14)) {
            for (int i = 0; i < selectedList.size(); i++) {
                selectedList.get(i)[3] = doubleLL[0];
                selectedList.get(i)[4] = doubleLL[1];
            }
        }

        // if (tripleWithDouble[3] >= 0 && tripleWithDouble[4] >= 0) {
        // CardUtil.sortDescending(tripleWithDouble);
        // return tripleWithDouble;
        // }
        suggestLastChoice(selfCardIds);
        if (checkOutSelectedList(selectedList)) {

            return selectedList;
        }

        return null;
    }

}

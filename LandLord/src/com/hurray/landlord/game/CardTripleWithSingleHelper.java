
package com.hurray.landlord.game;

import com.hurray.landlord.game.data.PlayerContext;

import java.util.ArrayList;

public class CardTripleWithSingleHelper extends CardHelper {

    public CardTripleWithSingleHelper(PlayerContext ctx) {
        super(ctx);
    }

    @Override
    protected ArrayList<int[]> suggestCards() {
        // 3带1
        if (selfCardIds.length < 4) {
            return null;
        }
        boolean foundTriple = false;
        int[] tripleWithSingle = null;
        tmpList = analyzer.getVecTriple();
        int size = tmpList.size();
        for (int i = 0; i < size; i++) {
            int[] triple = tmpList.get(i);
            tripleWithSingle = new int[] {
                    -1, -1, -1, -1
            };
            int val = CardUtil.getCardValue(triple[0]);
            if (val > preCardValue) {
                for (int j = 0; j < triple.length; j++) {
                    tripleWithSingle[j] = triple[j];
                    foundTriple = true;
                }
                selectedList.add(tripleWithSingle);
            }
        }
        // 没有三张满足条件
        if (!foundTriple) {
            return null;
        }
        // 再找单牌组合成3带1
        tmpList = analyzer.getVecSingle();
        size = tmpList.size();
        int[] single = null;
        if (size > 0) {
            single = tmpList.get(0);
        }

        if (single != null && (CardUtil.getCardValue(single[0]) < 14)) {

            for (int i = 0; i < selectedList.size(); i++) {
                selectedList.get(i)[3] = single[0];
            }
        } else {
            // 拆6个以上的单顺，取单牌
            tmpList = analyzer.getVecSingleDragon();
            size = tmpList.size();
            for (int i = 0; i < size; i++) {
                int[] singleDragon = tmpList.get(i);
                if (singleDragon.length >= 6) {

                    for (int j = 0; j < selectedList.size(); j++) {
                        selectedList.get(i)[3] = singleDragon[0];
                    }
                }
            }
        }

        suggestLastChoice(selfCardIds);

        if (checkOutSelectedList(selectedList)) {

            return selectedList;
        }

        /**
         * 当手牌既没有单牌也没有单顺时，选择一张最小的牌
         */

        // 从中随便找一个最小的
        if (tripleWithSingle[3] < 0) {
            for (int i = selfCardIds.length - 1; i >= 0; i--) {
                if (CardUtil.getCardValue(selfCardIds[i]) != CardUtil
                        .getCardValue(tripleWithSingle[0])) {
                    tripleWithSingle[3] = selfCardIds[i];
                }
            }
        }
        if (tripleWithSingle[3] >= 0) {
            CardUtil.sortDescending(tripleWithSingle);
            selectedList.add(tripleWithSingle);
        }

        suggestLastChoice(selfCardIds);

        if (checkOutSelectedList(selectedList)) {

            return selectedList;
        }

        return null;
    }

}

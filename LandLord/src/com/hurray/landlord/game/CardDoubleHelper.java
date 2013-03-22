
package com.hurray.landlord.game;

import com.hurray.landlord.game.data.PlayerContext;

import java.util.ArrayList;

public class CardDoubleHelper extends CardHelper {

    public CardDoubleHelper(PlayerContext ctx) {
        super(ctx);
    }

//    public CardDoubleHelper(int[] extractedCards) {
//        super(extractedCards);
//    }

    @Override
    protected ArrayList<int[]> suggestCards() {
        // 寻找合适的对牌（ 对牌是从小到大排列的）
        tmpList = analyzer.getVecDouble();
        int size = tmpList.size();
        for (int i = 0; i < size; i++) {
            int[] doubleLL = tmpList.get(i);
            int val = CardUtil.getCardValue(doubleLL[0]);
            if (val > preCardValue) {

                selectedList.add(doubleLL);

            }
        }

        suggestLastChoice(selfCardIds);
        // 导出list
        if (checkOutSelectedList(selectedList)) {

            return selectedList;
        }
        /**
         * 这里有问题
         */
        // 如果对牌中没有，则需要检查双顺
        tmpList = analyzer.getVecDoubleDragon();
        size = tmpList.size();
        int[] doubleLL;
        for (int i = 0; i < size; i++) {
            int[] doubleDragon = tmpList.get(i);
            // 双顺从大到小，所以倒序检查大小
            for (int j = doubleDragon.length - 1; j > 0; j--) {
                doubleLL = new int[2];
                int val = CardUtil.getCardValue(doubleDragon[j]);
                if (val > preCardValue) {
                    doubleLL[0] = doubleDragon[0];
                    doubleLL[1] = doubleDragon[1];
                    selectedList.add(doubleLL);
                }
            }
        }
        // 导出list
        if (checkOutSelectedList(selectedList)) {

            return selectedList;
        }

        // 如果双顺中没有，则需要检查三张
        tmpList = analyzer.getVecTriple();
        size = tmpList.size();
        for (int i = 0; i < size; i++) {
            int[] triple = tmpList.get(i);
            int val = CardUtil.getCardValue(triple[0]);
            if (val > preCardValue) {
                selectedList.add(new int[] {
                        triple[0], triple[1]
                });
            }
        }

        suggestLastChoice(selfCardIds);

        // 导出list
        if (checkOutSelectedList(selectedList)) {

            return selectedList;
        }
        return null;
    }

}

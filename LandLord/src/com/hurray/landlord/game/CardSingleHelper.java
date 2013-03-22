
package com.hurray.landlord.game;

import com.hurray.landlord.game.data.PlayerContext;

import java.util.ArrayList;

public class CardSingleHelper extends CardHelper {

    public CardSingleHelper(PlayerContext ctx) {
        super(ctx);
    }

    @Override
    protected ArrayList<int[]> suggestCards() {

        selectedList = new ArrayList<int[]>();

        tmpList = analyzer.getVecSingle();

        int size = tmpList.size();

        // ------------大于上家出牌，并且所有单牌集合里面的牌放入content
        for (int i = 0; i < size; i++) {
            int[] single = tmpList.get(i);
            int val = CardUtil.getCardValue(single[0]);
            if (val > preCardValue) {
                selectedList.add(single);
            }
        }

        // 如果单牌中没有，拆牌：选择现有牌型中除火箭和4个2后的最大一个
        int idx = 0;
        if (analyzer.getCountJoker() != 0 && preCardValue < CardUtil.getCardValue(selfCardIds[0])) {
            selectedList.add(convertInt(selfCardIds[idx]));
            idx += analyzer.getCountJoker();
        }
        int length = selfCardIds.length;
        if (analyzer.getCountJoker() != 0 && length > 2) {
            if (preCardValue < CardUtil.getCardValue(selfCardIds[1])) {
                selectedList.add(convertInt(selfCardIds[1]));
            }
        }
        if (analyzer.getCount2() != 0 && preCardValue < 15) {
            selectedList.add(convertInt(selfCardIds[idx]));
            idx += analyzer.getCount2();
        }

        if (length > 1 && CardUtil.getCardValue(selfCardIds[idx]) > preCardValue) {

            selectedList.add(convertInt(selfCardIds[idx]));

        }

        suggestLastChoice(selfCardIds);

        // 导出list
        if (checkOutSelectedList(selectedList)) {

            return selectedList;
        }

        return null;
    }

}

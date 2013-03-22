
package com.hurray.landlord.game;

import com.hurray.landlord.game.data.PlayerContext;

import java.util.ArrayList;

public class CardTripleHelper extends CardHelper {

    public CardTripleHelper(PlayerContext ctx) {
        super(ctx);
    }

    @Override
    protected ArrayList<int[]> suggestCards() {

        tmpList = analyzer.getVecTriple();
        int size = tmpList.size();
        for (int i = 0; i < size; i++) {
            int[] triple = tmpList.get(i);
            int val = CardUtil.getCardValue(triple[0]);
            if (val > preCardValue) {
                selectedList.add(triple);
            }
        }
        suggestLastChoice(selfCardIds);
        if (checkOutSelectedList(selectedList)) {

            return selectedList;
        }

        return null;
    }

}

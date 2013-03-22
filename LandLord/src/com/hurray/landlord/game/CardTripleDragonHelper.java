
package com.hurray.landlord.game;

import com.hurray.landlord.game.data.PlayerContext;

import java.util.ArrayList;

public class CardTripleDragonHelper extends CardHelper {

    public CardTripleDragonHelper(PlayerContext ctx) {
        super(ctx);
    }

    @Override
    protected ArrayList<int[]> suggestCards() {
        tmpList = analyzer.getVecTripleDragon();
        int size = tmpList.size();
        for (int i = size - 1; i >= 0; i--) {
            int[] tripleDragon = tmpList.get(i);
            if (preCardLength > tripleDragon.length) {
                continue;
            }

            if (preCardValue < CardUtil.getCardValue(tripleDragon[0])) {
                if (preCardLength == tripleDragon.length) {
                    selectedList.add(tripleDragon);
                } else {
                    int[] properTripleDragon = new int[preCardLength];
                    for (int k = 0; k < preCardLength; k++) {
                        properTripleDragon[k] = tripleDragon[k]; // ????有问题
                    }
                    selectedList.add(properTripleDragon);
                }
            }
        }

        suggestLastChoice(selfCardIds);

        if (checkOutSelectedList(selectedList)) {
            return selectedList;
        }
        return null;
    }

}

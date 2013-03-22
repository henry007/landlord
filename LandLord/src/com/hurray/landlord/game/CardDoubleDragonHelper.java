
package com.hurray.landlord.game;

import com.hurray.landlord.game.data.PlayerContext;

import java.util.ArrayList;

public class CardDoubleDragonHelper extends CardHelper {

    public CardDoubleDragonHelper(PlayerContext ctx) {
        super(ctx);
    }

    @Override
    protected ArrayList<int[]> suggestCards() {
        tmpList = analyzer.getVecDoubleDragon();
        int size = tmpList.size();

        for (int i = size - 1; i >= 0; i--) {
            int doubleDragon[] = tmpList.get(i);
            if (doubleDragon.length < preCardLength) {
                continue;
            }

            if (preCardValue < CardUtil.getCardValue(doubleDragon[0])) {
                if (doubleDragon.length == preCardLength) {
                    selectedList.add(doubleDragon);
                } else {
                    int index = 0;
                    for (int j = doubleDragon.length - 1; j >= 0; j--) {
                        if (preCardValue < CardUtil
                                .getCardValue(doubleDragon[j])) {
                            index = j / 2;
                            break;
                        }
                    }

                    int total = doubleDragon.length / 2;
                    int cardTotal = preCardLength / 2;
                    if (index + cardTotal > total) {
                        index = total - cardTotal;
                    }
                    int properDoubleDragon[] = new int[preCardLength];
                    int m = 0;
                    for (int k = index * 2; k < doubleDragon.length; k++) {
                        properDoubleDragon[m++] = doubleDragon[k];
                    }
                    selectedList.add(properDoubleDragon);
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

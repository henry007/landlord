
package com.hurray.landlord.game;

import com.hurray.landlord.game.data.PlayerContext;

import java.util.ArrayList;

public class CardBombHelper extends CardHelper {

    public CardBombHelper(PlayerContext ctx) {
        super(ctx);
    }

    @Override
    protected ArrayList<int[]> suggestCards() {
        tmpList = analyzer.getVecBomber();
        int size = tmpList.size();
        int bomber[] = null;
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                bomber = tmpList.get(i);
                if (preCardValue < CardUtil.getCardValue(bomber[0])) {
                    selectedList.add(bomber);
                    if (checkOutSelectedList(selectedList)) {
                        return selectedList;
                    }
                }
            }
        }
        return null;
    }

}


package com.hurray.landlord.game;

import com.hurray.landlord.game.data.PlayerContext;

import java.util.ArrayList;

public class CardLastJudgeHelper extends CardHelper {

    public CardLastJudgeHelper(PlayerContext ctx) {
        super(ctx);
    }

    @Override
    protected ArrayList<int[]> suggestCards() {

        // 有炸弹出炸弹， 没就过
        tmpList = analyzer.getVecBomber();
        int size = tmpList.size();
        int bomber[] = null;
        if (size > 0) {
            bomber = tmpList.get(0);
            selectedList.add(bomber);
            if (checkOutSelectedList(selectedList)) {
                return selectedList;
            }

        }

        return null;
    }

}

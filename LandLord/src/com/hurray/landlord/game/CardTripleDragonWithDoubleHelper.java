
package com.hurray.landlord.game;

import com.hurray.landlord.game.data.PlayerContext;

import java.util.ArrayList;

public class CardTripleDragonWithDoubleHelper extends CardHelper {

    public CardTripleDragonWithDoubleHelper(PlayerContext ctx) {
        super(ctx);
    }

    @Override
    protected ArrayList<int[]> suggestCards() {
        // 飞机带翅膀：三顺＋同数量的对牌
        tmpList = analyzer.getVecTripleDragon();
        int size = tmpList.size();
        int tripleNum = preCardLength / 5; // 三张的个数
        int prevTripleDragonLength = tripleNum * 3; // 上家三顺的牌数
        int[] temp = new int[tripleNum]; // 单牌数组
        if (selfCardIds.length > preCardLength && size != 0) {
            for (int i = size - 1; i >= 0; i--) {
                int[] tripleDws = new int[preCardLength]; // 返回的数组
                int[] tripleDragon = tmpList.get(i);
                if (prevTripleDragonLength > tripleDragon.length) {
                    continue;
                }
                if (preCardValue < CardUtil.getCardValue(tripleDragon[0])) {
                    size = analyzer.getVecDouble().size();
                    if (size >= tripleNum) {
                        for (int j = 0; j < tripleNum; j++) {
                            temp[j] = analyzer.getVecDouble().get(0)[0];
                        }
                        for (int j = 0; j < tripleNum; j++) {
                            tripleDws[j] = tripleDragon[j];
                        }
                        for (int j = 0; j < temp.length; j++) {
                            tripleDws[tripleNum + j] = temp[j];
                        }
                        selectedList.add(tripleDws);

                    } else {
                        break;
                    }
                }
            }

        } else {
            // 手中牌数不够或没有三顺，则有炸弹出炸弹， 没就过
            tmpList = analyzer.getVecBomber();
            size = tmpList.size();
            int bomber[] = null;
            if (size > 0) {
                bomber = tmpList.get(0);
                selectedList.add(bomber);
            }
        }
        suggestLastChoice(selfCardIds);
        if (checkOutSelectedList(selectedList)) {
            return selectedList;
        }
        return null;
    }

}

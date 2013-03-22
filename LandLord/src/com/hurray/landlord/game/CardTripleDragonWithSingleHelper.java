
package com.hurray.landlord.game;

import com.hurray.landlord.game.data.PlayerContext;

import java.util.ArrayList;

/**
 * 可能有错。飞机选择的长度有问题
 * 
 * @author Administrator
 */
public class CardTripleDragonWithSingleHelper extends CardHelper {

    public CardTripleDragonWithSingleHelper(PlayerContext ctx) {
        super(ctx);
    }

    @Override
    protected ArrayList<int[]> suggestCards() {
        tmpList = analyzer.getVecTripleDragon();
        int size = tmpList.size();
        int tripleNum = preCardLength / 4; // 三张的个数
        // 例如：33344456则tripleNum
        // = 2
        int prevTripleDragonLength = tripleNum * 3;
        // 上家三顺的牌数
        // 例如:33344456 则
        // = 6
        int[] temp = new int[tripleNum]; // 单牌数组
        if (selfCardIds.length > preCardLength && size != 0) {
            for (int i = size - 1; i >= 0; i--) {
                int[] tripleDws = new int[preCardLength];
                int[] tripleDragon = tmpList.get(i);
                if (prevTripleDragonLength > tripleDragon.length) {
                    continue;
                }
                if (preCardValue < CardUtil.getCardValue(tripleDragon[0])) {
                    // TODO 选单构造飞机带翅膀
                    size = analyzer.getVecSingle().size(); // 单牌集合大小
                    if (preCardLength % 2 == 0) {
                        // 例如：333444
                        if (size >= tripleNum) { // 手中单牌数大于需要的数量则选单牌
                            for (int j = 0; j < tripleNum; j++) {
                                /**
                                 * analyzer.getVecSingle().get(0)[0]
                                 * 这里的0取的是单牌数组里的第一个 可能取的单牌是最大的
                                 * ，如果需要取最小的单牌，则改为数组最后
                                 */

                                temp[j] = analyzer.getVecSingle().get(0)[0];
                            }
                        } else { // 手中单牌数不满足需要则选对子
                            for (int j = 0; j < tripleNum; j++) {
                                temp[j] = analyzer.getVecDouble().get(0)[0];
                            }
                        }
                    } else {
                        // 333444555
                        if (size >= tripleNum) {
                            for (int j = 0; j < tripleNum; j++) {
                                temp[j] = analyzer.getVecSingle().get(0)[0];
                            }
                        } else if (size == 1) {
                            for (int j = 0; j < tripleNum; j++) {
                                temp[j] = analyzer.getVecDouble().get(0)[0];
                            }
                            temp[tripleNum - 1] = analyzer.getVecSingle().get(0)[0];
                        }
                    }

                    // ----------------------可能有错-----------------------
                    for (int j = 0; j < 6; j++) {
                        tripleDws[j] = tripleDragon[j];
                    }
                    for (int j = 0; j < temp.length; j++) {
                        tripleDws[tripleNum + j] = temp[j];
                    }
                    selectedList.add(tripleDws);
                    // ---------------------可能有错--------------------------
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

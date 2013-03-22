
package com.hurray.landlord.game;

import com.hurray.landlord.game.data.PlayerContext;

import java.util.ArrayList;

public class CardSingleDragonHelper extends CardHelper {

    public CardSingleDragonHelper(PlayerContext ctx) {
        super(ctx);
    }

    @Override
    protected ArrayList<int[]> suggestCards() {
        tmpList = analyzer.getVecSingleDragon();
        int size = tmpList.size();
        for (int i = 0; i < size; i++) {
            int[] singleDragon = tmpList.get(i);
            if (singleDragon.length == preCardLength) {
                if (preCardValue < CardUtil.getCardValue(singleDragon[0])) {
                    selectedList.add(singleDragon);
                }
            }
        }

        suggestLastChoice(selfCardIds);

        if (checkOutSelectedList(selectedList)) {
            return selectedList;
        }

        for (int i = 0; i < size; i++) {
            int[] singleDragon = tmpList.get(i);
            if (singleDragon.length > preCardLength) {

                // 2个顺子中最大值比较
                if (preCardValue >= CardUtil.getCardValue(singleDragon[0])) {
                    continue;
                }

                // 可能拆出3张以上(含)单牌
                if ((singleDragon.length - preCardLength) >= 3) {

                    int begIdx = 0;
                    for (int j = 0; j < singleDragon.length; j++) {
                        if (preCardValue < CardUtil.getCardValue(singleDragon[j])) {
                            begIdx = j;
                        } else {
                            break;
                        }
                    }

                    if ((singleDragon.length - begIdx) >= preCardLength) {
                        int[] properSingleDragon = new int[preCardLength];
                        int pIdx = 0;
                            for (int m = begIdx; m < singleDragon.length; m++) {
                                if(pIdx == preCardLength)break;
                                properSingleDragon[pIdx++] = singleDragon[m];
                            }
                            selectedList.add(properSingleDragon);
                    } else {

                        int durLength = singleDragon.length - preCardLength;

                        int pIdx = 0;
                        int temp = 0;
                        for (int j = durLength; j >= 0; j--) {
                            int[] properSingleDragon = new int[preCardLength];
                            for (int m = j; m < singleDragon.length - temp; m++) {
                                properSingleDragon[pIdx++] = singleDragon[m];
                            }
                            if (pIdx != 0) {
                                pIdx = 0;
                            }
                            temp++;
                            CardUtil.printCards("properSingleDragon", properSingleDragon);
                            selectedList.add(properSingleDragon);
                        }
                    }
                } else {
                    // 多出1张或2张
                    int begIdx = 0;
                    if (singleDragon.length - preCardLength == 1) {
                        if (preCardValue < CardUtil.getCardValue(singleDragon[1])) {
                            begIdx = 1;
                        } else {
                            begIdx = 0;
                        }
                    } else if (singleDragon.length - preCardLength == 2) {
                        if (preCardValue < CardUtil.getCardValue(singleDragon[2])) {
                            begIdx = 2;
                        } else if (preCardValue < CardUtil.getCardValue(singleDragon[1])) {
                            begIdx = 1;
                        } else {
                            begIdx = 0;
                        }
                    }

                    if ((singleDragon.length - begIdx) >= preCardLength) {
                        try {
                            int[] properSingleDragon = new int[preCardLength];
                            int pIdx = 0;
                            for (int m = begIdx; m < singleDragon.length; m++) {
                                properSingleDragon[pIdx++] = singleDragon[m];
                            }
                            selectedList.add(properSingleDragon);
                        } catch (Exception e) {
                            return null;
                        }
                    }
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


package com.hurray.landlord.game;

import com.hurray.landlord.utils.LogUtil;

import java.util.ArrayList;

public class BaseAnalyzer {

    private static final String TAG = "BaseAnalyzer";

    protected int[] mCountNormal = new int[12]; // 3-A

    protected int mCount2; // 2的个数

    protected int mCountJoker; // 大小王

    protected ArrayList<int[]> mVecSingle = new ArrayList<int[]>();

    protected ArrayList<int[]> mVecDouble = new ArrayList<int[]>();

    protected ArrayList<int[]> mVecTriple = new ArrayList<int[]>();

    protected ArrayList<int[]> mVecSingleDragon = new ArrayList<int[]>();

    protected ArrayList<int[]> mVecDoubleDragon = new ArrayList<int[]>();

    protected ArrayList<int[]> mVecTripleDragon = new ArrayList<int[]>();

    protected ArrayList<int[]> mVecBomber = new ArrayList<int[]>();

    protected ArrayList<int[]> mVecRocket = new ArrayList<int[]>();

    // TODO其他牌型

    public BaseAnalyzer() {
        
    }

    private void resetCount() {
        for (int i = 0; i < mCountNormal.length; i++) {
            mCountNormal[i] = 0;
        }
        mCount2 = 0;
        mCountJoker = 0;
    }

    private void clearArrayList() {
        mVecBomber.clear();
        mVecRocket.clear();
        mVecTripleDragon.clear();
        mVecDoubleDragon.clear();
        mVecSingleDragon.clear();
        mVecTriple.clear();
        mVecDouble.clear();
        mVecSingle.clear();
    }

    protected void clear() {
        resetCount();
        clearArrayList();
    }

    public int getCount2() {
        return mCount2;
    }

    public int getCountJoker() {
        return mCountJoker;
    }

    public ArrayList<int[]> getVecRocket() {
        return mVecRocket;
    }

    public ArrayList<int[]> getVecBomber() {
        return mVecBomber;
    }

    public ArrayList<int[]> getVecTripleDragon() {
        return mVecTripleDragon;
    }

    public ArrayList<int[]> getVecDoubleDragon() {
        return mVecDoubleDragon;
    }

    public ArrayList<int[]> getVecSingleDragon() {
        return mVecSingleDragon;
    }

    public ArrayList<int[]> getVecTriple() {
        return mVecTriple;
    }

    public ArrayList<int[]> getVecDouble() {
        return mVecDouble;
    }

    public ArrayList<int[]> getVecSingle() {
        return mVecSingle;
    }

    // // TODO 牌型处理不全
    // public boolean isLastCardTypeEq(int cardType) {
    // if (remainHandCount() > 1) {
    // return false;
    // }
    //
    // switch (cardType) {
    // case CardType.LL_TRIPLE:
    // return mVecTriple.size() == 1;
    // case CardType.LL_DOUBLE:
    // return mVecDouble.size() == 1;
    // case CardType.LL_SINGLE:
    // return mVecSingle.size() == 1;
    // }
    // return false;
    // }

    public void analyze(int[] cardIds) {
        CardUtil.sortDescending(cardIds);
        analyzeCards(cardIds);
    }

    protected void analyzeBomber(int[] cardIds) {
        for (int i = 0; i < mCountNormal.length - 1; i++) {
            if (mCountNormal[i] == 4) {
                mVecBomber.add(new int[] {
                        i * 4 + 3, i * 4 + 2,
                        i * 4 + 1, i * 4
                });

                // 清除这4个
                for (int j = 0; j < cardIds.length; j++) {
                    if (cardIds[j] == i * 4 + 3) {
                        for (int k = 0; k < 4; k++) {
                            cardIds[j + k] = -1;
                        }
                        break;
                    }
                }

                mCountNormal[i] = -1;
            }
        }
    }

    protected void analyzeTripleDragon(int[] cards) {
        // 分析三顺
        int start = -1;
        int end = -1;
        for (int i = 0; i < mCountNormal.length; i++) {
            if (mCountNormal[i] == 3) {
                if (start == -1) {
                    start = i;
                } else {
                    end = i;
                }
            } else {
                if (end != -1 && start != -1) {
                    int dur = end - start + 1;
                    int[] tripleDragon = new int[dur * 3];
                    int m = 0;
                    for (int j = 0; j < cards.length; j++) {
                        int v = CardUtil.getCardValue(cards[j]) - 3;
                        if (v >= start && v <= end) {
                            tripleDragon[m++] = cards[j];
                            cards[j] = -1;
                        }
                    }

                    if (m == dur * 3 - 1) {
                        LogUtil.d(TAG, "analyzeTripleDragon done!!!");
                    } else {
                        LogUtil.d(TAG, "analyzeTripleDragon error!!!");
                    }

                    mVecTripleDragon.add(tripleDragon);
                    for (int s = start; s <= end; s++) {
                        mCountNormal[s] = -1;
                    }
                    start = end = -1;
                    continue;
                } else {
                    start = end = -1;
                }
            }
        }
    }

    protected void analyzeDoubleDragon(int[] cardIds) {
        // 分析双顺
        int sstart = -1;
        int send = -1;
        for (int i = 0; i < mCountNormal.length; i++) {
            if (mCountNormal[i] == 2) {
                if (sstart == -1) {
                    sstart = i;
                } else {
                    send = i;
                }
            } else {
                if (sstart != -1 && send != -1) {
                    int dur = send - sstart + 1;
                    if (dur < 3) {
                        sstart = send = -1;
                        continue;
                    } else {
                        int shuangshun[] = new int[dur * 2];
                        int m = 0;
                        for (int j = 0; j < cardIds.length; j++) {
                            int v = CardUtil.getCardValue(cardIds[j]) - 3;
                            if (v >= sstart && v <= send) {
                                shuangshun[m++] = cardIds[j];
                                cardIds[j] = -1;
                            }
                        }
                        mVecDoubleDragon.add(shuangshun);
                        for (int s = sstart; s <= send; s++) {
                            mCountNormal[s] = -1;
                        }
                        sstart = send = -1;
                        continue;
                    }
                } else {
                    sstart = send = -1;
                }
            }
        }
    }

    protected void analyzeSingleDragon(int[] cardIds) {
        // 分析单顺
        int dstart = -1;
        int dend = -1;
        for (int i = 0; i < mCountNormal.length; i++) {
            if (mCountNormal[i] >= 1) {
                if (dstart == -1) {
                    dstart = i;
                } else {
                    dend = i;
                }
            } else {
                if (dstart != -1 && dend != -1) {
                    int dur = dend - dstart + 1;
                    if (dur >= 5) {
                        int m = 0;
                        int[] danshun = new int[dur];
                        for (int j = 0; j < cardIds.length; j++) {
                            int v = CardUtil.getCardValue(cardIds[j]) - 3;
                            if (v == dend) {
                                danshun[m++] = cardIds[j];
                                mCountNormal[dend]--;
                                dend--;
                                cardIds[j] = -1;
                            }
                            if (dend == dstart - 1) {
                                break;
                            }
                        }

                        mVecSingleDragon.add(danshun);
                    }
                    dstart = dend = -1;
                } else {
                    dstart = dend = -1;
                }
            }
        }
    }

    protected void analyzeTriple(int[] cardIds) {
        // 分析三张
        for (int i = 0; i < mCountNormal.length; i++) {
            // LogUtil.d(TAG, "mCountNormal[" + i + "] i=" + mCountNormal[i]);
            if (mCountNormal[i] == 3) {
                mCountNormal[i] = -1;
                int[] triple = new int[3];
                int k = 0;
                for (int j = 0; j < cardIds.length; j++) {
                    if (cardIds[j] == 52 || cardIds[j] == 53) {
                        continue;
                    }

                    int val = CardUtil.getCardValue(cardIds[j]);
                    if (val == 15) {
                        continue;
                    }

                    int id = val - 3;
                    // LogUtil.d(TAG, "triple cardId=" + cardIds[j] + " id=" +
                    // id + " k=" + k);
                    if (id == i) {
                        triple[k++] = cardIds[j];
                        cardIds[j] = -1;
                    }
                }
                mVecTriple.add(triple);
            }
        }
    }

    protected void analyzeDouble(int[] cardIds) {
        // 分析对牌
        for (int i = 0; i < mCountNormal.length; i++) {
            if (mCountNormal[i] == 2) {
                int[] duipai = new int[2];
                for (int j = 0; j < cardIds.length; j++) {
                    int v = CardUtil.getCardValue(cardIds[j]) - 3;
                    if (v == i) {
                        duipai[0] = cardIds[j];
                        duipai[1] = cardIds[j + 1];
                        mVecDouble.add(duipai);
                        cardIds[j] = -1;
                        cardIds[j + 1] = -1;
                        break;
                    }
                }
                mCountNormal[i] = -1;
            }
        }
    }

    protected void analyzeSingle(int[] cardIds) {
        // 分析单牌
        for (int i = 0; i < mCountNormal.length; i++) {
            if (mCountNormal[i] == 1) {
                for (int j = 0; j < cardIds.length; j++) {
                    int v = CardUtil.getCardValue(cardIds[j]) - 3;
                    if (v == i) {
                        mVecSingle.add(new int[] {
                                cardIds[j]
                        });
                        cardIds[j] = -1;
                        mCountNormal[i] = -1;
                        break;
                    }

                }
            }
        }
    }

    protected void analyze2(int[] cardIds) {
        // 根据2的数量进行分析
        switch (mCount2) {
            case 4:
                mVecBomber.add(new int[] {
                        cardIds[mCountJoker],
                        cardIds[mCountJoker + 1], cardIds[mCountJoker + 2],
                        cardIds[mCountJoker + 3]
                });
                break;
            case 3:
                mVecTriple.add(new int[] {
                        cardIds[mCountJoker],
                        cardIds[mCountJoker + 1], cardIds[mCountJoker + 2]
                });
                break;
            case 2:
                mVecDouble.add(new int[] {
                        cardIds[mCountJoker],
                        cardIds[mCountJoker + 1]
                });
                break;
            case 1:
                mVecSingle.add(new int[] {
                        cardIds[mCountJoker]
                });
                break;
        }
    }

    protected void analyzeJoker(int[] cardIds) {
        // 分析火箭
        if (mCountJoker == 1) {
            mVecSingle.add(new int[] {
                    cardIds[0]
            });
        } else if (mCountJoker == 2) {
            mVecRocket.add(new int[] {
                    cardIds[0], cardIds[1]
            });
        }
    }

    protected void analyzeCount(int[] cardIds) {
        // 分析王，2，普通牌的数量
        resetCount();

        for (int i = 0; i < cardIds.length; i++) {
            int val = CardUtil.getCardValue(cardIds[i]);
            if (val == 16 || val == 17) {
                mCountJoker++;
            } else if (val == 15) {
                mCount2++;
            } else {
                mCountNormal[val - 3]++;
            }
        }

    }

    // 分析几大主要牌型
    protected void analyzeCards(int[] cardIds) {
        // LogUtil.d(TAG, "----------------------------------");
        // 初始化牌型容器
        clear();
        int[] tmpCardIds = CardUtil.copyCards(cardIds);
        // 分析炸弹
        // LogUtil.d(TAG, "分析炸弹");
        analyzeCount(tmpCardIds); // 分析王，2，普通牌的数量
        analyzeBomber(tmpCardIds);
        tmpCardIds = CardUtil.copyCards(tmpCardIds);
        

        // 分析三顺
        // LogUtil.d(TAG, "分析三顺");
        analyzeCount(tmpCardIds);
        analyzeTripleDragon(tmpCardIds);
        tmpCardIds = CardUtil.copyCards(tmpCardIds);

        // 分析双顺
        // LogUtil.d(TAG, "分析双顺");
        analyzeCount(tmpCardIds);
        analyzeDoubleDragon(tmpCardIds);
        tmpCardIds = CardUtil.copyCards(tmpCardIds);

        // 分析单顺
        // LogUtil.d(TAG, "分析单顺");
        analyzeCount(tmpCardIds);
        analyzeSingleDragon(tmpCardIds);
        tmpCardIds = CardUtil.copyCards(tmpCardIds);

        // 分析三张
        // LogUtil.d(TAG, "分析三张");
        analyzeCount(tmpCardIds);
        analyzeTriple(tmpCardIds);
        tmpCardIds = CardUtil.copyCards(tmpCardIds);

        // 分析对牌
        // LogUtil.d(TAG, "分析对牌");
        analyzeCount(tmpCardIds);
        analyzeDouble(tmpCardIds);
        tmpCardIds = CardUtil.copyCards(tmpCardIds);

        // 分析单牌
        // LogUtil.d(TAG, "分析单牌");
        analyzeCount(tmpCardIds);
        analyzeSingle(tmpCardIds);
        tmpCardIds = CardUtil.copyCards(tmpCardIds);

        // 根据2的数量进行分析
        // LogUtil.d(TAG, "分析2");
        analyzeCount(tmpCardIds);
        analyze2(tmpCardIds);
 
        // 分析大小王
        // LogUtil.d(TAG, "分析大小王");
        analyzeJoker(tmpCardIds);

        // LogUtil.d(TAG, "----------------------------------");
    }

    public int remainHandCount() {
        return mVecRocket.size() + mVecBomber.size() + mVecTripleDragon.size()
                + mVecDoubleDragon.size() + mVecTriple.size()
                + mVecSingleDragon.size() + mVecDouble.size() + mVecSingle.size();
    }

    /**
     * 如果下家不是敌人 nextEnemyLeftCardNum = -1
     */
    public int[] getMinValueType(int nextEnemyLeftCardNum) {

        ArrayList<int[]> tmpVec;
        int size;
        int indexOfVec = 0;
        int cardType = -1;

        int minValue = 55;

        tmpVec = mVecTripleDragon;
        size = tmpVec.size();

        for (int i = 0; i < size; i++) {
            int[] p = tmpVec.get(i);
            if (minValue > p[0]) {
                cardType = CardType.LL_TRIPLE_DRAGON;
                minValue = p[0];
                indexOfVec = i;
            }
        }

        tmpVec = mVecDoubleDragon;
        size = tmpVec.size();

        for (int i = 0; i < size; i++) {
            int[] p = tmpVec.get(i);
            if (minValue > p[0]) {
                cardType = CardType.LL_DOUBLE_DRAGON;
                minValue = p[0];
                indexOfVec = i;
            }
        }

        tmpVec = mVecSingleDragon;
        size = tmpVec.size();

        for (int i = 0; i < size; i++) {
            int[] p = tmpVec.get(i);
            if (minValue > p[0]) {
                cardType = CardType.LL_SINGLE_DRAGON;
                minValue = p[0];
                indexOfVec = i;
            }
        }

        tmpVec = mVecTriple;
        size = tmpVec.size();

        for (int i = 0; i < size; i++) {
            int[] p = tmpVec.get(i);
            if (minValue > p[0]) {
                cardType = CardType.LL_TRIPLE;
                minValue = p[0];
                indexOfVec = i;
            }
        }

        // 下家敌人剩2张
        if (nextEnemyLeftCardNum == 2) {
            if (cardType != -1) {
                return new int[] {
                        cardType, indexOfVec
                };
            } else { // 最大的对牌
                tmpVec = mVecDouble;
                size = tmpVec.size();
                int min2 = -1;
                for (int i = 0; i < size; i++) {
                    int[] p = tmpVec.get(i);
                    if (min2 <= p[0]) {
                        cardType = CardType.LL_DOUBLE;
                        minValue = p[0];
                        min2 = p[0];
                        indexOfVec = i;
                    }
                }
            }

        } else { // 最小的对牌
            tmpVec = mVecDouble;
            size = tmpVec.size();

            for (int i = 0; i < size; i++) {
                int[] p = tmpVec.get(i);
                if (minValue > p[0]) {
                    cardType = CardType.LL_DOUBLE;
                    minValue = p[0];
                    indexOfVec = i;
                }
            }
        }

        // 下家敌人剩1张
        if (nextEnemyLeftCardNum == 1) {
            if (cardType != -1) {
                return new int[] {
                        cardType, indexOfVec
                };
            } else {// 最大的单牌（TODO 应该从所有牌中找最大的单牌）
                int min1 = -1;
                for (int i = 0; i < size; i++) {
                    int[] p = tmpVec.get(i);
                    if (min1 <= p[0]) {
                        cardType = CardType.LL_SINGLE;
                        minValue = p[0];
                        min1 = p[0];
                        indexOfVec = i;
                    }
                }
            }

        } else { // 最小的单牌
            tmpVec = mVecSingle;
            size = tmpVec.size();

            for (int i = 0; i < size; i++) {
                int[] p = tmpVec.get(i);
                if (minValue > p[0]) {
                    cardType = CardType.LL_SINGLE;
                    minValue = p[0];
                    indexOfVec = i;
                }
            }
        }

        return new int[] {
                cardType, indexOfVec
        };
    }

}

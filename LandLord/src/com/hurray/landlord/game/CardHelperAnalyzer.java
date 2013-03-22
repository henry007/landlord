
package com.hurray.landlord.game;

import com.hurray.landlord.utils.LogUtil;

public class CardHelperAnalyzer extends BaseAnalyzer {

    public void analyze(int[] cardIds) {

        analyzeCount(cardIds);
        // 单牌
        analyzeSingle(cardIds);

        // 双牌
        analyzeDouble(cardIds);
        // 三双
        analyzeTriple(cardIds);
        // 单片顺
        analyzeSingleDragon(cardIds);
        // 双顺
        analyzeDoubleDragon(cardIds);
        // 三顺
        analyzeTripleDragon(cardIds);
        // 飞机带单
        analyzeTripleDragon(cardIds);
        // 飞机带双
        analyzeTripleDragon(cardIds);

        // 炸弹
        analyzeBomber(cardIds);

        // 最后一次判断，有炸弹就炸弹，没炸弹就过
        analyzeBomber(cardIds);
        // 判断2
        analyze2(cardIds);
        // 分析小丑
        analyzeJoker(cardIds);
    }

    @Override
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
                        // cardIds[j] = -1;
                        // mCountNormal[i] = -1;
                        break;
                    }

                }
            }
        }
    }

    @Override
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
                        // cardIds[j] = -1;
                        // cardIds[j + 1] = -1;
                        break;
                    }
                }
                // mCountNormal[i] = -1;
            }
        }
    }

    @Override
    protected void analyzeTriple(int[] cardIds) {
        // 分析三张
        for (int i = 0; i < mCountNormal.length; i++) {
            // LogUtil.d(TAG, "mCountNormal[" + i + "] i=" + mCountNormal[i]);
            if (mCountNormal[i] >= 3) {
                // mCountNormal[i] = -1;
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
                    if (k >= 3) {
                        continue;
                    }
                    if (id == i) {
                        triple[k++] = cardIds[j];
                        // cardIds[j] = -1;
                    }
                }
                mVecTriple.add(triple);
            }
        }
    }

    @Override
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
                                // cardIds[j] = -1;
                            }
                        }
                        mVecDoubleDragon.add(shuangshun);
                        // for (int s = sstart; s <= send; s++) {
                        // mCountNormal[s] = -1;
                        // }
                        sstart = send = -1;
                        continue;
                    }
                } else {
                    sstart = send = -1;
                }
            }
        }
    }

    @Override
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
                            // cards[j] = -1;
                        }
                    }

                    if (m == dur * 3 - 1) {
                        LogUtil.d("CardUtil", "analyzeTripleDragon done!!!");
                    } else {
                        LogUtil.d("CardUtil", "analyzeTripleDragon error!!!");
                    }

                    mVecTripleDragon.add(tripleDragon);
                    // for (int s = start; s <= end; s++) {
                    // mCountNormal[s] = -1;
                    // }
                    start = end = -1;
                    continue;
                } else {
                    start = end = -1;
                }
            }
        }
    }

    @Override
    protected void analyzeBomber(int[] cardIds) {
        for (int i = 0; i < mCountNormal.length - 1; i++) {
            if (mCountNormal[i] == 4) {
                mVecBomber.add(new int[] {
                        i * 4 + 3, i * 4 + 2,
                        i * 4 + 1, i * 4
                });

                // 清除这4个
                // for (int j = 0; j < cardIds.length; j++) {
                // if (cardIds[j] == i * 4 + 3) {
                // for (int k = 0; k < 4; k++) {
                // cardIds[j + k] = -1;
                // }
                // break;
                // }
                // }

                // mCountNormal[i] = -1;
            }
        }
    }

    @Override
    protected void analyzeSingleDragon(int[] cardIds) {
        // 分析单顺
        int dstart = -1;
        int dend = -1;

        for (int i = 0; i < mCountNormal.length; i++) {
            if (mCountNormal[i] >= 1 && i != mCountNormal.length - 1) {
                if (dstart == -1) {
                    dstart = i;
                } else {
                    dend = i;
                }
            } else {

                if (i == mCountNormal.length - 1 && mCountNormal[i] != 0) {
                    dend = i;
                }

                if (dstart != -1 && dend != -1) {
                    int dur = dend - dstart + 1;
                    if (dur >= 5) {
                        int m = 0;
                        int[] danshun = new int[dur];
                        for (int j = 0; j < cardIds.length; j++) {
                            int v = CardUtil.getCardValue(cardIds[j]) - 3;
                            LogUtil.d("CarndUtil", "endvalue==" + String.valueOf(dend));
                            if (v == dend) {
                                danshun[m++] = cardIds[j];
                                dend--;
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
}

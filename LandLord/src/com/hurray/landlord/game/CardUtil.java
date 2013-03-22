
package com.hurray.landlord.game;

import com.hurray.landlord.Constants;
import com.hurray.landlord.game.data.PlayerContext;
import com.hurray.landlord.utils.LogUtil;

import java.util.ArrayList;
import java.util.Random;

public class CardUtil {

    private static final String TAG = "CardUtil";

    private static final String[] CARD_NAME_ARR = new String[] {
            "J", "Q", "K", "A", "2"
    };

    // 牌值 3-15 小王16 大王17

    public static final int CARD_J_VAL = 11;

    public static final int CARD_Q_VAL = 12;

    public static final int CARD_K_VAL = 13;

    public static final int CARD_A_VAL = 14;

    public static final int CARD_2_VAL = 15;

    public static final int S_JOKER_VAL = 16; // 小王

    public static final int B_JOKER_VAL = 17; // 大王

    // CardId

    public static final int S_JOKER_ID = 52; // 小王

    public static final int B_JOKER_ID = 53; // 大王

    // 花色

    public static final int SUITS_FANG_KUAI = 0; // 方块

    public static final int SUITS_MEI_HUA = 1; // 梅花

    public static final int SUITS_HONG_TAO = 2; // 红桃

    public static final int SUITS_HEI_TAO = 3; // 黑桃

    public static final int SUITS_S_JOKER = 4; // 小怪是一种特殊花色

    public static final int SUITS_B_JOKER = 5; // 大怪是一种特殊花色

    public static Random sRand = new Random(System.currentTimeMillis());

    private static final int SHOW_CARD_NUM_PERCENT_IN_DANGER = 75; // percent

    private static final int TRY_YOUR_BEST_BEAT_ENEMY = 100; // percent

    private static final int HALF_HALF = 50; // percent

    private static final int LET_COMPANY_BEAT_ENEMY = 0; // percent

    private static final int SMALLEST_BIG_CARD_VALUE = 14;

    private static final int BIGGEST_SMALL_CARD_VALUE = 12;

    private static final int DANGER_ENEMY_LEFT_CARD_NUM = 5;

    private static final int SAFE_COMPANY_LEFT_CARD_NUM = 5;

    private static final int SAFE_SELF_HAND_COUNT = 4;

    public static boolean inRect(int x, int y, int rectX, int rectY, int rectW, int rectH) {
        if (x < rectX || x > rectX + rectW || y < rectY || y > rectY + rectH) {
            return false;
        }
        return true;
    }

    // -------------------------------------------------------------------------------

    // 0-53表示54张牌
    public static void shuffle(int[] cardIds) {
        sRand.setSeed(System.currentTimeMillis()); // 提高洗牌的随机性

        for (int i = 0; i < Constants.SHUFFLE_TIMES; i++) {
            int id0 = randCardId();
            int id1 = randCardId();
            if (id0 != id1) { // 序号为id0和id1的牌交换
                int tmpCardId = cardIds[id0];
                cardIds[id0] = cardIds[id1];
                cardIds[id1] = tmpCardId;
            }
        }
    }

    private static int randCardId() {
        return (sRand.nextInt() % Constants.TOTAL_CARDS_NUM + Constants.TOTAL_CARDS_NUM)
                % Constants.TOTAL_CARDS_NUM;
    }

    public static int randLandLordId() {
        sRand.setSeed(System.currentTimeMillis());
        return sRand.nextInt(Constants.PLAYER_NUM);
    }

    // 对cards进行从大到小 降序排序，采用冒泡排序
    public static void sortDescending(int[] cardIds) {
        for (int i = 0; i < cardIds.length; i++) {
            for (int j = i + 1; j < cardIds.length; j++) {
                if (cardIds[i] < cardIds[j]) {
                    int temp = cardIds[i];
                    cardIds[i] = cardIds[j];
                    cardIds[j] = temp;
                }
            }
        }
    }

    public static int[] copyCards(int[] cardIds) {
        int count = 0;
        for (int i = 0; i < cardIds.length; i++) {
            if (cardIds[i] != -1) {
                count++;
            }
        }

        int[] tmpCardIds = new int[count];
        int k = 0;
        for (int i = 0; i < cardIds.length; i++) {
            if (cardIds[i] != -1) {
                tmpCardIds[k++] = cardIds[i];
            }
        }

        return tmpCardIds;
    }

    /**
     * 16小王，17大王
     */
    public static int getCardValue(int cardId) {
        if (cardId < 0) {
            return cardId;
        }
        // 当扑克值为52时，是小王
        if (cardId == S_JOKER_ID) {
            return S_JOKER_VAL;
        }
        // 当扑克值为53时，是大王
        if (cardId == B_JOKER_ID) {
            return B_JOKER_VAL;
        }
        // 其它情况下返回相应的值(3,4,5,6,7,8,9,10,11(J),12(Q),13(K),14(A),15(2))
        // 3: 0 1 2 3;
        // 4: 4 5 6 7;
        // 5: 8,9,10,11; ...
        return getNormCardValue(cardId);
    }

    public static int getNormCardValue(int cardId) {
        return cardId / 4 + 3;
    }

    public static int getNormCardId(int value, int suits) {
        return (value - 3) * 4 + suits;
    }

    // 花色
    public static int getCardSuits(int cardId) {
        switch (cardId) {
            case S_JOKER_ID:
                return SUITS_S_JOKER;
            case B_JOKER_ID:
                return SUITS_B_JOKER;
            default:
                return getNormCardSuits(cardId);
        }
    }

    public static int getNormCardSuits(int cardId) {
        return cardId % 4;
    }

    /**
     * 是不是一个有效的牌型
     * 
     * @param cardIds
     * @return
     */
    public static boolean isValidCardType(int[] cardIds) {
        if (getCardType(cardIds) == CardType.LL_NONE) {
            return false;
        }

        return true;
    }

    /**
     * sortCardIds中的牌的顺序要按照牌的值从大到小排列,顺牌中不包含2
     * 
     * @param sortCardIds
     * @return
     */
    public static int getCardType(int[] sortCardIds) {
        int len = sortCardIds.length;
        // 当牌数量为1时,单牌
        if (len == 1) {
            return CardType.LL_SINGLE;
        }

        // 当牌数量为2时,可能是对牌和火箭
        if (len == 2) {
            if (sortCardIds[0] == B_JOKER_ID && sortCardIds[1] == S_JOKER_ID) {
                return CardType.LL_JOKER_AS_ROCKET;
            }
            if (getCardValue(sortCardIds[0]) == getCardValue(sortCardIds[1])) {
                return CardType.LL_DOUBLE;
            }
        }

        // 当牌数为3时,只可能是三张
        if (len == 3) {
            int count = getCardValueCount(sortCardIds, sortCardIds[0]);
            if (count == 3) {
                return CardType.LL_TRIPLE;
            }
        }

        // 当牌数为4时,可能是3带1或炸弹
        if (len == 4) {
            int count = getCardValueCount(sortCardIds, sortCardIds[1]);
            if (count == 3) {
                return CardType.LL_TRIPLE_W_SINGLE;
            }
            if (count == 4) {
                return CardType.LL_FOUR_AS_BOMBER;
            }
        }

        // 3带2
        if (len == 5) {
            if (isTripleWithDouble(sortCardIds)) {
                return CardType.LL_TRIPLE_W_DOUBLE;
            }
        }

        // 当牌数大于5时,判断是不是单顺
        if (len >= 5) {
            if (isSingleDragon(sortCardIds)) {
                return CardType.LL_SINGLE_DRAGON;
            }
        }

        // 当牌数为6时,判断是否4带2个单牌
        if (len == 6) {
            int bomberCount = 0;
            int singleCount = 0;
            int doubleCount = 0;
            for (int i = 0; i < len; i++) {
                int c = getCardValueCount(sortCardIds, sortCardIds[i]);
                if (c == 4) {
                    bomberCount++;
                }
                if (c == 1) {
                    singleCount++;
                }
                if(c==2){
                    doubleCount++;
                }
            }

            bomberCount /= 4;

            if ((bomberCount == 1&&doubleCount == 2) || (bomberCount == 1 && singleCount == 2)) {
                return CardType.LL_FOUR_W_TWO_SINGLE;
            }
        }

        // 当牌数大于等于6时,先检测是不是双顺和三顺
        if (len >= 6) {
            // 双顺
            boolean isDoubleDragon = true;
            for (int i = 0; i < len; i++) {
                if (getCardValueCount(sortCardIds, sortCardIds[i]) != 2) {
                    isDoubleDragon = false;
                    break;
                }
            }
            if (isDoubleDragon) {
                int[] tempCardIds = new int[len / 2];
                for (int i = 0; i < len / 2; i++) {
                    tempCardIds[i] = sortCardIds[i * 2];
                }
                if (isSingleDragon(tempCardIds)) {
                    LogUtil.d(TAG, "isDoubleDragon:" + isDoubleDragon);
                    return CardType.LL_DOUBLE_DRAGON;
                }
            }

            // 三顺
            boolean isTripleDragon = true;
            for (int i = 0; i < len; i++) {
                if (getCardValueCount(sortCardIds, sortCardIds[i]) != 3) {
                    isTripleDragon = false;
                    break;
                }
            }
            if (isTripleDragon) {
                int[] tempCardIds = new int[len / 3];
                for (int i = 0; i < len / 3; i++) {
                    tempCardIds[i] = sortCardIds[i * 3];
                }
                if (isSingleDragon(tempCardIds)) {
                    LogUtil.d(TAG, "isTripleDragon:" + isTripleDragon);
                    return CardType.LL_TRIPLE_DRAGON;
                }
            }

        }

        // 当牌数大于等于8，且能够被4整除时，判断是不是飞机带单牌
        if (len >= 8 && len % 4 == 0) {
            CardIdsFilter filter = new CardIdsFilter();
            int singleCount = 0;

            for (int i = 0; i < sortCardIds.length; i++) {
                int c = getCardValueCount(sortCardIds, sortCardIds[i]);
                if (c == 3) {
                    filter.addCardIdFilterRepeatValue(sortCardIds[i]);
                } else  {
                    singleCount++;
                }
            }

            if (filter.size() == singleCount) {
                int[] tempCardIds = filter.getCardIds();
                if (tempCardIds != null) {
                    sortDescending(tempCardIds);
                    if (isSingleDragon(tempCardIds)) {
                        return CardType.LL_TRIPLE_DRAGON_W_SINGLE;
                    }
                }
            }

        }

        if (len == 8) { // 判断是不是 4带2个对
            int bomberCount = 0;
            int doubleCount = 0;
            for (int i = 0; i < sortCardIds.length; i++) {
                int c = getCardValueCount(sortCardIds, sortCardIds[i]);
                if (c == 4) {
                    bomberCount++;
                } else if (c == 2) {
                    doubleCount++;
                }
            }

            bomberCount /= 4;
            doubleCount /= 2;

            if (bomberCount == 1 && doubleCount == 2) {
                return CardType.LL_FOUR_W_TWO_DOUBLE;
            }
        }

        if (len == 10 || len == 15) { // 判断是否飞机带对牌翅膀

            CardIdsFilter filter = new CardIdsFilter();
            int doubleCount = 0;
            int tripleCount = 0; 
            for (int i = 0; i < sortCardIds.length; i++) {
                int c = getCardValueCount(sortCardIds, sortCardIds[i]);
                if (c == 3) {
                    tripleCount++;
                    filter.addCardIdFilterRepeatValue(sortCardIds[i]);
                } else if (c == 2) {
                    doubleCount++;
                }
            }

            tripleCount /= 3;
            doubleCount /= 2;

            if (filter.size() == tripleCount && tripleCount == doubleCount) {
                int[] tempCardIds = filter.getCardIds();
                if (tempCardIds != null) {
                    sortDescending(tempCardIds);
                    if (isSingleDragon(tempCardIds)) {
                        return CardType.LL_TRIPLE_DRAGON_W_DOUBLE;
                    }
                }
            }

        }

        // 如果不是可知牌型,返回错误型
        return CardType.LL_NONE;
    }

    /**
     * 判断是不是顺子，没有长度限制
     * 
     * @param cardIds
     * @return
     */
    private static boolean isSingleDragon(int[] cardIds) {
        if (cardIds == null) {
            return false;
        }

        if (cardIds.length <= 0) {
            return false;
        }

        int start = getCardValue(cardIds[0]);
        // 顺子中不能包含2,king
        if (start >= 15) {
            return false;
        }
        int next;
        for (int i = 1; i < cardIds.length; i++) {
            next = getCardValue(cardIds[i]);
            if (start - next != 1) {
                return false;
            }
            start = next;
        }
        return true;
    }

    private static boolean isTripleWithDouble(int[] cardIds) {
        if (cardIds.length == 5) { // 判断是否三带二
            int begCount = getCardValueCount(cardIds, cardIds[0]);
            int endCount = getCardValueCount(cardIds, cardIds[4]);
            if ((begCount == 2 && endCount == 3) || (begCount == 3 && endCount == 2)) {
                return true;
            }
        }
        return false;
    }

    // 统计一手牌中同值的牌出现的次数来判断是对牌,三顺,三带一,炸弹,四代二等
    public static int getCardValueCount(int[] cardIds, int cardId) {
        int count = 0;
        for (int i = 0; i < cardIds.length; i++) {
            if (getCardValue(cardIds[i]) == getCardValue(cardId)) {
                count++;
            }
        }
        return count;
    }

    // 通过给出的一手牌,来返回它的牌值大小，cardIds中的顺序是从大到小排列好的
    public static int getCardTypeValue(int[] cardIds, int cardType) {
        // 这几种类型直接返回第一个值
        if (cardType == CardType.LL_SINGLE || cardType == CardType.LL_DOUBLE
                || cardType == CardType.LL_TRIPLE || cardType == CardType.LL_SINGLE_DRAGON
                || cardType == CardType.LL_DOUBLE_DRAGON || cardType == CardType.LL_TRIPLE_DRAGON
                || cardType == CardType.LL_FOUR_AS_BOMBER) {
            return getCardValue(cardIds[0]);
        }

        // 三带一和飞机返回数量为3的牌的最大牌值
        if (cardType == CardType.LL_TRIPLE_W_SINGLE || cardType == CardType.LL_TRIPLE_W_DOUBLE
                || cardType == CardType.LL_TRIPLE_DRAGON_W_SINGLE
                || cardType == CardType.LL_TRIPLE_DRAGON_W_DOUBLE) {
            for (int i = 0; i < cardIds.length - 2; i++) {
                if (getCardValue(cardIds[i]) == getCardValue(cardIds[i + 1])
                        && getCardValue(cardIds[i + 1]) == getCardValue(cardIds[i + 2])) {
                    return getCardValue(cardIds[i]);
                }
            }
        }

        // 四带二返回数量为4的牌值
        if (cardType == CardType.LL_FOUR_W_TWO_SINGLE || cardType == CardType.LL_FOUR_W_TWO_DOUBLE) {
            for (int i = 0; i < cardIds.length - 3; i++) {
                if (getCardValue(cardIds[i]) == getCardValue(cardIds[i + 1])
                        && getCardValue(cardIds[i + 1]) == getCardValue(cardIds[i + 2])
                        && getCardValue(cardIds[i + 2]) == getCardValue(cardIds[i + 3])) {
                    return getCardValue(cardIds[i]);
                }
            }
        }

        return 0;
    }

    /**
     * true f比s大， false不可比较或者小
     */
    public static boolean compare(int[] f, int[] s) {

        int fCardType = getCardType(f);
        int sCardType = getCardType(s);

        // 当两种牌型相同时
        if (fCardType == sCardType) {
            // 两手牌牌型相同时，数量不同将无法比较，默认为第二个大，使s不能出牌
            if (f.length != s.length) {
                return false;
            }

            // 牌型相同，数量相同时，比较牌值
            int fCardTypeValue = getCardTypeValue(f, fCardType);
            int sCardTypeValue = getCardTypeValue(s, sCardType);

            return fCardTypeValue > sCardTypeValue;
        }

        // 在牌型不同的时候,如果f的牌型是火箭,则返回true
        if (fCardType == CardType.LL_JOKER_AS_ROCKET) {
            return true;
        }

        if (sCardType == CardType.LL_JOKER_AS_ROCKET) {
            return false;
        }

        // 排除火箭的类型，炸弹最大
        if (fCardType == CardType.LL_FOUR_AS_BOMBER) {
            return true;
        }

        if (sCardType == CardType.LL_FOUR_AS_BOMBER) {
            return false;
        }

        // 无法比较的情况，默认为s大于f
        return false;
    }

    /**
     * 第一个出牌
     */
    public static int[] firstShowCards(int[] selfCardIds, int nextEnemyLeftCardNum) {

        BaseAnalyzer analyzer = new BaseAnalyzer();
        analyzer.analyze(selfCardIds);

        // 最后2手，快扔火箭、炸弹
        int handCount = analyzer.remainHandCount();
        ArrayList<int[]> bombers = analyzer.getVecBomber();
        if (handCount <= 2) {
            if (analyzer.getCountJoker() == 2) {
                return new int[] {
                        selfCardIds[0], selfCardIds[1]
                };
            } else if (bombers.size() > 0) {
                return bombers.get(bombers.size() - 1);
            }
        }

        ArrayList<int[]> vecSingle = analyzer.getVecSingle();
        ArrayList<int[]> vecDouble = analyzer.getVecDouble();
        ArrayList<int[]> vecTriple = analyzer.getVecTriple();

        int singleSize = vecSingle.size();
        int doubleSize = vecDouble.size();
        int tripleSize = vecTriple.size();

        int[] suggest = suggestFirstShowMinCardIds(analyzer, nextEnemyLeftCardNum);
        int cardType = suggest[0];
        int idxOfVec = suggest[1];

        LogUtil.d(TAG, "cardType:" + cardType + " idxOfVec=" + idxOfVec);

        switch (cardType) {
            case CardType.LL_TRIPLE_DRAGON: // 先出三顺和飞机
                LogUtil.d(TAG, "LL_TRIPLE_DRAGON");
                ArrayList<int[]> vecTripleDragon = analyzer.getVecTripleDragon();
                if (vecTripleDragon.size() > 0) {
                    int tripleDragon[] = vecTripleDragon.get(idxOfVec);
                    int dragonLen = tripleDragon.length / 3;
                    if (dragonLen < doubleSize
                            && getCardValue(vecDouble.get(dragonLen - 1)[0]) < SMALLEST_BIG_CARD_VALUE) { // 三顺带对牌
                        int[] tripleDragonWithDouble = new int[dragonLen * 5];
                        for (int i = 0; i < tripleDragon.length; i++) {
                            tripleDragonWithDouble[i] = tripleDragon[i];
                        }

                        for (int j = 0; j < dragonLen; j++) { // 三顺带对牌
                            tripleDragonWithDouble[tripleDragon.length + j * 2] = vecDouble
                                    .get(j)[0];
                            tripleDragonWithDouble[tripleDragon.length + j * 2 + 1] = vecDouble
                                    .get(j)[1];
                        }
                        CardUtil.sortDescending(tripleDragonWithDouble);
                        return tripleDragonWithDouble;
                    }

                    if (dragonLen < singleSize
                            && getCardValue(vecSingle.get(dragonLen - 1)[0]) < SMALLEST_BIG_CARD_VALUE) { // 三顺带单牌
                        int[] tripleDragonWithSingle = new int[dragonLen * 4];
                        for (int i = 0; i < tripleDragon.length; i++) {
                            tripleDragonWithSingle[i] = tripleDragon[i];
                        }

                        for (int j = 0; j < dragonLen; j++) { // 单牌数
                            tripleDragonWithSingle[tripleDragon.length + j] = vecSingle
                                    .get(j)[0];
                        }
                        CardUtil.sortDescending(tripleDragonWithSingle);
                        return tripleDragonWithSingle;
                    }

                    // 出三顺
                    return tripleDragon;

                }
                break;
            case CardType.LL_DOUBLE_DRAGON:
                LogUtil.d(TAG, "LL_DOUBLE_DRAGON");
                ArrayList<int[]> vecDoubleDragon = analyzer.getVecDoubleDragon();
                if (vecDoubleDragon.size() > 0) {
                    int doubleDragon[] = vecDoubleDragon.get(idxOfVec);
                    CardUtil.sortDescending(doubleDragon);
                    return doubleDragon;
                }
                break;
            case CardType.LL_SINGLE_DRAGON:
                LogUtil.d(TAG, "LL_SINGLE_DRAGON");
                ArrayList<int[]> vecSingleDragon = analyzer.getVecSingleDragon();
                if (vecSingleDragon.size() > 0) {
                    int singleDragon[] = vecSingleDragon.get(idxOfVec);
                    CardUtil.sortDescending(singleDragon);
                    return singleDragon;
                }
                break;
            case CardType.LL_TRIPLE:
                LogUtil.d(TAG, "LL_TRIPLE");
                if (tripleSize > 0) {
                    int[] triple = vecTriple.get(idxOfVec);
                    if (doubleSize > 0) { // 3带2
                        int tripleValue = getCardValue(triple[0]);
                        int doubleValue = getCardValue(vecDouble.get(0)[0]);
                        if (doubleValue <= 10 || tripleValue > doubleValue) {
                            int tripleWithDouble[] = new int[] {
                                    triple[0], triple[1], triple[2], vecDouble.get(0)[0],
                                    vecDouble.get(0)[1]
                            };
                            CardUtil.sortDescending(tripleWithDouble);
                            return tripleWithDouble;
                        }
                    }

                    if (singleSize > 0) { // 3带1
                        int tripleValue = getCardValue(triple[0]);
                        int singleValue = getCardValue(vecSingle.get(0)[0]);
                        if (singleValue <= 10 || tripleValue > singleValue) {
                            int tripleWithSingle[] = new int[] {
                                    triple[0], triple[1], triple[2], vecSingle.get(0)[0]
                            };
                            CardUtil.sortDescending(tripleWithSingle);
                            return tripleWithSingle;
                        }
                    }

                    return triple;
                }
                break;
            case CardType.LL_DOUBLE:
                LogUtil.d(TAG, "LL_DOUBLE");
                if (doubleSize > 0) {
                    int[] doubleLL = vecDouble.get(idxOfVec);
                    if (tripleSize > 0) {
                        int[] triple = vecTriple.get(0);
                        if (getCardValue(triple[0]) < 11) {
                            return new int[] {
                                    triple[0], triple[1], triple[2], doubleLL[0], doubleLL[1]
                            };

                        }
                    }

                    return doubleLL;
                }
                break;
            case CardType.LL_SINGLE:
                LogUtil.d(TAG, "LL_SINGLE");
                if (singleSize > 0) {
                    int[] single = vecSingle.get(idxOfVec);

                    if (tripleSize > 0) {
                        int[] triple = vecTriple.get(0);
                        if (getCardValue(triple[0]) < 11) {
                            return new int[] {
                                    triple[0], triple[1], triple[2], single[0]
                            };

                        }
                    }

                    return single;
                }
                break;
        }

        // 炸弹
        ArrayList<int[]> bomber = analyzer.getVecBomber();
        if (bomber.size() > 0) {
            return bomber.get(0);
        }

        // 火箭
        ArrayList<int[]> rocket = analyzer.getVecRocket();
        if (rocket.size() > 0) {
            return rocket.get(0);
        }

        // 出最大的单牌，理论上不应该到这里！！
        LogUtil.d(TAG, "???????出最大的单牌，理论上不应该到这里！！");
        return new int[] {
            selfCardIds[0]
        };
    }

    /**
     * 如果下家不是敌人 nextEnemyLeftCardNum = -1 cardType LL_TRIPLE_DRAGON
     * LL_DOUBLE_DRAGON LL_SINGLE_DRAGON LL_TRIPLE LL_DOUBLE LL_SINGLE cardType
     * indexOfVec
     */
    private static int[] suggestFirstShowMinCardIds(BaseAnalyzer analyzer, int nextEnemyLeftCardNum) {

        ArrayList<int[]> tmpVec;
        int size;

        int cardType = -1;
        int indexOfVec = 0;

        int minCardId = 55;
        int cardNum = 0;

        // 三顺
        tmpVec = analyzer.getVecTripleDragon();
        size = tmpVec.size();
        cardNum = 0;
        for (int i = 0; i < size; i++) {
            int[] p = tmpVec.get(i);
            if (minCardId > p[0]) {
                cardType = CardType.LL_TRIPLE_DRAGON;
                minCardId = p[0];
                indexOfVec = i;
                cardNum = p.length;
            }
        }
        if (cardType != -1 && nextEnemyLeftCardNum != -1 && nextEnemyLeftCardNum < cardNum) {
            return new int[] {
                    cardType, indexOfVec
            };
        }

        // 双顺
        tmpVec = analyzer.getVecDoubleDragon();
        size = tmpVec.size();
        cardNum = 0;
        for (int i = 0; i < size; i++) {
            int[] p = tmpVec.get(i);
            if (minCardId > p[0]) {
                cardType = CardType.LL_DOUBLE_DRAGON;
                minCardId = p[0];
                indexOfVec = i;
                cardNum = p.length;
            }
        }
        if (cardType != -1 && nextEnemyLeftCardNum != -1 && nextEnemyLeftCardNum < cardNum) {
            return new int[] {
                    cardType, indexOfVec
            };
        }

        // 单顺
        tmpVec = analyzer.getVecSingleDragon();
        size = tmpVec.size();
        cardNum = 0;
        for (int i = 0; i < size; i++) {
            int[] p = tmpVec.get(i);
            if (minCardId > p[0]) {
                cardType = CardType.LL_SINGLE_DRAGON;
                minCardId = p[0];
                indexOfVec = i;
                cardNum = p.length;
            }
        }
        if (cardType != -1 && nextEnemyLeftCardNum != -1 && nextEnemyLeftCardNum < cardNum) {
            return new int[] {
                    cardType, indexOfVec
            };
        }

        // 三张
        tmpVec = analyzer.getVecTriple();
        size = tmpVec.size();
        for (int i = 0; i < size; i++) {
            int[] p = tmpVec.get(i);
            if (minCardId > p[0]) {
                cardType = CardType.LL_TRIPLE;
                minCardId = p[0];
                indexOfVec = i;
            }
        }
        if (cardType != -1 && nextEnemyLeftCardNum != -1 && nextEnemyLeftCardNum < 3) {
            return new int[] {
                    cardType, indexOfVec
            };
        }

        // 对牌， 下家敌人剩2张，尽量出3张以上的牌
        tmpVec = analyzer.getVecDouble();
        size = tmpVec.size();
        if (nextEnemyLeftCardNum == 2) {
            if (cardType != -1) {
                return new int[] {
                        cardType, indexOfVec
                };
            } else { // 最大的对牌
                int maxCardId = -1;
                for (int i = 0; i < size; i++) {
                    int[] p = tmpVec.get(i);
                    if (maxCardId <= p[0]) {
                        cardType = CardType.LL_DOUBLE;
                        maxCardId = p[0];
                        indexOfVec = i;
                    }
                }
            }
        } else { // 最小的对牌
            for (int i = 0; i < size; i++) {
                int[] p = tmpVec.get(i);
                if (minCardId > p[0]) {
                    cardType = CardType.LL_DOUBLE;
                    minCardId = p[0];
                    indexOfVec = i;
                }
            }
        }

        // 单牌，下家敌人剩1张
        tmpVec = analyzer.getVecSingle();
        size = tmpVec.size();
        if (nextEnemyLeftCardNum == 1) {
            if (cardType != -1) {
                return new int[] {
                        cardType, indexOfVec
                };
            } else { // 最大的单牌
                int maxCardId = -1;
                for (int i = 0; i < size; i++) {
                    int[] p = tmpVec.get(i);
                    if (maxCardId <= p[0]) {
                        cardType = CardType.LL_SINGLE;
                        maxCardId = p[0];
                        indexOfVec = i;
                    }
                }
            }
        } else { // 最小的单牌
            for (int i = 0; i < size; i++) {
                int[] p = tmpVec.get(i);
                if (minCardId > p[0]) {
                    cardType = CardType.LL_SINGLE;
                    minCardId = p[0];
                    indexOfVec = i;
                }
            }
        }

        return new int[] {
                cardType, indexOfVec
        };
    }

    public static boolean isLandLord(int landLordId, int prevId, int nextId) {
        return (landLordId != prevId && landLordId != nextId);
    }

    public static boolean isLandLord(int landLordId, int selfId) {
        return (landLordId == selfId);
    }

    /**
     * 跟牌 出牌智能，对手，上下家相关智能
     */
    public static int[] followCards(PlayerContext ctx) {

        int[] prevCards = ctx.mInfoCommon.mPrevShowCardIds;
        int[] selfCards = ctx.getSelfCardIds();

        BaseAnalyzer analyzer = new BaseAnalyzer();
        analyzer.analyze(selfCards);

        int handCount = analyzer.remainHandCount();
        // 当玩家只剩下一手牌的时候，无论如何都要出牌
        if (handCount == 1) {
            return suggestFollowCards(prevCards, selfCards, TRY_YOUR_BEST_BEAT_ENEMY);
        }

        // 判断我该不该要牌
        if (ctx.isSelfLandLord()) { // 我是地主，要牌
            int leftCardNum = ctx.prevShowLeftCardNum();
            int enemyShowCardNumPercent = 100 - (leftCardNum * 100 / Constants.PLAYER_CARDS_NUM);
            if (leftCardNum <= DANGER_ENEMY_LEFT_CARD_NUM) {
                enemyShowCardNumPercent = TRY_YOUR_BEST_BEAT_ENEMY;
            }

            return suggestFollowCards(prevCards, selfCards, enemyShowCardNumPercent);
        } else {
            if (ctx.isPrevShowLandLord()) { // 上一手牌是地主出的，要牌
                // 根据敌人剩下的牌数设置紧急度
                int leftCardNum = ctx.prevShowLeftCardNum();
                int enemyshowCardNumPercent0 = 100 - (leftCardNum * 100 / Constants.PLAYER_CARDS_NUM);

                // 敌人剩的少，危险
                int enemyshowCardNumPercent1 = -1;
                if (leftCardNum <= DANGER_ENEMY_LEFT_CARD_NUM) {
                    enemyshowCardNumPercent1 = TRY_YOUR_BEST_BEAT_ENEMY;
                }
                
                int percent2 = 0;

                // 下家是自己家而且剩的少，让道
                int enemyshowCardNumPercent2 = -1;
                if (!ctx.isNextPlayerLandLord()) {
                    int nextLeftCardNum = ctx.nextPlayerLeftCardNum();
                    if (prevCards.length == nextLeftCardNum && nextLeftCardNum < SAFE_COMPANY_LEFT_CARD_NUM) {
                        enemyshowCardNumPercent2 = LET_COMPANY_BEAT_ENEMY;
                        percent2 = 50;
                        if (nextLeftCardNum == 1) {
                            percent2 = 85;
                        }
                    }
                }

                // 综合判断
                int enemyshowCardNumPercent;
                if (enemyshowCardNumPercent1 >= 0 && enemyshowCardNumPercent2 >= 0) {
                    if (sRand.nextInt(100) < percent2) {
                        enemyshowCardNumPercent = enemyshowCardNumPercent2;
                    } else {
                        enemyshowCardNumPercent = enemyshowCardNumPercent1;
                    }
                } else if (enemyshowCardNumPercent1 >= 0) {
                    enemyshowCardNumPercent = enemyshowCardNumPercent1;
                } else if (enemyshowCardNumPercent2 >= 0) {
                    enemyshowCardNumPercent = enemyshowCardNumPercent2;
                    LogUtil.d(TAG, "LAST IS ENEMY, LET_COMPANY_BEAT_ENEMY");
                } else {
                    enemyshowCardNumPercent = enemyshowCardNumPercent0;
                }

                return suggestFollowCards(prevCards, selfCards, enemyshowCardNumPercent);
            } else { // 上一手牌是自己家的牌
                if (ctx.isPrevShowNextOfMe()) { // 下家，不要牌，让他继续出，除非我一次出完
                    if (handCount < SAFE_SELF_HAND_COUNT) {
                        return suggestFollowCards(prevCards, selfCards, TRY_YOUR_BEST_BEAT_ENEMY);
                    }
                    return null;
                } else { // 上家，牌的大小如果大于一定值我不要，否则我顺一个
                    int cardType = getCardType(prevCards);
                    int cardTypeValue = getCardTypeValue(prevCards, cardType);
                    if (cardTypeValue < BIGGEST_SMALL_CARD_VALUE) { // 顺一个
                        int leftCardNum = ctx.prevShowLeftCardNum();

                        int showCardNumPercent = 100 - (leftCardNum * 100 / Constants.PLAYER_CARDS_NUM);
                        // 上家既然是自己人，牌剩的少，我就不要随便出了
                        if (leftCardNum < SAFE_COMPANY_LEFT_CARD_NUM) {
                            showCardNumPercent = LET_COMPANY_BEAT_ENEMY;
                            LogUtil.d(TAG, "LAST IS COMPANY, LET_COMPANY_BEAT_ENEMY");
                        } else {
                            showCardNumPercent = HALF_HALF;
                        }

                        // 下家是对手，如果他剩的牌不多，就要必须要出了
                        int nextPlayerLeftCardNum = ctx.nextPlayerLeftCardNum();
                        if (nextPlayerLeftCardNum < 4) { // 剩3张牌，咱们就出手
                            return suggestFollowCards(prevCards, selfCards, TRY_YOUR_BEST_BEAT_ENEMY);
                        } else {
                            return suggestFollowCards(prevCards, selfCards, showCardNumPercent);
                        }

                    } else {
                        return null;
                    }
                }
            }
        }
    }

    // 从selfCardIds中找到比prevCardIds大的一手牌
    private static int[] suggestFollowCards(int[] prevCardIds, int selfCardIds[],
            int enemyShowCardNumPercent) {
        try {
            // 获取card的信息，牌值，牌型
            int[] cpPrevCardIds = copyCards(prevCardIds);
            int prevCardType = getCardType(prevCardIds);
            int prevCardValue = getCardTypeValue(cpPrevCardIds, prevCardType);
            int prevCardLength = cpPrevCardIds.length;

            // 对牌进行分析
            BaseAnalyzer analyzer = new BaseAnalyzer();
            analyzer.analyze(selfCardIds);

            int handCount = analyzer.remainHandCount();

            // 最后2手，快扔炸弹
            if (handCount == 2 && analyzer.getCountJoker() == 2) {
                return new int[] {
                        selfCardIds[0], selfCardIds[1]
                };
            }

            // 最后的炸弹
            if (handCount == 1 && analyzer.getCountJoker() == 2 && selfCardIds.length == 2) {
                return new int[] {
                        selfCardIds[0], selfCardIds[1]
                };
            }

            ArrayList<int[]> tmpVec;
            int size = 0;

            // 根据适当牌型选取适单牌
            switch (prevCardType) {
                case CardType.LL_SINGLE:
                    if (enemyShowCardNumPercent == TRY_YOUR_BEST_BEAT_ENEMY) { // 最大单牌
                        int[] maxSingle = new int[] {
                                selfCardIds[0]
                        };

                        if (CardUtil.compare(maxSingle, cpPrevCardIds)) {
                            return maxSingle;
                        }
                    }

                    tmpVec = analyzer.getVecSingle();
                    size = tmpVec.size();
                    for (int i = 0; i < size; i++) {
                        int[] single = tmpVec.get(i);
                        int val = CardUtil.getCardValue(single[0]);
                        if (val > prevCardValue) {
                            return single;
                        }
                    }

                    // 如果单牌中没有，拆牌：选择现有牌型中除火箭和4个2后的最大一个
                    int idx = 0;
                    if (analyzer.getCountJoker() == 2) {
                        idx += 2;
                    }
                    if (analyzer.getCount2() == 4) {
                        idx += 4;
                    }
                    if (CardUtil.getCardValue(selfCardIds[idx]) > prevCardValue) {
                        return new int[] {
                            selfCardIds[idx]
                        };
                    } else if (enemyShowCardNumPercent > SHOW_CARD_NUM_PERCENT_IN_DANGER) {
                        if (analyzer.getCount2() > 1) {
                            // 根据leftCardNumPercent 拆2 出2
                            for (int i = 0; i < selfCardIds.length; i++) {
                                if (CardUtil.getCardValue(selfCardIds[i]) == 15) {
                                    return new int[] {
                                        selfCardIds[i]
                                    };
                                }
                            }
                        } else if (analyzer.getCountJoker() == 2) {
                            // 根据leftCardNumPercent 拆炸弹 出小王
                            return new int[] {
                                selfCardIds[1]
                            };
                        }
                    }
                    break;
                case CardType.LL_DOUBLE:
                    // 寻找合适的对牌（ 对牌是从小到大排列的）
                    tmpVec = analyzer.getVecDouble();
                    size = tmpVec.size();
                    for (int i = 0; i < size; i++) {
                        int[] doubleLL = tmpVec.get(i);
                        int val = CardUtil.getCardValue(doubleLL[0]);
                        if (val > prevCardValue) {
                            return doubleLL;
                        }
                    }

                    // 如果对牌中没有，则需要检查双顺
                    tmpVec = analyzer.getVecDoubleDragon();
                    size = tmpVec.size();
                    for (int i = 0; i < size; i++) {
                        int[] doubleDragon = tmpVec.get(i);
                        // 双顺从大到小，所以倒序检查大小
                        for (int j = doubleDragon.length - 1; j > 0; j--) {
                            int val = CardUtil.getCardValue(doubleDragon[j]);
                            if (val > prevCardValue) {
                                return new int[] {
                                        doubleDragon[j], doubleDragon[j - 1]
                                };
                            }
                        }
                    }

                    // 如果双顺中没有，则需要检查三张
                    tmpVec = analyzer.getVecTriple();
                    size = tmpVec.size();
                    for (int i = 0; i < size; i++) {
                        int[] triple = tmpVec.get(i);
                        int val = CardUtil.getCardValue(triple[0]);
                        if (val > prevCardValue) {
                            return new int[] {
                                    triple[0], triple[1]
                            };
                        }
                    }
                    break;
                case CardType.LL_TRIPLE:
                    tmpVec = analyzer.getVecTriple();
                    size = tmpVec.size();
                    for (int i = 0; i < size; i++) {
                        int[] triple = tmpVec.get(i);
                        int val = CardUtil.getCardValue(triple[0]);
                        if (val > prevCardValue) {
                            return triple;
                        }
                    }
                    break;
                case CardType.LL_TRIPLE_W_SINGLE: {
                    // 3带1
                    if (selfCardIds.length < 4) {
                        break;
                    }
                    boolean foundTriple = false;
                    int[] tripleWithSingle = new int[] {
                            -1, -1, -1, -1
                    };
                    tmpVec = analyzer.getVecTriple();
                    size = tmpVec.size();
                    for (int i = 0; i < size; i++) {
                        int[] triple = tmpVec.get(i);
                        int val = CardUtil.getCardValue(triple[0]);
                        if (val > prevCardValue) {
                            for (int j = 0; j < triple.length; j++) {
                                tripleWithSingle[j] = triple[j];
                                foundTriple = true;
                            }
                        }
                    }
                    // 没有三张满足条件
                    if (!foundTriple) {
                        break;
                    }
                    // 再找单牌组合成3带1
                    tmpVec = analyzer.getVecSingle();
                    size = tmpVec.size();
                    int[] single = null;
                    if (size > 0) {
                        single = tmpVec.get(0);
                    }

                    // 不能用太大的单牌, 但是如果危险就不管是不是大的单牌了，出吧
                    if (single != null
                            && (getCardValue(single[0]) < SMALLEST_BIG_CARD_VALUE || enemyShowCardNumPercent > SHOW_CARD_NUM_PERCENT_IN_DANGER)) {
                        tripleWithSingle[3] = single[0];
                    } else { // 拆6个以上的单顺，取单牌
                        tmpVec = analyzer.getVecSingleDragon();
                        size = tmpVec.size();
                        for (int i = 0; i < size; i++) {
                            int[] singleDragon = tmpVec.get(i);
                            if (singleDragon.length >= 6) {
                                tripleWithSingle[3] = singleDragon[0];
                            }
                        }
                    }

                    // 从中随便找一个最小的
                    if (tripleWithSingle[3] < 0) {
                        for (int i = selfCardIds.length - 1; i >= 0; i--) {
                            if (CardUtil.getCardValue(selfCardIds[i]) != CardUtil
                                    .getCardValue(tripleWithSingle[0])) {
                                tripleWithSingle[3] = selfCardIds[i];
                            }
                        }
                    }
                    if (tripleWithSingle[3] >= 0) {
                        CardUtil.sortDescending(tripleWithSingle);
                        return tripleWithSingle;
                    }
                }
                    break;
                case CardType.LL_TRIPLE_W_DOUBLE: {
                    // 3带2
                    if (selfCardIds.length < 5) {
                        break;
                    }
                    boolean foundTriple = false;
                    int[] tripleWithDouble = new int[] {
                            -1, -1, -1, -1, -1
                    };
                    tmpVec = analyzer.getVecTriple();
                    size = tmpVec.size();
                    for (int i = 0; i < size; i++) {
                        int[] triple = tmpVec.get(i);
                        int val = CardUtil.getCardValue(triple[0]);
                        if (val > prevCardValue) {
                            for (int j = 0; j < triple.length; j++) {
                                tripleWithDouble[j] = triple[j];
                                foundTriple = true;
                            }
                        }
                    }
                    // 没有三张满足条件
                    if (!foundTriple) {
                        break;
                    }

                    // 再找对牌合成3带2
                    tmpVec = analyzer.getVecDouble();
                    size = tmpVec.size();
                    int[] doubleLL = null;
                    if (size > 0) {
                        doubleLL = tmpVec.get(0);
                    }

                    // 不能用太大的对牌,但是如果危险就不管是不是大的对牌了，出吧
                    if (doubleLL != null
                            && (getCardValue(doubleLL[0]) < SMALLEST_BIG_CARD_VALUE || enemyShowCardNumPercent > SHOW_CARD_NUM_PERCENT_IN_DANGER)) {
                        tripleWithDouble[3] = doubleLL[0];
                        tripleWithDouble[4] = doubleLL[1];
                    }

                    if (tripleWithDouble[3] >= 0 && tripleWithDouble[4] >= 0) {
                        CardUtil.sortDescending(tripleWithDouble);
                        return tripleWithDouble;
                    }
                }
                    break;
                case CardType.LL_SINGLE_DRAGON: {
                    tmpVec = analyzer.getVecSingleDragon();
                    size = tmpVec.size();
                    for (int i = 0; i < size; i++) {
                        int[] singleDragon = tmpVec.get(i);
                        if (singleDragon.length == prevCardLength) {
                            if (prevCardValue < CardUtil.getCardValue(singleDragon[0])) {
                                return singleDragon;
                            }
                        }
                    }

                    for (int i = 0; i < size; i++) {
                        int[] singleDragon = tmpVec.get(i);
                        if (singleDragon.length > prevCardLength) {

                            // 2个顺子中最大值比较
                            if (prevCardValue >= CardUtil.getCardValue(singleDragon[0])) {
                                continue;
                            }

                            // 可能拆出3张以上(含)单牌
                            if ((singleDragon.length - prevCardLength) >= 3) {
                                if (sRand.nextInt(100) < enemyShowCardNumPercent) {
                                    int begIdx = 0;
                                    for (int j = 0; j < singleDragon.length; j++) {
                                        if (prevCardValue < CardUtil.getCardValue(singleDragon[j])) {
                                            begIdx = j;
                                        } else {
                                            break;
                                        }
                                    }

                                    if ((singleDragon.length - begIdx) >= prevCardLength) {
                                        int[] properSingleDragon = new int[prevCardLength];

                                        int pIdx = 0;
                                        for (int m = begIdx; m < singleDragon.length; m++) {
                                            properSingleDragon[pIdx++] = singleDragon[m];
                                        }
                                        return properSingleDragon;
                                    }
                                }
                            } else { // 多出1张或2张
                                int begIdx = 0;
                                if (singleDragon.length - prevCardLength == 1) {
                                    if (prevCardValue < CardUtil.getCardValue(singleDragon[1])) {
                                        begIdx = 1;
                                    } else {
                                        begIdx = 0;
                                    }
                                } else if (singleDragon.length - prevCardLength == 2) {
                                    if (prevCardValue < CardUtil.getCardValue(singleDragon[2])) {
                                        begIdx = 2;
                                    } else if (prevCardValue < CardUtil
                                            .getCardValue(singleDragon[1])) {
                                        begIdx = 1;
                                    } else {
                                        begIdx = 0;
                                    }
                                }

                                if ((singleDragon.length - begIdx) >= prevCardLength) {
                                    int[] properSingleDragon = new int[prevCardLength];
                                    int pIdx = 0;
                                    for (int m = begIdx; m < singleDragon.length; m++) {
                                        properSingleDragon[pIdx++] = singleDragon[m];
                                    }
                                    return properSingleDragon;
                                }
                            }
                        }
                    }
                }
                    break;
                case CardType.LL_DOUBLE_DRAGON:
                    tmpVec = analyzer.getVecDoubleDragon();
                    size = tmpVec.size();

                    for (int i = size - 1; i >= 0; i--) {
                        int doubleDragon[] = tmpVec.get(i);
                        if (doubleDragon.length < prevCardLength) {
                            continue;
                        }

                        if (prevCardValue < CardUtil.getCardValue(doubleDragon[0])) {
                            if (doubleDragon.length == prevCardLength) {
                                return doubleDragon;
                            } else {
                                int index = 0;
                                for (int j = doubleDragon.length - 1; j >= 0; j--) {
                                    if (prevCardValue < CardUtil.getCardValue(doubleDragon[j])) {
                                        index = j / 2;
                                        break;
                                    }
                                }

                                int total = doubleDragon.length / 2;
                                int cardTotal = prevCardLength / 2;
                                if (index + cardTotal > total) {
                                    index = total - cardTotal;
                                }
                                int properDoubleDragon[] = new int[prevCardLength];
                                int m = 0;
                                for (int k = index * 2; k < doubleDragon.length; k++) {
                                    properDoubleDragon[m++] = doubleDragon[k];
                                }
                                return properDoubleDragon;
                            }
                        }
                    }
                    break;
                case CardType.LL_TRIPLE_DRAGON:
                    tmpVec = analyzer.getVecTripleDragon();
                    size = tmpVec.size();
                    for (int i = size - 1; i >= 0; i--) {
                        int[] tripleDragon = tmpVec.get(i);
                        if (prevCardLength > tripleDragon.length) {
                            continue;
                        }

                        if (prevCardValue < CardUtil.getCardValue(tripleDragon[0])) {
                            if (prevCardLength == tripleDragon.length) {
                                return tripleDragon;
                            } else {
                                int[] properTripleDragon = new int[prevCardLength];
                                for (int k = 0; k < prevCardLength; k++) {
                                    properTripleDragon[k] = tripleDragon[k]; // ????有问题
                                }
                                return properTripleDragon;
                            }
                        }
                    }
                    break;
                case CardType.LL_TRIPLE_DRAGON_W_SINGLE: // 飞机带翅膀：三顺＋同数量的单牌
                    tmpVec = analyzer.getVecTripleDragon();
                    size = tmpVec.size();
                    int tripleNum = prevCardLength / 4; // 三张的个数
                    // 例如：33344456则 tripleNum = 2
                    int prevTripleDragonLength = tripleNum * 3; // 上家三顺的牌数
                    // 例如:33344456 则 = 6
                    int[] temp = new int[tripleNum]; // 单牌数组
                    int[] tripleDws = new int[prevCardLength];
                    if (selfCardIds.length > prevCardLength && size != 0) {
                        for (int i = size - 1; i >= 0; i--) {
                            int[] tripleDragon = tmpVec.get(i);
                            if (prevTripleDragonLength > tripleDragon.length) {
                                continue;
                            }
                            if (prevCardValue < CardUtil.getCardValue(tripleDragon[0])) {
                                // TODO 选单构造飞机带翅膀
                                size = analyzer.getVecSingle().size(); // 单牌集合大小
                                if (prevCardLength % 2 == 0) { // 例如：333444
                                    if (size >= tripleNum) { // 手中单牌数大于需要的数量则选单牌
                                        for (int j = 0; j < tripleNum; j++) {
                                            temp[j] = getVecSingle(enemyShowCardNumPercent);
                                        }
                                    } else { // 手中单牌数不满足需要则选对子
                                        for (int j = 0; j < tripleNum; j++) {
                                            temp[j] = getVecDouble(enemyShowCardNumPercent)[j];
                                        }
                                    }
                                } else { // 333444555
                                    if (size >= tripleNum) {
                                        for (int j = 0; j < tripleNum; j++) {
                                            temp[j] = getVecSingle(enemyShowCardNumPercent);
                                        }
                                    } else if (size == 1) {
                                        for (int j = 0; j < tripleNum; j++) {
                                            temp[j] = getVecDouble(enemyShowCardNumPercent)[j];
                                        }
                                        temp[tripleNum - 1] = getVecSingle(enemyShowCardNumPercent);
                                    }
                                }
                                for (int j = 0; j < tripleNum; j++) {
                                    tripleDws[j] = tripleDragon[j];
                                }
                                for (int j = 0; j < temp.length; j++) {
                                    tripleDws[tripleNum + j] = temp[j];
                                }
                                return tripleDws;
                            }
                        }
                    } else { // 手中牌数不够或没有三顺，则有炸弹出炸弹， 没就过
                        tmpVec = analyzer.getVecBomber();
                        size = tmpVec.size();
                        int bomber[] = null;
                        if (size > 0) {
                            bomber = tmpVec.get(0);
                            return bomber;
                        }
                    }

                    break;
                case CardType.LL_TRIPLE_DRAGON_W_DOUBLE: // 飞机带翅膀：三顺＋同数量的对牌
                    tmpVec = analyzer.getVecTripleDragon();
                    size = tmpVec.size();
                    tripleNum = prevCardLength / 5; // 三张的个数
                    prevTripleDragonLength = tripleNum * 3; // 上家三顺的牌数
                    temp = new int[tripleNum]; // 单牌数组
                    tripleDws = new int[prevCardLength]; // 返回的数组
                    if (selfCardIds.length > prevCardLength && size != 0) {
                        for (int i = size - 1; i >= 0; i--) {
                            int[] tripleDragon = tmpVec.get(i);
                            if (prevTripleDragonLength > tripleDragon.length) {
                                continue;
                            }
                            if (prevCardValue < CardUtil.getCardValue(tripleDragon[0])) {
                                size = analyzer.getVecDouble().size();
                                if (size >= tripleNum) {
                                    for (int j = 0; j < tripleNum; j++) {
                                        temp[j] = getVecDouble(enemyShowCardNumPercent)[j];
                                    }
                                    for (int j = 0; j < tripleNum; j++) {
                                        tripleDws[j] = tripleDragon[j];
                                    }
                                    for (int j = 0; j < temp.length; j++) {
                                        tripleDws[tripleNum + j] = temp[j];
                                    }
                                    return tripleDws;

                                } else {
                                    break;
                                }
                            }
                        }

                    } else { // 手中牌数不够或没有三顺，则有炸弹出炸弹， 没就过
                        tmpVec = analyzer.getVecBomber();
                        size = tmpVec.size();
                        int bomber[] = null;
                        if (size > 0) {
                            bomber = tmpVec.get(0);
                            return bomber;
                        }
                    }
                    break;
                case CardType.LL_FOUR_AS_BOMBER: {
                    tmpVec = analyzer.getVecBomber();
                    size = tmpVec.size();
                    int bomber[] = null;
                    if (size > 0) {
                        for (int i = 0; i < size; i++) {
                            bomber = tmpVec.get(i);
                            if (prevCardValue < CardUtil.getCardValue(bomber[0])) {
                                return bomber;
                            }
                        }
                    }
                }
                    break;
                case CardType.LL_FOUR_W_TWO_SINGLE:
                case CardType.LL_FOUR_W_TWO_DOUBLE: {
                    // 有炸弹出炸弹， 没就过
                    tmpVec = analyzer.getVecBomber();
                    size = tmpVec.size();
                    int bomber[] = null;
                    if (size > 0) {
                        bomber = tmpVec.get(0);
                        return bomber;
                    }
                }
                    break;
                case CardType.LL_JOKER_AS_ROCKET:
                    return null;
            }

            // TODO 如果可以一次性出完，无论如何都要，由followCards处理
            // 根据leftCardNumPercent的值来判断要牌的必要性，是否必须出炸弹或者火箭
            boolean needBomber = false;
            if (enemyShowCardNumPercent < SHOW_CARD_NUM_PERCENT_IN_DANGER) {
                enemyShowCardNumPercent /= 5;
                if (sRand.nextInt(100) < enemyShowCardNumPercent) {
                    needBomber = true;
                }
            } else {
                needBomber = true;
            }

            if (needBomber) {
                tmpVec = analyzer.getVecBomber();
                size = tmpVec.size();
                if (size > 0) {
                    return tmpVec.get(size - 1);
                }

                tmpVec = analyzer.getVecRocket();
                size = tmpVec.size();
                if (size > 0) {
                    return tmpVec.get(0);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void printCards(String tag, int[] cardIds) {
        if (cardIds != null) {
            String cardsString = "";
            for (int i = 0; i < cardIds.length; i++) {
                cardsString += (toString(cardIds[i]) + " ");
            }

            LogUtil.d(TAG, tag + cardsString);

        } else {
            LogUtil.d(TAG, tag + "null");
        }
    }

    public static String toString(int cardId) {
        String str = "[";

        switch (getCardSuits(cardId)) {
            case SUITS_FANG_KUAI:
                str += "◇";
                break;
            case SUITS_MEI_HUA:
                str += "♧";
                break;
            case SUITS_HONG_TAO:
                str += "♡";
                break;
            case SUITS_HEI_TAO:
                str += "♤";
                break;
            case SUITS_S_JOKER:
                str += "@";
                break;
            case SUITS_B_JOKER:
                str += "☆";
                break;
            default:
                break;
        }

        int val = getCardValue(cardId);
        if (val >= 3 && val < 10) {
            str += (" " + String.valueOf(val));
        } else if (val == 10) {
            str += String.valueOf(val);
        } else if (val > 10 && val < S_JOKER_VAL) {
            str += (" " + CARD_NAME_ARR[val - 11]);
        } else if (val == S_JOKER_VAL) {
            str += " S";
        } else if (val == B_JOKER_VAL) {
            str += " B";
        }

        str += "]";

        return str;
    }

    // // TODO 暂时放这里
    // public static int nextEnemyLeftCardNum(Player prev, Player next) {
    // int nextEnemysLeftCardNum = -1;
    // if (GameParams.sLandLordId == next.mId
    // || (GameParams.sLandLordId != next.mId && GameParams.sLandLordId !=
    // prev.mId)) { // 是对手
    // if (next.mPokes.length <= 2) {
    // nextEnemysLeftCardNum = next.mPokes.length;
    // }
    // }
    //
    // return nextEnemysLeftCardNum;
    // }

    public static String toString(int[] cardIds) {
        String str = " ";
        for (int i = 0; i < cardIds.length; i++) {
            str += toString(cardIds[i]) + " ";
        }
        return str;
    }

    // 选择单牌，用于组合飞机
    private static int getVecSingle(int showCardNumPercent) {
        BaseAnalyzer analyzer = new BaseAnalyzer();
        ArrayList<int[]> tmpVec = analyzer.getVecSingle();
        int size = tmpVec.size();
        int[] single = null;
        int result = -1;
        if (size > 0) {
            single = tmpVec.get(0);
        }
        if (single != null
                && (getCardValue(single[0]) < SMALLEST_BIG_CARD_VALUE || showCardNumPercent > SHOW_CARD_NUM_PERCENT_IN_DANGER)) {
            result = single[0];
            tmpVec.remove(0);
        }
        return result;
    }

    private static int[] getVecDouble(int showCardNumPercent) {
        BaseAnalyzer analyzer = new BaseAnalyzer();
        ArrayList<int[]> tmpVec = analyzer.getVecDouble();
        int size = tmpVec.size();
        int[] vecDouble = null;
        int[] result = {
                -1, -1
        };
        if (size > 0) {
            vecDouble = tmpVec.get(0);
        }
        if (vecDouble != null
                && (getCardValue(vecDouble[0]) < SMALLEST_BIG_CARD_VALUE || showCardNumPercent > SHOW_CARD_NUM_PERCENT_IN_DANGER)) {
            result[0] = vecDouble[0];
            result[1] = vecDouble[1];
            tmpVec.remove(0);
        }
        return result;
    }

    public static int suggestDeclareLordNum(int[] selfCardIds) {
        // 对牌进行分析

        /**
         * 因为在斗地主中，火箭、炸弹、王和2可以认为是大牌，所以叫牌需要按照这些牌的多少来判断。 下面是一个简单的原则：
         * 假定火箭为8分，炸弹为6分，大王4分，小王3分，一个2为2分，则当分数 大于等于7分时叫三倍； 大于等于5分时叫二倍；
         * 大于等于3分时叫一倍； 小于三分不叫。
         **/

        int num = analyzeCardsQuality(selfCardIds);

        if (num >= 7) {
            return 3;
        } else if (num < 7 && num >= 5) {
            return 2;
        } else if (num >= 3 && num < 5) {
            return 1;
        }

        return 0;

    }

    public static boolean suggestRobLord(int[] selfCardIds) {
        if (analyzeCardsQuality(selfCardIds) >= 6) {
            return sRand.nextBoolean();
        }

        return false;
    }

    private static int analyzeCardsQuality(int[] selfCardIds) {
        BaseAnalyzer analyzer = new BaseAnalyzer();
        analyzer.analyze(selfCardIds);

        int num = 0;
        num += (analyzer.getVecRocket().size() * 8);
        num += (analyzer.getVecBomber().size() * 6);
        if (analyzer.getVecRocket().size() <= 0) {
            num += (analyzer.getCountJoker() * 3);
        }
        if (analyzer.getCount2() != 4) {
            num += (analyzer.getCount2() * 2);
        }

        return num;
    }

    public static long caculatePlus(int[] showCards) {
        if (showCards == null || showCards.length <= 0) {
            return 0;
        }

        long plus = 0;
        int cardType = CardUtil.getCardType(showCards);
        switch (cardType) {
            case CardType.LL_JOKER_AS_ROCKET:
                plus += 10000;
                break;
            case CardType.LL_FOUR_AS_BOMBER:
                plus += 5000;
                break;
            case CardType.LL_SINGLE_DRAGON:
                if (showCards.length > 5) {
                    plus += 500 * (showCards.length - 5);
                }
                break;
            case CardType.LL_DOUBLE_DRAGON: {
                if (showCards.length > 6) {
                    plus += 1000 * (showCards.length - 6) / 2;
                }
            }
                break;
            case CardType.LL_TRIPLE_DRAGON: {
                if (showCards.length > 6) {
                    plus += 2000 * (showCards.length - 6) / 3;
                }
            }
                break;
            case CardType.LL_TRIPLE_DRAGON_W_SINGLE: {
                if (showCards.length > 8) {
                    plus += 2000 * (showCards.length - 8) / 4;
                }
            }
                break;
            case CardType.LL_TRIPLE_DRAGON_W_DOUBLE: {
                if (showCards.length == 15) {
                    plus += 2000;
                }
            }
                break;
        }

        return plus;
    }
}

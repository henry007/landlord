package com.hurray.landlord.game;

public interface CardType {
    // 斗地主
    public static final int LL_NONE = -1;

    // 单牌：单个牌（如：红桃 5 ）。
    public static final int LL_SINGLE = 0;

    // 对牌：数值相同的两张牌（如：梅花 4+ 方块 4 ）。

    public static final int LL_DOUBLE = 1;

    // 三张牌：数值相同的三张牌（如：3个 J ）。
    public static final int LL_TRIPLE = 2;

    // 三带一：数值相同的三张牌 + 一张单牌。（如： 333+6）
    public static final int LL_TRIPLE_W_SINGLE = 3;

    // 三带二：数值相同的三张牌 + 一对牌。（如： 444+99）
    public static final int LL_TRIPLE_W_DOUBLE = 4;

    // 单顺：五张或更多的连续单牌（如： 45678 或 78910JQK ）。不包括 2 点和双王。
    public static final int LL_SINGLE_DRAGON = 5;

    // 双顺：三对或更多的连续对牌（如： 334455 、 7788991010JJ ）。不包括 2 点和双王。
    public static final int LL_DOUBLE_DRAGON = 6;

    // 三顺：二个或更多的连续三张牌（如： 333444 、 555666777888 ）。不包括 2 点和双王。
    public static final int LL_TRIPLE_DRAGON = 7;

    // 飞机带翅膀：三顺＋同数量的单牌（如： 444555+79）
    public static final int LL_TRIPLE_DRAGON_W_SINGLE = 8;

    // 飞机带翅膀：三顺＋同数量的对牌（如：333444555+7799JJ）
    public static final int LL_TRIPLE_DRAGON_W_DOUBLE = 9;

    // 四带二 （如： 5555+3+8）
    public static final int LL_FOUR_W_TWO_SINGLE = 10;

    // 四带二（如：4444+55+77）
    public static final int LL_FOUR_W_TWO_DOUBLE = 11;

    // 炸弹：四张同数值牌（如4个 7 ）
    public static final int LL_FOUR_AS_BOMBER = 12;

    // 火箭：即双王（大王和小王），最大的牌。
    public static final int LL_JOKER_AS_ROCKET = 13;
}

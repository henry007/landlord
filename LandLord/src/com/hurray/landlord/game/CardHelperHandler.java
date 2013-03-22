
package com.hurray.landlord.game;

import com.hurray.landlord.game.data.PlayerContext;

import java.util.ArrayList;

public class CardHelperHandler {

    public static ArrayList<int[]> getCardHelper(PlayerContext ctx) {

        int cardType = CardUtil.getCardType(ctx.mInfoCommon.mPrevShowCardIds);

        CardHelper cardHelper = null;
        // 单牌
        if (cardType == CardType.LL_SINGLE) {

            cardHelper = new CardSingleHelper(ctx);
        }
        // 双牌
        else if (cardType == CardType.LL_DOUBLE) {

            cardHelper = new CardDoubleHelper(ctx);
        }
        // 三双
        else if (cardType == CardType.LL_TRIPLE) {

            cardHelper = new CardTripleHelper(ctx);
        }
        // 三带一
        else if (cardType == CardType.LL_TRIPLE_W_SINGLE) {

            cardHelper = new CardTripleWithSingleHelper(ctx);
        }
        // 三带二
        else if (cardType == CardType.LL_TRIPLE_W_DOUBLE) {

            cardHelper = new CardTripleWithDoubleHelper(ctx);

        }
        // 单片顺
        else if (cardType == CardType.LL_SINGLE_DRAGON) {

            cardHelper = new CardSingleDragonHelper(ctx);
        }
        // 双顺
        else if (cardType == CardType.LL_DOUBLE_DRAGON) {

            cardHelper = new CardDoubleDragonHelper(ctx);
        }
        // 三顺
        else if (cardType == CardType.LL_TRIPLE_DRAGON) {

            cardHelper = new CardTripleDragonHelper(ctx);
        }
        // 飞机带单
        else if (cardType == CardType.LL_TRIPLE_DRAGON_W_SINGLE) {

            cardHelper = new CardTripleDragonWithSingleHelper(ctx);
        }
        // 飞机带双
        else if (cardType == CardType.LL_TRIPLE_DRAGON_W_DOUBLE) {

            cardHelper = new CardTripleDragonWithDoubleHelper(ctx);
        }
        // 炸弹
        else if (cardType == CardType.LL_FOUR_AS_BOMBER) {

            cardHelper = new CardBombHelper(ctx);
        }
        // 最后一次判断，有炸弹就炸弹，没炸弹就过
        else if (cardType == CardType.LL_FOUR_W_TWO_DOUBLE) {

            cardHelper = new CardLastJudgeHelper(ctx);
        } else {

            return null;
        }

        if (cardHelper != null) {
				try {
					return cardHelper.suggestCards();
				} catch (Exception e) {
					e.printStackTrace();
				}
        }
        return null;

    }

}

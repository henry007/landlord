
package com.hurray.landlord.game;

import com.hurray.landlord.game.data.PlayerContext;

import java.util.ArrayList;

public abstract class CardHelper {

    protected int leftCardNum;

    protected int[] selfCardIds;

    protected int[] preCardIds;

    protected int preCardValue;

    protected int preCardType;

    protected int preCardLength;

    protected int handCount;

    protected CardHelperAnalyzer analyzer;

    protected ArrayList<int[]> selectedList;

    protected ArrayList<int[]> tmpList;
    
    protected int[] extractedCards=null;

    // private PlayerContext mPlayerContext;

    public CardHelper(PlayerContext ctx) {

        initData(ctx.mInfoCommon.mPrevShowCardIds, ctx.getSelfCardIds());
    }
    
//    public CardHelper(int[] extractedCards){
//        
//        this.extractedCards = extractedCards;
//
//        analyzer = new CardHelperAnalyzer();
//
//        analyzer.analyze(extractedCards);
//        
//    }

    private void initData(int[] preCardIds, int[] selfCardIds) {

        // this.leftCardNum = leftCardNum;

        // 获取上家出牌precard的信息，牌值，牌型

        this.preCardIds = CardUtil.copyCards(preCardIds);

        this.preCardType = CardUtil.getCardType(preCardIds);

        this.preCardValue = CardUtil.getCardTypeValue(this.preCardIds, preCardType);

        this.preCardLength = preCardIds.length;

        this.selfCardIds = selfCardIds;

        selectedList = new ArrayList<int[]>();

        // 对手牌进行分析

        analyzer = new CardHelperAnalyzer();

        analyzer.analyze(selfCardIds);

        handCount = analyzer.remainHandCount();

    }

    protected void suggestLastChoice(int[] selfCardIds) {
        // 最后的炸弹
        if (analyzer.getCountJoker() == 2) {
            selectedList.add(new int[] {
                    selfCardIds[0], selfCardIds[1]
            });
        }
        if (analyzer.getVecBomber() != null && analyzer.getVecBomber().size() != 0) {

            selectedList.add(analyzer.getVecBomber().get(0));
        }

    }

    protected boolean checkOutSelectedList(ArrayList<int[]> selectedList) {
        if (!selectedList.isEmpty() && selectedList.size() != 0) {

            return true;
        }

        return false;
    }

    protected int[] convertInt(int obj) {
        return new int[] {
            obj
        };
    }

    protected abstract ArrayList<int[]> suggestCards();

}

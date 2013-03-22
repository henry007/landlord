
package com.hurray.landlord.game.local;

import com.hurray.landlord.game.CardType;
import com.hurray.landlord.game.data.PlayerContext;

import java.util.ArrayList;
import java.util.Random;

public class ChatAi implements OnAiContextListener {

    private OnChatListener mOnChatListener;

    private Random mRandom = new Random();

    private PlayerContext mPlayerContext;

    // 珍妮、艾莉、小曼、安娜
    private static final String[] BEAUTY_WORD_ON_MY_SHOW_IS_BIG = new String[] {
            "我那么喜欢你，你喜欢我一下会死啊。",
            "胜负还有悬念吗",
            "姐不是电视机，不要老是盯着姐看。",
            "姐不是蒙娜丽莎，不会对每个人都微笑。"
    };

    // 珍妮、艾莉、小曼、安娜
    private static final String[] BEAUTY_WORD_ON_MY_ENEMY_SHOW_IS_BIG = new String[] {
            "我诅咒你买方便面没作料",
            "本姑娘法眼一开就知道你是个妖孽",
            "别调戏我，不然我非礼你.",
            "明骚易躲，暗贱难防"
    };

    public ChatAi(PlayerContext playerContext) {
        mPlayerContext = playerContext;
    }

    public void setOnChatListener(OnChatListener l) {
        mOnChatListener = l;
    }

    public static interface OnChatListener {

        public void onChat(int playerId, String chat);

    }

    @Override
    public void onMyPassedCountIsTooMuch(int playerId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMyPassStillCanHoldByMyself(int playerId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMyShowTotalPassedTooMuchByOthers(int playerId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMyShowPassedByEnemy(int playerId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMyShowFollowed(int playerId) {

    }

    @Override
    public void onMyShowFollowedByFriend(int playerId) {
        mOnChatListener.onChat(playerId, "不要管我嘛");
    }

    @Override
    public void onMyShowFollowedByEnemy(int playerId) {
        mOnChatListener.onChat(playerId, "让人家一下嘛");
    }

    @Override
    public void onLordsShowFollowedByFriendWhichPassedByMeEarlier(int myPlayerId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLordsShowFollowedByMeWhichPassedByFriendEarlier(int myPlayerId) {        
        String chat;
        if (mRandom.nextBoolean()) {
            chat = "让我来，我还有好牌";
        } else {
            chat = "看我的吧";
        }
        mOnChatListener.onChat(myPlayerId, chat);
    }

    @Override
    public void onMyShowIsBig(int playerId) {
        ArrayList<String> chats = new ArrayList<String>(2);

        int beautyId = mPlayerContext.getBeautyId(playerId);
        if (beautyId >= 0 && beautyId < BEAUTY_WORD_ON_MY_SHOW_IS_BIG.length) {
            chats.add(BEAUTY_WORD_ON_MY_SHOW_IS_BIG[beautyId]);
        }
        chats.add("牌不好，请多关照哈");
        chats.add("");
        int size = chats.size();
        if (size > 0) {
            String chat = chats.get(mRandom.nextInt(size));
            if (chat.length() > 0) {
                mOnChatListener.onChat(playerId, chat);
            }
        }
    }

    @Override
    public void onMyFriendShowIsBig(int playerId) {
        String chat;
        if (mRandom.nextBoolean()) {
            chat = "原来你的牌这么好";
        } else {
            chat = "太给力了，亲你";
        }
        mOnChatListener.onChat(playerId, chat);
    }

    @Override
    public void onMyEnemyShowIsBig(int playerId) {
        ArrayList<String> chats = new ArrayList<String>(5);

        if (mPlayerContext.isLandLord(playerId)) { // 地主4种情况
            chats.add("难以形容我现在的心情");
            chats.add("不公平~~讨厌你啦~");
            chats.add("你的牌有杀气");
            chats.add("好牌都被你摸光了耶");
        } else {
            chats.add("你的牌有杀气");
            chats.add("好牌都被你摸光了耶");
        }

        int beautyId = mPlayerContext.getBeautyId(playerId);
        if (beautyId >= 0 && beautyId < BEAUTY_WORD_ON_MY_ENEMY_SHOW_IS_BIG.length) {
            chats.add(BEAUTY_WORD_ON_MY_ENEMY_SHOW_IS_BIG[beautyId]);
            chats.add("");
        }

        int size = chats.size();
        if (size > 0) {
            int randIdx = mRandom.nextInt(size);
            String chat = chats.get(randIdx);
            if (chat.length() > 0) {
                mOnChatListener.onChat(playerId, chat);
            }
        }
    }

    @Override
    public void onIWinMatch(int playerId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onILostMatch(int playerId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMyLeftCardNumLess(int playerId) {       
        String chat;
        if (mRandom.nextBoolean()) {
            chat = "看来这盘快要结束了";
        } else {
            chat = "承让承让啦~";
        }
        mOnChatListener.onChat(playerId, chat);
    }

    @Override
    public void onMyFriendCardNumLess(int playerId) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onMyEnemyCardNumLess(int playerId) {

        if (mPlayerContext.isLandLord(playerId)) {            
            String chat;
            if (mRandom.nextBoolean()) {
                chat = "投降输一半可以吗";
            } else {
                chat = "我好怕怕哦";
            }
            mOnChatListener.onChat(playerId, chat);            
        }

    }

    @Override
    public void onShowCardType(int playerId, int cardType) {
        ArrayList<String> chats = new ArrayList<String>(2);
        switch (cardType) {
            case CardType.LL_SINGLE_DRAGON:
                chats.add("顺子，这次看我的");
                chats.add("顺子，被拆牌乱跟哦");
                break;
            case CardType.LL_FOUR_AS_BOMBER:
                chats.add("炸掉，玩的就是心跳");
                chats.add("炸死你");
                break;
            case CardType.LL_JOKER_AS_ROCKET:
                chats.add("无敌至尊，火箭！");
                chats.add("火箭发射啦！");
                break;
            case CardType.LL_TRIPLE_W_SINGLE:
            case CardType.LL_TRIPLE_W_DOUBLE:
                chats.add("三带，别乱拆牌跟哦");
                chats.add("三带来了！");
                break;
            default:
                break;
        }
        int size = chats.size();
        if (size > 0) {
            int randIdx = mRandom.nextInt(size);
            mOnChatListener.onChat(playerId, chats.get(randIdx));
        }

    }

}

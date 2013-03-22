
package com.hurray.landlord.game.online;


public class CardIdAdapter {
/*
    public static final int SRV_S_JOKER_ID = 53;

    public static final int SRV_B_JOKER_ID = 54;

    public static final boolean ID_CONVERT_ON = true;*/

    public static int[] toServerCardIds(int[] clientCardIds) {
    	return clientCardIds;
     /*   if (!ID_CONVERT_ON) {
            return clientCardIds;
        }
        
        int[] serverCardIds = new int[clientCardIds.length];
        for (int i = clientCardIds.length - 1; i >= 0; i--) {
            serverCardIds[i] = toServerCardId(clientCardIds[i]);
        }
        return serverCardIds;*/
    }

    public static int[] toClientCardIds(int[] serverCardIds) {
    	return serverCardIds;
       /* if (!ID_CONVERT_ON) {
            return serverCardIds;
        }
        
        int[] clientCardIds = new int[serverCardIds.length];
        for (int i = serverCardIds.length - 1; i >= 0; i--) {
            clientCardIds[i] = toClientCardId(serverCardIds[i]);
        }
        return clientCardIds;*/
    }

    public static int toServerCardId(int clientCardId) {
    	return clientCardId;
       /* if (!ID_CONVERT_ON) {
            return clientCardId;
        }
        
        switch (clientCardId) {
            case CardUtil.S_JOKER_ID:
                return SRV_S_JOKER_ID;
            case CardUtil.B_JOKER_ID:
                return SRV_B_JOKER_ID;
            default: {
                int val = CardUtil.getNormCardValue(clientCardId);
                int suits = CardUtil.getNormCardSuits(clientCardId);
                return getServerNormCardId(val, suits);
            }
        }*/
    }

    public static int toClientCardId(int serverCardId) {
    	return serverCardId;
        /*if (!ID_CONVERT_ON) {
            return serverCardId;
        }

        switch (serverCardId) {
            case SRV_S_JOKER_ID:
                return CardUtil.S_JOKER_ID;
            case SRV_B_JOKER_ID:
                return CardUtil.B_JOKER_ID;
            default: {
                int val = (serverCardId - 1) % 13 + 3;
                int suits = (serverCardId - 1) / 13;
                return getClientNormCardId(val, suits);
            }
        }*/

    }

   /* private static int getServerNormCardId(int value, int suits) {
        return (value - 3) + 13 * suits + 1;
    }

    private static int getClientNormCardId(int value, int suits) {
        return CardUtil.getNormCardId(value, suits);
    }*/

}

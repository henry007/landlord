
package com.hurray.landlord.bitmaps;

import com.hurray.landlord.game.ui.UiConstants;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Iterator;

public class CardsBitmap {

    private static HashMap<Integer, CardSoftBitmapRef> sCardsHashMap = new HashMap<Integer, CardSoftBitmapRef>();

    public static Bitmap getCardBitmap(int cardId) {

        synchronized (sCardsHashMap) {
            CardSoftBitmapRef ref = sCardsHashMap.get(cardId);
            if (ref == null) {
                ref = new CardSoftBitmapRef(cardId);
                sCardsHashMap.put(cardId, ref);
            }

            return ref.getCardBitmap();
        }
    }

    public static Bitmap getSmallCardBitmap(int cardId) {

        synchronized (sCardsHashMap) {
            CardSoftBitmapRef ref = sCardsHashMap.get(cardId);
            if (ref == null) {
                ref = new CardSoftBitmapRef(cardId);
                sCardsHashMap.put(cardId, ref);
            }

            return ref.getScaledCardBitmap(UiConstants.SMALL_SCALE);
        }
    }

    public static void clearAll() {
        
        synchronized (sCardsHashMap) {
            
            Iterator<CardSoftBitmapRef> it = sCardsHashMap.values().iterator();
            while (it.hasNext()) {
                it.next().clear();
            }

            sCardsHashMap.clear();
        }
    }
}

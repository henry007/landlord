
package com.hurray.landlord.bitmaps;

import com.hurray.landlord.game.CardUtil;
import com.hurray.landlord.game.local.Effects;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Iterator;

public class CardFgsBitmap {

    private static HashMap<Integer, SoftBitmapRef> sCardFgsHashMap = new HashMap<Integer, SoftBitmapRef>();

    private static Context sAppContext;

    private static int sCardFg;

    public static void init(Context context) {
        sAppContext = context;
        Effects.init(context);
        sCardFg = Effects.getCardFg();
    }
    
    public static void updateOnlineCardFg(){
       sCardFg = Effects.CARD_FG_0; 
        
    }
    
    public static void updateCardFg() {
        sCardFg = Effects.getCardFg();
    }

    public static Bitmap getCardFgBitmap(int cardId) {
        int resId = sAppContext.getResources().getIdentifier(
                "cf" + sCardFg + "_suit" + CardUtil.getCardSuits(cardId),
                "drawable", sAppContext.getPackageName());

        synchronized (sCardFgsHashMap) {
            
            SoftBitmapRef ref = sCardFgsHashMap.get(resId);
            if (ref == null) {
                ref = new SoftBitmapRef(resId);
                sCardFgsHashMap.put(resId, ref);
            }

            return ref.getBitmap();
        }
    }

    public static void clearAll() {
        
        synchronized (sCardFgsHashMap) {
            
            Iterator<SoftBitmapRef> it = sCardFgsHashMap.values().iterator();
            while (it.hasNext()) {
                it.next().clear();
            }

            sCardFgsHashMap.clear();
        }
    }
}

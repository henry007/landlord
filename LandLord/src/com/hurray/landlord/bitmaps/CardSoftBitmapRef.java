
package com.hurray.landlord.bitmaps;

import com.hurray.landlord.utils.LogUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;

public class CardSoftBitmapRef {

    private static final String TAG = "CardSoftBitmapRef";

    private static Context sAppContext;

    private static BitmapFactory.Options sOpts;

    private static final int CARD_BG_ID = 54;

    private SoftReference<Bitmap> mBitmap = null;

    private HashMap<Float, SoftReference<Bitmap>> mScaledBitmaps = new HashMap<Float, SoftReference<Bitmap>>();

    private int mCardId;

    public static void init(Context ctx) {
        sAppContext = ctx.getApplicationContext();
        sOpts = new BitmapFactory.Options();
        sOpts.inPurgeable = true;
    }

    private static Bitmap decode(int resId) {
        LogUtil.d(TAG, "decode resId=" + resId);
        return BitmapFactory.decodeResource(sAppContext.getResources(), resId, sOpts);
    }

    private static int getCardResId(int cardId) {
        return sAppContext.getResources().getIdentifier("c_" + cardId, "drawable",
                sAppContext.getPackageName());
    }

    public CardSoftBitmapRef(int cardId) {
        update(cardId);
    }

    /**
     * mResId可用和 decode时， 确保得到可用Bitmap
     * 
     * @return
     */
    public Bitmap getCardBitmap() {

        synchronized (this) {

            Bitmap b = null;

            if (mBitmap != null) {
                b = mBitmap.get();
            }

            if ((b == null || b.isRecycled()) && mCardId >= 0) {
                Bitmap cd = decode(getCardResId(mCardId));
                if (mCardId != CARD_BG_ID) {
                    Bitmap fg = CardFgsBitmap.getCardFgBitmap(mCardId);
                    b = BitmapUtil.overlay(cd, fg);

                    if (cd != null && !cd.isRecycled())
                        cd.recycle();

                    if (fg != null && !fg.isMutable())
                        fg.recycle();
                } else {
                    b = cd;
                }

                if (b != null) {
                    update(b);
                }
            }

            return b;
        }
    }

    public Bitmap getScaledCardBitmap(float scale) {
        if (mCardId < 0) {
            return null;
        }

        synchronized (this) {

            Bitmap b = null;
            SoftReference<Bitmap> scaledBitmapRef = mScaledBitmaps.get(scale);
            if (scaledBitmapRef != null) {
                b = scaledBitmapRef.get();
            }

            if (b == null || b.isRecycled()) {
                b = getCardBitmap();
                if (b != null) {
                    int width = Math.round(b.getWidth() * scale);
                    int height = Math.round(b.getHeight() * scale);
                    b = Bitmap.createScaledBitmap(b, width, height, true);
                    if (b != null) {
                        LogUtil.d(TAG, "scaled mCardId=" + mCardId);
                        mScaledBitmaps.put(scale, new SoftReference<Bitmap>(b));
                    }
                }
            }

            return b;
        }
    }

    /**
     * 释放内存，不会清除资源。
     */
    private void freeMemory() {
        synchronized (this) {
            if (mScaledBitmaps != null) {
                Iterator<SoftReference<Bitmap>> it = mScaledBitmaps.values().iterator();
                while (it.hasNext()) {
                    freeBitmapRef((SoftReference<Bitmap>) it.next());
                }
            }

            freeBitmapRef(mBitmap);
        }
    }

    private void freeBitmapRef(SoftReference<Bitmap> ref) {
        if (ref != null) {
            BitmapUtil.recycle(ref.get());
            ref.clear();
            ref = null;
        }
    }

    /**
     * 清除资源，释放内存。
     */
    public void clear() {
        mCardId = -1;
        freeMemory();
    }

    private void update(Bitmap b) {
        if (b != null) {
            mBitmap = new SoftReference<Bitmap>(b);
        } else {
            mBitmap = null;
        }
    }

    private void update(int cardId) {
        if (cardId >= 0) {
            mCardId = cardId;
        } else {
            mCardId = -1;
        }
    }

}


package com.hurray.landlord.bitmaps;

import com.hurray.landlord.utils.LogUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;

public class SoftBitmapRef {

    private static final String TAG = "SoftBitmapRef";

    private static Context mAppContext;

    private static BitmapFactory.Options mOpts;

    private SoftReference<Bitmap> mBitmap = null;

    private HashMap<Float, SoftReference<Bitmap>> mScaledBitmaps = new HashMap<Float, SoftReference<Bitmap>>();

    private int mResId = 0;

    public static void init(Context ctx) {
        mAppContext = ctx.getApplicationContext();
        mOpts = new BitmapFactory.Options();
        mOpts.inPurgeable = true;
    }

    private static Bitmap decode(int resId) {
        LogUtil.d(TAG, "decode resId=" + resId);
        return BitmapFactory.decodeResource(mAppContext.getResources(), resId, mOpts);
    }

    public SoftBitmapRef(int resId) {
        update(resId);
    }

    /**
     * mResId可用和 decode时， 确保得到可用Bitmap
     * @return
     */
    public Bitmap getBitmap() {

        synchronized (this) {

            Bitmap b = null;

            if (mBitmap != null) {
                b = mBitmap.get();
            }

            if ((b == null || b.isRecycled()) && mResId > 0) {
                b = decode(mResId);
                if (b != null) {
                    update(b);
                }
            }

            return b;
        }
    }

    public Bitmap getScaledBitmap(float scale) {
        if (mResId <= 0) {
            return null;
        }

        synchronized (this) {

            Bitmap b = null;
            SoftReference<Bitmap> scaledBitmapRef = mScaledBitmaps.get(scale);
            if (scaledBitmapRef != null) {
                b = scaledBitmapRef.get();
            }

            if (b == null || b.isRecycled()) {
                b = getBitmap();
                if (b != null) {
                    int width = Math.round(b.getWidth() * scale);
                    int height = Math.round(b.getHeight() * scale);
                    b = Bitmap.createScaledBitmap(b, width, height, true);
                    if (b != null) {
                        LogUtil.d(TAG, "scaled mResId=" + mResId);
                        mScaledBitmaps.put(scale, new SoftReference<Bitmap>(b));
                    }
                }
            }

            return b;
        }
    }
    
    /**
     * 清除资源，释放内存。
     */
    public void clear() {
        LogUtil.d(TAG, "clear mResId=" + mResId);
        
        mResId = 0;
        freeMemory();
    }

    /**
     * 释放内存，不会清除资源。
     */
    public void freeMemory() {
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

    private void update(Bitmap b) {
        if (b != null) {
            mBitmap = new SoftReference<Bitmap>(b);
        } else {
            mBitmap = null;
        }
    }

    private void update(int resId) {
        if (resId > 0) {
            mResId = resId;
        } else {
            mResId = 0;
        }
    }

}

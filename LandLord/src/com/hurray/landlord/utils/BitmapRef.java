
package com.hurray.landlord.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.lang.ref.WeakReference;

public class BitmapRef {

    private static Context mAppContext;

    private static BitmapFactory.Options mOpts;

    private WeakReference<Bitmap> mWeakRef = null;

    private int mResId = 0;

    public static void init(Context ctx) {
        mAppContext = ctx.getApplicationContext();
        mOpts = new BitmapFactory.Options();
        mOpts.inPurgeable = true;
    }

    private static Bitmap decode(int resId) {
        return BitmapFactory.decodeResource(mAppContext.getResources(), resId, mOpts);
    }

    public BitmapRef() {
        mWeakRef = null;
        mResId = 0;
    }

    public BitmapRef(Bitmap b) {
        set(b);
    }

    public BitmapRef(int resId) {
        set(resId);
    }

    public void set(Bitmap b) {
        clear();
        update(b);
    }

    public void set(int resId) {
        clear();
        update(resId);
    }

    public Bitmap get() {
        Bitmap b = null;

        if (mWeakRef != null) {
            b = mWeakRef.get();
        }

        if (b == null && mResId > 0) {
            b = decode(mResId);
            update(b);
        }

        return b;
    }

    public boolean isEmpty() {
        if (mResId > 0 || (mWeakRef != null && mWeakRef.get() != null)) {
            return false;
        }
        return true;
    }
    
    /**
     * 释放内存，不会清除资源。
     */
    public void freeMemory() {
        if (mWeakRef != null) {
            BitmapUtil.recycle(mWeakRef.get());
            mWeakRef.clear();
            mWeakRef = null;
        }
    }

    /**
     * 清除资源，释放内存。
     */
    public void clear() {
        mResId = 0;
        freeMemory();
    }
    

    private void update(Bitmap b) {
        if (b != null) {
            mWeakRef = new WeakReference<Bitmap>(b);
        } else {
            mWeakRef = null;
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

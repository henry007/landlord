
package com.hurray.landlord.bitmaps;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;

public class BitmapUtil {

    private static final String TAG = "BitmapUtil";

    public static void recycle(Bitmap b) {
        if (b != null) {
            if (!b.isRecycled()) {
                b.recycle();
            }
            b = null;
        }
    }

    public static void drawBitmap(Canvas canvas, Bitmap b, float left, float top) {
        if (b != null && !b.isRecycled()) {
            canvas.drawBitmap(b, left, top, null);
        }
    }

    public static Bitmap overlay(Bitmap down, Bitmap up) {
       
        if (down != null && up != null) {
            int w = down.getWidth();
            int h = down.getHeight();
            Bitmap bmOverlay = Bitmap.createBitmap(w, h, Config.ARGB_8888);
            Canvas canvas = new Canvas(bmOverlay);
            canvas.drawBitmap(down, 0, 0, null);
            canvas.drawBitmap(up, 0, 0, null);
            return bmOverlay;
        }
        return null;
    }

}


package com.hurray.landlord.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class BitmapUtil {
    public static void recycle(Bitmap b) {
        if (b != null) {
            if (!b.isRecycled()) {
                b.recycle();
            }
            b = null;
        }
    }

    public static void unbindDrawables(View view) {
        if (view != null) {
            if (view.getBackground() != null) {
                view.getBackground().setCallback(null);
            }
            view.setBackgroundDrawable(null);
            view.clearAnimation();
            view.destroyDrawingCache();
        }

        if (!(view instanceof AdapterView) && view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

}


package com.hurray.landlord.animation;

import com.hurray.landlord.R;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class PlaneAnimation extends ImageTweenFrameAnim {
    
    private WeakReference<ImageView> mStillViewRef;

    public PlaneAnimation(Context context, View tweenView, ImageView frameView,
            ImageView stillView) {
        super(context, frameView, tweenView, R.anim.plane_tween);
        
        mStillViewRef = new WeakReference<ImageView>(stillView);

        setFrameAnimListener(new FrameAnimListener() {

            @Override
            public void onStop() {
                ImageView v = mStillViewRef.get();
                if (v != null)
                    v.setBackgroundResource(0);
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFrameAnimResUpdated(ImageView iv) {
                ImageView v = mStillViewRef.get();
                if (v != null)
                    v.setBackgroundResource(R.drawable.plane);
            }
        });
    }

}

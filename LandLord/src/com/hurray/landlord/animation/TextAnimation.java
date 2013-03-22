
package com.hurray.landlord.animation;

import com.hurray.landlord.R;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class TextAnimation extends TweenAnim {

    private static final String TAG = "TextAnimation";

    private WeakReference<TextView> mTextViewRef;

    public TextAnimation(Context context, TextView textView) {
        super(context, textView, R.anim.score_plus_tween);
        mTextViewRef = new WeakReference<TextView>(textView);

        setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onStop();
            }
        });
    }

    public void setText(String text) {
        mTextViewRef.get().setText(text);
    }

}

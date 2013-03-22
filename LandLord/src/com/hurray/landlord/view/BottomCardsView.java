
package com.hurray.landlord.view;

import com.hurray.landlord.Constants;
import com.hurray.landlord.R;
import com.hurray.landlord.bitmaps.CardsBitmap;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class BottomCardsView extends LinearLayout {

    private ArrayList<WeakReference<ImageView>> mBottomImages;

    private WeakReference<TextView> mRateNumTextView;

    private WeakReference<TextView> mBaseNumTextView;

//    public BottomCardsView(Context context) {
//        super(context);
//        initViews(context);
//    }

    public BottomCardsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    private void initViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.bottom_cards_view, this);

        mBottomImages = new ArrayList<WeakReference<ImageView>>(Constants.BOTTOM_CARDS_NUM);
        mBottomImages.add(new WeakReference<ImageView>((ImageView) findViewById(R.id.bottom_card0)));
        mBottomImages.add(new WeakReference<ImageView>((ImageView) findViewById(R.id.bottom_card1)));
        mBottomImages.add(new WeakReference<ImageView>((ImageView) findViewById(R.id.bottom_card2)));

        mRateNumTextView = new WeakReference<TextView>((TextView) findViewById(R.id.rate_num));
        mBaseNumTextView = new WeakReference<TextView>((TextView) findViewById(R.id.base_num));
    }

    public void reset() {
        setBottomCardIds(null);
        setRateAndBase(0, -1);
    }

    public void setBottomCardIds(int[] bottomCardIds) {
        if (bottomCardIds != null) {
            for (int i = 0; i < Constants.BOTTOM_CARDS_NUM; i++) {
                Drawable d = new BitmapDrawable(CardsBitmap.getCardBitmap(bottomCardIds[i]));
                mBottomImages.get(i).get().setBackgroundDrawable(d);

            }
        } else {
            for (int i = 0; i < Constants.BOTTOM_CARDS_NUM; i++) {
                mBottomImages.get(i).get().setBackgroundResource(R.drawable.game_card_bg);

            }
        }
    }

    public void setRateAndBase(int rate, int base) {
        setRateNum(rate);
        setBaseNum(base);
    }

    private void setRateNum(int rate) {
        if (rate >= 1) {
            mRateNumTextView.get().setText(getContext().getString(R.string.rate, rate));
        } else {
            mRateNumTextView.get().setText(null);
        }
    }

    private void setBaseNum(int base) {
        if (base > 0) {
            mBaseNumTextView.get().setText(getContext().getString(R.string.delcare_num, base * Constants.BASE_NUM));
        } else {
            mBaseNumTextView.get().setText(null);
        }
    }

}

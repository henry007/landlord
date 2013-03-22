
package com.hurray.landlord.view;

import com.hurray.landlord.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;

public class LocalGameTopView extends LinearLayout {

    private WeakReference<BottomCardsView> mBottomCardsView;

    public LocalGameTopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public BottomCardsView bottomCardsView() {
        return mBottomCardsView.get();
    }

    private void initViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.local_game_top_view, this);
        mBottomCardsView = new WeakReference<BottomCardsView>((BottomCardsView) v.findViewById(R.id.bottom_cards_view));
    }

    public void setOnBtnListener(OnClickListener l) {
        findViewById(R.id.back).setOnClickListener(l);
    }

}

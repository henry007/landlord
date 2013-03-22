
package com.hurray.landlord.view;

import com.hurray.landlord.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;

public class OnlineGameTopView extends LinearLayout {

    private WeakReference<BottomCardsView> mBottomCardsView;

    private WeakReference<View> mBtnRobot;

    public OnlineGameTopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public BottomCardsView bottomCardsView() {
        return mBottomCardsView.get();
    }

    private void initViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.online_game_top_view, this);
        mBottomCardsView = new WeakReference<BottomCardsView>((BottomCardsView) v.findViewById(R.id.bottom_cards_view));
    }

    public void setOnBtnListener(OnClickListener l) {
        findViewById(R.id.back).setOnClickListener(l);
        findViewById(R.id.emotion).setOnClickListener(l);
        findViewById(R.id.chat).setOnClickListener(l);

        mBtnRobot = new WeakReference<View>(findViewById(R.id.robot));
        mBtnRobot.get().setOnClickListener(l);
    }

    public void setRobot(boolean isRobotOn) {
        if (isRobotOn) {
            mBtnRobot.get().setBackgroundResource(R.drawable.btn_robot_on);
        } else {
            mBtnRobot.get().setBackgroundResource(R.drawable.btn_robot_off);
        }
    }

}

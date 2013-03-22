
package com.hurray.landlord.view;

import com.hurray.landlord.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class OnlineGameBgView extends FrameLayout {

    private static final String TAG = "OnlineGameBgView";

    private final int mGameBgId;

    private LayoutInflater mInflater;

    private OnlineGameBgHandler mOnlineGameBgHandler;

    public OnlineGameBgView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mGameBgId = 0;

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mOnlineGameBgHandler = createOnlineGameBgHandler(mGameBgId);
        mOnlineGameBgHandler.initViews(context);
    }

    private OnlineGameBgHandler createOnlineGameBgHandler(int gameBgId) {
        switch (gameBgId) {
            case 0:
                return new OnlineGameBg0();
            default: // 0
                return new OnlineGameBg0();
        }
    }

    public void onStart() {
        mOnlineGameBgHandler.onStart();
    }

    public void onStop() {
        mOnlineGameBgHandler.onStop();
    }
    
    public void onDestroy() {
        mOnlineGameBgHandler = null;
    }

    public void onLoadSensitiveRes() {
        mOnlineGameBgHandler.onLoadSensitiveRes();
    }

    public void onReleaseSensitiveRes() {
        mOnlineGameBgHandler.onReleaseSensitiveRes();
    }

    abstract private static class OnlineGameBgHandler {

        abstract protected void initViews(Context context);

        abstract public void onStart();

        abstract public void onStop();

        abstract public void onLoadSensitiveRes();

        abstract public void onReleaseSensitiveRes();
    }

    private class OnlineGameBg0 extends OnlineGameBgHandler {

        private WeakReference<ImageView> mBg;

        @Override
        protected void initViews(Context context) {
            View view = mInflater.inflate(R.layout.online_game_bg0, OnlineGameBgView.this);
            mBg = new WeakReference<ImageView>((ImageView) view.findViewById(R.id.game_bg0));
        }

        @Override
        public void onStart() {
            mBg.get().setBackgroundResource(R.drawable.online_game_bg0);
        }

        @Override
        public void onStop() {
            mBg.get().setBackgroundResource(0);
        }

        @Override
        public void onLoadSensitiveRes() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onReleaseSensitiveRes() {
            // TODO Auto-generated method stub

        }

    }

}

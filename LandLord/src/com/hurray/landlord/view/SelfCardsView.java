
package com.hurray.landlord.view;

import com.hurray.landlord.bitmaps.BitmapUtil;
import com.hurray.landlord.bitmaps.CardsBitmap;
import com.hurray.landlord.game.CardUtil;
import com.hurray.landlord.game.data.PlayerContext;
import com.hurray.landlord.game.ui.Player;
import com.hurray.landlord.game.ui.Player.NullPlayer;
import com.hurray.landlord.game.ui.UiConstants;
import com.hurray.landlord.utils.LogUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.ref.WeakReference;

public class SelfCardsView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "SelfCardsView";

    private static final int sWidth = UiConstants.sScreenwidth;

    private static final int sHeight = UiConstants.sSelfCardHeight + UiConstants.sSelfCardHead;

    private DrawThread mDrawThread;

    private PlayerContext mPlayerContext;

    private Player mSelf = new NullPlayer();

    private TouchHandler mTouchHandler;

    private Paint mClearPaint;

    public SelfCardsView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        getHolder().addCallback(this);
        setFocusable(true);

        mClearPaint = new Paint();
        mClearPaint.setAntiAlias(true);
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.d(TAG, "surfaceCreated");
        mDrawThread = new DrawThread(holder, mClearPaint);
        mDrawThread.setPlayer(mSelf);
        mDrawThread.setPlayerContext(mPlayerContext);

        mTouchHandler = new TouchHandler();
        mTouchHandler.setPlayerContext(mPlayerContext);
        mTouchHandler.setDrawThread(mDrawThread);

        mDrawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtil.d(TAG, "surfaceChanged format=" + format + " w=" + width + " h=" + height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtil.d(TAG, "surfaceDestroyed");
        mDrawThread.close();
        LogUtil.d(TAG, mDrawThread.getName() + " mDrawThread call close");
        mDrawThread.wakeUp();
        LogUtil.d(TAG, mDrawThread.getName() + " mDrawThread call wakeUp");

        boolean retry = true;
        while (retry) {
            try {
                mDrawThread.join();
                retry = false;
                LogUtil.d(TAG, mDrawThread.getName() + " mDrawThread died ok");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mDrawThread.setPlayer(null);

        mDrawThread = null;

        mTouchHandler.setPlayerContext(null);
        mTouchHandler.setDrawThread(null);
        mTouchHandler = null;
    }

    public void setPlayerContext(PlayerContext playerContext) {
        mPlayerContext = playerContext;

        if (mDrawThread != null) {
            mDrawThread.setPlayerContext(mPlayerContext);
        }

        if (mTouchHandler != null) {
            mTouchHandler.setPlayerContext(mPlayerContext);
        }
    }

    public void setPlayer(Player self) {
        if (self != null) {
            mSelf = self;
        } else {
            mSelf = new NullPlayer();
        }

        if (mDrawThread != null) {
            mDrawThread.setPlayer(mSelf);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(sWidth, sHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDrawThread != null && mTouchHandler != null) {
            mTouchHandler.onTouch(event);
            refreshView();
        }

        return true;
    }

    public void refreshView() {
        if (mDrawThread != null) {
            mDrawThread.wakeUp();
            requestLayout();
        }
    }

    private static class TouchHandler {

        private int mTouchStartX = 0;

        private int mTouchStartY = 0;

        private boolean mIsMove = false;

        private PlayerContext mPlayerContext;

        private DrawThread mDrawThread;

        public void setPlayerContext(PlayerContext playerContext) {
            mPlayerContext = playerContext;
        }

        public void setDrawThread(DrawThread drawThread) {
            mDrawThread = drawThread;
        }

        // 当玩家自己操作时，触摸屏事件的处理
        public void onTouch(MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    mTouchStartX = (int) event.getX();
                    mTouchStartY = (int) event.getY();
                }
                    break;
                case MotionEvent.ACTION_MOVE: {
                    int currX = (int) event.getX();
                    int currY = (int) event.getY();
                    mIsMove = true;
                    handleMoveAction(currX, currY);
                }
                    break;
                case MotionEvent.ACTION_UP: {
                    int currX = (int) event.getX();
                    int currY = (int) event.getY();

                    if (mIsMove) {
                        mIsMove = false;
                        handleMoveAction(currX, currY);
                        handleUpActionWithMove();
                    } else {
                        handleUpActionWithoutMove(currX, currY);
                    }
                }
                    break;
            }
        }

        private void handleMoveAction(int currX, int currY) {
            int[] selfCardIds = mPlayerContext.getSelfCardIds();
            boolean[] selects = mPlayerContext.getSelfCardSelects();
            boolean[] touches = mPlayerContext.getSelfCardTouches();
            if (selfCardIds != null && selects != null && touches != null) {
                int begIdx = -1;
                int endIdx = -1;
                for (int i = selfCardIds.length - 1; i >= 0; i--) {
                    if (CardUtil.inRect(mTouchStartX, mTouchStartY,
                            mDrawThread.left() + i * UiConstants.sSelfCardsGap,
                            UiConstants.sSelfCardHead
                                    - (selects[i] ? UiConstants.sSelfCardHead : 0),
                                UiConstants.sSelfCardWidth, UiConstants.sSelfCardHeight)) {
                        begIdx = i;
                        break;
                    }
                }

                for (int i = selfCardIds.length - 1; i >= 0; i--) {
                    if (CardUtil.inRect(currX, currY,
                            mDrawThread.left() + i * UiConstants.sSelfCardsGap,
                            UiConstants.sSelfCardHead
                                    - (selects[i] ? UiConstants.sSelfCardHead : 0),
                                UiConstants.sSelfCardWidth, UiConstants.sSelfCardHeight)) {
                        endIdx = i;
                        break;
                    }
                }

                if (begIdx != -1 && endIdx != -1) {
                    if (begIdx > endIdx) {
                        int tmp = begIdx;
                        begIdx = endIdx;
                        endIdx = tmp;
                    }
                    for (int i = selfCardIds.length - 1; i >= 0; i--) {
                        touches[i] = (i >= begIdx && i <= endIdx);
                    }
                }

            }
        }

        private void handleUpActionWithoutMove(int currX, int currY) {
            int[] selfCardIds = mPlayerContext.getSelfCardIds();
            boolean[] selects = mPlayerContext.getSelfCardSelects();
            if (selfCardIds != null && selects != null) {
                for (int i = selfCardIds.length - 1; i >= 0; i--) {
                    if (CardUtil.inRect(currX, currY,
                            mDrawThread.left() + i * UiConstants.sSelfCardsGap,
                            UiConstants.sSelfCardHead
                                    - (selects[i] ? UiConstants.sSelfCardHead : 0),
                            UiConstants.sSelfCardWidth, UiConstants.sSelfCardHeight)) {
                        selects[i] = !selects[i];
                        break;
                    }
                }
            }
        }

        private void handleUpActionWithMove() {
            boolean[] selects = mPlayerContext.getSelfCardSelects();
            boolean[] touches = mPlayerContext.getSelfCardTouches();
            if (selects != null && touches != null) {
                for (int i = 0; i < touches.length; i++) {
                    if (touches[i]) {
                        touches[i] = false;
                        selects[i] = !selects[i];
                    }
                }
            }
        }
    }

    private static class DrawThread extends Thread {

        private WeakReference<SurfaceHolder> mHolder;

        private PlayerContext mPlayerContext;

        private Player mSelf;

        private int mLeft;

        private boolean mLoop;

        private boolean mIsWaiting;

        private Paint mClearPaint;

        public DrawThread(SurfaceHolder holder, Paint p) {
            mHolder = new WeakReference<SurfaceHolder>(holder);
            mClearPaint = p;
            mLoop = true;
        }

        public void setPlayerContext(PlayerContext playerContext) {
            mPlayerContext = playerContext;
        }

        public void setPlayer(Player self) {
            mSelf = self;
        }

        public int left() {
            return mLeft;
        }

        public void close() {
            mLoop = false;
        }

        @Override
        public void run() {

            while (mLoop) {
                synchronized (this) {
                    doDraw(mHolder.get());
                    waitEvent();
                }
            }
        }

        public void wakeUp() {
            synchronized (this) {
                if (mIsWaiting) {
                    notify();
                }
            }
        }

        private void waitEvent() {
            try {
                mIsWaiting = true;
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mIsWaiting = false;
            }

        }

        private void doDraw(SurfaceHolder h) {
            if (h != null) {
                Canvas canvas = h.lockCanvas(null);
                if (canvas != null) {
                    onDraw(canvas);
                    h.unlockCanvasAndPost(canvas);
                }
            }
        }

        private void onDraw(Canvas canvas) {
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                    | Paint.FILTER_BITMAP_FLAG));
            // 清屏
            canvas.drawRect(0, 0, sWidth, sHeight, mClearPaint);

            // debug 标记
            // canvas.drawRect(0, 0, sWidth, sHeight, UiConstants.sPaint);

            // 绘制玩家手中的牌
            paintSelfCards(canvas);
        }

        private void paintSelfCards(Canvas canvas) {
            LogUtil.d(TAG, getName() + "before paintSelfCards");
            if (mPlayerContext == null || !mPlayerContext.isPlayerSelf(mSelf.getPlayerId())) {
                return;
            }

            int[] selfCardIds = mPlayerContext.getSelfCardIds();
            boolean[] selects = mPlayerContext.getSelfCardSelects();
            boolean[] touches = mPlayerContext.getSelfCardTouches();
            if (selfCardIds == null || selects == null || touches == null) {
                return;
            }

            mLeft = (UiConstants.sScreenwidth - (UiConstants.sSelfCardsGap
                    * (selfCardIds.length - 1) + UiConstants.sSelfCardWidth)) / 2;

            for (int i = 0; i < selfCardIds.length; i++) {
                // 绘制当前玩家
                int top = 0;
                if (!selects[i]) {
                    top = UiConstants.sSelfCardHead;
                }
                int ll = mLeft + i * UiConstants.sSelfCardsGap;
                BitmapUtil.drawBitmap(canvas, CardsBitmap.getCardBitmap(selfCardIds[i]), ll, top);

                if (touches[i]) {
                    BitmapUtil.drawBitmap(canvas, CardsBitmap.getCardBitmap(54), ll, top);
                }
            }
            LogUtil.d(TAG, getName() + "after paintSelfCards");
        }
    }
}

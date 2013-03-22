
package com.hurray.landlord.view;

import com.hurray.landlord.Constants;
import com.hurray.landlord.bitmaps.BitmapUtil;
import com.hurray.landlord.bitmaps.CardsBitmap;
import com.hurray.landlord.game.ui.Player;
import com.hurray.landlord.game.ui.Player.NullPlayer;
import com.hurray.landlord.game.ui.UiConstants;
import com.hurray.landlord.utils.LogUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.ref.WeakReference;

public class ShowCardsView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "ShowCardsView";

    private int mWidth;

    private int mHeight;

    private Player mPlayer = new NullPlayer();

    private boolean mWrapCards = true;

    private DrawThread mDrawThread;

    private String mName;
    
    private Paint mClearPaint;

    public ShowCardsView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        getHolder().addCallback(this);
        
        mClearPaint = new Paint();
        mClearPaint.setAntiAlias(true);
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.d(TAG, mName + " surfaceCreated");
        mDrawThread = new DrawThread(holder, mClearPaint, mWidth, mHeight);
        mDrawThread.setPlayer(mPlayer);
        mDrawThread.setWrapCards(mWrapCards);
        mDrawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtil.d(TAG, mName + " surfaceChanged format=" + format + " w=" + width + " h=" + height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtil.d(TAG, mName + " surfaceDestroyed");
        mDrawThread.close();
        LogUtil.d(TAG, mName + mDrawThread.getName() + " mDrawThread call close");
        mDrawThread.wakeUp();
        LogUtil.d(TAG, mName + mDrawThread.getName() + " mDrawThread call wakeUp");

        boolean retry = true;
        while (retry) {
            try {
                mDrawThread.join();
                retry = false;
                LogUtil.d(TAG, mName + mDrawThread.getName() + " mDrawThread died ok");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mDrawThread.setPlayer(null);

        mDrawThread = null;
    }

    public void refreshView() {
        if (mDrawThread != null) {
            mDrawThread.wakeUp();
            LogUtil.d(TAG, mName + "mDrawThread call wakeUp from refresh");
            requestLayout();
        }
    }

    public void setName(String name) {
        mName = name;
    }

    public void setPlayer(Player p) {
        if (p != null) {
            mPlayer = p;
        } else {
            mPlayer = new NullPlayer();
        }

        if (mDrawThread != null)
            mDrawThread.setPlayer(mPlayer);
    }

    public void setWrapCards(boolean wrapCards) {
        mWrapCards = wrapCards;
        calWidthHeight();

        if (mDrawThread != null) {
            mDrawThread.setWrapCards(mWrapCards);
            mDrawThread.setSize(mWidth, mHeight);
        }
    }

    private void calWidthHeight() {
        if (mWrapCards) {
            mWidth = UiConstants.sShowCardsGap * (UiConstants.MAX_COL - 1)
                    + UiConstants.sShowCardWidth;
            mHeight = UiConstants.sShowCardHeight * (UiConstants.MAX_ROW + 1) / 2;
        } else {
            mWidth = UiConstants.sShowCardsGap * (Constants.LANDLORD_CARDS_NUM - 1)
                    + UiConstants.sShowCardWidth;
            mHeight = UiConstants.sShowCardHeight;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    private static class DrawThread extends Thread {

        private WeakReference<SurfaceHolder> mHolder;

        private Player mPlayer;

        private boolean mWrapCards;

        private boolean mLoop;

        private boolean mIsWaiting;

        private Paint mClearPaint;

        private int mWidth;

        private int mHeight;

        public DrawThread(SurfaceHolder holder, Paint p, int w, int h) {
            mHolder = new WeakReference<SurfaceHolder>(holder);
            mLoop = true;

            mClearPaint = p;

            mWidth = w;
            mHeight = h;
        }

        public void setWrapCards(boolean wrapCards) {
            mWrapCards = wrapCards;
        }

        public void setSize(int w, int h) {
            mWidth = w;
            mHeight = h;
        }

        public void setPlayer(Player player) {
            mPlayer = player;
        }

        public void close() {
            mLoop = false;
        }

        @Override
        public void run() {

            LogUtil.d(TAG, getName() + "begin run");
            while (mLoop) {

                synchronized (this) {
                    LogUtil.d(TAG, getName() + "before doDraw");
                    doDraw(mHolder.get());
                    LogUtil.d(TAG, getName() + "after doDraw");

                    LogUtil.d(TAG, getName() + "before waitEvent");
                    waitEvent();
                    LogUtil.d(TAG, getName() + "after waitEvent");
                }
            }
            
            mClearPaint = null;
            mPlayer = null;
            
            LogUtil.d(TAG, getName() + "end run");

        }

        public void wakeUp() {
            synchronized (this) {
                if (mIsWaiting) {
                    notify();
                }
            }
        }

        private void doDraw(SurfaceHolder h) {
            LogUtil.d(TAG, getName() + "doDraw");
            if (h != null) {
                Canvas canvas = h.lockCanvas(null);
                if (canvas != null) {
                    onDraw(canvas);
                    h.unlockCanvasAndPost(canvas);
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

        private void onDraw(Canvas canvas) {
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                    | Paint.FILTER_BITMAP_FLAG));

            // canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            // 清屏
            canvas.drawRect(0, 0, mWidth, mHeight, mClearPaint);

            // // debug 标记
            // canvas.drawRect(0, 0, sWidth, sHeight, UiConstants.sPaint);

            // 绘制玩家手中的牌
            paintPrevShowCards(canvas);
        }

        private void paintPrevShowCards(Canvas canvas) {
            LogUtil.d(TAG, getName() + "-----begin paintPrevShowCards-----");

            int[] preShowCardIds = mPlayer.getPreShowCardIds();
            int cardsNum = preShowCardIds.length;
            // CardUtil.printCards(getName(), preShowCardIds);
            if (cardsNum > 0) {

                int w = 0;

                if (mWrapCards) {
                    int maxCol = 0;
                    if (cardsNum < UiConstants.MAX_COL) {
                        maxCol = cardsNum;
                    } else {
                        maxCol = UiConstants.MAX_COL;
                    }

                    w = UiConstants.sShowCardsGap * (maxCol - 1) + UiConstants.sShowCardWidth;
                } else {
                    w = UiConstants.sShowCardsGap * (cardsNum - 1) + UiConstants.sShowCardWidth;
                }

                int left = (mWidth - w) / 2;
                if (mWrapCards) {
                    int rowNum = (cardsNum - 1) / UiConstants.MAX_COL + 1;
                    int top = mHeight - (rowNum + 1) * UiConstants.sShowCardHeight / 2;
                    if (top < 0)
                        top = 0;
                    for (int i = 0; i < cardsNum; i++) {
                        int rowIdx = i / UiConstants.MAX_COL;
                        int colIdx = i % UiConstants.MAX_COL;
                        int ll = left + colIdx * UiConstants.sShowCardsGap;
                        int tt = top + rowIdx * UiConstants.sShowCardHeight / 2;
                        // LogUtil.d(TAG, getName() +
                        // "before wrapCards getSmallCardBitmap");
                        Bitmap b = CardsBitmap.getSmallCardBitmap(preShowCardIds[i]);
                        BitmapUtil.drawBitmap(canvas, b, ll, tt);
                        // LogUtil.d(TAG, getName() +
                        // "after wrapCards getSmallCardBitmap");
                    }
                } else {
                    for (int i = 0; i < cardsNum; i++) {
                        int ll = left + i * UiConstants.sShowCardsGap;
                        // LogUtil.d(TAG, getName() +
                        // "before getSmallCardBitmap");
                        Bitmap b = CardsBitmap.getSmallCardBitmap(preShowCardIds[i]);
                        BitmapUtil.drawBitmap(canvas, b, ll, 0);
                        // LogUtil.d(TAG, getName() +
                        // "after getSmallCardBitmap");
                    }
                }
            }

            LogUtil.d(TAG, getName() + "-----end paintPrevShowCards-----");

        }

    }

}

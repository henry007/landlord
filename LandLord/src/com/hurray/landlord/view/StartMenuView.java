
package com.hurray.landlord.view;

import com.hurray.landlord.R;
import com.hurray.landlord.bitmaps.SoftBitmapRef;
import com.hurray.landlord.utils.LogUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class StartMenuView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "StartMenuView";

    public static final int MENU_ABOUT = 0;

    public static final int MENU_EXIT = 1;

    public static final int MENU_LOCAL = 2;

    public static final int MENU_NET = 3;

    public static final int MENU_HELP = 4;

    public static final int MENU_NUM = 5;

    private static final int POS_NUM = 5;

    private static final int FRAME_NUM = 3;

    private static final int FRAME_INTERVAL = 20;

    private static final int FLING_VELOCITY_UNIT = 300;

    private int mCurrMenuIndex;

    private boolean mIsShowingAnimation = false;

    private DrawThread mDrawThread;

    private Timer mTimer;

    private RunAnim mRunAnim;

    private GestureDetector mGestureDetector;

    private OnStartMenuListener mListener;

    public StartMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        getHolder().addCallback(this);
        setFocusable(true);

        mCurrMenuIndex = MENU_NET;

        initGestureDetector();

        LogUtil.d(TAG, "StartMenuView");
    }

    public int getMenuIndex() {
        return mCurrMenuIndex;
    }

    public void setMenuIndex(int menuIndex) {
        mCurrMenuIndex = menuIndex;
    }

    public void setOnStartMenuListener(OnStartMenuListener listener) {
        mListener = listener;
    }

    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(getContext(), new OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                LogUtil.d(TAG, "GestureDetector onDown x=" + e.getX() + " y=" + e.getY());
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                LogUtil.d(TAG, "GestureDetector onFling velocityX=" + velocityX + " velocityY="
                        + velocityY);
                int velocity = (int) velocityX / FLING_VELOCITY_UNIT;
                boolean isLeftToRight = true;
                if (velocityX < 0) {
                    isLeftToRight = false;
                }
                int skipMenuNum = Math.abs(velocity);
                if (skipMenuNum > 0) {
                    startAnimation(skipMenuNum, isLeftToRight);
                }

                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                LogUtil.d(TAG, "GestureDetector onLongPress x=" + e.getX() + " y=" + e.getY());

            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2,
                    float distanceX,
                    float distanceY) {
                LogUtil.d(TAG, "GestureDetector onScroll distanceX=" + distanceX + " distanceY="
                        + distanceY);
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                LogUtil.d(TAG, "GestureDetector onShowPress x=" + e.getX() + " y=" + e.getY());
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                LogUtil.d(TAG, "GestureDetector onSingleTapUp x=" + e.getX() + " y=" + e.getY());
                if (mDrawThread != null) {
                    int posIdx = mDrawThread.onSingleTapMenuPos(e.getX(), e.getY());
                    if (posIdx != 2) {
                        int skipMenuNum = posIdx - 2;
                        boolean isLeftToRight = true;
                        if (skipMenuNum > 0) {
                            isLeftToRight = false;
                        }
                        skipMenuNum = Math.abs(skipMenuNum);

                        startAnimation(skipMenuNum, isLeftToRight);
                    }
                }

                return true;
            }

        });
    }

    private boolean mIsLeftToRight;

    private int mSkipMenuNum;

    private int mFrameIndex;

    private int mSkipMenuIndex;

    private void resetAnim() {
        mFrameIndex = 0;
        mSkipMenuIndex = 0;
    }

    private void setAnimDirection(boolean isLeftToRight) {
        mIsLeftToRight = isLeftToRight;
    }

    private void setAnimSkipMenuNum(int skipMenuNum) {
        mSkipMenuNum = skipMenuNum;
    }

    private class RunAnim extends TimerTask {

        @Override
        public void run() {

            if (mDrawThread == null)
                return;

            mDrawThread.setCurrMenuIndex(getMenuIndex());
            mDrawThread.setFrameIndex(mFrameIndex);
            mDrawThread.wakeUp();

            boolean isAnimFinished = false;

            if (mIsLeftToRight) {

                if (mSkipMenuIndex >= 0 && mSkipMenuIndex < mSkipMenuNum - 2) {// 起点和中间
                    schedule(FRAME_INTERVAL);
                } else if (mSkipMenuIndex == mSkipMenuNum - 1) { // 最后
                    schedule(3 * FRAME_INTERVAL);
                } else if (mSkipMenuIndex == mSkipMenuNum - 2) { // 倒数第一
                    schedule(2 * FRAME_INTERVAL);
                } else if (mSkipMenuIndex == mSkipMenuNum && mFrameIndex == 0) { // 最后一帧
                    isAnimFinished = true;
                }

                if (!isAnimFinished) {
                    mFrameIndex++;
                    if (mFrameIndex == FRAME_NUM) {
                        mSkipMenuIndex++;
                        mFrameIndex = 0;
                        increaseMenuIndex();
                    }
                } else {
                    mIsShowingAnimation = false;
                    mDrawThread.freeMemory();
                }

            } else {

                if (mSkipMenuIndex > 0 && mSkipMenuIndex < mSkipMenuNum - 1) {// 中间
                    schedule(FRAME_INTERVAL);
                } else if (mSkipMenuIndex == mSkipMenuNum) { // 最后
                    if (mFrameIndex == 0) {// 最后一帧
                        isAnimFinished = true;
                    } else {
                        schedule(3 * FRAME_INTERVAL);
                    }
                } else if (mSkipMenuIndex == mSkipMenuNum - 1) { // 　倒数第二
                    schedule(2 * FRAME_INTERVAL);
                } else if (mSkipMenuIndex == 0 && mFrameIndex == 0) { // 起始一帧
                    schedule(FRAME_INTERVAL);
                }

                if (!isAnimFinished) {
                    mFrameIndex--;
                    if (mFrameIndex < 0) {
                        mSkipMenuIndex++;
                        mFrameIndex = FRAME_NUM - 1;
                        decreaseMenuIndex();
                    }
                } else {
                    mIsShowingAnimation = false;
                    mDrawThread.freeMemory();
                }
            }

        }

    }

    private synchronized void startAnimation(int skipMenuNum, boolean isLeftToRight) {
        if (mIsShowingAnimation) {
            LogUtil.d(TAG, "startAnimation but isShowingAnimation");
            return;
        }
        mIsShowingAnimation = true;
        LogUtil.d(TAG, "startAnimation skipMenuNum=" + skipMenuNum + " isLeftToRight="
                + isLeftToRight);

        resetAnim();
        setAnimDirection(isLeftToRight);
        setAnimSkipMenuNum(skipMenuNum);

        schedule(0);
    }

    private void schedule(long delay) {
        if (mTimer != null)
            mTimer.cancel();
        if (mRunAnim != null)
            mRunAnim.cancel();

        mRunAnim = new RunAnim();
        mTimer = new Timer();
        mTimer.schedule(mRunAnim, delay);
    }

    private void increaseMenuIndex() {
        mCurrMenuIndex++;
        mCurrMenuIndex = mCurrMenuIndex % MENU_NUM;
    }

    private void decreaseMenuIndex() {
        mCurrMenuIndex--;
        mCurrMenuIndex = (mCurrMenuIndex + MENU_NUM) % MENU_NUM;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.d(TAG, "surfaceCreated");

        LogUtil.d(TAG, "w=" + this.getWidth());
        LogUtil.d(TAG, "h=" + this.getHeight());

        LogUtil.d(TAG, "mCurrMenuIndex=" + mCurrMenuIndex);

        mDrawThread = new DrawThread(holder, getWidth(), getHeight(),this);
        mDrawThread.setCurrMenuIndex(mCurrMenuIndex);
        mDrawThread.setFrameIndex(0);
        mDrawThread.setOnStartMenuListener(mListener);
        mDrawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtil.d(TAG, "surfaceChanged format=" + format + " w=" + width + " h=" + height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtil.d(TAG, "surfaceDestroyed");

        mCurrMenuIndex = mDrawThread.getCurrMenuIndex();
        mDrawThread.close();
        mDrawThread.wakeUp();

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

        mDrawThread = null;
    }

    private static class DrawThread extends Thread {

        private static final boolean MENU_ODD = true;

        private static final int MENU_HALF_SIZE = MENU_NUM / 2;

        private WeakReference<SurfaceHolder> mHolder;

        private OnStartMenuListener mListener;

        private boolean mLoop;

        private int mCurrMenuIndex;

        private int mFrameIndex;

        private Paint mPaint;

        private Paint mClearPaint;

        private boolean mIsWaiting;

        private PosInfoPool mPosInfoPool;

        private MenuBitmap mMenuBitmap;
        
        private StartMenuView view;

        public DrawThread(SurfaceHolder holder, int w, int h,StartMenuView view) {
            mLoop = true;
            mIsWaiting = false;
            mHolder = new WeakReference<SurfaceHolder>(holder);
            initPaint();
            initMenu(w, h);
            this.view = view;
        }

        public void setCurrMenuIndex(int currMenuIndex) {
            // LogUtil.d(TAG, "DrawThread setCurrMenuIndex = " + currMenuIndex);
            mCurrMenuIndex = currMenuIndex;
        }

        public void setFrameIndex(int frameIndex) {
            // LogUtil.d(TAG, "DrawThread setFrameIndex = " + frameIndex);
            mFrameIndex = frameIndex;
        }

        private void initPaint() {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);

            mClearPaint = new Paint();
            mClearPaint.setAntiAlias(true);
            mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }

        private void initMenu(int w, int h) {
            mMenuBitmap = new MenuBitmap();
            int w0 = mMenuBitmap.getWidth();
            int h0 = mMenuBitmap.getHeight();

            mPosInfoPool = new PosInfoPool(w, h, w0, h0);
        }

        public int getCurrMenuIndex() {
            return mCurrMenuIndex;
        }

        private static class MenuBitmap {

            private ArrayList<SoftBitmapRef> mBitmaps = new ArrayList<SoftBitmapRef>(MENU_NUM);

            public MenuBitmap() {
                initBitmaps();
            }

            public int getWidth() {
                return mBitmaps.get(0).getBitmap().getWidth();
            }

            public int getHeight() {
                return mBitmaps.get(0).getBitmap().getHeight();
            }

            public void clear() {
                if (mBitmaps != null) {
                    Iterator<SoftBitmapRef> it = mBitmaps.iterator();
                    while (it.hasNext()) {
                        it.next().clear();
                    }
                    mBitmaps.clear();
                    mBitmaps = null;
                }
            }

            public void freeMemory() {
                if (mBitmaps != null) {
                    Iterator<SoftBitmapRef> it = mBitmaps.iterator();
                    while (it.hasNext()) {
                        it.next().freeMemory();
                    }
                }
            }

            // 依赖顺序
            private void initBitmaps() {
                mBitmaps.add(new SoftBitmapRef(R.drawable.start_about));
                mBitmaps.add(new SoftBitmapRef(R.drawable.start_exit));
                mBitmaps.add(new SoftBitmapRef(R.drawable.start_local));
                mBitmaps.add(new SoftBitmapRef(R.drawable.start_net));
                mBitmaps.add(new SoftBitmapRef(R.drawable.start_help));
            }

            private Bitmap getBitmap(int currIndex, int posIndex) {
                return mBitmaps.get((currIndex + 2 - posIndex + MENU_NUM) % MENU_NUM).getBitmap();
            }
        }

        private void doDraw(SurfaceHolder h) {
            if (h != null) {
                Canvas c = h.lockCanvas(null);
                if (c != null) {
                    c.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                            | Paint.FILTER_BITMAP_FLAG));
                    // draw menu
                    onDrawFrame(c);

                    h.unlockCanvasAndPost(c);
                }
            }
        }

        private void onDrawFrame(Canvas c) {
            try {
				c.drawRect(0, 0, mPosInfoPool.getViewWidth(),
						mPosInfoPool.getViewHeight(), mClearPaint);
				for (int j = 0; j < MENU_HALF_SIZE; j++) {
					int posIndex = j;
					onDrawMenu(c, posIndex);

					posIndex = MENU_NUM - j - 1;
					onDrawMenu(c, posIndex);
				}
				if (MENU_ODD) {
					int posIndex = MENU_HALF_SIZE;
					onDrawMenu(c, posIndex);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

        }

        private void onDrawMenu(Canvas c, int posIndex) {
            PosInfo posInfo = mPosInfoPool.getPosInfo(mFrameIndex, posIndex);
            if (posInfo == null) {
                return;
            }

            Bitmap b = mMenuBitmap.getBitmap(mCurrMenuIndex, posIndex);

            RectF posRectF = posInfo.getRectF();
            Matrix posMatrix = posInfo.getMatrix();
            if (posMatrix != null) {
                int color = posInfo.getColor();

                c.save();
                c.setMatrix(posMatrix);
                
                if(b ==null||b.isRecycled()){
                    b = mMenuBitmap.getBitmap(mCurrMenuIndex, posIndex);
                }
                c.drawBitmap(b, posRectF.left, posRectF.top, mPaint);
                c.clipRect(posRectF);
                c.drawColor(color);
                c.restore();
            } else {
                c.drawBitmap(b, posRectF.left, posRectF.top, mPaint);
            }

        }

        public void freeMemory() {
            if (mMenuBitmap != null)
                mMenuBitmap.freeMemory();
        }

        @Override
        public void run() {

            while (mLoop) {
                synchronized (mMenuBitmap) {
                    doDraw(mHolder.get());
                    waitEvent();
                }
            }

            if (mMenuBitmap != null)
                mMenuBitmap.clear();

            if (mPosInfoPool != null)
                mPosInfoPool.clear();

            LogUtil.d(TAG, this.getName() + ": i am over");
        }

        private void waitEvent() {

            try {
                mIsWaiting = true;
                mMenuBitmap.wait();
            } catch (InterruptedException e) {
                close();
                mMenuBitmap.clear();
                e.printStackTrace();
            } finally {
                mIsWaiting = false;
            }

        }

        public void wakeUp() {
            synchronized (mMenuBitmap) {
                if (mIsWaiting) {
                    mMenuBitmap.notify();
                }
            }

        }
        PerformClick mPerformClick;
        public int onSingleTapMenuPos(float x, float y) {
            LogUtil.d(TAG, "thread onSingleTapMenuPos x=" + x + " y=" + y);

            for (int posIndex = 0; posIndex < POS_NUM; posIndex++) {
                if (onTouch(x, y, posIndex)) {
                    if (posIndex == 2) {
                        if(mPerformClick == null){
                            mPerformClick = new PerformClick();
                        }
                        if(!view.post(mPerformClick)){
                            performClick();
                        }
                    }

                    LogUtil.d(TAG, "onSingleTapMenuPos posIndex = " + posIndex);
                    return posIndex;
                }
            }

            return -1;
        }
        public class PerformClick implements Runnable{

            @Override
            public void run() {
              performClick();
            }
        }

        private void performClick() {
            if (mListener != null)
                mListener.onMenuItemSelected(mCurrMenuIndex);
        }

        public boolean onTouch(float x, float y, int posIndex) {
            PosInfo posInfo = mPosInfoPool.getPosInfo(mFrameIndex, posIndex);
            if (posInfo == null) {
                return false;
            }

            Matrix posMatrix = posInfo.getMatrix();
            RectF posRectF = posInfo.getRectF();
            if (posMatrix == null) {
                return posRectF.contains(x, y);
            }

            Matrix matrix = new Matrix();
            if (posMatrix.invert(matrix)) {
                float[] pts = new float[] {
                        x, y
                };
                matrix.mapPoints(pts);
                return posRectF.contains(pts[0], pts[1]);
            }

            return false;
        }

        public void setOnStartMenuListener(OnStartMenuListener listener) {
            mListener = listener;
        }

        public void close() {
            mLoop = false;
        }

        private static class PosInfoPool {

            private int mViewWidth;

            private int mViewHeight;

            // data inside
            // frame 0, pos 0 1 2 3 4
            // frame 1, pos 0 1 2 3 4
            // frame 2, pos 0 1 2 3 4
            // frame 3, pos 0 1 2 3 4
            private ArrayList<PosInfo> mPosInfos = new ArrayList<PosInfo>(FRAME_NUM * POS_NUM);

            public PosInfoPool(int viewWidth, int viewHeight, int w0, int h0) {
                init(viewWidth, viewHeight, w0, h0);
            }

            public int getViewWidth() {
                return mViewWidth;
            }

            public int getViewHeight() {
                return mViewHeight;
            }

            public PosInfo getPosInfo(int frameIndex, int posIndex) {
                int idx = frameIndex * POS_NUM + posIndex;
                if (idx >= 0 && idx < mPosInfos.size()) {
                    return mPosInfos.get(idx);
                }

                return null;
            }

            public void clear() {
                mPosInfos.clear();
            }

            private void init(int w, int h, int w0, int h0) {
                mViewWidth = w;
                mViewHeight = h;

                float w1 = w0 * 0.75f;
                float h1 = h0 * 0.75f;
                float w2 = w0 * 0.50f;
                float h2 = h0 * 0.50f;

                // frame 0 left top
                float bpL2LeftFrame0 = (w - w0 - w1 * 2 - w2 * 2) / 2;
                float bpL2TopFrame0 = h - h2 / 2;

                float bpR2LeftFrame0 = w - bpL2LeftFrame0 - w2;
                float bpR2TopFrame0 = bpL2TopFrame0;

                float bpL1LeftFrame0 = (w - w0 - w1 * 5 / 2) / 2;
                float bpL1TopFrame0 = h - h1 * 9 / 10;

                float bpR1LeftFrame0 = w - bpL1LeftFrame0 - w1;
                float bpR1TopFrame0 = bpL1TopFrame0;

                float bpM0LeftFrame0 = (w - w0) / 2;
                float bpM0TopFrame0 = 0;

                // inc
                float scaleInc = 0.25f / FRAME_NUM;
                float rotate0to25Inc = 25.0f / FRAME_NUM;
                float rotate25to40Inc = 15.0f / FRAME_NUM;
                float rotate40to90Inc = 15.0f / FRAME_NUM;

                float x01Inc = (bpL1LeftFrame0 - bpL2LeftFrame0) / FRAME_NUM;
                float x12Inc = (bpM0LeftFrame0 - bpL1LeftFrame0) / FRAME_NUM;
                float x23Inc = (bpR1LeftFrame0 - bpM0LeftFrame0) / FRAME_NUM;
                float x34Inc = (bpR2LeftFrame0 - bpR1LeftFrame0) / FRAME_NUM;
                float x45Inc = (float) w0 / (3 * FRAME_NUM);

                float y01Inc = (bpL2TopFrame0 - bpL1TopFrame0) / FRAME_NUM;
                float y12Inc = (bpL1TopFrame0 - bpM0TopFrame0) / FRAME_NUM;
                float y23Inc = (bpR1TopFrame0 - bpM0TopFrame0) / FRAME_NUM;
                float y34Inc = (bpR2TopFrame0 - bpR1TopFrame0) / FRAME_NUM;
                float y45Inc = ((float) mViewHeight - bpR2TopFrame0) / FRAME_NUM;

                int alphaUnit = PosInfo.ALPHA / (2 * FRAME_NUM);

                for (int frameIndex = 0; frameIndex < FRAME_NUM; frameIndex++) {
                    for (int posIndex = 0; posIndex < POS_NUM; posIndex++) {

                        PosInfo pos = new PosInfo();
                        switch (posIndex) {
                            case 0: {
                                float bpL2Left = bpL2LeftFrame0 + frameIndex * x01Inc;
                                float bpL2Top = bpL2TopFrame0 - frameIndex * y01Inc;
                                RectF rf = new RectF(bpL2Left, bpL2Top, bpL2Left + w0, bpL2Top + h0);
                                Matrix m = getLeftMatrix(rf, 0.50f + frameIndex * scaleInc,
                                        -40.0f + frameIndex * rotate25to40Inc);

                                int alpha = PosInfo.ALPHA - frameIndex * alphaUnit;

                                pos.setRectF(rf);
                                pos.setMatrix(m);
                                pos.setAlpha(alpha);
                            }
                                break;
                            case 1: {
                                float bpL1Left = bpL1LeftFrame0 + frameIndex * x12Inc;
                                float bpL1Top = bpL1TopFrame0 - frameIndex * y12Inc;
                                RectF rf = new RectF(bpL1Left, bpL1Top, bpL1Left + w0, bpL1Top + h0);
                                Matrix m = getLeftMatrix(rf, 0.75f + frameIndex * scaleInc,
                                        -25.0f + frameIndex * rotate0to25Inc);

                                int alpha = PosInfo.ALPHA / 2 - frameIndex * alphaUnit;

                                pos.setRectF(rf);
                                pos.setMatrix(m);
                                pos.setAlpha(alpha);
                            }
                                break;
                            case 2: {
                                float bpM0Left = bpM0LeftFrame0 + frameIndex * x23Inc;
                                float bpM0Top = bpM0TopFrame0 + frameIndex * y23Inc;
                                RectF rf = new RectF(bpM0Left, bpM0Top, bpM0Left + w0, bpM0Top + h0);
                                Matrix m;
                                if (frameIndex != 0) {
                                    m = getRightMatrix(rf, 1.0f - frameIndex * scaleInc,
                                            0.0f + frameIndex * rotate0to25Inc);
                                } else {
                                    m = null;
                                }

                                int alpha = frameIndex * alphaUnit;

                                pos.setRectF(rf);
                                pos.setMatrix(m);
                                pos.setAlpha(alpha);
                            }
                                break;
                            case 3: {
                                float bpR1Left = bpR1LeftFrame0 + frameIndex * x34Inc;
                                float bpR1Top = bpR1TopFrame0 + frameIndex * y34Inc;
                                RectF rf = new RectF(bpR1Left, bpR1Top, bpR1Left + w0, bpR1Top + h0);
                                Matrix m = getRightMatrix(rf, 0.75f - frameIndex * scaleInc, 25.0f
                                        + frameIndex
                                        * rotate25to40Inc);

                                int alpha = PosInfo.ALPHA / 2 + frameIndex * alphaUnit;

                                pos.setRectF(rf);
                                pos.setMatrix(m);
                                pos.setAlpha(alpha);
                            }
                                break;
                            case 4: {
                                float bpR2Left = bpR2LeftFrame0 + frameIndex * x45Inc;
                                float bpR2Top = bpR2TopFrame0 + frameIndex * y45Inc;
                                RectF rf = new RectF(bpR2Left, bpR2Top, bpR2Left + w0, bpR2Top + h0);
                                Matrix m = getRightMatrix(rf, 0.50f - frameIndex * scaleInc, 40
                                        + frameIndex
                                        * rotate40to90Inc);

                                int alpha = PosInfo.ALPHA + frameIndex * alphaUnit;

                                pos.setRectF(rf);
                                pos.setMatrix(m);
                                pos.setAlpha(alpha);
                            }
                                break;
                        }

                        mPosInfos.add(pos);
                    }
                }

            }

            private Matrix getRightMatrix(RectF rect, float scale, float degree) {
                Matrix matrix = new Matrix();
                matrix.preScale(scale, scale, rect.left, rect.top);
                matrix.preRotate(degree, rect.right, rect.top);
                return matrix;
            }

            private Matrix getLeftMatrix(RectF rect, float scale, float degree) {
                Matrix matrix = new Matrix();
                matrix.preScale(scale, scale, rect.left, rect.top);
                matrix.preRotate(degree, rect.left, rect.top);
                return matrix;
            }

        }

        private static class PosInfo {

            public static final int ALPHA = 0xB8;

            private static final int MASK_COLOR = 0x001e014a;

            private RectF mRectF;

            private Matrix mMatrix;

            private int mColor;

            public int getColor() {
                return mColor;
            }

            public void setAlpha(int alpha) {
                mColor = (alpha << 24) | MASK_COLOR;
            }

            public RectF getRectF() {
                return mRectF;
            }

            public Matrix getMatrix() {
                return mMatrix;
            }

            public void setRectF(RectF rectF) {
                this.mRectF = rectF;
            }

            public void setMatrix(Matrix matrix) {
                this.mMatrix = matrix;
            }
        }

    }

    public interface OnStartMenuListener {
        public void onMenuItemSelected(int index);
    }

}

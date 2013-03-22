
package com.hurray.landlord.view;

import com.hurray.landlord.Constants;
import com.hurray.landlord.R;
import com.hurray.landlord.utils.BitmapRef;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.ToastUtil;
import com.hurray.lordserver.protocol.message.prize.SendPrizeResp.TurntableInfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class TurnTableView extends SurfaceView implements SurfaceHolder.Callback {

    private int lightFlag = 0;

    private boolean isSendedMsg = false;// 发送一次结束消息

    private boolean refreshLight = false; // 控制灯泡帧动画

    private int mTurnDegreeFlag = -1; // 判断旋转角度不变化

    private boolean runCanvasThread = false; // 设置canvas启动时是否线程等待

    private int secTurnRotate = -1;// ---控制红蓝灯刷新速率

    private int preTurnRotate = -1;// ---控制红蓝灯刷新速率

    private int isDiffValue = -1; // 两次变化的值不同，则改变刷新速率

    public boolean mDrawAgain = false; // 切出去了，回来重新转

    Object obj = new Object();

    private static final String TAG = "TurntableView";

    private int INTERVAL = 15;

    private static final int ANGULAR_VELOCITY = 180;

    private static final int FAN_NUM = 8;

    private static final int FAN_DEGREE = 45;

    private static final int FONT_SIZE_SP = 10; // sp

    private static final float BALL_FAN_DEGREE = 22.5f;

    private static final int[] GIFT_ICONS_RES = {

            R.drawable.icon_heart, R.drawable.icon_money, R.drawable.icon_heart,

            R.drawable.icon_money, R.drawable.icon_heart, R.drawable.icon_money,

            R.drawable.icon_heart, R.drawable.icon_money, R.drawable.icon_heart,

            R.drawable.icon_money, R.drawable.icon_heart, R.drawable.icon_money,

            R.drawable.icon_heart, R.drawable.icon_money
    };

    private static final String[] mText = {
            "x111", "x222", "x333", "x444", "x555", "x666", "x777", "x888"
    };

    private static final int RET_SUCCESS = 0;

    private static final int RET_RUNNING = 1;

    private int mTargetGiftIndex = 0;

    private BitmapRef mTurnRef = new BitmapRef();

    private BitmapRef mSelectRef = new BitmapRef();

    private BitmapRef mPointerRef = new BitmapRef();

    private BitmapRef mShadowRef = new BitmapRef();

    private BitmapRef mBlueBallRef = new BitmapRef();

    private BitmapRef mRedBallRef = new BitmapRef();

    private BitmapRef mBg = new BitmapRef();

    private ArrayList<TurntableAward> mOfflineIconInfos;

    private TurntableAward[] mOnlineIconInfos = null;

    private ArrayList<Integer> mTurntableAwardList;

    private Matrix mMatrixTurn = new Matrix();

    private Matrix mMatrixSelect = new Matrix();

    private Matrix mMatrixPointer = new Matrix();

    private float mCenX;

    private float mCenY;

    private int mTurnDegree = 0;

    private int mSelectDegree = 0;

    private int mTargetMinDegree;

    private int mTargetMaxDegree;

    private int mAngularVelocity = 0;

    private boolean mIsRotating = false;

    private Paint mGiftNumPaint = new Paint();

    private Paint mBitmapPaint = new Paint();

    private OnTurntableListener mListener = null;

    private Timer mTimer = null;

    private TimerTask mTimerTask;

    private Handler mHandler;

    private DrawThread mDrawThread = null;

    private SurfaceHolder mSth;

    private Canvas mCanvas;

    private Context mContext;

    public TurnTableView(Context context) {

        super(context);

        mContext = context;

        BitmapRef.init(context);

        mTurnRef.set(R.drawable.turntable);
        // mSelectRef.set(R.drawable.turn_select);
        mPointerRef.set(R.drawable.turn_pointer);

        mGiftNumPaint.setColor(context.getResources().getColor(android.R.color.darker_gray));
        float fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                FONT_SIZE_SP, getResources().getDisplayMetrics());
        mGiftNumPaint.setTextSize(fontSize);
        mGiftNumPaint.setAntiAlias(true);

        mBitmapPaint.setAntiAlias(true);

        mTurnDegree = FAN_DEGREE / 2;
        mSelectDegree = mTurnDegree % FAN_DEGREE - FAN_DEGREE;

        mCenX = mBg.get().getWidth() / 2.0f;
        mCenY = mBg.get().getHeight() / 2.0f;

        mSth = this.getHolder();

        // mSth.addCallback(this);

        mCanvas = new Canvas();

        mDrawThread = new DrawThread(mSth, mCanvas);
    }

    public TurnTableView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LogUtil.d("Demo", "Constructed");

        mContext = context;

        BitmapRef.init(context);

        mTurnRef.set(R.drawable.turntable);

        // mSelectRef.set(R.drawable.turn_select);

        mPointerRef.set(R.drawable.turn_pointer);

        mShadowRef.set(R.drawable.shadow);

        mBlueBallRef.set(R.drawable.blue_light);

        mRedBallRef.set(R.drawable.red_light);

        mBg.set(R.drawable.turntablebg);

        mGiftNumPaint.setColor(context.getResources().getColor(android.R.color.white));
        float fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                FONT_SIZE_SP, getResources().getDisplayMetrics());
        mGiftNumPaint.setTextSize(fontSize);
        mGiftNumPaint.setAntiAlias(true);

        mBitmapPaint.setAntiAlias(true);

        // mTurnDegree = FAN_DEGREE / 2;
        mSelectDegree = mTurnDegree % FAN_DEGREE - FAN_DEGREE;

        mCenX = mBg.get().getWidth() / 2.0f;
        mCenY = mBg.get().getHeight() / 2.0f;

        mSth = this.getHolder();

        mSth.addCallback(this);

        mCanvas = new Canvas();

        if (Constants.DEBUG_OFFLINE_TURNTABLE_SKIP_NETWORK) {

            setAwardList();

        }

        setZOrderOnTop(true);

        getHolder().setFormat(PixelFormat.TRANSLUCENT);

        // setAwardList();

        // mIsRotating = true;
        // //
        // mDrawThread.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        isSendedMsg = false;

        refreshLight = false;

        mDrawThread = new DrawThread(mSth, mCanvas);

        mIsRotating = true;

        mDrawThread.start();

        if (mDrawAgain) {

            startRotate(mListener, mTargetGiftIndex);

            mDrawAgain = false;
        }

        LogUtil.d("Demo", "SurfaceCreated::Thread==" + mDrawThread.getId());
    }

    public void startThread() {

        if (!mIsRotating) {

            mIsRotating = true;

        }

        if (!mDrawThread.isAlive()) {

            mDrawThread.start();

        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtil.d("Demo", "SurfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtil.d("Demo", "SurfaceDestroyed");
        
        if(mTimer!= null){
            
            mTimer.cancel();

        }

        mDrawThread.stopThread();

    }

    private void calDegree() {

        if (mTurnDegree != 0) {

            preTurnRotate = mTurnDegree;

        }
        int rotate = mAngularVelocity * 50 / 500;
        mTurnDegree += rotate;
        mTurnDegree %= 360;
        mSelectDegree = mTurnDegree % FAN_DEGREE - FAN_DEGREE;

        if (mTurnDegree != 0) {

            secTurnRotate = mTurnDegree;

        }

        if (preTurnRotate != -1 && secTurnRotate != -1) {

            if (!(isDiffValue == Math.abs(secTurnRotate - preTurnRotate))) {

                isDiffValue = Math.abs(secTurnRotate - preTurnRotate);

                if (isDiffValue <= 18) {

                    INTERVAL += (18 - Math.abs(secTurnRotate - preTurnRotate)) * 0.7;

                }

            }

        }

        LogUtil.d(TAG, "INTERVAL=>" + INTERVAL);
        LogUtil.d(TAG, "mTurnDegree=" + mTurnDegree);
    }

    private void calAngularVelocity() {
        if (mAngularVelocity > 130) {
            mAngularVelocity -= 4;
        } else if (mAngularVelocity > 60) {
            mAngularVelocity -= 4;
        }

        if (mAngularVelocity <= 60) {
            if (!isTargeted()) {

                // 一圈内必停

                int angleDiff = getAngleDiff();
                if (angleDiff < 10 && angleDiff >= 5) {
                    if (mAngularVelocity > 50) {
                        mAngularVelocity -= 5;
                    }
                } else if (angleDiff < 5 && angleDiff >= 3) {
                    if (mAngularVelocity > 40) {
                        mAngularVelocity -= 5;
                    }
                } else if (angleDiff < 3 && angleDiff >= 2) {
                    if (mAngularVelocity > 30) {
                        mAngularVelocity -= 5;
                    }
                } else if (angleDiff < 2 && angleDiff >= 1) {
                    if (mAngularVelocity > 28) {
                        mAngularVelocity -= 1;
                    }
                } else if (angleDiff < 1 && angleDiff >= 0) {
                    if (mAngularVelocity > 25) {
                        mAngularVelocity -= 2;
                    }
                }
            } else {

                // mTurnDegree = mTargetGiftIndex * FAN_DEGREE + FAN_DEGREE / 2;

                mTurnDegree = mTargetGiftIndex * FAN_DEGREE;

                mSelectDegree = mTurnDegree % FAN_DEGREE - FAN_DEGREE;
                // mHandler.sendEmptyMessage(1);

                if (mListener != null) {
                    synchronized (mListener) {
                        if (!isSendedMsg) {
                            mListener.onTurntableDone(0);
                            isSendedMsg = true;
                            System.out.println("issendedMsg:" + isSendedMsg);
                        }

                    }

                }

            }
        }

        LogUtil.d(TAG, "mAngularVelocity=" + mAngularVelocity);
    }

    private void notifyRotateDone(int result) {
        if (mListener != null) {
            mListener.onTurntableDone(result);
        }

        stopRotate();
    }

    private boolean isTargeted() {
        if (mTurnDegree > mTargetMinDegree && mTurnDegree < mTargetMaxDegree) {
            return true;
        }
        return false;
    }

    private int getAngleDiff() {
        int degreeDiff = mTargetMinDegree - mTurnDegree;
        while (degreeDiff < 0) {
            degreeDiff += 360;
        }
        int angleDiff = degreeDiff / FAN_DEGREE;
        LogUtil.d(TAG, "angleDiff=" + angleDiff);
        return angleDiff;
    }

    Paint p = new Paint();

    protected void Draw(Canvas canvas) {
        drawTurntable(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = mBg.get().getWidth();
        int h = mBg.get().getHeight();
        setMeasuredDimension(w, h);
    }

    private void drawTurntable(Canvas canvas) {

        canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);

        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG));

        canvas.drawBitmap(mBg.get(), 0, 0, mBitmapPaint);

        // mMatrixTurn.setRotate(mTurnDegree, mCenX, mCenY);

        mMatrixTurn.setTranslate(mCenX - mTurnRef.get().getWidth() / 2.0f, mCenY
                - mTurnRef.get().getHeight() / 2.0f);

        mMatrixTurn.postRotate(mTurnDegree, mCenX, mCenY);

        if (mTurnRef != null) {
            canvas.drawBitmap(mTurnRef.get(), mMatrixTurn, mBitmapPaint);
            // canvas.drawBitmap(mTurnRef.get(), mCenX-mTurnRef.get().getWidth()
            // / 2.0f, mCenY-mTurnRef.get().getHeight() / 2.0f, mBitmapPaint);
        }
        canvas.drawBitmap(mShadowRef.get(), mCenX - mShadowRef.get().getWidth() / 2.0f, mCenY
                - mShadowRef.get().getHeight() / 2.0f, mBitmapPaint);

        // mMatrixSelect.setRotate(mSelectDegree, mCenX, mCenY);
        // if (mSelectRef != null) {
        // canvas.drawBitmap(mSelectRef.get(), mMatrixSelect, mBitmapPaint);
        // }

        drawIcons(canvas);

        if (mPointerRef != null) {
            canvas.drawBitmap(mPointerRef.get(), mCenX - mPointerRef.get().getWidth() / 2, mCenY
                    - (mPointerRef.get().getHeight() / 3.0f) * 2, mBitmapPaint);
        }

        drawLights(canvas);
    }

    private void drawIcons(Canvas canvas) {

        if (Constants.DEBUG_OFFLINE_TURNTABLE_SKIP_NETWORK) {

            drawOfflineIcons(canvas);

        } else {

            drawOnlineIcons(canvas);

        }

    }

    private void drawOfflineIcons(Canvas canvas) {
        if (!mOfflineIconInfos.isEmpty()) {

            canvas.save();

            canvas.rotate(mTurnDegree, mCenX, mCenY);

            for (int i = 0; i < FAN_NUM; i++) {

                canvas.rotate(-FAN_DEGREE, mCenX, mCenY);

                TurntableAward info = mOfflineIconInfos.get(i);

                Bitmap b = info.mBitmapRef.get();

                if (b != null) {
                    canvas.drawBitmap(b, mCenX - b.getWidth() / 2,
                            mTurnRef.get().getHeight() / 4.5f,
                            mBitmapPaint);
                }

                float txtWidth = mGiftNumPaint.measureText(mText[i]);

                String name = info.mName;

                if (name != null) {

                    canvas.drawText(name, mCenX - txtWidth / 2,
                            mTurnRef.get().getHeight() / 6.5f + 10,
                            mGiftNumPaint);
                }

            }

            canvas.restore();

        }
    }

    private void drawOnlineIcons(Canvas canvas) {
        if (mOnlineIconInfos != null && mOnlineIconInfos.length == 8) {

            canvas.save();

            canvas.rotate(mTurnDegree, mCenX, mCenY);

            for (int i = 0; i < FAN_NUM; i++) {
                canvas.rotate(-FAN_DEGREE, mCenX, mCenY);

                Bitmap b = mOnlineIconInfos[i].mBitmapRef.get();

                if (b != null) {
                    canvas.drawBitmap(b, mCenX - b.getWidth() / 2,
                            mTurnRef.get().getHeight() / 3.5f,
                            mBitmapPaint);
                }
                float txtWidth = mGiftNumPaint.measureText(mText[i]);

                if (mText[i] != null) {

                    canvas.drawText(mText[i], mCenX - txtWidth / 2,
                            mTurnRef.get().getHeight() / 4f,
                            mGiftNumPaint);
                }

                // 数目
                // int num = mTurntableAwardList.get(i).mNum;
                // if (num > 0) {
                // String text = "x ";
                //
                // if (num >= 1000 && num < 10000) {
                // text += num / 1000 + "千";
                // } else if (num >= 10000) {
                // text += num / 10000 + "万";
                // } else {
                // text += num;
                // }
            }

            canvas.restore();
        }
    }

    public void drawLights(Canvas canvas) {
        canvas.save();

        if (lightFlag == 0)
        {
            for (int i = 0; i < 16; i++) {

                canvas.rotate(-BALL_FAN_DEGREE, mCenX, mCenY);

                if (i % 2 == 0)
                {
                    canvas.drawBitmap(mBlueBallRef.get(), mCenX - mBlueBallRef.get().getWidth()
                            / 2,
                            mCenY - mTurnRef.get().getHeight() / 2.0f
                                    - mBlueBallRef.get().getHeight() + 3,
                            mBitmapPaint);
                } else
                {
                    canvas.drawBitmap(mRedBallRef.get(), mCenX - mRedBallRef.get().getWidth()
                            / 2,
                            mCenY - mTurnRef.get().getHeight() / 2.0f
                                    - mBlueBallRef.get().getHeight() + 3,
                            mBitmapPaint);
                }
            }

            if (refreshLight) {

                lightFlag = 1;

            }
        }
        else {
            for (int i = 0; i < 16; i++) {

                canvas.rotate(-BALL_FAN_DEGREE, mCenX, mCenY);

                if (i % 2 == 0)
                {
                    canvas.drawBitmap(mRedBallRef.get(), mCenX - mRedBallRef.get().getWidth()
                            / 2,
                            mCenY - mTurnRef.get().getHeight() / 2.0f
                                    - mBlueBallRef.get().getHeight() + 3,
                            mBitmapPaint);
                } else
                {

                    canvas.drawBitmap(mBlueBallRef.get(), mCenX - mBlueBallRef.get().getWidth()
                            / 2,
                            mCenY - mTurnRef.get().getHeight() / 2.0f
                                    - mBlueBallRef.get().getHeight() + 3,
                            mBitmapPaint);
                }
            }
            if (refreshLight) {

                lightFlag = 0;
            }
        }
        canvas.restore();
    }

    public void setAwardList() {
        mOfflineIconInfos = new ArrayList<TurntableAward>();
        for (int i = 0; i < 8; i++)
        {
            TurntableAward t = new TurntableAward();

            t.mBitmapRef.set(GIFT_ICONS_RES[i]);

            t.mName = mText[i];

            mOfflineIconInfos.add(t);

        }

    }

    public void setAwardList(TurntableInfo[] infos) {

        mOnlineIconInfos = new TurntableAward[infos.length];

        if (infos != null) {

            for (int i = 0; i < infos.length; i++) {

                mText[i] = infos[i].goodsDesc; // 设置奖品名

                TurntableAward t = new TurntableAward();

                t.mBitmapRef.set(mContext.getResources().getIdentifier(
                        infos[i].iconId, "drawable", mContext.getPackageName()));

                // mTurntableAwardList3[i].mBitmapRef.set(mContext.getResources().getIdentifier(
                // infos[i].iconId, "drawable", mContext.getPackageName()));
                mOnlineIconInfos[i] = t;
            }
        }

    }

    public void setAwardList(ArrayList<Integer> awardList) {

        mTurntableAwardList = awardList;
        for (int i = 0; i < 8; i++) {
            mTurntableAwardList.add(GIFT_ICONS_RES[i]);
        }
        // mTurntableAwardList = new ArrayList<TurntableAward>();
        // for (int i = 0; i < awardList.size(); i++) {
        // TurntableAward gift = new TurntableAward();
        // AwardInfo info = awardList.get(i);
        //
        // gift.mId = info.mId;
        // gift.mBitmapRef.set(getResIdByAwardId((info.mId)));
        // gift.mNum = info.mNum;
        //
        // mTurntableAwardList.add(gift);
        // }
    }

    public void startRotate(OnTurntableListener listener, int targetGiftIndex) {
        // Log.d(TAG, "startRotate idx:" + targetGiftIndex);
        // if (mIsRotating || targetGiftIndex < 0) {
        // return;
        // }
        // mIsRotating = true;
        // mListener = listener;
        if (isShown()) {

            if (this.mListener == null) {
                this.mListener = listener;
            }
            refreshLight = true;

            runCanvasThread = true;

            mTargetGiftIndex = targetGiftIndex;

            // mTargetMinDegree = mTargetGiftIndex * FAN_DEGREE + FAN_DEGREE ;

            mTargetMinDegree = mTargetGiftIndex * FAN_DEGREE - 2;

            mTargetMaxDegree = mTargetMinDegree + 5;
            
            if(!mDrawAgain){
                
                mAngularVelocity = ANGULAR_VELOCITY;

            }

            // mIsRotating = true;
            //
            // mDrawThread.start();

            mTimer = new Timer();
            //
            mTimerTask = new TimerTask() {

                @Override
                public void run() {

                    synchronized (obj) {

                        calDegree();

                        calAngularVelocity();

                        isRefreshLight();

                        obj.notify();

                        try {
                            obj.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    // mHandler.sendEmptyMessage(0);
                }

                private void isRefreshLight() {

                    if (mTurnDegreeFlag == mTurnDegree) {

                        refreshLight = false;

                    } else {

                        mTurnDegreeFlag = mTurnDegree;

                    }
                }
            };
            mTimer.schedule(mTimerTask, INTERVAL, 10);

        }

    }

    public void stopRotate() {
        if (mIsRotating) {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }

            mTimerTask = null;

            refreshLight = false;

        }
    }

    /**
     * 1-9 1变速齿轮 2西瓜炸弹 3活力汽水 4招财玉猫 5金币少 6 金币中 7金币多 8水晶少 9水晶多 20-39 点卡 40-59 话费
     * 60-79 Q币
     */

    private int getResIdByAwardId(int awardId) {
        if (awardId >= 1 && awardId < 10) {// 道具 1-9
            return GIFT_ICONS_RES[awardId];
        } else if (awardId >= 20 && awardId < 40) {// 盛大点卡
            if (awardId == 20) {
                return GIFT_ICONS_RES[10];
            } else {
                return GIFT_ICONS_RES[13];
            }
        } else if (awardId >= 40 && awardId < 60) { // 移动话费
            return GIFT_ICONS_RES[11];
        } else if (awardId >= 60 && awardId < 80) { // Q币
            return GIFT_ICONS_RES[12];
        }

        return GIFT_ICONS_RES[0];
    }

    // public Bitmap getBitmap(int id) {
    // for (TurntableAward g : mTurntableAwardList) {
    // if (g.mId == id) {
    // return g.mBitmapRef.get();
    // }
    // }
    //
    // return null;
    // }

    public void release() {

        stopRotate();

        if (mDrawThread != null)
        {
            mIsRotating = false;
        }

        // mListener = null;
        //
        mTurnRef.clear();

        mSelectRef.clear();

        mPointerRef.clear();

        mBlueBallRef.clear();

        mRedBallRef.clear();
        //
        // for (TurntableAward g : mTurntableAwardList) {
        // g.mBitmapRef.clear();
        // }
        //
        // mTurntableAwardList.clear();
        //
        System.gc();
    }

    public interface OnTurntableListener {
        public void onTurntableDone(int result);
    }

    public class TurntableAward {
        public String mName = null;
        public BitmapRef mBitmapRef = new BitmapRef();
        public int mNum = -1;
        public int mId = -1;
    }

    public class DrawThread extends Thread {

        SurfaceHolder sth = null;

        Canvas canvas = null;

        public DrawThread(SurfaceHolder sth, Canvas canvas) {

            this.sth = sth;

            this.canvas = canvas;

            mIsRotating = false;
        }

        public void startThread(OnTurntableListener listener, int targetGiftIndex) {

            if (mDrawThread != null)
            {
                mIsRotating = true;

                startRotate(listener, targetGiftIndex);

                mDrawThread.start();

            }

        }

        public void stopThread() {

            if (mDrawThread != null)
            {

                mDrawThread = null;

                mIsRotating = false;

                runCanvasThread = false;

            }

        }

        @Override
        public void run() {

            synchronized (obj) {
                while (mIsRotating)
                {

                    try {
                        canvas = sth.lockCanvas();

                        if (canvas != null)
                        {
                            Draw(canvas);
                        }
                        startCanvasThreadSyn();

                    } catch (Exception e) {
                        LogUtil.d(TAG, "canvas error");
                    } finally {
                        if (canvas != null) {

                            sth.unlockCanvasAndPost(canvas);
                        }
                    }
                }
            }
        }

        private void startCanvasThreadSyn() {

            if (runCanvasThread) {

                try {
                    obj.wait();
                    Thread.sleep(INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                obj.notify();
            }

        }
    }

}

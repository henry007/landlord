
package com.hurray.landlord.game.local;

import java.util.Timer;
import java.util.TimerTask;

public class SingleUserTimeOut {

    private static long NETWORK_DELAY = 5000;

    private Timer mTimer;

    private TimerTask mTimerTask;

    private boolean mTimeOut = false;

    public boolean isTimeOut() {
        synchronized (this) {
            return mTimeOut;
        }
    }

    public void setTimeOut(long delay) {

        // synchronized (this) {
        //
        // resetAnyway();
        //
        // mTimerTask = new TimerTask() {
        //
        // @Override
        // public void run() {
        // mTimeOut = true;
        // }
        // };
        //
        // mTimer = new Timer();
        // mTimer.schedule(mTimerTask, delay + NETWORK_DELAY);
        // }
    }
    
    public void setTimeOutNow() {
        mTimeOut = true;
    }

    public void reset() {
        synchronized (this) {
            resetAnyway();
        }
    }

    private void resetAnyway() {
        if (mTimer != null) {
            mTimer.cancel();
        }

        mTimer = null;
        mTimerTask = null;
        mTimeOut = false;
    }
}

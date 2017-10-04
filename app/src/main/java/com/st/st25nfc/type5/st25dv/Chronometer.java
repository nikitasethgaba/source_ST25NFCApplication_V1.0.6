/*
  * @author STMicroelectronics MMY Application team
  *
  ******************************************************************************
  * @attention
  *
  * <h2><center>&copy; COPYRIGHT 2017 STMicroelectronics</center></h2>
  *
  * Licensed under ST MIX_MYLIBERTY SOFTWARE LICENSE AGREEMENT (the "License");
  * You may not use this file except in compliance with the License.
  * You may obtain a copy of the License at:
  *
  *        http://www.st.com/Mix_MyLiberty  
  *
  * Unless required by applicable law or agreed to in writing, software 
  * distributed under the License is distributed on an "AS IS" BASIS, 
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied,
  * AND SPECIFICALLY DISCLAIMING THE IMPLIED WARRANTIES OF MERCHANTABILITY,
  * FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  ******************************************************************************
*/

package com.st.st25nfc.type5.st25dv;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.TextView;

import java.text.DecimalFormat;



public class Chronometer extends TextView {
    /*
 * The Android chronometer widget revised so as to count milliseconds
 */

    public interface OnChronometerTickListener {

        void onChronometerTick(Chronometer chronometer);
    }

    private long mBase;
    private boolean mVisible;
    public enum STATE {
        STARTED,
        PAUSED,
        STOPPED
    }

    private STATE mState;
    private boolean mRunning;
    private OnChronometerTickListener mOnChronometerTickListener;

    private static final int TICK_WHAT = 2;

    private long mTimeElapsed;

    public Chronometer(Context context) {
        this(context, null, 0);
    }

    public Chronometer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Chronometer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        mBase = SystemClock.elapsedRealtime();
        updateText(mBase);
    }

    public void setBase(long base) {
        mBase = base;
        dispatchChronometerTick();
        updateText(SystemClock.elapsedRealtime());
    }

    public long getBase() {
        return mBase;
    }

    public void setOnChronometerTickListener(
            OnChronometerTickListener listener) {
        mOnChronometerTickListener = listener;
    }

    public OnChronometerTickListener getOnChronometerTickListener() {
        return mOnChronometerTickListener;
    }

    public void start() {
        mBase = SystemClock.elapsedRealtime();
        updateText(mBase);
        mState = STATE.STARTED;
        updateRunning();
    }

    public void resume() {
        if (mState == STATE.PAUSED) {
            mState = STATE.STARTED;
            updateRunning();
        }
    }

    public void pause() {
        if (mState == STATE.STARTED) {
            mState = STATE.PAUSED;
            updateRunning();
        }
    }


    public void stop() {
        mState = STATE.STOPPED;
        updateRunning();
    }

    public void setStarted(boolean started) {
        updateRunning();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mVisible = false;
        updateRunning();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mVisible = visibility == VISIBLE;
        updateRunning();
    }

    private synchronized void updateText(long now) {

        mTimeElapsed = now - mBase;

        DecimalFormat df = new DecimalFormat("00");

        int hours = (int) (mTimeElapsed / (3600 * 1000));
        int remaining = (int) (mTimeElapsed % (3600 * 1000));

        int minutes = (int) (remaining / (60 * 1000));
        remaining = (int) (remaining % (60 * 1000));

        int seconds = (int) (remaining / 1000);


        int hundredsseconds = (int) (((int) mTimeElapsed % 1000) / 10);

        String text = "";

        if (hours > 0) {
            text += df.format(hours) + ":";
        }

        text += df.format(minutes) + ":";
        text += df.format(seconds) + ":";
        text += df.format(hundredsseconds);
        //text += Integer.toString(hundredsseconds);

        setText(text);

    }

    private void updateRunning() {
        boolean running = mVisible && (mState == STATE.STARTED);
        if (running != mRunning) {
            if (running) {
                updateText(SystemClock.elapsedRealtime());
                dispatchChronometerTick();
                mHandler.sendMessageDelayed(Message.obtain(mHandler,
                        TICK_WHAT), 10);
            } else {
                mHandler.removeMessages(TICK_WHAT);
            }
            mRunning = running;
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message m) {
            if (mRunning) {
                updateText(SystemClock.elapsedRealtime());
                dispatchChronometerTick();
                sendMessageDelayed(Message.obtain(this, TICK_WHAT),
                        10);
            }
        }
    };

    void dispatchChronometerTick() {
        if (mOnChronometerTickListener != null) {
            mOnChronometerTickListener.onChronometerTick(this);
        }
    }

    public long getTimeElapsed() {
        return mTimeElapsed;
    }

    public STATE getState() { return  mState;}
}


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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.st.st25nfc.generic.ST25Menu;
import com.st.st25sdk.NFCTag;
import com.st.st25nfc.R;
import com.st.st25nfc.generic.MainActivity;
import com.st.st25nfc.generic.STFragmentActivity;
import com.st.st25sdk.type5.ST25DVTag;


public class ST25DVStopwatchDemoActivity extends STFragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener, ST25DVTransferTask.OnTransferListener {

    final static String TAG = "ST25DVStopwatchDemoActivity";
    public ST25DVTag mTag;

    private Handler mHandler;
    private Chronometer mChronometer;
    private ST25DVTransferTask mTransferTask;

    private long mTimeElapsed;
    private long mLastTimeUpdate;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_layout);

        // Inflate content of FrameLayout
        FrameLayout frameLayout=(FrameLayout) findViewById(R.id.frame_content);
        View childView = getLayoutInflater().inflate(R.layout.chronometer_demo_content_st25dv, null);
        frameLayout.addView(childView);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mChronometer = (Chronometer) findViewById(R.id.st25DvChronometer);

        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            public void onChronometerTick(Chronometer chronometer) {
                mTimeElapsed = chronometer.getTimeElapsed();
            }
        });


        mMenu = ST25Menu.newInstance(super.getTag());
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        mMenu.inflateMenu(navigationView);

        Button startButton = (Button) findViewById(R.id.startChronoButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });

        Button stopButton = (Button) findViewById(R.id.stopChronoButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

        Button resumeButton = (Button) findViewById(R.id.resumeChronoButton);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resume();
            }
        });

        Button pauseButton = (Button) findViewById(R.id.pauseChronoButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause();
            }
        });


        mHandler = new Handler();
        mTag = (ST25DVTag) MainActivity.getTag();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        stop();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds read_list_items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_empty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long

        // as you specify a parent activity in AndroidManifest.xml.


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return mMenu.selectItem(this, item);
    }


    public void start() {
        mChronometer.start();
        mTransferTask = new ST25DVTransferTask(ST25DVTransferTask.FAST_CHRONO_DEMO_FUNCTION, null, mTag);
        mTransferTask.setTransferListener(this);
        new Thread(mTransferTask).start();
        //IF you want to stop your chrono after X seconds or minutes.
    }


    public void resume() {
        mChronometer.resume();
        if (mTransferTask != null)
            mTransferTask.resume();
    }

    public void pause() {
        mChronometer.pause();
        //To do find a sync with the display
        //while (mTimeElapsed != mLastTimeUpdate);
        if (mTransferTask != null)
            mTransferTask.pause();
    }

    public void stop() {
        mChronometer.stop();
        //To do find a sync with the display
        //while (mTimeElapsed != mLastTimeUpdate);
        if (mTransferTask != null)
            mTransferTask.stop();
    }


    @Override
    public void transferOnProgress(double progressStatus) {

    }

    @Override
    public void transferFinished(boolean success, long time, byte[] buffer) {
         if (!success) {
             stop();
         }
    }

    @Override
    public byte[] getDataToWrite() {
        byte[] data = new byte[3];
        long time = mChronometer.getTimeElapsed();

        int remaining = (int) (time % (3600 * 1000));

        int minutes = (int) (remaining / (60 * 1000));
        remaining = (int) (remaining % (60 * 1000));

        int seconds = (int) (remaining / 1000);
        remaining = (int) (remaining % (1000));

        int hundredsseconds = (int) (((int) time % 1000) / 10);

        data[0] = (byte) (minutes & 0xFF);
        data[1] = (byte) (seconds  & 0xFF);
        data[2] = (byte) (hundredsseconds & 0xFF);

        mLastTimeUpdate = time;
        return data;
    }


    public NFCTag getTag() {
            return mTag;
        }
}


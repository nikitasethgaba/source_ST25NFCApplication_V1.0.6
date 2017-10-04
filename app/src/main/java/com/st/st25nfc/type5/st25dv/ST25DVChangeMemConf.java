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

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.MainActivity;
import com.st.st25nfc.generic.PwdDialogFragment;
import com.st.st25nfc.generic.STFragmentActivity;
import com.st.st25nfc.generic.STType5PwdDialogFragment;
import com.st.st25sdk.STException;
import com.st.st25sdk.type5.ST25DVTag;
import com.st.st25sdk.type5.STType5PasswordInterface;

import static com.st.st25nfc.type5.st25dv.ST25DVChangeMemConf.ActionStatus.ACTION_FAILED;
import static com.st.st25nfc.type5.st25dv.ST25DVChangeMemConf.ActionStatus.ACTION_SUCCESSFUL;
import static com.st.st25nfc.type5.st25dv.ST25DVChangeMemConf.ActionStatus.CONFIG_PASSWORD_NEEDED;
import static com.st.st25nfc.type5.st25dv.ST25DVChangeMemConf.ActionStatus.TAG_NOT_IN_THE_FIELD;
import static com.st.st25nfc.type5.st25dv.ST25DVChangeMemConf.SelectedArea.AREA1_SELECTED;
import static com.st.st25nfc.type5.st25dv.ST25DVChangeMemConf.SelectedArea.AREA2_SELECTED;
import static com.st.st25nfc.type5.st25dv.ST25DVChangeMemConf.SelectedArea.AREA3_SELECTED;


public class ST25DVChangeMemConf extends STFragmentActivity implements NavigationView.OnNavigationItemSelectedListener,
        STType5PwdDialogFragment.STType5PwdDialogListener {

    private final int TOUCH_RANGE = 50;

    static final String TAG = "ST25DVChangeMemConf";
    private Handler mHandler;
    private ST25DVTag mST25DVTag;
    FragmentManager mFragmentManager;

    private RelativeLayout mColumn2Layout;
    private int mWholeTagHeightInPixels;
    private int mTagTotalMemSizeInBlocks;

    private TextView mArea1TextView;
    private TextView mArea2TextView;
    private TextView mArea3TextView;
    private TextView mArea4TextView;

    private boolean mAreAreaSizesKnown = false;

    // WARNING: EndOfArea values should be stored in a byte but we use an int to simplify the
    //          calculations and comparisons.
    private int mEndOfArea1;
    private int mEndOfArea2;
    private int mEndOfArea3;
    // mMaxEndOfAreaI corresponds to the maximum value that can take mEndOfArea1, mEndOfArea2 and mEndOfArea3.
    // (the end of tag's memory is reached)
    private int mMaxEndOfAreaI;

    private SelectedArea mSelectedArea;

    private TextView mArea1SizeInBytesTextView;
    private TextView mArea1SizeInBlocksTextView;

    private TextView mArea2SizeInBytesTextView;
    private TextView mArea2SizeInBlocksTextView;

    private TextView mArea3SizeInBytesTextView;
    private TextView mArea3SizeInBlocksTextView;

    private TextView mArea4SizeInBytesTextView;
    private TextView mArea4SizeInBlocksTextView;

    private TextView mTotalSizeInBytesTextView;
    private TextView mTotalSizeInBlocksTextView;

    private RadioButton mArea1RadioButton;
    private RadioButton mArea2RadioButton;
    private RadioButton mArea3RadioButton;

    enum Action {
        READ_REGISTER_VALUES,
        WRITE_REGISTER_VALUES
    }

    enum ActionStatus {
        ACTION_SUCCESSFUL,
        ACTION_FAILED,
        TAG_NOT_IN_THE_FIELD,
        CONFIG_PASSWORD_NEEDED
    };

    enum SelectedArea {
        AREA1_SELECTED,
        AREA2_SELECTED,
        AREA3_SELECTED
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_layout);

        // Inflate content of FrameLayout
        FrameLayout frameLayout=(FrameLayout) findViewById(R.id.frame_content);
        View childView = getLayoutInflater().inflate(R.layout.st25dv_memory_configuration, null);
        frameLayout.addView(childView);

        mST25DVTag = (ST25DVTag) MainActivity.getTag();
        mHandler = new Handler();
        mFragmentManager = getSupportFragmentManager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        mMenu.inflateMenu(navigationView);

        try {
            mTagTotalMemSizeInBlocks = mST25DVTag.getMemSizeInBytes() / mST25DVTag.getBlockSizeInBytes();
            mMaxEndOfAreaI = (mST25DVTag.getMaxEndOfAreaValue() & 0xFF);
        } catch (STException e) {
            e.printStackTrace();
            return;
        }

        mColumn2Layout = (RelativeLayout) findViewById(R.id.column2Layout);
        // Hide column2Layout until we have calculated all the area sizes
        mColumn2Layout.setVisibility(View.INVISIBLE);

        // Trick to get the height of column2Layout
        // By this way, the drawing will fit the space available on this phone
        ViewTreeObserver viewTreeObserver = mColumn2Layout.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver viewTreeObserver = mColumn2Layout.getViewTreeObserver();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    viewTreeObserver.removeGlobalOnLayoutListener(this);
                } else {
                    viewTreeObserver.removeOnGlobalLayoutListener(this);
                }

                int column2LayoutHeight = mColumn2Layout.getMeasuredHeight();
                setDrawingSize(column2LayoutHeight);
            }
        });

        mArea1TextView = (TextView) findViewById(R.id.area1TextView);
        mArea1TextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return area1OnTouchEvent(v, event);
            }
        });

        mArea2TextView = (TextView) findViewById(R.id.area2TextView);
        mArea2TextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return area2OnTouchEvent(v, event);
            }
        });

        mArea3TextView = (TextView) findViewById(R.id.area3TextView);
        mArea3TextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return area3OnTouchEvent(v, event);
            }
        });

        mArea4TextView = (TextView) findViewById(R.id.area4TextView);
        // No onTouchListener for Area4 because it will simply take all the remaining space

        mArea1SizeInBytesTextView = (TextView) findViewById(R.id.area1SizeInBytesTextView);
        mArea1SizeInBlocksTextView = (TextView) findViewById(R.id.area1SizeInBlocksTextView);

        mArea2SizeInBytesTextView = (TextView) findViewById(R.id.area2SizeInBytesTextView);
        mArea2SizeInBlocksTextView = (TextView) findViewById(R.id.area2SizeInBlocksTextView);

        mArea3SizeInBytesTextView = (TextView) findViewById(R.id.area3SizeInBytesTextView);
        mArea3SizeInBlocksTextView = (TextView) findViewById(R.id.area3SizeInBlocksTextView);

        mArea4SizeInBytesTextView = (TextView) findViewById(R.id.area4SizeInBytesTextView);
        mArea4SizeInBlocksTextView = (TextView) findViewById(R.id.area4SizeInBlocksTextView);

        mTotalSizeInBytesTextView = (TextView) findViewById(R.id.totalSizeInBytesTextView);
        mTotalSizeInBlocksTextView = (TextView) findViewById(R.id.totalSizeInBlocksTextView);


        mArea1RadioButton = (RadioButton) findViewById(R.id.area1RadioButton);
        mArea1RadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedArea = AREA1_SELECTED;
            }
        });

        mArea2RadioButton = (RadioButton) findViewById(R.id.area2RadioButton);
        mArea2RadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedArea = AREA2_SELECTED;
            }
        });

        mArea3RadioButton = (RadioButton) findViewById(R.id.area3RadioButton);
        mArea3RadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedArea = AREA3_SELECTED;
            }
        });

        setSelectedArea(AREA1_SELECTED);

        Button upButton = (Button) findViewById(R.id.upButton);
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(mSelectedArea) {
                    case AREA1_SELECTED:
                        setEndOfArea1(mEndOfArea1 - 1);
                        break;
                    case AREA2_SELECTED:
                        setEndOfArea2(mEndOfArea2 - 1);
                        break;
                    case AREA3_SELECTED:
                        setEndOfArea3(mEndOfArea3 - 1);
                        break;
                }
                drawAreas();
            }
        });

        Button downButton = (Button) findViewById(R.id.downButton);
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(mSelectedArea) {
                    case AREA1_SELECTED:
                        setEndOfArea1(mEndOfArea1 + 1);
                        break;
                    case AREA2_SELECTED:
                        setEndOfArea2(mEndOfArea2 + 1);
                        break;
                    case AREA3_SELECTED:
                        setEndOfArea3(mEndOfArea3 + 1);
                        break;
                }
                drawAreas();
            }
        });

        mArea1TextView.setBackgroundResource(R.drawable.shape_area1_selected);
        mArea2TextView.setBackgroundResource(R.drawable.shape_area2_selected);
        mArea3TextView.setBackgroundResource(R.drawable.shape_area3_selected);

        readCurrentAreaSizes();
    }

    @Override
    public void onResume() {
        super.onResume();

        drawAreas();
    }

    private void setDrawingSize(int column2LayoutHeight) {

        // Areas will be drawn in "column2Layout"
        // Each area will be represented by a rectangle.
        // - Rectangle width will be fixed (arbitrary set to "80dp")
        // - Rectangle height will be relative to column2Layout's height. By this way the drawing
        //   size will fit the screen size.

        // The drawing will occupy 90% of column2Layout's height
        mWholeTagHeightInPixels = (column2LayoutHeight * 90) / 100;

        drawAreas();
    }

    /**
     * This function will read the tag's registers to know the size and position of each area.
     */
    private void readCurrentAreaSizes() {
        new myAsyncTask(Action.READ_REGISTER_VALUES).execute();
    }

    /**
     * This function will write the area sizes to the tag.
     */
    private void writeAreaSizes() {
        new myAsyncTask(Action.WRITE_REGISTER_VALUES).execute();
    }

    private void drawAreas() {

        if(mWholeTagHeightInPixels == 0) {
            // Areas cannot be drawn until mWholeTagHeightInPixels is known because all the sizes
            // will be relative to this size
            return;
        }

        if(!mAreAreaSizesKnown) {
            // Areas cannot be drawn until area sizes are known
            return;
        }

        int area1EndBlock = getOffsetInBlocks(mEndOfArea1);
        int area2EndBlock = getOffsetInBlocks(mEndOfArea2);
        int area3EndBlock = getOffsetInBlocks(mEndOfArea3);
        int area4EndBlock = getOffsetInBlocks(mMaxEndOfAreaI);

        int area1SizeInBlocks = area1EndBlock + 1;
        int area2SizeInBlocks = area2EndBlock - area1EndBlock;
        int area3SizeInBlocks = area3EndBlock - area2EndBlock;
        int area4SizeInBlocks = area4EndBlock - area3EndBlock;

        int area1RectangleHeight = convertSizeInBlocksIntoRectangleHeight(area1SizeInBlocks);
        int area2RectangleHeight = convertSizeInBlocksIntoRectangleHeight(area2SizeInBlocks);
        int area3RectangleHeight = convertSizeInBlocksIntoRectangleHeight(area3SizeInBlocks);
        int area4RectangleHeight = convertSizeInBlocksIntoRectangleHeight(area4SizeInBlocks);

        mArea1TextView.getLayoutParams().height = area1RectangleHeight;
        mArea1TextView.requestLayout();

        mArea2TextView.getLayoutParams().height = area2RectangleHeight;
        mArea2TextView.requestLayout();

        mArea3TextView.getLayoutParams().height = area3RectangleHeight;
        mArea3TextView.requestLayout();

        mArea4TextView.getLayoutParams().height = area4RectangleHeight;
        mArea4TextView.requestLayout();

        // The content of column2Layout is ready
        mColumn2Layout.setVisibility(View.VISIBLE);

        // We can update the summary array with the new sizes
        fillSummaryArray();
    }

    private void fillSummaryArray() {
        int area1EndBlock = getOffsetInBlocks(mEndOfArea1);
        int area2EndBlock = getOffsetInBlocks(mEndOfArea2);
        int area3EndBlock = getOffsetInBlocks(mEndOfArea3);
        int area4EndBlock = getOffsetInBlocks(mMaxEndOfAreaI);

        int area1SizeInBlocks = area1EndBlock + 1;
        int area2SizeInBlocks = area2EndBlock - area1EndBlock;
        int area3SizeInBlocks = area3EndBlock - area2EndBlock;
        int area4SizeInBlocks = area4EndBlock - area3EndBlock;

        int totalSizeInBlocks = area4EndBlock + 1;

        int blockSizeInBytes = mST25DVTag.getBlockSizeInBytes();

        mArea1SizeInBytesTextView.setText(String.valueOf(area1SizeInBlocks*blockSizeInBytes));
        mArea1SizeInBlocksTextView.setText(String.valueOf(area1SizeInBlocks));

        mArea2SizeInBytesTextView.setText(String.valueOf(area2SizeInBlocks*blockSizeInBytes));
        mArea2SizeInBlocksTextView.setText(String.valueOf(area2SizeInBlocks));

        mArea3SizeInBytesTextView.setText(String.valueOf(area3SizeInBlocks*blockSizeInBytes));
        mArea3SizeInBlocksTextView.setText(String.valueOf(area3SizeInBlocks));

        mArea4SizeInBytesTextView.setText(String.valueOf(area4SizeInBlocks*blockSizeInBytes));
        mArea4SizeInBlocksTextView.setText(String.valueOf(area4SizeInBlocks));

        mTotalSizeInBytesTextView.setText(String.valueOf(totalSizeInBlocks*blockSizeInBytes));
        mTotalSizeInBlocksTextView.setText(String.valueOf(totalSizeInBlocks));

    }

    /**
     * This function converts a size in blocks into a rectangle Height.
     * It does a rule of three:
     *      mTagTotalMemSizeInBlocks ---> mWholeTagHeightInPixels
     *      sizeInBlocks             ---> rectangleHeight
     * @param sizeInBlocks
     * @return
     */
    private int convertSizeInBlocksIntoRectangleHeight(int sizeInBlocks) {
        int rectangleHeight = (sizeInBlocks * mWholeTagHeightInPixels) / mTagTotalMemSizeInBlocks;
        return rectangleHeight;
    }

    /**
     * This function converts a size (as defined in endOfArea registers) into a rectangle Height.
     * It does a rule of three:
     *      mMaxEndOfAreaI ---> mWholeTagHeightInPixels
     *      endOfAreaValue  ---> rectangleHeight
     * @param endOfAreaValue
     * @return
     */
    private int convertEndOfAreaValueIntoRectangleHeight(int endOfAreaValue) {
        int rectangleHeight = (endOfAreaValue * mWholeTagHeightInPixels) / mMaxEndOfAreaI;
        return rectangleHeight;
    }

    /**
     * This function converts a rectangle Height into a size (as defined in endOfArea registers).
     * It does a rule of three:
     *      mWholeTagHeightInPixels ---> mMaxEndOfAreaI
     *      rectangleHeight         ---> endOfAreaValue
     * @param rectangleHeight
     * @return
     */
    private int convertRectangleHeightIntoEndOfAreaValue(int rectangleHeight) {
        int endOfAreaValue = (rectangleHeight * mMaxEndOfAreaI) / mWholeTagHeightInPixels;
        return endOfAreaValue;
    }


    /**
     * Proceed touch events in Area1
     * @param v
     * @param event
     * @return
     */
    public boolean area1OnTouchEvent(View v, MotionEvent event) {
        Log.v(TAG, "Area1 MotionEvent: " + event);

        int height = v.getLayoutParams().height;
        Log.v(TAG, "height="+ height);

        int y = (int)event.getY();
        Log.v(TAG, "y="+ y);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                // We consume the touch even only if the click was in the last pixels of the area
                if((height - y) < TOUCH_RANGE) {
                    // return true to indicate that we have consummed the ACTION_DOWN event
                    setSelectedArea(AREA1_SELECTED);
                    return true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                int newRectangleHeight = y;

                if(newRectangleHeight < 0) {
                    newRectangleHeight = 0;
                }

                int newArea1Size = convertRectangleHeightIntoEndOfAreaValue(newRectangleHeight);
                setEndOfArea1(0 + newArea1Size);

                drawAreas();
                break;

            case MotionEvent.ACTION_UP:
                break;
        }

        return false;
    }

    /**
     * Proceed touch events in Area2
     * @param v
     * @param event
     * @return
     */
    public boolean area2OnTouchEvent(View v, MotionEvent event) {
        Log.v(TAG, "Area2 MotionEvent: " + event);

        int height = v.getLayoutParams().height;
        Log.v(TAG, "height="+ height);

        int y = (int)event.getY();
        Log.v(TAG, "y="+ y);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                // We consume the touch even only if the click was in the last pixels of the area
                if((height - y) < TOUCH_RANGE) {
                    // return true to indicate that we have consummed the ACTION_DOWN event
                    setSelectedArea(AREA2_SELECTED);
                    return true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                int newRectangleHeight = y;

                if(newRectangleHeight < 0) {
                    newRectangleHeight = 0;
                }

                int newArea2Size = convertRectangleHeightIntoEndOfAreaValue(newRectangleHeight);
                setEndOfArea2(mEndOfArea1 + newArea2Size);

                drawAreas();
                break;

            case MotionEvent.ACTION_UP:
                break;
        }

        return false;
    }

    /**
     * Proceed touch events in Area3
     * @param v
     * @param event
     * @return
     */
    public boolean area3OnTouchEvent(View v, MotionEvent event) {
        Log.v(TAG, "Area3 MotionEvent: " + event);

        int height = v.getLayoutParams().height;
        Log.v(TAG, "height="+ height);

        int y = (int)event.getY();
        Log.v(TAG, "y="+ y);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                // We consume the touch even only if the click was in the last pixels of the area
                if((height - y) < TOUCH_RANGE) {
                    // return true to indicate that we have consummed the ACTION_DOWN event
                    setSelectedArea(AREA3_SELECTED);
                    return true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                int newRectangleHeight = y;

                if(newRectangleHeight < 0) {
                    newRectangleHeight = 0;
                }

                int newArea3Size = convertRectangleHeightIntoEndOfAreaValue(newRectangleHeight);
                setEndOfArea3(mEndOfArea2 + newArea3Size);

                drawAreas();
                break;

            case MotionEvent.ACTION_UP:
                break;
        }

        return false;
    }

    private void setEndOfArea1(int endOfArea1) {

        if(endOfArea1 < 0) {
            endOfArea1 = 0;
        }

        if(mEndOfArea2 == mMaxEndOfAreaI) {
            // Max value for endOfArea1 is mMaxEndOfAreaI
            if(endOfArea1 > mMaxEndOfAreaI) {
                endOfArea1 = mMaxEndOfAreaI;
            }

        } else {
            // Max value for endOfArea1 is (mEndOfArea2-1)
            if(endOfArea1 > (mEndOfArea2-1)) {
                endOfArea1 = (mEndOfArea2-1);
            }
        }

        mEndOfArea1 = endOfArea1;
    }

    private void setEndOfArea2(int endOfArea2) {

        if(endOfArea2 < 0) {
            endOfArea2 = 0;
        }

        if(mEndOfArea3 == mMaxEndOfAreaI) {
            // Max value for endOfArea2 is mMaxEndOfAreaI
            if(endOfArea2 > mMaxEndOfAreaI) {
                endOfArea2 = mMaxEndOfAreaI;
            }

        } else {
            // Max value for endOfArea2 is (mEndOfArea3-1)
            if(endOfArea2 > (mEndOfArea3-1)) {
                endOfArea2 = (mEndOfArea3-1);
            }
        }

        if(mEndOfArea1 == mMaxEndOfAreaI) {
            // Min value for endOfArea2 is mMaxEndOfAreaI
            endOfArea2 = mMaxEndOfAreaI;

        } else {
            // Min value for endOfArea2 is (mEndOfArea1+1)
            if(endOfArea2 < (mEndOfArea1+1)) {
                endOfArea2 = (mEndOfArea1+1);
            }
        }

        mEndOfArea2 = endOfArea2;
    }

    private void setEndOfArea3(int endOfArea3) {

        if(endOfArea3 < 0) {
            endOfArea3 = 0;
        }

        // Max value for endOfArea3 is mMaxEndOfAreaI
        if(endOfArea3 > mMaxEndOfAreaI) {
            endOfArea3 = mMaxEndOfAreaI;
        }

        if(mEndOfArea2 == mMaxEndOfAreaI) {
            // Min value for endOfArea3 is mMaxEndOfAreaI
            endOfArea3 = mMaxEndOfAreaI;

        } else {
            // Min value for endOfArea3 is (mEndOfArea2+1)
            if(endOfArea3 < (mEndOfArea2+1)) {
                endOfArea3 = (mEndOfArea2+1);
            }
        }

        mEndOfArea3 = endOfArea3;
    }


    private void setSelectedArea(SelectedArea selectedArea) {
        mSelectedArea = selectedArea;

        switch(selectedArea) {
            case AREA1_SELECTED:
                mArea1RadioButton.setChecked(true);
                break;
            case AREA2_SELECTED:
                mArea2RadioButton.setChecked(true);
                break;
            case AREA3_SELECTED:
                mArea3RadioButton.setChecked(true);
                break;
        }
    }

    /**
     * This function converts an offset value, contained in a EndAi register into an offset in blocks
     * @param offset
     * @return
     */
    private int getOffsetInBlocks(int offset) {
        return (offset*8 + 7);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds read_list_items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_st25dv_mem_conf, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                writeAreaSizes();
                return true;

            case R.id.action_refresh:
                readCurrentAreaSizes();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return mMenu.selectItem(this, item);
    }

    private void displayPasswordDialogBox() {
        Log.v(TAG, "displayPasswordDialogBox");

        final byte passworNumber;
        try {
            STType5PasswordInterface STType5PasswordInterface = (STType5PasswordInterface) mST25DVTag;
            passworNumber = STType5PasswordInterface.getConfigurationPasswordNumber();
        } catch (STException e) {
            e.printStackTrace();
            return;
        }

        // Warning: Function called from background thread! Post a request to the UI thread
        runOnUiThread(new Runnable() {
            public void run() {
                FragmentManager fragmentManager = getSupportFragmentManager();

                STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(
                        STType5PwdDialogFragment.STPwdAction.PRESENT_CURRENT_PWD,
                        passworNumber,
                        getResources().getString(R.string.enter_configuration_pwd));
                pwdDialogFragment.show(fragmentManager, "pwdDialogFragment");
            }
        });

    }

    @Override
    public void onSTType5PwdDialogFinish(int result) {
        Log.v(TAG, "onSTType5PwdDialogFinish. result = " + result);

        if (result == PwdDialogFragment.RESULT_OK) {
            // Config password has been entered successfully so we can now retry to write the register values
            writeAreaSizes();

        } else {
            Log.e(TAG, "Action failed! Tag not updated!");
            showToast(R.string.register_action_not_completed);;
        }
    }


    private class myAsyncTask extends AsyncTask<Void, Void, ActionStatus> {
        Action mAction;

        public myAsyncTask(Action action) {
            mAction = action;
        }

        @Override
        protected ActionStatus doInBackground(Void... param) {
            ActionStatus result;

            try {
                if(mAction == Action.READ_REGISTER_VALUES) {
                    mEndOfArea1 = mST25DVTag.getRegisterEndArea1().getRegisterValue();
                    Log.v(TAG, "mEndOfArea1 = " + mEndOfArea1);

                    mEndOfArea2 = mST25DVTag.getRegisterEndArea2().getRegisterValue();
                    Log.v(TAG, "mEndOfArea2 = " + mEndOfArea2);

                    mEndOfArea3 = mST25DVTag.getRegisterEndArea3().getRegisterValue();
                    Log.v(TAG, "mEndOfArea3 = " + mEndOfArea3);

                    mAreAreaSizesKnown = true;

                    result = ACTION_SUCCESSFUL;

                } else {
                    Log.v(TAG, "WRITE_REGISTER_VALUES:");
                    Log.v(TAG, "mEndOfArea1 = " + mEndOfArea1);
                    Log.v(TAG, "mEndOfArea2 = " + mEndOfArea2);
                    Log.v(TAG, "mEndOfArea3 = " + mEndOfArea3);

                    mST25DVTag.setAreaEndValues((byte) mEndOfArea1, (byte) mEndOfArea2, (byte) mEndOfArea3);
                    result = ACTION_SUCCESSFUL;
                }

            } catch (STException e) {
                switch (e.getError()) {
                    case CONFIG_PASSWORD_NEEDED:
                        result = CONFIG_PASSWORD_NEEDED;
                        break;

                    case TAG_NOT_IN_THE_FIELD:
                        result = TAG_NOT_IN_THE_FIELD;
                        break;

                    default:
                        e.printStackTrace();
                        result = ACTION_FAILED;
                        break;
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(ActionStatus actionStatus) {

            switch(actionStatus) {
                case ACTION_SUCCESSFUL:
                    if(mAction == Action.READ_REGISTER_VALUES) {
                        // Now that area sizes are known, we can refresh the drawing
                        drawAreas();
                    } else {
                        // Write successful
                        showToast(R.string.tag_updated);
                    }
                    break;

                case CONFIG_PASSWORD_NEEDED:
                    displayPasswordDialogBox();
                    break;

                case ACTION_FAILED:
                    if(mAction == Action.READ_REGISTER_VALUES) {
                        showToast(R.string.error_while_reading_the_tag);
                        mColumn2Layout.setVisibility(View.INVISIBLE);
                    } else {
                        showToast(R.string.error_while_updating_the_tag);
                    }
                    break;

                case TAG_NOT_IN_THE_FIELD:
                    showToast(R.string.tag_not_in_the_field);
                    mColumn2Layout.setVisibility(View.INVISIBLE);
                    break;
            }

            return;
        }
    }


}

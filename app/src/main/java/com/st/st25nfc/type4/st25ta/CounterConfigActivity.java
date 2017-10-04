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

package com.st.st25nfc.type4.st25ta;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.MainActivity;
import com.st.st25nfc.generic.PwdDialogFragment;
import com.st.st25nfc.generic.STFragmentActivity;
import com.st.st25sdk.STException;
import com.st.st25sdk.type4a.STType4CounterInterface;
import com.st.st25sdk.type4a.STType4Tag;

import java.util.ArrayList;


public class CounterConfigActivity extends STFragmentActivity implements NavigationView.OnNavigationItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    // Set here the Toolbar to use for this activity
    private int toolbar_res = R.menu.toolbar_empty;

    static final String TAG = "CounterConfigActivity";
    private STType4Tag myTag;
    private boolean counterFeatureAvailable;

    FragmentManager mFragmentManager;

    private RadioButton[] mCounterRb;

    private Switch mLockSwitch;
    private Switch mEnableSwitch;
    private TextView mCounterValueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_layout);

        // Inflate content of FrameLayout
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_content);
        View childView = getLayoutInflater().inflate(R.layout.fragment_counter_conf, null);
        frameLayout.addView(childView);

        myTag = (STType4Tag) MainActivity.getTag();
        mFragmentManager = getSupportFragmentManager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        mMenu.inflateMenu(navigationView);

        counterFeatureAvailable = true;
        try {
            if (((STType4CounterInterface) myTag) instanceof STType4CounterInterface) {
                enableCounterFeatures(true);
            } else {
                enableCounterFeatures(false);
                counterFeatureAvailable = false;
            }
        } catch (Exception e) {
            // in case of the Tag is not Counter enabled
            enableCounterFeatures(false);
            counterFeatureAvailable = false;
        }

        mCounterValueTextView = (TextView) findViewById(R.id.counterValueTextView);
        getCurrentCounterValue();
    }


    private void createRadioButton(ArrayList<String> counterStates, boolean enable) {
        RadioGroup rgp = (RadioGroup) findViewById(R.id.counterReadWriteRadioGroup);
        rgp.setOrientation(LinearLayout.VERTICAL);
        mCounterRb = new RadioButton[counterStates.size()];
        for (int i = 0; i < counterStates.size(); i++) {
            mCounterRb[i] = new RadioButton(this);
            mCounterRb[i].setId(i);
            mCounterRb[i].setText("- " + mCounterRb[i].getId() + " " + counterStates.get(i));
            rgp.addView(mCounterRb[i]);
        }
        rgp.setEnabled(enable);
        setCheckableRadioButton(rgp, enable);
    }

    private void setCheckableRadioButton(RadioGroup rgp, boolean checked) {
        for (int i = 0; i < rgp.getChildCount(); i++) {
            ((RadioButton) rgp.getChildAt(i)).setEnabled(checked);
        }
    }

    private void updateCounterModeRadioButtonStatus(int index, RadioButton[] counterModeRb) {
        if (index < counterModeRb.length) {
            counterModeRb[index].setChecked(true);
        }
    }

    private void enableCounterFeatures(boolean enable) {
        int visibility = enable ? View.VISIBLE : View.INVISIBLE;

        ArrayList<String> counterStates = new ArrayList<String>();
        counterStates.add("Increment on Read");
        counterStates.add("Increment on Write");
        createRadioButton(counterStates, enable);
        Button updateTagButton = (Button) findViewById(R.id.updateRWButton);
        updateTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTagCounterMode();
            }
        });
        updateTagButton.setVisibility(visibility);

        mLockSwitch = (Switch) findViewById(R.id.lockCounterSwitch);
        mLockSwitch.setOnCheckedChangeListener(this);
        mLockSwitch.setVisibility(visibility);

        mEnableSwitch = (Switch) findViewById(R.id.enableCounterSwitch);
        mEnableSwitch.setOnCheckedChangeListener(this);
        mEnableSwitch.setVisibility(visibility);

        // Descriptions
        if (enable) {
            TextView headerNotAvailable = (TextView) findViewById(R.id.counterNotAvailableView);
            headerNotAvailable.setVisibility(View.INVISIBLE);
            TextView header = (TextView) findViewById(R.id.counterConfHeaderTextView);
            header.setVisibility(View.VISIBLE);
        } else {
            TextView headerNotAvailable = (TextView) findViewById(R.id.counterNotAvailableView);
            headerNotAvailable.setVisibility(View.VISIBLE);
            TextView header = (TextView) findViewById(R.id.counterConfHeaderTextView);
            header.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (counterFeatureAvailable) getCurrentCounterConf();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds read_list_items to the action bar if it is present.
        getMenuInflater().inflate(toolbar_res, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long

        // as you specify a parent activity in AndroidManifest.xml.


        return super.onOptionsItemSelected(item);
    }

    /**
     * Get current memory configuration.
     * <p>
     * NB: The access to register values should be done in a background thread because, if the
     * cache is not up-to-date, it will trigger a read of register value from the tag.
     */
    private void getCurrentCounterConf() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    final boolean counterlocked = ((STType4CounterInterface) myTag).isCounterLocked();
                    final boolean counterEnabled = ((STType4CounterInterface) myTag).isCounterEnabled();
                    final boolean counterInReadMode = ((STType4CounterInterface) myTag).isCounterIncrementedOnRead();
                    final boolean counterInWriteMode = ((STType4CounterInterface) myTag).isCounterIncrementedOnWrite();
                    final RadioButton[] counterStatus = mCounterRb;
                    // Post an action to UI Thead to update the widgets
                    runOnUiThread(new Runnable() {
                        public void run() {
                            updateCounterModeRadioButtonStatus(counterInReadMode == true ? 0 : 1, counterStatus);
                            mLockSwitch.setChecked(counterlocked == true ? true : false);
                            mEnableSwitch.setChecked(counterEnabled == true ? true : false);
                        }
                    });

                } catch (STException e) {
                    switch (e.getError()) {
                        case TAG_NOT_IN_THE_FIELD:
                            showToast(R.string.tag_not_in_the_field);
                            break;
                        default:
                            e.printStackTrace();
                            showToast(R.string.error_while_reading_the_tag);
                    }
                }
            }
        }).start();
    }

    private void getCurrentCounterValue() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    final int counterValue = ((STType4CounterInterface) myTag).getCounterValue();

                    // Post an action to UI Thead to update the widgets
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mCounterValueTextView.setText(String.valueOf(counterValue));
                        }
                    });

                } catch (STException e) {
                    switch (e.getError()) {
                        case TAG_NOT_IN_THE_FIELD:
                            showToast(R.string.tag_not_in_the_field);
                            break;
                        default:
                            e.printStackTrace();
                            showToast(R.string.error_while_reading_the_tag);
                    }
                }
            }
        }).start();
    }

    private void changeTagCounterMode() {
        new Thread(new Runnable() {
            public void run() {
                Log.v(TAG, "changeTagCounterMode");
                RadioGroup rgp = (RadioGroup) findViewById(R.id.counterReadWriteRadioGroup);
                int selection = rgp.getCheckedRadioButtonId();
                try {
                    switch (selection) {
                        case 0:
                            ((STType4CounterInterface) myTag).incrementCounterOnRead();
                            break;
                        case 1:
                            ((STType4CounterInterface) myTag).incrementCounterOnWrite();
                            break;
                        default:
                            showToast("Wrong feature ...");
                            break;
                        // none selected
                    }
                    showToast(R.string.tag_updated);

                } catch (STException e) {
                    switch (e.getError()) {
                        case TAG_NOT_IN_THE_FIELD:
                            showToast(R.string.tag_not_in_the_field);
                            break;
                        case CONFIG_PASSWORD_NEEDED:
                            displayPasswordDialogBox();
                            break;
                        default:
                            e.printStackTrace();
                            showToast(R.string.error_while_updating_the_tag);
                    }
                }
            }
        }).start();
    }

    private void enableCounter() {
        new Thread(new Runnable() {
            public void run() {
                Log.v(TAG, "enableCounter");
                try {
                    if (((STType4CounterInterface) myTag).isCounterEnabled()) {
                        //showToast("Tag already enabled");
                    } else {
                        ((STType4CounterInterface) myTag).enableCounter();
                        showToast(R.string.tag_updated);
                    }
                } catch (STException e) {
                    switch (e.getError()) {
                        case TAG_NOT_IN_THE_FIELD:
                            showToast(R.string.tag_not_in_the_field);
                            break;
                        case CONFIG_PASSWORD_NEEDED:
                            displayPasswordDialogBox();
                            break;
                        default:
                            e.printStackTrace();
                            showToast(R.string.error_while_updating_the_tag);
                    }
                }

            }
        }).start();
    }

    private void disableCounter() {
        new Thread(new Runnable() {
            public void run() {
                Log.v(TAG, "disableCounter");
                try {
                    if (((STType4CounterInterface) myTag).isCounterEnabled()) {
                        ((STType4CounterInterface) myTag).disableCounter();
                        showToast(R.string.tag_updated);
                    } else {
                        //showToast("Tag already disabled");
                    }
                } catch (STException e) {
                    switch (e.getError()) {
                        case TAG_NOT_IN_THE_FIELD:
                            showToast(R.string.tag_not_in_the_field);
                            break;
                        case CONFIG_PASSWORD_NEEDED:
                            displayPasswordDialogBox();
                            break;
                        default:
                            e.printStackTrace();
                            showToast(R.string.error_while_updating_the_tag);
                    }
                }

            }
        }).start();
    }

    private void lockCounter() {
        new Thread(new Runnable() {
            public void run() {
                Log.v(TAG, "lockCounter");
                try {
                    if (((STType4CounterInterface) myTag).isCounterLocked()) {
                        showToast("Tag already locked ");
                    } else {
                        // The lock cannot be removed .... WARNING
                        //((STType4CounterInterface) myTag).lockCounter();
                        showToast(R.string.tag_updated );
                        showToast("Warning: " +  "Cmd not executed - reverse not available ");
                    }
                } catch (STException e) {
                    switch (e.getError()) {
                        case TAG_NOT_IN_THE_FIELD:
                            showToast(R.string.tag_not_in_the_field);
                            break;
                        case CONFIG_PASSWORD_NEEDED:
                            displayPasswordDialogBox();
                            break;
                        default:
                            e.printStackTrace();
                            showToast(R.string.error_while_updating_the_tag);
                    }
                }

            }
        }).start();
    }

    private void unlockCounter() {
        new Thread(new Runnable() {
            public void run() {
                Log.v(TAG, "unlockCounter");
                try {
                    if (((STType4CounterInterface) myTag).isCounterLocked()) {
                        showToast("Counter already locked. Unlock Counter not possible");
                    } else {
                        //showToast("Counter already unlocked");
                    }
                } catch (STException e) {
                    switch (e.getError()) {
                        case TAG_NOT_IN_THE_FIELD:
                            showToast(R.string.tag_not_in_the_field);
                            break;
                        case CONFIG_PASSWORD_NEEDED:
                            displayPasswordDialogBox();
                            break;
                        default:
                            e.printStackTrace();
                            showToast(R.string.error_while_updating_the_tag);
                    }
                }

            }
        }).start();
    }


    private void displayPasswordDialogBox() {
        Log.v(TAG, "displayPasswordDialogBox");

    }
    private void displayLockConfirmationDialogBox() {
        Log.v(TAG, "displayConfirmationDialogBox");
        new AlertDialog.Builder(this)
                .setTitle("Action confirmation")
                .setMessage("Do you really want to lock the configuration?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        lockCounter();
                    }})
                .setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //Do Something Here
                                mLockSwitch.setChecked(false);
                            }
                        }).show();
    }



    public void onPwdDialogFinish(int result) {
        Log.v(TAG, "onPwdDialogFinish. result = " + result);
        if (result == PwdDialogFragment.RESULT_OK) {
            // Config password has been entered successfully so we can now retry to change the memory configuration
            changeTagCounterMode();
        } else {
            Log.e(TAG, "Action failed! Tag not updated!");
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return mMenu.selectItem(this, item);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.lockCounterSwitch:
                //TODO: Code for checked one...
                if (isChecked) {
                    displayLockConfirmationDialogBox();
                    //lockCounter();
                } else {
                    unlockCounter();
                }
                break;

            case R.id.enableCounterSwitch:
                //TODO: Code for checked one...
                if (isChecked) {
                    enableCounter();
                } else {
                    disableCounter();
                }
                break;
        }

    }
}

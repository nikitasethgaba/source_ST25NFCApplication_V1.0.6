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

package com.st.st25nfc.type5.st25tv;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.MainActivity;
import com.st.st25nfc.generic.PwdDialogFragment;
import com.st.st25nfc.generic.STFragmentActivity;
import com.st.st25nfc.generic.STType5PwdDialogFragment;
import com.st.st25sdk.STException;
import com.st.st25sdk.type5.ST25TVTag;


public class ST25TVEasActivity extends STFragmentActivity implements STType5PwdDialogFragment.STType5PwdDialogListener, NavigationView.OnNavigationItemSelectedListener {

    // Set here the Toolbar to use for this activity
    private int toolbar_res = R.menu.toolbar_empty;

    static final String TAG = "EasActivity";
    private Handler mHandler;
    private ST25TVTag myTag;
    FragmentManager mFragmentManager;

    EditText mEasIdEditText;
    EditText mEasTelegramEditText;

    RadioButton mEasNotProtectedRadioButton;
    RadioButton mEasPwdProtectedRadioButton;
    RadioButton mEasPermanentlyProtectedRadioButton;

    CheckBox mEnableEasCheckBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_layout);

        // Inflate content of FrameLayout
        FrameLayout frameLayout=(FrameLayout) findViewById(R.id.frame_content);
        View childView = getLayoutInflater().inflate(R.layout.fragment_st25tv_eas, null);
        frameLayout.addView(childView);

        myTag = (ST25TVTag) MainActivity.getTag();
        mHandler = new Handler();
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

        Button updateTagButton = (Button) findViewById(R.id.updateTagButton);
        updateTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configureEas();
            }
        });

        mEnableEasCheckBox = (CheckBox) findViewById(R.id.enableEasCheckBox);

        mEasNotProtectedRadioButton = (RadioButton) findViewById(R.id.easNotProtectedRadioButton);
        mEasPwdProtectedRadioButton = (RadioButton) findViewById(R.id.easPwdProtectedRadioButton);
        mEasPermanentlyProtectedRadioButton = (RadioButton) findViewById(R.id.easPermanentlyProtectedRadioButton);

        mEasPermanentlyProtectedRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ST25TVEasActivity.this);

                // set title
                alertDialogBuilder.setTitle("Warning");

                // set dialog message
                alertDialogBuilder
                        .setMessage("This permament mode is IRREVERSIBLE.\n\nIf you set this mode and want to disable the Electronic Article Surveillance feature, you will have to enable the confidential mode. The tag will not be usable anymore.")
                        .setCancelable(true)
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });

        // Set a dummy EAS_ID
        mEasIdEditText = (EditText) findViewById(R.id.easIdEditText);
        mEasIdEditText.setText("1234");

        // Set a dummy EAS_Telegram
        mEasTelegramEditText = (EditText) findViewById(R.id.easTelegramEditText);
        mEasTelegramEditText.setText("Ex: CD Beethoven 5th symphony");

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

    private void configureEas() {
        new Thread(new Runnable() {
            public void run() {
                String easTelegram = mEasTelegramEditText.getText().toString();
                int easIdValue = Integer.parseInt(mEasIdEditText.getText().toString(), 16);

                try {
                    // Write the EAD ID
                    myTag.writeEasId(easIdValue);

                    // Write the EAS Telegram
                    myTag.writeEasTelegram(easTelegram);

                    // Set the EAS password protection (if needed
                    if(mEasPwdProtectedRadioButton.isChecked()) {
                        // EAS password protected
                        myTag.writeEasSecurityConfiguration(true);
                    } else {
                        // No password protection
                        myTag.writeEasSecurityConfiguration(false);
                    }

                    // Set the EAS Lock (if needed)
                    if(mEasPermanentlyProtectedRadioButton.isChecked()) {
                        myTag.lockEas();
                    }

                    // Enable/Disable the EAS
                    if(mEnableEasCheckBox.isChecked()) {
                        // Enable EAS
                        myTag.setEas();
                    } else {
                        // Disable EAS
                        myTag.resetEas();
                    }

                    // EAS was configured without errors
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

    private void displayPasswordDialogBox() {
        Log.v(TAG, "displayPasswordDialogBox");

        // Warning: Function called from background thread! Post a request to the UI thread
        mHandler.post(new Runnable() {
            public void run() {
                STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(
                        STType5PwdDialogFragment.STPwdAction.PRESENT_CURRENT_PWD,
                        ST25TVTag.ST25TV_CONFIGURATION_PASSWORD_ID,
                        getResources().getString(R.string.enter_configuration_pwd));
                pwdDialogFragment.show(mFragmentManager, "pwdDialogFragment");
            }
        });
    }

    public void onSTType5PwdDialogFinish(int result) {
        Log.v(TAG, "onSTType5PwdDialogFinish. result = " + result);
        if (result == PwdDialogFragment.RESULT_OK) {
            // Config password has been entered successfully so we can now retry the command
            configureEas();

        } else {
            Log.e(TAG, "Action failed! Tag not updated!");
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return mMenu.selectItem(this, item);
    }
}

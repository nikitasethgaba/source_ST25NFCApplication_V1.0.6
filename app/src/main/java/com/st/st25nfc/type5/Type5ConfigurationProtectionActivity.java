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

package com.st.st25nfc.type5;

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
import android.widget.FrameLayout;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.MainActivity;
import com.st.st25nfc.generic.PwdDialogFragment;
import com.st.st25nfc.generic.STFragmentActivity;
import com.st.st25nfc.generic.STType5PwdDialogFragment;
import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.type5.ST25DV02KWTag;
import com.st.st25sdk.type5.ST25DVTag;
import com.st.st25sdk.type5.ST25TVTag;

import static com.st.st25nfc.generic.STType5PwdDialogFragment.STPwdAction.ENTER_NEW_PWD;
import static com.st.st25nfc.generic.STType5PwdDialogFragment.STPwdAction.PRESENT_CURRENT_PWD;
import static com.st.st25nfc.type5.Type5ConfigurationProtectionActivity.Action.ENTER_NEW_CONFIGURATION_PWD;
import static com.st.st25nfc.type5.Type5ConfigurationProtectionActivity.Action.LOCK_CONFIGURATION;
import static com.st.st25nfc.type5.Type5ConfigurationProtectionActivity.Action.PRESENT_CONFIGURATION_PWD;
import static com.st.st25nfc.type5.Type5ConfigurationProtectionActivity.Action.PRESENT_CURRENT_CONFIGURATION_PWD;


public class Type5ConfigurationProtectionActivity extends STFragmentActivity implements STType5PwdDialogFragment.STType5PwdDialogListener, NavigationView.OnNavigationItemSelectedListener {

    enum Action {
        PRESENT_CONFIGURATION_PWD,

        PRESENT_CURRENT_CONFIGURATION_PWD,
        ENTER_NEW_CONFIGURATION_PWD,

        LOCK_CONFIGURATION,
    };

    // Set here the Toolbar to use for this activity
    private int toolbar_res = R.menu.toolbar_empty;

    static final String TAG = "ConfigurationProtection";
    private Handler mHandler;
    private NFCTag myTag;
    FragmentManager mFragmentManager;
    private Action mCurrentAction;
    private byte mPasswordNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_layout);

        // Inflate content of FrameLayout
        FrameLayout frameLayout=(FrameLayout) findViewById(R.id.frame_content);
        View childView = getLayoutInflater().inflate(R.layout.fragment_type5_configuration_protection, null);
        frameLayout.addView(childView);

        myTag = MainActivity.getTag();
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

        Button presentConfigPwdButton = (Button) findViewById(R.id.presentConfigPwdButton);
        presentConfigPwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presentConfigPassword();
            }
        });

        Button changeConfigPwdButton = (Button) findViewById(R.id.changeConfigPwdButton);
        changeConfigPwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterCurrentConfigurationPassword();
            }
        });

        Button lockConfigButton = (Button) findViewById(R.id.lockConfigButton);
        lockConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockConfiguration();
            }
        });

        if (myTag instanceof ST25TVTag) {
            mPasswordNumber = ST25TVTag.ST25TV_CONFIGURATION_PASSWORD_ID;
        } else if (myTag instanceof ST25DVTag) {
            mPasswordNumber = ST25DVTag.ST25DV_CONFIGURATION_PASSWORD_ID;
        } else if (myTag instanceof ST25DV02KWTag) {
            mPasswordNumber = ST25DV02KWTag.ST25DV02KW_CONFIGURATION_PASSWORD_ID;
        } else {
            Log.e(TAG, "Tag not supported!");
        }
    }

    private void presentConfigPassword() {
        new Thread(new Runnable() {
            public void run() {
                Log.v(TAG, "PRESENT_CONFIGURATION_PWD");

                mCurrentAction = PRESENT_CONFIGURATION_PWD;
                STType5PwdDialogFragment.STPwdAction pwdAction = PRESENT_CURRENT_PWD;
                String message = getResources().getString(R.string.enter_configuration_pwd);

                STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(pwdAction, mPasswordNumber, message);
                pwdDialogFragment.show(mFragmentManager, "pwdDialogFragment");
            }
        }).start();
    }


    private void enterCurrentConfigurationPassword() {
        new Thread(new Runnable() {
            public void run() {
                Log.v(TAG, "PRESENT_CURRENT_CONFIGURATION_PWD");

                mCurrentAction = PRESENT_CURRENT_CONFIGURATION_PWD;
                String message = getResources().getString(R.string.enter_configuration_pwd);
                STType5PwdDialogFragment.STPwdAction action = PRESENT_CURRENT_PWD;

                STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(action, mPasswordNumber, message);
                pwdDialogFragment.show(mFragmentManager, "pwdDialogFragment");

            }
        }).start();
    }

    private void enterNewConfigurationPassword() {
        new Thread(new Runnable() {
            public void run() {
                Log.v(TAG, "ENTER_NEW_CONFIGURATION_PWD");

                mCurrentAction = ENTER_NEW_CONFIGURATION_PWD;
                String message = getResources().getString(R.string.enter_new_configuration_pwd);
                STType5PwdDialogFragment.STPwdAction action = ENTER_NEW_PWD;

                STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(action, mPasswordNumber, message);
                pwdDialogFragment.show(mFragmentManager, "pwdDialogFragment");
            }
        }).start();
    }

    private void lockConfiguration() {
        new Thread(new Runnable() {
            public void run() {
                Log.v(TAG, "LOCK_CONFIGURATION");

                mCurrentAction = LOCK_CONFIGURATION;
                String message = getResources().getString(R.string.lock_configuration_warning);
                STType5PwdDialogFragment.STPwdAction action = PRESENT_CURRENT_PWD;

                STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(action, mPasswordNumber, message);
                pwdDialogFragment.show(mFragmentManager, "pwdDialogFragment");
            }
        }).start();
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

    private void displayPasswordDialogBox() {
        Log.v(TAG, "displayPasswordDialogBox");

        // Warning: Function called from background thread! Post a request to the UI thread
        mHandler.post(new Runnable() {
            public void run() {
                STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(
                        PRESENT_CURRENT_PWD,
                        mPasswordNumber,
                        getResources().getString(R.string.enter_configuration_pwd));
                pwdDialogFragment.show(mFragmentManager, "pwdDialogFragment");
            }
        });
    }

    public void onSTType5PwdDialogFinish(int result) {
        Log.v(TAG, "onSTType5PwdDialogFinish. result = " + result);

        switch(mCurrentAction) {
            case PRESENT_CONFIGURATION_PWD:
                if (result == PwdDialogFragment.RESULT_OK) {
                    showToast(R.string.present_pwd_succeeded);
                } else {
                    Log.e(TAG, "Action failed! Tag not updated!");
                }
                break;

            case PRESENT_CURRENT_CONFIGURATION_PWD:
                if (result == PwdDialogFragment.RESULT_OK) {
                    enterNewConfigurationPassword();
                } else {
                    Log.e(TAG, "Action failed! Tag not updated!");
                }
                break;

            case ENTER_NEW_CONFIGURATION_PWD:
                if (result == PwdDialogFragment.RESULT_OK) {
                    showToast(R.string.change_pwd_succeeded);
                } else {
                    Log.e(TAG, "Action failed! Tag not updated!");
                }
                break;

            case LOCK_CONFIGURATION:
                if (result == PwdDialogFragment.RESULT_OK) {
                    changeLockConfigurationRegister();
                } else {
                    Log.e(TAG, "Action failed! Tag not updated!");
                }
                break;
        }
    }

    private void changeLockConfigurationRegister() {
        new Thread(new Runnable() {
            public void run() {
                Log.v(TAG, "changeLockConfigurationRegister");
                try {
                    if (myTag instanceof ST25TVTag) {
                        ((ST25TVTag) myTag).lockConfiguration();
                        showToast(R.string.configuration_registers_are_now_locked);
                    } else if (myTag instanceof ST25DVTag) {
                        ((ST25DVTag) myTag).lockConfiguration();
                        showToast(R.string.configuration_registers_are_now_locked);
                    }

                } catch (STException e) {
                    e.printStackTrace();
                    showToast(R.string.Command_failed);
                }
            }
        }).start();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return mMenu.selectItem(this, item);
    }
}

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
import com.st.st25sdk.type5.ST25TVTag;

import static com.st.st25nfc.type5.st25tv.ST25TVConfidentialModeActivity.Action.ENABLE_CONFIDENTIAL_MODE;
import static com.st.st25nfc.type5.st25tv.ST25TVConfidentialModeActivity.Action.ENTER_NEW_CONFIDENTIAL_MODE_PWD;


public class ST25TVConfidentialModeActivity extends STFragmentActivity implements STType5PwdDialogFragment.STType5PwdDialogListener, NavigationView.OnNavigationItemSelectedListener {

    enum Action {
        ENABLE_CONFIDENTIAL_MODE,
        ENTER_NEW_CONFIDENTIAL_MODE_PWD
    };

    // Set here the Toolbar to use for this activity
    private int toolbar_res = R.menu.toolbar_empty;

    static final String TAG = "ConfidentialMode";
    private Handler mHandler;
    private ST25TVTag myTag;
    FragmentManager mFragmentManager;
    private Action mCurrentAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_layout);

        // Inflate content of FrameLayout
        FrameLayout frameLayout=(FrameLayout) findViewById(R.id.frame_content);
        View childView = getLayoutInflater().inflate(R.layout.fragment_st25tv_confidential_mode, null);
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

        Button confidentialButton = (Button) findViewById(R.id.confidentialButton);
        confidentialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableConfidentialMode();
            }
        });

        Button changeConfidentialPasswordButton = (Button) findViewById(R.id.changeConfidentialPasswordButton);
        changeConfidentialPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterNewConfidentialModePassword();
            }
        });

    }

    private void enableConfidentialMode() {
        mCurrentAction = ENABLE_CONFIDENTIAL_MODE;

        STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(
                STType5PwdDialogFragment.STPwdAction.ENABLE_CONFIDENTIAL_MODE,
                ST25TVTag.ST25TV_CONFIDENTIAL_PASSWORD_ID,
                getResources().getString(R.string.confidential_warning));
        pwdDialogFragment.show(mFragmentManager, "pwdDialogFragment");
    }

    private void enterNewConfidentialModePassword() {
        new Thread(new Runnable() {
            public void run() {
                Log.v(TAG, "enterNewConfidentialModePassword");

                mCurrentAction = ENTER_NEW_CONFIDENTIAL_MODE_PWD;
                byte passwordNumber = ST25TVTag.ST25TV_CONFIDENTIAL_PASSWORD_ID;
                String message = getResources().getString(R.string.enter_new_confidential_mode_pwd);
                STType5PwdDialogFragment.STPwdAction action = STType5PwdDialogFragment.STPwdAction.ENTER_NEW_PWD;

                STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(action, passwordNumber, message);
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
            switch(mCurrentAction) {
                case ENABLE_CONFIDENTIAL_MODE:
                    // Confidential password has been entered successfully so the tag will now be silent
                    break;
                case ENTER_NEW_CONFIDENTIAL_MODE_PWD:
                    showToast(R.string.change_pwd_succeeded);
                    break;
            }
        } else {
            Log.e(TAG, "Action failed! Tag not updated!");
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return mMenu.selectItem(this, item);
    }
}

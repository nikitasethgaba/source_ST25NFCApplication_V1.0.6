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

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.FrameLayout;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.MainActivity;
import com.st.st25nfc.generic.STFragmentActivity;
import com.st.st25nfc.generic.STType5PwdDialogFragment;
import com.st.st25sdk.STException;
import com.st.st25sdk.command.Iso15693CustomKillCommandInterface;
import com.st.st25sdk.type5.ST25TVTag;
import com.st.st25sdk.type5.STType5PasswordInterface;

import static com.st.st25nfc.type5.st25tv.ST25TVKillTagActivity.Action.ENTER_NEW_KILL_PWD;
import static com.st.st25nfc.type5.st25tv.ST25TVKillTagActivity.Action.KILL_TAG;


public class ST25TVKillTagActivity extends STFragmentActivity implements STType5PwdDialogFragment.STType5PwdDialogListener, NavigationView.OnNavigationItemSelectedListener {

    enum Action {
        KILL_TAG,
        ENTER_NEW_KILL_PWD
    };

    // Set here the Toolbar to use for this activity
    private int toolbar_res = R.menu.toolbar_empty;

    static final String TAG = "KillActivity";
    private Handler mHandler;
    private ST25TVTag myTag;
    FragmentManager mFragmentManager;
    private Action mCurrentAction;

    private Iso15693CustomKillCommandInterface mKillCommandInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_layout);

        // Inflate content of FrameLayout
        FrameLayout frameLayout=(FrameLayout) findViewById(R.id.frame_content);
        View childView = getLayoutInflater().inflate(R.layout.fragment_st25tv_kill_tag, null);
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

        Button killButton = (Button) findViewById(R.id.killButton);
        killButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                killTag();
            }
        });

        Button changeKillPasswordButton = (Button) findViewById(R.id.changeKillPasswordButton);
        changeKillPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterNewKillPassword();
            }
        });

        Button lockKillPasswordButton = (Button) findViewById(R.id.lockKillPasswordButton);
        lockKillPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askConfirmation();
            }
        });

        // This Activity will fail if the tag doesn't implement a Iso15693CustomKillCommandInterface
        try {
            mKillCommandInterface = (Iso15693CustomKillCommandInterface) MainActivity.getTag();
        } catch (ClassCastException e) {
            Log.e(TAG, "Error! Tag not implementing Iso15693CustomKillCommandInterface!");
            return;
        }

    }

    private void killTag() {
        mCurrentAction = KILL_TAG;
        STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(
                STType5PwdDialogFragment.STPwdAction.KILL_TAG,
                ST25TVTag.ST25TV_KILL_PASSWORD_ID,
                getResources().getString(R.string.kill_warning));
        pwdDialogFragment.show(mFragmentManager, "pwdDialogFragment");
    }

    private void enterNewKillPassword() {
        new Thread(new Runnable() {
            public void run() {
                Log.v(TAG, "enterNewKillPassword");

                mCurrentAction = ENTER_NEW_KILL_PWD;
                byte passwordNumber = ST25TVTag.ST25TV_KILL_PASSWORD_ID;
                String message = getResources().getString(R.string.enter_new_kill_pwd);
                STType5PwdDialogFragment.STPwdAction action = STType5PwdDialogFragment.STPwdAction.ENTER_NEW_KILL_PWD;

                STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(action, passwordNumber, message);
                pwdDialogFragment.show(mFragmentManager, "pwdDialogFragment");
            }
        }).start();
    }

    private void askConfirmation() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Confirmation needed");

        // set dialog message
        alertDialogBuilder
                .setMessage("Do you want to lock the kill password?")
                .setCancelable(true)

                .setPositiveButton("Lock password",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                        lockKillPassword();
                    }
                })
                .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void lockKillPassword() {
        new Thread(new Runnable() {
            public void run() {
                Log.v(TAG, "lockKillPassword");

                try {
                    mKillCommandInterface.lockKill();
                    showToast(R.string.command_successful);
                } catch (STException e) {
                    e.printStackTrace();
                    showToast(R.string.command_failed);
                }

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
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return mMenu.selectItem(this, item);
    }
}

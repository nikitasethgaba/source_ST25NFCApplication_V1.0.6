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

package com.st.st25nfc.type5.stm24lr;

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
import android.widget.RadioButton;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.MainActivity;
import com.st.st25nfc.generic.PwdDialogFragment;
import com.st.st25nfc.generic.ST25Menu;
import com.st.st25nfc.generic.STFragmentActivity;
import com.st.st25nfc.generic.STType5PwdDialogFragment;
import com.st.st25sdk.type5.STType5PasswordInterface;

public class STM24LRChangePwdActivity extends STFragmentActivity implements STType5PwdDialogFragment.STType5PwdDialogListener, NavigationView.OnNavigationItemSelectedListener {

    // Set here the Toolbar to use for this activity
    private int toolbar_res = R.menu.toolbar_empty;

    static final String TAG = "ChangePwd";
    private STType5PasswordInterface myTag;
    private Handler mHandler;
    FragmentManager mFragmentManager;
    STType5PwdDialogFragment.STPwdAction mCurrentAction;

    RadioButton mPwdButton1;
    RadioButton mPwdButton2;
    RadioButton mPwdButton3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_layout);

        // Inflate content of FrameLayout
        FrameLayout frameLayout=(FrameLayout) findViewById(R.id.frame_content);
        View childView = getLayoutInflater().inflate(R.layout.fragment_m24lr_change_password, null);
        frameLayout.addView(childView);

        myTag = (STType5PasswordInterface) MainActivity.getTag();
        mHandler = new Handler();
        mFragmentManager = getSupportFragmentManager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mMenu = ST25Menu.newInstance(super.getTag());
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        mMenu.inflateMenu(navigationView);

        String password = getResources().getString(R.string.password);
        mPwdButton1 = (RadioButton) findViewById(R.id.pwdButton1);
        mPwdButton1.setChecked(true);
        mPwdButton1.setText(password + " 1");

        mPwdButton2 = (RadioButton) findViewById(R.id.pwdButton2);
        mPwdButton2.setChecked(false);
        mPwdButton2.setText(password + " 2");

        mPwdButton3 = (RadioButton) findViewById(R.id.pwdButton3);
        mPwdButton3.setChecked(false);
        mPwdButton3.setText(password + " 3");

        toolbar.setTitle(getTag().getName());

        Button updateTagButton = (Button) findViewById(R.id.updateTagButton);
        updateTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterOldPassword();
            }
        });
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

    private int getButtonChecked() {
        if (mPwdButton1.isChecked()) {
            return 1;
        }
        else if (mPwdButton2.isChecked()) {
            return 2;
        }
        else if (mPwdButton3.isChecked()) {
            return 3;
        }
        return -1;
    }

    private void enterOldPassword() {
        new Thread(new Runnable() {
            public void run() {
                String message = getResources().getString(R.string.enter_password)  + " ";

                int passwordNumber = getButtonChecked();
                if (passwordNumber < 0) {
                    Log.v(TAG, "Value not handled");
                    return;
                }

                message += passwordNumber;


                Log.v(TAG, "enterOldPassword");
                mCurrentAction = STType5PwdDialogFragment.STPwdAction.PRESENT_CURRENT_PWD;

                STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(mCurrentAction, (byte) (passwordNumber & 0xFF), message);
                pwdDialogFragment.show(mFragmentManager, "pwdDialogFragment");

            }
        }).start();
    }

    private void enterNewPassword() {
        new Thread(new Runnable() {
            public void run() {

                String message = getResources().getString(R.string.enter_new_password) + " ";
                mCurrentAction = STType5PwdDialogFragment.STPwdAction.ENTER_NEW_PWD;

                int passwordNumber = getButtonChecked();
                if (passwordNumber < 0) {
                    Log.v(TAG, "Value not handled");
                    return;
                }

                message += passwordNumber;

                STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(mCurrentAction, (byte) (passwordNumber & 0xFF), message);
                pwdDialogFragment.show(mFragmentManager, "pwdDialogFragment");
            }
        }).start();
    }

    public void onSTType5PwdDialogFinish(int result) {
        Log.v(TAG, "onSTType5PwdDialogFinish. result = " + result);
        if (result == PwdDialogFragment.RESULT_OK) {
            switch (mCurrentAction) {
                case PRESENT_CURRENT_PWD:
                    // Old password entered successfully
                    // We can now enter the new password
                    enterNewPassword();
                    break;

                case ENTER_NEW_PWD:
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

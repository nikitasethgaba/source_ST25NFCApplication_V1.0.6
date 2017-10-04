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

import android.os.Bundle;
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
import android.widget.RadioGroup;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.MainActivity;
import com.st.st25nfc.generic.PwdDialogFragment;
import com.st.st25nfc.generic.ST25Menu;
import com.st.st25nfc.generic.STFragmentActivity;
import com.st.st25nfc.generic.STType5PwdDialogFragment;
import com.st.st25sdk.MultiAreaInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterRfAiSS;
import com.st.st25sdk.type5.ST25DVTag;

import static com.st.st25sdk.MultiAreaInterface.AREA1;
import static com.st.st25sdk.type5.st25dv.ST25DVRegisterRfAiSS.ST25DVSecurityStatusPWDControl.NO_PWD_SELECTED;
import static com.st.st25sdk.type5.st25dv.ST25DVRegisterRfAiSS.ST25DVSecurityStatusPWDControl.PROTECTED_BY_PWD1;
import static com.st.st25sdk.type5.st25dv.ST25DVRegisterRfAiSS.ST25DVSecurityStatusPWDControl.PROTECTED_BY_PWD2;
import static com.st.st25sdk.type5.st25dv.ST25DVRegisterRfAiSS.ST25DVSecurityStatusPWDControl.PROTECTED_BY_PWD3;

public class ST25DVChangeAreasPasswordActivity extends STFragmentActivity implements STType5PwdDialogFragment.STType5PwdDialogListener, NavigationView.OnNavigationItemSelectedListener {

    // Set here the Toolbar to use for this activity
    private int toolbar_res = R.menu.toolbar_empty;

    static final String TAG = "ST25DVChangeAreasPwd";
    private ST25DVTag myTag;
    FragmentManager mFragmentManager;

    RadioButton mArea1RadioButton;
    RadioButton mArea2RadioButton;
    RadioButton mArea3RadioButton;
    RadioButton mArea4RadioButton;

    RadioButton mPasswordLessRadioButton;
    RadioButton mPassword1RadioButton;
    RadioButton mPassword2RadioButton;
    RadioButton mPassword3RadioButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_layout);

        // Inflate content of FrameLayout
        FrameLayout frameLayout=(FrameLayout) findViewById(R.id.frame_content);
        View childView = getLayoutInflater().inflate(R.layout.fragment_st25dv_change_areas_password, null);
        frameLayout.addView(childView);

        myTag = (ST25DVTag) MainActivity.getTag();
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

        mArea1RadioButton = (RadioButton) findViewById(R.id.area1RadioButton);
        mArea2RadioButton = (RadioButton) findViewById(R.id.area2RadioButton);
        mArea3RadioButton = (RadioButton) findViewById(R.id.area3RadioButton);
        mArea4RadioButton = (RadioButton) findViewById(R.id.area4RadioButton);
        //mArea1RadioButton.setChecked(true);


        RadioGroup areaRadioGroup = (RadioGroup) findViewById(R.id.areaRadioGroup);
        areaRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Refresh the display with the permissions of the newly selected area.
                getCurrentPermissisons();
            }
        });

        mPasswordLessRadioButton = (RadioButton) findViewById(R.id.passwordLessRadioButton);
        mPassword1RadioButton = (RadioButton) findViewById(R.id.password1RadioButton);
        mPassword2RadioButton = (RadioButton) findViewById(R.id.password2RadioButton);
        mPassword3RadioButton = (RadioButton) findViewById(R.id.password3RadioButton);

        Button updateTagButton = (Button) findViewById(R.id.updateTagButton);
        updateTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAreaPassword();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getCurrentPermissisons();
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
     * Get current permissions.
     *
     * NB: The access to register values should be done in a background thread because, if the
     * cache is not up-to-date, it will trigger a read of register value from the tag.
     */
    private void getCurrentPermissisons() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    final int numberOfAreas = myTag.getNumberOfAreas();

                    final byte area1PasswordStatus = (byte) myTag.getPasswordNumber(AREA1);
                    final byte area2PasswordStatus = (byte) myTag.getPasswordNumber(MultiAreaInterface.AREA2);
                    final byte area3PasswordStatus = (byte) myTag.getPasswordNumber(MultiAreaInterface.AREA3);
                    final byte area4PasswordStatus = (byte) myTag.getPasswordNumber(MultiAreaInterface.AREA4);

                    // Post an action to UI Thead to update the radio buttons
                    runOnUiThread(new Runnable() {
                        public void run() {

                            RadioButton mAreaRadioButton = (RadioButton) findViewById(R.id.area1RadioButton);
                            byte passwordNumber = ST25DVTag.ST25DV_PASSWORD_1;
                            for (int area = AREA1; area <= myTag.getMaxNumberOfAreas(); area++) {
                                switch(area) {
                                    case 1:
                                        mAreaRadioButton = (RadioButton) findViewById(R.id.area1RadioButton);
                                        mArea1RadioButton = mAreaRadioButton;
                                        mArea1RadioButton.setText(getResources().getString(R.string.area1) + " " + (area1PasswordStatus == 0? "(no password currently set)":"(currently protected by password: " + area1PasswordStatus + ")"));

                                        if (area<=numberOfAreas) displayAreaRadioButton(true, mAreaRadioButton);
                                        else displayAreaRadioButton(false, mAreaRadioButton);
                                        break;
                                    case 2:
                                        mAreaRadioButton = (RadioButton) findViewById(R.id.area2RadioButton);
                                        mArea2RadioButton = mAreaRadioButton;
                                        mArea2RadioButton.setText(getResources().getString(R.string.area2) + " " + (area2PasswordStatus == 0? "(no password currently set)":"(currently protected by password: " + area2PasswordStatus + ")"));

                                        if (area<=numberOfAreas) displayAreaRadioButton(true, mAreaRadioButton);
                                        else displayAreaRadioButton(false, mAreaRadioButton);
                                        break;
                                    case 3:
                                        mAreaRadioButton = (RadioButton) findViewById(R.id.area3RadioButton);
                                        mArea3RadioButton = mAreaRadioButton;
                                        mArea3RadioButton.setText(getResources().getString(R.string.area3) + " " + (area3PasswordStatus == 0? "(no password currently set)":"(currently protected by password: " + area3PasswordStatus + ")"));

                                        if (area<=numberOfAreas) displayAreaRadioButton(true, mAreaRadioButton);
                                        else displayAreaRadioButton(false, mAreaRadioButton);
                                        break;
                                    case 4:
                                        mAreaRadioButton = (RadioButton) findViewById(R.id.area4RadioButton);
                                        mArea4RadioButton = mAreaRadioButton;
                                        mArea4RadioButton.setText(getResources().getString(R.string.area4) + " " + (area4PasswordStatus == 0? "(no password currently set)":"(currently protected by password: " + area4PasswordStatus + ")"));

                                        if (area<=numberOfAreas) displayAreaRadioButton(true, mAreaRadioButton);
                                        else displayAreaRadioButton(false, mAreaRadioButton);
                                        break;
                                }

                            }



                            if (mArea1RadioButton.isChecked()) enablePasswordRadioButton(area1PasswordStatus);
                            if (mArea2RadioButton.isChecked()) enablePasswordRadioButton(area2PasswordStatus);
                            if (mArea3RadioButton.isChecked()) enablePasswordRadioButton(area3PasswordStatus);
                            if (mArea4RadioButton.isChecked()) enablePasswordRadioButton(area4PasswordStatus);



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

    private void enablePasswordRadioButton(int pwdNumber) {
        switch(pwdNumber) {
            case ST25DVTag.ST25DV_PASSWORD_1:
                mPassword1RadioButton.setChecked(true);
                break;
            case ST25DVTag.ST25DV_PASSWORD_2:
                mPassword2RadioButton.setChecked(true);
                break;
            case ST25DVTag.ST25DV_PASSWORD_3:
                mPassword3RadioButton.setChecked(true);
                break;
            default:
                mPasswordLessRadioButton.setChecked(true);
                break;
        }
    }

    private ST25DVRegisterRfAiSS.ST25DVSecurityStatusPWDControl getPasswordControlStatus(int pwdNumber) {
        ST25DVRegisterRfAiSS.ST25DVSecurityStatusPWDControl pwdControl = NO_PWD_SELECTED;
        switch(pwdNumber) {
            case ST25DVTag.ST25DV_PASSWORD_1:
                pwdControl = PROTECTED_BY_PWD1;
                break;
            case ST25DVTag.ST25DV_PASSWORD_2:
                pwdControl = PROTECTED_BY_PWD2;
                break;
            case ST25DVTag.ST25DV_PASSWORD_3:
                pwdControl = PROTECTED_BY_PWD3;
                break;
            default:
                break;
        }
        return pwdControl;
    }
    private void displayAreaRadioButton(boolean display, RadioButton rb) {
        if (!display) {
            rb.setClickable(false);
            rb.setTextColor(getResources().getColor(R.color.st_middle_grey));
        } else {
            rb.setClickable(true);
            rb.setTextColor(getResources().getColor(R.color.st_dark_blue));
        }
    }

    private void changeAreaPassword() {
        new Thread(new Runnable() {
            public void run() {
                byte areaPassword = 0;

                Log.v(TAG, "changeAreaPassword");
                if (mPasswordLessRadioButton.isChecked()) {
                    areaPassword = 0;
                }
                if (mPassword1RadioButton.isChecked()) {
                    areaPassword = ST25DVTag.ST25DV_PASSWORD_1;
                }
                if (mPassword2RadioButton.isChecked()) {
                    areaPassword = ST25DVTag.ST25DV_PASSWORD_2;
                }
                if (mPassword3RadioButton.isChecked()) {
                    areaPassword = ST25DVTag.ST25DV_PASSWORD_3;
                }

                try {
                    if (mArea1RadioButton.isChecked())   myTag.setPasswordNumber(AREA1,areaPassword);
                    if (mArea2RadioButton.isChecked())   myTag.setPasswordNumber(MultiAreaInterface.AREA2,areaPassword);
                    if (mArea3RadioButton.isChecked())   myTag.setPasswordNumber(MultiAreaInterface.AREA3,areaPassword);
                    if (mArea4RadioButton.isChecked())   myTag.setPasswordNumber(MultiAreaInterface.AREA4,areaPassword);

                    showToast(R.string.tag_updated);
                    //refresh display informations
                    getCurrentPermissisons();

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
        runOnUiThread(new Runnable() {
            public void run() {
                STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(
                        STType5PwdDialogFragment.STPwdAction.PRESENT_CURRENT_PWD,
                        ST25DVTag.ST25DV_CONFIGURATION_PASSWORD_ID,
                        getResources().getString(R.string.enter_configuration_pwd));
                pwdDialogFragment.show(mFragmentManager, "pwdDialogFragment");
            }
        });
    }

    public void onSTType5PwdDialogFinish(int result) {
        Log.v(TAG, "onSTType5PwdDialogFinish. result = " + result);
        if (result == PwdDialogFragment.RESULT_OK) {
            // Config password has been entered successfully so we can now retry to change the permission
            changeAreaPassword();
        } else {
            Log.e(TAG, "Action failed! Tag not updated!");
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return mMenu.selectItem(this, item);
    }

}

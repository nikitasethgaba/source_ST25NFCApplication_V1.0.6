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

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.MainActivity;
import com.st.st25nfc.generic.PwdDialogFragment;
import com.st.st25nfc.generic.ST25Menu;
import com.st.st25nfc.generic.STFragmentActivity;
import com.st.st25nfc.generic.STType5PwdDialogFragment;
import com.st.st25sdk.STException;
import com.st.st25sdk.TagHelper;
import com.st.st25sdk.TagHelper.ReadWriteProtection;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterRfAiSS;
import com.st.st25sdk.type5.ST25DVTag;

import static com.st.st25sdk.MultiAreaInterface.AREA1;
import static com.st.st25sdk.MultiAreaInterface.AREA2;
import static com.st.st25sdk.MultiAreaInterface.AREA3;
import static com.st.st25sdk.MultiAreaInterface.AREA4;
import static com.st.st25sdk.TagHelper.ReadWriteProtection.READABLE_AND_WRITABLE;
import static com.st.st25sdk.TagHelper.ReadWriteProtection.READABLE_AND_WRITE_PROTECTED_BY_PWD;
import static com.st.st25sdk.TagHelper.ReadWriteProtection.READ_AND_WRITE_PROTECTED_BY_PWD;
import static com.st.st25sdk.TagHelper.ReadWriteProtection.READ_PROTECTED_BY_PWD_AND_WRITE_IMPOSSIBLE;
import static com.st.st25sdk.type5.st25dv.ST25DVRegisterRfAiSS.ST25DVSecurityStatusPWDControl.NO_PWD_SELECTED;

public class ST25DVChangePermissionsActivity extends STFragmentActivity implements STType5PwdDialogFragment.STType5PwdDialogListener, NavigationView.OnNavigationItemSelectedListener {

    // Set here the Toolbar to use for this activity
    private int toolbar_res = R.menu.toolbar_empty;

    static final String TAG = "ST25DVRWActivity";
    private ST25DVTag myTag;
    FragmentManager mFragmentManager;

    RadioButton mArea1RadioButton;
    RadioButton mArea2RadioButton;
    RadioButton mArea3RadioButton;
    RadioButton mArea4RadioButton;

    RadioButton mReadableWritableRadioButton;
    RadioButton mReadableWritePWDProtectedRadioButton;
    RadioButton mReadWritePWDProtectedRadioButton;
    RadioButton mReadPWDProtectedWriteImpossibleRadioButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_layout);

        // Inflate content of FrameLayout
        FrameLayout frameLayout=(FrameLayout) findViewById(R.id.frame_content);
        View childView = getLayoutInflater().inflate(R.layout.fragment_st25dv_change_permissions, null);
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
        mArea1RadioButton.setChecked(true);

        RadioGroup areaRadioGroup = (RadioGroup) findViewById(R.id.areaRadioGroup);
        areaRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // When Area1 is selected, READ_AND_WRITE_PROTECTED_BY_PWD and READ_PROTECTED_BY_PWD_AND_WRITE_IMPOSSIBLE are disabled because
                // the read of Area1 is always possible on a ST25DV tag.
                if (mArea1RadioButton.isChecked()) {
                    mReadWritePWDProtectedRadioButton.setClickable(false);
                    mReadWritePWDProtectedRadioButton.setTextColor(getResources().getColor(R.color.st_middle_grey));

                    mReadPWDProtectedWriteImpossibleRadioButton.setClickable(false);
                    mReadPWDProtectedWriteImpossibleRadioButton.setTextColor(getResources().getColor(R.color.st_middle_grey));

                } else {
                    mReadWritePWDProtectedRadioButton.setClickable(true);
                    mReadWritePWDProtectedRadioButton.setTextColor(getResources().getColor(R.color.st_dark_blue));

                    mReadPWDProtectedWriteImpossibleRadioButton.setClickable(true);
                    mReadPWDProtectedWriteImpossibleRadioButton.setTextColor(getResources().getColor(R.color.st_dark_blue));
                }
                // Refresh the display with the permissions of the newly selected area.
                getCurrentPermissisons();
            }
        });

        mReadableWritableRadioButton = (RadioButton) findViewById(R.id.readableWritableRadioButton);
        mReadableWritePWDProtectedRadioButton = (RadioButton) findViewById(R.id.readableWritePWDProtectedRadioButton);
        mReadWritePWDProtectedRadioButton = (RadioButton) findViewById(R.id.readWritePWDProtectedRadioButton);
        mReadPWDProtectedWriteImpossibleRadioButton = (RadioButton) findViewById(R.id.readPWDProtectedWriteImpossibleRadioButton);
        mReadableWritableRadioButton.setChecked(true);

        mReadWritePWDProtectedRadioButton.setClickable(false);
        mReadWritePWDProtectedRadioButton.setTextColor(getResources().getColor(R.color.st_middle_grey));
        mReadPWDProtectedWriteImpossibleRadioButton.setClickable(false);
        mReadPWDProtectedWriteImpossibleRadioButton.setTextColor(getResources().getColor(R.color.st_middle_grey));

        Button updateTagButton = (Button) findViewById(R.id.updateTagButton);
        updateTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePermission();
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

                    final TagHelper.ReadWriteProtection area1ReadWriteProtection = myTag.getReadWriteProtection(AREA1);
                    final TagHelper.ReadWriteProtection area2ReadWriteProtection = myTag.getReadWriteProtection(AREA2);
                    final ReadWriteProtection area3ReadWriteProtection = myTag.getReadWriteProtection(AREA3);
                    final ReadWriteProtection area4ReadWriteProtection = myTag.getReadWriteProtection(AREA4);

                    // Post an action to UI Thead to update the radio buttons
                    runOnUiThread(new Runnable() {
                        public void run() {

                            RadioButton mAreaRadioButton = (RadioButton) findViewById(R.id.area1RadioButton);
                            for (int area = AREA1; area <= myTag.getMaxNumberOfAreas(); area++) {
                                switch(area) {
                                    case 1:
                                        mAreaRadioButton = (RadioButton) findViewById(R.id.area1RadioButton);
                                        mArea1RadioButton = mAreaRadioButton;

                                        if (area<=numberOfAreas) displayAreaRadioButton(true, mAreaRadioButton);
                                        else displayAreaRadioButton(false, mAreaRadioButton);
                                        break;
                                    case 2:
                                        mAreaRadioButton = (RadioButton) findViewById(R.id.area2RadioButton);
                                        mArea2RadioButton = mAreaRadioButton;

                                        if (area<=numberOfAreas) displayAreaRadioButton(true, mAreaRadioButton);
                                        else displayAreaRadioButton(false, mAreaRadioButton);
                                        break;
                                    case 3:
                                        mAreaRadioButton = (RadioButton) findViewById(R.id.area3RadioButton);
                                        mArea3RadioButton = mAreaRadioButton;

                                        if (area<=numberOfAreas) displayAreaRadioButton(true, mAreaRadioButton);
                                        else displayAreaRadioButton(false, mAreaRadioButton);
                                        break;
                                    case 4:
                                        mAreaRadioButton = (RadioButton) findViewById(R.id.area4RadioButton);
                                        mArea4RadioButton = mAreaRadioButton;

                                        if (area<=numberOfAreas) displayAreaRadioButton(true, mAreaRadioButton);
                                        else displayAreaRadioButton(false, mAreaRadioButton);
                                        break;
                                }

                            }

                            ReadWriteProtection readWriteProtection = area1ReadWriteProtection;

                            if (mArea1RadioButton.isChecked()) readWriteProtection = area1ReadWriteProtection;
                            if (mArea2RadioButton.isChecked()) readWriteProtection = area2ReadWriteProtection;
                            if (mArea3RadioButton.isChecked()) readWriteProtection = area3ReadWriteProtection;
                            if (mArea4RadioButton.isChecked()) readWriteProtection = area4ReadWriteProtection;

                            switch (readWriteProtection) {
                                case READABLE_AND_WRITABLE:
                                    mReadableWritableRadioButton.setChecked(true);
                                    break;
                                case READABLE_AND_WRITE_PROTECTED_BY_PWD:
                                    mReadableWritePWDProtectedRadioButton.setChecked(true);
                                    break;
                                case READ_AND_WRITE_PROTECTED_BY_PWD:
                                    mReadWritePWDProtectedRadioButton.setChecked(true);
                                    break;
                                case READ_PROTECTED_BY_PWD_AND_WRITE_IMPOSSIBLE:
                                    mReadPWDProtectedWriteImpossibleRadioButton.setChecked(true);
                                    break;
                            }
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

    private void displayAreaRadioButton(boolean display, RadioButton rb) {
        if (!display) {
            rb.setClickable(false);
            rb.setTextColor(getResources().getColor(R.color.st_middle_grey));
        } else {
            rb.setClickable(true);
            rb.setTextColor(getResources().getColor(R.color.st_dark_blue));
        }
    }

    private void changePermission() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Log.v(TAG, "changePermission");

                    TagHelper.ReadWriteProtection readWriteProtection = getSelectAreaSecurityStatus();
                    int area = getSelectedArea();
                    ST25DVRegisterRfAiSS rfAiSSRegister = getRFAiSSRegister(area);

                    rfAiSSRegister.setSSReadWriteProtection(readWriteProtection);

                    showToast(R.string.tag_updated);

                    ST25DVRegisterRfAiSS.ST25DVSecurityStatusPWDControl pwdNbr = rfAiSSRegister.getSSPWDControl();

                    if((readWriteProtection != READABLE_AND_WRITABLE) && (pwdNbr == NO_PWD_SELECTED)) {
                        // The area has some protections but not password has been chosen for this area. Display a warning
                        showWarningWhenNoPassword();
                    }

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

    private TagHelper.ReadWriteProtection getSelectAreaSecurityStatus() {
        TagHelper.ReadWriteProtection readWriteProtection = READABLE_AND_WRITABLE;

        if (mReadableWritableRadioButton.isChecked()) {
            readWriteProtection = READABLE_AND_WRITABLE;
        }
        if (mReadableWritePWDProtectedRadioButton.isChecked()) {
            readWriteProtection = READABLE_AND_WRITE_PROTECTED_BY_PWD;
        }
        if (mReadWritePWDProtectedRadioButton.isChecked()) {
            readWriteProtection = READ_AND_WRITE_PROTECTED_BY_PWD;
        }
        if (mReadPWDProtectedWriteImpossibleRadioButton.isChecked()) {
            readWriteProtection = READ_PROTECTED_BY_PWD_AND_WRITE_IMPOSSIBLE;
        }

        return readWriteProtection;
    }

    private int getSelectedArea() {
        int selectedArea = AREA1;

        if (mArea1RadioButton.isChecked()) {
            selectedArea = AREA1;
        } else if (mArea2RadioButton.isChecked()) {
            selectedArea = AREA2;
        } else if (mArea3RadioButton.isChecked()) {
            selectedArea = AREA3;
        } else if (mArea4RadioButton.isChecked()) {
            selectedArea = AREA4;
        }

        return selectedArea;
    }

    private ST25DVRegisterRfAiSS getRFAiSSRegister(int area) throws STException {
        ST25DVRegisterRfAiSS register;

        switch (area) {
            case AREA1:
                register = (ST25DVRegisterRfAiSS) myTag.getRegister(ST25DVTag.REGISTER_RFA1SS_ADDRESS);
                break;
            case AREA2:
                register = (ST25DVRegisterRfAiSS) myTag.getRegister(ST25DVTag.REGISTER_RFA2SS_ADDRESS);
                break;
            case AREA3:
                register = (ST25DVRegisterRfAiSS) myTag.getRegister(ST25DVTag.REGISTER_RFA3SS_ADDRESS);
                break;
            case AREA4:
                register = (ST25DVRegisterRfAiSS) myTag.getRegister(ST25DVTag.REGISTER_RFA4SS_ADDRESS);
                break;
            default:
                throw new STException(STException.STExceptionCode.BAD_PARAMETER);
        }

        return register;
    }

    private void showWarningWhenNoPassword() {
        runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ST25DVChangePermissionsActivity.this);

                // set title
                alertDialogBuilder.setTitle("No password selected");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Some protections have been set for this Area but no password number is currently selected. Do you want to select a password number now?")
                        .setCancelable(true)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                Intent intent = new Intent(ST25DVChangePermissionsActivity.this, ST25DVChangeAreasPasswordActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
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
    }

    private void displayPasswordDialogBox() {
        Log.v(TAG, "PasswordDialogBox");

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
            changePermission();
        } else {
            Log.e(TAG, "Action failed! Tag not updated!");
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return mMenu.selectItem(this, item);
    }

}

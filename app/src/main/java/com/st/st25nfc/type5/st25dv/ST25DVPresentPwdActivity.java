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
import com.st.st25sdk.MultiAreaInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.type5.ST25DVTag;

import static com.st.st25sdk.MultiAreaInterface.AREA1;

public class ST25DVPresentPwdActivity extends STFragmentActivity implements STType5PwdDialogFragment.STType5PwdDialogListener, NavigationView.OnNavigationItemSelectedListener {

    // Set here the Toolbar to use for this activity
    private int toolbar_res = R.menu.toolbar_empty;

    static final String TAG = "ST25DVPresentPwd";
    private ST25DVTag myTag;
    private Handler mHandler;
    FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_layout);

        // Inflate content of FrameLayout
        FrameLayout frameLayout=(FrameLayout) findViewById(R.id.frame_content);
        View childView = getLayoutInflater().inflate(R.layout.fragment_st25dv_present_password, null);
        frameLayout.addView(childView);

        myTag = (ST25DVTag) MainActivity.getTag();
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

        RadioButton area1PwdRadioButton = (RadioButton) findViewById(R.id.area1PwdRadioButton);
        area1PwdRadioButton.setChecked(true);

        Button updateTagButton = (Button) findViewById(R.id.updateTagButton);
        updateTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presentPassword();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        getCurrentMemoryConf();
    }

    /**
     * Get current memory configuration.
     *
     * NB: The access to register values should be done in a background thread because, if the
     * cache is not up-to-date, it will trigger a read of register value from the tag.
     */
    private void getCurrentMemoryConf() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // Retrieve the number of areas and the permissions of each one
                    final int numberOfAreas = myTag.getNumberOfAreas();

                    // Post an action to UI Thead to update the widgets
                    runOnUiThread(new Runnable() {
                        public void run() {
                            RadioButton areaPwdRadioButton = (RadioButton) findViewById(R.id.area1PwdRadioButton);
                            for (int area = AREA1; area <= myTag.getMaxNumberOfAreas(); area++) {
                                switch(area) {
                                    case 1:
                                        areaPwdRadioButton = (RadioButton) findViewById(R.id.area1PwdRadioButton);
                                        if (area<=numberOfAreas) displayAreaRadioButton(true, areaPwdRadioButton);
                                        else displayAreaRadioButton(false, areaPwdRadioButton);
                                        break;
                                    case 2:
                                        areaPwdRadioButton = (RadioButton) findViewById(R.id.area2PwdRadioButton);
                                        if (area<=numberOfAreas) displayAreaRadioButton(true, areaPwdRadioButton);
                                        else displayAreaRadioButton(false, areaPwdRadioButton);
                                        break;
                                    case 3:
                                        areaPwdRadioButton = (RadioButton) findViewById(R.id.area3PwdRadioButton);
                                        if (area<=numberOfAreas) displayAreaRadioButton(true, areaPwdRadioButton);
                                        else displayAreaRadioButton(false, areaPwdRadioButton);
                                        break;
                                    case 4:
                                        areaPwdRadioButton = (RadioButton) findViewById(R.id.area4PwdRadioButton);
                                        if (area<=numberOfAreas) displayAreaRadioButton(true, areaPwdRadioButton);
                                        else displayAreaRadioButton(false, areaPwdRadioButton);
                                        break;
                                }

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

    private void presentPassword() {
        new Thread(new Runnable() {
            public void run() {
                byte passwordNumber = ST25DVTag.ST25DV_CONFIGURATION_PASSWORD_ID;
                STType5PwdDialogFragment.STPwdAction pwdAction = STType5PwdDialogFragment.STPwdAction.PRESENT_CURRENT_PWD;
                String message = "";

                Log.v(TAG, "presentPassword");

                RadioButton area1PwdRadioButton = (RadioButton) findViewById(R.id.area1PwdRadioButton);
                if (area1PwdRadioButton.isChecked()) {
                    try {
                        passwordNumber = (byte) myTag.getPasswordNumber(AREA1);
                    } catch (STException e) {
                        e.printStackTrace();
                    }
                    message = getResources().getString(R.string.enter_area1_pwd) + " " + passwordNumber;
                }

                RadioButton area2PwdRadioButton = (RadioButton) findViewById(R.id.area2PwdRadioButton);
                if (area2PwdRadioButton.isChecked()) {
                    try {
                        passwordNumber = (byte) myTag.getPasswordNumber(MultiAreaInterface.AREA2);
                    } catch (STException e) {
                        e.printStackTrace();
                    }
                    message = getResources().getString(R.string.enter_area2_pwd)+ " " + passwordNumber;
                }
                RadioButton area3PwdRadioButton = (RadioButton) findViewById(R.id.area3PwdRadioButton);
                if (area3PwdRadioButton.isChecked()) {
                    try {
                        passwordNumber = (byte) myTag.getPasswordNumber(MultiAreaInterface.AREA3);
                    } catch (STException e) {
                        e.printStackTrace();
                    }
                    message = getResources().getString(R.string.enter_area3_pwd)+ " " + passwordNumber;
                }

                RadioButton area4PwdRadioButton = (RadioButton) findViewById(R.id.area4PwdRadioButton);
                if (area4PwdRadioButton.isChecked()) {
                    try {
                        passwordNumber = (byte) myTag.getPasswordNumber(MultiAreaInterface.AREA4);
                    } catch (STException e) {
                        e.printStackTrace();
                    }
                    message = getResources().getString(R.string.enter_area4_pwd)+ " " + passwordNumber;
                }

                STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(pwdAction, passwordNumber, message);
                pwdDialogFragment.show(mFragmentManager, "pwdDialogFragment");
            }
        }).start();
    }

    public void onSTType5PwdDialogFinish(int result) {
        Log.v(TAG, "onSTType5PwdDialogFinish. result = " + result);
        if (result == PwdDialogFragment.RESULT_OK) {
            showToast(R.string.present_pwd_succeeded);
        } else {
            Log.e(TAG, "Action failed! Tag not updated!");
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return mMenu.selectItem(this, item);
    }

}

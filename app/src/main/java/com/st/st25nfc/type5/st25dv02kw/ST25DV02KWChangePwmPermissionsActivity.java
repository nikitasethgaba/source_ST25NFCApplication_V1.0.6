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

package com.st.st25nfc.type5.st25dv02kw;

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

import com.st.st25nfc.R;
import com.st.st25nfc.generic.MainActivity;
import com.st.st25nfc.generic.PwdDialogFragment;
import com.st.st25nfc.generic.STFragmentActivity;
import com.st.st25nfc.generic.STType5PwdDialogFragment;
import com.st.st25sdk.STException;
import com.st.st25sdk.TagHelper;
import com.st.st25sdk.type5.ST25DV02KWTag;

import static com.st.st25sdk.TagHelper.ReadWriteProtection.READABLE_AND_WRITABLE;
import static com.st.st25sdk.TagHelper.ReadWriteProtection.READABLE_AND_WRITE_PROTECTED_BY_PWD;
import static com.st.st25sdk.TagHelper.ReadWriteProtection.READ_AND_WRITE_PROTECTED_BY_PWD;
import static com.st.st25sdk.TagHelper.ReadWriteProtection.READ_PROTECTED_BY_PWD_AND_WRITE_IMPOSSIBLE;

public class ST25DV02KWChangePwmPermissionsActivity extends STFragmentActivity implements STType5PwdDialogFragment.STType5PwdDialogListener, NavigationView.OnNavigationItemSelectedListener {

    // Set here the Toolbar to use for this activity
    private int toolbar_res = R.menu.toolbar_empty;

    static final String TAG = "ChangePermission";
    private ST25DV02KWTag myTag;
    FragmentManager mFragmentManager;


    RadioButton mReadableWritableRadioButton;
    RadioButton mReadableWritePWDProtectedRadioButton;
    RadioButton mReadWritePWDProtectedRadioButton;
    RadioButton mReadPWDProtectedWriteImpossibleRadioButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_layout);

        // Inflate content of FrameLayout
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_content);
        View childView = getLayoutInflater().inflate(R.layout.fragment_st25dv02kw_change_pwm_permissions, null);
        frameLayout.addView(childView);

        myTag = (ST25DV02KWTag) MainActivity.getTag();
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


        mReadableWritableRadioButton = (RadioButton) findViewById(R.id.readableWritableRadioButton);
        mReadableWritePWDProtectedRadioButton = (RadioButton) findViewById(R.id.readableWritePWDProtectedRadioButton);
        mReadWritePWDProtectedRadioButton = (RadioButton) findViewById(R.id.readWritePWDProtectedRadioButton);
        mReadPWDProtectedWriteImpossibleRadioButton = (RadioButton) findViewById(R.id.readPWDProtectedWriteImpossibleRadioButton);
        mReadableWritableRadioButton.setChecked(true);

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
     * <p>
     * NB: The access to register values should be done in a background thread because, if the
     * cache is not up-to-date, it will trigger a read of register value from the tag.
     */
    private void getCurrentPermissisons() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    final TagHelper.ReadWriteProtection readWriteProtection = myTag.getPwmCtrlAccessRights();

                    // Post an action to UI Thead to update the radio buttons
                    runOnUiThread(new Runnable() {
                        public void run() {

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

    private void changePermission() {
        new Thread(new Runnable() {
            public void run() {
                TagHelper.ReadWriteProtection readWriteProtection = READABLE_AND_WRITABLE;

                Log.v(TAG, "changePermission");

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

                try {

                    myTag.setPwmCtrlAccessRights(readWriteProtection);
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
        runOnUiThread(new Runnable() {
            public void run() {
                STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(
                        STType5PwdDialogFragment.STPwdAction.PRESENT_CURRENT_PWD,
                        ST25DV02KWTag.ST25DV02KW_CONFIGURATION_PASSWORD_ID,
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

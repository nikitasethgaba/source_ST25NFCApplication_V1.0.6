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

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.MainActivity;
import com.st.st25nfc.generic.PwdDialogFragment;
import com.st.st25nfc.generic.ST25Menu;
import com.st.st25nfc.generic.STFragmentActivity;
import com.st.st25nfc.generic.STType5PwdDialogFragment;
import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.type5.ST25DVTag;


public class ST25DVMailboxActivity extends STFragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener,  CompoundButton.OnCheckedChangeListener, STType5PwdDialogFragment.STType5PwdDialogListener {

    final static String TAG = "ST25DVMailboxActivity";
    public ST25DVTag mTag;

    private ListView mLv;
    private Handler mHandler;
    private CustomListAdapter mAdapter;

    private ST25DVMailboxAction mAction;

    private boolean mMBEnabled;
    private boolean mHostPutMsg;
    private boolean mRfPutMsg;
    private boolean mHostMissMsg;
    private boolean mRfMissMsg;

    private Switch mEnableSwitch;

    // PasswordNumber to change ST25DV's Configuration
    private final byte ST25DV_CONFIGURATION_PWD_NBR = 0;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_layout);

        // Inflate content of FrameLayout
        FrameLayout frameLayout=(FrameLayout) findViewById(R.id.frame_content);
        View childView = getLayoutInflater().inflate(R.layout.config_content_mailbox_st25dv, null);
        frameLayout.addView(childView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        mMenu = ST25Menu.newInstance(super.getTag());
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        mMenu.inflateMenu(navigationView);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mEnableSwitch = (Switch) findViewById(R.id.enable_mailbox);
        mEnableSwitch.setOnCheckedChangeListener(this);

        Button refreshStatusButton = (Button) findViewById(R.id.refreshStatusButton);
        refreshStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshStatus();
            }
        });

        Button resetButton = (Button) findViewById(R.id.resetMailboxButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetMailbox();
            }
        });

        mLv = (ListView) findViewById(R.id.config_mailbox_list_view);
        mAdapter = new CustomListAdapter();
        mHandler = new Handler();

        if (mHandler != null && mLv != null) {
            mLv.setAdapter(mAdapter);
        }

        mTag = (ST25DVTag) MainActivity.getTag();
        refreshStatus();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds read_list_items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_empty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long

        // as you specify a parent activity in AndroidManifest.xml.


        return super.onOptionsItemSelected(item);
    }

    public void onSTType5PwdDialogFinish(int result) {
        Log.v(TAG, "onSTType5PwdDialogFinish. result = " + result);
        if (result == PwdDialogFragment.RESULT_OK) {
            switch (mAction) {
                case ENABLE:
                    mAction = ST25DVMailboxAction.ENABLE;
                    fillView(mAction);
                    break;
            }
            // Config password has been entered successfully so we can now retry to change the permission
            //changePermission();
        } else {
            Log.e(TAG, "Action failed! Tag not updated!");
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return mMenu.selectItem(this, item);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if(!mMBEnabled) {
                mAction = ST25DVMailboxAction.ENABLE;
                fillView(mAction);
            }
        } else {
            if(mMBEnabled) {
                mAction = ST25DVMailboxAction.DISABLE;
                fillView(mAction);
            }
        }
    }

    public void refreshStatus() {
        mAction = ST25DVMailboxAction.REFRESH;
        fillView(mAction);
    }

    public void resetMailbox() {
        fillView(ST25DVMailboxAction.RESET);
    }

    public void disableMailbox() {
        mAction = ST25DVMailboxAction.DISABLE;
        fillView(mAction);
    }

    private enum ST25DVMailboxAction{ INIT,
        REFRESH,
        ENABLE,
        DISABLE,
        RESET };

    class ContentView implements Runnable {


        ST25DVMailboxAction mAction;

        private void displayPasswordDialogBox() {
            Log.v(TAG, "displayPasswordDialogBox");

            mHandler.post(new Runnable() {
                public void run() {
                    FragmentManager fragmentManager = getSupportFragmentManager();

                    STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(
                            STType5PwdDialogFragment.STPwdAction.PRESENT_CURRENT_PWD,
                            ST25DV_CONFIGURATION_PWD_NBR,
                            getResources().getString(R.string.enter_configuration_pwd));
                    pwdDialogFragment.show(fragmentManager, "pwdDialogFragment");
                }
            });
        }

        private void updateMBStatus() throws STException {
            mMBEnabled = mTag.isMBEnabled(false);
            mHostPutMsg = mTag.hasHostPutMsg(false);
            mHostMissMsg = mTag.hasHostMissMsg(false);
            mRfPutMsg = mTag.hasRFPutMsg(false);
            mRfMissMsg = mTag.hasRFMissMsg(false);

            runOnUiThread(new Runnable() {
                public void run() {
                    if(mEnableSwitch.isChecked() != mMBEnabled) {
                        mEnableSwitch.setChecked(mMBEnabled);
                    }
                }
            });

        }

        public ContentView(ST25DVMailboxAction action) {
            mAction = action;

        }

        public void run() {

            if (mTag != null) {
                switch (mAction) {
                    case REFRESH:
                        try {
                            mTag.refreshMBStatus();
                            updateMBStatus();
                        }
                        catch (STException e) {
                            //to do error message
                        }
                        break;
                    case ENABLE:
                        try {
                            mTag.enableMB();
                            mTag.refreshMBStatus();
                            updateMBStatus();
                        }
                        catch (STException e) {
                            switch (e.getError()) {
                                case TAG_NOT_IN_THE_FIELD:
                                    showToast(R.string.tag_not_in_the_field);
                                    break;
                                case CONFIG_PASSWORD_NEEDED:
                                    displayPasswordDialogBox();
                                    return;
                                    //to do error message;
                            }
                        }
                        break;
                    case DISABLE:
                        try {
                            mTag.disableMB();
                            mHostMissMsg = mMBEnabled = mHostPutMsg =
                                    mRfPutMsg = mRfMissMsg = false;
                        }
                        catch (STException e) {
                            //to do error message
                        }
                        break;
                    case RESET:
                        try {
                            mTag.resetMB();
                            mTag.refreshMBStatus();
                        }
                        catch (STException e) {
                            //to do error message
                        }
                        break;
                    default:
                        break;
                }
            }

            mHandler.post(new Runnable() {
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
                });
            }
    }

    public void fillView(ST25DVMailboxAction action) {

        new Thread(new ContentView(action)).start();
    }

    public NFCTag getTag() {
        return mTag;
    }

    class CustomListAdapter extends BaseAdapter {


        public CustomListAdapter() {

        }

        //get read_list_items count
        @Override
        public int getCount() {
            return 5;
        }

        //get read_list_items position
        @Override
        public Object getItem(int position) {
            return position;
        }

        //get read_list_items id at selected position
        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {


            View listItem = convertView;

            if (listItem == null) {
                //set the main ListView's layout
                listItem = getLayoutInflater().inflate(R.layout.config_mailbox_items, parent, false);
            }


            TextView title = (TextView) listItem.findViewById(R.id.title);
            TextView description = (TextView) listItem.findViewById(R.id.description);

            Drawable contentImage = null;

            switch (pos) {
                case 0:
                    title.setText(R.string.Mailbox_enabled);
                    if (mMBEnabled)
                        contentImage = getResources().getDrawable(R.drawable.st_green_circle);
                    else
                        contentImage = getResources().getDrawable(R.drawable.st_orange_circle);
                    break;
                case 1:
                    title.setText(R.string.HostPutMsg);
                    if (mHostPutMsg)
                        contentImage = getResources().getDrawable(R.drawable.st_green_circle);
                    else
                        contentImage = getResources().getDrawable(R.drawable.st_orange_circle);
                    break;
                case 2:
                    title.setText(R.string.HostMissMsg);
                    if (mHostMissMsg)
                        contentImage = getResources().getDrawable(R.drawable.st_green_circle);
                    else
                        contentImage = getResources().getDrawable(R.drawable.st_orange_circle);
                    break;
                case 3:
                    title.setText(R.string.RFPutMsg);
                    if (mRfPutMsg)
                        contentImage = getResources().getDrawable(R.drawable.st_green_circle);
                    else
                        contentImage = getResources().getDrawable(R.drawable.st_orange_circle);
                    break;
                case 4:
                    title.setText(R.string.RFMissMsg);
                    if (mRfMissMsg)
                        contentImage = getResources().getDrawable(R.drawable.st_green_circle);
                    else
                        contentImage = getResources().getDrawable(R.drawable.st_orange_circle);
                    break;

            }


            ImageView image = (ImageView) listItem.findViewById(R.id.thumb);
            image.setImageDrawable(contentImage);

            return listItem;
        }
    }

}


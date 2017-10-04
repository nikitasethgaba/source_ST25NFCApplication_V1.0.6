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

package com.st.st25nfc.generic;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.st.st25sdk.NFCTag;
import com.st.st25sdk.type5.STType5PasswordInterface;
import com.st.st25nfc.R;
import com.st.st25sdk.RegisterInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.STRegister;

import java.util.ArrayList;
import java.util.List;

import static com.st.st25nfc.generic.RegistersActivity.ActionStatus.*;


public class RegistersActivity extends STFragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener, STType5PwdDialogFragment.STType5PwdDialogListener {

    public static final String USE_DYNAMIC_REGISTER = "USE_DYN_REGISTER";

    final static String TAG = "ST25RegistersActivity";
    private NFCTag mTag;
    private RegisterInterface mRegisterInterface;

    private ListView mListView;
    private Handler mHandler;
    private CustomListAdapter mAdapter;
    private TextView mStatusTextView;

    private boolean mForDynamicRegister;
    private final byte ST25_CONFIGURATION_PWD_NBR = 0;

    // Local class used to store the information about each register
    class RegisterInfo {
        STRegister register;

        // Boolean indicating if the user has typed a new value in the TextView.
        boolean isValueUpdated;

        // 'value' is meaningful only if isValueUpdated==true.
        int value;

        public RegisterInfo(STRegister register) {
            this.register = register;
            this.isValueUpdated = false;
            this.value = 0;
        }
    }

    enum ActionStatus {
        ACTION_SUCCESSFUL,
        ACTION_FAILED,
        TAG_NOT_IN_THE_FIELD
    };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_layout);

        // Inflate content of FrameLayout
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_content);
        View childView = getLayoutInflater().inflate(R.layout.config_registers_activity, null);
        frameLayout.addView(childView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.register_list);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        mMenu.inflateMenu(navigationView);


        mForDynamicRegister = false;
        Bundle b = getIntent().getExtras();
        if(b != null) {
            mForDynamicRegister = b.getBoolean(RegistersActivity.USE_DYNAMIC_REGISTER)==true?true:false;
        }

        mTag = MainActivity.getTag();

        try {
            mRegisterInterface = (RegisterInterface) mTag;
            if (mRegisterInterface.getDynamicRegisterList() == null && mForDynamicRegister) {
                throw new STException("Error! Tag not implementing Dynamic RegisterInterface!");
            }
        } catch (ClassCastException e) {
            // Tag not implementing RegisterInterface
            Log.e(TAG, "Error! Tag not implementing RegisterInterface!");
            return;
        } catch (STException e) {
            Log.e(TAG, e.getMessage());
            return;
        }

        mListView = (ListView) findViewById(R.id.config_register_list_view);
        mListView.setItemsCanFocus(true);

        // Create an empty register list
        List<RegisterInfo> st25RegisterList = new ArrayList<RegisterInfo>();
        mAdapter = new CustomListAdapter(st25RegisterList);

        mHandler = new Handler();

        if (mHandler != null && mListView != null) {
            mListView.setAdapter(mAdapter);
        }

        mStatusTextView = (TextView) findViewById(R.id.statusTextView);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mAdapter.getCount() == 0) {
            refreshRegisterList();
        }
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
        getMenuInflater().inflate(R.menu.toolbar_registers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                writeRegisters();
                return true;

            case R.id.action_refresh:
                refreshRegisterList();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshRegisterList() {
        // Registers should not be read in UI Thread context. We use an AsyncTask to read them.
        Log.d(TAG, "refreshRegisterList");
        new readRegisterValues().execute();
    }

    private void writeRegisters() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Log.d(TAG, "writeRegisters");

                    List<RegisterInfo> registerList = mAdapter.getData();
                    int nbrOfRegistersWritten = 0;

                    for(int registerNbr=0; registerNbr<registerList.size(); registerNbr++) {
                        RegisterInfo registerInfo = registerList.get(registerNbr);

                        if(registerInfo.isValueUpdated) {
                            // User has entered a  new value for this register. Write it to the tag
                            registerInfo.register.setRegisterValue(registerInfo.value);
                            nbrOfRegistersWritten++;
                        }
                    }

                    if(nbrOfRegistersWritten > 0) {
                        showToast(R.string.register_action_completed);

                        // Registers have been written successfully. Go through all the registers and reset "isValueUpdated"
                        for (RegisterInfo registerInfo : registerList) {
                            registerInfo.isValueUpdated = false;
                        }

                        // Refresh the display of the register list
                        refreshRegisterList();

                    } else {
                        showToast(R.string.no_register_to_update);
                    }

                } catch (STException e) {
                    switch (e.getError()) {
                        case CONFIG_PASSWORD_NEEDED:
                            displayPasswordDialogBox();
                            break;

                        case TAG_NOT_IN_THE_FIELD:
                            showToast(R.string.tag_not_in_the_field);
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

        final byte passworNumber;
        try {
            STType5PasswordInterface STType5PasswordInterface = (STType5PasswordInterface) mTag;
            passworNumber = STType5PasswordInterface.getConfigurationPasswordNumber();
        } catch (STException e) {
            e.printStackTrace();
            return;
        }

        // Warning: Function called from background thread! Post a request to the UI thread
        runOnUiThread(new Runnable() {
            public void run() {
                FragmentManager fragmentManager = getSupportFragmentManager();

                STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(
                        STType5PwdDialogFragment.STPwdAction.PRESENT_CURRENT_PWD,
                        passworNumber,
                        getResources().getString(R.string.enter_configuration_pwd));
                pwdDialogFragment.show(fragmentManager, "pwdDialogFragment");
            }
        });

    }

    private int getRegisterValueTypedByUser(int position) {
        Log.d(TAG, "getRegisterValueTypedByUser: " + position);

        View view = mListView.getChildAt(position);

        EditText registerValueEditText = (EditText) view.findViewById(R.id.registerValueEditText);
        String text = registerValueEditText.getText().toString();

        int value = 0;
        if(!text.isEmpty()) {
            value = Integer.parseInt(text, 16);
        }

        return value;
    }

    public void onSTType5PwdDialogFinish(int result) {
        Log.v(TAG, "onSTType5PwdDialogFinish. result = " + result);
        if (result == PwdDialogFragment.RESULT_OK) {
            // Config password has been entered successfully so we can now retry to write the register values
            writeRegisters();

        } else {
            Log.e(TAG, "Action failed! Tag not updated!");
            showToast(R.string.register_action_not_completed);;

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


    static class ViewHolder{
        public TextView mRegisterNameTextView;
        public ImageView mRegisterRightImageView;
        public EditText mRegisterValueEditText;
        public TextView mRegisterDescriptionTextView;

        // Custom watcher used to activate or not the watching of EditText modification
        public MutableWatcher mWatcher;
    }

    class MutableWatcher implements TextWatcher {

        private int mPosition;
        private boolean mActive;
        private EditText mRegisterValueEditText;

        public MutableWatcher(EditText registerValueEditText) {
            this.mRegisterValueEditText = registerValueEditText;
        }

        void setPosition(int position) {
            mPosition = position;
        }

        void setActive(boolean active) {
            mActive = active;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            if (mActive) {
                int newValue;

                String valueEntered = s.toString();
                if (valueEntered != null && !valueEntered.isEmpty()) {
                    newValue = Integer.parseInt(valueEntered, 16);
                } else {
                    newValue = 0;
                }

                List<RegisterInfo> registerList = mAdapter.getData();
                RegisterInfo registerInfo = registerList.get(mPosition);
                int currentValue = 0;
                try {
                    currentValue = registerInfo.register.getRegisterValue();
                } catch (STException e) {
                    e.printStackTrace();
                }

                if(newValue != currentValue) {
                    // Value changed
                    mRegisterValueEditText.setBackgroundColor(getResources().getColor(R.color.light_red));
                    registerInfo.isValueUpdated = true;
                    registerInfo.value = newValue;
                } else {
                    // Value not changed
                    mRegisterValueEditText.setBackgroundColor(getResources().getColor(R.color.light_blue));
                    registerInfo.isValueUpdated = false;
                }
            }
        }
    }


    class CustomListAdapter extends BaseAdapter {
        List<RegisterInfo> mRegisterList;

        public CustomListAdapter(List<RegisterInfo> registerList) {
            mRegisterList = registerList;
        }

        public List<RegisterInfo> getData() {
            return mRegisterList;
        }

        @Override
        public int getCount() {
            return mRegisterList.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {

            try {

                ViewHolder holder = null;
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.config_registers_items, parent, false);

                    holder = new ViewHolder();

                    holder.mRegisterNameTextView = (TextView) convertView.findViewById(R.id.registerNameTextView);
                    holder.mRegisterRightImageView  = (ImageView) convertView.findViewById(R.id.registerRightImageView);
                    holder.mRegisterDescriptionTextView = (TextView) convertView.findViewById(R.id.registerDescriptionTextView);
                    holder.mRegisterValueEditText = (EditText) convertView.findViewById(R.id.registerValueEditText);

                    holder.mWatcher = new MutableWatcher(holder.mRegisterValueEditText);
                    holder.mRegisterValueEditText.addTextChangedListener(holder.mWatcher);

                    // Warning: This is not a NFC Tag but a simple 'tag' used to retrieve this ViewHolder
                    convertView.setTag(holder);
                } else{
                    holder = (ViewHolder) convertView.getTag();
                }


                RegisterInfo registerInfo = mRegisterList.get(position);

                if (registerInfo.register.getRegisterAccessRights() == STRegister.RegisterAccessRights.REGISTER_READ_ONLY) {
                    holder.mRegisterValueEditText.setFocusable(false);
                    holder.mRegisterValueEditText.setClickable(false);
                    holder.mRegisterRightImageView.setImageResource(R.drawable.read_only);
                } else {
                    holder.mRegisterValueEditText.setFocusable(true);
                    holder.mRegisterValueEditText.setClickable(true);
                    holder.mRegisterRightImageView.setImageResource(R.drawable.read_write);
                }

                String registerName = "#" + position + " " + registerInfo.register.getRegisterName();
                holder.mRegisterNameTextView.setText(registerName);
                holder.mRegisterDescriptionTextView.setText(registerInfo.register.getRegisterContentDescription());

                String registerValueStr;
                int registerValue = registerInfo.register.getRegisterValue();
                if(registerInfo.isValueUpdated) {
                    registerValue = registerInfo.value;
                }

                switch (registerInfo.register.getRegisterDataSize()) {
                    default:
                    case REGISTER_DATA_ON_8_BITS:
                        registerValueStr = String.format("%02x", registerValue).toUpperCase();
                        break;
                    case REGISTER_DATA_ON_16_BITS:
                        registerValueStr = String.format("%04x", registerValue).toUpperCase();
                        break;
                }
                holder.mWatcher.setActive(true);
                holder.mWatcher.setPosition(position);
                holder.mRegisterValueEditText.setText(registerValueStr);

                return convertView;

            } catch (STException e) {
                e.printStackTrace();
                // TODO
                return null;
            }
        }
    }

    private class readRegisterValues extends AsyncTask<Void, Void, ActionStatus> {

        List<RegisterInfo> mRegisterInfoList = new ArrayList<RegisterInfo>();

        public readRegisterValues() {

        }

        @Override
        protected ActionStatus doInBackground(Void... param) {
            ActionStatus result;

            Log.d(TAG, "readRegisterValues");

            // Read all the registers in order to be sure that their value is present in cache
            List<STRegister> registerList;
            if (mForDynamicRegister) {
                registerList = mRegisterInterface.getDynamicRegisterList();
            } else {
                registerList = mRegisterInterface.getRegisterList();
            }

            try {
                for (STRegister register : registerList) {
                    // Read the register value so that it gets present in cache
                    register.getRegisterValue();

                    // Create a new entry in mRegisterInfoList
                    RegisterInfo registerInfo = new RegisterInfo(register);
                    mRegisterInfoList.add(registerInfo);
                }
                result = ACTION_SUCCESSFUL;

            } catch (STException e) {
                switch (e.getError()) {
                    case TAG_NOT_IN_THE_FIELD:
                        result = TAG_NOT_IN_THE_FIELD;
                        break;

                    default:
                        e.printStackTrace();
                        result = ACTION_FAILED;
                        break;
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(ActionStatus actionStatus) {

            switch(actionStatus) {
                case ACTION_SUCCESSFUL:
                    Log.d(TAG, "readRegisterValues: ACTION_SUCCESSFUL");

                    // No status message to display
                    mStatusTextView.setVisibility(View.GONE);

                    // update data in our adapter
                    mAdapter.getData().clear();
                    mAdapter.getData().addAll(mRegisterInfoList);

                    mAdapter.notifyDataSetChanged();
                    break;

                case ACTION_FAILED:
                    showToast("Error while reading the register values!");
                    mStatusTextView.setText(R.string.error_while_reading_the_tag);
                    mStatusTextView.setVisibility(View.VISIBLE);
                    break;

                case TAG_NOT_IN_THE_FIELD:
                    mStatusTextView.setText(R.string.tag_not_in_the_field);
                    mStatusTextView.setVisibility(View.VISIBLE);
                    break;
            }

            return;
        }
    }

}


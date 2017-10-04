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

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.MainActivity;
import com.st.st25nfc.generic.PwdDialogFragment;
import com.st.st25nfc.generic.STFragmentActivity;
import com.st.st25nfc.generic.STType5PwdDialogFragment;
import com.st.st25sdk.STException;
import com.st.st25sdk.command.Iso15693Command;
import com.st.st25sdk.type5.Type5Tag;


public class Type5LockBlockActivity extends STFragmentActivity implements STType5PwdDialogFragment.STType5PwdDialogListener, NavigationView.OnNavigationItemSelectedListener {

    // Set here the Toolbar to use for this activity
    private int toolbar_res = R.menu.toolbar_empty;

    // Block security status as defined in ISO15693-3
    private byte LOCK_FLAG = 0x01;

    static final String TAG = "LockBlockActivity";
    private Handler mHandler;
    private Type5Tag myTag;
    FragmentManager mFragmentManager;

    // Block status
    public enum ST25TVBlockStatus {
        STATUS_UNKNOWN,
        BLOCK_UNLOCKED,
        BLOCK_LOCKED
    }
    private ST25TVBlockStatus mBlockStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_layout);

        // Inflate content of FrameLayout
        FrameLayout frameLayout=(FrameLayout) findViewById(R.id.frame_content);
        View childView = getLayoutInflater().inflate(R.layout.fragment_lock_block, null);
        frameLayout.addView(childView);

        myTag = (Type5Tag) MainActivity.getTag();
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

        Button checkblockStatusButton = (Button) findViewById(R.id.checkblockStatusButton);
        checkblockStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBlockStatus();
            }
        });

        Button lockButton = (Button) findViewById(R.id.lockButton);
        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askConfirmationBeforeLocking();
            }
        });

        EditText blockNbrEditText = (EditText) findViewById(R.id.blockNbrEditText);
        blockNbrEditText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                setBlockStatus(ST25TVBlockStatus.STATUS_UNKNOWN);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        setBlockStatus(ST25TVBlockStatus.STATUS_UNKNOWN);
    }

    private void checkBlockStatus() {
        new Thread(new Runnable() {
            public void run() {
                byte[] readBlockAnswer = null;
                // OPTION_FLAG is necessary to get the "Block security status" from the readSingleBlock command
                byte flag = Iso15693Command.OPTION_FLAG |
                            Iso15693Command.HIGH_DATA_RATE_MODE |
                            Iso15693Command.ADDRESSED_MODE;

                mBlockStatus = ST25TVBlockStatus.STATUS_UNKNOWN;

                EditText blockNbrEditText = (EditText) findViewById(R.id.blockNbrEditText);
                int blockNbr = Integer.parseInt(blockNbrEditText.getText().toString());

                Log.v(TAG, "checkBlockStatus for block " + blockNbr);

                try {
                    if(blockNbr < 256) {
                        readBlockAnswer = myTag.readSingleBlock((byte) blockNbr, flag);
                    } else {
                        Byte MSB = (byte) ((blockNbr & 0xFF00) >> 8);
                        Byte LSB = (byte) (blockNbr & 0xFF);
                        // MSB should be at index 0
                        byte[] blockAddress = new byte[]{MSB, LSB};
                        readBlockAnswer = myTag.extendedReadSingleBlock(blockAddress, flag);
                    }
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

                if ( (readBlockAnswer != null) && (readBlockAnswer.length == 6) && (readBlockAnswer[0] == 0) )  {
                    // The command was successful

                    // readBlockAnswer[1] contains the "Block security status"
                    if( (readBlockAnswer[1] & LOCK_FLAG) == LOCK_FLAG) {
                        setBlockStatus(ST25TVBlockStatus.BLOCK_LOCKED);
                    } else {
                        setBlockStatus(ST25TVBlockStatus.BLOCK_UNLOCKED);
                    }
                }

            }
        }).start();
    }

    /**
     * Update the display of the block status
     */
    private void setBlockStatus(ST25TVBlockStatus newBlockStatus) {
        // Warning: This function might be called from background thread! Post a request to the UI thread
        mBlockStatus = newBlockStatus;

        mHandler.post(new Runnable() {
            public void run() {
                TextView blockStatusTextView = (TextView) findViewById(R.id.blockStatusTextView);
                switch(mBlockStatus) {
                    case STATUS_UNKNOWN:
                        blockStatusTextView.setText(R.string.unknown);
                        break;
                    case BLOCK_LOCKED:
                        blockStatusTextView.setText(R.string.locked);
                        break;
                    case BLOCK_UNLOCKED:
                        blockStatusTextView.setText(R.string.unlocked);
                        break;
                }
            }
        });
    }

    private void askConfirmationBeforeLocking() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Type5LockBlockActivity.this);

        // set title
        alertDialogBuilder.setTitle("Confirmation needed");

        // set dialog message
        alertDialogBuilder
                .setMessage("WARNING! This action is IRREVERSIBLE. Are you sure that you want to lock this block?")
                .setCancelable(true)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                        lockBlock();
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

    private void lockBlock() {
        new Thread(new Runnable() {
            public void run() {
                EditText blockNbrEditText = (EditText) findViewById(R.id.blockNbrEditText);
                int blockNbr = Integer.parseInt(blockNbrEditText.getText().toString());

                Log.v(TAG, "LockBlock " + blockNbr);

                try {
                    byte result;

                    if(blockNbr < 256) {
                        result = myTag.lockSingleBlock((byte) blockNbr);
                    } else {
                        Byte MSB = (byte) ((blockNbr & 0xFF00) >> 8);
                        Byte LSB = (byte) (blockNbr & 0xFF);
                        // MSB should be at index 0
                        byte[] blockAddress = new byte[]{MSB, LSB};
                        result = myTag.extendedLockSingleBlock(blockAddress);
                    }

                    if(result == 0x0) {
                        // Command successful
                        showToast(R.string.block_locked);
                        setBlockStatus(ST25TVBlockStatus.BLOCK_LOCKED);
                    } else {
                        showToast(R.string.error_while_updating_the_tag);
                    }

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
                /* TODO when password management per Area will be done
                STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(
                        STType5PwdDialogFragment.STPwdAction.PRESENT_CURRENT_PWD,
                        ST25TVTag.ST25TV_CONFIGURATION_PASSWORD_ID,
                        getResources().getString(R.string.enter_configuration_pwd));
                pwdDialogFragment.show(mFragmentManager, "pwdDialogFragment");
                */
            }
        });
    }

    public void onSTType5PwdDialogFinish(int result) {
        Log.v(TAG, "onSTType5PwdDialogFinish. result = " + result);
        if (result == PwdDialogFragment.RESULT_OK) {
            // Area password has been entered successfully so we can now retry the command
            checkBlockStatus();
        } else {
            Log.e(TAG, "Action failed! Tag not updated!");
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return mMenu.selectItem(this, item);
    }
}

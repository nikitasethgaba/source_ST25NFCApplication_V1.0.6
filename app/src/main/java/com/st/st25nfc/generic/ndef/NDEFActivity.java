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

package com.st.st25nfc.generic.ndef;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.MainActivity;
import com.st.st25nfc.generic.PwdDialogFragment;
import com.st.st25nfc.generic.STFragment;
import com.st.st25nfc.generic.STFragmentActivity;
import com.st.st25nfc.generic.STType5PwdDialogFragment;
import com.st.st25nfc.generic.util.UIHelper;
import com.st.st25sdk.MultiAreaInterface;
import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.ndef.AarRecord;
import com.st.st25sdk.ndef.BtLeRecord;
import com.st.st25sdk.ndef.BtRecord;
import com.st.st25sdk.ndef.EmailRecord;
import com.st.st25sdk.ndef.EmptyRecord;
import com.st.st25sdk.ndef.ExternalRecord;
import com.st.st25sdk.ndef.MimeRecord;
import com.st.st25sdk.ndef.NDEFMsg;
import com.st.st25sdk.ndef.NDEFRecord;
import com.st.st25sdk.ndef.SmsRecord;
import com.st.st25sdk.ndef.TextRecord;
import com.st.st25sdk.ndef.UriRecord;
import com.st.st25sdk.ndef.VCardRecord;
import com.st.st25sdk.ndef.WifiRecord;
import com.st.st25sdk.type4a.ControlTlv;
import com.st.st25sdk.type4a.STType4PasswordInterface;
import com.st.st25sdk.type4a.STType4Tag;
import com.st.st25sdk.type5.CCFile;
import com.st.st25sdk.type5.STType5PasswordInterface;
import com.st.st25sdk.type5.Type5Tag;

import static com.st.st25nfc.generic.ndef.NDEFActivity.ActionStatus.NDEF_MESSAGE_TOO_BIG;
import static com.st.st25nfc.generic.ndef.NDEFActivity.ActionStatus.TAG_NOT_IN_THE_FIELD;


/**
 * Activity used to display the content of one NDEF Record.
 * A fragment corresponding to the record type is instantiated.
 */
public class NDEFActivity extends STFragmentActivity implements NavigationView.OnNavigationItemSelectedListener,
                                                                STFragment.STFragmentListener,
                                                                View.OnClickListener,
                                                                STType5PwdDialogFragment.STType5PwdDialogListener,
                                                                PwdDialogFragment.PwdDialogListener {

    enum ActionStatus {
        ACTION_SUCCESSFUL,
        ACTION_FAILED,
        TAG_WRITE_PROTECTED,
        TAG_NOT_IN_THE_FIELD,
        NDEF_MESSAGE_TOO_BIG
    };

    // Set here the Toolbar to use for this activity
    private int toolbar_res = R.menu.toolbar_ndef_editor;

    final static String TAG = "NDEFActivity";

    private NDEFRecordFragment mFragment;
    FragmentManager mFragmentManager;

    private NFCTag myTag = null;
    private int mArea;

    private NDEFMsg mNdefMsg;
    private NDEFRecord mNDEFRecord;
    private int mRecordNbr;

    private int mAction;

    private boolean mIsAreaProtectedInWrite;
    private byte[] mReadPassword;
    private byte[] mWritePassword;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        myTag = MainActivity.getTag();

        setContentView(R.layout.default_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        Bundle bundle = getIntent().getExtras();
        if(bundle == null) {
            Log.e(TAG, "Fatal error! No data passed to this fragment!");
            return;
        }

        mAction = bundle.getInt(NDEFEditorFragment.EditorKey);
        mNdefMsg = (NDEFMsg) bundle.getSerializable(NDEFRecordFragment.NDEFKey);
        mArea = bundle.getInt("area_nbr", -1);
        mIsAreaProtectedInWrite = false;

        if(mArea == -1) {
            Log.e(TAG, "Fatal error! Invalid Area!");
            return;
        }

        // The intent should contain:
        // - The area where this NDEF is located.
        // - The action currently performed (EDIT_NDEF_RECORD or ADD_NDEF_RECORD).
        // - The NDEFMsg
        // - the record number of the record to edit

        switch(mAction) {
            case NDEFEditorFragment.VIEW_NDEF_RECORD:
            case NDEFEditorFragment.ADD_NDEF_RECORD:
                if(mNdefMsg == null) {
                    Log.e(TAG, "Fatal error! No NDEF passed to this fragment!");
                    return;
                }

                mRecordNbr = bundle.getInt(NDEFRecordFragment.RecordNbrKey);
                mNDEFRecord = mNdefMsg.getNDEFRecord(mRecordNbr);
                break;

            default:
                Log.e(TAG, "Fatal error! Invalid action!");
                return;
        }

        Context context = getApplicationContext();

        if (mNDEFRecord instanceof TextRecord) {
            mFragment = NDEFTextFragment.newInstance(context);
        } else if (mNDEFRecord instanceof UriRecord) {
            mFragment = NDEFUriFragment.newInstance(context);
        } else if (mNDEFRecord instanceof SmsRecord) {
            mFragment = NDEFSmsFragment.newInstance(context);
        } else if (mNDEFRecord instanceof EmailRecord) {
            mFragment = NDEFEmailFragment.newInstance(context);
        } else if (mNDEFRecord instanceof VCardRecord) {
            mFragment = NDEFVCardFragment.newInstance(context);
        } else if (mNDEFRecord instanceof WifiRecord) {
            mFragment = NDEFWifiFragment.newInstance(context);
        } else if (mNDEFRecord instanceof BtRecord) {
            mFragment = NDEFBtFragment.newInstance(context);
        } else if (mNDEFRecord instanceof BtLeRecord) {
            mFragment = NDEFBtLeFragment.newInstance(context);
        } else if (mNDEFRecord instanceof AarRecord) {
            mFragment = NDEFAarFragment.newInstance(context);
        } else if (mNDEFRecord instanceof MimeRecord) {
            mFragment = NDEFMimeFragment.newInstance(context);
        } else if (mNDEFRecord instanceof ExternalRecord) {
            mFragment = NDEFExternalFragment.newInstance(context);
        } else if (mNDEFRecord instanceof EmptyRecord) {
            mFragment = NDEFEmptyFragment.newInstance(context);
        } else {
            Log.e(TAG, "Fatal error! Non supported record type!");
            return;
        }

        mFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frame_content, mFragment);
        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        mMenu.inflateMenu(navigationView);

        toolbar.setTitle(getTag().getName());
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            switch(mAction) {
                case NDEFEditorFragment.EDIT_NDEF_RECORD:
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NDEFActivity.this);

                    // set title
                    alertDialogBuilder.setTitle("Confirmation needed");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Data not written to the tag. Are you sure that you want to leave the edition mode?")
                            .setCancelable(true)
                            .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // Go back to VIEW_NDEF_RECORD mode
                                    changeActionMode(NDEFEditorFragment.VIEW_NDEF_RECORD);
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
                    break;

                default:
                    finish();
                    NDEFActivity.this.finish();
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds read_list_items to the action bar if it is present.
        getMenuInflater().inflate(toolbar_res, menu);
        updateMenuItemsVisibility(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_edit:
                // Switch to edition mode
                changeActionMode(NDEFEditorFragment.EDIT_NDEF_RECORD);
                return true;

            case R.id.action_save:
                mFragment.updateContent();
                writeNdefMessage();
                return true;

            case R.id.action_delete:
                if(mAction == NDEFEditorFragment.ADD_NDEF_RECORD) {
                    // This NDEF was not yet written to the tag so we just have to cancel the current activity
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_CANCELED, intent);
                    finish();
                } else {
                    // mAction = EDIT_NDEF_RECORD
                    // Ask confirmation before deleting this NDEF record on the tag
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NDEFActivity.this);

                    // set title
                    alertDialogBuilder.setTitle("Confirmation needed");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Are you sure you want to delete this record?")
                            .setCancelable(true)
                            .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    mAction = NDEFEditorFragment.DELETE_NDEF_RECORD;
                                    mNdefMsg.deleteRecord(mRecordNbr);
                                    writeNdefMessage();
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
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void changeActionMode(int newActionMode) {
        mAction = newActionMode;

        // Recreate option menu
        invalidateOptionsMenu();

        if(mAction == NDEFEditorFragment.EDIT_NDEF_RECORD) {
            mFragment.ndefRecordEditable(true);
        } else {
            mFragment.ndefRecordEditable(false);
        }
    }
    /**
     * Change visibility of menu item icons depending of the request code
     */
    protected void updateMenuItemsVisibility(Menu menu) {

        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        MenuItem editItem = menu.findItem(R.id.action_edit);
        MenuItem saveItem = menu.findItem(R.id.action_save);

        if( (deleteItem == null) || (editItem == null) || (saveItem == null) ) {
            return;
        }

        switch(mAction) {
            case NDEFEditorFragment.ADD_NDEF_RECORD:
                deleteItem.setVisible(false);
                editItem.setVisible(false);
                saveItem.setVisible(true);
                break;

            case NDEFEditorFragment.VIEW_NDEF_RECORD:
                deleteItem.setVisible(true);
                editItem.setVisible(true);
                saveItem.setVisible(false);
                break;

            case NDEFEditorFragment.EDIT_NDEF_RECORD:
                deleteItem.setVisible(false);
                editItem.setVisible(false);
                saveItem.setVisible(true);
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        return mMenu.selectItem(this, item);
    }

    @Override
    public void onClick(View v) {
        Snackbar snackbar = Snackbar.make(v, "", Snackbar.LENGTH_LONG);

        snackbar.setAction("Reading from", this);
        snackbar.setActionTextColor(getResources().getColor(R.color.white));

        snackbar.show();
    }

    private void writeNdefMessage() {
        new AsyncTaskWriteNdefMessage().execute();
    }

    private void showWritePasswordDialog() {

        if(UIHelper.isAType5Tag(myTag)) {
            new AsyncTaskDisplayPasswordDialogBoxForType5Tag().execute();

        } else if(UIHelper.isAType4Tag(myTag)) {
            new AsyncTaskDisplayPasswordDialogBoxForType4Tag().execute();

        } else {
            // Tag type not supported yet
        }
    }

    private void showNdefTooBigAlert() {
        int memoryAreaSizeInBytes = getMemoryAreaSizeInBytes();
        // Length of the bytes containing the "type" and "length" of the TLV block containing the NDEF
        int tlSize = 2;
        int terminatorTlvLength = 1;

        int ndefMsgLength = 0;
        try {
            ndefMsgLength = mNdefMsg.getLength();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int ndefSizeInBytes = ndefMsgLength + tlSize + terminatorTlvLength;

        String message = "NDEF message too big for this Area.";

        if(memoryAreaSizeInBytes != 0) {
            if (myTag instanceof STType4Tag) {
                message += "\n\n";
                message += "NDEF size: " + ndefSizeInBytes + " Bytes.\n";
                message += "Max File Size: " + memoryAreaSizeInBytes + " Bytes.\n";

            } else if (myTag instanceof Type5Tag) {
                int ccfileLength = CCFile.getExpectedCCFileLength(memoryAreaSizeInBytes);
                int totalLength = ndefSizeInBytes + ccfileLength;

                message += "\n\n";
                message += "NDEF + CCFile size: " + totalLength + " Bytes.\n";
                message += "Max Area Size: " + memoryAreaSizeInBytes + " Bytes.\n";
            }
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NDEFActivity.this);

        // set title
        alertDialogBuilder.setTitle("Error!");

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Continue",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private int getMemoryAreaSizeInBytes() {
        int memoryAreaSizeInBytes = 0;

        try {
            if (myTag instanceof STType4Tag) {

                int fileId = UIHelper.getType4FileIdFromArea(mArea);
                ControlTlv controlTlv = ((STType4Tag) myTag).getCCFileTlv(fileId);

                memoryAreaSizeInBytes = controlTlv.getMaxFileSize();

            } else {
                if (myTag instanceof MultiAreaInterface) {
                    memoryAreaSizeInBytes = ((MultiAreaInterface) myTag).getAreaSizeInBytes(mArea);
                } else {
                    memoryAreaSizeInBytes = myTag.getMemSizeInBytes();
                }
            }
        } catch (STException e) {
            e.printStackTrace();
        }

        return memoryAreaSizeInBytes;
    }

    @Override
    public void onSTType5PwdDialogFinish(int result) {
        Log.v(TAG, "onSTType5PwdDialogFinish. result = " + result);

        if (result == PwdDialogFragment.RESULT_OK) {
            // Area Password has been entered successfully

            // The password was requested because the area is protected in write
            // The area is now unlocked so we can try again to write the NDEF data
            writeNdefMessage();

        } else {
            Log.e(TAG, "Failed to unlock the area!");
        }
    }

    @Override
    public void onPwdDialogFinish(int result, byte[] password) {
        if (result == PwdDialogFragment.RESULT_OK) {
            // The write password is now known and will be provided when writting the NDEF message
            writeNdefMessage();

        } else {
            Log.e(TAG, "Failed to retrieve the write password!");
        }
    }

    /**
     * AsyncTask writing the NDEFMsg to the tag
     */
    private class AsyncTaskWriteNdefMessage extends AsyncTask<Void, Void, ActionStatus> {

        public AsyncTaskWriteNdefMessage() {
        }

        @Override
        protected ActionStatus doInBackground(Void... param) {
            ActionStatus result = ActionStatus.ACTION_FAILED;

            try {
                if (myTag instanceof STType4Tag) {
                    int fileId = UIHelper.getType4FileIdFromArea(mArea);

                    if(mIsAreaProtectedInWrite) {
                        ((STType4Tag) myTag).writeNdefMessage(fileId, mNdefMsg, mWritePassword);
                    } else {
                        ((STType4Tag) myTag).writeNdefMessage(fileId, mNdefMsg);
                    }
                } else {
                    if (myTag instanceof MultiAreaInterface) {
                        ((MultiAreaInterface) myTag).writeNdefMessage(mArea, mNdefMsg);
                    } else {
                        myTag.writeNdefMessage(mNdefMsg);
                    }
                }
                result = ActionStatus.ACTION_SUCCESSFUL;

            } catch (STException e) {
                switch (e.getError()) {
                    case NDEF_MESSAGE_TOO_BIG:
                        result = NDEF_MESSAGE_TOO_BIG;
                        break;

                    case WRONG_SECURITY_STATUS:
                    case ISO15693_BLOCK_PROTECTED:
                    case ISO15693_BLOCK_IS_LOCKED:
                        result = ActionStatus.TAG_WRITE_PROTECTED;
                        mIsAreaProtectedInWrite = true;
                        break;

                    case TAG_NOT_IN_THE_FIELD:
                        result = TAG_NOT_IN_THE_FIELD;
                        break;

                    default:
                        e.printStackTrace();
                        break;
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(ActionStatus actionStatus) {

            switch(actionStatus) {
                case NDEF_MESSAGE_TOO_BIG:
                    showNdefTooBigAlert();
                    break;

                case ACTION_SUCCESSFUL:
                    showToast(R.string.tag_updated);
                    NDEFActivity.this.finish();
                    break;

                case TAG_WRITE_PROTECTED:
                    showWritePasswordDialog();
                    break;

                case TAG_NOT_IN_THE_FIELD:
                    showToast(R.string.tag_not_in_the_field);
                    break;

                case ACTION_FAILED:
                default:
                    showToast(R.string.command_failed);
                    break;
            }
        }
    }

    /**
     * AsyncTask retrieving the passwordNumber corresponding to an area of a Type5 tag.
     * When the password number is available, a password dialog box is displayed.
     */
    private class AsyncTaskDisplayPasswordDialogBoxForType5Tag extends AsyncTask<Void, Void, ActionStatus> {

        byte mPasswordNumber;

        public AsyncTaskDisplayPasswordDialogBoxForType5Tag() {

        }

        @Override
        protected ActionStatus doInBackground(Void... param) {
            ActionStatus result = ActionStatus.ACTION_FAILED;

            if (myTag instanceof STType5PasswordInterface) {
                try {
                    mPasswordNumber = ((STType5PasswordInterface) myTag).getPasswordNumber(mArea);
                    result = ActionStatus.ACTION_SUCCESSFUL;

                } catch (STException e) {
                    e.printStackTrace();
                }
            } else {
                if (myTag instanceof STType5PasswordInterface) {
                    // TODO:
                    // The tag implements STType5PasswordInterface but doesn't implement MultiAreaInterface.
                    // This is the case for instance of Vicinity tags.
                    // I don't know how to retrieve the password number in that case!
                    Log.e(TAG, "Error! Not implemented yet!");

                } else {
                    Log.e(TAG, "Error! This tag doesn't have a password interface!");
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(ActionStatus actionStatus) {

            switch(actionStatus) {
                case ACTION_SUCCESSFUL:
                    String dialogMsg = String.format(getResources().getString(R.string.enter_area_pwd), UIHelper.getAreaName(mArea));

                    FragmentManager fragmentManager = getSupportFragmentManager();

                    STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(
                            STType5PwdDialogFragment.STPwdAction.PRESENT_CURRENT_PWD,
                            mPasswordNumber,
                            dialogMsg,
                            NDEFActivity.this);
                    pwdDialogFragment.show(fragmentManager, "pwdDialogFragment");
                    break;

                case ACTION_FAILED:
                    showToast(R.string.error_while_reading_the_tag);
                    break;

                case TAG_NOT_IN_THE_FIELD:
                    showToast(R.string.tag_not_in_the_field);
                    break;
            }

            return;
        }
    }

    private class AsyncTaskDisplayPasswordDialogBoxForType4Tag extends AsyncTask<Void, Void, ActionStatus> {

        public AsyncTaskDisplayPasswordDialogBoxForType4Tag() {

        }

        @Override
        protected ActionStatus doInBackground(Void... param) {
            ActionStatus result = ActionStatus.ACTION_FAILED;

            try {
                // Request write password
                PwdDialogFragment pwdDialogFragment = PwdDialogFragment.newInstance(getString(R.string.enter_write_password),
                        mFragmentManager,
                        NDEFActivity.this,
                        ((STType4PasswordInterface) myTag).getWritePasswordLengthInBytes(mArea));
                mWritePassword = pwdDialogFragment.getPassword();
            }
            catch (STException e) {
                return result;
            }

            result = ActionStatus.ACTION_SUCCESSFUL;
            return result;
        }
    }

}


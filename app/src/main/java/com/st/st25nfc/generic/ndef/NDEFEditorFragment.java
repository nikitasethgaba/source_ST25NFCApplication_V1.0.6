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

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.CloningActivity;
import com.st.st25nfc.generic.PwdDialogFragment;
import com.st.st25nfc.generic.STFragment;
import com.st.st25nfc.generic.STFragmentActivity;
import com.st.st25nfc.generic.STHeaderFragment;
import com.st.st25nfc.generic.STType5PwdDialogFragment;
import com.st.st25nfc.generic.util.UIHelper;
import com.st.st25sdk.MultiAreaInterface;
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
import com.st.st25sdk.type4a.STType4PasswordInterface;
import com.st.st25sdk.type4a.STType4Tag;
import com.st.st25sdk.type5.STType5PasswordInterface;

import java.util.concurrent.Semaphore;

import static com.st.st25sdk.MultiAreaInterface.AREA1;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NDEFEditorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NDEFEditorFragment extends STFragment implements AdapterView.OnItemClickListener, View.OnClickListener, STType5PwdDialogFragment.STType5PwdDialogListener, PwdDialogFragment.PwdDialogListener {


    enum WriteStatus {
        ERROR_WHILE_WRITING_THE_DATA,
        NDEF_WRITTEN,
        AREA_WRITE_PROTECTED
    };

    enum ActionStatus {
        ACTION_SUCCESSFUL,
        ACTION_FAILED,
        TAG_NOT_IN_THE_FIELD,
        AREA_PASSWORD_NEEDED
    };

    final static String TAG = "NDEFEditorFragment";

    public static final int INVALID_NDEF_ACTION = 0;

    public static final int ADD_NDEF_RECORD = 1;
    public static final int VIEW_NDEF_RECORD = 2;
    public static final int EDIT_NDEF_RECORD = 3;
    public static final int DELETE_NDEF_RECORD = 4;

    public static final int NDEF_RECORD_ADDED = 10;
    public static final int NDEF_RECORD_UPDATED = 11;
    public static final int NDEF_RECORD_DELETED = 12;

    public static final String EditorKey = "action";

    // NDEF contained in this Area
    protected NDEFMsg mCurrentNdefMsg;

    protected int mArea;

    private ListView mLv;
    private BaseAdapter mAdapter;
    private AlertDialog mAlertDialog;

    private boolean mIsAreaProtectedInRead;

    private Semaphore mLock = new Semaphore(1);

    private byte[] mReadPassword;




    public NDEFEditorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment NDEFEditorFragment.
     */
    public static NDEFEditorFragment newInstance(Context context) {
        NDEFEditorFragment fragment = new NDEFEditorFragment();

        // Set the title of this fragment
        fragment.setTitle(context.getResources().getString(R.string.ndef));

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_ndef_edition, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new NdefMsgAdapter();

        mLv = (ListView) getActivity().findViewById(R.id.list_view);
        mLv.setOnItemClickListener((AdapterView.OnItemClickListener) mAdapter);
        mLv.setAdapter(mAdapter);

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            mArea = intent.getIntExtra("area_nbr", AREA1);
        } else {
            mArea = AREA1;
        }

        // Option menu with cloning button is only available for Area1
        if(mArea == AREA1) {
            setHasOptionsMenu(true);
        } else {
            setHasOptionsMenu(false);
        }

        // mCurrentNdefMsg will be filled by readNdefContent()
        mCurrentNdefMsg = null;
        mIsAreaProtectedInRead = false;

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(this);

        TextView messageTextView = (TextView) getActivity().findViewById(R.id.messageTextView);
        messageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsAreaProtectedInRead) {
                    showReadPasswordDialog();
                }
            }

        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_cloning, menu);
        MenuItem item = menu.findItem(R.id.action_cloning);

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_cloning:
                        if(mCurrentNdefMsg == null) {
                            showToast("Cloning not possible: No valid NDEF message set!");
                        } else {
                            askCloningConfirmation();
                        }
                        break;
                }
                return false;
            }

        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void askCloningConfirmation() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // set title
        alertDialogBuilder.setTitle("Confirmation needed");

        // set dialog message
        alertDialogBuilder
                .setMessage("Do you want to clone this NDEF to some other tags?")
                .setCancelable(true)

                .setPositiveButton("Clone NDEF content",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                        startCloningActivity();
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

    /**
     *  Go to CloningActivity and flush the activity stack history.
     *  The intent should contain the NDEF message to clone.
     */
    private void startCloningActivity() {
        if(mCurrentNdefMsg == null) {
            Log.e(TAG, "Invalid NDEF!");
            return;
        }

        Intent intent = new Intent(getActivity(), CloningActivity.class);
        intent.putExtra("NDEF", mCurrentNdefMsg);

        // Set the flags to flush the activity stack history
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }


    private void showReadPasswordDialog() {

        if(UIHelper.isAType5Tag(myTag)) {
            new AsyncTaskDisplayPasswordDialogBoxForType5Tag().execute();

        } else if(UIHelper.isAType4Tag(myTag)) {
            new AsyncTaskDisplayPasswordDialogBoxForType4Tag(this).execute();

        } else {
            // Tag type not supported yet
        }
    }

    @Override
    public void onPwdDialogFinish(int result, byte[] password) {
        if (password != null)
            onSTType5PwdDialogFinish(result);
    }

    @Override
    public void onSTType5PwdDialogFinish(int result) {
        Log.v(TAG, "onSTType5PwdDialogFinish. result = " + result);
        if (result == PwdDialogFragment.RESULT_OK) {
            // Area Password has been entered successfully

            if(mIsAreaProtectedInRead) {
                // The password was requested because the area is protected in read
                // The area is now unlocked so we can refresh the display
                readNdefContent();
            }

        } else {
            Log.e(TAG, "Failed to unlock the area!");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (STFragmentActivity.tagChanged(getActivity(), myTag))
            return;

        // Refresh content
        readNdefContent();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


    }

    private void setHeaderContent() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        STHeaderFragment fragment = new STHeaderFragment();
        fragmentTransaction.replace(R.id.ndef_header_fragment_container, fragment);
        fragmentTransaction.commit();

        displayOnGoingRetrievalInformation();
    }

    private void displayOnGoingRetrievalInformation() {
        RelativeLayout tagInformationRetrieval = (RelativeLayout) mView.findViewById(R.id.XX_file_retrieval_ongoing);
        tagInformationRetrieval.setVisibility(View.VISIBLE);
    }

    private void hideOnGoingRetrievalInformation() {
        RelativeLayout tagInformationRetrieval = (RelativeLayout) mView.findViewById(R.id.XX_file_retrieval_ongoing);
        tagInformationRetrieval.setVisibility(View.GONE);
    }

    private void readNdefContent() {

        setHeaderContent();

        new AsyncTaskReadNdefMessage().execute();
    }

    /**
     * AsyncTask reading the NDEFMsg from the tag
     */
    private class AsyncTaskReadNdefMessage extends AsyncTask<Void, Void, ActionStatus> {

        NDEFMsg mNdefMsgRead;

        public AsyncTaskReadNdefMessage() {
        }

        @Override
        protected ActionStatus doInBackground(Void... param) {
            ActionStatus result = ActionStatus.ACTION_FAILED;

            try {
                mLock.acquire();
            }
            catch (InterruptedException e) {}

            try {
                if (myTag instanceof STType4Tag) {
                    int fileId = UIHelper.getType4FileIdFromArea(mArea);

                    if(mIsAreaProtectedInRead) {
                        mNdefMsgRead = ((STType4Tag) myTag).readNdefMessage(fileId, mReadPassword);
                    } else {
                        mNdefMsgRead = ((STType4Tag) myTag).readNdefMessage(fileId);
                    }

                } else {
                    if (myTag instanceof MultiAreaInterface) {
                        mNdefMsgRead = ((MultiAreaInterface) myTag).readNdefMessage(mArea);
                    } else {
                        mNdefMsgRead = myTag.readNdefMessage();
                    }
                }

                result = ActionStatus.ACTION_SUCCESSFUL;
                mIsAreaProtectedInRead = false;

            } catch (STException e) {
                switch (e.getError()) {
                    case INVALID_CCFILE:
                    case INVALID_NDEF_DATA:
                        // This area doesn't contain a valid CCFile or NDEF but read done successfully
                        result = ActionStatus.ACTION_SUCCESSFUL;
                        mIsAreaProtectedInRead = false;
                        break;

                    case ISO15693_BLOCK_PROTECTED:
                    case WRONG_SECURITY_STATUS:
                        result = ActionStatus.AREA_PASSWORD_NEEDED;
                        mIsAreaProtectedInRead = true;
                        break;

                    case TAG_NOT_IN_THE_FIELD:
                        result = ActionStatus.TAG_NOT_IN_THE_FIELD;
                        break;

                    default:
                        showToast("Error [" + e.getMessage() + "] while reading the content of " + UIHelper.getAreaName(mArea));
                        e.printStackTrace();
                        break;
                }
            }

            mLock.release();

            return result;
        }

        @Override
        protected void onPostExecute(ActionStatus actionStatus) {

            switch(actionStatus) {
                case ACTION_SUCCESSFUL:
                    mCurrentNdefMsg = mNdefMsgRead;

                    mAdapter.notifyDataSetChanged();

                    // Refresh the display of the number of records
                    setTitleMsg();
                    break;

                case AREA_PASSWORD_NEEDED:
                    mAdapter.notifyDataSetChanged();

                    // Refresh the display of the number of records
                    setTitleMsg();
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

    public void setTitleMsg() {
        TextView titleTextView = (TextView)mView.findViewById(R.id.messageTextView);
        String txt;
        int nbrOfRecords = 0;

        hideOnGoingRetrievalInformation();

        if (mCurrentNdefMsg != null) {

            int ndefMsgLength = 0;
            try {
                ndefMsgLength = mCurrentNdefMsg.getLength();
            } catch (Exception e) {
                e.printStackTrace();
            }

            nbrOfRecords = mCurrentNdefMsg.getNDEFRecords().size();
            txt = UIHelper.getAreaName(mArea) + " : NDEF message containing " + nbrOfRecords + " record(s).\n" +
                    "NDEF size: " + ndefMsgLength + " Bytes";
        } else {
            if(mIsAreaProtectedInRead) {
                txt = UIHelper.getAreaName(mArea) + " : " + mView.getContext().getString(R.string.area_protected_in_read_click_here_to_unlock);
            } else {
                if(mArea == 1) {
                    txt = UIHelper.getAreaName(mArea) + " : " + mView.getContext().getString(R.string.no_ndef_data_or_unknown_data);
                } else {
                    txt = UIHelper.getAreaName(mArea) + " : " + mView.getContext().getString(R.string.unknown_data);
                }
            }
        }

        titleTextView.setText(txt);
    }

    @Override
    public void onClick(View v) {
        if(mIsAreaProtectedInRead) {
            showToast("Area should be unlocked first");
            return;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());

        // set title
        alertDialogBuilder.setTitle("Select the NDEF record to add");

        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.add_ndef_record, null);
        alertDialogBuilder.setView(dialogView);

        ListView ndefListView = (ListView) dialogView.findViewById(R.id.ndef_list_view);
        NdefListAdapter adapter = new NdefListAdapter();
        ndefListView.setAdapter(adapter);
        ndefListView.setOnItemClickListener(adapter);

        // create alert dialog
        mAlertDialog = alertDialogBuilder.create();
        // show it
        mAlertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    class NdefMsgAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

        public NdefMsgAdapter() {
            //mLv.setOnItemClickListener(this);
        }

        //get read_list_items count
        @Override
        public int getCount() {
            if (mCurrentNdefMsg != null)
                return mCurrentNdefMsg.getNDEFRecords().size();
            else
                return 0;
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
                listItem = getActivity().getLayoutInflater().inflate(R.layout.ndef_items, parent, false);
            }

            TextView title = (TextView) listItem.findViewById(R.id.title);
            TextView description = (TextView) listItem.findViewById(R.id.description);


            Drawable contentImage = null;
            int type;
            if (mCurrentNdefMsg != null && mCurrentNdefMsg.getNDEFRecords().size() != 0 && pos<mCurrentNdefMsg.getNDEFRecords().size()) {

                NDEFRecord record = mCurrentNdefMsg.getNDEFRecord(pos);

                if (record instanceof TextRecord) {
                    contentImage = getResources().getDrawable(R.drawable.ic_message_st_light_blue_24dp);
                    title.setText(R.string.text_record);
                    description.setText(R.string.edit_text_record);

                } else if (record instanceof UriRecord) {
                    contentImage = getResources().getDrawable(R.drawable.ic_web_st_light_blue_24dp);
                    title.setText(R.string.Uri_record);
                    description.setText(R.string.edit_uri_record);

                } else if (record instanceof SmsRecord) {
                    contentImage = getResources().getDrawable(R.drawable.ic_textsms_st_light_blue_24dp);
                    title.setText(R.string.Sms_record);
                    description.setText(R.string.edit_sms_record);

                } else if (record instanceof EmailRecord) {
                    contentImage = getResources().getDrawable(R.drawable.ic_email_st_light_blue_24dp);
                    title.setText(R.string.Email_record);
                    description.setText(R.string.edit_email_record);

                } else if (record instanceof VCardRecord) {
                    contentImage = getResources().getDrawable(R.drawable.ic_contacts_st_light_blue_24dp);
                    title.setText(R.string.Contact_record);
                    description.setText(R.string.edit_contact_record);

                } else if (record instanceof WifiRecord) {
                    contentImage = getResources().getDrawable(R.drawable.ic_signal_wifi_2_bar_st_light_blue_24dp);
                    title.setText(R.string.Wifi_record);
                    description.setText(R.string.edit_wifi_record);

                } else if (record instanceof BtRecord) {
                    contentImage = getResources().getDrawable(R.drawable.ic_bluetooth_st_light_blue_24dp);
                    title.setText(R.string.Bluetooth_record);
                    description.setText(R.string.edit_bluetooth_record);

                } else if (record instanceof BtLeRecord) {
                    contentImage = getResources().getDrawable(R.drawable.ic_bluetooth_st_light_blue_24dp);
                    title.setText(R.string.BluetoothLe_record);
                    description.setText(R.string.edit_bluetooth_le_record);

                } else if (record instanceof AarRecord) {
                    contentImage = getResources().getDrawable(R.drawable.ic_android_light_blue_24dp);
                    title.setText(R.string.Aar_record);
                    description.setText(R.string.edit_aar_record);

                } else if (record instanceof MimeRecord) {
                    contentImage = getResources().getDrawable(R.drawable.ic_layers_light_blue_24dp);
                    title.setText(R.string.mime_record);
                    description.setText(R.string.edit_mime_record);

                } else if (record instanceof ExternalRecord) {
                    contentImage = getResources().getDrawable(R.drawable.ic_receipt_light_blue_24dp);
                    title.setText(R.string.external_record);
                    description.setText(R.string.edit_external_record);

                } else if (record instanceof EmptyRecord) {
                    contentImage = getResources().getDrawable(R.drawable.ic_hourglass_empty_light_blue_24dp);
                    title.setText(R.string.empty_record);
                    description.setText(R.string.edit_empty_record);

                } else {
                    Log.e(TAG, "Fatal error! Non supported record type!");
                    return null;
                }
            }

            ImageView image = (ImageView) listItem.findViewById(R.id.thumb);
            image.setImageDrawable(contentImage);

            return listItem;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Intent intent = new Intent(getActivity(), NDEFActivity.class);

            int action = NDEFEditorFragment.VIEW_NDEF_RECORD;
            intent.putExtra(NDEFRecordFragment.NDEFKey, mCurrentNdefMsg);
            intent.putExtra(NDEFRecordFragment.RecordNbrKey, position);
            intent.putExtra(NDEFEditorFragment.EditorKey, action);
            intent.putExtra("area_nbr", mArea);

            startActivityForResult(intent, action);
        }
    }

    /**
     * Manage a popup displaying every kind of NDEF Record that can be added
     */
    class NdefListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

        // List of NDEF Records that can be added
        final int NDEF_EDITOR_SMS_RECORD = 0;
        final int NDEF_EDITOR_EMAIL_RECORD = 1;
        final int NDEF_EDITOR_TEXT_RECORD = 2;
        final int NDEF_EDITOR_URI_RECORD = 3;
        final int NDEF_EDITOR_VCARD_RECORD = 4;
        final int NDEF_EDITOR_WIFI_RECORD = 5;
        final int NDEF_EDITOR_BT_RECORD = 6;
        final int NDEF_EDITOR_BTLE_RECORD = 7;
        final int NDEF_EDITOR_AAR_RECORD = 8;
        final int NDEF_EDITOR_MIME_RECORD = 9;
        final int NDEF_EDITOR_EXTERNAL_RECORD = 10;
        final int NDEF_EDITOR_EMPTY_RECORD = 11;

        final int NDEF_EDITOR_NBR_OF_RECORD_TYPES = 12;


        public NdefListAdapter() {
            //mLv.setOnItemClickListener(this);
        }

        //get read_list_items count
        @Override
        public int getCount() {
            return NDEF_EDITOR_NBR_OF_RECORD_TYPES;
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

            if (listItem == null)
                //set the main ListView's layout
                listItem = getActivity().getLayoutInflater().inflate(R.layout.ndef_items, parent, false);

            TextView title = (TextView) listItem.findViewById(R.id.title);
            TextView description = (TextView) listItem.findViewById(R.id.description);

            Drawable contentImage = null;

            switch (pos) {
                case NDEF_EDITOR_SMS_RECORD:
                    title.setText(R.string.Sms_record);
                    description.setText(R.string.create_sms_record);
                    contentImage = getResources().getDrawable(R.drawable.ic_textsms_st_light_blue_24dp);
                    break;
                case NDEF_EDITOR_TEXT_RECORD:
                    title.setText(R.string.text_record);
                    description.setText(R.string.create_text_record);
                    contentImage = getResources().getDrawable(R.drawable.ic_message_st_light_blue_24dp);
                    break;
                case NDEF_EDITOR_URI_RECORD:
                    title.setText(R.string.Uri_record);
                    description.setText(R.string.create_uri_record);
                    contentImage = getResources().getDrawable(R.drawable.ic_web_st_light_blue_24dp);
                    break;
                case NDEF_EDITOR_EMAIL_RECORD:
                    title.setText(R.string.Email_record);
                    description.setText(R.string.create_email_record);
                    contentImage = getResources().getDrawable(R.drawable.ic_email_st_light_blue_24dp);
                    break;
                case NDEF_EDITOR_VCARD_RECORD:
                    title.setText(R.string.Contact_record);
                    description.setText(R.string.create_contact_record);
                    contentImage = getResources().getDrawable(R.drawable.ic_contacts_st_light_blue_24dp);
                    break;
                case NDEF_EDITOR_WIFI_RECORD:
                    title.setText(R.string.Wifi_record);
                    description.setText(R.string.create_wifi_record);
                    contentImage = getResources().getDrawable(R.drawable.ic_signal_wifi_2_bar_st_light_blue_24dp);
                    break;
                case NDEF_EDITOR_BT_RECORD:
                    title.setText(R.string.Bluetooth_record);
                    description.setText(R.string.create_bluetooth_record);
                    contentImage = getResources().getDrawable(R.drawable.ic_bluetooth_st_light_blue_24dp);
                    break;
                case NDEF_EDITOR_BTLE_RECORD:
                    title.setText(R.string.BluetoothLe_record);
                    description.setText(R.string.create_bluetooth_record_le);
                    contentImage = getResources().getDrawable(R.drawable.ic_bluetooth_st_light_blue_24dp);
                    break;
                case NDEF_EDITOR_AAR_RECORD:
                    title.setText(R.string.Aar_record);
                    description.setText(R.string.create_aar_record);
                    contentImage = getResources().getDrawable(R.drawable.ic_android_light_blue_24dp);
                    break;
                case NDEF_EDITOR_MIME_RECORD:
                    title.setText(R.string.mime_record);
                    description.setText(R.string.create_mime_record);
                    contentImage = getResources().getDrawable(R.drawable.ic_layers_light_blue_24dp);
                    break;
                case NDEF_EDITOR_EXTERNAL_RECORD:
                    title.setText(R.string.external_record);
                    description.setText(R.string.create_external_record);
                    contentImage = getResources().getDrawable(R.drawable.ic_receipt_light_blue_24dp);
                    break;
                case NDEF_EDITOR_EMPTY_RECORD:
                    title.setText(R.string.empty_record);
                    description.setText(R.string.create_empty_record);
                    contentImage = getResources().getDrawable(R.drawable.ic_hourglass_empty_light_blue_24dp);
                    break;
                default:
                    Log.e(TAG, "Invalid position!");
                    break;
            }

            ImageView image = (ImageView) listItem.findViewById(R.id.thumb);
            image.setImageDrawable(contentImage);

            return listItem;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            NDEFRecord record = null;

            if(mCurrentNdefMsg == null) {
                mCurrentNdefMsg = new NDEFMsg();
            }

            Intent intent = null;
            switch (position) {
                case NDEF_EDITOR_SMS_RECORD:
                    record = new SmsRecord();
                    break;
                case NDEF_EDITOR_TEXT_RECORD:
                    record = new TextRecord();
                    break;
                case NDEF_EDITOR_URI_RECORD:
                    record = new UriRecord(UriRecord.NdefUriIdCode.NDEF_RTD_URI_ID_HTTP_WWW, getResources().getString(R.string.st_uri));
                    break;
                case NDEF_EDITOR_EMAIL_RECORD:
                    record = new EmailRecord();
                    break;
                case NDEF_EDITOR_VCARD_RECORD:
                    record = new VCardRecord();
                    break;
                case NDEF_EDITOR_WIFI_RECORD:
                    record = new WifiRecord();
                    break;
                case NDEF_EDITOR_BT_RECORD:
                    record = new BtRecord();
                    break;
                case NDEF_EDITOR_BTLE_RECORD:
                    record = new BtLeRecord();
                    break;
                case NDEF_EDITOR_AAR_RECORD:
                    record = new AarRecord();
                    break;
                case NDEF_EDITOR_MIME_RECORD:
                    record = new MimeRecord();
                    break;
                case NDEF_EDITOR_EXTERNAL_RECORD:
                    record = new ExternalRecord();
                    break;
                case NDEF_EDITOR_EMPTY_RECORD:
                    record = new EmptyRecord();
                    break;
            }

            // NB: The change will be effective in the tag only if the user follows the instructions
            //     to fill the new record and save it.
            mCurrentNdefMsg.addRecord(record);

            // The new record is the last one of the record list
            int recordNbr = mCurrentNdefMsg.getNbrOfRecords() - 1;

            intent = new Intent(getActivity(), NDEFActivity.class);
            int action = NDEFEditorFragment.ADD_NDEF_RECORD;

            intent.putExtra(NDEFRecordFragment.NDEFKey, mCurrentNdefMsg);
            intent.putExtra(NDEFRecordFragment.RecordNbrKey, recordNbr);
            intent.putExtra(NDEFEditorFragment.EditorKey, action);
            intent.putExtra("area_nbr", mArea);

            startActivityForResult(intent, action);

            mAlertDialog.dismiss();
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

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                    STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(
                            STType5PwdDialogFragment.STPwdAction.PRESENT_CURRENT_PWD,
                            mPasswordNumber,
                            dialogMsg,
                            NDEFEditorFragment.this);
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

        private NDEFEditorFragment mFragment;

        public AsyncTaskDisplayPasswordDialogBoxForType4Tag(NDEFEditorFragment ndefEditorFragment) {
            mFragment = ndefEditorFragment;
        }

        @Override
        protected ActionStatus doInBackground(Void... param) {
            ActionStatus result = ActionStatus.ACTION_FAILED;

            try {
                // Request Read Password
                PwdDialogFragment pwdDialogFragment = PwdDialogFragment.newInstance(getString(R.string.enter_read_password),
                                                                                    getFragmentManager(),
                                                                                    mFragment,
                                                                                    ((STType4PasswordInterface) myTag).getReadPasswordLengthInBytes(mArea));
                mReadPassword = pwdDialogFragment.getPassword();
            }
            catch (STException e) {
                return result;
            }

            result = ActionStatus.ACTION_SUCCESSFUL;
            return result;
        }
    }


}



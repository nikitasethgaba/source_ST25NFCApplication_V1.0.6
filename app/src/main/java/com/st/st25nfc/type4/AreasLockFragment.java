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

package com.st.st25nfc.type4;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.PwdDialogFragment;
import com.st.st25nfc.generic.STFragment;
import com.st.st25sdk.Helper;
import com.st.st25sdk.STException;
import com.st.st25sdk.type4a.STType4Tag;
import com.st.st25sdk.type4a.Type4Tag;

import static com.st.st25nfc.type4.AreasLockFragment.ActionStatus.ACTION_SUCCESSFUL;
import static com.st.st25nfc.type4.AreasLockFragment.ActionStatus.ACTION_FAILED;
import static com.st.st25nfc.type4.AreasLockFragment.ActionStatus.TAG_NOT_IN_THE_FIELD;


public class AreasLockFragment extends STFragment implements AdapterView.OnItemClickListener, PwdDialogFragment.PwdDialogListener {

    private int mAreaNum;

    private PwdDialogFragment mPwdDialogFragment;

    private enum Action {
        READ_LOCK,
        READ_UNLOCK,

        WRITE_LOCK,
        WRITE_UNLOCK,
        WRITE_LOCK_PERMANENT,

        CHANGE_READ_PWD,
        CHANGE_WRITE_PWD,

        PRESENT_READ_PWD,
        PRESENT_WRITE_PWD
    }

    enum ActionStatus {
        ACTION_SUCCESSFUL,
        ACTION_FAILED,
        ACTION_CANCELLED,
        TAG_NOT_IN_THE_FIELD
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.activity_stm24ta_areas_lock, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            mAreaNum = intent.getIntExtra("area_num", 1);
        } else {
            mAreaNum = 1;
        }

        TextView lockHeaderTextView = (TextView) mView.findViewById(R.id.lockHeaderView);
        String dialogMsg = String.format(getResources().getString(R.string.area_mem_conf), mAreaNum);
        lockHeaderTextView.setText(dialogMsg);

        Button readLockButton = (Button) mView.findViewById(R.id.readLockButton);
        readLockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAction(Action.READ_LOCK);
            }
        });

        Button readUnLockButton = (Button) mView.findViewById(R.id.readUnLockButton);
        readUnLockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAction(Action.READ_UNLOCK);
            }
        });

        Button writeLockButton = (Button) mView.findViewById(R.id.writeLockButton);
        writeLockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAction(Action.WRITE_LOCK);
            }
        });

        Button writeUnLockButton = (Button) mView.findViewById(R.id.writeUnLockButton);
        writeUnLockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAction(Action.WRITE_UNLOCK);
            }
        });


        Button permanentLockButton = (Button) mView.findViewById(R.id.permanentWriteLockButton);
        permanentLockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAction(Action.WRITE_LOCK_PERMANENT);
            }


        });

        Button changeReadPwdButton = (Button) mView.findViewById(R.id.changeReadPwdButton);
        changeReadPwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAction(Action.CHANGE_READ_PWD);
            }


        });

        Button changeWritePwdButton = (Button) mView.findViewById(R.id.changeWritePwdButton);
        changeWritePwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAction(Action.CHANGE_WRITE_PWD);
            }


        });

        Button presentReadPwdButton = (Button) mView.findViewById(R.id.presentReadPwdButton);
        presentReadPwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAction(Action.PRESENT_READ_PWD);
            }


        });

        Button presentWritePwdButton = (Button) mView.findViewById(R.id.presentWritePwdButton);
        presentWritePwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAction(Action.PRESENT_WRITE_PWD);
            }


        });

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshAccessStatus();
    }

    private void refreshAccessStatus() {
        new asyncTaskReadAreasAccessStatus().execute();
    }

    private byte[] getReadPassword(String message) throws  STException{
        mPwdDialogFragment = PwdDialogFragment.newInstance(message,
                                                           getFragmentManager(),
                                                           this,
                                                           ((STType4Tag) myTag).getReadPasswordLengthInBytes(mAreaNum));

        // Get the password typed by the user
        return mPwdDialogFragment.getPassword();
    }

    private byte[] getWritePassword(String message) throws STException {
        mPwdDialogFragment = PwdDialogFragment.newInstance(message,
                                                           getFragmentManager(),
                                                           this,
                                                           ((STType4Tag) myTag).getWritePasswordLengthInBytes(mAreaNum));

        // Get the password typed by the user
        return mPwdDialogFragment.getPassword();
    }

    @Override
    public void onPwdDialogFinish(int result, byte[] password) {

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
     * AsyncTask retrieving the access status of this area
     */
    private class asyncTaskReadAreasAccessStatus extends AsyncTask<Void, Void, ActionStatus> {

        Type4Tag.AccessStatus mReadAccessStatus;
        Type4Tag.AccessStatus mWriteAccessStatus;

        byte mReadAccessValue;
        byte mWriteAccessValue;

        public asyncTaskReadAreasAccessStatus() {

        }

        @Override
        protected ActionStatus doInBackground(Void... param) {
            try {

                int fileId = ((Type4Tag) myTag).getCCTlv(mAreaNum-1).getFileId();

                mReadAccessValue = ((Type4Tag) myTag).getFileReadAccess(fileId);
                mWriteAccessValue = ((Type4Tag) myTag).getFileWriteAccess(fileId);

                mReadAccessStatus = ((Type4Tag) myTag).getFileReadAccessStatus(fileId);
                mWriteAccessStatus = ((Type4Tag) myTag).getFileWriteAccessStatus(fileId);

            } catch (STException e) {
                switch (e.getError()) {
                    case TAG_NOT_IN_THE_FIELD:
                        return TAG_NOT_IN_THE_FIELD;

                    default:
                        e.printStackTrace();
                        return ACTION_FAILED;
                }
            }

            return ACTION_SUCCESSFUL;
        }

        @Override
        protected void onPostExecute(ActionStatus actionStatus) {

            switch(actionStatus) {
                case ACTION_SUCCESSFUL:
                    updateAccessStatus();
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

        private void updateAccessStatus() {
            String readStatus, writeStatus;

            switch (mReadAccessStatus) {
                case NOT_LOCKED:
                    readStatus = "Not locked";
                    break;
                case LOCKED_BY_PASSWORD:
                    readStatus = "Locked by password";
                    break;
                case NOT_AUTHORIZED:
                    readStatus = "Not authorized";
                    break;
                default:
                case STATUS_UNKNOWN:
                    readStatus = "Lock status unknown";
                    break;
            }

            switch (mWriteAccessStatus) {
                case NOT_LOCKED:
                    writeStatus = "Not locked";
                    break;
                case LOCKED_BY_PASSWORD:
                    writeStatus = "Locked by password";
                    break;
                case NOT_AUTHORIZED:
                    writeStatus = "Not authorized";
                    break;
                default:
                case STATUS_UNKNOWN:
                    writeStatus = "Lock status unknown";
                    break;
            }

            String statusString = "Read access status = 0x" + Helper.convertByteToHexString(mReadAccessValue).toUpperCase() +
                    " = " + readStatus + "\n" +
                    "Write access status = 0x" + Helper.convertByteToHexString(mWriteAccessValue).toUpperCase() +
                    " = " + writeStatus;

            TextView statusView = (TextView) mView.findViewById(R.id.currentStateView);
            statusView.setText(statusString);
        }

    }

    private void executeAction(Action action) {
        new asyncTaskExecuteAction().execute(action);
    }

    private class asyncTaskExecuteAction extends AsyncTask<Action, Void, ActionStatus> {
        int mNbrOfAreas;
        Action mAction;

        public asyncTaskExecuteAction() {

        }

        @Override
        protected ActionStatus doInBackground(Action... param) {
            ActionStatus result = ActionStatus.ACTION_CANCELLED;
            byte[] currentWritePassword;
            int fileId = mAreaNum;

            mAction = param[0];

            try {
                switch (mAction) {
                    case READ_LOCK:
                        currentWritePassword = getWritePassword(getString(R.string.enter_write_password) + " ");
                        if(currentWritePassword != null) {
                            ((STType4Tag) myTag).lockRead(fileId, currentWritePassword);
                            result = ActionStatus.ACTION_SUCCESSFUL;
                        }
                        break;
                    case READ_UNLOCK:
                        currentWritePassword = getWritePassword(getString(R.string.enter_write_password) + " ");
                        if(currentWritePassword != null) {
                            ((STType4Tag) myTag).unlockRead(fileId, currentWritePassword);
                            result = ActionStatus.ACTION_SUCCESSFUL;
                        }
                        break;
                    case WRITE_LOCK:
                        currentWritePassword = getWritePassword(getString(R.string.enter_write_password) + " ");
                        if(currentWritePassword != null) {
                            ((STType4Tag) myTag).lockWrite(fileId, currentWritePassword);
                            result = ActionStatus.ACTION_SUCCESSFUL;
                        }
                        break;
                    case WRITE_UNLOCK:
                        currentWritePassword = getWritePassword(getString(R.string.enter_write_password) + " ");
                        if(currentWritePassword != null) {
                            ((STType4Tag) myTag).unlockWrite(fileId, currentWritePassword);
                            result = ActionStatus.ACTION_SUCCESSFUL;
                        }
                        break;
                    case WRITE_LOCK_PERMANENT:
                        currentWritePassword = getWritePassword(getString(R.string.enter_write_password) + " ");
                        if(currentWritePassword != null) {
                            ((STType4Tag) myTag).lockWritePermanently(fileId, currentWritePassword);
                            result = ActionStatus.ACTION_SUCCESSFUL;
                        }
                        break;
                    case PRESENT_WRITE_PWD:
                        currentWritePassword = getWritePassword(getString(R.string.enter_write_password) + " ");
                        if(currentWritePassword != null) {
                            ((STType4Tag) myTag).verifyWritePassword(fileId, currentWritePassword);
                            result = ActionStatus.ACTION_SUCCESSFUL;
                        }
                        result = ActionStatus.ACTION_SUCCESSFUL;
                        break;
                    case CHANGE_READ_PWD:
                        currentWritePassword = getWritePassword(getString(R.string.enter_write_password) + " ");
                        byte[] newReadPassword = getReadPassword(getString(R.string.enter_new_read_password) + " ");
                        if((currentWritePassword != null) && (newReadPassword != null)) {

                            ((STType4Tag) myTag).changeReadPassword(fileId, newReadPassword, currentWritePassword);
                            result = ActionStatus.ACTION_SUCCESSFUL;
                        }
                        break;
                    case CHANGE_WRITE_PWD:
                        currentWritePassword = getWritePassword(getString(R.string.enter_write_password) + " ");
                        byte[] newWritePassword = getWritePassword(getString(R.string.enter_new_write_password) + " ");
                        if((currentWritePassword != null) && (newWritePassword != null)) {
                            ((STType4Tag) myTag).changeWritePassword(fileId, newWritePassword, currentWritePassword);
                            result = ActionStatus.ACTION_SUCCESSFUL;
                        }
                        break;
                    case PRESENT_READ_PWD:
                        byte[] currentReadPassword = getReadPassword(getString(R.string.enter_read_password) + " ");
                        if(currentReadPassword != null) {
                            ((STType4Tag) myTag).verifyReadPassword(fileId, currentReadPassword);
                            result = ActionStatus.ACTION_SUCCESSFUL;
                        }
                        break;

                    default:
                        break;
                }
            }
            catch (final STException e) {
                e.printStackTrace();
                result = ActionStatus.ACTION_FAILED;
            }

            return result;
        }

        @Override
        protected void onPostExecute(ActionStatus actionStatus) {

            switch(actionStatus) {
                case ACTION_SUCCESSFUL:
                    showToast(R.string.tag_updated);
                    ((STType4Tag) myTag).invalidateCache();
                    refreshAccessStatus();
                    break;

                case ACTION_FAILED:
                    showToast(R.string.error_while_updating_the_tag);
                    break;

                case ACTION_CANCELLED:
                    showToast(R.string.tag_not_updated);
                    break;

                case TAG_NOT_IN_THE_FIELD:
                    showToast(R.string.tag_not_in_the_field);
                    break;
            }

            return;
        }
    }


}

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

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.st.st25sdk.ConfidentialModeInterface;
import com.st.st25sdk.Helper;
import com.st.st25sdk.command.Iso15693CustomKillCommandInterface;
import com.st.st25sdk.type5.STType5PasswordInterface;
import com.st.st25nfc.R;
import com.st.st25sdk.RandomNumberInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.TagHelper;

import static com.st.st25nfc.generic.STType5PwdDialogFragment.AsyncTaskStatus.*;


public class STType5PwdDialogFragment extends DialogFragment {

    static final String TAG = "STType5PwdDialog";

    private Handler mHandler;

    public interface STType5PwdDialogListener {
        public void onSTType5PwdDialogFinish(int result);
    }

    private STType5PwdDialogListener mListener;

    public void setListener(STType5PwdDialogListener listener) {
        this.mListener = listener;
    }

    private static final String ARG_ACTION = "action";
    private static final String ARG_DIALOG_MSG = "dialogMessage";
    private static final String ARG_PASSWORD_NUMBER = "passwordNumber";

    private View mCurFragmentView = null; // Store view corresponding to current fragment

    public enum STPwdAction {
        // Actions called when presenting a password
        PRESENT_CURRENT_PWD,

        // Action called when CHANGING a password
        ENTER_NEW_PWD,

        // Some other actions where a specific password is needed
        KILL_TAG,
        ENTER_NEW_KILL_PWD,
        ENABLE_CONFIDENTIAL_MODE
    }

    private String mDialogMessage;
    private STPwdAction mAction;
    private byte mPasswordNumber;

    private STType5PasswordInterface mSTType5PasswordInterface;

    private boolean mIsPwdOk = false;
    private STType5PasswordInterface.PasswordLength mPasswordLength;

    enum AsyncTaskStatus {
        // Failing status
        COMMAND_FAILED,
        PRESENT_PWD_FAILED,
        TAG_NOT_IN_THE_FIELD,

        // Successful status
        COMMAND_SUCCESSFUL,
        TAG_KILLED,
        CONFIDENTIAL_MODE_ENABLED,
        KILL_PWD_UPDATED
    };


    public STType5PwdDialogFragment() {
        // Required empty public constructor
    }

    public void setPwdDialogListener(STType5PwdDialogListener listener) {
        this.mListener = listener;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment STType5PwdDialogFragment.
     */
    public static STType5PwdDialogFragment newInstance(STPwdAction action,
                                                       byte passwordNumber,
                                                       String dialogMessage) {
        STType5PwdDialogFragment fragment = new STType5PwdDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_ACTION, action);
        args.putSerializable(ARG_PASSWORD_NUMBER, passwordNumber);
        args.putSerializable(ARG_DIALOG_MSG, dialogMessage);

        fragment.setArguments(args);
        return fragment;
    }

    public static STType5PwdDialogFragment newInstance(STPwdAction action,
                                                       byte passwordNumber,
                                                       String dialogMessage,
                                                       STType5PwdDialogListener listener) {
        STType5PwdDialogFragment fragment = newInstance(action, passwordNumber, dialogMessage);
        fragment.setListener(listener);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mAction = (STPwdAction) getArguments().get(ARG_ACTION);
            mPasswordNumber = (byte) getArguments().get(ARG_PASSWORD_NUMBER);
            mDialogMessage = (String) getArguments().get(ARG_DIALOG_MSG);
        }

        mHandler = new Handler();

        if(mListener == null) {
            // No listener was passed to newInstance(). Assume the activity is used as listener
            mListener = (STType5PwdDialogListener) getActivity();
            Log.v(TAG, "mListener = " + mListener);
        }

        setStyle(STYLE_NO_TITLE, 0); // remove title from DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String message = "";

        // This dialog box will fail if the tag doesn't implement a STType5PasswordInterface
        try {
            mSTType5PasswordInterface = (STType5PasswordInterface) MainActivity.getTag();
        } catch (ClassCastException e) {
            // Tag not implementing STType5PasswordInterface
            Log.e(TAG, "Error! Tag not implementing STType5PasswordInterface!");
            return null;
        }

        // Inflate the layout for this fragment
        mCurFragmentView = inflater.inflate(R.layout.fragment_pwd_dialog, container, false);

        try {
            mPasswordLength = mSTType5PasswordInterface.getPasswordLength(mPasswordNumber);
        } catch (STException e) {
            e.printStackTrace();
            return null;
        }

        LinearLayout pwdByte7To4Layout = (LinearLayout) mCurFragmentView.findViewById(R.id.pwdByte7To4Layout);
        LinearLayout pwdByte11To8Layout = (LinearLayout) mCurFragmentView.findViewById(R.id.pwdByte11To8Layout);
        LinearLayout pwdByte15To12Layout = (LinearLayout) mCurFragmentView.findViewById(R.id.pwdByte15To12Layout);

        switch(mPasswordLength) {
            case PWD_ON_32_BITS:
                message = mDialogMessage + "\n" + getResources().getString(R.string.thirty_two_bits_pwd);
                pwdByte7To4Layout.setVisibility(View.GONE);
                pwdByte11To8Layout.setVisibility(View.GONE);
                pwdByte15To12Layout.setVisibility(View.GONE);
                break;
            case PWD_ON_64_BITS:
                message = mDialogMessage + "\n" + getResources().getString(R.string.sixty_four_bits_pwd);
                pwdByte7To4Layout.setVisibility(View.VISIBLE);
                pwdByte11To8Layout.setVisibility(View.GONE);
                pwdByte15To12Layout.setVisibility(View.GONE);
                break;
            case PWD_ON_96_BITS:
                message = mDialogMessage + "\n" + getResources().getString(R.string.ninety_six_bits_pwd);
                pwdByte7To4Layout.setVisibility(View.VISIBLE);
                pwdByte11To8Layout.setVisibility(View.VISIBLE);
                pwdByte15To12Layout.setVisibility(View.GONE);
                break;
            case PWD_ON_128_BITS:
                message = mDialogMessage + "\n" + getResources().getString(R.string.one_hundred_twenty_height_bits_pwd);
                pwdByte7To4Layout.setVisibility(View.VISIBLE);
                pwdByte11To8Layout.setVisibility(View.VISIBLE);
                pwdByte15To12Layout.setVisibility(View.VISIBLE);
                break;
        }

        TextView messageTextView = (TextView) mCurFragmentView.findViewById(R.id.messageTextView);
        messageTextView.setText(message);

        Button okButton = (Button) mCurFragmentView.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // The task will be executed in a background task
                                            MyAsyncTask myAsyncTask = new MyAsyncTask();
                                            myAsyncTask.execute();
                                        }
                                    });

        Button cancelButton = (Button) mCurFragmentView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Leave the current activity
                dismiss();
            }
        });

        return mCurFragmentView;
    }

    private boolean isTagNeedingARandomNbr() {

        try {
            RandomNumberInterface randomNumberInterface = (RandomNumberInterface) MainActivity.getTag();
            return true;
        } catch (ClassCastException e) {
            // Tag not implementing RandomNumberInterface
            return false;
        }

    }

    private byte[] getPasswordTypedByUser() {
        byte[] password = null;

        EditText byte15EditText = (EditText) mCurFragmentView.findViewById(R.id.byte15EditText);
        EditText byte14EditText = (EditText) mCurFragmentView.findViewById(R.id.byte14EditText);
        EditText byte13EditText = (EditText) mCurFragmentView.findViewById(R.id.byte13EditText);
        EditText byte12EditText = (EditText) mCurFragmentView.findViewById(R.id.byte12EditText);

        EditText byte11EditText = (EditText) mCurFragmentView.findViewById(R.id.byte11EditText);
        EditText byte10EditText = (EditText) mCurFragmentView.findViewById(R.id.byte10EditText);
        EditText byte9EditText = (EditText) mCurFragmentView.findViewById(R.id.byte9EditText);
        EditText byte8EditText = (EditText) mCurFragmentView.findViewById(R.id.byte8EditText);

        EditText byte7EditText = (EditText) mCurFragmentView.findViewById(R.id.byte7EditText);
        EditText byte6EditText = (EditText) mCurFragmentView.findViewById(R.id.byte6EditText);
        EditText byte5EditText = (EditText) mCurFragmentView.findViewById(R.id.byte5EditText);
        EditText byte4EditText = (EditText) mCurFragmentView.findViewById(R.id.byte4EditText);

        EditText byte3EditText = (EditText) mCurFragmentView.findViewById(R.id.byte3EditText);
        EditText byte2EditText = (EditText) mCurFragmentView.findViewById(R.id.byte2EditText);
        EditText byte1EditText = (EditText) mCurFragmentView.findViewById(R.id.byte1EditText);
        EditText byte0EditText = (EditText) mCurFragmentView.findViewById(R.id.byte0EditText);


        // In the command, the LSB (= Byte 0) should be sent first (at index 0)
        switch(mPasswordLength) {
            case PWD_ON_32_BITS:
                password = new byte[4];
                password[3] = (byte) Integer.parseInt(byte3EditText.getText().toString(), 16);                 // This is the MSB
                password[2] = (byte) Integer.parseInt(byte2EditText.getText().toString(), 16);
                password[1] = (byte) Integer.parseInt(byte1EditText.getText().toString(), 16);
                password[0] = (byte) Integer.parseInt(byte0EditText.getText().toString(), 16);                 // This is the LSB
                break;

            case PWD_ON_64_BITS:
                password = new byte[8];
                password[7] = (byte) Integer.parseInt(byte7EditText.getText().toString(), 16);                 // This is the MSB
                password[6] = (byte) Integer.parseInt(byte6EditText.getText().toString(), 16);
                password[5] = (byte) Integer.parseInt(byte5EditText.getText().toString(), 16);
                password[4] = (byte) Integer.parseInt(byte4EditText.getText().toString(), 16);
                password[3] = (byte) Integer.parseInt(byte3EditText.getText().toString(), 16);
                password[2] = (byte) Integer.parseInt(byte2EditText.getText().toString(), 16);
                password[1] = (byte) Integer.parseInt(byte1EditText.getText().toString(), 16);
                password[0] = (byte) Integer.parseInt(byte0EditText.getText().toString(), 16);                 // This is the LSB
                break;

            case PWD_ON_96_BITS:
                password = new byte[12];
                password[11] = (byte) Integer.parseInt(byte11EditText.getText().toString(), 16);                 // This is the MSB
                password[10] = (byte) Integer.parseInt(byte10EditText.getText().toString(), 16);
                password[9] = (byte) Integer.parseInt(byte9EditText.getText().toString(), 16);
                password[8] = (byte) Integer.parseInt(byte8EditText.getText().toString(), 16);
                password[7] = (byte) Integer.parseInt(byte7EditText.getText().toString(), 16);
                password[6] = (byte) Integer.parseInt(byte6EditText.getText().toString(), 16);
                password[5] = (byte) Integer.parseInt(byte5EditText.getText().toString(), 16);
                password[4] = (byte) Integer.parseInt(byte4EditText.getText().toString(), 16);
                password[3] = (byte) Integer.parseInt(byte3EditText.getText().toString(), 16);
                password[2] = (byte) Integer.parseInt(byte2EditText.getText().toString(), 16);
                password[1] = (byte) Integer.parseInt(byte1EditText.getText().toString(), 16);
                password[0] = (byte) Integer.parseInt(byte0EditText.getText().toString(), 16);                 // This is the LSB
                break;

            case PWD_ON_128_BITS:
                password = new byte[16];
                password[15] = (byte) Integer.parseInt(byte15EditText.getText().toString(), 16);                 // This is the MSB
                password[14] = (byte) Integer.parseInt(byte14EditText.getText().toString(), 16);
                password[13] = (byte) Integer.parseInt(byte13EditText.getText().toString(), 16);
                password[12] = (byte) Integer.parseInt(byte12EditText.getText().toString(), 16);
                password[11] = (byte) Integer.parseInt(byte11EditText.getText().toString(), 16);
                password[10] = (byte) Integer.parseInt(byte10EditText.getText().toString(), 16);
                password[9] = (byte) Integer.parseInt(byte9EditText.getText().toString(), 16);
                password[8] = (byte) Integer.parseInt(byte8EditText.getText().toString(), 16);
                password[7] = (byte) Integer.parseInt(byte7EditText.getText().toString(), 16);
                password[6] = (byte) Integer.parseInt(byte6EditText.getText().toString(), 16);
                password[5] = (byte) Integer.parseInt(byte5EditText.getText().toString(), 16);
                password[4] = (byte) Integer.parseInt(byte4EditText.getText().toString(), 16);
                password[3] = (byte) Integer.parseInt(byte3EditText.getText().toString(), 16);
                password[2] = (byte) Integer.parseInt(byte2EditText.getText().toString(), 16);
                password[1] = (byte) Integer.parseInt(byte1EditText.getText().toString(), 16);
                password[0] = (byte) Integer.parseInt(byte0EditText.getText().toString(), 16);                 // This is the LSB
                break;
        }

        return password;
    }

    /**
     * Function called by background thread to display a toast on UI
     *
     * @param resource_id
     */
    private void showToast(final int resource_id) {
        // Warning: Function called from background thread! Post a request to the UI thread
        mHandler.post(new Runnable() {
            public void run() {
                Resources resources = getResources();
                if(resources != null) {
                    Toast.makeText(getContext(), resources.getString(resource_id), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {

        Log.v(TAG, "onDismiss");

        // Notify the activity that opened the
        if(mListener != null) {
            if(mIsPwdOk == true) {
                mListener.onSTType5PwdDialogFinish(PwdDialogFragment.RESULT_OK);
            } else {
                mListener.onSTType5PwdDialogFinish(PwdDialogFragment.RESULT_FAIL);
            }
        }

        super.onDismiss(dialog);
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, AsyncTaskStatus> {

        public MyAsyncTask() {
        }

        @Override
        protected AsyncTaskStatus doInBackground(Void... params) {
            AsyncTaskStatus result = COMMAND_FAILED;

            switch(mAction) {
                case KILL_TAG:
                    result = killTag();
                    break;
                case ENTER_NEW_KILL_PWD:
                    result = enterNewKillPassword();
                    break;
                case ENABLE_CONFIDENTIAL_MODE:
                    result = enableConfidentialMode();
                    break;
                case PRESENT_CURRENT_PWD:
                    result = presentCurrentPassword();
                    break;
                case ENTER_NEW_PWD:
                    result = writePassword();
                    break;
            }

            return result;
        }

        private AsyncTaskStatus killTag(){
            AsyncTaskStatus result;

            Iso15693CustomKillCommandInterface killCommandInterface;

            Log.v(TAG, "killTag");

            try {
                killCommandInterface = (Iso15693CustomKillCommandInterface) MainActivity.getTag();
            } catch (ClassCastException e) {
                Log.e(TAG, "Error! Tag not implementing Iso15693CustomKillCommandInterface!");
                return COMMAND_FAILED;
            }

            try {
                byte[] unencryptedKillPassword = getPasswordTypedByUser();
                Log.v(TAG, "unencryptedKillPassword: " + Helper.convertHexByteArrayToString(unencryptedKillPassword));

                killCommandInterface.kill(unencryptedKillPassword);
                result = TAG_KILLED;

            } catch (STException e) {
                switch (e.getError()) {
                    case TAG_NOT_IN_THE_FIELD:
                        result = TAG_NOT_IN_THE_FIELD;
                        break;
                    default:
                        e.printStackTrace();
                        result = COMMAND_FAILED;
                        break;
                }
            }

            return result;
        }

        private AsyncTaskStatus enterNewKillPassword(){
            AsyncTaskStatus result;

            Iso15693CustomKillCommandInterface killCommandInterface;

            Log.v(TAG, "enterNewKillPassword");

            try {
                killCommandInterface = (Iso15693CustomKillCommandInterface) MainActivity.getTag();
            } catch (ClassCastException e) {
                Log.e(TAG, "Error! Tag not implementing Iso15693CustomKillCommandInterface!");
                return COMMAND_FAILED;
            }

            try {
                byte[] newKillPassword = getPasswordTypedByUser();
                Log.v(TAG, "newKillPassword: " + Helper.convertHexByteArrayToString(newKillPassword));

                killCommandInterface.writeKill(newKillPassword);
                result = KILL_PWD_UPDATED;

            } catch (STException e) {
                switch (e.getError()) {
                    case TAG_NOT_IN_THE_FIELD:
                        result = TAG_NOT_IN_THE_FIELD;
                        break;
                    default:
                        e.printStackTrace();
                        result = COMMAND_FAILED;
                        break;
                }
            }

            return result;
        }

        private AsyncTaskStatus enableConfidentialMode(){
            AsyncTaskStatus result;
            byte[] randomNumber;
            RandomNumberInterface randomNumberInterface;
            ConfidentialModeInterface confidentialModeInterface;

            Log.v(TAG, "enableConfidentialMode");

            try {
                randomNumberInterface = (RandomNumberInterface) MainActivity.getTag();
            } catch (ClassCastException e) {
                Log.e(TAG, "Error! Tag not implementing RandomNumberInterface!");
                return COMMAND_FAILED;
            }

            try {
                confidentialModeInterface = (ConfidentialModeInterface) MainActivity.getTag();
            } catch (ClassCastException e) {
                Log.e(TAG, "Error! Tag not implementing ConfidentialModeInterface!");
                return COMMAND_FAILED;
            }

            try {
                // Get a 16 bits random number
                randomNumber = randomNumberInterface.getRandomNumber();

            } catch (STException e) {
                switch (e.getError()) {
                    case TAG_NOT_IN_THE_FIELD:
                        return TAG_NOT_IN_THE_FIELD;
                    default:
                        e.printStackTrace();
                        return COMMAND_FAILED;
                }
            }

            try {
                byte[] password = getPasswordTypedByUser();

                Log.v(TAG, "Kill/Confidential password:");
                Log.v(TAG, "password: " + Helper.convertHexByteArrayToString(password));
                Log.v(TAG, "randomNumber: " + Helper.convertHexByteArrayToString(randomNumber));
                TagHelper.xorBetweenPwdAndRandomNbr(password, randomNumber);
                Log.v(TAG, "password after XOR: " + Helper.convertHexByteArrayToString(password));

                confidentialModeInterface.enableConfidentialMode(password);
                result = CONFIDENTIAL_MODE_ENABLED;

            } catch (STException e) {
                switch (e.getError()) {
                    case TAG_NOT_IN_THE_FIELD:
                        result = TAG_NOT_IN_THE_FIELD;
                        break;
                    default:
                        e.printStackTrace();
                        result = COMMAND_FAILED;
                        break;
                }
            }
            return result;
        }

        private AsyncTaskStatus writePassword() {
            AsyncTaskStatus result;
            byte[] password = getPasswordTypedByUser();

            Log.v(TAG, "writePassword");

            try {
                mSTType5PasswordInterface.writePassword(mPasswordNumber, password);
                // Password updated successfully
                mIsPwdOk = true;
                result = COMMAND_SUCCESSFUL;

            } catch (STException e) {
                result = PRESENT_PWD_FAILED;
            }

            return result;
        }

        private AsyncTaskStatus presentCurrentPassword() {
            byte[] password = getPasswordTypedByUser();
            AsyncTaskStatus result;

            if(isTagNeedingARandomNbr()) {
                // A random number is needed for password presentation
                result = getRandomNumber(password);
            } else {
                // We can directly present the password
                result = presentPassword(password);
            }

            return result;
        }

        private AsyncTaskStatus getRandomNumber(final byte[] password){
            RandomNumberInterface randomNumberInterface;
            AsyncTaskStatus result;

            Log.v(TAG, "getRandomNumber");

            try {
                randomNumberInterface = (RandomNumberInterface) MainActivity.getTag();
            } catch (ClassCastException e) {
                // Tag not implementing RandomNumberInterface
                Log.e(TAG, "Error! Tag not implementing RandomNumberInterface!");
                return COMMAND_FAILED;
            }

            try {
                // Get a 16 bits random number
                byte[] randomNumber = randomNumberInterface.getRandomNumber();

                // We successfully retrieved a randomNumber

                Log.v(TAG, "getRandomNumber for password " + mPasswordNumber);
                Log.v(TAG, "password: " + Helper.convertHexByteArrayToString(password));
                Log.v(TAG, "randomNumber: " + Helper.convertHexByteArrayToString(randomNumber));

                TagHelper.xorBetweenPwdAndRandomNbr(password, randomNumber);
                Log.v(TAG, "password after XOR: " + Helper.convertHexByteArrayToString(password));

                mSTType5PasswordInterface.presentPassword(mPasswordNumber, password);
                // Password presented successfully
                mIsPwdOk = true;
                result = COMMAND_SUCCESSFUL;

            } catch (STException e) {
                switch (e.getError()) {
                    case TAG_NOT_IN_THE_FIELD:
                        result = TAG_NOT_IN_THE_FIELD;
                        break;
                    default:
                        e.printStackTrace();
                        result = COMMAND_FAILED;
                        break;
                }
            }

            return result;
        }

        private AsyncTaskStatus presentPassword(final byte[] password) {
            AsyncTaskStatus result;

            try {
                Log.v(TAG, "presentPassword " + mPasswordNumber);
                Log.v(TAG, "password: " + Helper.convertHexByteArrayToString(password));

                mSTType5PasswordInterface.presentPassword(mPasswordNumber, password);
                // Password presented successfully
                mIsPwdOk = true;
                result = COMMAND_SUCCESSFUL;

            } catch (STException e) {
                // Present password failed
                e.printStackTrace();
                result = COMMAND_FAILED;
            }

            return result;
        }

        @Override
        protected void onPostExecute(AsyncTaskStatus result) {
            switch(result) {
                // Failing commands
                case COMMAND_FAILED:
                    showToast(R.string.Command_failed);
                    break;
                case PRESENT_PWD_FAILED:
                    showToast(R.string.present_pwd_failed);
                    break;
                case TAG_NOT_IN_THE_FIELD:
                    showToast(R.string.tag_not_in_the_field);
                    break;

                // Successful commands
                case COMMAND_SUCCESSFUL:
                    // Nothing displayed
                    break;
                case CONFIDENTIAL_MODE_ENABLED:
                    showToast(R.string.confidential_mode_enabled);
                    break;
                case TAG_KILLED:
                    showToast(R.string.tag_killed);
                    break;
                case KILL_PWD_UPDATED:
                    showToast(R.string.kill_password_updated);
                    break;
            }

            if((result == COMMAND_SUCCESSFUL) ||
               (result == CONFIDENTIAL_MODE_ENABLED) ||
               (result == KILL_PWD_UPDATED) ||
               (result == TAG_KILLED) ) {
                // Close the dialog and return back to the parent activity
                dismiss();
            }
        }
    }

}

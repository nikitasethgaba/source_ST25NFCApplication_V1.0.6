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
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.st.st25nfc.R;

import java.util.concurrent.Semaphore;


public class PwdDialogFragment extends DialogFragment {

    public interface PwdDialogListener {
        public void onPwdDialogFinish(int result, byte[] password);
    }


    static final String TAG = "PwdDialogFragment";

    private Handler mHandler;


    private PwdDialogListener mListener;
    private FragmentManager mFragmentManager;

    public void setListener(PwdDialogListener listener) {
        this.mListener = listener;
    }

    private static final String ARG_DIALOG_MSG = "dialogMessage";
    private static final String PWD_LENGTH_IN_BYTES = "pwdLengthInBytes";


    private View mCurFragmentView = null; // Store view corresponding to current fragment

    public static final int RESULT_FAIL = 0;
    public static final int RESULT_OK = 1;

    private String mDialogMessage;


    private boolean mIsPwdOk = false;
    private int mPasswordLengthInBytes = 0;
    private byte[] mPassword;
    private Semaphore mLock = new Semaphore(0);


    public PwdDialogFragment() {
        // Required empty public constructor
    }


    public void setPwdDialogListener(PwdDialogListener listener) {
        this.mListener = listener;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment STType5PwdDialogFragment.
     */
    public static PwdDialogFragment newInstance(String dialogMessage, int pwdLengthInBytes) {
        PwdDialogFragment fragment = new PwdDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_DIALOG_MSG, dialogMessage);
        args.putSerializable(PWD_LENGTH_IN_BYTES, pwdLengthInBytes);

        fragment.setArguments(args);
        return fragment;
    }

    public static PwdDialogFragment newInstance(String dialogMessage, FragmentManager fragmentManager,
                                                PwdDialogListener listener, int pwdLengthInBytes) {
        PwdDialogFragment fragment = newInstance(dialogMessage, pwdLengthInBytes);
        fragment.setListener(listener);
        fragment.mFragmentManager = fragmentManager;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mDialogMessage = (String) getArguments().get(ARG_DIALOG_MSG);
            mPasswordLengthInBytes = (int) getArguments().get(PWD_LENGTH_IN_BYTES);
        }

        mHandler = new Handler();

        if (mListener == null) {
            // No listener was passed to newInstance(). Assume the activity is used as listener
            mListener = (PwdDialogListener) getActivity();
            Log.v(TAG, "mListener = " + mListener);
        }

        setStyle(STYLE_NO_TITLE, 0); // remove title from DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String message;

        // Inflate the layout for this fragment
        mCurFragmentView = inflater.inflate(R.layout.fragment_pwd_dialog, container, false);

        mFragmentManager = getFragmentManager();

        LinearLayout pwdByte7To4Layout = (LinearLayout) mCurFragmentView.findViewById(R.id.pwdByte7To4Layout);
        pwdByte7To4Layout.setVisibility(View.GONE);
        LinearLayout pwdByte11To8Layout = (LinearLayout) mCurFragmentView.findViewById(R.id.pwdByte11To8Layout);
        pwdByte11To8Layout.setVisibility(View.GONE);
        LinearLayout pwdByte15To12Layout = (LinearLayout) mCurFragmentView.findViewById(R.id.pwdByte15To12Layout);
        pwdByte15To12Layout.setVisibility(View.GONE);


        switch (mPasswordLengthInBytes) {

            case 4:
                message = mDialogMessage + "\n" + getResources().getString(R.string.thirty_two_bits_pwd);
                break;
            case 8:
                message = mDialogMessage + "\n" + getResources().getString(R.string.sixty_four_bits_pwd);
                pwdByte7To4Layout.setVisibility(View.VISIBLE);
                break;
            case 12:
                message = mDialogMessage + "\n" + getResources().getString(R.string.ninety_six_bits_pwd);
                pwdByte7To4Layout.setVisibility(View.VISIBLE);
                pwdByte11To8Layout.setVisibility(View.VISIBLE);
                break;
            case 16:
                message = mDialogMessage + "\n" + getResources().getString(R.string.one_hundred_twenty_height_bits_pwd);
                pwdByte7To4Layout.setVisibility(View.VISIBLE);
                pwdByte11To8Layout.setVisibility(View.VISIBLE);
                pwdByte15To12Layout.setVisibility(View.VISIBLE);
                break;
            default:
                message = mDialogMessage + "\n" + "Password Length not yet implemented";
                break;
        }

        TextView messageTextView = (TextView) mCurFragmentView.findViewById(R.id.messageTextView);
        messageTextView.setText(message);

        Button okButton = (Button) mCurFragmentView.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPassword = getPasswordTypedByUser();
                mIsPwdOk = true;
                dismiss();

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


    private byte getInputByte(int position) {
        EditText byteEditText;

        switch (position) {
            case 0:
                byteEditText = (EditText) mCurFragmentView.findViewById(R.id.byte0EditText);
                return (byte) Integer.parseInt(byteEditText.getText().toString(), 16);                 // This is the LSB
            case 1:
                byteEditText = (EditText) mCurFragmentView.findViewById(R.id.byte1EditText);
                return (byte) Integer.parseInt(byteEditText.getText().toString(), 16);
            case 2:
                byteEditText = (EditText) mCurFragmentView.findViewById(R.id.byte2EditText);
                return (byte) Integer.parseInt(byteEditText.getText().toString(), 16);
            case 3:
                byteEditText = (EditText) mCurFragmentView.findViewById(R.id.byte3EditText);
                return (byte) Integer.parseInt(byteEditText.getText().toString(), 16);                 // This is the MSB
            case 4:
                byteEditText = (EditText) mCurFragmentView.findViewById(R.id.byte4EditText);
                return (byte) Integer.parseInt(byteEditText.getText().toString(), 16);
            case 5:
                byteEditText = (EditText) mCurFragmentView.findViewById(R.id.byte5EditText);
                return (byte) Integer.parseInt(byteEditText.getText().toString(), 16);
            case 6:
                byteEditText = (EditText) mCurFragmentView.findViewById(R.id.byte6EditText);
                return (byte) Integer.parseInt(byteEditText.getText().toString(), 16);
            case 7:
                byteEditText = (EditText) mCurFragmentView.findViewById(R.id.byte7EditText);
                return (byte) Integer.parseInt(byteEditText.getText().toString(), 16);                 // This is the MSB
            case 8:
                byteEditText = (EditText) mCurFragmentView.findViewById(R.id.byte8EditText);
                return (byte) Integer.parseInt(byteEditText.getText().toString(), 16);                 // This is the LSB
            case 9:
                byteEditText = (EditText) mCurFragmentView.findViewById(R.id.byte9EditText);
                return (byte) Integer.parseInt(byteEditText.getText().toString(), 16);
            case 10:
                byteEditText = (EditText) mCurFragmentView.findViewById(R.id.byte10EditText);
                return (byte) Integer.parseInt(byteEditText.getText().toString(), 16);
            case 11:
                byteEditText = (EditText) mCurFragmentView.findViewById(R.id.byte11EditText);
                return (byte) Integer.parseInt(byteEditText.getText().toString(), 16);
            case 12:
                byteEditText = (EditText) mCurFragmentView.findViewById(R.id.byte12EditText);
                return (byte) Integer.parseInt(byteEditText.getText().toString(), 16);
            case 13:
                byteEditText = (EditText) mCurFragmentView.findViewById(R.id.byte13EditText);
                return (byte) Integer.parseInt(byteEditText.getText().toString(), 16);
            case 14:
                byteEditText = (EditText) mCurFragmentView.findViewById(R.id.byte14EditText);
                return (byte) Integer.parseInt(byteEditText.getText().toString(), 16);
            case 15:
                byteEditText = (EditText) mCurFragmentView.findViewById(R.id.byte15EditText);
                return (byte) Integer.parseInt(byteEditText.getText().toString(), 16);                 // This is the MSB
            default:
                break;
        }
        return (byte) 0xFF;
    }

    private byte[] getPasswordTypedByUser() {
        byte[] password = new byte[mPasswordLengthInBytes];

        for (int i = 0; i < mPasswordLengthInBytes; i++) {
            password[i] = getInputByte(i);
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
                Toast.makeText(getContext(), getResources().getString(resource_id), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {

        Log.v(TAG, "onDismiss");

        mLock.release();
        super.onDismiss(dialog);
    }

    public byte[] getPassword() {
        show(mFragmentManager, "stType4PwdDialogFragment");
        try {
            mLock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mHandler.post(new Runnable() {
            public void run() {
                if (mListener != null) {
                    if (mIsPwdOk == true) {
                        mListener.onPwdDialogFinish(RESULT_OK, mPassword);
                    } else {
                        mListener.onPwdDialogFinish(RESULT_FAIL, null);
                    }
                }
            }
        });

        if (mIsPwdOk == true)
            return mPassword;
        else
            return null;
    }
}


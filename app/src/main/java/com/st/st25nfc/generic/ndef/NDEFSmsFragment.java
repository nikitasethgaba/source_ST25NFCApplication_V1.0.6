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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.st.st25nfc.R;
import com.st.st25sdk.ndef.NDEFMsg;
import com.st.st25sdk.ndef.SmsRecord;

public class NDEFSmsFragment extends NDEFRecordFragment {

    final static String TAG = "NDEFSmsFragment";

    private View mView;
    private SmsRecord mSmsRecord;
    private int mAction;

    private EditText mCallNumberEditText;
    private EditText mSmsMsgEditText;


    public static NDEFSmsFragment newInstance(Context context) {
        NDEFSmsFragment f = new NDEFSmsFragment();
        /* If needed, pass some argument to the fragment
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        */
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ndef_sms, container, false);
        mView = view;

        Bundle bundle = getArguments();
        if (bundle == null) {
            Log.e(TAG, "Fatal error! Arguments are missing!");
            return null;
        }

        NDEFMsg ndefMsg = (NDEFMsg) bundle.getSerializable(NDEFRecordFragment.NDEFKey);
        int recordNbr = bundle.getInt(NDEFRecordFragment.RecordNbrKey);
        mSmsRecord = (SmsRecord) ndefMsg.getNDEFRecord(recordNbr);

        initFragmentWidgets();

        mAction = bundle.getInt(NDEFEditorFragment.EditorKey);
        if(mAction == NDEFEditorFragment.VIEW_NDEF_RECORD) {
            // We are displaying an existing record. By default it is not editable
            ndefRecordEditable(false);
        } else {
            // We are adding a new TextRecord or editing an existing record
            ndefRecordEditable(true);
        }

        return view;
    }

    private void initFragmentWidgets() {

        mCallNumberEditText = (EditText) mView.findViewById(R.id.call_number);
        mSmsMsgEditText = (EditText) mView.findViewById(R.id.ndef_fragment_sms_msg);

        setContent();
    }

    /**
     * The content from the fragment is saved into the NDEF Record
     */
    @Override
    public void updateContent() {
        String tel = mCallNumberEditText.getText().toString();
        String msg = mSmsMsgEditText.getText().toString();

        mSmsRecord.setMessage(msg);
        mSmsRecord.setContact(tel);
    }

    /**
     * The content from the NDEF Record is displayed in the Fragment
     */
    public void setContent() {
        mCallNumberEditText.setText(mSmsRecord.getContact());
        mSmsMsgEditText.setText(mSmsRecord.getMessage());
    }

    public void ndefRecordEditable(boolean editable) {
        mCallNumberEditText.setClickable(editable);
        mCallNumberEditText.setFocusable(editable);
        mCallNumberEditText.setFocusableInTouchMode(editable);

        mSmsMsgEditText.setClickable(editable);
        mSmsMsgEditText.setFocusable(editable);
        mSmsMsgEditText.setFocusableInTouchMode(editable);

        if(!editable) {
            // The Fragment is no more editable. Reload its content
            setContent();
        }
    }

}



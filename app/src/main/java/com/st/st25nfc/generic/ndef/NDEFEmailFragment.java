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
import android.widget.TextView;

import com.st.st25nfc.R;
import com.st.st25sdk.ndef.EmailRecord;
import com.st.st25sdk.ndef.NDEFMsg;

public class NDEFEmailFragment extends NDEFRecordFragment {

    final static String TAG = "NDEFEmailFragment";

    private View mView;
    private EmailRecord mEmailRecord;
    private int mAction;

    private TextView mTextEmailContact;
    private TextView mTextEmailMsg;
    private TextView mTextEmailSubject;

    public static NDEFEmailFragment newInstance(Context context) {
        NDEFEmailFragment f = new NDEFEmailFragment();
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

        View view = inflater.inflate(R.layout.fragment_ndef_email, container, false);
        mView = view;

        Bundle bundle = getArguments();
        if (bundle == null) {
            Log.e(TAG, "Fatal error! Arguments are missing!");
            return null;
        }

        NDEFMsg ndefMsg = (NDEFMsg) bundle.getSerializable(NDEFRecordFragment.NDEFKey);
        int recordNbr = bundle.getInt(NDEFRecordFragment.RecordNbrKey);
        mEmailRecord = (EmailRecord) ndefMsg.getNDEFRecord(recordNbr);

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
        mTextEmailContact = (TextView) mView.findViewById(R.id.email_contact);
        mTextEmailMsg = (TextView) mView.findViewById(R.id.email_msg);
        mTextEmailSubject = (TextView) mView.findViewById(R.id.email_subject);

        setContent();
    }

    /**
     * The content from the fragment is saved into the NDEF Record
     */
    @Override
    public void updateContent() {

        String contact = mTextEmailContact.getText().toString();
        String msg = mTextEmailMsg.getText().toString();
        String subject = mTextEmailSubject.getText().toString();

        mEmailRecord.setContact(contact);
        mEmailRecord.setMessage(msg);
        mEmailRecord.setSubject(subject);
    }

    /**
     * The content from the NDEF Record is displayed in the Fragment
     */
    public void setContent() {
        mTextEmailContact.setText(mEmailRecord.getContact());
        mTextEmailMsg.setText(mEmailRecord.getMessage());
        mTextEmailSubject.setText(mEmailRecord.getSubject());
    }

    public void ndefRecordEditable(boolean editable) {
        mTextEmailContact.setClickable(editable);
        mTextEmailContact.setFocusable(editable);
        mTextEmailContact.setFocusableInTouchMode(editable);

        mTextEmailMsg.setClickable(editable);
        mTextEmailMsg.setFocusable(editable);
        mTextEmailMsg.setFocusableInTouchMode(editable);

        mTextEmailSubject.setClickable(editable);
        mTextEmailSubject.setFocusable(editable);
        mTextEmailSubject.setFocusableInTouchMode(editable);

        if(!editable) {
            // The Fragment is no more editable. Reload its content
            setContent();
        }
    }

}



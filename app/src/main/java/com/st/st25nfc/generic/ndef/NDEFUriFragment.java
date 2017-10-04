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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.st.st25nfc.R;
import com.st.st25sdk.ndef.NDEFMsg;
import com.st.st25sdk.ndef.UriRecord;

import java.util.Collection;


public class NDEFUriFragment extends NDEFRecordFragment implements AdapterView.OnItemSelectedListener {

    final static String TAG = "NDEFUriFragment";

    private UriRecord mUriRecord;
    private EditText mUriEditText;
    private Spinner mUriSpinner;
    private int mAction;
    private boolean mIsNdefRecordEditable;

    public static NDEFUriFragment newInstance(Context context) {
        NDEFUriFragment f = new NDEFUriFragment();
        /* If needed, pass some argument to the fragment
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        */
        return f;
    }

    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ndef_uri, container, false);
        mView = view;

        Bundle bundle = getArguments();
        if (bundle == null) {
            Log.e(TAG, "Fatal error! Arguments are missing!");
            return null;
        }

        NDEFMsg ndefMsg = (NDEFMsg) bundle.getSerializable(NDEFRecordFragment.NDEFKey);
        int recordNbr = bundle.getInt(NDEFRecordFragment.RecordNbrKey);
        mUriRecord = (UriRecord) ndefMsg.getNDEFRecord(recordNbr);

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void initFragmentWidgets() {
        mUriEditText = (EditText) mView.findViewById(R.id.ndef_fragment_uri_text);
        mUriSpinner = (Spinner) mView.findViewById(R.id.ndef_fragment_uri_title);

        final String defaultUri = getResources().getString(R.string.st_uri);

        mUriEditText.setOnTouchListener( new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mIsNdefRecordEditable) {
                    if (mUriEditText.getText().toString().equals(defaultUri)){
                        mUriEditText.setText("");
                    }
                }
                return false;
            }
        });

        mUriEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && TextUtils.isEmpty(mUriEditText.getText().toString())){
                    mUriEditText.setText(defaultUri);
                }
            }
        });

        Collection<String> spinnerList = UriRecord.getUriCodesList();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.spinner_text_view){
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position%2 == 1) {
                    // Set the item background color
                    tv.setBackgroundColor(getResources().getColor(R.color.st_light_grey));
                }
                else {
                    // Set the alternate item background color
                    tv.setBackgroundColor(getResources().getColor(R.color.st_very_light_blue));
                }
                return view;
            }
        };
        spinnerAdapter.addAll(spinnerList);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_text_view);
        mUriSpinner.setAdapter(spinnerAdapter);
        mUriSpinner.setOnItemSelectedListener(this);

        setContent();
    }

    /**
     * The content from the NDEF Record is displayed in the Fragment
     */
    public void setContent() {
        String uri = mUriRecord.getContent();
        mUriEditText.setText(uri);

        UriRecord.NdefUriIdCode uriCode = mUriRecord.getUriID();
        mUriSpinner.setSelection(UriRecord.getUriCodePositionInList(uriCode));
    }

    /**
     * The content from the fragment is saved into the NDEF Record
     */
    @Override
    public void updateContent() {
        UriRecord.NdefUriIdCode uriID = UriRecord.getUriCodeFromStr((String) mUriSpinner.getSelectedItem());
        mUriRecord.setUriID(uriID);

        String text = mUriEditText.getText().toString();
        mUriRecord.setContent(text);
    }


    @Override
    public void ndefRecordEditable(boolean editable) {
        mIsNdefRecordEditable = editable;

        mUriEditText.setClickable(editable);
        mUriEditText.setFocusable(editable);
        mUriEditText.setFocusableInTouchMode(editable);

        mUriSpinner.setClickable(editable);
        mUriSpinner.setFocusable(editable);
        mUriSpinner.setFocusableInTouchMode(editable);
        mUriSpinner.setEnabled(editable);

        if(!editable) {
            // The Fragment is no more editable. Reload its content
            setContent();
        }
    }

}



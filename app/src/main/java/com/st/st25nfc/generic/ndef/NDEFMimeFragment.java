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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.st.st25sdk.Helper;
import com.st.st25sdk.ndef.MimeRecord;
import com.st.st25sdk.ndef.NDEFMsg;

import java.util.Collection;


public class NDEFMimeFragment extends NDEFRecordFragment implements AdapterView.OnItemSelectedListener {

    final static String TAG = "NDEFMimeFragment";

    private View mView;
    private String nameDefaultValue;
    boolean editing = false; // all EditText-s use the same editing flag
    private MimeRecord mMimeRecord;
    private int mAction;

    private EditText mMimeContentEditText;
    private EditText mMimeContentEditHexaText;
    private Spinner mMimeTitleSpinner;

    public static NDEFMimeFragment newInstance(Context context) {
        NDEFMimeFragment f = new NDEFMimeFragment();
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

        View view = inflater.inflate(R.layout.fragment_ndef_mime, container, false);
        mView = view;

        Bundle bundle = getArguments();
        if (bundle == null) {
            Log.e(TAG, "Fatal error! Arguments are missing!");
            return null;
        }

        NDEFMsg ndefMsg = (NDEFMsg) bundle.getSerializable(NDEFRecordFragment.NDEFKey);
        int recordNbr = bundle.getInt(NDEFRecordFragment.RecordNbrKey);
        mMimeRecord = (MimeRecord) ndefMsg.getNDEFRecord(recordNbr);

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
        mMimeContentEditText = (EditText) mView.findViewById(R.id.ndef_fragment_mime_content);
        mMimeContentEditHexaText = (EditText) mView.findViewById(R.id.ndef_fragment_mime_content_hexa);
        mMimeTitleSpinner = (Spinner) mView.findViewById(R.id.ndef_fragment_mime_title);

        nameDefaultValue = MimeRecord.DEFAULT_MIME_TYPE_FORMAT;
        mMimeContentEditText.setOnTouchListener( new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mMimeContentEditText.getText().toString().equals(nameDefaultValue)){
                    mMimeContentEditText.setText("");
                }
                return false;
            }
        });
        mMimeContentEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && TextUtils.isEmpty(mMimeContentEditText.getText().toString())){
                    mMimeContentEditText.setText(nameDefaultValue);
                } else if (hasFocus && mMimeContentEditText.getText().toString().equals(nameDefaultValue)){
                    mMimeContentEditText.setText("");
                }
            }
        });

        Collection<String> spinnerList = MimeRecord.getMimeCodesList();
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
        mMimeTitleSpinner.setAdapter(spinnerAdapter);
        mMimeTitleSpinner.setOnItemSelectedListener(this);

        mMimeContentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String qtyString = s.toString().trim();
                if(!editing)
                {
                    editing = true;
                    if(qtyString.length() > 0){
                        mMimeContentEditHexaText.setText(formatToHexa(qtyString.getBytes()));
                    }
                    else{
                        mMimeContentEditHexaText.setText("");
                    }
                    editing = false;
                }

            }
        });

        mMimeContentEditHexaText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String qtyString = s.toString().trim();
                if(!editing)
                {
                    editing = true;
                    if(qtyString.length() > 0){
                        mMimeContentEditText.setText(convertHexToString(qtyString));
                    }
                    else{
                        mMimeContentEditText.setText("");
                    }
                    editing = false;
                }

            }
        });

        setContent();
    }

    /**
     * The content from the fragment is saved into the NDEF Record
     */
    @Override
    public void updateContent() {
        MimeRecord.NdefMimeIdCode mimeID = MimeRecord.getMimeCodeFromStr((String) mMimeTitleSpinner.getSelectedItem());

        byte[] content = mMimeContentEditText.getText().toString().getBytes();

        mMimeRecord.setContent(content);
        mMimeRecord.setMimeID(mimeID);
    }


    public String convertHexToString(String hex){

        String ascii="";
        String str;

        // Convert hex string to "even" length
        int rmd,length;
        length=hex.length();
        rmd =length % 2;
        if(rmd==1)
            hex = hex + "0";

        // split into two characters
        for( int i=0; i<hex.length()-1; i+=2 ){

            //split the hex into pairs
            String pair = hex.substring(i, (i + 2));
            //convert hex to decimal
            int dec = Integer.parseInt(pair, 16);
            str=CheckCode(dec);
            ascii=ascii+str;
        }
        return ascii;
    }

    public String CheckCode(int dec){
        String str;

        //convert the decimal to character
        str = Character.toString((char) dec);

        if(dec<32 || dec>126 && dec<161)
            str="n/a";
        return str;
    }
    private char getChar(byte myByte) {
        char myChar = ' ';

        if(myByte > 0x20) {
            myChar = (char) (myByte & 0xFF);
        }

        return myChar;
    }

    private String formatToHexa(byte[] content) {
        String data = "";
        Byte myByte;
        String bytexStr = "  ";

        if(content == null) {
            return "";
        }

        int bufferLength = content.length;

        for (int i = 0; i < bufferLength; i++) {
            myByte = content[i];
            bytexStr = Helper.convertByteToHexString(myByte).toUpperCase();
            data = data + String.format("%s", bytexStr);
        }
        return data;
    }
    private String formatToAscii(byte[] content) {
        String data = "";
        Byte myByte;
        char charx = ' ';

        if(content == null) {
            return "";
        }

        int bufferLength = content.length;

        for (int i = 0; i < bufferLength; i++) {
            myByte = content[i];
            charx = getChar(myByte);
            data = data + String.format("%c", charx);
        }
        return data;
    }

    /**
     * The content from the NDEF Record is displayed in the Fragment
     */
    public void setContent() {
        // ascii
        String mimeContent = formatToAscii(mMimeRecord.getContent());
        mMimeContentEditText.setText(mimeContent);

        // Hexa
        String mimeContentHexa = formatToHexa(mMimeRecord.getContent());
        mMimeContentEditHexaText.setText(mimeContentHexa);

        MimeRecord.NdefMimeIdCode mimeCode = mMimeRecord.getMimeID();
        mMimeTitleSpinner.setSelection(MimeRecord.getMimeCodePositionInList(mimeCode));
    }

    public void ndefRecordEditable(boolean editable) {
        mMimeContentEditText.setClickable(editable);
        mMimeContentEditText.setFocusable(editable);
        mMimeContentEditText.setFocusableInTouchMode(editable);

        mMimeContentEditHexaText.setClickable(editable);
        mMimeContentEditHexaText.setFocusable(editable);
        mMimeContentEditHexaText.setFocusableInTouchMode(editable);

        mMimeTitleSpinner.setClickable(editable);
        mMimeTitleSpinner.setFocusable(editable);
        mMimeTitleSpinner.setFocusableInTouchMode(editable);
        mMimeTitleSpinner.setEnabled(editable);

        if(!editable) {
            // The Fragment is no more editable. Reload its content
            setContent();
        }
    }
}



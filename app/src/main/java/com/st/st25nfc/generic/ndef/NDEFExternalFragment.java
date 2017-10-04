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
import android.widget.EditText;


import com.st.st25nfc.R;
import com.st.st25sdk.Helper;
import com.st.st25sdk.ndef.ExternalRecord;
import com.st.st25sdk.ndef.NDEFMsg;


public class NDEFExternalFragment extends NDEFRecordFragment implements AdapterView.OnItemSelectedListener {

    final static String TAG = "NDEFExternalFragment";

    private View mView;
    private String nameDomainDefaultValue;
    private String nameTypeDefaultValue;
    boolean editing = false; // all EditText-s use the same editing flag
    private ExternalRecord mExternalRecord;
    private int mAction;

    private EditText mExternalContentEditText;
    private EditText mExternalContentHexaEditText;
    private EditText mDomainContentEditText;
    private EditText mTypeContentEditText;


    public static NDEFExternalFragment newInstance(Context context) {
        NDEFExternalFragment f = new NDEFExternalFragment();
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

        View view = inflater.inflate(R.layout.fragment_ndef_external, container, false);
        mView = view;

        Bundle bundle = getArguments();
        if (bundle == null) {
            Log.e(TAG, "Fatal error! Arguments are missing!");
            return null;
        }

        NDEFMsg ndefMsg = (NDEFMsg) bundle.getSerializable(NDEFRecordFragment.NDEFKey);
        int recordNbr = bundle.getInt(NDEFRecordFragment.RecordNbrKey);
        mExternalRecord = (ExternalRecord) ndefMsg.getNDEFRecord(recordNbr);

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
        mExternalContentEditText = (EditText) mView.findViewById(R.id.ndef_fragment_external_content);
        mExternalContentHexaEditText = (EditText) mView.findViewById(R.id.ndef_fragment_external_content_hexa);
        mDomainContentEditText = (EditText) mView.findViewById(R.id.ndef_fragment_domain_content);
        mTypeContentEditText = (EditText) mView.findViewById(R.id.ndef_fragment_type_content);

        nameDomainDefaultValue = ExternalRecord.DEFAULT_EXTERNAL_DOMAIN_FORMAT;
        mDomainContentEditText.setOnTouchListener( new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mDomainContentEditText.getText().toString().equals(nameDomainDefaultValue)){
                    mDomainContentEditText.setText("");
                }
                return false;
            }
        });
        mDomainContentEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && TextUtils.isEmpty(mDomainContentEditText.getText().toString())){
                    mDomainContentEditText.setText(nameDomainDefaultValue);
                } else if (hasFocus && mDomainContentEditText.getText().toString().equals(nameDomainDefaultValue)){
                    mDomainContentEditText.setText("");
                }
            }
        });

        nameTypeDefaultValue = ExternalRecord.DEFAULT_EXTERNAL_DOMAIN_FORMAT;
        mTypeContentEditText.setOnTouchListener( new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mTypeContentEditText.getText().toString().equals(nameTypeDefaultValue)){
                    mTypeContentEditText.setText("");
                }
                return false;
            }
        });
        mTypeContentEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && TextUtils.isEmpty(mTypeContentEditText.getText().toString())){
                    mTypeContentEditText.setText(nameTypeDefaultValue);
                } else if (hasFocus && mTypeContentEditText.getText().toString().equals(nameTypeDefaultValue)){
                    mTypeContentEditText.setText("");
                }
            }
        });

        mExternalContentEditText.addTextChangedListener(new TextWatcher() {
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
                        mExternalContentHexaEditText.setText(formatToHexa(qtyString.getBytes()));
                    }
                    else{
                        mExternalContentHexaEditText.setText("");
                    }
                    editing = false;
                }

            }
        });

        mExternalContentHexaEditText.addTextChangedListener(new TextWatcher() {
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
                        mExternalContentEditText.setText(convertHexToString(qtyString));
                    }
                    else{
                        mExternalContentEditText.setText("");
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
        byte[] content = mExternalContentEditText.getText().toString().getBytes();
        String domain = mDomainContentEditText.getText().toString();
        String type = mTypeContentEditText.getText().toString();

        mExternalRecord.setContent(content);
        mExternalRecord.setExternalDomain(domain);
        mExternalRecord.setExternalType(type);
    }


    private String convertHexToString(String hex){
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

    private String CheckCode(int dec){
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
        mExternalContentEditText.setText(formatToAscii(mExternalRecord.getContent()));
        // Hexa
        mExternalContentHexaEditText.setText(formatToHexa(mExternalRecord.getContent()));
        mDomainContentEditText.setText(mExternalRecord.getExternalDomain());
        mTypeContentEditText.setText(mExternalRecord.getExternalType());
    }

    public void ndefRecordEditable(boolean editable) {
        mExternalContentEditText.setClickable(editable);
        mExternalContentEditText.setFocusable(editable);
        mExternalContentEditText.setFocusableInTouchMode(editable);

        mExternalContentHexaEditText.setClickable(editable);
        mExternalContentHexaEditText.setFocusable(editable);
        mExternalContentHexaEditText.setFocusableInTouchMode(editable);

        mDomainContentEditText.setClickable(editable);
        mDomainContentEditText.setFocusable(editable);
        mDomainContentEditText.setFocusableInTouchMode(editable);

        mTypeContentEditText.setClickable(editable);
        mTypeContentEditText.setFocusable(editable);
        mTypeContentEditText.setFocusableInTouchMode(editable);

        if(!editable) {
            // The Fragment is no more editable. Reload its content
            setContent();
        }
    }
}



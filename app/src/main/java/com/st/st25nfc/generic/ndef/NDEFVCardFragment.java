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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.st.st25nfc.R;
import com.st.st25sdk.ndef.NDEFMsg;
import com.st.st25sdk.ndef.VCardRecord;

import java.io.ByteArrayOutputStream;

public class NDEFVCardFragment extends NDEFRecordFragment {

    final static String TAG = "NDEFVCardFragment";

    private View mView;
    private int mSeekPhotoCurPos;

    private TextView mSeekTextView;
    private EditText mContactAddressEditText;
    private EditText mContactNameEditText;
    private EditText mContactEmailEditText;
    private EditText mContactNumberEditText;
    private EditText mContactWebsiteEditText;
    private ImageView mPhotoImageView;
    private CheckBox mPhotoCheckBox;
    private Button mCapturePhotoButton;
    private Button mGetContactButton;
    private SeekBar mPhotoQualitySeekBar;

    private int mCompressRate = 50;

    private boolean mPictureExport = false;
    private VCardRecord mVCardRecord;
    private int mAction;




    public static NDEFVCardFragment newInstance(Context context) {
        NDEFVCardFragment f = new NDEFVCardFragment();
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

        View view = inflater.inflate(R.layout.fragment_ndef_vcard, container, false);
        mView = view;

        Bundle bundle = getArguments();
        if (bundle == null) {
            Log.e(TAG, "Fatal error! Arguments are missing!");
            return null;
        }

        NDEFMsg ndefMsg = (NDEFMsg) bundle.getSerializable(NDEFRecordFragment.NDEFKey);
        int recordNbr = bundle.getInt(NDEFRecordFragment.RecordNbrKey);
        mVCardRecord = (VCardRecord) ndefMsg.getNDEFRecord(recordNbr);

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

    private int estimatedVCardSize(Bitmap bitmap) {

        String encodedImage = tranformPhoto(bitmap);
        return encodedImage.length();
    }
    private void updatePhotoInformationFields() {
        int imgSize = 0;

        if (mPhotoImageView != null) {
            Bitmap bitmap = mPhotoImageView.getDrawable() == null ? null : ((BitmapDrawable) mPhotoImageView.getDrawable()).getBitmap();
            if (bitmap != null) {
                imgSize = estimatedVCardSize(bitmap);
            }

        }
        mSeekTextView.setText("Photo(bytes)[" + (int)mCompressRate + "] :" + imgSize);

    }

    private String tranformPhoto(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, mCompressRate, outputStream);
        byte[] b = outputStream.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }


    public void setPhotoContact(Bitmap picture) {
        mPhotoImageView.setImageBitmap(picture);
    }

    private void initFragmentWidgets() {

        mContactAddressEditText = (EditText) mView.findViewById(R.id.edit_contact_address);
        mContactNameEditText = (EditText) mView.findViewById(R.id.edit_contact_name);
        mContactEmailEditText = (EditText) mView.findViewById(R.id.edit_contact_email);
        mContactNumberEditText = (EditText) mView.findViewById(R.id.edit_contact_number);
        mContactWebsiteEditText = (EditText) mView.findViewById(R.id.edit_contact_website);
        mPhotoImageView = (ImageView) mView.findViewById(R.id.photoView);
        mPhotoCheckBox = (CheckBox) mView.findViewById(R.id.capture_photo_checkbox);
        mCapturePhotoButton = (Button) mView.findViewById(R.id.capturePhotoButton);
        mGetContactButton = (Button) mView.findViewById(R.id.getContactButton);
        mPhotoQualitySeekBar = (SeekBar) mView.findViewById(R.id.vcard_photo_quality_slider);
        mSeekTextView = (TextView) mView.findViewById(R.id.vcard_seekbar_quality);


        mPhotoCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                     @Override
                                                     public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                         mPictureExport = isChecked;
                                                     }
                                                 }
        );

        mCapturePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mGetContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Not implemented yet");
            }
        });

        mSeekPhotoCurPos = 80;    //you need to give starting position value of SeekBar
        mCompressRate = mSeekPhotoCurPos;
        //TextView seekText = (TextView) mView.findViewById(R.id.SeekBarLabel);
        mPhotoQualitySeekBar.setProgress((int) mSeekPhotoCurPos);


        mPhotoQualitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSeekPhotoCurPos = progress;
                mCompressRate = mSeekPhotoCurPos;
                updatePhotoInformationFields();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getContext(), "discrete = " + String.valueOf(mSeekPhotoCurPos), Toast.LENGTH_SHORT).show();
            }
        });

        setContent();
    }

    /**
     * The content from the fragment is saved into the NDEF Record
     */
    @Override
    public void updateContent() {
        String contactAddress = mContactAddressEditText.getText().toString();
        String contactName = mContactNameEditText.getText().toString();
        String contactEmail = mContactEmailEditText.getText().toString();
        String contactNumber = mContactNumberEditText.getText().toString();
        String contactWebsite = mContactWebsiteEditText.getText().toString();

        BitmapDrawable drawable = (BitmapDrawable) mPhotoImageView.getDrawable();
        Bitmap photo = null;
        if (drawable != null) {
            photo = drawable.getBitmap();
        }

        mVCardRecord.setSPAddr(contactAddress);
        mVCardRecord.setName(contactName);
        mVCardRecord.setEmail(contactEmail);
        mVCardRecord.setNumber(contactNumber);
        mVCardRecord.setWebSite(contactWebsite);
    }

    /**
     * The content from the NDEF Record is displayed in the Fragment
     */
    public void setContent() {
        String address = mVCardRecord.getStructPostalAddr();
        mContactAddressEditText.setText(address);

        String name = mVCardRecord.getFormattedName();
        mContactNameEditText.setText(name);

        String email = mVCardRecord.getEmail();
        mContactEmailEditText.setText(email);

        String number = mVCardRecord.getNumber();
        mContactNumberEditText.setText(number);

        String webSite = mVCardRecord.getWebSiteAddr();
        mContactWebsiteEditText.setText(webSite);

        String photoString = mVCardRecord.getPhoto();
        Bitmap decodedByte = null;
        if (photoString != null) {
            byte[] decodedString = Base64.decode(photoString, Base64.DEFAULT);
            decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }

        if (decodedByte != null) {
            mPhotoImageView.setImageBitmap(decodedByte);
        }
    }

    public void ndefRecordEditable(boolean editable) {
        mContactNameEditText.setClickable(editable);
        mContactNameEditText.setFocusable(editable);
        mContactNameEditText.setFocusableInTouchMode(editable);

        mContactNumberEditText.setClickable(editable);
        mContactNumberEditText.setFocusable(editable);
        mContactNumberEditText.setFocusableInTouchMode(editable);

        mContactEmailEditText.setClickable(editable);
        mContactEmailEditText.setFocusable(editable);
        mContactEmailEditText.setFocusableInTouchMode(editable);

        mContactAddressEditText.setClickable(editable);
        mContactAddressEditText.setFocusable(editable);
        mContactAddressEditText.setFocusableInTouchMode(editable);

        mContactWebsiteEditText.setClickable(editable);
        mContactWebsiteEditText.setFocusable(editable);
        mContactWebsiteEditText.setFocusableInTouchMode(editable);

        if(!editable) {
            // The Fragment is no more editable. Reload its content
            setContent();
        }
    }

}



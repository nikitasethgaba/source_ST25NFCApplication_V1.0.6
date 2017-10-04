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

package com.st.st25nfc.type5;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.st.st25sdk.STException;
import com.st.st25sdk.NFCTag;
import com.st.st25nfc.R;
import com.st.st25nfc.generic.STFragment;

public class CCFileType5Fragment extends STFragment {

    public static CCFileType5Fragment newInstance(Context context) {
        CCFileType5Fragment f = new CCFileType5Fragment();
        /* If needed, pass some argument to the fragment
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        */

        // Set the title of this fragment
        f.setTitle(context.getResources().getString(R.string.cc_file));

        return f;
    }

    public CCFileType5Fragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_cc_file_type5, container, false);

        super.onCreateView(inflater, container, savedInstanceState);
        mView = view;

        initView();

        return (View) view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected class FillViewTask extends STFragment.FillViewTask {
        TextView mTagNameView;
        TextView mTagDescriptionView;
        TextView mTagTypeView;
        TextView mLengthView;
        TextView mMagicNumView;
        TextView mMappingVersionView;
        TextView mMemSizeView;
        TextView mReadAccessView;
        TextView mWriteAccessView;

        String mTagName;
        String mTagDescription;
        String mTagType;
        String mLength;
        String mMagicNum;
        String mMappingVersion;
        String mMemSize;
        String mReadAccess;
        String mWriteAccess;



        public FillViewTask() {

        }

        @Override
        protected Integer doInBackground(NFCTag... param) {
            if (myTag != null) {
                mTagName = myTag.getName();
                mTagDescription = myTag.getDescription();
                mTagType = myTag.getTypeDescription();
                try {
                    mLength = String.format("%d", myTag.getCCFileLength());
                    mMagicNum = String.format("%2X", (int) (myTag.getCCMagicNumber()&0xFF));
                    mMappingVersion = String.format("%X", (int) (myTag.getCCMappingVersion()&0xFF));
                    mMemSize = String.format("%d", myTag.getCCMemorySize());
                    mReadAccess = String.format("%X", myTag.getCCReadAccess());
                    mWriteAccess = String.format("%X", myTag.getCCWriteAccess());
                } catch (STException e) {
                    mLength = "Error";
                    return -1;
                }

                return 0;
            }
            return -1;
        }

        @Override
        protected void onPreExecute() {
            if (mView != null) {
                // In Progress
                RelativeLayout tagInformationRetrieval = (RelativeLayout) mView.findViewById(R.id.XX_file_retrieval_ongoing);
                tagInformationRetrieval.setVisibility(View.VISIBLE);
                // Hide inforamtion not available
                RelativeLayout tagCCInformationView = (RelativeLayout) mView.findViewById(R.id.cc_file_not_available);
                tagCCInformationView.setVisibility(View.GONE);
                // Hide result
                tagCCInformationView = (RelativeLayout) mView.findViewById(R.id.cc_file);
                tagCCInformationView.setVisibility(View.GONE);
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            // In Progress
            RelativeLayout tagInformationRetrieval = (RelativeLayout) mView.findViewById(R.id.XX_file_retrieval_ongoing);
            tagInformationRetrieval.setVisibility(View.GONE);

            if (result == 0) {
                if (mView != null) {
                    mTagNameView = (TextView) mView.findViewById(R.id.model_header);
                    mTagTypeView = (TextView) mView.findViewById(R.id.model_type);
                    mTagDescriptionView = (TextView) mView.findViewById(R.id.model_description);
                    mLengthView = (TextView) mView.findViewById(R.id.cc_length);
                    mMagicNumView = (TextView) mView.findViewById(R.id.cc_magic_number);
                    mMappingVersionView = (TextView) mView.findViewById(R.id.cc_mapping_version);
                    mMemSizeView = (TextView) mView.findViewById(R.id.memory_size);
                    mReadAccessView = (TextView) mView.findViewById(R.id.cc_read_access);
                    mWriteAccessView = (TextView) mView.findViewById(R.id.cc_write_access);

                    mTagNameView.setText(mTagName);
                    mTagTypeView.setText(mTagDescription);
                    mTagDescriptionView.setText(mTagType);

                    RelativeLayout tagCCInformationView = (RelativeLayout) mView.findViewById(R.id.cc_file_not_available);
                    Typeface textTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                    if (mLength.startsWith("-1") ) {
                        tagCCInformationView.setVisibility(View.VISIBLE);
                        textTypeface = Typeface.defaultFromStyle(Typeface.ITALIC);
                        tagCCInformationView = (RelativeLayout) mView.findViewById(R.id.cc_file);
                        tagCCInformationView.setVisibility(View.GONE); // remove for debug if necessary

                    } else {
                        tagCCInformationView.setVisibility(View.GONE);
                        tagCCInformationView = (RelativeLayout) mView.findViewById(R.id.cc_file);
                        tagCCInformationView.setVisibility(View.VISIBLE); // remove for debug if necessary
                    }
                    mLengthView.setText(mLength);
                    mLengthView.setTypeface(textTypeface);
                    mMagicNumView.setText(mMagicNum);
                    mMagicNumView.setTypeface(textTypeface);
                    mMappingVersionView.setText(mMappingVersion);
                    mMappingVersionView.setTypeface(textTypeface);
                    mMemSizeView.setText(mMemSize);
                    mMemSizeView.setTypeface(textTypeface);
                    mReadAccessView.setText(mReadAccess);
                    mReadAccessView.setTypeface(textTypeface);
                    mWriteAccessView.setText(mWriteAccess);
                    mWriteAccessView.setTypeface(textTypeface);
                }
            } else {
                if (mView != null) {
                    RelativeLayout tagCCInformationView = (RelativeLayout) mView.findViewById(R.id.cc_file_not_available);
                    RelativeLayout tagCCView = (RelativeLayout) mView.findViewById(R.id.cc_file);
                    if (mLength.startsWith("Error") ) {
                        tagCCInformationView.setVisibility(View.VISIBLE);
                        tagCCView.setVisibility(View.GONE);
                    } else {
                        tagCCInformationView.setVisibility(View.VISIBLE);
                        tagCCView.setVisibility(View.VISIBLE);
                    }

                }
            }
        }

    }

    @Override
    public void fillView() {
        new FillViewTask().execute(myTag);
    }
}

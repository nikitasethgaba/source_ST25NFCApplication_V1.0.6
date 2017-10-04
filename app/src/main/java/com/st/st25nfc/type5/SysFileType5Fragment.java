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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.st.st25sdk.Helper;
import com.st.st25sdk.STException;
import com.st.st25sdk.NFCTag;
import com.st.st25nfc.R;
import com.st.st25nfc.generic.STFragment;
import com.st.st25sdk.type5.Type5Tag;

public class SysFileType5Fragment extends STFragment {

    private Type5Tag mType5Tag = null;
    final static String TAG = "SysFileType5Fragment";

    public static SysFileType5Fragment newInstance(Context context) {
        SysFileType5Fragment f = new SysFileType5Fragment();
        /* If needed, pass some argument to the fragment
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        */

        // Set the title of this fragment
        f.setTitle(context.getResources().getString(R.string.system_file));

        return f;
    }

    public SysFileType5Fragment() {
    }

    @Override
    protected void setTag() {
        super.setTag();

        if (myTag instanceof Type5Tag) {
            mType5Tag = (Type5Tag) myTag;
        } else {
            Log.e(TAG, "Error! This tag is not a Type5Tag!");
            mType5Tag = null;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_sys_file_type5, container, false);
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

    private class FillViewTask extends STFragment.FillViewTask {
        TextView mTagNameView;
        TextView mTagDescriptionView;
        TextView mTagTypeView;
        TextView mLengthView;
        TextView mDSFIDView;
        TextView mAFIView;
        TextView mMemSizeView;
        TextView mBlocksView;
        TextView mBytesPerBlocksView;
        TextView mUidView;
        TextView mProductCodeView;
        String mTagName;
        String mTagDescription;
        String mTagType;
        String mLength;
        String mDSFID;
        String mAFI;
        String mMemSize;
        String mBlocks;
        String mBytesPerBlocks;
        String mUid;
        String mProductCode;


        public FillViewTask() {

        }

        @Override
        protected Integer doInBackground(NFCTag... param) {
            if (mType5Tag != null) {
                mTagName = mType5Tag.getName();
                mTagDescription = mType5Tag.getDescription();
                mTagType = mType5Tag.getTypeDescription();

                try {
                    mLength = String.format("%d", mType5Tag.getSysFileLength());
                    mMemSize = String.format("%d", mType5Tag.getMemSizeInBytes());
                    mBytesPerBlocks = String.format(": %d", mType5Tag.getBlockSizeInBytes());
                    mBlocks = String.format(": %d", mType5Tag.getNumberOfBlocks());
                    mDSFID = ": " + Helper.convertByteToHexString(mType5Tag.getDSFID());
                    mAFI = ": " + Helper.convertByteToHexString(mType5Tag.getAFI());
                    mUid = ": " + mType5Tag.getUidString();
                    mProductCode = ": " + Helper.convertByteToHexString(mType5Tag.getICRef());
                } catch (STException e) {
                    return -1;
                }
                return 0;
            }
            return -1;
        }

        @Override
        protected void onPostExecute(Integer result) {

            if (result == 0) {
                if (mView != null) {
                    mTagNameView = (TextView) mView.findViewById(R.id.model_header);
                    mTagTypeView = (TextView) mView.findViewById(R.id.model_type);
                    mTagDescriptionView = (TextView) mView.findViewById(R.id.model_description);
                    mLengthView = (TextView) mView.findViewById(R.id.sys_length);
                    mDSFIDView = (TextView) mView.findViewById(R.id.sys_dsfid);
                    mAFIView = (TextView) mView.findViewById(R.id.sys_afi);
                    mMemSizeView = (TextView) mView.findViewById(R.id.memory_size);
                    mBlocksView = (TextView) mView.findViewById(R.id.blocks);
                    mBytesPerBlocksView = (TextView) mView.findViewById(R.id.bytes_per_block);
                    mUidView = (TextView) mView.findViewById(R.id.uid);
                    mProductCodeView = (TextView) mView.findViewById(R.id.product_code);

                    mTagNameView.setText(mTagName);
                    mTagTypeView.setText(mTagDescription);
                    mTagDescriptionView.setText(mTagType);
                    mLengthView.setText(mLength);
                    mDSFIDView.setText(mDSFID);
                    mAFIView.setText(mAFI);
                    mMemSizeView.setText(mMemSize);
                    mBlocksView.setText(mBlocks);
                    mBytesPerBlocksView.setText(mBytesPerBlocks);
                    mUidView.setText(mUid);
                    mProductCodeView.setText(mProductCode);
                }
            }
        }

    }

    @Override
    public void fillView() {
        new FillViewTask().execute(myTag);
    }

}



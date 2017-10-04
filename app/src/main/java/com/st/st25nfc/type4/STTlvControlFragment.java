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

package com.st.st25nfc.type4;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.st.st25sdk.Helper;
import com.st.st25nfc.R;
import com.st.st25sdk.STException;
import com.st.st25sdk.NFCTag;
import com.st.st25nfc.generic.STFragment;
import com.st.st25sdk.type4a.ControlTlv;
import com.st.st25sdk.type4a.Type4Tag;


public class STTlvControlFragment extends STFragment {

    private ListView mLv;
    private Handler mHandler;

    private CustomListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.tag_control_tlv, container, false);
        Bundle args = getArguments();

        initView();

        mLv = (ListView) mView.findViewById(R.id.tag_ccffile_tlv_view);
        mAdapter = new CustomListAdapter();
        mHandler = new Handler();

        if (mHandler != null && mLv != null) {
            mLv.setAdapter(mAdapter);
        }
        return (View) mView;
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
        TextView mTlvTypeView;
        TextView mTlvLengthView;
        TextView mTlvFileIdView;
        TextView mTlvFileSizeView;
        TextView mTlvReadAccessView;
        TextView mTlvWriteAccessView;

        String mTlvType;
        String mTlvLength;
        String mTlvFileId;
        String mTlvFileSize;
        String mTlvFileReadAccess;
        String mTlvFileWriteAccess;

        ControlTlv mCCTlv;
        int mTlvPos;
        private View mView;


        public FillViewTask(int pos, View view) {
            mTlvPos = pos;
            mView = view;
        }

        @Override
        protected Integer doInBackground(NFCTag... param) {
            try {
                mCCTlv = ((Type4Tag) myTag).getCCTlv(mTlvPos);
                if (mCCTlv != null) {
                    mTlvType = ": " + Helper.convertByteToHexString(mCCTlv.getType());
                    mTlvLength = ": " + String.format("%d ", mCCTlv.getLength());
                    mTlvFileId = ": 0x" + Helper.convertIntToHexFormatString(mCCTlv.getFileId());
                    mTlvFileSize = ": " + String.format("%d bytes", mCCTlv.getMaxFileSize());
                    mTlvFileReadAccess = ": " + Helper.convertByteToHexString(mCCTlv.getReadAccess());
                    mTlvFileWriteAccess = ": " + Helper.convertByteToHexString(mCCTlv.getWriteAccess());
                }
            } catch (STException e) {
                return -1;
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {

            if (result == 0) {
                if (mView != null) {
                    mTlvTypeView = (TextView) mView.findViewById(R.id.tlv_type);
                    mTlvLengthView = (TextView) mView.findViewById(R.id.tlv_length);
                    mTlvFileIdView = (TextView) mView.findViewById(R.id.tlv_file_id);
                    mTlvFileSizeView = (TextView) mView.findViewById(R.id.tlv_file_size);
                    mTlvReadAccessView = (TextView) mView.findViewById(R.id.tlv_read_access);
                    mTlvWriteAccessView = (TextView) mView.findViewById(R.id.tlv_write_access);

                    mTlvTypeView.setText(mTlvType);
                    mTlvLengthView.setText(mTlvLength);
                    mTlvFileIdView.setText(mTlvFileId);
                    mTlvFileSizeView.setText(mTlvFileSize);
                    mTlvReadAccessView.setText(mTlvFileReadAccess);
                    mTlvWriteAccessView.setText(mTlvFileWriteAccess);
                }
            }
        }

    }

    @Override
    public void fillView() {

    }

    public void fillView(int pos, View v) {
        new FillViewTask(pos, v).execute(myTag);
    }

    class CustomListAdapter extends BaseAdapter {



        public CustomListAdapter() {

        }

        //get read_list_items count
        @Override
        public int getCount() {
            try {
                return ((Type4Tag) myTag).getNbOfTlv();
            } catch (STException e) {
                return 0;
            }
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        //get read_list_items position
        @Override
        public Object getItem(int position) {
            return position;
        }

        //get read_list_items id at selected position
        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {


            View listItem = convertView;

            if (listItem == null) {
                //set the main ListView's layout
                listItem = getActivity().getLayoutInflater().inflate(R.layout.tlv_type4_item, parent, false);
            }

            fillView(pos, listItem);

            return listItem;
        }
    }

}





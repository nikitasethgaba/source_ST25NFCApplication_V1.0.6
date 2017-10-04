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

package com.st.st25nfc.type4.st25ta;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.st.st25sdk.Helper;
import com.st.st25sdk.NFCTag;
import com.st.st25nfc.R;
import com.st.st25sdk.STException;
import com.st.st25sdk.type4a.st25ta.ST25TATag;
import com.st.st25nfc.generic.STFragment;
import com.st.st25nfc.generic.STHeaderFragment;

public class SysFileST25TAFragment extends STFragment {

    public ST25TATag myTag = null;

    public static SysFileST25TAFragment newInstance(Context context) {
        SysFileST25TAFragment f = new SysFileST25TAFragment();
        /* If needed, pass some argument to the fragment
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        */

        // Set the title of this fragment
        f.setTitle(context.getResources().getString(R.string.system_file));

        return f;
    }

    public SysFileST25TAFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_sys_file_st25ta, container, false);
        mView = view;


        if (myTag == null)
            myTag = (ST25TATag) ((STFragmentListener) getActivity()).getTag();

        fillView();

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
        TextView mLengthView;
        TextView mEventCounterView;
        TextView mBitsCounterView;
        TextView mUidView;
        TextView mMemSizeView;
        TextView mProductCodeView;
        TextView mProductVersionView;

        String mLength;
        String mEventCounter;
        String mBitsCounter;
        String mUid;
        String mMemSize;
        String mProductCode;
        String mProductVersion;


        public FillViewTask() {

        }

        @Override
        protected Integer doInBackground(NFCTag... param) {
            if (myTag != null) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                STHeaderFragment headerFragment = new STHeaderFragment();
                fragmentTransaction.replace(R.id.sys_type4_header_fragment_container, headerFragment);
                fragmentTransaction.commit();

                try {
                    mLength = String.format("%d", myTag.getSysFileLength());
                    mEventCounter = ": " + Helper.convertByteToHexString(myTag.getEventCounter());
                    mMemSize = String.format("%d", myTag.getMemSizeInBytes());
                    mUid = ": " + myTag.getUidString();
                    mProductCode = ": 0x" + Helper.convertByteToHexString(myTag.getICRef());
                    mProductVersion = ": 0x" + Helper.convertByteToHexString(myTag.getProductVersion());
                } catch (STException e) {

                    return -1;
                }
                // special behaviour for the counter as it must be read each time to warranty value
                try {
                    mBitsCounter = ": " + Helper.convertHexByteArrayToString(myTag.getCounterBytes());
                } catch (STException e) {

                    mBitsCounter = ": " + "Tag out of range";
                }
                    return 0;
            }
            return -1;
        }

        @Override
        protected void onPostExecute(Integer result) {

            if (result == 0) {
                if (mView != null) {
                    mLengthView = (TextView) mView.findViewById(R.id.sys_length);
                    mEventCounterView = (TextView) mView.findViewById(R.id.sys_event_counter);
                    mBitsCounterView = (TextView) mView.findViewById(R.id.sys_bits_counter);
                    mMemSizeView = (TextView) mView.findViewById(R.id.sys_memory_size);
                    mUidView = (TextView) mView.findViewById(R.id.uid);
                    mProductCodeView = (TextView) mView.findViewById(R.id.product_code);
                    mProductVersionView = (TextView) mView.findViewById(R.id.product_version);

                    mLengthView.setText(mLength);
                    mEventCounterView.setText(mEventCounter);
                    mBitsCounterView.setText(mBitsCounter);
                    mMemSizeView.setText(mMemSize);
                    mUidView.setText(mUid);
                    mProductCodeView.setText(mProductCode);
                    mProductVersionView.setText(mProductVersion);

                }
            }
        }

    }

    @Override
    public void fillView() {
        new FillViewTask().execute(myTag);
    }
}



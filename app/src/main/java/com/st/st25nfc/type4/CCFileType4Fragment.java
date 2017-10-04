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
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.st.st25nfc.R;
import com.st.st25sdk.STException;
import com.st.st25sdk.type4a.Type4Tag;
import com.st.st25nfc.generic.STFragment;
import com.st.st25nfc.generic.STHeaderFragment;

public class CCFileType4Fragment extends STFragment {

    private Handler mHandler;

    public static CCFileType4Fragment newInstance(Context context) {
        CCFileType4Fragment f = new CCFileType4Fragment();
        /* If needed, pass some argument to the fragment
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        */

        // Set the title of this fragment
        f.setTitle(context.getResources().getString(R.string.cc_file));

        return f;
    }

    public CCFileType4Fragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cc_file_type4, container, false);
        mView = view;

        initView();

        return (View) view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mHandler = new Handler();

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
        setFragmentContentView();
    }

    private void setFragmentContentView() {

        class CCFileType4ContentView implements Runnable {
            TextView mLengthView;
            TextView mMappingVersionView;
            TextView mMaxReadSizeView;
            TextView mMawWriteSizeView;


            String mLength;
            String mMappingVersion;
            String mMaxReadSize;
            String mMaxWriteSize;


            public CCFileType4ContentView() {

            }

            @Override
            public void run() {
                if (myTag != null) {

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    STTlvControlFragment tlvControlFragment = new STTlvControlFragment();
                    STHeaderFragment headerFragment = new STHeaderFragment();
                    fragmentTransaction.replace(R.id.cc_header_fragment_container, headerFragment);
                    fragmentTransaction.replace(R.id.tlv_fragment_container, tlvControlFragment);
                    fragmentTransaction.commit();

                    try {
                        mLength = String.format(": %d bytes", myTag.getCCFileLength());
                        mMappingVersion = String.format(": 0x%x", myTag.getCCMappingVersion());
                        mMaxReadSize = String.format(": %d bytes", ((Type4Tag) myTag).getCCMaxReadSize());
                        mMaxWriteSize = String.format(": %d bytes", ((Type4Tag) myTag).getCCMaxWriteSize());
                    } catch (STException e) {
                        mLength = "Error";
                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mView != null) {
                                mLengthView = (TextView) mView.findViewById(R.id.cc_length);
                                mMappingVersionView = (TextView) mView.findViewById(R.id.cc_mapping_version);
                                mMaxReadSizeView = (TextView) mView.findViewById(R.id.cc_max_read_size);
                                mMawWriteSizeView = (TextView) mView.findViewById(R.id.cc_max_write_size);

                                mLengthView.setText(mLength);
                                mMappingVersionView.setText(mMappingVersion);
                                mMaxReadSizeView.setText(mMaxReadSize);
                                mMawWriteSizeView.setText(mMaxWriteSize);

                            }
                        }
                    });
                }
            }
        }
        new Thread(new CCFileType4ContentView()).start();
    }

}

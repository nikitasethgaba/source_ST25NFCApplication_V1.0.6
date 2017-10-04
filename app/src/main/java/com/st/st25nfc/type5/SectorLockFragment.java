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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.st.st25sdk.NFCTag;
import com.st.st25nfc.R;
import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.SectorInterface;
import com.st.st25nfc.generic.STFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SectorLockFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SectorLockFragment extends STFragment {

    private Handler mHandler;


    TextView mSecurityStatusView;
    byte mValue;
    private int mSectorNbr;


    public SectorLockFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment AreasEditorFragment.
     */
    public static SectorLockFragment newInstance() {
        SectorLockFragment fragment = new SectorLockFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_sector_sec_status, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            mSectorNbr = intent.getIntExtra("sector_nbr", 0);
        } else {
            mSectorNbr = 0;
        }


        Button updateTagButton = (Button) mView.findViewById(R.id.updateTagButton);
        updateTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCurrentSecurityStatus();
            }
        });
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHandler = new Handler();
    }

    @Override
    public void onResume() {
        super.onResume();// Refresh content
        fillView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    protected class FillViewTask extends STFragment.FillViewTask {

        public FillViewTask() {

        }

        @Override
        protected Integer doInBackground(NFCTag... param) {
            if (myTag != null) {
                try {
                    mValue = ((SectorInterface) myTag).getSecurityStatus(mSectorNbr);
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
                    TextView inputTextView = (TextView) mView.findViewById(R.id.security_sector_input);
                    mSecurityStatusView = (TextView) mView.findViewById(R.id.security_sector_value);

                    inputTextView.setText(getResources().getString(R.string.value_of_the_current_sector) +
                            String.format(" %d", mSectorNbr));
                    mSecurityStatusView.setText(String.valueOf(mValue));

                }
            }
        }

    }

    @Override
    public void fillView() {
        new FillViewTask().execute(myTag);
    }

    public void changeCurrentSecurityStatus() {
        int value = Integer.parseInt(mSecurityStatusView.getText().toString());


        // 31 because acccording data sheet b7=b6=b5=0, hence max value is 31 0b11111
        if (value > 31 || value < 0) {
            STLog.e("Input error");
            Toast.makeText(getActivity(), "Fail to update value out of bounds", Toast.LENGTH_LONG).show();
        }

        mValue = (byte) (value & 0xFF);

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    ((SectorInterface) myTag).setSecurityStatus(mSectorNbr, mValue);
                } catch (STException e) {
                 //

                }
//                Toast.makeText(getActivity(), "Update done", Toast.LENGTH_LONG).show();
            }
        }).start();

    }
}

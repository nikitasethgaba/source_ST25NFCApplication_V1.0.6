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

package com.st.st25nfc.type4.stm24sr;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.STFragment;
import com.st.st25nfc.generic.STHeaderFragment;
import com.st.st25sdk.Helper;
import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.type4a.m24srtahighdensity.M24SRTag;

public class SysFileM24SRFragment extends STFragment {

    public M24SRTag myTag = null;

    public static SysFileM24SRFragment newInstance(Context context) {
        SysFileM24SRFragment f = new SysFileM24SRFragment();
        /* If needed, pass some argument to the fragment
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        */

        // Set the title of this fragment
        f.setTitle(context.getResources().getString(R.string.system_file));

        return f;
    }

    public SysFileM24SRFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_sys_file_m24sr, container, false);
        mView = view;


        if (myTag == null)
            myTag = (M24SRTag) ((STFragmentListener) getActivity()).getTag();

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
        TextView mI2CProtectedView;
        TextView mI2CWatchdogView;
        TextView mGPOView;
        TextView mRFEnabledView;
        TextView mNDEFFileNumberView;
        TextView mUidView;
        TextView mMemSizeView;
        TextView mProductCodeView;

        String mLength;
        String mI2CProtected;
        String mI2CWatchdog;
        String mGPO;
        String mRFEnabled;
        String mNDEFFileNumber;
        String mUid;
        String mMemSize;
        String mProductCode;


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

                    if (myTag.getI2CProtected() == 0x00)
                        mI2CProtected = ": " + "the I²C host has the SuperUser right\n" +
                                " access without sending the I²C password";
                    else if (myTag.getI2CProtected() == 0x01)
                        mI2CProtected = ": " + "the I²C host has the SuperUser right\n" +
                                " access after sending the I²C password ";
                    else
                        mI2CProtected = ": " + "I²C protect field state unknown";

                    if (myTag.getI2CWatchdog() == 0x00)
                        mI2CWatchdog = ": " + "watchdog is off";
                    else
                        mI2CWatchdog = String.format(": %d ms", ((int) myTag.getI2CWatchdog() * 30));

                    int i2cMask = (int) (0x07 & myTag.getGpo());
                    int rfMask = (int) (0x70 & myTag.getGpo()) >> 4;

                    if ((i2cMask | 0x00) == 0x00 || ((rfMask | 0x00) == 0x00)) {
                        mGPO = ": When no RF or 12C session is open\n the GPO is high impedance\n";
                    }

                    else {
                        mGPO = ": When RF session is opened \n";

                        if ((i2cMask & 0x05) == 0x05)
                            mGPO += " GPO low when reset\n";
                        if ((i2cMask & 0x04) == 0x04)
                            mGPO += " GPO low after receiving an interrupt cmd\n";
                        if ((i2cMask & 0x03) == 0x03)
                            mGPO += " GPO low when command is computed\n";
                        if ((i2cMask & 0x02) == 0x02)
                            mGPO += " GPO low when programming\n";
                        if ((i2cMask & 0x01) == 0x01)
                            mGPO += " GPO low when session active\n";

                        mGPO += "When an I²C session is opened \n";

                        if ((rfMask & 0x06) == 0x06)
                            mGPO += " GPO low after receiving an RF cmd\n";
                        if ((rfMask & 0x05) == 0x05)
                            mGPO += " GPO low when reset\n";
                        if ((rfMask & 0x04) == 0x04)
                            mGPO += " GPO low after receiving an interrupt cmd\n";
                        if ((rfMask & 0x03) == 0x03)
                            mGPO += " GPO low when modifying an NDEF\n";
                        if ((rfMask & 0x02) == 0x02)
                            mGPO += " GPO low when programming\n";
                        if ((rfMask & 0x01) == 0x01)
                            mGPO += " GPO low when session active\n";
                    }


                    if ((myTag.getRfEnabled() & 0x01) == 0x01)
                        mRFEnabled = ": RF interface enabled\n";
                    else
                        mRFEnabled = ": RF interface disabled\n";

                    if ((myTag.getRfEnabled() & 0x08) == 0x08)
                        mRFEnabled += "RF disable pad is at high state\n";
                    else
                        mRFEnabled += "  RF disable pad is at low state\n";

                    if ((myTag.getRfEnabled() & 0x80) == 0x80)
                        mRFEnabled += "  RF field is on\n";
                    else
                        mRFEnabled += "  RF field is off\n";

                    mMemSize = String.format("%d", myTag.getMemSizeInBytes());
                    mUid = ": " + myTag.getUidString();
                    mNDEFFileNumber = String.format(": %d", myTag.getNDEFFileNumber() + 1);
                    mProductCode = ": 0x" + Helper.convertByteToHexString(myTag.getICRef());
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
                    mLengthView = (TextView) mView.findViewById(R.id.sys_length);
                    mI2CProtectedView = (TextView) mView.findViewById(R.id.sys_i2c_protected);
                    mI2CWatchdogView = (TextView) mView.findViewById(R.id.sys_i2c_watchdog);
                    mGPOView = (TextView) mView.findViewById(R.id.sys_gpo);
                    mRFEnabledView = (TextView) mView.findViewById(R.id.sys_rf_enabled);
                    mNDEFFileNumberView = (TextView) mView.findViewById(R.id.sys_ndef_file_number);
                    mMemSizeView = (TextView) mView.findViewById(R.id.sys_memory_size);
                    mUidView = (TextView) mView.findViewById(R.id.uid);
                    mProductCodeView = (TextView) mView.findViewById(R.id.product_code);

                    mLengthView.setText(mLength);
                    mI2CProtectedView.setText(mI2CProtected);
                    mI2CWatchdogView.setText(mI2CWatchdog);
                    mGPOView.setText(mGPO);
                    mRFEnabledView.setText(mRFEnabled);
                    mNDEFFileNumberView.setText(mNDEFFileNumber);
                    mMemSizeView.setText(mMemSize);
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



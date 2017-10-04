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
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.st.st25nfc.R;
import com.st.st25sdk.ndef.NDEFMsg;
import com.st.st25sdk.ndef.WifiRecord;

import java.util.ArrayList;
import java.util.List;

public class NDEFWifiFragment extends NDEFRecordFragment implements AdapterView.OnItemSelectedListener {

    final static String TAG = "NDEFWifiFragment";

    private View mView;

    private ArrayAdapter<String> mSpinnerAdapter;

    private WifiManager mWifiManager = null;
    private List<WifiConfiguration> mAPList = null;
    private WifiRecord mWifiRecord;
    private int mAction;

    private EditText mNdefSsidEditText;
    private Spinner mNdefSsidWifiSpinner;
    private Spinner mSsidSpinner;
    private Spinner mAuthTypeSpinner;
    private Spinner mEncrTypeSpinner;
    private EditText mNetKeyEditText;


    public static NDEFWifiFragment newInstance(Context context) {
        NDEFWifiFragment f = new NDEFWifiFragment();
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

        View view = inflater.inflate(R.layout.fragment_ndef_wifi, container, false);
        mView = view;

        Bundle bundle = getArguments();
        if (bundle == null) {
            Log.e(TAG, "Fatal error! Arguments are missing!");
            return null;
        }

        NDEFMsg ndefMsg = (NDEFMsg) bundle.getSerializable(NDEFRecordFragment.NDEFKey);
        int recordNbr = bundle.getInt(NDEFRecordFragment.RecordNbrKey);
        mWifiRecord = (WifiRecord) ndefMsg.getNDEFRecord(recordNbr);

        initFragmentWidgets();

        mAction = bundle.getInt(NDEFEditorFragment.EditorKey);
        if(mAction == NDEFEditorFragment.VIEW_NDEF_RECORD) {
            // We are displaying an existing record. By default it is not editable
            ndefRecordEditable(false);
        } else {
            // We are adding a new TextRecord or editing an existing record
            ndefRecordEditable(true);
        }

        return mView;
    }


    private void readWepConfig(WifiConfiguration config) {
        Log.d("WifiPreference", "SSID " + config.SSID);
        Log.d("WifiPreference", "PASSWORD " + config.preSharedKey);
        Log.d("WifiPreference", "ALLOWED ALGORITHMS -------------");
        Log.d("WifiPreference", "LEAP " + config.allowedAuthAlgorithms.get(WifiConfiguration.AuthAlgorithm.LEAP));
        Log.d("WifiPreference", "OPEN " + config.allowedAuthAlgorithms.get(WifiConfiguration.AuthAlgorithm.OPEN));
        Log.d("WifiPreference", "SHARED " + config.allowedAuthAlgorithms.get(WifiConfiguration.AuthAlgorithm.SHARED));
        Log.d("WifiPreference", "GROUP CIPHERS--------------------");
        Log.d("WifiPreference", "CCMP " + config.allowedGroupCiphers.get(WifiConfiguration.GroupCipher.CCMP));
        Log.d("WifiPreference", "TKIP " + config.allowedGroupCiphers.get(WifiConfiguration.GroupCipher.TKIP));
        Log.d("WifiPreference", "WEP104 " + config.allowedGroupCiphers.get(WifiConfiguration.GroupCipher.WEP104));
        Log.d("WifiPreference", "WEP40  " + config.allowedGroupCiphers.get(WifiConfiguration.GroupCipher.WEP40));
        Log.d("WifiPreference", "KEYMGMT -------------------------");
        Log.d("WifiPreference", "IEEE8021X " + config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X));
        Log.d("WifiPreference", "NONE " + config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.NONE));
        Log.d("WifiPreference", "WPA_EAP " + config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP));
        Log.d("WifiPreference", "WPA_PSK " + config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK));
        Log.d("WifiPreference", "PairWiseCipher-------------------");
        Log.d("WifiPreference", "CCMP " + config.allowedPairwiseCiphers.get(WifiConfiguration.PairwiseCipher.CCMP));
        Log.d("WifiPreference", "NONE " + config.allowedPairwiseCiphers.get(WifiConfiguration.PairwiseCipher.NONE));
        Log.d("WifiPreference", "TKIP " + config.allowedPairwiseCiphers.get(WifiConfiguration.PairwiseCipher.TKIP));
        Log.d("WifiPreference", "Protocols-------------------------");
        Log.d("WifiPreference", "RSN " + config.allowedProtocols.get(WifiConfiguration.Protocol.RSN));
        Log.d("WifiPreference", "WPA " + config.allowedProtocols.get(WifiConfiguration.Protocol.WPA));
        Log.d("WifiPreference", "WEP Key Strings--------------------");
        String[] wepKeys = config.wepKeys;
        Log.d("WifiPreference", "WEP KEY 0 " + wepKeys[0]);
        Log.d("WifiPreference", "WEP KEY 1 " + wepKeys[1]);
        Log.d("WifiPreference", "WEP KEY 2 " + wepKeys[2]);
        Log.d("WifiPreference", "WEP KEY 3 " + wepKeys[3]);
    }

    private void initFragmentWidgets() {
        mNdefSsidEditText = (EditText) mView.findViewById(R.id.ndefSsidEditor);
        mNdefSsidWifiSpinner = (Spinner) mView.findViewById(R.id.ndefSSidWifiSpinner);
        mSsidSpinner = (Spinner) mView.findViewById(R.id.ndefSSidWifiSpinner);
        mAuthTypeSpinner = (Spinner) mView.findViewById(R.id.authTypeList);
        mEncrTypeSpinner = (Spinner) mView.findViewById(R.id.encrTypeList);
        mNetKeyEditText = (EditText) mView.findViewById(R.id.netKeyTxt);

        Context context;
        context = getActivity().getApplicationContext();

        mSpinnerAdapter = new ArrayAdapter<String>(context, R.layout.spinner_text_view);
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (!mWifiManager.isWifiEnabled()) {
            Toast toast = Toast.makeText(context, "Wifi is not enabled", Toast.LENGTH_LONG);
            toast.show();

        } else {
            mAPList = mWifiManager.getConfiguredNetworks();

            // - build the applicable list
            List<String> ssidSpinnerList = new ArrayList<String>();
            for (int i = 0; i < mAPList.size(); i++) {

                ssidSpinnerList.add(mAPList.get(i).SSID.replaceAll("\"", ""));
                readWepConfig(mAPList.get(i));
            }

            mSpinnerAdapter.setDropDownViewResource(R.layout.spinner_text_view);
            mSpinnerAdapter.addAll(ssidSpinnerList);
            mNdefSsidWifiSpinner.setAdapter(mSpinnerAdapter);
        }

        setContent();
    }

    /**
     * The content from the NDEF Record is displayed in the Fragment
     */
    public void setContent() {

        String ssid = mWifiRecord.getSSID();

        Integer s = 0;
        s = Integer.valueOf(mSpinnerAdapter.getPosition(ssid));
        if (s < 0) {
            mSpinnerAdapter.add(ssid);
            mNdefSsidWifiSpinner.setAdapter(mSpinnerAdapter);
            s = 0;
        }

        mNdefSsidWifiSpinner.setSelection(s);

        if (mWifiRecord.getAuthType() < 2)
            mAuthTypeSpinner.setSelection(mWifiRecord.getAuthType());
        else
            mAuthTypeSpinner.setSelection(0);

        if (mWifiRecord.getEncrType() < 3)
            mEncrTypeSpinner.setSelection(mWifiRecord.getEncrType());
        else
            mEncrTypeSpinner.setSelection(0);

        if (mWifiRecord.getEncrKey() != null)
            mNetKeyEditText.setText(mWifiRecord.getEncrKey());
        else
            mNetKeyEditText.setText("");
    }

    /**
     * The content from the fragment is saved into the NDEF Record
     */
    @Override
    public void updateContent() {
        String ssid;
        if (mNdefSsidWifiSpinner.getSelectedItem() != null) {
            ssid = mNdefSsidWifiSpinner.getSelectedItem().toString();
        } else {
            ssid = "NotDefined";
        }

        int authType = mAuthTypeSpinner.getSelectedItemPosition();
        int encrType = mEncrTypeSpinner.getSelectedItemPosition();

        String encrKey;
        if (mNetKeyEditText.getText() != null) {
            encrKey = mNetKeyEditText.getText().toString();
        } else {
            encrKey = "NotDefined";
        }

        mWifiRecord.setSSID(ssid);
        mWifiRecord.setAuthType(authType);
        mWifiRecord.setEncrType(encrType);
        mWifiRecord.setEncrKey(encrKey);
    }

    public void populateWifiExportedField(WifiConfiguration config) {

        if ((config.preSharedKey == null) &&
                (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.NONE) == true) &&
                (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK) == false)) {
            // we have an open network
            mAuthTypeSpinner.setSelection(0);
            mEncrTypeSpinner.setSelection(0);
        } else if ((config.preSharedKey != null) &&
                (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.NONE) == false) &&
                (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK) == true)
                ) {
            // we have a WPA/WPA2 PSK
            mAuthTypeSpinner.setSelection(1);
            mEncrTypeSpinner.setSelection(2);
        } else {
            // we have a WPA/WPA2 PSK
            mAuthTypeSpinner.setSelection(0);
            mEncrTypeSpinner.setSelection(0);
        }
    }


    public void ndefRecordEditable(boolean editable) {
        mNdefSsidEditText.setEnabled(false);
        mNdefSsidEditText.setText("");
        mNdefSsidEditText.setVisibility(View.INVISIBLE);

        mSsidSpinner.setFocusable(editable);
        mSsidSpinner.setEnabled(editable);
        mSsidSpinner.setClickable(editable);

        mAuthTypeSpinner.setFocusable(editable);
        mAuthTypeSpinner.setEnabled(editable);
        mAuthTypeSpinner.setClickable(editable);

        mEncrTypeSpinner.setFocusable(editable);
        mEncrTypeSpinner.setEnabled(editable);
        mEncrTypeSpinner.setClickable(editable);

        // netKeyTxt.setFocusable(editable);
        // netKeyTxt.setEnabled(editable);
        // netKeyTxt.setClickable(editable);

        if (editable) {
            mSsidSpinner.setOnItemSelectedListener(this);
            int ssid;
            if (mSsidSpinner.getSelectedItem() != null) {
                ssid = mSsidSpinner.getSelectedItemPosition();
            } else {
                ssid = -1;
            }
            if (ssid >= 0 && mAPList != null && ssid < mAPList.size())
                populateWifiExportedField(mAPList.get(ssid));
        } else {
            // The Fragment is no more editable. Reload its content
            setContent();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // readWepConfig(_maccesspointlist.get(pos));
        if (mAPList != null)
            populateWifiExportedField(mAPList.get(position));

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}



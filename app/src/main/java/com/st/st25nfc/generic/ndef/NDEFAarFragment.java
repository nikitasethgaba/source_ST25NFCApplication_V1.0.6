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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.st.st25nfc.R;
import com.st.st25sdk.ndef.AarRecord;
import com.st.st25sdk.ndef.NDEFMsg;

import java.util.ArrayList;
import java.util.List;

public class NDEFAarFragment extends NDEFRecordFragment implements AdapterView.OnItemClickListener {

    final static String TAG = "NDEFAarFragment";

    private View mView;

    private ListView mAarListView;
    private EditText mAarEditText;
    private Button mAppListButton;

    private List<ResolveInfo> mAarlist;
    private AarRecord mAarRecord;
    private int mAction;
    private boolean mIsNdefRecordEditable;




    public static NDEFAarFragment newInstance(Context context) {
        NDEFAarFragment f = new NDEFAarFragment();
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


        View view = inflater.inflate(R.layout.fragment_ndef_aar, container, false);
        mView = view;

        Bundle bundle = getArguments();
        if (bundle == null) {
            Log.e(TAG, "Fatal error! Arguments are missing!");
            return null;
        }

        NDEFMsg ndefMsg = (NDEFMsg) bundle.getSerializable(NDEFRecordFragment.NDEFKey);
        int recordNbr = bundle.getInt(NDEFRecordFragment.RecordNbrKey);
        mAarRecord = (AarRecord) ndefMsg.getNDEFRecord(recordNbr);

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


    private void initFragmentWidgets() {
        mAarEditText = (EditText) mView.findViewById(R.id.ndef_fragment_aar_selection);
        mAarListView = (ListView) mView.findViewById(R.id.listViewAar);
        mAppListButton = (Button) mView.findViewById(R.id.appListButton);

        PackageManager pm = getContext().getPackageManager();

        mAarListView.setOnItemClickListener(this);

        mAppListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAarListView.setVisibility(View.VISIBLE);
            }
        });

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        mAarlist = pm.queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED);
        ArrayList results = new ArrayList();

        for (ResolveInfo rInfo : mAarlist)
        {
            results.add(rInfo.activityInfo.applicationInfo.packageName);
            //results.add(rInfo.activityInfo.applicationInfo.loadLabel(pm).toString());
            Log.w("Installed Applications", rInfo.activityInfo.applicationInfo.loadLabel(pm).toString());
        }
        ArrayAdapter arrAdapter = new ArrayAdapter(getContext(), R.layout.list_item, results);

        mAarListView.setAdapter(arrAdapter);

        setContent();
    }

    /**
     * The content from the NDEF Record is displayed in the Fragment
     */
    public void setContent() {
        String aar = mAarRecord.getAar();
        if (aar != null) {
            mAarEditText.setText(aar);
        } else {
            mAarEditText.setText("");
        }
    }

    /**
     * The content from the fragment is saved into the NDEF Record
     */
    @Override
    public void updateContent() {
        String aar;
        if (mAarEditText.getText() != null) {
            aar = mAarEditText.getText().toString();
        } else {
            aar = "";
        }
        mAarRecord.setAar(aar);
    }

    public void ndefRecordEditable(boolean editable) {
        mIsNdefRecordEditable = editable;

        mAarEditText.setEnabled(editable);
        mAarEditText.setClickable(editable);
        mAarEditText.setFocusable(editable);

        mAppListButton.setEnabled(editable);
        mAppListButton.setClickable(editable);
        mAppListButton.setFocusable(editable);

        if(editable) {
            mAppListButton.setVisibility(View.VISIBLE);
        } else {
            mAppListButton.setVisibility(View.GONE);

            // The Fragment is no more editable. Reload its content
            setContent();
        }

        mAarListView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        getAarSelected(parent, view, position, id);
    }

    public void  getAarSelected(AdapterView parentView, View childView, int position, long id) {
        mAarEditText.setText(mAarlist.get(position).activityInfo.applicationInfo.packageName);
        mAarListView.setVisibility(View.INVISIBLE);
    }

}



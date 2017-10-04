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

package com.st.st25nfc.generic;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.st.st25sdk.STException;
import com.st.st25sdk.NFCTag;
import com.st.st25nfc.R;

public class TagInfoFragment extends STFragment {


    public static TagInfoFragment newInstance(Context context) {
        TagInfoFragment f = new TagInfoFragment();
        /* If needed, pass some argument to the fragment
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        */

        // Set the title of this fragment
        f.setTitle(context.getResources().getString(R.string.tag_info));

        return f;
    }

    public TagInfoFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_tag_info, container, false);
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

        TextView mUidView;
        TextView mManufacturerNameView;
        TextView mTagNameView;
        TextView mTagDescriptionView;
        TextView mTagTypeView;
        TextView mTagSizeView;
        TextView mTechListView;

        String mManufacturerName;
        String mUid;
        String mTagName;
        String mTagDescription;
        String mTagType;
        String mTagSize;
        String mTechList;



        public FillViewTask() {
        }

        @Override
        protected Integer doInBackground(NFCTag... param) {

            if (myTag != null) {
                try {
                    mTagName = myTag.getName();
                    mTagDescription = myTag.getDescription();
                    mTagType = myTag.getTypeDescription();
                    mManufacturerName = ": " + myTag.getManufacturerName();
                    mUid = ": " + myTag.getUidString();
                    mTagSize = ": " + String.valueOf(myTag.getMemSizeInBytes()) + " bytes";
                    mTechList = ": " + TextUtils.join("\n ", myTag.getTechList());
                } catch (STException e) {
                    return -1;

                }
            }

            return 0;
        }


        @Override
        protected void onPostExecute(Integer result) {

            if (result == 0) {
                if (mView != null) {
                    mTagNameView = (TextView) mView.findViewById(R.id.model_header);
                    mTagTypeView = (TextView) mView.findViewById(R.id.model_type);
                    mTagDescriptionView = (TextView) mView.findViewById(R.id.model_description);
                    mManufacturerNameView = (TextView) mView.findViewById(R.id.manufacturer_name);
                    mUidView = (TextView) mView.findViewById(R.id.uid);
                    mTagSizeView = (TextView) mView.findViewById(R.id.memory_size);
                    mTechListView = (TextView) mView.findViewById(R.id.tech_list);
                }

                if (mManufacturerNameView != null && mUidView != null) {
                    mManufacturerNameView.setText(mManufacturerName);
                    mUidView.setText(mUid);
                    mTagNameView.setText(mTagName);
                    mTagTypeView.setText(mTagDescription);
                    mTagDescriptionView.setText(mTagType);
                    mTagSizeView.setText(mTagSize);
                    mTechListView.setText(mTechList);
                }
            }
            return;

        }
    }

    @Override
    public void fillView() {
        new FillViewTask().execute(myTag);
    }
}

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
import android.os.Handler;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.st.st25nfc.R;


/**
 * A dummy fragment representing a section of the app, but that simply displays dummy text.
 */
public class STHeaderFragment extends STFragment {


    private Handler mHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.tag_header_info, container, false);
        Bundle args = getArguments();

        initView();

        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHandler = new Handler();

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
        fillView();
    }

    protected class HeaderFillView implements Runnable {
        private TextView mTagNameView;
        private TextView mTagDescriptionView;
        private TextView mTagTypeView;

        private String mTagName;
        private String mTagDescription;
        private String mTagType;

        public HeaderFillView() {

        }


        public void  run() {
            if (myTag != null) {
                mTagName = myTag.getName();
                mTagDescription = myTag.getDescription();
                mTagType = myTag.getTypeDescription();
            }

            if (mHandler != null)
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mView != null) {
                            mTagNameView = (TextView) mView.findViewById(R.id.model_header);
                            mTagTypeView = (TextView) mView.findViewById(R.id.model_type);
                            mTagDescriptionView = (TextView) mView.findViewById(R.id.model_description);

                            mTagNameView.setText(mTagName);
                            mTagDescriptionView.setText(mTagDescription);
                            mTagTypeView.setText(mTagType);
                        }
                    }
                });


        }


    }

    @Override
    public void fillView() {
        //new FillViewTask().execute(myTag);
        new Thread(new HeaderFillView()).start();
    }


}





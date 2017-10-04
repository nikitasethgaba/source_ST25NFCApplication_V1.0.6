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


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.st.st25sdk.NFCTag;
import com.st.st25nfc.R;


/**
 * A dummy fragment representing a section of the app, but that simply displays dummy text.
 */
public class STFragment extends Fragment {

    STFragmentListener mFragmentListener;


    public NFCTag myTag = null;
    protected View mView;

    protected boolean mPaused = false;

    private String title = "";

    public interface STFragmentListener {
        public NFCTag getTag();
    }

    protected void setTag() {
        if (myTag == null)
            myTag = ((STFragment.STFragmentListener) getActivity()).getTag();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_collection_object, container, false);
        Bundle args = getArguments();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mFragmentListener = (STFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement STFragmentListener");
        }
        setTag();
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getActivity().getIntent();

        if (STFragmentActivity.tagChanged(getActivity(), myTag))
            return;

        if (mPaused) {
            fillView();
            mPaused = false;
        }
    }

    @Override
    public void onPause() {super.onPause();mPaused = true;}


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    protected abstract class FillViewTask extends AsyncTask<NFCTag, Void, Integer> {

        @Override
        protected Integer doInBackground(NFCTag... params) {
            return null;
        }
    }

    public  void fillView() {
        return;
    }

    protected void initView() {
        setTag();
        fillView();
    }

    /**
     * Helper function to display a Toast from non UI thread
     *
     * @param message
     */
    protected void showToast(final String message) {

        // This function can be called from a background thread so it may happen after the detach
        // from parent activity. In such case, getActivity() may be null.
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * Helper function to display a Toast from non UI thread
     *
     * @param resource_id
     */
    protected void showToast(final int resource_id) {

        // This function can be called from a background thread so it may happen after the detach
        // from parent activity. In such case, getActivity() and getResources() may be null.
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Resources resources = getResources();
                    if(resources != null) {
                        Toast.makeText(activity, resources.getString(resource_id), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}





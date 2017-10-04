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
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.st.st25sdk.MultiAreaInterface;
import com.st.st25nfc.R;
import com.st.st25sdk.STException;
import com.st.st25nfc.generic.STFragment;
import com.st.st25nfc.generic.STHeaderFragment;
import com.st.st25sdk.type4a.Type4Tag;

import java.util.ArrayList;
import java.util.List;

import static com.st.st25nfc.type4.AreasPwdFragment.ActionStatus.ACTION_SUCCESSFUL;
import static com.st.st25nfc.type4.AreasPwdFragment.ActionStatus.ACTION_FAILED;
import static com.st.st25nfc.type4.AreasPwdFragment.ActionStatus.TAG_NOT_IN_THE_FIELD;
import static com.st.st25sdk.MultiAreaInterface.AREA1;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AreasPwdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AreasPwdFragment extends STFragment implements AdapterView.OnItemClickListener {
    final static String TAG = "AreasPwdFragment";

    enum ActionStatus {
        ACTION_SUCCESSFUL,
        ACTION_FAILED,
        TAG_NOT_IN_THE_FIELD
    };

    class AreaAccessStatus {
        Type4Tag.AccessStatus readAccessStatus;
        Type4Tag.AccessStatus writeAccessStatus;
    }

    private ListView mListView;
    private Handler mHandler;
    private BaseAdapter mAdapter;

    private List<AreaAccessStatus> mAreaAccessStatus;


    public AreasPwdFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment AreasEditorFragment.
     */
    public static AreasPwdFragment newInstance() {
        AreasPwdFragment fragment = new AreasPwdFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.activity_stm24ta_areas_pwd, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mHandler = new Handler();

        mAreaAccessStatus = new ArrayList<AreaAccessStatus>();

        mListView = (ListView) mView.findViewById(R.id.list_view);
        mAdapter = new AreasListAdapter();
        mListView.setOnItemClickListener(this);
        //mListView.setOnTouchListener(this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh content
        retrieveAreaAccessStatus();
    }

    private void retrieveAreaAccessStatus() {
        new asyncTaskReadAreasAccessStatus().execute();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int area = position + 1;
        Intent intent;

        intent = new Intent(getActivity(), AreasLockActivity.class);
        intent.putExtra("area_num", area);
        startActivityForResult(intent, 1);
    }


    class AreasListAdapter extends BaseAdapter  {



        public AreasListAdapter() {
            //mListView.setOnItemClickListener(this);
        }

        //get read_list_items count
        @Override
        public int getCount() {
            return mAreaAccessStatus.size();
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

            int area = pos + 1;
            View listItem = convertView;


            if (listItem == null) {
                //set the main ListView's layout
                listItem = getActivity().getLayoutInflater().inflate(R.layout.sector_items, parent, false);

            }

            TextView title = (TextView) listItem.findViewById(R.id.title);
            TextView description = (TextView) listItem.findViewById(R.id.description);

            Drawable contentImage = null;


            String titleString =  getResources().getString(R.string.Area) + area;

            String descriptionString = "Read/Write status unknown" ;

            AreaAccessStatus areaAccessStatus = mAreaAccessStatus.get(pos);

            switch(areaAccessStatus.readAccessStatus) {
                case NOT_LOCKED:
                    descriptionString = "Area read: Not locked\n";
                    break;
                case LOCKED_BY_PASSWORD:
                    descriptionString = "Area read: Locked by password\n";
                    break;
                case NOT_AUTHORIZED:
                    descriptionString = "Area read: Not authorized\n";
                    break;
                case STATUS_UNKNOWN:
                    descriptionString = "Area read: Lock status unknown\n";
                    break;
            }

            switch(areaAccessStatus.writeAccessStatus) {
                case NOT_LOCKED:
                    descriptionString += "Area write: Not locked\n";
                    break;
                case LOCKED_BY_PASSWORD:
                    descriptionString += "Area write: Locked by password\n";
                    break;
                case NOT_AUTHORIZED:
                    descriptionString += "Area write: Not authorized\n";
                    break;
                case STATUS_UNKNOWN:
                    descriptionString += "Area write: Lock status unknown\n";
                    break;
            }

            title.setText(titleString + String.format(" Lock status: "));

            description.setText(descriptionString);

            if (pos % 2 == 0)
                contentImage = getResources().getDrawable(R.drawable.ic_label_st_light_blue_24dp);
            else
                contentImage = getResources().getDrawable(R.drawable.ic_label_st_dark_blue_24dp);


            ImageView image = (ImageView) listItem.findViewById(R.id.thumb);


            Resources.Theme appTheme = getResources().newTheme();
            appTheme.applyStyle(R.style.STAppTheme, false);

            image.setImageDrawable(contentImage);

            return listItem;
        }

    }

    private void setHeaderContent() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        STHeaderFragment fragment = new STHeaderFragment();
        fragmentTransaction.replace(R.id.areas_pwd_header_fragment_container, fragment);
        fragmentTransaction.commit();

    }

    /**
     * AsyncTask retrieving the access status of each area
     */
    private class asyncTaskReadAreasAccessStatus extends AsyncTask<Void, Void, ActionStatus> {

        // List local to the AsyncTask where we will store the data retrieved by the background thread
        List<AreaAccessStatus> mAsyncTaskAreaAccessStatus = new ArrayList<AreaAccessStatus>();

        public asyncTaskReadAreasAccessStatus() {

        }

        @Override
        protected ActionStatus doInBackground(Void... param) {
            ActionStatus result;
            int numberOfAreas;

            try {

                if (myTag instanceof MultiAreaInterface)
                    numberOfAreas = ((MultiAreaInterface) myTag).getNumberOfAreas();
                else
                    numberOfAreas = 1;

                mAsyncTaskAreaAccessStatus = new ArrayList<AreaAccessStatus>();

                for (int area = AREA1; area <= numberOfAreas; area++) {
                    int fileId = ((Type4Tag) myTag).getCCTlv(area-1).getFileId();

                    AreaAccessStatus areaAccessStatus = new AreaAccessStatus();

                    areaAccessStatus.readAccessStatus = ((Type4Tag) myTag).getFileReadAccessStatus(fileId);
                    areaAccessStatus.writeAccessStatus = ((Type4Tag) myTag).getFileWriteAccessStatus(fileId);

                    mAsyncTaskAreaAccessStatus.add(areaAccessStatus);
                }

            } catch (STException e) {
                switch (e.getError()) {
                    case TAG_NOT_IN_THE_FIELD:
                        return TAG_NOT_IN_THE_FIELD;

                    default:
                        e.printStackTrace();
                        return ACTION_FAILED;
                }
            }

            result = ACTION_SUCCESSFUL;

            return result;
        }

        @Override
        protected void onPostExecute(ActionStatus actionStatus) {

            switch(actionStatus) {
                case ACTION_SUCCESSFUL:
                    // Clear mAreaAccessStatus and copy all the data from mAsyncTaskAreaAccessStatus to it
                    mAreaAccessStatus.clear();

                    for (AreaAccessStatus areaAccessStatus : mAsyncTaskAreaAccessStatus) {
                        mAreaAccessStatus.add(areaAccessStatus);
                    }

                    Log.v(TAG, "notifyDataSetChanged");
                    mAdapter.notifyDataSetChanged();
                    break;

                case ACTION_FAILED:
                    showToast(R.string.error_while_reading_the_tag);
                    break;

                case TAG_NOT_IN_THE_FIELD:
                    showToast(R.string.tag_not_in_the_field);
                    break;
            }

            return;
        }
    }

}

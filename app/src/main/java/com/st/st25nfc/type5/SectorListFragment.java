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
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
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

import com.st.st25nfc.R;
import com.st.st25sdk.STException;
import com.st.st25sdk.SectorInterface;
import com.st.st25nfc.generic.STFragment;
import com.st.st25nfc.generic.STHeaderFragment;
import com.st.st25nfc.type5.stm24lr.STM24LRSectorLockActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SectorListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SectorListFragment extends STFragment implements AdapterView.OnItemClickListener {
    final static String TAG = "SectorListFragment";

    private ListView mListView;
    private Handler mHandler;
    private BaseAdapter mAdapter;

    private byte[] mSecurityStatus = null;


    public SectorListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment AreasEditorFragment.
     */
    public static SectorListFragment newInstance() {
        SectorListFragment fragment = new SectorListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_sector_edition, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mHandler = new Handler();

        mListView = (ListView) mView.findViewById(R.id.list_view);
        mAdapter = new SectorListAdapter();
        mListView.setOnItemClickListener(this);
        //mListView.setOnTouchListener(this);
        mListView.setAdapter(mAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh content
        setFragmentContentView();
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
        Intent intent;

        intent = new Intent(getActivity(), STM24LRSectorLockActivity.class);
        intent.putExtra("sector_nbr", position);
        startActivityForResult(intent, 1);
    }



    class SectorListAdapter extends BaseAdapter  {


        public SectorListAdapter() {
            //mListView.setOnItemClickListener(this);
        }

        //get read_list_items count
        @Override
        public int getCount() {
            return ((SectorInterface) myTag).getNumberOfSectors();
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


            View listItem = convertView;


            if (listItem == null) {
                //set the main ListView's layout
                listItem = getActivity().getLayoutInflater().inflate(R.layout.sector_items, parent, false);

            }

            TextView title = (TextView) listItem.findViewById(R.id.title);
            TextView description = (TextView) listItem.findViewById(R.id.description);

            Drawable contentImage = null;


            String titleString = getResources().getString(R.string.Sector) + pos;

            if (mSecurityStatus == null)
                mSecurityStatus = new byte[((SectorInterface) myTag).getNumberOfSectors()];

            /*if (mSecurityStatus == null)
                mSecurityStatus = new byte[((SectorInterface) myTag).getNumberOfBlocksPerSector()*((SectorInterface) myTag).getNumberOfSectors()];*/

            if (pos  < mSecurityStatus.length)
                title.setText(titleString + String.format("\nSector Locked: %s ", mSecurityStatus[pos]));

            //description.setText(R.string.no_ndef_data_or_unknown_data);

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
        fragmentTransaction.replace(R.id.sector_header_fragment_container, fragment);
        fragmentTransaction.commit();

    }

    private void setFragmentContentView() {

        class SectorContentView implements Runnable {

            public void run() {
                setHeaderContent();

                try {
                    //Actually only one ndef per Area...
                    byte[] buf;
                    buf = ((SectorInterface) myTag).getSecurityStatus();
                    mSecurityStatus = buf.clone();
                    // Start from an empty list
                } catch (STException e) {
                    e.printStackTrace();
                }

                if (mHandler != null)
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.v(TAG, "notifyDataSetChanged");
                            mAdapter.notifyDataSetChanged();
                        }
                    });

                ;

            }
        }

        new Thread(new SectorContentView()).start();
    }

}

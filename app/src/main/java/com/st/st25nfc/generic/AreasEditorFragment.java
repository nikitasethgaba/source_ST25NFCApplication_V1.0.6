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
import android.content.Intent;
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

import com.st.st25nfc.R;
import com.st.st25nfc.generic.ndef.NDEFEditorActivity;
import com.st.st25nfc.generic.util.UIHelper;
import com.st.st25sdk.MultiAreaInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.ndef.NDEFMsg;

import java.util.ArrayList;
import java.util.List;

import static com.st.st25nfc.generic.AreasEditorFragment.ActionStatus.ACTION_FAILED;
import static com.st.st25nfc.generic.AreasEditorFragment.ActionStatus.ACTION_SUCCESSFUL;
import static com.st.st25nfc.generic.AreasEditorFragment.ActionStatus.TAG_NOT_IN_THE_FIELD;
import static com.st.st25nfc.generic.AreasEditorFragment.AreaContentStatus.AREA_READ_PROTECTED;
import static com.st.st25nfc.generic.AreasEditorFragment.AreaContentStatus.ERROR_WHILE_READING_THE_AREA;
import static com.st.st25nfc.generic.AreasEditorFragment.AreaContentStatus.NDEF_DATA_AVAILABLE;
import static com.st.st25nfc.generic.AreasEditorFragment.AreaContentStatus.NO_NDEF_OR_UNKNOWN_DATA;
import static com.st.st25sdk.MultiAreaInterface.AREA1;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AreasEditorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AreasEditorFragment extends STFragment implements AdapterView.OnItemClickListener {
    final static String TAG = "AreasEditorFragment";

    /*@Override
    public void onPwdDialogFinish(int result, byte[] password) {
        //if (result == PwdDialogFragment.RESULT_OK && password != null) {
            try {
                ((STType4Tag) myTag).verifyReadPassword(mAreaSelected + 1, password);
            }
            catch (STException e) {
                onSTType5PwdDialogFinish(PwdDialogFragment.RESULT_FAIL);
            }
        }
        onSTType5PwdDialogFinish(result);
    }*/

    enum ActionStatus {
        ACTION_SUCCESSFUL,
        ACTION_FAILED,
        TAG_NOT_IN_THE_FIELD,
        AREA_PASSWORD_NEEDED,
        NO_PASSWORD_SELECTED_FOR_THIS_AREA
    };

    enum AreaContentStatus {
        NO_NDEF_OR_UNKNOWN_DATA,
        ERROR_WHILE_READING_THE_AREA,
        NDEF_DATA_AVAILABLE,
        AREA_READ_PROTECTED
    };


    // Local class used to store a status about area content + possibly the NDEF message contained in this area
    class AreaContent {
        AreaContentStatus areaContentStatus;
        int sizeInBytes;
        NDEFMsg ndefMsg;
    }

    protected List<AreaContent> mAreaContent;
    private int mAreaSelected;
    private ListView mListView;
    private Handler mHandler;
    private BaseAdapter mAdapter;
    private byte[] mReadPassword;


    public AreasEditorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment AreasEditorFragment.
     */
    public static AreasEditorFragment newInstance() {
        AreasEditorFragment fragment = new AreasEditorFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_area_edition, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mHandler = new Handler();
        mAreaContent = new ArrayList<AreaContent>();

        mListView = (ListView) mView.findViewById(R.id.list_view);
        mAdapter = new AreaListAdapter();
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh content
        refreshList();
    }

    private void refreshList() {
        new asyncTaskReadAreaContent().execute();
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
        mAreaSelected = position + 1;
        AreaContent areaContent = mAreaContent.get(position);

        /*if(areaContent.areaContentStatus == AREA_READ_PROTECTED) {
            if(UIHelper.isAType5Tag(myTag)) {
                new asyncTaskDisplayPasswordDialogBoxForType5Tag().execute();

            } else if(UIHelper.isAType4Tag(myTag)) {
                // TODO
                new asyncTaskForType4Password(this).execute();

                //showToast("Generic management of password not implemented yet for Type4");

            } else {
                // Tag type not supported yet
            }
        } else {*/
            Intent intent = new Intent(getActivity(), NDEFEditorActivity.class);
            intent.putExtra("area_nbr", mAreaSelected);
            startActivityForResult(intent, 1);
        //}
    }

   /* @Override
    public void onSTType5PwdDialogFinish(int result) {
        Log.v(TAG, "onSTType5PwdDialogFinish. result = " + result);

        if (result == PwdDialogFragment.RESULT_OK) {
            // Password has been entered successfully so we can now enter NDEFEditorActivity
            Intent intent = new Intent(getActivity(), NDEFEditorActivity.class);
            intent.putExtra("area_nbr", mAreaSelected);
            startActivityForResult(intent, 1);

        } else {
            showToast(R.string.failed_to_unlock);
        }
    }*/


    class AreaListAdapter extends BaseAdapter  {


        public AreaListAdapter() {
            //mListView.setOnItemClickListener(this);
        }

        //get read_list_items count
        @Override
        public int getCount() {
            return mAreaContent.size();
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
                listItem = getActivity().getLayoutInflater().inflate(R.layout.area_items, parent, false);
            }

            TextView title = (TextView) listItem.findViewById(R.id.title);
            TextView descriptionTextView = (TextView) listItem.findViewById(R.id.description);

            Drawable contentImage = null;

            int areaSizeInBytes = 0;

            if (mAreaContent != null && mAreaContent.size() > pos) {
                AreaContent areaContent = mAreaContent.get(pos);

                areaSizeInBytes = mAreaContent.get(pos).sizeInBytes;

                switch(areaContent.areaContentStatus) {
                    case NO_NDEF_OR_UNKNOWN_DATA:
                        if(pos == 0) {
                            descriptionTextView.setText(R.string.no_ndef_data_or_unknown_data);
                        } else {
                            descriptionTextView.setText(R.string.unknown_data);
                        }
                        break;

                    case ERROR_WHILE_READING_THE_AREA:
                        descriptionTextView.setText(R.string.error_while_reading_the_area);
                        break;

                    case NDEF_DATA_AVAILABLE:
                        descriptionTextView.setText(String.format("Ndef message content: %d record(s)", areaContent.ndefMsg.getNDEFRecords().size()));
                        break;

                    case AREA_READ_PROTECTED:
                        descriptionTextView.setText(R.string.area_protected_in_read);
                        break;
                }
            } else {
                if(pos == 0) {
                    descriptionTextView.setText(R.string.no_ndef_data_or_unknown_data);
                } else {
                    descriptionTextView.setText(R.string.unknown_data);
                }
            }

            title.setText(UIHelper.getAreaName(area) + String.format("\nSize: %d bytes", areaSizeInBytes));

            if (pos % 2 == 0)
                contentImage = getResources().getDrawable(R.drawable.ic_label_st_light_blue_24dp);
            else
                contentImage = getResources().getDrawable(R.drawable.ic_label_st_dark_blue_24dp);


            ImageView image = (ImageView) listItem.findViewById(R.id.thumb);
            image.setImageDrawable(contentImage);

            return listItem;
        }
    }

    private void setHeaderContent() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        STHeaderFragment fragment = new STHeaderFragment();
        fragmentTransaction.replace(R.id.area_header_fragment_container, fragment);
        fragmentTransaction.commit();

    }

    /**
     * AsyncTask retrieving the content of each area
     */
    private class asyncTaskReadAreaContent extends AsyncTask<Void, Void, ActionStatus> {

        // List local to the AsyncTask where we will store the data retrieved by the background thread
        List<AreaContent> mAsyncTaskAreaContent = new ArrayList<AreaContent>();

        public asyncTaskReadAreaContent() {

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

                mAsyncTaskAreaContent = new ArrayList<AreaContent>();

            } catch (STException e) {
                switch (e.getError()) {
                    case TAG_NOT_IN_THE_FIELD:
                        return TAG_NOT_IN_THE_FIELD;

                    default:
                        e.printStackTrace();
                        return ACTION_FAILED;
                }
            }


            for (int area= AREA1; area <= numberOfAreas; area++) {
                AreaContent areaContent = new AreaContent();

                try {
                    if (myTag instanceof MultiAreaInterface) {
                        areaContent.sizeInBytes = ((MultiAreaInterface) myTag).getAreaSizeInBytes(area);
                        areaContent.ndefMsg = ((MultiAreaInterface) myTag).readNdefMessage(area);
                    } else {
                        areaContent.sizeInBytes = myTag.getMemSizeInBytes();
                        areaContent.ndefMsg = myTag.readNdefMessage();
                    }

                    if(areaContent.ndefMsg != null) {
                        areaContent.areaContentStatus = NDEF_DATA_AVAILABLE;
                    } else {
                        // Invalid NDEF!
                        areaContent.areaContentStatus = NO_NDEF_OR_UNKNOWN_DATA;
                    }

                } catch (STException e) {
                    switch (e.getError()) {
                        case TAG_NOT_IN_THE_FIELD:
                            areaContent.areaContentStatus = ERROR_WHILE_READING_THE_AREA;
                            return TAG_NOT_IN_THE_FIELD;

                        case INVALID_CCFILE:
                        case INVALID_NDEF_DATA:
                            areaContent.ndefMsg = null;
                            areaContent.areaContentStatus = NO_NDEF_OR_UNKNOWN_DATA;
                            break;

                        case WRONG_SECURITY_STATUS:
                        case ISO15693_BLOCK_PROTECTED:
                            areaContent.areaContentStatus = AREA_READ_PROTECTED;
                            break;

                        default:
                            e.printStackTrace();
                            areaContent.ndefMsg = null;
                            areaContent.sizeInBytes = 0;
                            areaContent.areaContentStatus = ERROR_WHILE_READING_THE_AREA;
                    }
                }

                mAsyncTaskAreaContent.add(areaContent);
            }

            result = ACTION_SUCCESSFUL;

            return result;
        }

        @Override
        protected void onPostExecute(ActionStatus actionStatus) {

            switch(actionStatus) {
                case ACTION_SUCCESSFUL:
                    // Clear mAreaContent and copy all the data from mAsyncTaskAreaContent to it
                    mAreaContent.clear();

                    for (AreaContent areaContent : mAsyncTaskAreaContent) {
                        mAreaContent.add(areaContent);
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

    /**
     * AsyncTask retrieving the passwordNumber corresponding to an area of a Type5 tag.
     * When the password number is available, a password dialog box is displayed.
     */
   /* private class asyncTaskDisplayPasswordDialogBoxForType5Tag extends AsyncTask<Void, Void, ActionStatus> {

        byte mPasswordNumber;

        public asyncTaskDisplayPasswordDialogBoxForType5Tag() {

        }

        @Override
        protected ActionStatus doInBackground(Void... param) {
            ActionStatus result = ACTION_FAILED;

            if (myTag instanceof STType5PasswordInterface) {
                try {
                    mPasswordNumber = ((STType5PasswordInterface) myTag).getPasswordNumber(mAreaSelected);

                    if((myTag instanceof ST25DVTag) && (mPasswordNumber == 0)) {
                        result = NO_PASSWORD_SELECTED_FOR_THIS_AREA;
                    } else {
                        result = ACTION_SUCCESSFUL;
                    }

                } catch (STException e) {
                    e.printStackTrace();
                }
            } else {
                if (myTag instanceof STType5PasswordInterface) {
                    // TODO:
                    // The tag implements STType5PasswordInterface but doesn't implement MultiAreaInterface.
                    // This is the case for instance of Vicinity tags.
                    // I don't know how to retrieve the password number in that case!
                    Log.e(TAG, "Error! Not implemented yet!");

                } else {
                    Log.e(TAG, "Error! This tag doesn't have a password interface!");
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(ActionStatus actionStatus) {

            switch(actionStatus) {
                case ACTION_SUCCESSFUL:
                    String dialogMsg = String.format(getResources().getString(R.string.enter_area_pwd), UIHelper.getAreaName(mAreaSelected));

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                    STType5PwdDialogFragment pwdDialogFragment = STType5PwdDialogFragment.newInstance(
                            STType5PwdDialogFragment.STPwdAction.PRESENT_CURRENT_PWD,
                            mPasswordNumber,
                            dialogMsg,
                            AreasEditorFragment.this);
                    pwdDialogFragment.show(fragmentManager, "pwdDialogFragment");
                    break;

                case NO_PASSWORD_SELECTED_FOR_THIS_AREA:
                    showToast(R.string.no_password_selected_for_this_area);
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

    private class asyncTaskForType4Password extends AsyncTask<Void, Void, ActionStatus> {


        private AreasEditorFragment mFragment;

        public asyncTaskForType4Password(AreasEditorFragment areasEditorFragment) {
            mFragment = areasEditorFragment;
        }

        @Override
        protected ActionStatus doInBackground(Void... param) {
            ActionStatus result = ActionStatus.ACTION_FAILED;

            try {
                    PwdDialogFragment pwdDialogFragment = PwdDialogFragment.newInstance("", getFragmentManager(),
                            mFragment, ((STType4PasswordInterface) myTag).getReadPasswordLengthInBytes(mAreaSelected + 1));
                    mReadPassword = pwdDialogFragment.getPassword();
                }
            catch (STException e) {
                return result;
            }

            result = ActionStatus.ACTION_SUCCESSFUL;
            return result;
        }
    }

*/

}

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
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import com.st.st25nfc.generic.util.UIHelper;
import com.st.st25sdk.STException;

import static com.st.st25nfc.generic.RawReadWriteFragment.ActionStatus.ACTION_SUCCESSFUL;
import static com.st.st25nfc.generic.RawReadWriteFragment.ActionStatus.ACTION_FAILED;
import static com.st.st25nfc.generic.RawReadWriteFragment.ActionStatus.TAG_NOT_IN_THE_FIELD;


public class RawReadWriteFragment extends STFragment {

    private final int READ_MEMORY = 0;
    private final int WRITE_MEMORY = 1;
    private final int DUMP_MEMORY = 2;
    private final int FILL_MEMORY = 3;
    private final int ERASE_MEMORY = 4;

    private final int NUMBER_OF_CHOICES = 5;

    final static String TAG = "RawMemoryFragment";

    public CustomListAdapter mAdapter;
    public ListView mList;

    private LayoutInflater mInflater;

    enum ActionStatus {
        ACTION_SUCCESSFUL,
        ACTION_FAILED,
        TAG_NOT_IN_THE_FIELD
    };


    public static RawReadWriteFragment newInstance(Context context) {
        RawReadWriteFragment f = new RawReadWriteFragment();
        /* If needed, pass some argument to the fragment
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        */

        // Set the title of this fragment
        f.setTitle(context.getResources().getString(R.string.memory));

        return f;
    }

    public RawReadWriteFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mList = (ListView) inflater.inflate(R.layout.fragment_list_view, container, false);
        mInflater = inflater;
        mAdapter = new CustomListAdapter(getActivity());
        mList.setAdapter(mAdapter);

        //handle main ListView clicks
        mList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                            long arg3) {
                        Intent intent;
                        switch (arg2) {
                            case READ_MEMORY:
                                intent = new Intent(getActivity(), ReadFragmentActivity.class);
                                startActivityForResult(intent, 1);
                                break;
                            case WRITE_MEMORY:
                                intent = new Intent(getActivity(), WriteFragmentActivity.class);
                                startActivityForResult(intent, 1);
                                break;
                            case DUMP_MEMORY:
                                intent = new Intent(getActivity(), DumpMemoryFragmentActivity.class);
                                startActivityForResult(intent, 1);
                                break;
                            case FILL_MEMORY:
                                intent = new Intent(getActivity(), FillMemoryFragmentActivity.class);
                                startActivityForResult(intent, 1);
                                break;
                            case ERASE_MEMORY:
                                askConfirmation();
                                break;
                            default:
                                Log.e(TAG, "Invalid position");
                                break;
                        }
                    }
                }
        );


        return (View) mList;


    }

    private void askConfirmation() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // set title
        alertDialogBuilder.setTitle("Confirmation needed");

        // set dialog message
        alertDialogBuilder
                .setMessage("Do you want to erase the tag's memory?")
                .setCancelable(true)

                .setPositiveButton("Erase tag's memory",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                        eraseTagMemory();
                    }
                })
                .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void eraseTagMemory() {
        new eraseTagMemoryAsyncTask().execute();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    class CustomListAdapter extends BaseAdapter {


        public CustomListAdapter(Activity activity) {

        }

        //get read_list_items count
        @Override
        public int getCount() {
            return NUMBER_OF_CHOICES;
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
                listItem = mInflater.inflate(R.layout.read_list_items, parent, false);
            }


            TextView title = (TextView) listItem.findViewById(R.id.title);

            Drawable contentImage = null;

            switch (pos) {
                case READ_MEMORY:
                    title.setText(R.string.read_memory);
                    contentImage = getResources().getDrawable(R.drawable.ic_memory_st_dark_blue_24dp);
                    break;
                case WRITE_MEMORY:
                    title.setText(R.string.write_memory);
                    contentImage = getResources().getDrawable(R.drawable.ic_memory_st_light_purple_24dp);
                    break;
                case DUMP_MEMORY:
                    title.setText(R.string.dump_memory_to_file);
                    contentImage = getResources().getDrawable(R.drawable.ic_memory_st_light_blue_24dp);
                    break;
                case FILL_MEMORY:
                    title.setText(R.string.fill_memory_from_file);
                    contentImage = getResources().getDrawable(R.drawable.ic_memory_st_light_purple_24dp);
                    break;
                case ERASE_MEMORY:
                    title.setText(R.string.erase_memory);
                    contentImage = getResources().getDrawable(R.drawable.ic_memory_st_dark_blue_24dp);
                    break;
                default:
                    Log.e(TAG, "Invalid position");
                    break;
            }

            ImageView image = (ImageView) listItem.findViewById(R.id.thumb);
            image.setImageDrawable(contentImage);

            return listItem;
        }
    }


    private class eraseTagMemoryAsyncTask extends AsyncTask<Void, Void, ActionStatus> {

        public eraseTagMemoryAsyncTask() {
        }

        @Override
        protected ActionStatus doInBackground(Void... param) {
            ActionStatus result;

            try {
                int memSizeInBytes = myTag.getMemSizeInBytes();

                byte[] data = new byte[memSizeInBytes];
                myTag.writeBytes(0, data);

                // Tag's memory has been erased so the cache should be invalidated
                UIHelper.invalidateCache(myTag);

                result = ACTION_SUCCESSFUL;

            } catch (STException e) {
                switch (e.getError()) {
                    case TAG_NOT_IN_THE_FIELD:
                        result = TAG_NOT_IN_THE_FIELD;
                        break;

                    default:
                        e.printStackTrace();
                        result = ACTION_FAILED;
                        break;
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(ActionStatus actionStatus) {

            switch(actionStatus) {
                case ACTION_SUCCESSFUL:
                    showToast(R.string.tag_updated);
                    break;

                case TAG_NOT_IN_THE_FIELD:
                    showToast(R.string.tag_not_in_the_field);
                    break;

                case ACTION_FAILED:
                default:
                    showToast(R.string.error_while_erasing_the_tag);
                    break;
            }
        }
    }

}

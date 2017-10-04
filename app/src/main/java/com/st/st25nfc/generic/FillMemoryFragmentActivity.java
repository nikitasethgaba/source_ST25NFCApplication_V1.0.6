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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.st.st25sdk.Helper;
import com.st.st25nfc.R;
import com.st.st25sdk.STException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FillMemoryFragmentActivity extends STFragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener, STFragment.STFragmentListener, View.OnClickListener {

    // Set here the Toolbar to use for this activity
    private ST25Menu mMenu;
    private int toolbar_res = R.menu.toolbar_empty;

    // Address at which we will write the data
    static private int mDestinationByteAddress;

    // Data to write to memory
    private byte[] mBuffer;

    private EditText mDestinationOffsetEditText;
    private TextView mSourceFileTextView;

    // The data are now read by Byte but we will still format the display by raw of 4 Bytes
    private final  int NBR_OF_BYTES_PER_RAW = 4;

    private static final String TAG = "FillMemory";
    private ListView lv;
    private Handler mHandler;
    private CustomListAdapter mAdapter;
    private Thread mThread;
    private Uri mSelectedfileUri;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_layout);

        // Inflate content of FrameLayout
        FrameLayout frameLayout=(FrameLayout) findViewById(R.id.frame_content);
        View childView = getLayoutInflater().inflate(R.layout.fragment_fill_memory, null);
        frameLayout.addView(childView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getTag().getName());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mMenu = ST25Menu.newInstance(super.getTag());

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        mMenu.inflateMenu(navigationView);

        mSourceFileTextView = (TextView) findViewById(R.id.sourceFileTextView);
        mSelectedfileUri = null;

        mDestinationOffsetEditText = (EditText) findViewById(R.id.destinationOffsetEditText);
        mDestinationOffsetEditText.setText(Helper.convertIntToHexFormatString(mDestinationByteAddress));

        mHandler = new Handler();

        showDownloadDirectoryContent();

        Button selectSourceFileButton = (Button) findViewById(R.id.selectSourceFileButton);
        selectSourceFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent()
                        .setType("*/*")
                        .setAction(Intent.ACTION_GET_CONTENT);

                // Open a dialogBox allowing to select a file in phone's memory.
                startActivityForResult(Intent.createChooser(intent, "Select a file"), 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0 && resultCode==RESULT_OK) {
            mSelectedfileUri = data.getData(); //The uri with the location of the file
            mSourceFileTextView.setText(mSelectedfileUri.getPath());
        }
    }

    private void showDownloadDirectoryContent() {
        File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!downloadFolder.exists()) {
            downloadFolder.mkdir();
        }

        /* Show the list of files present in the Downloads directory */
        Log.v(TAG, "Files and folders present in Download directory:");
        File[] filesList = downloadFolder.listFiles();
        if(filesList != null) {
            for (File file : filesList) {
                Log.v(TAG, file.getName() );
            }
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds read_list_items to the action bar if it is present.
        getMenuInflater().inflate(toolbar_res, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long

        // as you specify a parent activity in AndroidManifest.xml.


        return super.onOptionsItemSelected(item);
    }

    /**
     * Parses the NdefSTMessage Message from the intent and prints to the TextView
     */
    TextView textView;

    void processIntent(Intent intent) {
        Log.d(TAG, "Process Intent");
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return mMenu.selectItem(this, item);
    }

    class ContentView implements Runnable {
        public void run() {
            byte buffer[] = null;
            lv = (ListView) findViewById(R.id.writeBlockListView);

            try {
                getTag().writeBytes(mDestinationByteAddress, mBuffer);

                // Re-read the data and display them
                buffer = getTag().readBytes(mDestinationByteAddress, mBuffer.length);

                // Warning: readBytes() may return less bytes than requested
                if(buffer.length != mBuffer.length) {
                    String txt = String.format(getResources().getString(R.string.error_during_read_operation), buffer.length);
                    showToast(txt);
                }

            } catch (STException e) {
                Log.e(TAG, e.getMessage());
                showToast(R.string.Command_failed);
            }

            if (buffer != null) {
                mAdapter = new CustomListAdapter(buffer);


                if (mHandler != null && lv != null) {
                    mHandler.post(new Runnable() {
                        public void run() {
                            lv.setAdapter(mAdapter);
                        }
                    });
                }
            }
        }
    }


    @Override
    public void onClick(View v) {

        // Hide Soft Keyboard
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        if(mSelectedfileUri == null) {
            showToast(R.string.please_select_source_file);
            return;
        }

        InputStream inputStream;
        try {
            inputStream = getContentResolver().openInputStream(mSelectedfileUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        try {
            int fileSize = inputStream.available();

            mBuffer = new byte[fileSize];
            inputStream.read(mBuffer);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            showToast("Source file not found!");
            return;
        } catch (IOException e) {
            e.printStackTrace();
            showToast("Failed to read source file!");
            return;
        }

        mThread = new Thread(new ContentView());

        try {
            mDestinationByteAddress = Helper.convertHexStringToInt(mDestinationOffsetEditText.getText().toString());
        } catch (STException e) {
            e.printStackTrace();
            return;
        }

        Snackbar snackbar = Snackbar.make(v, "", Snackbar.LENGTH_LONG);

        snackbar.setAction("Writing " + mBuffer.length + " Bytes of data at offset" + mDestinationByteAddress, this);
        snackbar.setActionTextColor(getResources().getColor(R.color.white));

        snackbar.show();
        mThread.start();
    }

    public void onPause() {
        if (mThread != null)
            try {
                mThread.join();
            } catch (InterruptedException e) {
                Log.e(TAG, "Issue joining thread");
            }
        super.onPause();
    }

    class CustomListAdapter extends BaseAdapter {

        byte[] mBuffer;

        public CustomListAdapter(byte[] buffer) {

            mBuffer = buffer;
        }

        //get read_list_items count
        @Override
        public int getCount() {
            try {
                return Helper.divisionRoundedUp(mBuffer.length, NBR_OF_BYTES_PER_RAW);
            } catch (STException e) {
                e.printStackTrace();
                return 0;
            }
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
            String data;
            Byte myByte;
            int address;
            char char1 = ' ';
            char char2 = ' ';
            char char3 = ' ';
            char char4 = ' ';
            String byte1Str = "  ";
            String byte2Str = "  ";
            String byte3Str = "  ";
            String byte4Str = "  ";

            // The data are now read by Byte but we will still format the display by raw of 4 Bytes

            // Get the 4 Bytes to display on this raw
            address = pos * NBR_OF_BYTES_PER_RAW;
            if(address < mBuffer.length) {
                myByte = mBuffer[address];
                byte1Str = Helper.convertByteToHexString(myByte).toUpperCase();
                char1 = getChar(myByte);
            }

            address = pos * NBR_OF_BYTES_PER_RAW + 1;
            if(address < mBuffer.length) {
                myByte = mBuffer[address];
                byte2Str = Helper.convertByteToHexString(myByte).toUpperCase();
                char2 = getChar(myByte);
            }

            address = pos * NBR_OF_BYTES_PER_RAW + 2;
            if(address < mBuffer.length) {
                myByte = mBuffer[address];
                byte3Str = Helper.convertByteToHexString(myByte).toUpperCase();
                char3 = getChar(myByte);
            }

            address = pos * NBR_OF_BYTES_PER_RAW + 3;
            if(address < mBuffer.length) {
                myByte = mBuffer[address];
                byte4Str = Helper.convertByteToHexString(myByte).toUpperCase();
                char4 = getChar(myByte);
            }

            if (listItem == null) {
                //set the main ListView's layout
                listItem = getLayoutInflater().inflate(R.layout.read_fragment_item, parent, false);
            }
            TextView addresssTextView = (TextView) listItem.findViewById(R.id.addrTextView);
            TextView hexValuesTextView = (TextView) listItem.findViewById(R.id.hexValueTextView);
            TextView asciiValueTextView = (TextView) listItem.findViewById(R.id.asciiValueTextView);

            String startAddress = String.format("%s %3d: ", getResources().getString(R.string.addr), mDestinationByteAddress + pos * NBR_OF_BYTES_PER_RAW);
            addresssTextView.setText(startAddress);

            data = String.format("%s %s %s %s", byte1Str, byte2Str, byte3Str, byte4Str);
            hexValuesTextView.setText(data);

            data = String.format("  %c%c%c%c", char1, char2, char3, char4);
            asciiValueTextView.setText(data);

            return listItem;
        }
    }

    private char getChar(byte myByte) {
        char myChar = ' ';

        if(myByte > 0x20) {
            myChar = (char) (myByte & 0xFF);
        }

        return myChar;
    }

}


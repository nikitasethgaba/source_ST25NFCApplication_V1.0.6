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

package com.st.st25nfc.type5.st25dv;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.ST25Menu;
import com.st.st25nfc.generic.STFragmentActivity;
import com.st.st25sdk.Helper;
import com.st.st25sdk.type5.ST25DVTag;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;



public class ST25DVFirmwareUpdateDemoActivity
        extends STFragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener, ST25DVTransferTask.OnTransferListener {


    public ST25DVTag mST25DVTag;


    private ST25DVTransferTask mTransferTask;
    private Chronometer mChronometer;

    private ListView mLv;
    private CustomListAdapter mAdapter;


    private final static String FIRWMARE_DIR = "/bintouploadforstSt25FWU/";
    private File[] mFirmwareFiles;
    private String mSelectedAbsolutePath;

    private byte[] mPassword;

    private ProgressBar mProgress;
    private double mProgressStatus;
    //private Handler mHandler;

    private enum Action {
        INIT,
        PRESENT_PASSWORD,
        UPLOAD_FIRMWARE,
        START_CHRONOMETER
    };

    private final int GET_FIRMWARE_LIST = 1;
    private Thread mThread;
    private int mTransferAction;
    private Action mCurrentAction;

    private int mApiVersion = Build.VERSION.SDK_INT;

    private byte[] mBuffer;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_layout);

        // Inflate content of FrameLayout
        FrameLayout frameLayout=(FrameLayout) findViewById(R.id.frame_content);
        View childView = getLayoutInflater().inflate(R.layout.firmware_update_demo_content_st25dv, null);
        frameLayout.addView(childView);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //Button fab = (Button) findViewById(R.id.button);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.button);
        //fab.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mMenu = ST25Menu.newInstance(super.getTag());
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        mMenu.inflateMenu(navigationView);

        mLv = (ListView) findViewById(R.id.firmware_transfer_list_view);


        mAdapter = new CustomListAdapter(this);
        mLv.setOnItemClickListener(mAdapter);
        mLv.setAdapter(mAdapter);


        mST25DVTag = (ST25DVTag) super.getTag();

        Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {startTranfer();
            }
        });

        Button stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {stopTransfer();
            }
        });

        Button pauseButton = (Button) findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pauseTransfer();
                mChronometer.pause();
            }
        });

        Button resumeButton = (Button) findViewById(R.id.resumeButton);
        resumeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                resumeTransfer();
                mChronometer.resume();
            }
        });

        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        //mHandler = new Handler();

        mChronometer = (Chronometer) findViewById(R.id.st25DvChronometer);

        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            public void onChronometerTick(Chronometer chronometer) {

            }
        });

        mCurrentAction = Action.INIT;

        toolbar.setTitle(mST25DVTag.getName());

        if (mApiVersion >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        GET_FIRMWARE_LIST);
            }
        }

        //setContentView();
    }



    private Handler mHandler = new Handler() {
        public void handleMessage(Message m) {
                Action action = (Action) m.obj;
                 switch (action) {
                     case PRESENT_PASSWORD:
                         sendPassword();
                         break;
                     case UPLOAD_FIRMWARE:
                         startUpload();
                         break;
                     case START_CHRONOMETER:
                         mChronometer.start();
                         break;
                     default:
                         break;
            }
        }
    };


    public void transferOnProgress(double progressStatus) {
        // Start lengthy operation in a background thread

        if (mTransferAction != ST25DVTransferTask.FAST_PRESENT_PWD_FUNCTION) {
            if (mProgress != null) {
                if (mProgressStatus == 0 && progressStatus != 0) {
                    Message message = mHandler.obtainMessage();
                    message.obj = Action.START_CHRONOMETER;
                    mHandler.sendMessage(message);
                }
                mProgress.setProgress((int) progressStatus);
                mProgressStatus = progressStatus;
            }
        }
    }


    public void transferFinished(boolean success, final long timeTransfer, byte[] buffer) {

        if (mTransferAction == ST25DVTransferTask.FAST_PRESENT_PWD_FUNCTION) {
            if (success) {
                showToast("Password OK");
                Message message = mHandler.obtainMessage();
                message.obj = Action.UPLOAD_FIRMWARE;
                mHandler.sendMessage(message);

            } else {
                showToast("Wrong password [or MailBox not enabled]");
            }
        } else if (mTransferAction == ST25DVTransferTask.FAST_FIRMWARE_UPDATE_FUNCTION) {
            mChronometer.stop();
            if (success) {
                showToast("Transfer OK");
            } else {
                showToast("Transfer Failed");
            }
        }

    }


    @Override
    public byte[] getDataToWrite() {
        return null;
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        stopTransfer();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds read_list_items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_empty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long

        // as you specify a parent activity in AndroidManifest.xml.


        return super.onOptionsItemSelected(item);
    }


    void processIntent(Intent intent) {

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return mMenu.selectItem(this, item);
    }

    public ST25DVTag getTag() {
        return mST25DVTag;
    }

    public void onPause() {
        /*if (mThread != null)
            try {
                mThread.join();
            } catch (InterruptedException e) {
                Log.e(TAG, "Issue joining thread");
            }*/
        super.onPause();
    }


    private void fillBuffer() {
        try {
            mBuffer = null;
            FileInputStream in = new FileInputStream(mSelectedAbsolutePath);
            int len = (in.available() > 1000) ? 1000 : in.available();
            int size;
            byte[] readBuffer = new byte[len];

            while ((size = in.read(readBuffer, 0, len)) >= 0) {
                byte[] tmpBuffer = null;

                if (mBuffer != null) {
                    tmpBuffer = new byte[mBuffer.length];
                    System.arraycopy(mBuffer, 0, tmpBuffer, 0, mBuffer.length);
                    mBuffer = new byte[mBuffer.length + size];
                } else {
                    mBuffer = new byte[size];
                }

                if (tmpBuffer != null)
                    System.arraycopy(tmpBuffer, 0, mBuffer, 0, tmpBuffer.length);

                System.arraycopy(readBuffer, 0, mBuffer, mBuffer.length - size, size);

            }

        } catch (IOException e) {
        }

    }

    public void startTranfer() {

        switch (mCurrentAction) {
            case UPLOAD_FIRMWARE:
                //when password is ok the transfer start
                presentPassword();
                break;
            case INIT:
                showToast("Please select a Firmware");
                break;
            default:
                break;
        }


    }

    public void stopTransfer() {
        mChronometer.stop();
        if (mTransferTask != null) mTransferTask.stop();
    }

    public void pauseTransfer() {
        mChronometer.pause();
        if (mTransferTask != null) mTransferTask.pause();
    }

    public void resumeTransfer() {
        mChronometer.resume();
        if (mTransferTask != null) mTransferTask.resume();
    }


    class CustomListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, PopupMenu.OnMenuItemClickListener {

        ST25DVFirmwareUpdateDemoActivity mActivity;
        View mlistItem;

        public CustomListAdapter(ST25DVFirmwareUpdateDemoActivity st25DVMailboxDataTransferActivity) {
            mActivity = st25DVMailboxDataTransferActivity;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItem = convertView;

            if (listItem == null) {
                //set the main ListView's layout
                listItem = getLayoutInflater().inflate(R.layout.data_transfer_mailbox_items, parent, false);
            }

            TextView title = (TextView) listItem.findViewById(R.id.title);
            TextView description = (TextView) listItem.findViewById(R.id.description);
            Drawable contentImage = null;
            ImageView image = null;

            switch (position) {
                case 0:
                    title.setText(R.string.Pick_firmware_upload);

                    if (mCurrentAction != Action.UPLOAD_FIRMWARE) {
                        contentImage = getResources().getDrawable(R.drawable.st_grey_circle);
                        //description.setText(R.string.Select_Buffer_Size);
                    } else {
                        //description.setText(String.valueOf(mSize) + " Bytes");
                        contentImage = getResources().getDrawable(R.drawable.st_green_circle);
                    }
                    break;
                default:
                     break;
            }

            image = (ImageView) listItem.findViewById(R.id.thumb);
            image.setImageDrawable(contentImage);
            mlistItem = listItem;

            return listItem;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            if (position == 0) {
                mCurrentAction = Action.UPLOAD_FIRMWARE;
                getFirmwaresList();

                if (position == 0) {
                    if (mFirmwareFiles != null) {
                        PopupMenu popupMenu = new PopupMenu(mActivity, view);
                        for (int i = 0; i < mFirmwareFiles.length; i++) {
                            popupMenu.getMenu().add(0, i, 0, mFirmwareFiles[i].getName());

                        }
                        //popupMenu.setOnMenuItemClickListener(this);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                            @Override
                            public boolean onMenuItemClick(
                                    android.view.MenuItem item) {
                                if (mFirmwareFiles[item.getItemId()].getName() != null) {
                                    mSelectedAbsolutePath = mFirmwareFiles[item.getItemId()].getAbsolutePath();
                                    int size = getSelectedFileSize(mFirmwareFiles[item.getItemId()].getAbsolutePath());
                                    TextView description = (TextView) mlistItem.findViewById(R.id.description);
                                    description.setText(mFirmwareFiles[item.getItemId()].getName() + " " + size + " Bytes");
                                }
                                return false;
                            }
                        });

                        popupMenu.show();
                    }
                }
            }

            mAdapter.notifyDataSetChanged();
        }

        private int getSelectedFileSize(String file) {
            File f;
            int fileSize = 0;
            f = new File(file);
            if (f.exists()) fileSize = (int) f.length();
            return fileSize;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            mSelectedAbsolutePath = mFirmwareFiles[item.getItemId()].getAbsolutePath();
            mAdapter.notifyDataSetChanged();
            return true;
        }
    }


    /* Checks if external storage is available to at least read */
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private void getFirmwaresList() {

        File internalStorage;
        File externalStorage;

        File appStorageFile = getFilesDir();
        String path = appStorageFile.getAbsolutePath() + FIRWMARE_DIR;

        internalStorage = new File(path);
        if (!internalStorage.exists()) {
            internalStorage.mkdirs();
        }

        // Retrieve files from SDCard.
        File extStorageFile = Environment.getExternalStorageDirectory();
        path = extStorageFile.getAbsolutePath() + "/Download/" + FIRWMARE_DIR;

        externalStorage = new File(path);
        if (!externalStorage.exists()) {
            externalStorage.mkdir();  // build directory in user wants to store within his own firmwares.
        }


        int nbOfFiles = 0;
        if (externalStorage.listFiles() != null)
            nbOfFiles = externalStorage.listFiles().length;
        if (internalStorage.listFiles() != null)
            nbOfFiles += internalStorage.listFiles().length;

        if (nbOfFiles > 0) {
            mFirmwareFiles = new File[nbOfFiles];

            if (internalStorage.listFiles() != null ) {
                for (int i = 0; i < internalStorage.listFiles().length; i++)
                    mFirmwareFiles[i] = internalStorage.listFiles()[i];
            }
            if (externalStorage.listFiles() != null) {
                for (int i = internalStorage.listFiles().length; i < nbOfFiles; i++)
                    mFirmwareFiles[i] = externalStorage.listFiles()[i - internalStorage.listFiles().length];
            }
        }

    }

    private void presentPassword() {

        View promptView = getLayoutInflater().inflate(R.layout.present_firmware_password, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        final EditText passwordEditText = (EditText) promptView.findViewById(R.id.user_input);

        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String password = passwordEditText.getText().toString();
                mPassword =  Helper.convertHexStringToByteArray(password);
                sendPassword();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }


    private void sendPassword() {
        mTransferAction = ST25DVTransferTask.FAST_PRESENT_PWD_FUNCTION;
        mTransferTask = new ST25DVTransferTask(mTransferAction, mPassword, mST25DVTag);
        mTransferTask.setTransferListener(this);
        new Thread(mTransferTask).start();
    }

    private void startUpload() {
        mTransferAction = ST25DVTransferTask.FAST_FIRMWARE_UPDATE_FUNCTION;
        fillBuffer();
        mTransferTask = new ST25DVTransferTask(mTransferAction, mBuffer, mST25DVTag);
        mTransferTask.setTransferListener(this);
        mProgressStatus = 0;
        new Thread(mTransferTask).start();
    }
}

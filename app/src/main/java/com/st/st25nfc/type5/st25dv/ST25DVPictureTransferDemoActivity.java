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
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.ST25Menu;
import com.st.st25sdk.STLog;
import com.st.st25nfc.generic.STFragmentActivity;
import com.st.st25sdk.type5.ST25DVTag;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;


public class ST25DVPictureTransferDemoActivity
        extends STFragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener, ST25DVTransferTask.OnTransferListener {


    public ST25DVTag mST25DVTag;


    private ST25DVTransferTask mTransferTask;
    private Chronometer mChronometer;

    private ListView mLv;
    private CustomListAdapter mAdapter;


    private ProgressBar mProgress;
    private Handler mHandler;

    private enum Action {
        UPLOAD_TAKEN_PICTURE,
        UPLOAD_PICKED_PICTURE,
        DOWNLOAD_PICTURE,
        UNKNOWN_ACTION
    };

    private Thread mThread;
    private int mTransferAction;
    private Action mCurrentAction = Action.UNKNOWN_ACTION;

    private int mApiVersion = android.os.Build.VERSION.SDK_INT;
    private final int CAMERA_CAPTURE = 0;
    private final int PICK_PICTURE = 1;
    private final int PICTURE_CROP = 4;
    private Uri mPictureUri = null;
    private Bitmap mPicture = null;
    private byte[] mBuffer;

    private final int mDefaultPhotoDisplayHSize = 240;
    private final int mDefaultPhotoDisplayWSize = 320;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_layout);

        // Inflate content of FrameLayout
        FrameLayout frameLayout=(FrameLayout) findViewById(R.id.frame_content);
        View childView = getLayoutInflater().inflate(R.layout.picture_transfer_demo_content_st25dv, null);
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

        mLv = (ListView) findViewById(R.id.picture_transfer_list_view);

        mAdapter = new CustomListAdapter(this);
        mLv.setOnItemClickListener(mAdapter);
        mLv.setAdapter(mAdapter);


        mST25DVTag = (ST25DVTag) super.getTag();

        Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startTranfer();
            }
        });

        Button stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stopTransfer();
            }
        });

        Button pauseButton = (Button) findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pauseTransfer();
            }
        });

        Button resumeButton = (Button) findViewById(R.id.resumeButton);
        resumeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                resumeTransfer();
            }
        });

        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mHandler = new Handler();

        mChronometer = (Chronometer) findViewById(R.id.st25DvChronometer);

        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            public void onChronometerTick(Chronometer chronometer) {

            }
        });

        if (mApiVersion >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        CAMERA_CAPTURE);
            }


            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PICK_PICTURE);
            }
        }

        toolbar.setTitle(mST25DVTag.getName());
        setContentView();
    }

    public void transferOnProgress(double progressStatus) {
        // Start lengthy operation in a background thread

        if (mProgress != null)
            mProgress.setProgress((int) progressStatus);
    }


    public void transferFinished(boolean success, final long timeTransfer, byte[] buffer) {
        mChronometer.stop();
        if (success) {
            showToast("Transfer OK");
            if (mCurrentAction == Action.DOWNLOAD_PICTURE &&
                    buffer != null)
                displayPicture(buffer);
        } else {
            showToast("Transfer Failed [or MailBox not enabled]");
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

    public void setContentView() {

        class ContentView implements Runnable {

            @Override
            public void run() {

            }
        }

        new Thread(new ContentView()).start();

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


    public void startTranfer() {
        switch (mCurrentAction) {
            case DOWNLOAD_PICTURE:
                mTransferAction = ST25DVTransferTask.FAST_IMAGE_DOWNLOAD_FUNCTION;
                break;
            case UPLOAD_PICKED_PICTURE:
            case UPLOAD_TAKEN_PICTURE:
                mTransferAction = ST25DVTransferTask.FAST_IMAGE_UPLOAD_FUNCTION;
                break;
            default:
                break;
        }
        if (mTransferAction == ST25DVTransferTask.FAST_IMAGE_DOWNLOAD_FUNCTION || mTransferAction == ST25DVTransferTask.FAST_IMAGE_UPLOAD_FUNCTION) {
            // check that buffer to download is not empty - task preparation will fail
            if (mTransferAction == ST25DVTransferTask.FAST_IMAGE_UPLOAD_FUNCTION) {
                if (mBuffer == null) {
                    String errorMessage = "Empty picture or picture not selected";
                    showToast(errorMessage);
                    return;
                }
            }
            mTransferTask = new ST25DVTransferTask(mTransferAction, mBuffer, mST25DVTag);
            mTransferTask.setTransferListener(this);
            new Thread(mTransferTask).start();
            mChronometer.start();
        } else {
            String errorMessage = "No use case selected - Please, select a use case first";
            showToast(errorMessage);
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

        ST25DVPictureTransferDemoActivity mActivity;

        public CustomListAdapter(ST25DVPictureTransferDemoActivity st25DVMailboxDataTransferActivity) {
            mActivity = st25DVMailboxDataTransferActivity;
        }

        @Override
        public int getCount() {
            return 3;
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
                    title.setText(R.string.Take_picture_upload);
                    image = (ImageView) listItem.findViewById(R.id.picture);

                    if (mCurrentAction != Action.UPLOAD_TAKEN_PICTURE) {
                        contentImage = getResources().getDrawable(R.drawable.st_grey_circle);
                        image.setVisibility(View.INVISIBLE);
                        //description.setText(R.string.Select_Buffer_Size);
                    } else {
                        //description.setText(String.valueOf(mSize) + " Bytes");
                        contentImage = getResources().getDrawable(R.drawable.st_green_circle);
                        if (mPicture != null && mCurrentAction == Action.UPLOAD_TAKEN_PICTURE) {
                            image = (ImageView) listItem.findViewById(R.id.picture);
                            image.setImageBitmap(mPicture);
                            image.setVisibility(View.VISIBLE);
                            description.setText("Picture size: " + computeAndSetEstimedImageSize(image) + " Bytes");
                        }
                    }
                    break;

                case 1:
                    title.setText(R.string.Download_picture);

                    if (mCurrentAction != Action.DOWNLOAD_PICTURE) {
                        //description.setText(R.string.Buffer_Size_SetByHost);
                        contentImage = getResources().getDrawable(R.drawable.st_grey_circle);
                    } else {
                        //description.setText(String.valueOf(mSize) + " Bytes");
                        contentImage = getResources().getDrawable(R.drawable.st_green_circle);
                    }
                    break;

                case 2:
                    title.setText(R.string.Pick_picture_upload);
                    image = (ImageView) listItem.findViewById(R.id.picture);

                    if (mCurrentAction != Action.UPLOAD_PICKED_PICTURE) {
                        //description.setText(R.string.Buffer_Size_SetByHost);
                        contentImage = getResources().getDrawable(R.drawable.st_grey_circle);
                        image.setVisibility(View.INVISIBLE);
                    } else {
                        //description.setText(String.valueOf(mSize) + " Bytes");
                        contentImage = getResources().getDrawable(R.drawable.st_green_circle);
                        if (mPicture != null && mCurrentAction == Action.UPLOAD_PICKED_PICTURE) {
                            image = (ImageView) listItem.findViewById(R.id.picture);
                            image.setImageBitmap(mPicture);
                            image.setVisibility(View.VISIBLE);
                            description.setText("Picture size: " + computeAndSetEstimedImageSize(image) + " Bytes");
                        }
                    }
                    break;
                default:
                    break;
            }

            image = (ImageView) listItem.findViewById(R.id.thumb);
            image.setImageDrawable(contentImage);


            return listItem;

        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



            if (position == 0) {
                mCurrentAction = Action.UPLOAD_TAKEN_PICTURE;
                captureFrame();
            } else if (position == 1) {
                mCurrentAction = Action.DOWNLOAD_PICTURE;
            } else if (position == 2) {
                mCurrentAction = Action.UPLOAD_PICKED_PICTURE;
                pickImageFromGallery();
            }

            mAdapter.notifyDataSetChanged();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            mAdapter.notifyDataSetChanged();
            return true;
        }
    }

    private int computeAndSetEstimedImageSize(ImageView imageView) {
        byte[] bufferFile = null;
        byte[] initialPicData;
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, (int) 100, baos); // bm

        initialPicData = baos.toByteArray();
        int modPicture = initialPicData.length % 4;
        if (modPicture != 0 ) {
            bufferFile = new byte[initialPicData.length + (4-modPicture)];
            System.arraycopy(initialPicData, 0, bufferFile, 0, initialPicData.length);
        } else {
            bufferFile = baos.toByteArray();
        }
        return bufferFile.length;
    }

    public void displayPicture(byte[] buffer) {
        class DisplayPicture implements Runnable {

            byte[] mBuffer;

            public DisplayPicture(byte[] buffer) {
                mBuffer = buffer;
            }

            @Override
            public void run() {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mLv.getContext());
                View v = getLayoutInflater().inflate(R.layout.transfer_dialog_layout, null);
                Bitmap bitmap = BitmapFactory.decodeByteArray(mBuffer, 0, mBuffer.length);

                ImageView imageView = (ImageView) v.findViewById(R.id.image_uploaded);
                imageView.setImageBitmap(bitmap);

                alertDialogBuilder.setView(v);
                alertDialogBuilder.show();

            }
        }
        mHandler.post(new DisplayPicture(buffer));

    }


    public void captureFrame() {
        try {

            Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(captureIntent, CAMERA_CAPTURE);
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = " You're device doesn't support Capturing";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
        }
    }


    public void pickImageFromGallery() {


        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("outputX", mDefaultPhotoDisplayWSize);
        intent.putExtra("outputY", mDefaultPhotoDisplayHSize);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("return-data", true);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PICTURE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAMERA_CAPTURE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        mPicture = bitmapResize((Bitmap) data.getExtras().get("data"));
                        compressImage(mPicture);
                        mAdapter.notifyDataSetChanged();;
                    } else {
                        String errorMessage = "Your device doesn't support the image capturing feature";
                        Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                    }
                }
                break;

            case PICK_PICTURE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        try {
                            Bitmap picture;
                            if (data.getExtras() == null) {

                                Uri contentURI = Uri.parse(data.getDataString());
                                ContentResolver cr = getContentResolver();
                                InputStream in = cr.openInputStream(contentURI);
                                picture = BitmapFactory.decodeStream(in, null, null);
                            } else {
                                picture = (Bitmap) data.getExtras().get("data");
                            }
                            mPicture = bitmapResize(picture);
                            compressImage(mPicture);
                            mAdapter.notifyDataSetChanged();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;

            case PICTURE_CROP:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    mPicture = extras.getParcelable("data");
                    int size     = mPicture.getRowBytes() * mPicture.getHeight();
                    ByteBuffer b = ByteBuffer.allocate(size);

                    mPicture.copyPixelsToBuffer(b);

                    mBuffer = new byte[size];

                    b.position(0);
                    try {
                        b.get(mBuffer);
                    } catch (BufferUnderflowException e) {
                        STLog.e("hg");
                    }
                break;
            }
        }
    }

    private Bitmap bitmapResize(Bitmap picture) {
        float scaleX = (float) mDefaultPhotoDisplayWSize / picture.getWidth();
        float scaleY = (float) mDefaultPhotoDisplayHSize / picture.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleX, scaleY);

        return Bitmap.createBitmap(picture, 0, 0, picture.getWidth(), picture.getHeight(), matrix, true);

    }

    public int compressImage(Bitmap picture) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.JPEG, (int) 100, baos); // bm

        mBuffer = baos.toByteArray();
        return mBuffer.length;
    }


    public void performCrop() {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(mPictureUri, "image/*");
            cropIntent.putExtra("crop", true);
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", mDefaultPhotoDisplayWSize);
            cropIntent.putExtra("outputY", mDefaultPhotoDisplayHSize);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, PICTURE_CROP);
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "Your device doesn't support the crop feature";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
        }
    }


}

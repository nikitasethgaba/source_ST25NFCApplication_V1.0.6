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

import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.util.UIHelper;
import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.ndef.NDEFMsg;


// Warning: CloningActivity doesn't extend STFragmentActivity because we don't want to get
//          STFragmentActivity.onResume() executed when a new tag is taped.
public class CloningActivity extends AppCompatActivity {

    final static String TAG = "CloningActivity";
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private NFCTag mTag;
    private Tag mAndroidTag;

    private final int SHORT_TOAST_DURATION_IN_MS = 500;
    private final int LONG_TOAST_DURATION_IN_MS = 1500;

    enum ActionStatus {
        ACTION_SUCCESSFUL,
        ACTION_FAILED,
        TAG_WRITE_PROTECTED,
        TAG_NOT_IN_THE_FIELD
    };

    // NDEF message to clone
    NDEFMsg mNdefMsg;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_layout);

        // Inflate content of FrameLayout
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_content);
        View childView = getLayoutInflater().inflate(R.layout.activity_cloning, null);
        frameLayout.addView(childView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.ndef_cloning);

        setNfcAdapter();

        mTag = MainActivity.getTag();

        Button finishButton = (Button) findViewById(R.id.finishButton);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToMainActivity();
            }
        });

        mNdefMsg = (NDEFMsg) getIntent().getSerializableExtra("NDEF");

    }

    public void setNfcAdapter() {
        //Log.v(TAG, "setNfcAdapter");

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Log.e(TAG, "Invalid NfcAdapter!");
            finish();
            return;
        }

        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        mAndroidTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (mAndroidTag != null) {
            // onResume() of CloneActivity has been called because a tag has been taped. Write the NDEF message to it.
            new TagDiscoveryAndWriteNdef().execute();
        }

        Log.v(TAG, "enableForegroundDispatch");
        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null /*nfcFiltersArray*/, null /*nfcTechLists*/);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "disableForegroundDispatch");
        mNfcAdapter.disableForegroundDispatch(this);

    }

    private void goBackToMainActivity() {
        // Create an intent to start the MainActivity
        Intent intent = new Intent(this, MainActivity.class);

        // Attach the NFC information to this intent
        intent.putExtra(NfcAdapter.EXTRA_TAG, mAndroidTag);

        // Set the flags to flush the activity stack history
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        goBackToMainActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_empty, menu);
        return true;
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * This AsyncTask will perform the discovery of the tag taped and write the NDEF message to it
     */
    private class TagDiscoveryAndWriteNdef extends AsyncTask<Void, Void, ActionStatus> {

        public TagDiscoveryAndWriteNdef() {
        }

        @Override
        protected ActionStatus doInBackground(Void... param) {
            ActionStatus result = ActionStatus.ACTION_FAILED;;

            UIHelper.TagInfo tagInfo = UIHelper.performTagDiscovery(mAndroidTag);
            NFCTag nfcTag = tagInfo.nfcTag;
            if(nfcTag != null) {
                try {
                    nfcTag.writeNdefMessage(mNdefMsg);
                    result = ActionStatus.ACTION_SUCCESSFUL;

                } catch (STException e) {
                    switch (e.getError()) {
                        case WRONG_SECURITY_STATUS:
                        case ISO15693_BLOCK_PROTECTED:
                        case ISO15693_BLOCK_IS_LOCKED:
                            result = ActionStatus.TAG_WRITE_PROTECTED;;
                            break;

                        default:
                            e.printStackTrace();
                            break;
                    }
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(ActionStatus actionStatus) {

            switch(actionStatus) {
                case ACTION_SUCCESSFUL:
                    showToast(R.string.cloning_successful, SHORT_TOAST_DURATION_IN_MS);
                    break;

                case TAG_WRITE_PROTECTED:
                    showToast(R.string.cloning_not_possible, LONG_TOAST_DURATION_IN_MS);
                    break;

                default:
                    showToast(R.string.cloning_failed, SHORT_TOAST_DURATION_IN_MS);
                    break;
            }
        }

        private void showToast(int resId, int durationInMs) {
            Resources resources = getResources();
            final Toast toast = Toast.makeText(CloningActivity.this, resources.getString(resId), Toast.LENGTH_SHORT);
            toast.show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, durationInMs);
        }
    }


}


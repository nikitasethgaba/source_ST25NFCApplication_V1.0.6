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
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.st.st25nfc.BuildConfig;
import com.st.st25nfc.R;
import com.st.st25nfc.generic.util.UIHelper;
import com.st.st25nfc.type4.GenericType4TagActivity;
import com.st.st25nfc.type4.st25ta.ST25TAActivity;
import com.st.st25nfc.type4.stm24sr.STM24SRActivity;
import com.st.st25nfc.type4.stm24tahighdensity.ST25TAHighDensityActivity;
import com.st.st25nfc.type5.GenericType5TagActivity;
import com.st.st25nfc.type5.st25dv.ST25DVActivity;
import com.st.st25nfc.type5.st25dv02kw.ST25DV02KWActivity;
import com.st.st25nfc.type5.st25tv.ST25TVActivity;
import com.st.st25nfc.type5.stlri.STLRiActivity;
import com.st.st25nfc.type5.stlri.STLRiS2kActivity;
import com.st.st25nfc.type5.stlri.STLRiS64kActivity;
import com.st.st25nfc.type5.stm24lr.STM24LR04Activity;
import com.st.st25nfc.type5.stm24lr.STM24LRActivity;
import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.TagCache;
import com.st.st25sdk.TagHelper;
import com.st.st25sdk.ndef.NDEFRecord;
import com.st.st25sdk.type5.ST25DVTag;

interface onTagDiscoveryCompleted {
    void onTagDiscoveryCompleted(Tag tag, TagHelper.ProductID productId);
}

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, onTagDiscoveryCompleted {

    private static final String TAG = "MainActivity";
    private static final boolean DBG = true;

    static private NFCTag mTag;

    public static final String NEW_TAG = "new_tag";

    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private TextView mNfcDisabledTextView;
    private Button mEnableNfcButton;


    public interface NfcIntentHook {
        void newNfcIntent(Intent intent);
    }

    private static NfcIntentHook mNfcIntentHook;

    public MainActivity() {
        if (BuildConfig.DEBUG) {
            enableDebugCode();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_layout);

        // Inflate content of FrameLayout
        FrameLayout frameLayout=(FrameLayout) findViewById(R.id.frame_content);
        View childView = getLayoutInflater().inflate(R.layout.activity_main, null);
        frameLayout.addView(childView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.inflateMenu(R.menu.menu_main_activity);
        navigationView.inflateMenu(R.menu.menu_help);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Log.e(TAG, "Invalid NfcAdapter!");
            finish();
            return;
        }

        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        mNfcDisabledTextView = (TextView) findViewById(R.id.nfcDisabledTextView);
        mEnableNfcButton = (Button) findViewById(R.id.enableNfcButton);

        mEnableNfcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    startActivity(intent);
                }
            }
        });

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
        int id = item.getItemId();

         switch (id) {
             case R.id.preferred_application:
                 Intent intent = new Intent(this, PreferredApplicationActivity.class);
                 startActivityForResult(intent, 1);
                 break;
             case R.id.about:
                super.onOptionsItemSelected(item);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    void processIntent(Intent intent) {
        if(intent == null) {
            return;
        }

        Log.d(TAG, "processIntent " + intent);

        if(mNfcIntentHook != null) {
            // NFC Intent hook used only for test purpose!
            mNfcIntentHook.newNfcIntent(intent);
            return;
        }

        Tag androidTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (androidTag != null) {
            // A tag has been taped

            // Perform tag discovery in an asynchronous task
            // onTagDiscoveryCompleted() will be called when the discovery is completed.
            new TagDiscovery(this).execute(androidTag);

            // This intent has been processed. Reset it to be sure that we don't process it again
            // if the MainActivity is resumed
            setIntent(null);
        }
    }

    static public void setNfcIntentHook(NfcIntentHook nfcIntentHook) {
        mNfcIntentHook = nfcIntentHook;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "disableForegroundDispatch");
        mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onResume() {
        Intent intent = getIntent();
        Log.d(TAG, "Resume mainActivity intent: " + intent);
        super.onResume();

        processIntent(intent);

        Log.v(TAG, "enableForegroundDispatch");
        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null /*nfcFiltersArray*/, null /*nfcTechLists*/);

        if (mNfcAdapter.isEnabled()) {
            // NFC enabled
            mNfcDisabledTextView.setVisibility(View.INVISIBLE);
            mEnableNfcButton.setVisibility(View.INVISIBLE);
        } else {
            // NFC disabled
            mNfcDisabledTextView.setVisibility(View.VISIBLE);
            mEnableNfcButton.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        Log.d(TAG, "onNewIntent " + intent);
        setIntent(intent);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        switch (id) {
            case R.id.preferred_application:
                intent = new Intent(this, PreferredApplicationActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.about:
                UIHelper.displayAboutDialogBox(this);
                break;
            case R.id.activity_menu:

                // Check if an intent has been associated to this menuItem
                intent = item.getIntent();

                if(intent != null) {
                    startActivityForResult(intent, 1);
                }
            break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    static public NFCTag getTag() {
        return mTag;
    }

    @Override
    public void onTagDiscoveryCompleted(Tag androidTag, TagHelper.ProductID productId) {
        //Toast.makeText(getApplication(), "onTagDiscoveryCompleted. productId:" + productId, Toast.LENGTH_LONG).show();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        Menu menu = navigationView.getMenu();

        MenuItem menuItem = menu.findItem(R.id.activity_menu);

        switch (productId) {
            case PRODUCT_ST_ST25DV64K_I:
            case PRODUCT_ST_ST25DV64K_J:
            case PRODUCT_ST_ST25DV16K_I:
            case PRODUCT_ST_ST25DV16K_J:
            case PRODUCT_ST_ST25DV04K_I:
            case PRODUCT_ST_ST25DV04K_J:
                checkMailboxActivation();
                startTagActivity(ST25DVActivity.class, R.string.st25dv_menus);
                break;

            case PRODUCT_ST_LRi512:
            case PRODUCT_ST_LRi1K:
            case PRODUCT_ST_LRi2K:
                startTagActivity(STLRiActivity.class, R.string.lri_menus);
                break;

            case PRODUCT_ST_LRiS2K:
                startTagActivity(STLRiS2kActivity.class, R.string.lriS2k_menus);
                break;

            case PRODUCT_ST_LRiS64K:
                startTagActivity(STLRiS64kActivity.class, R.string.lriS64k_menus);
                break;

            case PRODUCT_ST_M24SR02_Y:
            case PRODUCT_ST_M24SR04:
            case PRODUCT_ST_M24SR16_Y:
            case PRODUCT_ST_M24SR64_Y:
                startTagActivity(STM24SRActivity.class, R.string.m24sr64_menus);
                break;

            case PRODUCT_ST_ST25TA16K:
            case PRODUCT_ST_ST25TA64K:
                startTagActivity(ST25TAHighDensityActivity.class, R.string.m24sr64_menus);
                break;

            case PRODUCT_ST_ST25TV64K:
                // TODO: Implements ST25TV64K menus and activity
                startTagActivity(GenericType5TagActivity.class, R.string.type5_menus);
                break;

            case PRODUCT_ST_ST25TV02K:
            case PRODUCT_ST_ST25TV512:
                startTagActivity(ST25TVActivity.class, R.string.st25tv_menus);
                break;
            case PRODUCT_ST_ST25DV02K_W:
                startTagActivity(ST25DV02KWActivity.class, R.string.st25dv02kw_menus);
                break;
            case PRODUCT_ST_M24LR16E_R:
            case PRODUCT_ST_M24LR32E_R:
            case PRODUCT_ST_M24LR64E_R:
            case PRODUCT_ST_M24LR64_R:
            case PRODUCT_ST_M24LR128E_R:
            case PRODUCT_ST_M24LR256E_R:
                startTagActivity(STM24LRActivity.class, R.string.m24lr64_menus);
                break;
            case PRODUCT_ST_M24LR01E_R:
            case PRODUCT_ST_M24LR02E_R:
            case PRODUCT_ST_M24LR04E_R:
            case PRODUCT_ST_M24LR08E_R:
                startTagActivity(STM24LR04Activity.class, R.string.m24lr04_menus);
                break;
            case PRODUCT_ST_ST25TA02K:
            case PRODUCT_ST_ST25TA02K_G:
            case PRODUCT_ST_ST25TA02K_P:
            case PRODUCT_ST_ST25TA02K_D:
            case PRODUCT_ST_ST25TA512:
            case PRODUCT_ST_ST25TA512_G:
                startTagActivity(ST25TAActivity.class, R.string.st25ta_menus);
                break;

            case PRODUCT_GENERIC_TYPE5:
                startTagActivity(GenericType5TagActivity.class, R.string.type5_menus);
                break;

            case PRODUCT_GENERIC_TYPE5_AND_ISO15693:
                startTagActivity(GenericType5TagActivity.class, R.string.type5_menus);
                break;

            case PRODUCT_GENERIC_TYPE4:
                startTagActivity(GenericType4TagActivity.class, R.string.type4_menus);
                break;

            default:
                menuItem.setTitle(R.string.product_unknown);
                Toast.makeText(getApplication(), getResources().getString(R.string.unknown_tag), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Product not recognized");
                break;
        }
    }

    private void checkMailboxActivation() {
        new Thread(new Runnable() {
            public void run() {
                ST25DVTag st25DVTag = (ST25DVTag) mTag;

                try {
                    if(st25DVTag.isMBEnabled(true)) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, "Warning! ST25DV's mailbox is currently enabled so EEPROM writting is not possible.\n\nGo to 'Mailbox management' if you want to disable it.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (STException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startTagActivity(Class<?> cls, int menuTitle) {

        // We are about to start the activity related to a tag so mTag should be non null
        if(getTag() == null) {
            Log.e(TAG, "Error! Trying to start a TagActivity with a null tag!");
            return;
        }

        Log.v(TAG, "startTagActivity: " + cls.getName());

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.activity_menu);
        menuItem.setTitle(menuTitle);
        menuItem.setVisible(true);

        Intent st_intent = new Intent(this, cls);
        st_intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Flag indicating that we are displaying the information of a new tag
        st_intent.putExtra(NEW_TAG, true);

        // Save in the menuItem the intent that should be called when this menuItem is clicked
        // It allows to open the same activity with low efforts
        menuItem.setIntent(st_intent);

        startActivityForResult(st_intent, 1);
    }

    private class TagDiscovery extends AsyncTask<Tag, Void, Void> {
        private onTagDiscoveryCompleted listener;
        private TagHelper.ProductID mProductID;
        private Tag mAndroidTag;

        public TagDiscovery(onTagDiscoveryCompleted listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Tag... param) {
            mAndroidTag = param[0];

            UIHelper.TagInfo tagInfo = UIHelper.performTagDiscovery(mAndroidTag);

            mTag = tagInfo.nfcTag;
            mProductID = tagInfo.productID;

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Code executed on UI Thread
            listener.onTagDiscoveryCompleted(mAndroidTag, mProductID);
        }
    }

    private void enableDebugCode() {

        try {
            // Put here the debug features that you want to enable
            TagCache.class.getField("DBG_CACHE_MANAGER").set(null, true);

            NDEFRecord.class.getField("DBG_NDEF_RECORD").set(null, true);



        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

}

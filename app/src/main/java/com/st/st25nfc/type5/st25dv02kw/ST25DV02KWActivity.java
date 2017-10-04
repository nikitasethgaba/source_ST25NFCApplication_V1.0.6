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

package com.st.st25nfc.type5.st25dv02kw;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.MainActivity;
import com.st.st25nfc.generic.STFragment;
import com.st.st25nfc.generic.STFragmentActivity;
import com.st.st25nfc.generic.STPagerAdapter;
import com.st.st25nfc.generic.SlidingTabLayout;
import com.st.st25nfc.generic.util.UIHelper.STFragmentId;
import com.st.st25sdk.type5.ST25DV02KWTag;

import java.util.ArrayList;
import java.util.List;

public class ST25DV02KWActivity extends STFragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener, STFragment.STFragmentListener {

    // Set here the Toolbar to use for this activity
    private int toolbar_res = R.menu.toolbar_empty;

    final static String TAG = "ST25DV02KWActivity";
    public ST25DV02KWTag mST25DV02KWTag;

    STPagerAdapter mPagerAdapter;
    ViewPager mViewPager;

    private SlidingTabLayout mSlidingTabLayout;

    ListView lv;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager_layout);

        mST25DV02KWTag = (ST25DV02KWTag) super.getTag();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(mST25DV02KWTag.getName());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        mMenu.inflateMenu(navigationView);

        List<STFragmentId> fragmentList = new ArrayList<STFragmentId>();

        fragmentList.add(STFragmentId.TAG_INFO_FRAGMENT_ID);
        fragmentList.add(STFragmentId.NDEF_DETAILS_FRAGMENT_ID);
        fragmentList.add(STFragmentId.CC_FILE_TYPE5_FRAGMENT_ID);
        fragmentList.add(STFragmentId.SYS_FILE_TYP5_FRAGMENT_ID);
        fragmentList.add(STFragmentId.RAW_DATA_FRAGMENT_ID);

        mPagerAdapter = new STPagerAdapter(getSupportFragmentManager(), getApplicationContext(), fragmentList);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        // Check if the activity was started with a request to select a specific tab
        Intent mIntent = getIntent();
        int tabNbr = mIntent.getIntExtra("select_tab", -1);
        if (tabNbr != -1) {
            mViewPager.setCurrentItem(tabNbr);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            boolean isANewTag = bundle.getBoolean(MainActivity.NEW_TAG);
            if (isANewTag) {
                // We are entering to this activity to display a new tag.
                // Check if EAS alarm is set for this tag
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

    void processIntent(Intent intent) {
        Log.d(TAG, "Process Intent");
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        return mMenu.selectItem(this, item);
    }

    public ST25DV02KWTag getTag() {
        return mST25DV02KWTag;
    }

}


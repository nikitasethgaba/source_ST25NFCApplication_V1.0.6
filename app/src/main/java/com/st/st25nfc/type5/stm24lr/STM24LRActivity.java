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

package com.st.st25nfc.type5.stm24lr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.ST25Menu;
import com.st.st25sdk.type5.STType5Tag;
import com.st.st25sdk.type5.STVicinityTag;
import com.st.st25nfc.generic.STFragmentActivity;
import com.st.st25nfc.generic.SlidingTabLayout;
import com.st.st25nfc.generic.STFragment;
import com.st.st25nfc.generic.STPagerAdapter;
import com.st.st25nfc.generic.util.UIHelper;
import com.st.st25nfc.generic.util.UIHelper.STFragmentId;

import java.util.ArrayList;
import java.util.List;

public class STM24LRActivity extends STFragmentActivity implements NavigationView.OnNavigationItemSelectedListener,
        STFragment.STFragmentListener {

    // Set here the Toolbar to use for this activity
    private int toolbar_res = R.menu.toolbar_empty;

    final static String TAG = "STM24LRActivity";
    public STType5Tag mSTType5Tag;;

    STPagerAdapter mPagerAdapter;
    ViewPager mViewPager;

    private SlidingTabLayout mSlidingTabLayout;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager_layout);

        mSTType5Tag = (STType5Tag) super.getTag();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(mSTType5Tag.getName());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mMenu = ST25Menu.newInstance(super.getTag());
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        mMenu.inflateMenu(navigationView);

        List<STFragmentId> fragmentList = new ArrayList<STFragmentId>();

        fragmentList.add(STFragmentId.TAG_INFO_FRAGMENT_ID);
        fragmentList.add(STFragmentId.NDEF_DETAILS_FRAGMENT_ID);
        fragmentList.add(STFragmentId.CC_FILE_TYPE5_FRAGMENT_ID);
        fragmentList.add(STFragmentId.SYS_FILE_TYP5_FRAGMENT_ID);
        fragmentList.add(UIHelper.STFragmentId.RAW_DATA_FRAGMENT_ID);


        mPagerAdapter = new STPagerAdapter(getSupportFragmentManager(), getApplicationContext(), fragmentList);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        // Check if the activity was started with a request to select a specific tab
        Intent mIntent = getIntent();
        int tabNbr = mIntent.getIntExtra("select_tab", -1);
        if(tabNbr != -1) {
            mViewPager.setCurrentItem(tabNbr);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds read_list_items to the action bar if it is present.
        getMenuInflater().inflate(toolbar_res, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        return mMenu.selectItem(this, item);
    }


}


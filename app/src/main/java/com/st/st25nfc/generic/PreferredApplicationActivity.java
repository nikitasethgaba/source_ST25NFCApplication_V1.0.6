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

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.util.UIHelper;
import com.st.st25sdk.NFCTag;

import java.util.ArrayList;
import java.util.List;


public class PreferredApplicationActivity extends STFragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener, STFragment.STFragmentListener {

    class PreferredApplication {
        IntentFilter filter;
        ComponentName preferredApplication;
    }

    private TableLayout mHeaderTableLayout;
    private TableLayout mPreferredAppTableLayout;
    private RadioButton mShowOnlyNfcRadioButton;
    private RadioButton mShowAllRadioButton;

    protected List<PreferredApplication> mPreferredApplicationList;

    // Set here the Toolbar to use for this activity
    private int toolbar_res = R.menu.toolbar_empty;

    final static String TAG = "PrefApp";

    private final int ACTION_COLUMN = 0;
    private final int PREFERRED_APP_COLUMN = 1;
    private final int EDIT_COLUMN = 2;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Inflate content of FrameLayout
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_content);
        View childView = getLayoutInflater().inflate(R.layout.fragment_preferred_activities, null);
        frameLayout.addView(childView);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        NFCTag tag = super.getTag();
        if(tag != null) {
            mMenu = ST25Menu.newInstance(super.getTag());
            mMenu.inflateMenu(navigationView);
        } else {
            navigationView.inflateMenu(R.menu.menu_main_activity);
            navigationView.inflateMenu(R.menu.menu_help);
        }

        mPreferredApplicationList = new ArrayList<PreferredApplication>();

        mShowOnlyNfcRadioButton = (RadioButton) findViewById(R.id.showOnlyNfcRadioButton);
        mShowAllRadioButton = (RadioButton) findViewById(R.id.showAllRadioButton);
        mPreferredAppTableLayout = (TableLayout) findViewById(R.id.preferredAppTableLayout);
        mHeaderTableLayout = (TableLayout) findViewById(R.id.headerTableLayout);

        RadioGroup filterRadioGroup = (RadioGroup) findViewById(R.id.filterRadioGroup);
        filterRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Refresh the Table
                getPreferredApplications();
            }
        });
    }

    private void getPreferredApplications() {
        final PackageManager pm = getPackageManager();
        ArrayList<IntentFilter> filters = new ArrayList<IntentFilter>();
        ArrayList<ComponentName> preferredActivities = new ArrayList<ComponentName>();
        pm.getPreferredActivities(filters, preferredActivities, null);

        mPreferredApplicationList.clear();

        for (int i=0; i<preferredActivities.size(); i++) {
            String action = filters.get(i).getAction(0);
            Log.v(TAG, "Action: " + action);
            Log.v(TAG, "Preferred Activity: " + preferredActivities.get(i).getPackageName());

            PreferredApplication preferredApplication = new PreferredApplication();
            preferredApplication.filter = filters.get(i);
            preferredApplication.preferredApplication = preferredActivities.get(i);

            if (mShowOnlyNfcRadioButton.isChecked()) {
                if(action.toLowerCase().contains("android.nfc")) {
                    mPreferredApplicationList.add(preferredApplication);
                }
            } else {
                mPreferredApplicationList.add(preferredApplication);
            }
        }

        displayPreferredAppList();
    }

    private void displayPreferredAppList() {

        // Clear tables
        mHeaderTableLayout.removeAllViewsInLayout();
        mPreferredAppTableLayout.removeAllViewsInLayout();

        addTableHeader();

        int nbrOfRows = mPreferredApplicationList.size();
        int row = 0;
        for(PreferredApplication preferredApplication : mPreferredApplicationList) {
            addRow(preferredApplication, row++, nbrOfRows);
        }
    }

    private void addTableHeader() {
        TableRow.LayoutParams viewLayoutParams;
        int border_size = 4;
        int left_border_size = border_size;
        int top_border_size = border_size;
        int right_border_size = 0;
        int bottom_border_size = 0;
        int textViewColor = getResources().getColor(R.color.st_light_blue);

        // create tableRow
        TableRow tableRow = new TableRow(this);
        tableRow.setBackgroundColor(getResources().getColor(R.color.white));

        // Add TextView containing Action
        TextView actionTextView = new TextView(this);
        actionTextView.setBackgroundColor(textViewColor);
        actionTextView.setGravity(Gravity.CENTER);
        actionTextView.setText(getResources().getString(R.string.action));
        actionTextView.setLines(2);
        actionTextView.setMaxLines(2);
        // Params for this View item
        // Note that here you must use TableRow.LayoutParams since TableRow is the parent of this View item
        viewLayoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f);
        viewLayoutParams.setMargins(left_border_size, top_border_size, right_border_size, bottom_border_size);
        tableRow.addView(actionTextView, viewLayoutParams);

        // Add TextView containing the associated App
        TextView preferredAppTextView = new TextView(this);
        preferredAppTextView.setBackgroundColor(textViewColor);
        preferredAppTextView.setGravity(Gravity.CENTER);
        preferredAppTextView.setText(getResources().getString(R.string.preferred_app));
        preferredAppTextView.setLines(2);
        preferredAppTextView.setMaxLines(2);
        viewLayoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f);
        viewLayoutParams.setMargins(left_border_size, top_border_size, right_border_size, bottom_border_size);
        tableRow.addView(preferredAppTextView, viewLayoutParams);

        // Add TextView containing Edit
        TextView editTextView = new TextView(this);
        editTextView.setBackgroundColor(textViewColor);
        editTextView.setGravity(Gravity.CENTER);
        editTextView.setText(getResources().getString(R.string.edit));
        editTextView.setLines(2);
        editTextView.setMaxLines(2);
        // This is the last column so we should add a right border
        right_border_size = border_size;
        viewLayoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        viewLayoutParams.setMargins(left_border_size, top_border_size, right_border_size, bottom_border_size);
        tableRow.addView(editTextView, viewLayoutParams);

        // add tableRow to tableLayout
        // Note that you must use TableLayout.LayoutParams, since the parent of this TableRow is a TableLayout
        TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f);
        mHeaderTableLayout.addView(tableRow, tableRowParams);
    }



    private void addRow(PreferredApplication preferredApplication, int row, int nbrOfRows) {
        TableRow.LayoutParams viewLayoutParams;
        int border_size = 4;
        int left_border_size = border_size;
        int top_border_size = border_size;
        int right_border_size = 0;
        int bottom_border_size;

        if(row == (nbrOfRows-1)) {
            // This is the last row. We should add a bottom line
            bottom_border_size = border_size;
        } else {
            bottom_border_size = 0;
        }

        int textViewColor;
        if((row %2) == 0) {
            textViewColor = getResources().getColor(R.color.st_very_light_blue);
        } else {
            textViewColor = getResources().getColor(R.color.white);
        }

        // create tableRow
        TableRow tableRow = new TableRow(this);
        tableRow.setBackgroundColor(getResources().getColor(R.color.white));

        // Add TextView containing Action
        TextView actionTextView = new TextView(this);
        actionTextView.setBackgroundColor(textViewColor);
        actionTextView.setGravity(Gravity.LEFT);
        actionTextView.setText(preferredApplication.filter.getAction(0));
        actionTextView.setLines(2);
        actionTextView.setMaxLines(2);
        viewLayoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f);
        viewLayoutParams.setMargins(left_border_size, top_border_size, right_border_size, bottom_border_size);
        tableRow.addView(actionTextView, viewLayoutParams);

        // Add TextView containing the associated App
        TextView preferredAppTextView = new TextView(this);
        preferredAppTextView.setBackgroundColor(textViewColor);
        preferredAppTextView.setGravity(Gravity.LEFT);
        preferredAppTextView.setText(preferredApplication.preferredApplication.getPackageName());
        preferredAppTextView.setLines(2);
        preferredAppTextView.setMaxLines(2);
        viewLayoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f);
        viewLayoutParams.setMargins(left_border_size, top_border_size, right_border_size, bottom_border_size);
        tableRow.addView(preferredAppTextView, viewLayoutParams);

        // Add an icon to edit this row
        ImageButton imageButtonView = new ImageButton(this);
        imageButtonView.setBackgroundColor(textViewColor);
        imageButtonView.setImageResource(R.drawable.ic_settings_black_24dp);
        imageButtonView.setId(row);
        // This is the last column so we should add a right border
        right_border_size = border_size;
        viewLayoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);
        viewLayoutParams.setMargins(left_border_size, top_border_size, right_border_size, bottom_border_size);
        tableRow.addView(imageButtonView, viewLayoutParams);

        imageButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int row = v.getId();
                PreferredApplication preferredApplication = mPreferredApplicationList.get(row);
                String preferredAppPackageName = preferredApplication.preferredApplication.getPackageName();
                Log.v(TAG, "Click on package " + preferredAppPackageName);

                // An application can no more call "clearPackagePreferredActivities" to change the preferredApplications
                // registered by OTHER applications but we can display a system setting page where the user
                // can clear the "open by default" assignment for this application.

                //final PackageManager packageManager = getPackageManager();
                //packageManager.clearPackagePreferredActivities(preferredAppPackageName);

                displayApplicationSettings(preferredAppPackageName);
            }
        });

        // add tableRow to tableLayout
        // Note that you must use TableLayout.LayoutParams, since the parent of this TableRow is a TableLayout
        TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f);
        mPreferredAppTableLayout.addView(tableRow, tableRowParams);
    }

    private void displayApplicationSettings(final String packageName) {
        String message = "The system settings for app \'" +  packageName + "\' are going to be opened.\n" +
                         "In 'Open by default' entry, you can cancel the assignations for this application.\n\n" +
                         "Use back key to come back to current application";

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Confirmation needed");

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Open settings",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();

                        // Display the system settings concerning this package/application
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", packageName, null);
                        intent.setData(uri);
                        startActivity(intent);
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

    @Override
    public void onResume() {
        super.onResume();

        getPreferredApplications();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long

        // as you specify a parent activity in AndroidManifest.xml.


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        boolean res = false;

        if(mMenu != null) {
            res = mMenu.selectItem(this, item);
        } else {
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
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
            drawer.closeDrawer(GravityCompat.START);
            res = true;
        }

        return res;
    }

}


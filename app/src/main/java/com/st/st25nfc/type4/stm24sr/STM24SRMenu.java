package com.st.st25nfc.type4.stm24sr;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.AreasEditorActivity;
import com.st.st25nfc.generic.PreferredApplicationActivity;
import com.st.st25nfc.generic.ST25Menu;
import com.st.st25nfc.generic.ndef.NDEFEditorActivity;
import com.st.st25nfc.generic.util.UIHelper;
import com.st.st25nfc.type4.AreasListActivity;
import com.st.st25nfc.type4.AreasPwdActivity;
import com.st.st25nfc.type4.GpoConfigActivity;
import com.st.st25sdk.NFCTag;

public class STM24SRMenu extends ST25Menu {

    public STM24SRMenu(NFCTag tag) {
        super(tag);
        mMenuResource = new int[3];
        mMenuResource[0] = R.menu.menu_home;
        mMenuResource[1] = R.menu.menu_nfc_forum;
        mMenuResource[2] = R.menu.menu_m24sr;
    }

    @Override
    public boolean selectItem(Activity activity, MenuItem item) {
        // Handle navigation view item clicks here.

        Intent intent;
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.preferred_application:
                intent = new Intent(activity, PreferredApplicationActivity.class);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.about:
                UIHelper.displayAboutDialogBox(activity);
                break;
            case R.id.product_name:
            // Nfc forum
            case R.id.tag_info:
                //Set tab 0 of ST25DVActivity
                intent = new Intent(activity, STM24SRActivity.class);
                intent.putExtra("select_tab", 0);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.nfc_ndef_editor:
                intent = new Intent(activity, NDEFEditorActivity.class);
                intent.putExtra("area_nbr", 1);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.cc_file:
                intent = new Intent(activity, STM24SRActivity.class);
                intent.putExtra("select_tab", 2);
                activity.startActivityForResult(intent, 1);
                break;

            // Product features
            case R.id.sys_file:
                intent = new Intent(activity, STM24SRActivity.class);
                intent.putExtra("select_tab", 3);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.memory_dump:
                intent = new Intent(activity, STM24SRActivity.class);
                intent.putExtra("select_tab", 4);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.area_management:
                intent = new Intent(activity, AreasListActivity.class);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.areas_ndef_editor:
                intent = new Intent(activity, AreasEditorActivity.class);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.area_security_status_management:
                intent = new Intent(activity, AreasPwdActivity.class);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.gpo_status_management:
                intent = new Intent(activity, GpoConfigActivity.class);
                activity.startActivityForResult(intent, 1);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

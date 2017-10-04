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

package com.st.st25nfc.type5.st25tv;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.st.st25nfc.generic.MemoryTestActivity;
import com.st.st25nfc.generic.PreferredApplicationActivity;
import com.st.st25nfc.generic.ndef.NDEFEditorActivity;
import com.st.st25nfc.generic.util.UIHelper;
import com.st.st25nfc.type5.Type5ConfigurationProtectionActivity;
import com.st.st25sdk.NFCTag;
import com.st.st25nfc.R;
import com.st.st25nfc.generic.AreasEditorActivity;
import com.st.st25nfc.generic.RegistersActivity;
import com.st.st25nfc.generic.ST25Menu;
import com.st.st25nfc.type5.Type5LockBlockActivity;


public class ST25TVMenu extends ST25Menu {
    public ST25TVMenu(NFCTag tag) {
        super(tag);
        mMenuResource = new int[3];
        mMenuResource[0] = R.menu.menu_home;
        mMenuResource[1] = R.menu.menu_nfc_forum;
        mMenuResource[2] = R.menu.menu_st25tv;
    }

    public boolean selectItem(Activity activity, MenuItem item) {
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
                intent = new Intent(activity,  ST25TVActivity.class);
                intent.putExtra("select_tab", 0);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.nfc_ndef_editor:
                intent = new Intent(activity, NDEFEditorActivity.class);
                intent.putExtra("area_nbr", 1);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.cc_file:
                intent = new Intent(activity, ST25TVActivity.class);
                intent.putExtra("select_tab", 2);
                activity.startActivityForResult(intent, 1);
                break;
            // Product features
            case R.id.sys_file:
                intent = new Intent(activity, ST25TVActivity.class);
                intent.putExtra("select_tab", 3);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.memory_dump:
                intent = new Intent(activity, MemoryTestActivity.class);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.configuration:
                intent = new Intent(activity, ST25TVChangeMemConfActivity.class);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.register_management:
                intent = new Intent(activity, RegistersActivity.class);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.configuration_protection:
                intent = new Intent(activity, Type5ConfigurationProtectionActivity.class);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.areas_ndef_editor:
                intent = new Intent(activity,  AreasEditorActivity.class);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.area_security_status_management:
                intent = new Intent(activity, ST25TVAreaSecurityStatusActivity.class);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.counter_menu:
                intent = new Intent(activity,  ST25TVWriteCounterActivity.class);
                activity.startActivityForResult(intent, 1);
                break;

            case R.id.tamper_detect_menu:
                intent = new Intent(activity,  ST25TVTamperDetectActivity.class);
                activity.startActivityForResult(intent, 1);
                break;

            case R.id.confidential_mode_menu:
                intent = new Intent(activity,  ST25TVConfidentialModeActivity.class);
                activity.startActivityForResult(intent, 1);
                break;

            case R.id.kill_menu:
                intent = new Intent(activity,  ST25TVKillTagActivity.class);
                activity.startActivityForResult(intent, 1);
                break;

            case R.id.eas_menu:
                intent = new Intent(activity,  ST25TVEasActivity.class);
                activity.startActivityForResult(intent, 1);
                break;

            case R.id.lock_block_menu:
                intent = new Intent(activity,  Type5LockBlockActivity.class);
                activity.startActivityForResult(intent, 1);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

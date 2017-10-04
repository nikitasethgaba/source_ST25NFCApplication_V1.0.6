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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

public class ST25DVMenu  extends ST25Menu {
    public ST25DVMenu(NFCTag tag) {
        super(tag);
        mMenuResource = new int[3];
        mMenuResource[0] = R.menu.menu_home;
        mMenuResource[1] = R.menu.menu_nfc_forum;
        mMenuResource[2] = R.menu.menu_st25dv;
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
                intent = new Intent(activity, ST25DVActivity.class);
                intent.putExtra("select_tab", 0);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.cc_file:
                intent = new Intent(activity, ST25DVActivity.class);
                intent.putExtra("select_tab", 2);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.nfc_ndef_editor:
                intent = new Intent(activity, NDEFEditorActivity.class);
                intent.putExtra("area_nbr", 1);
                activity.startActivityForResult(intent, 1);
                break;

            // Product features
            case R.id.sys_file:
                intent = new Intent(activity, ST25DVActivity.class);
                intent.putExtra("select_tab", 3);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.memory_dump:
                intent = new Intent(activity, MemoryTestActivity.class);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.configuration:
                intent = new Intent(activity, ST25DVChangeMemConf.class);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.register_management:
                intent = new Intent(activity, RegistersActivity.class);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.dynamic_register_management:
                intent = new Intent(activity, RegistersActivity.class);
                Bundle b = new Bundle();
                b.putBoolean(RegistersActivity.USE_DYNAMIC_REGISTER, true); //true for dynamic register management
                intent.putExtras(b); //Put your parameters to your next Intent
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.configuration_protection:
                intent = new Intent(activity, Type5ConfigurationProtectionActivity.class);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.areas_ndef_editor:
                intent = new Intent(activity, AreasEditorActivity.class);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.area_security_status_management:
                intent = new Intent(activity, ST25DVAreaSecurityStatusActivity.class);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.mailbox_management:
                intent = new Intent(activity, ST25DVMailboxActivity.class);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.data_transfer:
                intent = new Intent(activity, ST25DVMailboxDataTransferActivity.class);
                activity.startActivityForResult(intent, 1);
                break;

            // Demos features
            case R.id.stopwatch_transfers:
                intent = new Intent(activity, ST25DVStopwatchDemoActivity.class);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.firmware_update:
                intent = new Intent(activity, ST25DVFirmwareUpdateDemoActivity.class);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.picture_transfers:
                intent = new Intent(activity, ST25DVPictureTransferDemoActivity.class);
                activity.startActivityForResult(intent, 1);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

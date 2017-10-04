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

package com.st.st25nfc.type4;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.st.st25nfc.R;
import com.st.st25nfc.generic.PreferredApplicationActivity;
import com.st.st25nfc.generic.ST25Menu;
import com.st.st25nfc.generic.ndef.NDEFEditorActivity;
import com.st.st25nfc.generic.util.UIHelper;
import com.st.st25sdk.NFCTag;

public class GenericType4Menu extends ST25Menu {
    public GenericType4Menu(NFCTag tag) {
        super(tag);
        mMenuResource = new int[2];
        mMenuResource[0] = R.menu.menu_home;
        mMenuResource[1] = R.menu.menu_nfc_forum;
    }

    @Override
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
                intent = new Intent(activity, GenericType4TagActivity.class);
                intent.putExtra("select_tab", 0);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.nfc_ndef_editor:
                intent = new Intent(activity, NDEFEditorActivity.class);
                intent.putExtra("area_nbr", 1);
                activity.startActivityForResult(intent, 1);
                break;
            case R.id.cc_file:
                intent = new Intent(activity, GenericType4TagActivity.class);
                intent.putExtra("select_tab", 2);
                activity.startActivityForResult(intent, 1);
                break;
            // Product features
            case R.id.memory_dump:
                intent = new Intent(activity, GenericType4TagActivity.class);
                intent.putExtra("select_tab", 4);
                activity.startActivityForResult(intent, 1);
                break;
            default:
                break;

        }

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

}

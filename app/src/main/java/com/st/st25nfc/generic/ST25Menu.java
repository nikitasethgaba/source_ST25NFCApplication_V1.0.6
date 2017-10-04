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

import android.app.Activity;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.st.st25nfc.R;
import com.st.st25nfc.type4.GenericType4Menu;
import com.st.st25nfc.type4.st25ta.ST25TAMenu;
import com.st.st25nfc.type4.stm24sr.STM24SRMenu;
import com.st.st25nfc.type4.stm24tahighdensity.STM24TAHighDensityMenu;
import com.st.st25nfc.type5.GenericType5Menu;
import com.st.st25nfc.type5.st25dv.ST25DVMenu;
import com.st.st25nfc.type5.st25dv02kw.ST25DV02KWMenu;
import com.st.st25nfc.type5.st25tv.ST25TVMenu;
import com.st.st25nfc.type5.stlri.STLRiMenu;
import com.st.st25nfc.type5.stlri.STLRiS2kMenu;
import com.st.st25nfc.type5.stlri.STLRiS64kMenu;
import com.st.st25nfc.type5.stm24lr.STLRMenu;
import com.st.st25sdk.NFCTag;
import com.st.st25sdk.type4a.Type4Tag;
import com.st.st25sdk.type5.Type5Tag;


public class ST25Menu {

    private static ST25Menu mMenu;
    public int[] mMenuResource;
    private static final String DBG_LOG = "ST25Menu";
    private static String mTagName;

    public static ST25Menu newInstance(NFCTag tag) {
        String tagName;
        if (tag == null) {
            mMenu = null;
            Log.e(DBG_LOG, "Menu not defined for this tag! - tag is null");
            return mMenu;
        }

        tagName = tag.getName();
        setProductName(tagName);
        if ( (tagName.contains("LRi2K")) || (tagName.contains("LRi1K")) || (tagName.contains("LRi512")) ) {
            mMenu = new STLRiMenu(tag);
        } else if (tagName.contains("LRiS2K")) {
            mMenu = new STLRiS2kMenu(tag);
        } else if (tagName.contains("LRiS64K")) {
            mMenu = new STLRiS64kMenu(tag);
        } else if (tagName.contains("M24SR")) {
            mMenu = new STM24SRMenu(tag);
        } else if (tagName.contains("ST25DV02K-W")) {
            mMenu = new ST25DV02KWMenu(tag);
        } else if (tagName.contains("ST25DV")) {
            mMenu = new ST25DVMenu(tag);
        } else if ((tagName.contains("ST25TV512")) || (tagName.contains("ST25TV02K"))) {
            mMenu = new ST25TVMenu(tag);
        } else if (tagName.contains("M24LR")) {
            mMenu = new STLRMenu(tag);
        } else if ((tagName.contains("ST25TA16")) || (tagName.contains("ST25TA64"))) {
            mMenu = new STM24TAHighDensityMenu(tag);
        } else if (tagName.contains("ST25TA")) {
            // Low density ST25TA
            mMenu = new ST25TAMenu(tag);

        } else if (tag instanceof Type5Tag) {
            mMenu = new GenericType5Menu(tag);
        } else if (tag instanceof Type4Tag) {
                mMenu = new GenericType4Menu(tag);
        }
        // TODO: Add generic menu for Type2 tags

        else {
            mMenu = null;
            Log.e(DBG_LOG, "Menu not defined for this tag!");
        }
        return mMenu;
    }

    private static void setProductName(String name) {
        mTagName = name;
    }
    private  String getProductName() {
        return mTagName;
    }
    public static ST25Menu getInstance(NFCTag tag) {
        if (mMenu == null) {
            mMenu = new ST25Menu(tag);
        }
        return mMenu;
    }

    public ST25Menu(NFCTag tag) {
        mMenuResource = new int[1];
        mMenuResource[0] = R.menu.menu_empty;
    }

    public boolean selectItem(Activity activity, MenuItem item) {
        return false;
    }

    public void inflateMenu(NavigationView view) {
        if (mMenuResource != null) {
            for (int i = 0; i < mMenuResource.length; i++) {
                view.inflateMenu(mMenuResource[i]);
            }
        }
        view.inflateMenu(R.menu.menu_help);

        // update the Home name
        // get menu from navigationView
        Menu menu = view.getMenu();
        // find MenuItem you want to change
        MenuItem nav_item = menu.findItem(R.id.product_name);
        if (nav_item != null) nav_item.setTitle(getProductName());

    }
}

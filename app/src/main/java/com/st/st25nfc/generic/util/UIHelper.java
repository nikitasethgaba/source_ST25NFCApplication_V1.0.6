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

package com.st.st25nfc.generic.util;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.st.st25android.AndroidReaderInterface;
import com.st.st25nfc.R;
import com.st.st25nfc.generic.RawReadWriteFragment;
import com.st.st25nfc.generic.STFragment;
import com.st.st25nfc.generic.TagInfoFragment;
import com.st.st25nfc.generic.ndef.NDEFEditorFragment;
import com.st.st25nfc.type4.CCFileType4Fragment;
import com.st.st25nfc.type4.st25ta.SysFileST25TAFragment;
import com.st.st25nfc.type4.stm24sr.SysFileM24SRFragment;
import com.st.st25nfc.type4.stm24tahighdensity.SysFileST25TAHighDensityFragment;
import com.st.st25nfc.type5.CCFileType5Fragment;
import com.st.st25nfc.type5.SysFileType5Fragment;
import com.st.st25sdk.Helper;
import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.TagHelper;
import com.st.st25sdk.type4a.m24srtahighdensity.M24SR02KTag;
import com.st.st25sdk.type4a.m24srtahighdensity.M24SR04KTag;
import com.st.st25sdk.type4a.m24srtahighdensity.M24SR16KTag;
import com.st.st25sdk.type4a.m24srtahighdensity.M24SR64KTag;
import com.st.st25sdk.type4a.st25ta.ST25TA02KDTag;
import com.st.st25sdk.type4a.st25ta.ST25TA02KPTag;
import com.st.st25sdk.type4a.st25ta.ST25TA02KTag;
import com.st.st25sdk.type4a.m24srtahighdensity.ST25TA16KTag;
import com.st.st25sdk.type4a.st25ta.ST25TA512Tag;
import com.st.st25sdk.type4a.m24srtahighdensity.ST25TA64KTag;
import com.st.st25sdk.type4a.Type4Tag;
import com.st.st25sdk.type5.LRi1KTag;
import com.st.st25sdk.type5.LRi2KTag;
import com.st.st25sdk.type5.LRi512Tag;
import com.st.st25sdk.type5.LRiS2KTag;
import com.st.st25sdk.type5.LRiS64KTag;
import com.st.st25sdk.type5.M24LR04KTag;
import com.st.st25sdk.type5.M24LR16KTag;
import com.st.st25sdk.type5.M24LR64KTag;
import com.st.st25sdk.type5.ST25DV02KWTag;
import com.st.st25sdk.type5.ST25DVTag;
import com.st.st25sdk.type5.ST25TV64KTag;
import com.st.st25sdk.type5.ST25TVTag;
import com.st.st25sdk.type5.STType5Tag;
import com.st.st25sdk.type5.Type5Tag;

import static com.st.st25sdk.TagHelper.ProductID.PRODUCT_UNKNOWN;
import static com.st.st25sdk.TagHelper.identifyType4Product;
import static com.st.st25sdk.TagHelper.identifyTypeVProduct;

public class UIHelper {

    static final String TAG = "UIHelper";

    /**
     * Function indicating if the current thread is the UI Thread
     *
     * @return
     */
    public static boolean isUIThread() {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            // On UI thread.
            return true;
        } else {
            // Not on UI thread.
            return false;
        }
    }

    // Identifiers of STFragments. Each id correspond to a STFragment
    public enum STFragmentId {
        // Generic Fragments
        TAG_INFO_FRAGMENT_ID,
        CC_FILE_TYPE5_FRAGMENT_ID,
        CC_FILE_TYPE4_FRAGMENT_ID,
        SYS_FILE_TYP5_FRAGMENT_ID,
        SYS_FILE_M24SR_FRAGMENT_ID,
        SYS_FILE_ST25TA_HIGH_DENSITY_FRAGMENT_ID,
        SYS_FILE_ST25TA_FRAGMENT_ID,
        RAW_DATA_FRAGMENT_ID,
        NDEF_DETAILS_FRAGMENT_ID,

        // M24SR Fragments
        M24SR_NDEF_DETAILS_FRAGMENT_ID,
        M24SR_EXTRA_FRAGMENT_ID,

        // ST25TV Fragments
        ST25TV_CONFIG_FRAGMENT_ID,

        // NDEF Fragments
        NDEF_MULTI_RECORD_FRAGMENT_ID,
        NDEF_SMS_FRAGMENT_ID,
        NDEF_TEXT_FRAGMENT_ID,
        NDEF_URI_FRAGMENT_ID
    }

    /**
     * This function instantiate a STFragment from its STFragmentId
     *
     * @param context
     * @param stFragmentId
     * @return
     */
    public static STFragment getSTFragment(Context context, STFragmentId stFragmentId) {
        STFragment fragment = null;

        switch (stFragmentId) {
            // Generic Fragments
            case TAG_INFO_FRAGMENT_ID:
                fragment = TagInfoFragment.newInstance(context);
                break;
            case CC_FILE_TYPE5_FRAGMENT_ID:
                fragment = CCFileType5Fragment.newInstance(context);
                break;
            case CC_FILE_TYPE4_FRAGMENT_ID:
                fragment = CCFileType4Fragment.newInstance(context);
                break;
            case SYS_FILE_TYP5_FRAGMENT_ID:
                fragment = SysFileType5Fragment.newInstance(context);
                break;
            case SYS_FILE_M24SR_FRAGMENT_ID:
                fragment = SysFileM24SRFragment.newInstance(context);
                break;
            case SYS_FILE_ST25TA_HIGH_DENSITY_FRAGMENT_ID:
                fragment = SysFileST25TAHighDensityFragment.newInstance(context);
                break;
            case SYS_FILE_ST25TA_FRAGMENT_ID:
                fragment = SysFileST25TAFragment.newInstance(context);
                break;
            case RAW_DATA_FRAGMENT_ID:
                fragment = RawReadWriteFragment.newInstance(context);
                break;
            case NDEF_DETAILS_FRAGMENT_ID:
                fragment = NDEFEditorFragment.newInstance(context);
                break;
            default:
                Log.e(TAG, "Invalid stFragmentId: " + stFragmentId);
                break;

        }

        return fragment;
    }


    // Convert the area number into an area name
    public static String getAreaName(int area) {
        String areaName = "Area" + area;
        return areaName;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean isAType4Tag(NFCTag tag) {
        if (tag instanceof Type4Tag) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isAType5Tag(NFCTag tag) {
        if (tag instanceof Type5Tag) {
            return true;
        } else {
            return false;
        }
    }

    public static void invalidateCache(NFCTag tag) {
        if (tag instanceof Type4Tag) {
            Type4Tag type4Tag = (Type4Tag) tag;
            type4Tag.invalidateCache();

        } else if (tag instanceof Type5Tag) {
            Type5Tag type5Tag = (Type5Tag) tag;
            type5Tag.invalidateCache();
        } else {
            Log.e(TAG, "Tag not supported yet!");
        }
    }

    /**
     * Function returning the Type4 fileId corresponding to an Area.*
     * @param area
     */
    public static int getType4FileIdFromArea(int area) {
        return area;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void displayAboutDialogBox(Context context) {

        //set up dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.application_version_dialog);
        dialog.setTitle(context.getResources().getString(R.string.version_dialog_header));
        dialog.setCancelable(true);

        String versionName = "???";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        //set up text
        String message;
        message = "App version: V" + versionName;

        TextView text = (TextView) dialog.findViewById(R.id.TextView01);
        text.setText(message);

        message = context.getResources().getString(R.string.app_description);
        text = (TextView) dialog.findViewById(R.id.TextView02);
        text.setText(message);

        //set up image view
        ImageView img = (ImageView) dialog.findViewById(R.id.ImageView01);
        img.setImageResource(R.drawable.logo_st25_transp);

        //set up button
        Button button = (Button) dialog.findViewById(R.id.Button01);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //now that the dialog is set up, it's time to show it
        dialog.show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // Class used to store the information about the tag taped. It contains:
    // - the NFCTag object
    // - the ProductID
    public static class TagInfo {
        public NFCTag nfcTag;
        public TagHelper.ProductID productID;
    }

    /**
     * This function performs the discovery of a tag (provided as an Android 'Tag' class).
     *
     * IMPORTANT: This function will fail if it is called from UI Thread.
     *
     * @param androidTag
     * @return a TagInfo or null if the discovery failed.
     */
    static public TagInfo performTagDiscovery(Tag androidTag) {
        TagInfo tagInfo = new TagInfo();
        tagInfo.nfcTag = null;
        tagInfo.productID = PRODUCT_UNKNOWN;

        Log.v(TAG, "Starting TagDiscovery");

        if(androidTag == null) {
            Log.e(TAG, "androidTag cannot be null!");
            return null;
        }

        AndroidReaderInterface readerInterface = AndroidReaderInterface.newInstance(androidTag);

        if (readerInterface == null) {
            tagInfo.nfcTag = null;
            tagInfo.productID = PRODUCT_UNKNOWN;
            return tagInfo;
        }

        byte[] uid = androidTag.getId();


        switch (readerInterface.mTagType) {
            case NFC_TAG_TYPE_V:
                uid = Helper.reverseByteArray(uid);
                tagInfo.productID = identifyTypeVProduct(readerInterface, uid);
                break;
            case NFC_TAG_TYPE_4A:
                tagInfo.productID = identifyType4Product(readerInterface, uid);
                break;
            case NFC_TAG_TYPE_A:
            case NFC_TAG_TYPE_B:
            default:
                tagInfo.productID = PRODUCT_UNKNOWN;
                break;
        }


        // Take advantage that we are in a background thread to allocate the NFCTag.
        try {
            switch (tagInfo.productID) {
                case PRODUCT_ST_ST25DV64K_I:
                case PRODUCT_ST_ST25DV64K_J:
                case PRODUCT_ST_ST25DV16K_I:
                case PRODUCT_ST_ST25DV16K_J:
                case PRODUCT_ST_ST25DV04K_I:
                case PRODUCT_ST_ST25DV04K_J:
                    tagInfo.nfcTag = new ST25DVTag(readerInterface, uid);
                    break;
                case PRODUCT_ST_LRi512:
                    tagInfo.nfcTag = new LRi512Tag(readerInterface, uid);
                    break;
                case PRODUCT_ST_LRi1K:
                    tagInfo.nfcTag = new LRi1KTag(readerInterface, uid);
                    break;
                case PRODUCT_ST_LRi2K:
                    tagInfo.nfcTag = new LRi2KTag(readerInterface, uid);
                    break;
                case PRODUCT_ST_LRiS2K:
                    tagInfo.nfcTag = new LRiS2KTag(readerInterface, uid);
                    break;
                case PRODUCT_ST_LRiS64K:
                    tagInfo.nfcTag = new LRiS64KTag(readerInterface, uid);
                    break;
                case PRODUCT_ST_M24SR02_Y:
                    tagInfo.nfcTag = new M24SR02KTag(readerInterface, uid);
                    break;
                case PRODUCT_ST_M24SR04:
                    tagInfo.nfcTag = new M24SR04KTag(readerInterface, uid);
                    break;
                case PRODUCT_ST_M24SR16_Y:
                    tagInfo.nfcTag = new M24SR16KTag(readerInterface, uid);
                    break;
                case PRODUCT_ST_M24SR64_Y:
                    tagInfo.nfcTag = new M24SR64KTag(readerInterface, uid);
                    break;
                case PRODUCT_ST_ST25TV02K:
                case PRODUCT_ST_ST25TV512:
                    tagInfo.nfcTag = new ST25TVTag(readerInterface, uid);
                    break;
                case PRODUCT_ST_ST25TV64K:
                    tagInfo.nfcTag =  new ST25TV64KTag(readerInterface, uid);
                    break;
                case PRODUCT_ST_ST25DV02K_W:
                    tagInfo.nfcTag = new ST25DV02KWTag(readerInterface, uid);
                    break;
                case PRODUCT_ST_M24LR16E_R:
                    tagInfo.nfcTag = new M24LR16KTag(readerInterface, uid);
                    break;
                case PRODUCT_ST_M24LR32E_R:
                case PRODUCT_ST_M24LR64E_R:
                case PRODUCT_ST_M24LR64_R:
                    tagInfo.nfcTag = new M24LR64KTag(readerInterface, uid);
                    break;
                case PRODUCT_ST_M24LR128E_R:
                case PRODUCT_ST_M24LR256E_R:
                case PRODUCT_ST_M24LR01E_R:
                case PRODUCT_ST_M24LR02E_R:
                case PRODUCT_ST_M24LR04E_R:
                case PRODUCT_ST_M24LR08E_R:
                    tagInfo.nfcTag = new M24LR04KTag(readerInterface, uid);
                    break;
                case PRODUCT_ST_ST25TA02K:
                case PRODUCT_ST_ST25TA02K_G:
                    tagInfo.nfcTag = new ST25TA02KTag(readerInterface, uid);
                    break;
                case PRODUCT_ST_ST25TA02K_P:
                    tagInfo.nfcTag = new ST25TA02KPTag(readerInterface, uid);
                    break;
                case PRODUCT_ST_ST25TA02K_D:
                    tagInfo.nfcTag = new ST25TA02KDTag(readerInterface, uid);
                    break;
                case PRODUCT_ST_ST25TA16K:
                    tagInfo.nfcTag = new ST25TA16KTag(readerInterface, uid);
                    break;
                case PRODUCT_ST_ST25TA512:
                case PRODUCT_ST_ST25TA512_G:
                    tagInfo.nfcTag = new ST25TA512Tag(readerInterface, uid);
                    break;
                case PRODUCT_ST_ST25TA64K:
                    tagInfo.nfcTag = new ST25TA64KTag(readerInterface, uid);
                    break;
                case PRODUCT_GENERIC_TYPE4:
                    tagInfo.nfcTag =  new Type4Tag(readerInterface, uid);
                    break;
                case PRODUCT_GENERIC_TYPE5_AND_ISO15693:
                    tagInfo.nfcTag =  new STType5Tag(readerInterface, uid);
                    break;
                case PRODUCT_GENERIC_TYPE5:
                    tagInfo.nfcTag =  new Type5Tag(readerInterface, uid);
                    break;
                default:
                    tagInfo.nfcTag = null;
                    tagInfo.productID = PRODUCT_UNKNOWN;
                    break;
            }
        } catch (STException e) {
            // An STException has occured while instantiating the tag
            e.printStackTrace();
            tagInfo.productID = PRODUCT_UNKNOWN;
        }

        if(tagInfo.nfcTag != null) {
            String manufacturerName = "";
            try {
                manufacturerName = tagInfo.nfcTag.getManufacturerName();
            } catch (STException e) {
                e.printStackTrace();
            }

            if(manufacturerName.equals("STMicroelectronics")) {
                tagInfo.nfcTag.setName(tagInfo.productID.toString());
            }
        }

        return tagInfo;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

}


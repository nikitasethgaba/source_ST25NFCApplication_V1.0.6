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

package com.st.st25androidapp.androidWrapper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.Until;
import android.util.Log;

import com.st.st25nfc.generic.util.UIHelper;
import com.st.st25nfc.generic.MainActivity;
import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.TagHelper;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class AndroidHelper {

    static public enum Slot {
        SLOT1_TAG_LRi2K,
        SLOT2_TAG_ST25TV,
        SLOT3_TAG_ST25DV,
        SLOT4_TAG_ST25TA,
        SLOT5_TAG_TYPE5,     /* To be clarified later when we will have a robot */
        SLOT6_TAG_TYPE4      /* To be clarified later when we will have a robot */
    }

    static final String TAG = "AndroidHelper";
    static private Tag mAndroidTag;
    static private TagHelper.ProductID mProductID;

    // Semaphore used to wait for a NFC Intent
    static private Semaphore mSemaphore = new Semaphore(0);

    private static final long LAUNCH_TIMEOUT = 5000;
    private static final String APP_PACKAGE = MainActivity.class.getPackage().getName();

    static private UiDevice mDevice;
    static private Context mContext;


    /**
     * Start MainActivity application
     * @return true in case of success, false otherwise
     */
    static public boolean startApplication() {
        // Ensure that no tag is in the field when we start the test suite
        AndroidHelper.deselectTag();

        // Get the device instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        //assertThat(mDevice, notNullValue());

        // Start from the home screen
        mDevice.pressHome();

        // Wait for launcher
        final String launcherPackage = getLauncherPackageName();
        //assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        // Launch the app
        Context appContext = InstrumentationRegistry.getTargetContext();
        final Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        appContext.startActivity(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(APP_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
        mContext = InstrumentationRegistry.getTargetContext();

        // MainActivity is now up and running.
        // For the moment there is no tag in front

        return true;
    }

    /**
     * Uses package manager to find the package name of the device launcher. Usually this package
     * is "com.android.launcher" but can be different at times. This is a generic solution which
     * works on all platforms.`
     */
    static private String getLauncherPackageName() {
        // Create launcher Intent
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        // Use PackageManager to get the launcher package name
        PackageManager pm = InstrumentationRegistry.getContext().getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }

    /**
     * The phone will be moved to the specified tag.
     * @param slotNbr
     * @return a NFCTag in case of success, null otherwise
     */
    static public NFCTag selectTagSlot(Slot slotNbr) {
        boolean result = false;
        NFCTag tag = null;

        // Setup a hook in order to be notified when the MainActivity will receive the NFC Intent
        MainActivity.setNfcIntentHook(new MainActivity.NfcIntentHook() {
            @Override
            public void newNfcIntent(Intent intent) {
                processIntent(intent);
            }
        });

        // ### TODO: Use a system command to move the phone to the specified tag

        Log.v(TAG, "### User action needed: Move the phone over " + slotNbr);

        // Wait for the intent to occur (with a time out of 2s)
        try {
            result = mSemaphore.tryAcquire(/*2*/20, TimeUnit.SECONDS);        // TODO: Timeout temporary set to 20s
            if(result) {
                // Intent received
                Log.v(TAG, "Intent received. mAndroidTag: " + mAndroidTag);
                tag = performTagDiscovery();
            } else {
                Log.e(TAG, "Time out!!!!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (STException e) {
            e.printStackTrace();
        }

        // Failure
        return tag;
    }

    static public NFCTag waitForATag() {
        boolean result = false;
        NFCTag tag = null;

        // Setup a hook in order to be notified when the MainActivity will receive the NFC Intent
        MainActivity.setNfcIntentHook(new MainActivity.NfcIntentHook() {
            @Override
            public void newNfcIntent(Intent intent) {
                processIntent(intent);
            }
        });

        Log.v(TAG, "### Please tap the tag that you want to test");

        // Wait for the intent to occur (with a time out of 20s)
        try {
            result = mSemaphore.tryAcquire(20, TimeUnit.SECONDS);
            if(result) {
                // Intent received
                Log.v(TAG, "Intent received. mAndroidTag: " + mAndroidTag);
                tag = performTagDiscovery();
            } else {
                Log.e(TAG, "Time out!!!!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (STException e) {
            e.printStackTrace();
        }

        // Failure
        return tag;
    }


    static private void processIntent(Intent intent) {
        Log.v(TAG, "newNfcIntent: " + intent);
        mAndroidTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        // Signal that we have received the intent
        mSemaphore.release();
    }

    static private NFCTag performTagDiscovery() throws STException {

        UIHelper.TagInfo tagInfo = UIHelper.performTagDiscovery(mAndroidTag);

        NFCTag tag = tagInfo.nfcTag;
        mProductID = tagInfo.productID;

        return tag;
    }

    /**
     * This function will move the phone out of any tag field
     * @return true in case of success, false otherwise
     */
    static public boolean deselectTag() {

        // ### TODO: Use a system command to move the phone out of tag field
        Log.v(TAG, "### User action needed: Move the phone out of tag field");

        // Let some time for the action to be executed
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "=== No tag in the field ===");

        return true;
    }

}

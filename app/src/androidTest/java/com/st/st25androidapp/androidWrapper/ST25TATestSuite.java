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

import android.util.Log;

import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.tests.type4.st25ta.ST25TATests;
import com.st.st25sdk.type4a.st25ta.ST25TATag;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Runs all unit tests.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ST25TATests.class})
public class ST25TATestSuite {

    private static final String TAG = "ST25TATestSuite";

    @BeforeClass
    public static void onceExecutedBeforeAllTests() throws STException {
        boolean result;
        NFCTag tag;
        int tagMemSizeInBytes;
        ST25TATag st25TA;
        String tagName;

        // NB: Called before the instanciation of MainActivity
        Log.v(TAG, "@BeforeClass: onceExecutedBeforeAllTests");

        result = AndroidHelper.startApplication();
        assertTrue(result);

        tag = AndroidHelper.selectTagSlot(AndroidHelper.Slot.SLOT4_TAG_ST25TA);
        assertNotNull(tag);

        try {
            st25TA = (ST25TATag) tag;
        } catch (ClassCastException e) {
            throw new STException("### This is not a ST25TATag ! ###");
        }

        // Pass the tag to JUnit
        ST25TATests.setTag(st25TA);

        Log.v(TAG, "ST25TA tests initialization done successfully");
    }

}

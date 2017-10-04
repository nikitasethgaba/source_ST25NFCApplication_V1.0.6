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
import com.st.st25sdk.tests.type5.Type5Tests;
import com.st.st25sdk.type5.Type5Tag;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Runs all unit tests.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({Type5Tests.class})
public class Type5TestSuite {

    private static final String TAG = "Type5TestSuite";

    @BeforeClass
    public static void onceExecutedBeforeAllTests() throws STException {
        boolean result;
        NFCTag tag;
        int tagMemSizeInBytes;
        Type5Tag type5Tag;

        // NB: Called before the instanciation of MainActivity
        Log.v(TAG, "@BeforeClass: onceExecutedBeforeAllTests");

        result = AndroidHelper.startApplication();
        assertTrue(result);

        tag = AndroidHelper.selectTagSlot(AndroidHelper.Slot.SLOT5_TAG_TYPE5);
        assertNotNull(tag);

        try {
            type5Tag = (Type5Tag) tag;
        } catch (ClassCastException e) {
            throw new STException("### This is not a Type5Tag ! ###");
        }

        // Pass the tag to JUnit
        Type5Tests.setTag(type5Tag);

        Log.v(TAG, "Type5Tests initialization done successfully");
    }

}

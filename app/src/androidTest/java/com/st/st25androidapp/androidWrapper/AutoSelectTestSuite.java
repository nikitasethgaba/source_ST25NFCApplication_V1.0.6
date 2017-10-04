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
import com.st.st25sdk.tests.type4.M24SR.M24SRTests;
import com.st.st25sdk.tests.type4.Type4Tests;
import com.st.st25sdk.tests.type4.st25ta.ST25TATests;
import com.st.st25sdk.tests.type5.Type5Tests;
import com.st.st25sdk.tests.type5.lri.LRiTests;
import com.st.st25sdk.tests.type5.VicinityTests;
import com.st.st25sdk.tests.type5.m24lr.M24LR04Tests;
import com.st.st25sdk.tests.type5.st25dv.ST25DVTests;
import com.st.st25sdk.tests.type5.st25tv.ST25TVTests;
import com.st.st25sdk.type4a.m24srtahighdensity.M24SRTag;
import com.st.st25sdk.type4a.st25ta.ST25TATag;
import com.st.st25sdk.type4a.Type4Tag;
import com.st.st25sdk.type5.LRiTag;
import com.st.st25sdk.type5.M24LR04KTag;
import com.st.st25sdk.type5.ST25DVTag;
import com.st.st25sdk.type5.ST25TVTag;
import com.st.st25sdk.type5.STVicinityTag;
import com.st.st25sdk.type5.Type5Tag;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;

import static com.st.st25androidapp.androidWrapper.AutoSelectTestSuite.TagType.GENERIC_TYPE4;
import static com.st.st25androidapp.androidWrapper.AutoSelectTestSuite.TagType.GENERIC_TYPE5;
import static com.st.st25androidapp.androidWrapper.AutoSelectTestSuite.TagType.GENERIC_VICINITY;
import static com.st.st25androidapp.androidWrapper.AutoSelectTestSuite.TagType.LRi;
import static com.st.st25androidapp.androidWrapper.AutoSelectTestSuite.TagType.M24LR04;
import static com.st.st25androidapp.androidWrapper.AutoSelectTestSuite.TagType.M24SR;
import static com.st.st25androidapp.androidWrapper.AutoSelectTestSuite.TagType.ST25DV;
import static com.st.st25androidapp.androidWrapper.AutoSelectTestSuite.TagType.ST25TA;
import static com.st.st25androidapp.androidWrapper.AutoSelectTestSuite.TagType.ST25TV;
import static com.st.st25androidapp.androidWrapper.AutoSelectTestSuite.TagType.UNKNOWN_TYPE;
import static com.st.st25sdk.STException.STExceptionCode.INVALID_DATA;

/**
 * This TestSuite will:
 * - start the application
 * - wait that a tag is taped
 * - detect the tag type
 * - launch the corresponding test suite
 */
public class AutoSelectTestSuite extends TestCase {

    private static final String TAG = "AutoSelectTestSuite";

    protected enum TagType {
        UNKNOWN_TYPE,

        GENERIC_TYPE4,
        M24SR,
        ST25TA,

        GENERIC_TYPE5,
        ST25TV,
        ST25DV,
        LRi,

        M24LR04,

        GENERIC_VICINITY
    }


    public static TestSuite suite() throws STException {
        boolean result;
        NFCTag tag;
        int tagMemSizeInBytes;
        Class<?> testClass;

        TestSuite testSuite = new TestSuite();

        result = AndroidHelper.startApplication();
        assertTrue(result);

        tag = AndroidHelper.waitForATag();
        assertNotNull("Error! No tag was tapped or an unknown tag was tapped!", tag);

        // Select the testSuite corresponding to this tag
        TagType tagType = getTagType(tag);

        switch(tagType) {
            default:
            case UNKNOWN_TYPE:
                throw new STException(INVALID_DATA);

            case GENERIC_TYPE4:
                Log.v(TAG, "### Selecting GENERIC_TYPE4 tests");
                Type4Tag type4Tag = (Type4Tag) tag;
                Type4Tests.setTag(type4Tag);
                testClass = Type4Tests.class;
                break;

            case GENERIC_TYPE5:
                Log.v(TAG, "### Selecting GENERIC_TYPE5 tests");
                Type5Tag type5Tag = (Type5Tag) tag;
                Type5Tests.setTag(type5Tag);
                testClass = Type5Tests.class;
                break;

            case ST25DV:
                Log.v(TAG, "### Selecting ST25DV tests");
                ST25DVTag st25DVTag = (ST25DVTag) tag;
                ST25DVTests.setTag(st25DVTag);
                testClass = ST25DVTests.class;
                break;

            case ST25TV:
                Log.v(TAG, "### Selecting ST25TV tests");
                ST25TVTag st25TVTag = (ST25TVTag) tag;
                ST25TVTests.setTag(st25TVTag);
                testClass = ST25TVTests.class;
                break;

            case LRi:
                Log.v(TAG, "### Selecting LRi tests");
                LRiTag lriTag = (LRiTag) tag;
                LRiTests.setTag(lriTag);
                testClass = LRiTests.class;
                break;

            case M24SR:
                Log.v(TAG, "### Selecting M24SR tests");
                M24SRTag m24SRTag = (M24SRTag) tag;
                M24SRTests.setTag(m24SRTag);
                testClass = M24SRTests.class;
                break;

            case ST25TA:
                Log.v(TAG, "### Selecting ST25TA tests");
                ST25TATag st25TATag = (ST25TATag) tag;
                ST25TATests.setTag(st25TATag);
                testClass = ST25TATests.class;
                break;

            case M24LR04:
                Log.v(TAG, "### Selecting M24LR tests");
                M24LR04KTag m24LR04KTag = (M24LR04KTag) tag;
                M24LR04Tests.setTag(m24LR04KTag);
                testClass = M24LR04Tests.class;
                break;

            case GENERIC_VICINITY:
                Log.v(TAG, "### Selecting GENERIC_VICINITY tests");
                STVicinityTag vicinityTag = (STVicinityTag) tag;
                VicinityTests.setTag(vicinityTag);
                testClass = VicinityTests.class;
                break;
        }

        JUnit4TestAdapter junit4TestAdapter = new JUnit4TestAdapter(testClass);

        // Change this "if" if you want to execute a single test
        if(false) {
            try {
                // Put the test name here
                String testName = "testKill";

                final Description method = Description.createTestDescription(testClass, testName);
                Filter filter = Filter.matchMethodDescription(method);
                junit4TestAdapter.filter(filter);

            } catch (NoTestsRemainException e) {
                e.printStackTrace();
            }
        }

        testSuite.addTest(junit4TestAdapter);

        return testSuite;
    }

    private static TagType getTagType(NFCTag tag) {
        if (tag instanceof M24SRTag) {
            return M24SR;
        }

        if (tag instanceof ST25TATag) {
            return ST25TA;
        }

        if (tag instanceof Type4Tag) {
            return GENERIC_TYPE4;
        }

        if (tag instanceof ST25TVTag) {
            return ST25TV;
        }

        if (tag instanceof ST25DVTag) {
            return ST25DV;
        }

        if (tag instanceof LRiTag) {
            return LRi;
        }

        if (tag instanceof M24LR04KTag) {
            return M24LR04;
        }

        if (tag instanceof STVicinityTag) {
            return GENERIC_VICINITY;
        }

        if (tag instanceof Type5Tag) {
            return GENERIC_TYPE5;
        }

        return UNKNOWN_TYPE;
    }
}

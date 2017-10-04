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

package com.st.st25sdk.tests.type4.st25ta;

import org.junit.BeforeClass;
import org.junit.Test;

import com.st.st25sdk.STException;
import com.st.st25sdk.tests.type4.Type4TestReadWriteRawData;
import com.st.st25sdk.tests.type4.Type4Tests;
import com.st.st25sdk.type4a.st25ta.ST25TA02KPTag;

public class ST25TA02KPTests {

    private static final String TAG = "ST25TA02KP Tests";
    static private ST25TA02KPTag mST25TA02KPTag;

    public static boolean debug = true;

    @BeforeClass
    static public void setUp() {
        // Function called once before all tests
    }

    static public void setTag(ST25TA02KPTag st25TA02KPTag) {
        mST25TA02KPTag = st25TA02KPTag;
        Type4Tests.setTag(st25TA02KPTag);
    }

    @Test
    public void testReadWriteRawData() throws STException {
        Type4TestReadWriteRawData.run(mST25TA02KPTag);
    }

    @Test
    public void ST25TAtestCounter() throws STException {
        ST25TATestCounterSysFile.run(mST25TA02KPTag);
    }

    @Test
    public void ST25TA02KPtestGpoSysFile() throws STException {
        ST25TATestGpoSysFile.run(mST25TA02KPTag);
    }

}
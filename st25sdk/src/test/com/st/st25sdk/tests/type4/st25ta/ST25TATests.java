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
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.st.st25sdk.STException;
import com.st.st25sdk.tests.generic.NFCTagUtils;
import com.st.st25sdk.tests.type4.Type4Tests;
import com.st.st25sdk.type4a.st25ta.ST25TATag;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ST25TATests extends Type4Tests {

    private static final String TAG = "ST25TATests";
    static private ST25TATag mST25TATag;

    public static boolean debug = true;

    @BeforeClass
    static public void setUp() {
        // Function called once before all tests
    }

    static public void setTag(ST25TATag st25TATag) {
        mST25TATag = st25TATag;
        Type4Tests.setTag(st25TATag);
    }

    @Test
    public void testCounter() throws STException {
        NFCTagUtils.printTestName("testCounter");
        ST25TATestCounterSysFile.run(mST25TATag);
    }

    @Test
    public void testGpoSysFile() throws STException {
        NFCTagUtils.printTestName("testGpoSysFile");
        ST25TATestGpoSysFile.run(mST25TATag);
    }

}

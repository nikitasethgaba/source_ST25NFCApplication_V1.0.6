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

package com.st.st25sdk.tests.type5.lri;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.st.st25sdk.STException;
import com.st.st25sdk.command.Iso15693CustomKillCommandInterface;
import com.st.st25sdk.tests.generic.NFCTagUtils;
import com.st.st25sdk.tests.type5.STType5Tests;
import com.st.st25sdk.tests.type5.Type5TestKill;
import com.st.st25sdk.tests.type5.Type5TestLockSingleBlock;
import com.st.st25sdk.tests.type5.Type5Tests;
import com.st.st25sdk.type5.LRiTag;


// This class is for LRi specific tests. For Type5 generic tests, use Type5Tests
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LRiTests extends STType5Tests {
    private static final String TAG = "LRiTests";
    private static LRiTag mLRiTag;

    @BeforeClass
    static public void setUp() {
        // Function called once before all tests
    }

    static public void setTag(LRiTag lriTag) {
        mLRiTag = lriTag;
        STType5Tests.setTag(lriTag);
    }

    /*
     * TEST DESCRIPTION:
     * Test checking that the "LockSingleBlock" command is working
     */
    @Test
    public void testLockSingleBlock() throws STException {
        NFCTagUtils.printTestName("testLockSingleBlock");

        Type5TestLockSingleBlock.run(mLRiTag);
    }

    /*
     * TEST DESCRIPTION:
     * Test checking that the "kill" command is working
     */
    @Test
    public void testKill() throws STException {
        NFCTagUtils.printTestName("testKill");

        Iso15693CustomKillCommandInterface killInterface = (Iso15693CustomKillCommandInterface) mLRiTag;
        Type5TestKill.run((mLRiTag), killInterface);
    }

}

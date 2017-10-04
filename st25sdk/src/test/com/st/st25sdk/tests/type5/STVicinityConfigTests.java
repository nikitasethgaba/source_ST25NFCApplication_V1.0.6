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

package com.st.st25sdk.tests.type5;

import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.tests.generic.NFCTagUtils;
import com.st.st25sdk.tests.type5.STType5Tests;
import com.st.st25sdk.tests.type5.TestSTSectors;
import com.st.st25sdk.tests.type5.VicinityTests;
import com.st.st25sdk.type5.M24LR04KTag;
import com.st.st25sdk.type5.STVicinityConfigInterface;
import com.st.st25sdk.type5.STVicinityTag;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;



public class STVicinityConfigTests {

    static STVicinityConfigInterface mTag = null;

    static boolean mFastCommandsAvailable = false;

    static public void run(STVicinityConfigInterface tag) throws STException, InterruptedException {

        mTag = tag;


        //////////////////////////////////////////////////////////////////
        //
        STLog.i("Test configuration registers commands");
        testConfigurationCommands();

        //////////////////////////////////////////////////////////////////
        // !!! Assumption is default pwd = {0x00, 0x00, 0x00, 0x00 }
        STLog.i("Test control registers commands");
        testControlCommands();


        //////////////////////////////////////////////////////////////////
        STLog.i("Test End");

    }

    @Test
    public static void testConfigurationCommands() throws STException {
        NFCTagUtils.printTestName("testConfigurationCommands");

        byte[] initialConfig = mTag.readCfg();
        if (initialConfig != null) {
            mTag.writeEHCfg(initialConfig[1]);
            mTag.writeDOCfg(initialConfig[1]);
            byte[] newConfig = mTag.readCfg();
            if (newConfig != null)
                assertArrayEquals(initialConfig, newConfig);
            else
                fail("Error reading again configuration registers");
        }
        else
            fail("Error reading configuration registers");
    }

    @Test
    public static void testControlCommands() throws STException {
        NFCTagUtils.printTestName("testConfigurationCommands");
        byte[] initialEhConfig = mTag.checkEHEn();
        if (initialEhConfig != null) {
            mTag.setRstEHEn(initialEhConfig[1]);
            byte[] newConfig = mTag.checkEHEn();
            if (newConfig != null)
                assertArrayEquals(initialEhConfig, newConfig);
            else
                fail("Error reading again Eh registers");
        }
    }
}

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
import com.st.st25sdk.command.VicinityMemoryCommand;
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
import static org.junit.Assert.assertTrue;



public class STVicinityCCFileTests {
    static STVicinityTag mTag = null;

    static boolean mFastCommandsAvailable = false;

    static public void run(STVicinityTag tag) throws STException, InterruptedException {

        mTag = tag;


        //////////////////////////////////////////////////////////////////
        //
        STLog.i("Basic test for CCFile ");
        testReadWriteCCFile();


        //////////////////////////////////////////////////////////////////
        STLog.i("Test End");

    }

    public static void testReadWriteCCFile() throws STException {
        byte[] initialCCFile = mTag.readCCFile();
        mTag.initEmptyCCFile();
        mTag.writeCCFile();
        byte value = mTag.getCCMagicNumber();
        assertTrue((value == (byte) 0xE1) || (value == (byte) 0xE2));
        assertEquals(mTag.getCCMappingVersion(), 0x40);
        assertEquals(mTag.getCCReadAccess(), 0x00);
        assertEquals(mTag.getCCWriteAccess(), 0x00);
        VicinityMemoryCommand memCmd = new VicinityMemoryCommand(mTag.getReaderInterface(), mTag.getUid());
        com.st.st25sdk.type5.CCFileVicinity ccFileVicinity = new com.st.st25sdk.type5.CCFileVicinity(memCmd);
        ccFileVicinity.parseCCFile(initialCCFile);
        ccFileVicinity.write();
     }
}

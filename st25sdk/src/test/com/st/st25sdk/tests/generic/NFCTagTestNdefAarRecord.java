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

package com.st.st25sdk.tests.generic;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;

import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.ndef.AarRecord;
import com.st.st25sdk.ndef.NDEFMsg;
import com.st.st25sdk.type5.Type5Tag;

public class NFCTagTestNdefAarRecord {

    static public void testNdefAarRecord(NFCTag tag) throws STException, Exception {
        NDEFMsg ndefMsgToWrite = new NDEFMsg();

        ///////////////////////////////////////////
        // Step 1: Test adding some NDEF Records //
        ///////////////////////////////////////////

        // Fill the first 32 bytes with random data to be sure that there is no valid NDEF
        NFCTagUtils.eraseBeginningOfTag(tag);

        // Create a NDEF containing a AAR Record
        String aar = "com.st.st25nfc";
        STLog.i("Add a AAR record: " + aar);

        AarRecord aarRecord = new AarRecord(aar);
        ndefMsgToWrite.addRecord(aarRecord);

        // Write the NDEF to the tag
        tag.writeNdefMessage(ndefMsgToWrite);

        // Flush the cache
        NFCTagUtils.invalidateCache(tag);

        STLog.i("Read the NDEF content");
        NDEFMsg ndefMsgRead = tag.readNdefMessage();

        // Check that the NDEF read is the same as what was written
        assertEquals(ndefMsgToWrite, ndefMsgRead);

        byte[] ndefDataRead = ndefMsgToWrite.serialize();
        byte[] expectedNdefData = new byte[] {(byte) 0xDC, (byte) 0x0F, (byte) 0x0E, (byte) 0x00, (byte) 0x61, (byte) 0x6E, (byte) 0x64, (byte) 0x72,
                                              (byte) 0x6F, (byte) 0x69, (byte) 0x64, (byte) 0x2E, (byte) 0x63, (byte) 0x6F, (byte) 0x6D, (byte) 0x3A,
                                              (byte) 0x70, (byte) 0x6B, (byte) 0x67, (byte) 0x63, (byte) 0x6F, (byte) 0x6D, (byte) 0x2E, (byte) 0x73,
                                              (byte) 0x74, (byte) 0x2E, (byte) 0x73, (byte) 0x74, (byte) 0x32, (byte) 0x35, (byte) 0x6E, (byte) 0x66,
                                              (byte) 0x63};

        Assert.assertArrayEquals(expectedNdefData, ndefDataRead);

        // For Type5 tags, check also that the CCFile is OK
        if (tag instanceof Type5Tag) {
            Type5Tag type5Tag = (Type5Tag) tag;
            NFCTagUtils.checkType5CcFileContent(type5Tag);
        }

    }
}

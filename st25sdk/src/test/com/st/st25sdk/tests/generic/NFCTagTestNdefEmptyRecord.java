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

import static com.st.st25sdk.tests.generic.NFCTagUtils.generateByteArray;
import static org.junit.Assert.assertEquals;

import org.junit.Assert;

import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.ndef.EmptyRecord;
import com.st.st25sdk.ndef.NDEFMsg;
import com.st.st25sdk.type5.Type5Tag;

public class NFCTagTestNdefEmptyRecord {

    static public void testNdefEmptyRecord(NFCTag tag) throws STException, Exception {
        NDEFMsg ndefMsgToWrite = new NDEFMsg();

        ///////////////////////////////////////////
        // Step 1: Test adding some NDEF Records //
        ///////////////////////////////////////////

        // Fill the first 32 bytes with random data to be sure that there is no valid NDEF
        NFCTagUtils.eraseBeginningOfTag(tag);

        // Create a NDEF containing an Empty Record
        STLog.i("Add an Empty record");
        EmptyRecord emptyRecord = new EmptyRecord();
        ndefMsgToWrite.addRecord(emptyRecord);

        // Write the NDEF to the tag
        tag.writeNdefMessage(ndefMsgToWrite);

        // Flush the cache
        NFCTagUtils.invalidateCache(tag);

        STLog.i("Read the NDEF content");
        NDEFMsg ndefMsgRead = tag.readNdefMessage();

        // Check that the NDEF read is the same as what was written
        assertEquals(ndefMsgToWrite, ndefMsgRead);

        byte[] ndefDataRead = ndefMsgToWrite.serialize();

        // Enable this if you want to generate the reference containing the expected bytes.
        if(true) {
            String expectedResult = generateByteArray(ndefDataRead);
        }

        byte[] expectedNdefData = new byte[] {(byte) 0xD8, (byte) 0x00, (byte) 0x00, (byte) 0x00};

        Assert.assertArrayEquals(expectedNdefData, ndefDataRead);

        // For Type5 tags, check also that the CCFile is OK
        if (tag instanceof Type5Tag) {
            Type5Tag type5Tag = (Type5Tag) tag;
            NFCTagUtils.checkType5CcFileContent(type5Tag);
        }

    }
}

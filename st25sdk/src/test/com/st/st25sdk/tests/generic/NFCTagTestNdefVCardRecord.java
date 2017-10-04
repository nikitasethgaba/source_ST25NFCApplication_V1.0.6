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

import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.ndef.NDEFMsg;
import com.st.st25sdk.ndef.UriRecord;
import com.st.st25sdk.ndef.VCardRecord;
import com.st.st25sdk.type5.Type5Tag;

import org.junit.Assert;

import static com.st.st25sdk.ndef.UriRecord.NdefUriIdCode.NDEF_RTD_URI_ID_HTTP_WWW;
import static com.st.st25sdk.tests.generic.NFCTagUtils.generateByteArray;
import static org.junit.Assert.assertEquals;

public class NFCTagTestNdefVCardRecord {

    static public void testNdefVCardRecord(NFCTag tag) throws STException, Exception {
        NDEFMsg ndefMsgToWrite = new NDEFMsg();

        ///////////////////////////////////////////
        // Step 1: Test adding some NDEF Records //
        ///////////////////////////////////////////

        // Fill the first 32 bytes with random data to be sure that there is no valid NDEF
        NFCTagUtils.eraseBeginningOfTag(tag);

        // Create a NDEF containing a VCard Record
        STLog.i("Add a VCard record");

        VCardRecord vcardRecord = new VCardRecord();
        vcardRecord.setName("Toto");
        vcardRecord.setNumber("123");
        vcardRecord.setEmail("toto@st.com");

        ndefMsgToWrite.addRecord(vcardRecord);

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

        byte[] expectedNdefData = new byte[] {(byte) 0xD2, (byte) 0x0C, (byte) 0x58, (byte) 0x74, (byte) 0x65, (byte) 0x78, (byte) 0x74, (byte) 0x2F,
                (byte) 0x78, (byte) 0x2D, (byte) 0x76, (byte) 0x43, (byte) 0x61, (byte) 0x72, (byte) 0x64, (byte) 0x42,
                (byte) 0x45, (byte) 0x47, (byte) 0x49, (byte) 0x4E, (byte) 0x3A, (byte) 0x56, (byte) 0x43, (byte) 0x41,
                (byte) 0x52, (byte) 0x44, (byte) 0x0A, (byte) 0x56, (byte) 0x45, (byte) 0x52, (byte) 0x53, (byte) 0x49,
                (byte) 0x4F, (byte) 0x4E, (byte) 0x3A, (byte) 0x32, (byte) 0x2E, (byte) 0x31, (byte) 0x0A, (byte) 0x4E,
                (byte) 0x3A, (byte) 0x3B, (byte) 0x54, (byte) 0x6F, (byte) 0x74, (byte) 0x6F, (byte) 0x3B, (byte) 0x3B,
                (byte) 0x3B, (byte) 0x0A, (byte) 0x46, (byte) 0x4E, (byte) 0x3A, (byte) 0x54, (byte) 0x6F, (byte) 0x74,
                (byte) 0x6F, (byte) 0x0A, (byte) 0x45, (byte) 0x4D, (byte) 0x41, (byte) 0x49, (byte) 0x4C, (byte) 0x3B,
                (byte) 0x57, (byte) 0x4F, (byte) 0x52, (byte) 0x4B, (byte) 0x3A, (byte) 0x74, (byte) 0x6F, (byte) 0x74,
                (byte) 0x6F, (byte) 0x40, (byte) 0x73, (byte) 0x74, (byte) 0x2E, (byte) 0x63, (byte) 0x6F, (byte) 0x6D,
                (byte) 0x0A, (byte) 0x54, (byte) 0x45, (byte) 0x4C, (byte) 0x3B, (byte) 0x43, (byte) 0x45, (byte) 0x4C,
                (byte) 0x4C, (byte) 0x3A, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x0A, (byte) 0x45, (byte) 0x4E,
                (byte) 0x44, (byte) 0x3A, (byte) 0x56, (byte) 0x43, (byte) 0x41, (byte) 0x52, (byte) 0x44};

        Assert.assertArrayEquals(expectedNdefData, ndefDataRead);

        // For Type5 tags, check also that the CCFile is OK
        if (tag instanceof Type5Tag) {
            Type5Tag type5Tag = (Type5Tag) tag;
            NFCTagUtils.checkType5CcFileContent(type5Tag);
        }

    }
}

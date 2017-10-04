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
import com.st.st25sdk.ndef.WifiRecord;
import com.st.st25sdk.type5.Type5Tag;

import org.junit.Assert;

import static com.st.st25sdk.tests.generic.NFCTagUtils.generateByteArray;
import static org.junit.Assert.assertEquals;

public class NFCTagTestNdefWifiRecord {

    static public void testNdefWifiRecord(NFCTag tag) throws STException, Exception {
        NDEFMsg ndefMsgToWrite = new NDEFMsg();

        if(tag.getMemSizeInBytes() < 256) {
            // This tag doesn't has enough memory to run this test
            return;
        }

        ///////////////////////////////////////////
        // Step 1: Test adding some NDEF Records //
        ///////////////////////////////////////////

        // Fill the first 32 bytes with random data to be sure that there is no valid NDEF
        NFCTagUtils.eraseBeginningOfTag(tag);

        // Create a NDEF containing a Wifi Record
        STLog.i("Add a Wifi record");

        WifiRecord wifiRecord = new WifiRecord();
        wifiRecord.setSSID("my SSID");
        wifiRecord.setAuthType(1);  // WPA/WPA2 PSK
        wifiRecord.setEncrType(2);  // AES
        wifiRecord.setEncrKey("Dummy key");

        ndefMsgToWrite.addRecord(wifiRecord);

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

        byte[] expectedNdefData = new byte[] {(byte) 0xDA, (byte) 0x17, (byte) 0x50, (byte) 0x00, (byte) 0x61, (byte) 0x70, (byte) 0x70, (byte) 0x6C,
                (byte) 0x69, (byte) 0x63, (byte) 0x61, (byte) 0x74, (byte) 0x69, (byte) 0x6F, (byte) 0x6E, (byte) 0x2F,
                (byte) 0x76, (byte) 0x6E, (byte) 0x64, (byte) 0x2E, (byte) 0x77, (byte) 0x66, (byte) 0x61, (byte) 0x2E,
                (byte) 0x77, (byte) 0x73, (byte) 0x63, (byte) 0x10, (byte) 0x4A, (byte) 0x00, (byte) 0x01, (byte) 0x10,
                (byte) 0x10, (byte) 0x0E, (byte) 0x00, (byte) 0x47, (byte) 0x10, (byte) 0x26, (byte) 0x00, (byte) 0x01,
                (byte) 0x01, (byte) 0x10, (byte) 0x45, (byte) 0x00, (byte) 0x07, (byte) 0x6D, (byte) 0x79, (byte) 0x20,
                (byte) 0x53, (byte) 0x53, (byte) 0x49, (byte) 0x44, (byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x02,
                (byte) 0x00, (byte) 0x01, (byte) 0x10, (byte) 0x0F, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x02,
                (byte) 0x10, (byte) 0x27, (byte) 0x00, (byte) 0x09, (byte) 0x44, (byte) 0x75, (byte) 0x6D, (byte) 0x6D,
                (byte) 0x79, (byte) 0x20, (byte) 0x6B, (byte) 0x65, (byte) 0x79, (byte) 0x10, (byte) 0x20, (byte) 0x00,
                (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
                (byte) 0x49, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x37, (byte) 0x2A, (byte) 0x02, (byte) 0x01,
                (byte) 0x01, (byte) 0x10, (byte) 0x49, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x37, (byte) 0x2A,
                (byte) 0x00, (byte) 0x01, (byte) 0x20};

        Assert.assertArrayEquals(expectedNdefData, ndefDataRead);

        // For Type5 tags, check also that the CCFile is OK
        if (tag instanceof Type5Tag) {
            Type5Tag type5Tag = (Type5Tag) tag;
            NFCTagUtils.checkType5CcFileContent(type5Tag);
        }

    }
}

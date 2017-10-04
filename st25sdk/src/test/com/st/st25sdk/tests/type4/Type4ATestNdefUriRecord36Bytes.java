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

package com.st.st25sdk.tests.type4;

import static com.st.st25sdk.ndef.UriRecord.NdefUriIdCode.NDEF_RTD_URI_ID_HTTP_WWW;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.command.Iso7816Type4RApduStatus;
import com.st.st25sdk.ndef.NDEFMsg;
import com.st.st25sdk.ndef.UriRecord;
import com.st.st25sdk.tests.generic.NFCTagUtils;
import com.st.st25sdk.type4a.STType4Tag;

/**
 * Created on 1/9/17.
 */

public class Type4ATestNdefUriRecord36Bytes {

    static public void run(STType4Tag tag) throws STException {

        if(tag.getMemSizeInBytes() < 300) {
            // This tag doesn't has enough memory to run this test
            return;
        }
        // Test Uri record without ID -> targets 36 bytes read
        testNdefUriRecord(tag, 274, null);

        // Test Uri record without ID -> targets 34 bytes read
        // This test is required to cover the workaround implementation
        // But this test doesn't triggers the 34 bytes bug (when workaround is disabled)
        testNdefUriRecord(tag, 272, null);

        // Test Uri record with an ID -> targets 36 bytes read
        // This test is required to cover the workaround implementation
        // This test currently triggers errors in the SDK (in M24SRTAHighDensityTag.applyM24SRChange),
        // But this test doesn't triggers the 36 bytes bug (when workaround is disabled)
        testNdefUriRecord(tag, 271, new byte[] {0x49, 0x44});

    }

    static public void testNdefUriRecord (STType4Tag tag, int lengthForTest, byte[] id) throws STException {
        NDEFMsg ndefMsgToWrite = new NDEFMsg();
        ///////////////////////////////////////////
        // Step 1: Test adding some NDEF Records //
        ///////////////////////////////////////////

        NFCTagUtils.eraseBeginningOfTag(tag);

        // Create a NDEF containing a URI Record
        UriRecord.NdefUriIdCode uriID = NDEF_RTD_URI_ID_HTTP_WWW;
        String uri = "";
        STLog.i("Add a URI record with a 36bytes M24SR issue");

        byte[] memoryData = new byte[lengthForTest];
        for (int i = 0; i < lengthForTest; i++) memoryData[i] = (byte) (31);
        try {
            uri = new String (memoryData,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        UriRecord uriRecord = new UriRecord(uriID, uri);
        if ((id != null) && (id.length > 0)) {
            uriRecord.setId(id);
        }
        ndefMsgToWrite.addRecord(uriRecord);

        // Write the NDEF to the tag
        tag.writeNdefMessage(ndefMsgToWrite);

        // Flush the cache
        NFCTagUtils.invalidateCache(tag);

        // Disable 36bytes read workaround
        Iso7816Type4RApduStatus.mIgnoreSw2 = false;

        STLog.i("Read the NDEF content");
        NDEFMsg ndefMsgRead = tag.readNdefMessage();

        Iso7816Type4RApduStatus.mIgnoreSw2 = true;

        // Check that the NDEF read is the same as what was written
        assertEquals(ndefMsgToWrite, ndefMsgRead);

    }
}

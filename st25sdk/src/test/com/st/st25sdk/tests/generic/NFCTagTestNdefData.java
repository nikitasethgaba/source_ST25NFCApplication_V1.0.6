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
import com.st.st25sdk.ndef.NDEFRecord;
import com.st.st25sdk.ndef.TextRecord;
import com.st.st25sdk.ndef.UriRecord;

import java.util.Random;

import static com.st.st25sdk.ndef.UriRecord.NdefUriIdCode.NDEF_RTD_URI_ID_HTTP_WWW;
import static org.junit.Assert.assertEquals;

public class NFCTagTestNdefData {

    static public void testAddUpdateDeleteNdefRecords(NFCTag tag) throws STException {
        NDEFMsg ndefMsgToWrite = new NDEFMsg();

        ///////////////////////////////////////////
        // Step 1: Test adding some NDEF Records //
        ///////////////////////////////////////////

        // Fill the first 32 bytes with random data to be sure that there is no valid NDEF
        NFCTagUtils.eraseBeginningOfTag(tag);

        // Create a NDEF containing some NDEF records (ex: a Text record + a URI record)
        STLog.i("Add a Text record containing the text: 'Coucou!'");
        TextRecord textRecord1 = new TextRecord("Coucou!");

        STLog.i("Add an URI record containing the text: 'www.st.com/'");
        UriRecord uriRecord = new UriRecord(NDEF_RTD_URI_ID_HTTP_WWW, "www.st.com/");

        STLog.i("Add a Text record containing the text: 'Hello!'");
        TextRecord textRecord2 = new TextRecord("Hello!");

        ndefMsgToWrite.addRecord(textRecord1);
        ndefMsgToWrite.addRecord(uriRecord);
        ndefMsgToWrite.addRecord(textRecord2);

        // Write the NDEF to the tag
        tag.writeNdefMessage(ndefMsgToWrite);

        // Flush the cache
        NFCTagUtils.invalidateCache(tag);

        STLog.i("Read the NDEF content");
        NDEFMsg ndefMsgRead = tag.readNdefMessage();

        // Check that the NDEF read is the same as what was written
        assertEquals(ndefMsgToWrite, ndefMsgRead);


        /////////////////////////////////////////////
        // Step 2: Test updating some NDEF Records //
        /////////////////////////////////////////////
        TextRecord textRecord3 = new TextRecord("Hi!");

        STLog.i("Update a NDEF record");
        ndefMsgRead.updateRecord(textRecord3, 0);
        tag.writeNdefMessage(ndefMsgRead);

        // Flush the cache
        NFCTagUtils.invalidateCache(tag);

        STLog.i("Read the NDEF content");
        NDEFMsg ndefMsgRead2 = tag.readNdefMessage();

        // Check that record 0 contains "Hi!" (and no more "Coucou!")
        NDEFRecord record0 = ndefMsgRead2.getNDEFRecord(0);
        // NB: This cast will raise an exception if record0 is not a TextRecord
        TextRecord textRecordRead = (TextRecord) record0;

        assertEquals("Hi!", textRecordRead.getText());


        /////////////////////////////////////////////
        // Step 3: Test deleting some NDEF Records //
        /////////////////////////////////////////////

        // Delete the record located at position 1 (containing "www.st.com")
        // Check that only 2 records remain. They should contain "Hi!" and "Hello!"
        STLog.i("Delete a NDEF record");
        ndefMsgRead2.deleteRecord(1);
        tag.writeNdefMessage(ndefMsgRead2);

        // Flush the cache
        NFCTagUtils.invalidateCache(tag);

        STLog.i("Read the NDEF content");
        NDEFMsg ndefMsgRead3 = tag.readNdefMessage();

        // Check that record 0 contains "Hi!"
        record0 = ndefMsgRead2.getNDEFRecord(0);
        // NB: This cast will raise an exception if record0 is not a TextRecord
        textRecordRead = (TextRecord) record0;
        assertEquals("Hi!", textRecordRead.getText());

        // Check that record 1 contains "Hello!"
        NDEFRecord record1 = ndefMsgRead2.getNDEFRecord(1);
        // NB: This cast will raise an exception if record1 is not a TextRecord
        textRecordRead = (TextRecord) record1;
        assertEquals("Hello!", textRecordRead.getText());
    }
}

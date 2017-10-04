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

import com.st.st25sdk.MultiAreaInterface;
import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.ndef.NDEFMsg;
import com.st.st25sdk.ndef.NDEFRecord;
import com.st.st25sdk.ndef.TextRecord;
import com.st.st25sdk.ndef.UriRecord;

import static com.st.st25sdk.MultiAreaInterface.AREA1;
import static com.st.st25sdk.ndef.UriRecord.NdefUriIdCode.NDEF_RTD_URI_ID_HTTP_WWW;
import static org.junit.Assert.assertEquals;

public class NFCTagTestNdefInMultipleAreas {

    static public void run(NFCTag nfcTag, MultiAreaInterface multiAreaInterface) throws STException {
        int nbrOfAreas = multiAreaInterface.getNumberOfAreas();

        for(int area=AREA1; area <= nbrOfAreas; area++) {
            NDEFMsg ndefMsgToWrite = new NDEFMsg();

            String areaName = getAreaName(area);

            STLog.i("");
            STLog.i("***************************");
            STLog.i("Testing " + areaName);
            STLog.i("");

            /////////////////////////////////////
            // Step 1: Write some NDEF Records //
            /////////////////////////////////////

            // Create a NDEF containing some NDEF records
            STLog.i("Add a Text record containing the text: 'This is " + areaName + "'");
            TextRecord textRecord1 = new TextRecord("This is " + areaName);

            STLog.i("Add an URI record containing the text: 'www.st.com/'");
            UriRecord uriRecord = new UriRecord(NDEF_RTD_URI_ID_HTTP_WWW, "www.st.com/");

            STLog.i("Add a Text record containing the text: 'This text record will be deleted'");
            TextRecord textRecord2 = new TextRecord("This text record will be deleted");

            ndefMsgToWrite.addRecord(textRecord1);
            ndefMsgToWrite.addRecord(uriRecord);
            ndefMsgToWrite.addRecord(textRecord2);

            // Write the NDEF to the area
            multiAreaInterface.writeNdefMessage(area, ndefMsgToWrite);

            ///////////////////////////////////////////////////////////////////
            // Step 2: Read the NDEF Records and check that they are correct //
            ///////////////////////////////////////////////////////////////////

            // Flush the cache
            NFCTagUtils.invalidateCache(nfcTag);

            STLog.i("Read the NDEF content");
            NDEFMsg ndefMsgRead = multiAreaInterface.readNdefMessage(area);

            STLog.i("Check that the NDEF read is the same as what was written");
            assertEquals(ndefMsgToWrite, ndefMsgRead);

            ///////////////////////////////////////////////////////////////////////
            // Step 3: Delete a NDEF record and check that the result is correct //
            ///////////////////////////////////////////////////////////////////////

            STLog.i("Delete the Text record containing: 'This text record will be deleted at end of the test'");
            ndefMsgRead.deleteRecord(2);

            // Write the NDEF to the area
            multiAreaInterface.writeNdefMessage(area, ndefMsgRead);

            // Flush the cache
            NFCTagUtils.invalidateCache(nfcTag);

            STLog.i("Read the NDEF content");
            NDEFMsg ndefMsgRead2 = multiAreaInterface.readNdefMessage(area);

            STLog.i("Check that the NDEF is as expected");
            assertEquals(ndefMsgRead, ndefMsgRead2);
        }

        // Recheck the content of the first Text record contained in each area
        for(int area=AREA1; area<=nbrOfAreas; area++) {
            String areaName = getAreaName(area);

            NDEFMsg ndefMsgRead = multiAreaInterface.readNdefMessage(area);

            // This NDEF should contain 2 items
            assertEquals(2, ndefMsgRead.getNbrOfRecords());

            NDEFRecord record0 = ndefMsgRead.getNDEFRecord(0);

            // NB: This cast will raise an exception if record0 is not a TextRecord
            TextRecord textRecordRead = (TextRecord) record0;

            String expectedText = "This is " + areaName;
            String textRead = textRecordRead.getText();

            assertEquals(expectedText, textRead);
        }

    }

    public static String getAreaName(int area) {
        String areaName = "Area" + area;
        return areaName;
    }

}

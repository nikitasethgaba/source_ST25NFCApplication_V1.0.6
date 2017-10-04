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

import com.st.st25sdk.Helper;
import com.st.st25sdk.MultiAreaInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.ndef.NDEFMsg;
import com.st.st25sdk.ndef.TextRecord;
import com.st.st25sdk.tests.generic.NFCTagUtils;
import com.st.st25sdk.type5.Type5Tag;

import java.io.UnsupportedEncodingException;

import static com.st.st25sdk.MultiAreaInterface.AREA1;
import static com.st.st25sdk.STException.STExceptionCode.BAD_PARAMETER;
import static com.st.st25sdk.STException.STExceptionCode.INVALID_DATA;
import static com.st.st25sdk.STException.STExceptionCode.INVALID_ERROR_CODE;
import static com.st.st25sdk.STException.STExceptionCode.INVALID_NDEF_DATA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class Type5TestAreaBoundaries {

    static public void run(Type5Tag type5Tag, MultiAreaInterface multiAreaInterface) throws STException {
        // Length of the bytes containing the "type" and "length" of the TLV block containing the NDEF
        int tlSize = 2;
        int terminatorTlvLength = 1;
        int nbrOfAreas = multiAreaInterface.getNumberOfAreas();

        // In each area, write:
        // "<<<<" at the beginning
        // ">>>>" at the end
        for (int area = AREA1; area <= nbrOfAreas; area++) {
            int areaSizeInBytes = multiAreaInterface.getAreaSizeInBytes(area);
            int areaOffsetInBytes = multiAreaInterface.getAreaOffsetInBytes(area);

            String startText = "<<<<";
            String endText = ">>>>";

            byte[] startTextBytes = new byte[0];
            byte[] endTextBytes = new byte[0];
            try {
                startTextBytes = startText.getBytes("UTF-8");
                endTextBytes = endText.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            type5Tag.writeBytes(areaOffsetInBytes, startTextBytes);
            type5Tag.writeBytes(areaOffsetInBytes + areaSizeInBytes - endTextBytes.length , endTextBytes);
        }

        // Invalidate the cache because we have written some dummy data in memory
        NFCTagUtils.invalidateCache(type5Tag);

        // For each area (excepted the last one), write a TextRecord too big to fit in the area
        for (int area = AREA1; area < nbrOfAreas; area++) {
            int areaSizeInBytes = multiAreaInterface.getAreaSizeInBytes(area);
            int areaOffsetInBytes = multiAreaInterface.getAreaOffsetInBytes(area);

            // Get the CC File length
            int ccFileSize = getCCFileLengthInBytes(type5Tag, multiAreaInterface, area);

            int maxNdefMsgSize = areaSizeInBytes - ccFileSize - tlSize - terminatorTlvLength;

            ////////////////////////////////////////////////////////////////////////////////////////
            // This write should fail because there is one byte too much
            boolean isTestSuccessful = false;
            try {
                NDEFMsg ndefMsg = generateNdefMessage(area, maxNdefMsgSize+1);
                multiAreaInterface.writeNdefMessage(area, ndefMsg);
                // Ths command didn't fail. This is not normal

            } catch (STException e) {
                // This failure is normal
                isTestSuccessful = true;
            } catch (Exception e) {
                // This exception is not the expected one
                isTestSuccessful = false;
            }

            if(!isTestSuccessful) {
                throw new STException(INVALID_ERROR_CODE);
            }

            ////////////////////////////////////////////////////////////////////////////////////////
            // This write should succeed.
            try {
                NDEFMsg ndefMsg = generateNdefMessage(area, maxNdefMsgSize);
                multiAreaInterface.writeNdefMessage(area, ndefMsg);
            } catch (Exception e) {
                fail("Unexpected exception!");
            }

            ////////////////////////////////////////////////////////////////////////////////////////
            // Check the content of the last block of the area
            int offset = areaOffsetInBytes + areaSizeInBytes - 4;
            byte[] data = type5Tag.readBytes(offset, 4);

            char expectedChar = Integer.toString(area).charAt(0);
            // The first 3 bytes should contain the pattern written
            assertEquals(expectedChar, data[0]);
            assertEquals(expectedChar, data[1]);
            assertEquals(expectedChar, data[2]);
            // This byte should contain the TerminatorTLV
            assertEquals(data[3], (byte) 0xFE);

            ////////////////////////////////////////////////////////////////////////////////////////
            // Check the content of the first block of the next area. It should still contain "<<<<"
            int nextArea = area + 1;
            int nextAreaOffsetInBytes = multiAreaInterface.getAreaOffsetInBytes(nextArea);
            data = type5Tag.readBytes(nextAreaOffsetInBytes, 4);

            // '<' = 0x3C in ASCII
            assertEquals(0x3C, data[0]);
            assertEquals(0x3C, data[1]);
            assertEquals(0x3C, data[2]);
            assertEquals(0x3C, data[3]);
        }
    }

    static private int getCCFileLengthInBytes(Type5Tag type5Tag, MultiAreaInterface multiAreaInterface, int area) throws STException {
        int size, ccFileSize;

        if(area == AREA1) {
            // The is an exception for Area1. The CC File is related to the whole tag size and not to Area 1 size
            size = type5Tag.getMemSizeInBytes();
        } else {
            size = multiAreaInterface.getAreaSizeInBytes(area);
        }

        if (size >= 2048) {
            // CC File on 8 bytes
            ccFileSize = 8;
        } else {
            // CC File on 4 bytes
            ccFileSize = 4;
        }

        return ccFileSize;
    }

    /**
     * This function will generate a NDEF message of the requested size
     * @param area
     * @param expectedNdefSize
     * @throws STException
     */
    static private NDEFMsg generateNdefMessage(int area, int expectedNdefSize) throws STException, Exception {

        NDEFMsg ndefMsg = new NDEFMsg();
        TextRecord textRecord = new TextRecord();
        ndefMsg.addRecord(textRecord);

        try {
            int currentNdefSize = ndefMsg.getLength();

            if(expectedNdefSize < currentNdefSize) {
                throw new STException(BAD_PARAMETER);

            } else if (expectedNdefSize == currentNdefSize) {
                // No padding necessary

            } else {
                // Add some data to the payload in order to get the expected Ndef size

                // Compute the number of bytes that should be added to the payload to get a NDEF of the expected size
                int bytesNeeded = expectedNdefSize - currentNdefSize;

                // Create a long String containing a character corresponding to the area number
                // Ex: "11111111...." for Area1
                String myString = "";
                for(int i=0; i< bytesNeeded; i++) {
                    myString += Integer.toString(area);
                }

                textRecord.setText(myString);
            }

            // Check that the NDEF now has the expected size
            if(ndefMsg.getLength() != expectedNdefSize) {
                throw new STException(INVALID_DATA);
            }

            // Dump the NDEF that is going to be written
            byte[] ndefData = ndefMsg.serialize();
            STLog.w("ndefData : " + Helper.convertHexByteArrayToString(ndefData));

        } catch (Exception e) {
            e.printStackTrace();
            throw new STException(INVALID_NDEF_DATA);
        }

        return ndefMsg;
    }

}

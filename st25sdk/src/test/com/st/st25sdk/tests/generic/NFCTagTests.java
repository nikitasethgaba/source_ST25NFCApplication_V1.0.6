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
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;


public class NFCTagTests {

    static private NFCTag mNFCTag;
    public static boolean debug = true;

    protected static String mReaderName = "notSet";

    static public void setTag(NFCTag nfcTag) {
        mNFCTag = nfcTag;
    }

    @Before
    public void resetTransceiveMode() {
        // Function called once before each test. It will reset the TransceiveMode to its default value
        mNFCTag.getReaderInterface().setTransceiveMode(RFReaderInterface.TransceiveMode.NORMAL);
    }

    public static void setReaderName(String readerName) {
        mReaderName = readerName;
    }

    /*
     * TEST DESCRIPTION:
     * Test reading and writing some NDEF records.
     * It will test that we can:
     * - add a record
     * - update an existing record
     * - and delete a record
     */
    @Test
    public void testAddUpdateDeleteNdefRecords() throws STException {
        NFCTagUtils.printTestName("testAddUpdateDeleteNdefRecords");
        NFCTagTestNdefData.testAddUpdateDeleteNdefRecords(mNFCTag);
    }

    /*
     * TEST DESCRIPTION:
     * Test reading and writing a NDEF Text record.
     */
    @Test
    public void testNdefTextRecord() throws STException, Exception {
        NFCTagUtils.printTestName("testNdefTextRecord");
        NFCTagTestNdefTextRecord.testNdefTextRecord(mNFCTag);
    }

    /*
     * TEST DESCRIPTION:
     * Test reading and writing a NDEF SMS record.
     */
    @Test
    public void testNdefSmsRecord() throws STException, Exception {
        NFCTagUtils.printTestName("testNdefSmsRecord");
        NFCTagTestNdefSmsRecord.testNdefSmsRecord(mNFCTag);
    }

    /*
     * TEST DESCRIPTION:
     * Test reading and writing a NDEF URI record.
     */
    @Test
    public void testNdefUriRecord() throws STException, Exception {
        NFCTagUtils.printTestName("testNdefUriRecord");
        NFCTagTestNdefUriRecord.testNdefUriRecord(mNFCTag);
    }

    /*
     * TEST DESCRIPTION:
     * Test reading and writing a NDEF E-Mail record.
     */
    @Test
    public void testNdefEmailRecord() throws STException, Exception {
        NFCTagUtils.printTestName("testNdefEmailRecord");
        NFCTagTestNdefEmailRecord.testNdefEmailRecord(mNFCTag);
    }

    /*
     * TEST DESCRIPTION:
     * Test reading and writing a NDEF VCard record.
     */
    @Test
    public void testNdefVCardRecord() throws STException, Exception {
        NFCTagUtils.printTestName("testNdefVCardRecord");
        NFCTagTestNdefVCardRecord.testNdefVCardRecord(mNFCTag);
    }

    /*
     * TEST DESCRIPTION:
     * Test reading and writing a NDEF AAR record.
     */
    @Test
    public void testNdefAarRecord() throws STException, Exception {
        NFCTagUtils.printTestName("testNdefAarRecord");
        NFCTagTestNdefAarRecord.testNdefAarRecord(mNFCTag);
    }

    /*
     * TEST DESCRIPTION:
     * Test reading and writing a NDEF Wifi record.
     */
    @Test
    public void testNdefWifiRecord() throws STException, Exception {
        NFCTagUtils.printTestName("testNdefWifiRecord");
        NFCTagTestNdefWifiRecord.testNdefWifiRecord(mNFCTag);
    }

    /*
     * TEST DESCRIPTION:
     * Test reading and writing a NDEF Mime record.
     */
    @Test
    public void testNdefMimeRecord() throws STException, Exception {
        NFCTagUtils.printTestName("testNdefMimeRecord");
        NFCTagTestNdefMimeRecord.testNdefMimeRecord(mNFCTag);
    }

    /*
     * TEST DESCRIPTION:
     * Test reading and writing a NDEF External record.
     */
    @Test
    public void testNdefExternalRecord() throws STException, Exception {
        NFCTagUtils.printTestName("testNdefExternalRecord");
        NFCTagTestNdefExternalRecord.testNdefExternalRecord(mNFCTag);
    }

    /*
     * TEST DESCRIPTION:
     * Test reading and writing a NDEF Bluetooth record.
     */
    @Test
    public void testNdefBtRecord() throws STException, Exception {
        NFCTagUtils.printTestName("testNdefBtRecord");
        NFCTagTestNdefBtRecord.testNdefBtRecord(mNFCTag);
    }

    /*
     * TEST DESCRIPTION:
     * Test reading and writing a NDEF Bluetooth record.
     */
    @Test
    public void testNdefBtLeRecord() throws STException, Exception {
        NFCTagUtils.printTestName("testNdefBtLeRecord");
        NFCTagTestNdefBtLeRecord.testNdefBtLeRecord(mNFCTag);
    }

    /*
     * TEST DESCRIPTION:
     * Test reading and writing an Empty record.
     */
    @Test
    public void testNdefEmptyRecord() throws STException, Exception {
        NFCTagUtils.printTestName("testNdefEmptyRecord");
        NFCTagTestNdefEmptyRecord.testNdefEmptyRecord(mNFCTag);
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the robustness of NDEF parser.
     */
    @Test
    public void testNdefParser() throws STException, Exception {
        NFCTagUtils.printTestName("testNdefParser");
        NFCTagTestNdefParser.run(mNFCTag);
    }

    /*
     * TEST DESCRIPTION:
     * Test reading and writing NDEF record in all the areas of the tags
     */
    @Test
    public void testNdefInMultipleAreas() throws STException {

        NFCTagUtils.printTestName("testNdefInMultipleAreas");

        // Test if this tag support multiple area.
        try {
            MultiAreaInterface multiAreaInterface = (MultiAreaInterface) mNFCTag;
            NFCTagTestNdefInMultipleAreas.run(mNFCTag, multiAreaInterface);

        } catch (ClassCastException e) {
            // Skip this test because this tag doesn't support multiple area
            Assume.assumeTrue(false);
        }
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readBytes() and writeBytes()
     */
    @Test
    public void testReadWriteBytes() throws STException {
        NFCTagUtils.printTestName("testReadWriteBytes");
        NFCTagTestReadWriteBytes.run(mNFCTag);
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the helper functions doing conversions between String and Integers
     */
    @Test
    public void testHelperFunctions() throws STException {
        NFCTagUtils.printTestName("testHelperFunctions");
        HelperFunctionTests.run();
    }

}

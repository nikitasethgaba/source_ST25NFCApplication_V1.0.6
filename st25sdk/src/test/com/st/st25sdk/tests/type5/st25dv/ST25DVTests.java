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

package com.st.st25sdk.tests.type5.st25dv;

import static com.st.st25sdk.MultiAreaInterface.AREA1;
import static com.st.st25sdk.MultiAreaInterface.AREA2;
import static com.st.st25sdk.MultiAreaInterface.AREA3;
import static com.st.st25sdk.MultiAreaInterface.AREA4;
import static com.st.st25sdk.TagHelper.ReadWriteProtection.READABLE_AND_WRITABLE;
import static com.st.st25sdk.TagHelper.ReadWriteProtection.READ_AND_WRITE_PROTECTED_BY_PWD;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.st.st25sdk.RFReaderInterface.TransceiveMode;
import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.tests.generic.NFCTagUtils;
import com.st.st25sdk.tests.type5.STType5Tests;
import com.st.st25sdk.type5.ST25DVTag;

public class ST25DVTests extends STType5Tests {

    static private ST25DVTag mST25DVTag;
    final static byte[] mDefaultPassword = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    @BeforeClass
    static public void setUp() {
        // Function called once before any test
    }

    @Before
    public void setTransceive() throws STException {
        // Function called once before each test
        mST25DVTag.getReaderInterface().setTransceiveMode(TransceiveMode.NORMAL);
    }

    @After
    public void tearDown() {
        // We don't know the current state, cache may be compromised
        mST25DVTag.invalidateCache();
    }

    static public void setTag(ST25DVTag st25DVTag) {
        mST25DVTag = st25DVTag;
        STType5Tests.setTag(st25DVTag);
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readRawData() and writeRawData()
     */
    @Test
    @Override
    public void testReadWriteRawData() throws STException {

        NFCTagUtils.printTestName("testReadWriteRawData");

        // Pre-requisite for ST25DV:
        // Configure the ST25DV tag with one area without protection
        setOneAreaWithoutProtections();

        // We can now proceed with the test
        super.testReadWriteRawData();
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readSingleBlock() and writeSingleBlock()
     */
    @Test
    @Override
    public void testReadWriteSingleBlock() throws STException {

        NFCTagUtils.printTestName("testReadWriteSingleBlock");

        // Pre-requisite for ST25DV:
        // Configure the ST25DV tag with one area without protection
        setOneAreaWithoutProtections();

        // We can now proceed with the test
        super.testReadWriteSingleBlock();
    }

    /*
     * TEST DESCRIPTION:
     * Test fastReadSingleBlock()
     */
    @Test
    @Override
    public void testFastReadSingleBlock() throws STException, Exception {

        NFCTagUtils.printTestName("testFastReadSingleBlock");

        // Pre-requisite for ST25DV:
        // Configure the ST25DV tag with one area without protection
        setOneAreaWithoutProtections();

        // We can now proceed with the test
        super.testFastReadSingleBlock();
    }

    /*
     * TEST DESCRIPTION:
     * Test fastReadMultipleBlock()
     */
    @Test
    @Override
    public void testFastReadMultipleBlock() throws STException, Exception {

        NFCTagUtils.printTestName("testFastReadMultipleBlock");

        // Pre-requisite for ST25DV:
        // Configure the ST25DV tag with one area without protection
        setOneAreaWithoutProtections();

        // We can now proceed with the test
        super.testFastReadMultipleBlock();
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readBlocks() and writeBlocks()
     */
    @Test
    @Override
    public void testReadWriteBlocks() throws STException {

        NFCTagUtils.printTestName("testReadWriteBlocks");

        // Pre-requisite for ST25DV:
        // Configure the ST25DV tag with one area without protection
        setOneAreaWithoutProtections();

        // We can now proceed with the test
        super.testReadWriteBlocks();
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
    @Override
    public void testAddUpdateDeleteNdefRecords() throws STException {

        NFCTagUtils.printTestName("testAddUpdateDeleteNdefRecords");

        // Pre-requisite for ST25DV:
        // Configure the ST25DV tag with one area without protection
        setOneAreaWithoutProtections();

        // We can now proceed with the test
        super.testAddUpdateDeleteNdefRecords();
    }

    /*
     * TEST DESCRIPTION:
     * Test reading and writing NDEF record in all the areas of the tags
     */
    @Test
    @Override
    public void testNdefInMultipleAreas() throws STException {

        NFCTagUtils.printTestName("testNdefInMultipleAreas");

        // Pre-requisite for ST25DV:
        // Configure the ST25DV tag with 4 areas without protection
        setFourAreaWithoutProtections();

        // We can now proceed with the test
        super.testNdefInMultipleAreas();
    }

    /*
     * TEST DESCRIPTION:
     * Test changing the EndArea registers and checking that the areas are correct
     */
    @Test
    public void testMultiAreas() throws STException {

        NFCTagUtils.printTestName("testMultiAreas");

        // Pre-requisite for ST25DV:
        // Configure the ST25DV tag with 4 areas without protection
        setFourAreaWithoutProtections();

        // We can now proceed with the test
        ST25DVTestMultiArea.run(mST25DVTag);
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of the tag when writting too much data in an area.
     * It should not corrupt the data in the following area.
     */
    @Override
    @Test
    public void testAreaBoundaries() throws STException {
        NFCTagUtils.printTestName("testMultiAreas");

        // Pre-requisite for ST25DV:
        // Configure the ST25DV tag with 4 areas without protection
        setFourAreaWithoutProtections();

        // We can now proceed with the test
        super.testAreaBoundaries();
    }


    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readBytes() and readBlocks() when there are less bytes available
     * than requested. We should return what we have been able to read and not raise an exception.
     *
     * This test checks the behavior at end of memory (when the read asks more bytes than available
     * in memory)
     */
    @Override
    @Test
    public void testIncompleteReadAtEndOfMemory() throws STException {
        NFCTagUtils.printTestName("testIncompleteReadAtEndOfMemory");

        // Pre-requisite for ST25DV:
        // Configure the ST25DV tag with one single area without protection
        setOneAreaWithoutProtections();

        // We can now proceed with the test
        super.testIncompleteReadAtEndOfMemory();
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readBytes() and readBlocks() when there are less bytes available
     * than requested. We should return what we have been able to read and not raise an exception.
     *
     * This test checks the behavior when the following area is not readable. It is executed only for tags
     * supporting MultiAreaInterface.
     */
    @Override
    @Test
    public void testIncompleteReadAtAreaBoundary() throws STException {
        NFCTagUtils.printTestName("testIncompleteReadAtAreaBoundary");

        // Pre-requisite for ST25DV:
        // Configure the ST25DV tag with multiple areas with areas protected excepted AREA1
        setMultipleAreasWithAreasProtectedExceptedArea1();

        // We can now proceed with the test
        super.testIncompleteReadAtAreaBoundary();
    }


    @Test
    public void testMailbox() throws STException, InterruptedException {
        NFCTagUtils.printTestName("testMailbox");
        try {
            ST25DVTestMailbox.run(mST25DVTag, mReaderName);
        } finally {
            // don't know current state, cache may be compromised
            mST25DVTag.invalidateCache();
            // disable the MB
            mST25DVTag.disableMB();
            // disable watchdog
            mST25DVTag.presentPassword((byte) 0, ST25DVTests.mDefaultPassword);
            mST25DVTag.writeConfig(ST25DVTag.REGISTER_MB_WDG_ADDRESS, (byte)0x00);
        }
    }

    /*
     * TEST DESCRIPTION:
     * Tests presentPassword and writePassword commands
     */
    @Test
    public void testPasswords() throws STException, InterruptedException {
        NFCTagUtils.printTestName("testST25DVPasswords");
        ST25DVPasswordTest.run(mST25DVTag);
    }

    /*
     * TEST DESCRIPTION:
     * Tests writeMultipleBlock command
     */
    @Test
    public void testWriteMultipleBlock() throws STException, InterruptedException {
        NFCTagUtils.printTestName("testST25DVWriteMultipleBlock");
        ST25DVWriteMultipleBlockTest.run(mST25DVTag);
    }

    /*
     * TEST DESCRIPTION:
     * Tests extendedWriteMultipleBlock command
     */
    @Test
    public void testExtendedWriteMultipleBlock() throws STException, InterruptedException {
        NFCTagUtils.printTestName("testST25DVExtendedWriteMultipleBlock");
        ST25DVExtendedWriteMultipleBlockTest.run(mST25DVTag);
    }

    /*
     * TEST DESCRIPTION:
     * Tests fastExtendedReadSingleBlock command
     */
    @Test
    public void testFastExtendedReadSingleBlock() throws STException, Exception {

        // Configure the ST25DV tag with one single area without protection
        setOneAreaWithoutProtections();

        NFCTagUtils.printTestName("testST25DVExtendedWriteMultipleBlock");
        ST25DVTestFastExtendedReadSingleBlock.run(mST25DVTag, mReaderName);
    }

    /*
     * TEST DESCRIPTION:
     * Tests the different access protection settings of ST25DV areas
     */
    @Test
    public void testMultiAreaRWProtection() throws STException {
        NFCTagUtils.printTestName("testMultiAreaRWProtection");
        try {
            ST25DVTestAreaAccessProtection.run(mST25DVTag);
        } catch (Exception e) {
            // Try to write back default passwords in case things go bad
            for (int i = 1; i < 4; i++) {
                mST25DVTag.presentPassword((byte) i, ST25DVTestAreaAccessProtection.passwordValue.get(i));
                mST25DVTag.writePassword((byte) i, mDefaultPassword);
            }
        }
    }

    /*
     * TEST DESCRIPTION:
     * Tests the lock configuration command in EVAL mode
     */
    @Test
    public void testlockConfiguration() throws STException {
        NFCTagUtils.printTestName("testLockConfiguration");
        ST25DVTestLockConfiguration.run(mST25DVTag);
    }

    /***********************************************************************************/

    public static void setOneAreaWithoutProtections() throws STException {
        STLog.i("Configure the ST25DV tag with 1 area without protections");

        // Config password is needed when we want to change the Config
        byte[] password = new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        byte passwordNumber = mST25DVTag.getConfigurationPasswordNumber();
        mST25DVTag.presentPassword(passwordNumber, password);

        // Configure the memory to get only 1 area
        int maxEndOfAreaValue = (mST25DVTag.getMaxEndOfAreaValue() & 0xFF);
        mST25DVTag.setAreaEndValues((byte) maxEndOfAreaValue, (byte) maxEndOfAreaValue, (byte) maxEndOfAreaValue);

        // Configure the protection of each Area
        mST25DVTag.setReadWriteProtection(AREA1, READABLE_AND_WRITABLE);
        mST25DVTag.setReadWriteProtection(AREA2, READABLE_AND_WRITABLE);
        mST25DVTag.setReadWriteProtection(AREA3, READABLE_AND_WRITABLE);
        mST25DVTag.setReadWriteProtection(AREA4, READABLE_AND_WRITABLE);
    }


    public static void setFourAreaWithoutProtections() throws STException {
        STLog.i("Configure the ST25DV tag with 4 areas without protections");

        // Config password is needed when we want to change the Config
        byte[] password = new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        byte passwordNumber = mST25DVTag.getConfigurationPasswordNumber();
        mST25DVTag.presentPassword(passwordNumber, password);

        // Arbitrary values to configure the tag with 4 areas
        byte endOfArea1 = 0x04;
        byte endOfArea2 = 0x08;
        byte endOfArea3 = 0x0C;

        mST25DVTag.setAreaEndValues(endOfArea1, endOfArea2, endOfArea3);

        // Configure the protection of each Area
        mST25DVTag.setReadWriteProtection(AREA1, READABLE_AND_WRITABLE);
        mST25DVTag.setReadWriteProtection(AREA2, READABLE_AND_WRITABLE);
        mST25DVTag.setReadWriteProtection(AREA3, READABLE_AND_WRITABLE);
        mST25DVTag.setReadWriteProtection(AREA4, READABLE_AND_WRITABLE);
    }

    public static void setMultipleAreasWithAreasProtectedExceptedArea1() throws STException {
        STLog.i("Configure the ST25DV tag with 4 areas without protections");

        // Config password is needed when we want to change the Config
        byte[] password = new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        byte passwordNumber = mST25DVTag.getConfigurationPasswordNumber();
        mST25DVTag.presentPassword(passwordNumber, password);

        // Arbitrary values to configure the tag with 4 areas
        byte endOfArea1 = 0x04;
        byte endOfArea2 = 0x08;
        byte endOfArea3 = 0x0C;

        mST25DVTag.setAreaEndValues(endOfArea1, endOfArea2, endOfArea3);

        // Configure the protection of each Area
        mST25DVTag.setReadWriteProtection(AREA1, READABLE_AND_WRITABLE);
        mST25DVTag.setReadWriteProtection(AREA2, READ_AND_WRITE_PROTECTED_BY_PWD);
        mST25DVTag.setReadWriteProtection(AREA3, READ_AND_WRITE_PROTECTED_BY_PWD);
        mST25DVTag.setReadWriteProtection(AREA4, READ_AND_WRITE_PROTECTED_BY_PWD);
    }

}

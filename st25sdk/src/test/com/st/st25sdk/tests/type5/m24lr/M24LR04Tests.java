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

package com.st.st25sdk.tests.type5.m24lr;

import com.st.st25sdk.STException;
import com.st.st25sdk.tests.generic.NFCTagUtils;
import com.st.st25sdk.tests.type5.STType5Tests;
import com.st.st25sdk.tests.type5.TestSTSectors;
import com.st.st25sdk.tests.type5.VicinityTests;
import com.st.st25sdk.type5.M24LR04KTag;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;



public class M24LR04Tests extends STType5Tests {

    private static final String TAG = "M24LR04Tests";
    static private M24LR04KTag mM24LR04KTag;

    @BeforeClass
    static public void setUp() {
        // Function called once before all tests
    }

    static public void setTag(M24LR04KTag tag) {
        mM24LR04KTag = tag;
        VicinityTests.setTag(tag);
    }

    static public void setReaderName(String readerName) {
        mReaderName = readerName;
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readRawData() and writeRawData()
     */
    @Test
    @Override
    public void testReadWriteRawData() throws STException {

        NFCTagUtils.printTestName("testReadWriteRawData");

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

        // We can now proceed with the test
        super.testReadWriteSingleBlock();
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readBlocks() and writeBlocks()
     */
    @Test
    @Override
    public void testReadWriteBlocks() throws STException {

        NFCTagUtils.printTestName("testReadWriteBlocks");

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
        // We can now proceed with the test
        super.testAddUpdateDeleteNdefRecords();
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

        // We can now proceed with the test
        super.testIncompleteReadAtEndOfMemory();
    }

    @Test
    public void testSectors() throws STException, InterruptedException {
        NFCTagUtils.printTestName("testSectors management");
        TestSTSectors.run(mM24LR04KTag, mReaderName);
    }

    @Test
    public void testConfigurationRegisters() throws STException, InterruptedException {
        NFCTagUtils.printTestName("testConfigurationRegisters()");

        // We can now proceed with the test
        M24LR04ConfigTests.run(mM24LR04KTag);
    }

}

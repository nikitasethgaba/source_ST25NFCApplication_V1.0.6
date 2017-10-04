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

import com.st.st25sdk.STException;
import com.st.st25sdk.tests.generic.NFCTagUtils;
import com.st.st25sdk.type5.STVicinityConfigInterface;
import com.st.st25sdk.type5.STVicinityTag;

import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;

public class VicinityTests extends STType5Tests {

    static private STVicinityTag mVicinityTag;


    static public void setTag(STVicinityTag vicinityTag) {
        mVicinityTag = vicinityTag;
        STType5Tests.setTag(vicinityTag);
    }

    static public void setReaderName(String readerName) {
        mReaderName = readerName;
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readRawData() and writeRawData()
     */
    @Test
    public void testReadWriteRawData() throws STException {
        NFCTagUtils.printTestName("testReadWriteRawData");
        VicinityTestReadWriteRawData.run(mVicinityTag);
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readSingleBlock() and writeSingleBlock()
     */
    @Test
    public void testReadWriteSingleBlock() throws STException {
        NFCTagUtils.printTestName("testReadWriteSingleBlock");
        VicinityTestReadWriteSingleBlock.run(mVicinityTag);
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readBlocks() and writeBlocks()
     */
    @Test
    public void testReadWriteBlocks() throws STException {
        NFCTagUtils.printTestName("testReadWriteBlocks");
        Type5TestReadWriteBlocks.run(mVicinityTag);
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readBytes() and readBlocks() when there are less bytes available
     * than requested. We should return what we have been able to read and not raise an exception.
     *
     * This test checks the behavior at end of memory (when the read asks more bytes than available
     * in memory)
     */
    @Test
    public void testIncompleteReadAtEndOfMemory() throws STException {
        NFCTagUtils.printTestName("testIncompleteReadAtEndOfMemory");
        Type5TestIncompleteReadAtEndOfMemory.run(mVicinityTag);
    }

    @Test
    @Ignore //set the tag in a state only reversible by I2C
    public void testSectors() throws STException, InterruptedException {
        NFCTagUtils.printTestName("testSectors management");
        TestSTSectors.run(mVicinityTag, mReaderName);
    }

    @Test
    public void testConfigs() throws STException, InterruptedException {

        Assume.assumeTrue(mVicinityTag instanceof STVicinityConfigInterface);

        NFCTagUtils.printTestName("testConfig management");
        STVicinityConfigTests.run((STVicinityConfigInterface) mVicinityTag);
    }

    @Test
    public void testCCFile() throws STException, InterruptedException {
        NFCTagUtils.printTestName("CCFile test");
        STVicinityCCFileTests.run(mVicinityTag);
    }
}

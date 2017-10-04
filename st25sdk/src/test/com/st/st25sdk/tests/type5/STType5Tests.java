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

import org.junit.Test;

import com.st.st25sdk.STException;
import com.st.st25sdk.tests.generic.NFCTagUtils;
import com.st.st25sdk.type5.STType5Tag;

public class STType5Tests extends Type5Tests {

    protected static STType5Tag mSTType5Tag;


    static public void setTag(STType5Tag stType5Tag) {
        mSTType5Tag = stType5Tag;
        Type5Tests.setTag(stType5Tag);
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readBlocks() and writeBlocks()
     */
    @Test
    public void testReadWriteAfi() throws STException {
        NFCTagUtils.printTestName("testReadWriteAfi");
        Type5TestReadWriteAfi.run(mSTType5Tag);
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readBlocks() and writeBlocks()
     */
    @Test
    public void testReadWriteDsfid() throws STException {
        NFCTagUtils.printTestName("testReadWriteDsfid");
        Type5TestReadWriteDsfid.run(mSTType5Tag);
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readRawData() and writeRawData()
     */
    @Override
    @Test
    public void testReadWriteRawData() throws STException {
        super.testReadWriteRawData();
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readSingleBlock() and writeSingleBlock()
     */
    @Override
    @Test
    public void testReadWriteSingleBlock() throws STException {
        super.testReadWriteSingleBlock();
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readBlocks() and writeBlocks()
     */
    @Override
    @Test
    public void testReadWriteBlocks() throws STException {
        super.testReadWriteBlocks();
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of the tag when writting too much data in an area.
     * It should not corrupt the data in the following area.
     */
    @Override
    @Test
    public void testAreaBoundaries() throws STException {
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
        super.testIncompleteReadAtAreaBoundary();
    }

    /*
     * TEST DESCRIPTION:
     * Test fastReadSingleBlock command
     */
    @Test
    public void testFastReadSingleBlock() throws Exception, STException {
        NFCTagUtils.printTestName("testFastReadSingleBlock");
        Type5TestFastReadSingleBlock.run(mSTType5Tag, mReaderName);
    }

    /*
     * TEST DESCRIPTION:
     * Test fastReadMultipleBlock command
     */
    @Test
    public void testFastReadMultipleBlock() throws Exception, STException {
        NFCTagUtils.printTestName("testFastReadMultipleBlock");
        Type5TestFastReadMultipleBlock.run(mSTType5Tag, mReaderName);
    }
}

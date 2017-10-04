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

import org.junit.Assume;
import org.junit.Test;

import com.st.st25sdk.MultiAreaInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.tests.generic.NFCTagTests;
import com.st.st25sdk.tests.generic.NFCTagUtils;
import com.st.st25sdk.type5.Type5Tag;

public class Type5Tests extends NFCTagTests {

    static private Type5Tag mType5Tag;


    static public void setTag(Type5Tag type5Tag) {
        mType5Tag = type5Tag;
        NFCTagTests.setTag(type5Tag);
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readRawData() and writeRawData()
     */
    @Test
    public void testReadWriteRawData() throws STException {
        NFCTagUtils.printTestName("testReadWriteRawData");
        Type5TestReadWriteRawData.run(mType5Tag);
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readSingleBlock() and writeSingleBlock()
     */
    @Test
    public void testReadWriteSingleBlock() throws STException {
        NFCTagUtils.printTestName("testReadWriteSingleBlock");
        Type5TestReadWriteSingleBlock.run(mType5Tag);
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readBlocks() and writeBlocks()
     */
    @Test
    public void testReadWriteBlocks() throws STException {
        NFCTagUtils.printTestName("testReadWriteBlocks");
        Type5TestReadWriteBlocks.run(mType5Tag);
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of the tag when writting too much data in an area.
     * It should not corrupt the data in the following area.
     */
    @Test
    public void testAreaBoundaries() throws STException {
        NFCTagUtils.printTestName("testAreaBoundaries");

        // Test if this tag support multiple area.
        try {
            MultiAreaInterface multiAreaInterface = (MultiAreaInterface) mType5Tag;
            Type5TestAreaBoundaries.run(mType5Tag, multiAreaInterface);

        } catch (ClassCastException e) {
            // Skip this test because this tag doesn't support multiple area
            Assume.assumeTrue(false);
        }
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
        Type5TestIncompleteReadAtEndOfMemory.run(mType5Tag);
    }

    /*
     * TEST DESCRIPTION:
     * Test checking the behavior of readBytes() and readBlocks() when there are less bytes available
     * than requested. We should return what we have been able to read and not raise an exception.
     *
     * This test checks the behavior when the following area is not readable. It is executed only for tags
     * supporting MultiAreaInterface.
     */
    @Test
    public void testIncompleteReadAtAreaBoundary() throws STException {
        NFCTagUtils.printTestName("testIncompleteReadAtAreaBoundary");

        // Test if this tag support multiple area.
        try {
            MultiAreaInterface multiAreaInterface = (MultiAreaInterface) mType5Tag;
            Type5TestIncompleteReadAtAreaBoundary.run(mType5Tag, multiAreaInterface);

        } catch (ClassCastException e) {
            // Skip this test because this tag doesn't support multiple area
            Assume.assumeTrue(false);
        }
    }
}

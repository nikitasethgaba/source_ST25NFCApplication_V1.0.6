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

import org.junit.Assert;

import com.st.st25sdk.NFCTag;
import com.st.st25sdk.STException;
import com.st.st25sdk.type4a.Type4Tag;
import com.st.st25sdk.type5.Type5Tag;

public class NFCTagTestReadWriteBytes {

    static public void run(NFCTag tag) throws STException {

        // Run the test on the first 10 bytes of the memory (or file)
        runTestAtMemoryOffset(tag, 0);

        if (tag instanceof Type5Tag) {
            // Run the test on the last 10 bytes of the memory
            int memSizeInBytes = tag.getMemSizeInBytes();
            runTestAtMemoryOffset(tag, memSizeInBytes - 10);
        }

        if (tag instanceof Type4Tag) {
            // Run the test on the last 10 bytes of first file
            Type4Tag type4Tag = (Type4Tag) tag;

            int[] fileIdList = type4Tag.getFileIdList();
            int maxFileSize = type4Tag.getMaxFileSize(fileIdList[0]);

            runTestAtMemoryOffset(tag, maxFileSize - 10);
        }

        // Invalidate the cache because we have written some dummy data in memory
        NFCTagUtils.invalidateCache(tag);
    }


    static private void runTestAtMemoryOffset(NFCTag tag, int offset) throws STException {
        byte[] readData;
        byte[] expectedResponse;

        byte[] pattern = new byte[] {(byte) 0xA0, (byte) 0xA1, (byte) 0xA2, (byte) 0xA3,
                (byte) 0xA4, (byte) 0xA5, (byte) 0xA6, (byte) 0xA7,
                (byte) 0xA8, (byte) 0xA9};

        // Write the pattern
        tag.writeBytes(offset, pattern);

        // Check that the pattern is correctly written
        readData = tag.readBytes(offset, pattern.length);
        Assert.assertArrayEquals(pattern, readData);

        ///////////////////////////////////////////////////////////

        // Write one byte at offset 5 of the pattern
        byte[] data = new byte[] { 0x55 };

        tag.writeBytes(offset+5, data);

        // Check that the fifth byte contains the expected value
        readData = tag.readBytes(offset+5, data.length);
        Assert.assertArrayEquals(data, readData);

        // Check that the 10 bytes of the pattern are as expected
        expectedResponse = new byte[] {(byte) 0xA0, (byte) 0xA1, (byte) 0xA2, (byte) 0xA3,
                (byte) 0xA4, (byte) 0x55, (byte) 0xA6, (byte) 0xA7,
                (byte) 0xA8, (byte) 0xA9};

        readData = tag.readBytes(offset, pattern.length);
        Assert.assertArrayEquals(expectedResponse, readData);

        ///////////////////////////////////////////////////////////

        // Write 5 bytes at offset 2 of the pattern
        data = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 };

        tag.writeBytes(offset+2, data);

        // Check that data have been written properly
        readData = tag.readBytes(offset+2, data.length);
        Assert.assertArrayEquals(data, readData);

        // Check that the 10 bytes of the pattern are as expected
        expectedResponse = new byte[] {(byte) 0xA0, (byte) 0xA1, (byte) 0x01, (byte) 0x02,
                (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0xA7,
                (byte) 0xA8, (byte) 0xA9};

        readData = tag.readBytes(offset, pattern.length);
        Assert.assertArrayEquals(expectedResponse, readData);

    }

}

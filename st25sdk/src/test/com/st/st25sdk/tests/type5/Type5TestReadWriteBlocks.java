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

import java.util.Random;

import org.junit.Assert;

import com.st.st25sdk.STException;
import com.st.st25sdk.tests.generic.NFCTagUtils;
import com.st.st25sdk.type5.ReadBlockResult;
import com.st.st25sdk.type5.Type5Tag;

public class Type5TestReadWriteBlocks {

    static public void run(Type5Tag type5Tag) throws STException {
        byte[] pattern = new byte[] {(byte) 0xA1, (byte) 0xA2, (byte) 0xA3, (byte) 0xA4};

        if(type5Tag.getMemSizeInBytes() < 256) {
            // This tag doesn't has enough memory to run this test
            return;
        }

        // read and write some blocks at arbitrary addresses and on arbitrary length

        // This test will write:
        // - the blocks 3 to 35
        // - the block 32 to 59

        // Write a pattern in blocks 2 and 60 in order to be sure that the writeBlocks() command
        // didn't corrupt the previous and next blocks
        type5Tag.writeBlocks(2, pattern);
        type5Tag.writeBlocks(60, pattern);

        // On Vicinity tags, a sector is made of 32 blocks so this write of 33 blocks will cross a sector boundary
        readWriteBlocks(type5Tag, 3, 33);

        readWriteBlocks(type5Tag, 32, 28);

        // Check that the two patterns are unchanged
        ReadBlockResult result = type5Tag.readBlocks(2, 1);
        Assert.assertArrayEquals(pattern, result.data);

        result = type5Tag.readBlocks(60, 1);
        Assert.assertArrayEquals(pattern, result.data);

        // Invalidate the cache because we have written some dummy data in memory
        NFCTagUtils.invalidateCache(type5Tag);
    }

    static private void readWriteBlocks(Type5Tag type5Tag, int firstBlockAddress, int numberOfBlocks) throws STException {

        // Allocate some random data
        byte[] randomData = new byte[numberOfBlocks * type5Tag.getBlockSizeInBytes()];
        new Random().nextBytes(randomData);

        // Write the blocks into the tag
        type5Tag.writeBlocks(firstBlockAddress, randomData);

        // Read the blocks from the tag
        ReadBlockResult readBlockResult = type5Tag.readBlocks(firstBlockAddress, numberOfBlocks);
        byte[] dataRead = readBlockResult.data;

        // Check the blocks content
        Assert.assertArrayEquals(randomData,dataRead);
    }

}

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

import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Assume;

import com.st.st25sdk.STException;
import com.st.st25sdk.type5.Type5Tag;


public class Type5TestReadWriteSingleBlock {

    static public void run(Type5Tag type5Tag) throws STException {

        Assume.assumeTrue("This tag doesn't have enough memory to run the test", type5Tag.getMemSizeInBytes() >= 256);

        int nbrOfBytesPerBlock = type5Tag.getBlockSizeInBytes();
        byte[] dataRead;
        byte[] readBlock1 = new byte[nbrOfBytesPerBlock];
        byte[] readBlock2 = new byte[nbrOfBytesPerBlock];
        byte[] readBlock3 = new byte[nbrOfBytesPerBlock];
        byte blockAddressBlock1 = 0;
        byte blockAddressBlock2 = 17;   // Arbitrary values
        byte blockAddressBlock3 = 43;

        // Allocate some blocks containing random data
        byte[] block1 = new byte[nbrOfBytesPerBlock];
        new Random().nextBytes(block1);

        byte[] block2 = new byte[nbrOfBytesPerBlock];
        new Random().nextBytes(block2);

        byte[] block3 = new byte[nbrOfBytesPerBlock];
        new Random().nextBytes(block3);

        type5Tag.writeSingleBlock(blockAddressBlock1, block1);
        type5Tag.writeSingleBlock(blockAddressBlock2, block2);
        type5Tag.writeSingleBlock(blockAddressBlock3, block3);

        // Read the blocks
        dataRead = type5Tag.readSingleBlock(blockAddressBlock1);
        readBlock1 = Arrays.copyOfRange(dataRead, 1, dataRead.length);    // Skip the status Byte

        dataRead = type5Tag.readSingleBlock(blockAddressBlock2);
        readBlock2 = Arrays.copyOfRange(dataRead, 1, dataRead.length);    // Skip the status Byte

        dataRead = type5Tag.readSingleBlock(blockAddressBlock3);
        readBlock3 = Arrays.copyOfRange(dataRead, 1, dataRead.length);    // Skip the status Byte

        // Check the blocks content
        Assert.assertArrayEquals(block1, readBlock1);
        Assert.assertArrayEquals(block2, readBlock2);
        Assert.assertArrayEquals(block3, readBlock3);
    }

}

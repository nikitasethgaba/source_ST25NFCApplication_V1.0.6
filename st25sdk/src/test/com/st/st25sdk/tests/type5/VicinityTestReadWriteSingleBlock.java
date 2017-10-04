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
import com.st.st25sdk.type5.STVicinityTag;


public class VicinityTestReadWriteSingleBlock {

    static public void run(STVicinityTag vicinityTag) throws STException {
        byte[] dataRead;
        int nbrOfBytesPerBlock = vicinityTag.getBlockSizeInBytes();
        byte[] readBlock1 = new byte[nbrOfBytesPerBlock];
        byte[] readBlock2 = new byte[nbrOfBytesPerBlock];
        byte[] readBlock3 = new byte[nbrOfBytesPerBlock];
        byte[] blockAddressBlock1 = new byte[]{0x00, 0x00};
        byte[] blockAddressBlock2 = new byte[]{0x00, 0x07};     // Arbitrary value
        byte[] blockAddressBlock3 = new byte[]{0x00, 0x13};     // Arbitrary value

        // Allocate some blocks containing random data
        byte[] block1 = new byte[nbrOfBytesPerBlock];
        new Random().nextBytes(block1);

        byte[] block2 = new byte[nbrOfBytesPerBlock];
        new Random().nextBytes(block2);

        byte[] block3 = new byte[nbrOfBytesPerBlock];
        new Random().nextBytes(block3);

        vicinityTag.writeSingleBlock(blockAddressBlock1, block1);
        vicinityTag.writeSingleBlock(blockAddressBlock2, block2);
        vicinityTag.writeSingleBlock(blockAddressBlock3, block3);

        // Read the blocks
        dataRead = vicinityTag.readSingleBlock(blockAddressBlock1);
        System.arraycopy(dataRead, 1, readBlock1, 0, readBlock1.length);    // Skip the status Byte

        dataRead = vicinityTag.readSingleBlock(blockAddressBlock2);
        System.arraycopy(dataRead, 1, readBlock2, 0, readBlock2.length);    // Skip the status Byte

        dataRead = vicinityTag.readSingleBlock(blockAddressBlock3);
        System.arraycopy(dataRead, 1, readBlock3, 0, readBlock3.length);    // Skip the status Byte

        // Check the blocks content
        Assert.assertArrayEquals(block1, readBlock1);
        Assert.assertArrayEquals(block2, readBlock2);
        Assert.assertArrayEquals(block3, readBlock3);

    }

}

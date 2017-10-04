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

package com.st.st25sdk.tests.type4;

import static com.st.st25sdk.Helper.printHexByteArray;
import static org.junit.Assert.assertArrayEquals;

import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.tests.generic.NFCTagTests;
import com.st.st25sdk.tests.generic.NFCTagUtils;
import com.st.st25sdk.type4a.STType4Tag;

/**
 * Created on 11/25/16.
 */

public class Type4ATestReadWrite36Bytes {
    /**
     * Test checking that the functions reading and writing raw data in memory are working
     *
     * @throws STException
     */
    static public void run(STType4Tag type4Tag) throws STException {

        if(type4Tag.getMemSizeInBytes() < 300) {
            // This tag doesn't has enough memory to run this test
            return;
        }

        STLog.i("testMemoryReadWriteFunctions: 36 bytes");
        writeAndReadBuffer(type4Tag, 36);

        //282 bytes raised the issue ......246+36
        STLog.i("testMemoryReadWriteFunctions: MaxReadAPDU+36 bytes");
        int maxReadSize = type4Tag.getApduMaxReadSize();
        writeAndReadBuffer(type4Tag, maxReadSize+36);

        STLog.i("testMemoryReadWriteFunctions: 34 bytes");
        writeAndReadBuffer(type4Tag, 34);
        //280 bytes raised the issue ......246+34
        STLog.i("testMemoryReadWriteFunctions: MaxReadAPDU+34 bytes");
        maxReadSize = type4Tag.getApduMaxReadSize();
        writeAndReadBuffer(type4Tag, maxReadSize+34);


    }

    private static void writeAndReadBuffer(STType4Tag type4Tag, int size) throws STException {
        byte[] memoryData;
        byte[] dataRead;
        int fileID = 0x01;

        STLog.i("writeAndReadBuffer");


        memoryData = new byte[size];
        for (int i = 0; i < size; i++) memoryData[i] = (byte) ((byte) i % 36);

        // Fill the File with the random data
        type4Tag.writeBytes(fileID, 0, memoryData);

        type4Tag.invalidateCache();
        // Read the File back
        dataRead = type4Tag.readBytes(fileID, 0, size);

        if (NFCTagTests.debug) {
            printHexByteArray("dataRead", dataRead);
        }
        assertArrayEquals(memoryData, dataRead);

        // Invalidate the cache because we have written some dummy data in memory
        NFCTagUtils.invalidateCache(type4Tag);
    }

}

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
import static com.st.st25sdk.STException.STExceptionCode.BAD_PARAMETER;
import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.tests.generic.NFCTagTestReadWriteRawData;
import com.st.st25sdk.tests.generic.NFCTagTests;
import com.st.st25sdk.tests.generic.NFCTagUtils;
import com.st.st25sdk.type4a.ControlTlv;
import com.st.st25sdk.type4a.Type4Tag;


public class Type4TestReadWriteRawData {
    static private byte[] mTagMemory;

    /**
     * Test checking that the functions reading and writing raw data in memory are working
     * @throws STException
     */
    static public void run(Type4Tag type4Tag) throws STException {
        byte[] randomData;
        byte[] dataRead;
        int ndefMaxSizeInBytes;

        STLog.i("testMemoryReadWriteFunctions");

        // NDEF File max size is obtained from CCFile
        ControlTlv controlTlv = type4Tag.getCCTlv();
        ndefMaxSizeInBytes = controlTlv.getMaxFileSize();

        // Allocate as many random data as room in NDEF file
        randomData = NFCTagUtils.allocateRandomData(ndefMaxSizeInBytes);

        // Select NDEF File
        type4Tag.selectNdef();

        // Fill the File with the random data
        writeRandomRawData(type4Tag, randomData);

        // Read the File back
        dataRead = readRandomRawData(type4Tag, ndefMaxSizeInBytes);

        if(NFCTagTests.debug) {
            printHexByteArray("dataRead", dataRead);
        }

        // Now check that the content read is the same as the content written

        // Special behavior for the first 2 bytes
        if((randomData[0] == dataRead[0]) && (randomData[1] == dataRead[1])) {
            assertArrayEquals(randomData, dataRead);
        } else {
            STLog.e("!!! WARNING !!!");
            STLog.e("First two bytes are incorrect !");

            // Compare all the bytes but the first two ones
            byte[] randomData2 = new byte[randomData.length-2];
            byte[] dataRead2 = new byte[dataRead.length-2];

            System.arraycopy(randomData, 2, randomData2, 0, randomData2.length);
            System.arraycopy(dataRead, 2, dataRead2, 0, randomData2.length);

            assertArrayEquals(randomData2, dataRead2);
        }

        // Invalidate the cache because we have written some dummy data in memory
        NFCTagUtils.invalidateCache(type4Tag);
    }



    /**
     * Write some random data into the tag.
     * @throws STException
     */
    static public void writeRandomRawData(Type4Tag type4Tag, byte[] randomData) throws STException {
        int dstByteAddress = 0;
        int dataLength;

        STLog.i("writeRandomRawData");

        if(randomData.length <= 32) {
            throw new STException(BAD_PARAMETER);
        }

        // Create an inputStream that we will use to read the data 4 by 4
        ByteArrayInputStream inputStream = new ByteArrayInputStream(randomData);

        // The first 5 Bytes (arbitrary value) are written through writeRawData().
        dataLength = 5;
        NFCTagTestReadWriteRawData.writeDataByRawDataFunction(inputStream, dataLength, type4Tag, dstByteAddress);
        dstByteAddress += dataLength;

        // The next 63 Bytes (arbitrary value) are written through writeRawData().
        dataLength = 63;
        NFCTagTestReadWriteRawData.writeDataByRawDataFunction(inputStream, dataLength, type4Tag, dstByteAddress);
        dstByteAddress += dataLength;

        // All the remaining Bytes in eeprom are written through another
        //  call to writeRawData()
        dataLength = randomData.length - dstByteAddress;
        NFCTagTestReadWriteRawData.writeDataByRawDataFunction(inputStream, dataLength, type4Tag, dstByteAddress);
        dstByteAddress += dataLength;

        STLog.i("Tag memory written");
    }

    /**
     * Read the tag memory
     * @throws STException
     */
    static public byte[] readRandomRawData(Type4Tag type4Tag, int ndefMaxSizeInBytes) throws STException {
        int offsetInBytes;
        int lengthInBytes;

        STLog.i("readRandomRawData");

        // Creates a new byte array output stream used to store the data read from the data
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(ndefMaxSizeInBytes);

        // The first 17 bytes (arbitrary value) are read thanks to a call to readRawData()
        offsetInBytes = 0;
        lengthInBytes = 17;
        NFCTagTestReadWriteRawData.readDataByRawDataFunction(type4Tag, offsetInBytes, lengthInBytes, outputStream);

        // All the remaining Bytes (= ndefMaxSizeInBytes - 17) are read through another call to readRawData()
        offsetInBytes += lengthInBytes;
        lengthInBytes = (ndefMaxSizeInBytes - lengthInBytes);
        NFCTagTestReadWriteRawData.readDataByRawDataFunction(type4Tag, offsetInBytes, lengthInBytes, outputStream);

        // Extract the Byte Array from the outputStream
        byte[] data = outputStream.toByteArray();

        return data;
    }

}

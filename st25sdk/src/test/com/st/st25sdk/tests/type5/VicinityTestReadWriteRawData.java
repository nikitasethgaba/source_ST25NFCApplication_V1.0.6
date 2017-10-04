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

import com.st.st25sdk.Helper;
import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.tests.generic.NFCTagTestReadWriteRawData;
import com.st.st25sdk.tests.generic.NFCTagTests;
import com.st.st25sdk.tests.generic.NFCTagUtils;
import com.st.st25sdk.type5.STVicinityTag;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static com.st.st25sdk.Helper.printHexByteArray;
import static com.st.st25sdk.STException.STExceptionCode.BAD_PARAMETER;
import static com.st.st25sdk.STException.STExceptionCode.INVALID_DATA;
import static org.junit.Assert.assertArrayEquals;

public class VicinityTestReadWriteRawData {
    static private byte[] mTagMemory;


    /**
     * Test checking that the functions reading and writing raw data in memory are working
     * @throws STException
     */
    static public void run(STVicinityTag vicinityTag) throws STException {
        byte[] randomData;
        byte[] dataRead;
        int sizeInBytes = vicinityTag.getMemSizeInBytes();

        STLog.i("testMemoryReadWriteFunctions");

        // Current implementation of readRandomRawData() assumes that the tag has at least 256 Bytes of memory
        if(sizeInBytes < 256) {
            // This tag doesn't has enough memory to run this test
            throw new STException(INVALID_DATA);
        }

        // Allocate as many random data as EEPROM size
        randomData = NFCTagUtils.allocateRandomData(sizeInBytes);

        // Fill the tag memory with the random data
        writeRandomRawData(vicinityTag, randomData);

        // Read the tag memory
        dataRead = readRandomRawData(vicinityTag);

        if(NFCTagTests.debug) {
            printHexByteArray("dataRead", dataRead);
        }

        // Now check that the content read is the same as the content written
        assertArrayEquals(randomData, dataRead);

        // Invalidate the cache because we have written some dummy data in memory
        NFCTagUtils.invalidateCache(vicinityTag);
    }


    /**
     * During this test, we will write some random data into the tag.
     * Write will be done in 2 ways:
     *      - through writeRawData()
     *      - through writeSingleBlock()
     *
     *  NB: This is NOT a test but part of a test
     *
     * @throws STException
     */
    static public void writeRandomRawData(STVicinityTag vicinityTag, byte[] randomData) throws STException {
        int dstByteAddress = 0;
        int dataLength;

        STLog.i("writeRandomRawData");

        if(randomData.length <= 32) {
            throw new STException(BAD_PARAMETER);
        }

        // Create an inputStream that we will use to read the data 4 by 4
        ByteArrayInputStream inputStream = new ByteArrayInputStream(randomData);

        // The first 32 Bytes are written through writeRawData() in the following manner;
        //      - Write the first 5 Bytes
        //      - Write the next 3 Bytes
        //      - Write the next 14 Bytes
        //      - Write the next 10 Bytes
        // The next 32 Bytes are written through writeSingleBlock()
        // All the remaining Bytes in eeprom (= randomData.length - 64) are written through writeRawData()
        //
        // NB: We intentionally choose some lengths that are not a multiple of block size.

        dataLength = 5;
        NFCTagTestReadWriteRawData.writeDataByRawDataFunction(inputStream, dataLength, vicinityTag, dstByteAddress);
        dstByteAddress += dataLength;

        dataLength = 3;
        NFCTagTestReadWriteRawData.writeDataByRawDataFunction(inputStream, dataLength, vicinityTag, dstByteAddress);
        dstByteAddress += dataLength;

        dataLength = 14;
        NFCTagTestReadWriteRawData.writeDataByRawDataFunction(inputStream, dataLength, vicinityTag, dstByteAddress);
        dstByteAddress += dataLength;

        dataLength = 10;
        NFCTagTestReadWriteRawData.writeDataByRawDataFunction(inputStream, dataLength, vicinityTag, dstByteAddress);
        dstByteAddress += dataLength;

        dataLength = 32;
        writeDataBySingleBlocks(inputStream, (dataLength / vicinityTag.getBlockSizeInBytes()), vicinityTag, dstByteAddress);
        dstByteAddress += dataLength;

        // We have written 64 Bytes so far.
        // Write all the remaining bytes through the writeRawData() function
        dataLength = randomData.length - 64;
        NFCTagTestReadWriteRawData.writeDataByRawDataFunction(inputStream, dataLength, vicinityTag, dstByteAddress);
        dstByteAddress += dataLength;

        STLog.i("Tag memory written");
    }



    /**
     * Write some raw data in a tag by using writeSingleBlock() function
     *
     * @param srcStream : ByteArrayInputStream containing the source data
     * @param nbrOfBlocks: Number of blocks to write into the tag
     * @param destTag : Destination tag
     */
    static public void writeDataBySingleBlocks(ByteArrayInputStream srcStream, int nbrOfBlocks, STVicinityTag destTag, int destByteAddress) throws STException {
        byte[] blockAddress = new byte[2];
        // Address in nbr of blocks
        int address = (destByteAddress / destTag.getBlockSizeInBytes());

        while(nbrOfBlocks > 0) {
            byte[] block;

            // Read a block
            block = Helper.readNextBlockOfByteArrayInputStream(srcStream, destTag.getBlockSizeInBytes());

            if(NFCTagTests.debug) {
                printHexByteArray("writeDataBySingleBlocks", block);
            }

            blockAddress[1] = (byte) (address & 0xFF);
            blockAddress[0] = (byte) ((address >> 8) & 0xFF);

            // And write it to the tag
            destTag.writeSingleBlock(blockAddress, block);

            nbrOfBlocks--;
            address++;
        }

    }

    /**
     * During this test, the tag memory will be read in different ways:
     * - Read single block
     * - Read multiple blocks
     * - Read raw data
     *
     *  NB: This is NOT a test but part of a test
     *
     * @throws STException
     */
    static public byte[] readRandomRawData(STVicinityTag vicinityTag) throws STException {
        int offsetInBytes;
        int lengthInBytes;

        STLog.i("readRandomRawData");

        int memSizeInBytes = vicinityTag.getMemSizeInBytes();

        // Creates a new byte array output stream used to store the data read from the data
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(memSizeInBytes);

        // The first 100 Bytes are read through readMultipleBlock().
        //
        // The next 64 Bytes are read through readSingleBlock()
        //
        // The next 32 Bytes data are read through readRawData() in the following manner
        //  - read 7 Bytes
        //  - read 5 Bytes
        //  - read 18 Bytes
        //  - read 2 Bytes
        //
        // All the remaining Bytes (= memSizeInBytes - 196) are read through readRawData()
        //
        // NB1: For readMultipleBlock() we choose a size > 128 because the size is on one byte and we
        //      want to check that we don't get issue with Byte being considered as negative.
        // NB2: For readRawData(), we intentionally choose some lengths that are not a multiple of block size.

        offsetInBytes = 0;
        lengthInBytes = 100;
        readDataByMultipleBlocks(vicinityTag, offsetInBytes, lengthInBytes, outputStream);

        offsetInBytes += lengthInBytes;
        lengthInBytes = 64;
        readDataBySingleBlocks(vicinityTag, offsetInBytes, lengthInBytes, outputStream);

        offsetInBytes += lengthInBytes;
        lengthInBytes = 7;
        NFCTagTestReadWriteRawData.readDataByRawDataFunction(vicinityTag, offsetInBytes, lengthInBytes, outputStream);

        offsetInBytes += lengthInBytes;
        lengthInBytes = 5;
        NFCTagTestReadWriteRawData.readDataByRawDataFunction(vicinityTag, offsetInBytes, lengthInBytes, outputStream);

        offsetInBytes += lengthInBytes;
        lengthInBytes = 18;
        NFCTagTestReadWriteRawData.readDataByRawDataFunction(vicinityTag, offsetInBytes, lengthInBytes, outputStream);

        offsetInBytes += lengthInBytes;
        lengthInBytes = 2;
        NFCTagTestReadWriteRawData.readDataByRawDataFunction(vicinityTag, offsetInBytes, lengthInBytes, outputStream);

        offsetInBytes += lengthInBytes;
        lengthInBytes = (memSizeInBytes - 196);
        NFCTagTestReadWriteRawData.readDataByRawDataFunction(vicinityTag, offsetInBytes, lengthInBytes, outputStream);

        // Extract the Byte Array from the outputStream
        byte[] data = outputStream.toByteArray();

        return data;
    }

    /**
     * Read data from the tag thanks to readSingleBlock() function
     * @param srcVicinityTag
     * @param offsetInBytes
     * @param lengthInBytes
     * @param dstOutputStream
     * @throws STException
     */
    static public void readDataBySingleBlocks(STVicinityTag srcVicinityTag, int offsetInBytes, int lengthInBytes, ByteArrayOutputStream dstOutputStream) throws STException {
        byte[] blockAddress = new byte[2];
        int nbrOfBytesPerBlock = srcVicinityTag.getBlockSizeInBytes();

        // offsetInBytes and lengthInBytes should be multiple of block size
        if((offsetInBytes % nbrOfBytesPerBlock) != 0) {
            throw new STException(BAD_PARAMETER);
        }

        if((lengthInBytes % nbrOfBytesPerBlock) != 0) {
            throw new STException(BAD_PARAMETER);
        }

        int firstBlock = offsetInBytes / nbrOfBytesPerBlock;
        int lengthInBlocks = lengthInBytes / nbrOfBytesPerBlock;

        for (int block = firstBlock; block < (firstBlock + lengthInBlocks); block++) {

            byte[] blockData = new byte[nbrOfBytesPerBlock];

            blockAddress[1] = (byte) (block & 0xFF);
            blockAddress[0] = (byte) ((block >> 8) & 0xFF);

            byte[] dataRead = srcVicinityTag.readSingleBlock(blockAddress);

            // dataRead should contain one status Byte + one block
            if(dataRead == null || dataRead.length != (1 + nbrOfBytesPerBlock)){
                throw new STException(INVALID_DATA);
            }

            // Skip the first status byte and you get the block data
            System.arraycopy(dataRead, 1, blockData, 0, blockData.length);

            // Write the block to the OutputStream
            dstOutputStream.write(blockData, 0, blockData.length);

            if(NFCTagTests.debug) {
                printHexByteArray("readDataBySingleBlocks", blockData);
            }
        }
    }

    /**
     * Read data from the tag thanks to readMultipleBlock() function
     * @param srcVicinityTag
     * @param offsetInBytes
     * @param lengthInBytes
     * @param dstOutputStream
     * @throws STException
     */
    static public void readDataByMultipleBlocks(STVicinityTag srcVicinityTag, int offsetInBytes, int lengthInBytes, ByteArrayOutputStream dstOutputStream) throws STException {
        byte[] blockAddress = new byte[2];
        int nbrOfBytesPerBlock = srcVicinityTag.getBlockSizeInBytes();

        // offsetInBytes and lengthInBytes should be multiple of block size
        if((offsetInBytes % nbrOfBytesPerBlock) != 0) {
            throw new STException(BAD_PARAMETER);
        }

        if((lengthInBytes % nbrOfBytesPerBlock) != 0) {
            throw new STException(BAD_PARAMETER);
        }

        int offsetInBlocks = offsetInBytes / nbrOfBytesPerBlock;
        int lengthInBlocks = lengthInBytes / nbrOfBytesPerBlock;

        blockAddress[1] = (byte) (offsetInBlocks & 0xFF);
        blockAddress[0] = (byte) ((offsetInBlocks >> 8) & 0xFF);

        // WARNING: When requesting N blocks to the readMultipleBlock command, it returns N+1 blocks.
        // So we should ask (lengthInBlocks-1)...
        byte[] dataRead = srcVicinityTag.readMultipleBlock(blockAddress, (byte) (lengthInBlocks-1) );

        // dataRead contains a status byte + the requested data
        // Check that the expected number of bytes was read
        if(dataRead == null || (dataRead.length - 1) != lengthInBytes){
            throw new STException(INVALID_DATA);
        }

        // Skip the status byte and copy the data to a new buffer
        byte[] data = new byte[lengthInBytes];
        System.arraycopy(dataRead, 1, data, 0, data.length);

        // Write the data to the OutputStream
        dstOutputStream.write(data, 0, data.length);


        if(NFCTagTests.debug) {
            printHexByteArray("readDataByMultipleBlocks", data);
        }
    }


}

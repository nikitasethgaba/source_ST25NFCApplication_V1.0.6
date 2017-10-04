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

import static com.st.st25sdk.Helper.printHexByteArray;
import static com.st.st25sdk.STException.STExceptionCode.BAD_PARAMETER;
import static com.st.st25sdk.STException.STExceptionCode.CMD_FAILED;
import static com.st.st25sdk.STException.STExceptionCode.INVALID_DATA;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assume.assumeTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.st.st25sdk.Helper;
import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.tests.generic.NFCTagTestReadWriteRawData;
import com.st.st25sdk.tests.generic.NFCTagTests;
import com.st.st25sdk.tests.generic.NFCTagUtils;
import com.st.st25sdk.type5.M24LR04KTag;
import com.st.st25sdk.type5.Type5Tag;

/**
 * This automatic test will test the following functions:
 * - readBytes
 * - writeBytes
 * - readSingleBlock
 * - writeSingleBlock
 * - readMultipleBlock
 * - extendedReadMultipleBlock  (if the tag supports it)
 * - writeMultipleBlock         (if the tag supports it)
 * - extendedWriteMultipleBlock (if the tag supports it)
 *
 * NB: readBlocks and writeBlocks are tested by another test (see Type5TestReadWriteBlocks)
 */
public class Type5TestReadWriteRawData {
    static private byte[] mTagMemory;


    /**
     * Test checking that the functions reading and writing raw data in memory are working
     * @throws STException
     */
    static public void run(Type5Tag type5Tag) throws STException {
        byte[] randomData;
        byte[] dataRead;
        int sizeInBytes = type5Tag.getMemSizeInBytes();

        STLog.i("testMemoryReadWriteFunctions");

        // Current implementation of readRandomRawData() assumes that the tag has at least 256 Bytes of memory
        assumeTrue(sizeInBytes >= 256);

        // Allocate as many random data as EEPROM size
        randomData = NFCTagUtils.allocateRandomData(sizeInBytes);

        // Fill the tag memory with the random data
        writeRandomRawData(type5Tag, randomData);

        // Read the tag memory
        dataRead = readRandomRawData(type5Tag);

        if (NFCTagTests.debug) {
            printHexByteArray("dataRead", dataRead);
        }

        // Now check that the content read is the same as the content written
        assertArrayEquals(randomData, dataRead);

        // Invalidate the cache because we have written some dummy data in memory
        NFCTagUtils.invalidateCache(type5Tag);
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
    static public void writeRandomRawData(Type5Tag type5Tag, byte[] randomData) throws STException {
        int dstByteAddress = 0;
        int dataLength;

        STLog.i("writeRandomRawData");

        if (randomData.length <= 32) {
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
        NFCTagTestReadWriteRawData.writeDataByRawDataFunction(inputStream, dataLength, type5Tag, dstByteAddress);
        dstByteAddress += dataLength;

        dataLength = 3;
        NFCTagTestReadWriteRawData.writeDataByRawDataFunction(inputStream, dataLength, type5Tag, dstByteAddress);
        dstByteAddress += dataLength;

        dataLength = 14;
        NFCTagTestReadWriteRawData.writeDataByRawDataFunction(inputStream, dataLength, type5Tag, dstByteAddress);
        dstByteAddress += dataLength;

        dataLength = 10;
        NFCTagTestReadWriteRawData.writeDataByRawDataFunction(inputStream, dataLength, type5Tag, dstByteAddress);
        dstByteAddress += dataLength;

        dataLength = 32;
        writeDataBySingleBlocks(inputStream, (dataLength / type5Tag.getBlockSizeInBytes()), type5Tag, dstByteAddress);
        dstByteAddress += dataLength;

        // We have written 64 Bytes so far.
        // Write all the remaining bytes through the writeRawData() function
        dataLength = randomData.length - 64;
        NFCTagTestReadWriteRawData.writeDataByRawDataFunction(inputStream, dataLength, type5Tag, dstByteAddress);
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
    static public void writeDataBySingleBlocks(ByteArrayInputStream srcStream, int nbrOfBlocks, Type5Tag destTag, int destByteAddress) throws STException {
        int blockAddress = (destByteAddress / destTag.getBlockSizeInBytes());

        while(nbrOfBlocks > 0) {
            byte[] block;

            // Read a block
            block = Helper.readNextBlockOfByteArrayInputStream(srcStream, destTag.getBlockSizeInBytes());

            if(NFCTagTests.debug) {
                printHexByteArray("writeDataBySingleBlocks", block);
            }

            if (blockAddress > 255) {
                // This function cannot write beyond the block 255
                throw new STException(BAD_PARAMETER);
            }

            // And write it to the tag
            destTag.writeSingleBlock((byte) blockAddress, block);

            nbrOfBlocks--;
            blockAddress++;
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
    static public byte[] readRandomRawData(Type5Tag type5Tag) throws STException {
        int offsetInBytes;
        int lengthInBytes;

        STLog.i("readRandomRawData");

        int memSizeInBytes = type5Tag.getMemSizeInBytes();

        // Creates a new byte array output stream used to store the data read from the data
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(memSizeInBytes);

        // The first 132 Bytes are read through readMultipleBlock().
        //
        // The next 32 Bytes are read through readSingleBlock()
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


        // NB: We have to do a special procedure for the tag M24LR04E. This tag has sectors of 128 Bytes so
        //     it is not possible to write 132 bytes in one shot because it crosses a sector boundary (which is not allowed).
        //     So we split the write in 2 commands in order to workaround this issue.

        if (type5Tag instanceof M24LR04KTag) {
            offsetInBytes = 0;
            lengthInBytes = 128;
            readDataByMultipleBlocks(type5Tag, offsetInBytes, lengthInBytes, outputStream);

            offsetInBytes += lengthInBytes;
            lengthInBytes = 4;
            readDataByMultipleBlocks(type5Tag, offsetInBytes, lengthInBytes, outputStream);
        } else {
            offsetInBytes = 0;
            lengthInBytes = 132;
            readDataByMultipleBlocks(type5Tag, offsetInBytes, lengthInBytes, outputStream);
        }

        offsetInBytes += lengthInBytes;
        lengthInBytes = 32;
        readDataBySingleBlocks(type5Tag, offsetInBytes, lengthInBytes, outputStream);

        offsetInBytes += lengthInBytes;
        lengthInBytes = 7;
        NFCTagTestReadWriteRawData.readDataByRawDataFunction(type5Tag, offsetInBytes, lengthInBytes, outputStream);

        offsetInBytes += lengthInBytes;
        lengthInBytes = 5;
        NFCTagTestReadWriteRawData.readDataByRawDataFunction(type5Tag, offsetInBytes, lengthInBytes, outputStream);

        offsetInBytes += lengthInBytes;
        lengthInBytes = 18;
        NFCTagTestReadWriteRawData.readDataByRawDataFunction(type5Tag, offsetInBytes, lengthInBytes, outputStream);

        offsetInBytes += lengthInBytes;
        lengthInBytes = 2;
        NFCTagTestReadWriteRawData.readDataByRawDataFunction(type5Tag, offsetInBytes, lengthInBytes, outputStream);

        offsetInBytes += lengthInBytes;
        lengthInBytes = (memSizeInBytes - 196);
        NFCTagTestReadWriteRawData.readDataByRawDataFunction(type5Tag, offsetInBytes, lengthInBytes, outputStream);

        // Extract the Byte Array from the outputStream
        byte[] data = outputStream.toByteArray();

        return data;
    }

    /**
     * Read data from the tag thanks to readSingleBlock() function
     * @param srcType5Tag
     * @param offsetInBytes
     * @param lengthInBytes
     * @param dstOutputStream
     * @throws STException
     */
    static public void readDataBySingleBlocks(Type5Tag srcType5Tag, int offsetInBytes, int lengthInBytes, ByteArrayOutputStream dstOutputStream) throws STException {
        int nbrOfBytesPerBlock = srcType5Tag.getBlockSizeInBytes();

        // offsetInBytes and lengthInBytes should be multiple of block size
        if ((offsetInBytes % nbrOfBytesPerBlock) != 0) {
            throw new STException(BAD_PARAMETER);
        }

        if ((lengthInBytes % nbrOfBytesPerBlock) != 0) {
            throw new STException(BAD_PARAMETER);
        }

        int firstBlock = offsetInBytes / nbrOfBytesPerBlock;
        int lengthInBlocks = lengthInBytes / nbrOfBytesPerBlock;

        for (int block = firstBlock; block < (firstBlock + lengthInBlocks); block++) {

            // In current version, blockAddress cannot exceed 255
            if(block > 255) {
                throw new STException(CMD_FAILED);
            }

            byte blockNumber = (byte) block;
            byte[] blockData = new byte[nbrOfBytesPerBlock];

            byte[] dataRead = srcType5Tag.readSingleBlock(blockNumber);

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
     * @param srcType5Tag
     * @param offsetInBytes
     * @param lengthInBytes
     * @param dstOutputStream
     * @throws STException
     */
    static public void readDataByMultipleBlocks(Type5Tag srcType5Tag, int offsetInBytes, int lengthInBytes, ByteArrayOutputStream dstOutputStream) throws STException {
        int nbrOfBytesPerBlock = srcType5Tag.getBlockSizeInBytes();

        // offsetInBytes and lengthInBytes should be multiple of block size
        if ((offsetInBytes % nbrOfBytesPerBlock) != 0) {
            throw new STException(BAD_PARAMETER);
        }

        if ((lengthInBytes % nbrOfBytesPerBlock) != 0) {
            throw new STException(BAD_PARAMETER);
        }

        int offsetInBlocks = offsetInBytes / nbrOfBytesPerBlock;
        int lengthInBlocks = lengthInBytes / nbrOfBytesPerBlock;

        // In current version, we can only read blocks accessible with a one byte address
        if ((offsetInBlocks + lengthInBlocks) > 255) {
            throw new STException(CMD_FAILED);
        }

        // WARNING: When requesting N blocks to the readMultipleBlock command, it returns N+1 blocks.
        // So we should ask (lengthInBlocks-1)...
        byte[] dataRead = srcType5Tag.readMultipleBlock((byte) offsetInBlocks, (byte) (lengthInBlocks-1) );

        // dataRead contains a status byte + the requested data
        // Check that the expected number of bytes was read
        if (dataRead == null || (dataRead.length - 1) != lengthInBytes) {
            throw new STException(INVALID_DATA);
        }

        // Skip the status byte and copy the data to a new buffer
        byte[] data = new byte[lengthInBytes];
        System.arraycopy(dataRead, 1, data, 0, data.length);

        // Write the data to the OutputStream
        dstOutputStream.write(data, 0, data.length);

        if (NFCTagTests.debug) {
            printHexByteArray("readDataByMultipleBlocks", data);
        }
    }

}

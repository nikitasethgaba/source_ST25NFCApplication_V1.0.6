package com.st.st25sdk.tests.type5.st25dv;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import com.st.st25sdk.RFReaderInterface.TransceiveMode;
import com.st.st25sdk.STException;
import com.st.st25sdk.STException.STExceptionCode;
import com.st.st25sdk.STLog;
import com.st.st25sdk.tests.generic.NFCTagUtils;
import com.st.st25sdk.type5.ReadBlockResult;
import com.st.st25sdk.type5.ST25DVTag;

public class ST25DVExtendedWriteMultipleBlockTest {

    static ST25DVTag mST25DVTag = null;

    static public void run(ST25DVTag st25DVTag) throws STException {
        mST25DVTag = st25DVTag;

        //////////////////////////////////////////////////////////////////
        STLog.i("Test ST25DV extendedWriteMultipleBlock");

        assumeTrue("Tag must have a memory size > 8Kb to test extended commands", (mST25DVTag.getMemSizeInBytes() >= 8192));

        final int firstBlockAddress = 0x0180;

        // Test wrong parameters
        try {
            mST25DVTag.extendedWriteMultipleBlock(firstBlockAddress, null);
            fail("Expecting exception for null buffer");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            mST25DVTag.extendedWriteMultipleBlock(firstBlockAddress, 2, null);
            fail("Expecting exception for null buffer");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            mST25DVTag.extendedWriteMultipleBlock(firstBlockAddress, 02, null, (byte) 02);
            fail("Expecting exception for null buffer");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        // Allocate 4 blocks of random data
        byte[] randomData = NFCTagUtils.allocateRandomData(4 * mST25DVTag.getBlockSizeInBytes());

        // Check that command is well-formed
        mST25DVTag.getReaderInterface().setTransceiveMode(TransceiveMode.EVAL);
        try {
            mST25DVTag.extendedWriteMultipleBlock(firstBlockAddress, 3, randomData, (byte) 0x02);
            fail("Transceive in EVAL mode should throw an exception");
        } catch (STException e) {
            if (e.getError() == STExceptionCode.TRANSCEIVE_EVAL_MODE) {
                byte[] expectedCommand = ArrayUtils.addAll(new byte[]{
                        0x02,           // Flag
                        (byte) 0x34,    // Cmd
                        (byte) 0x80,    // Block Address LSB
                        (byte) 0x01,    // Block Address MSB
                        (byte) 0x03,    // Number of Blocks LSB
                        (byte) 0x00},   // Number of Blocks MSB
                        randomData);    // Data
                assertArrayEquals("Transceive command not as expected", expectedCommand, e.getErrorData());
            } else {
                fail("Unexpected exception");
            }
        } finally {
            mST25DVTag.getReaderInterface().setTransceiveMode(TransceiveMode.NORMAL);
        }

        final int readTestSizeInBlocks = 6;

        // Read readTestSizeInBlocks of original data from tag
        ReadBlockResult originalTagData = new ReadBlockResult(readTestSizeInBlocks, 4);
        originalTagData = mST25DVTag.readBlocks(firstBlockAddress, readTestSizeInBlocks);
        assertEquals(originalTagData.data.length, readTestSizeInBlocks * 4);

        // Allocate readTestSizeInBlocks new blocks of random data
        randomData = NFCTagUtils.allocateRandomData(readTestSizeInBlocks * mST25DVTag.getBlockSizeInBytes());

        // Size zero
        try {
            mST25DVTag.extendedWriteMultipleBlock(firstBlockAddress, Arrays.copyOfRange(randomData, 0, 0));
            fail("Expecting exception for buffer length of 0");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            mST25DVTag.extendedWriteMultipleBlock(firstBlockAddress, (byte) 0, Arrays.copyOfRange(randomData, 0, 0));
            fail("Expecting exception for buffer length of 0");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            mST25DVTag.extendedWriteMultipleBlock(firstBlockAddress, Arrays.copyOfRange(randomData, 0, 1));
            fail("Expecting exception for buffer length less than a block size");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        // Try writing buffer size that is not a multiple of blockSizeInBytes
        try {
            mST25DVTag.extendedWriteMultipleBlock(firstBlockAddress, Arrays.copyOfRange(randomData, 0, mST25DVTag.getBlockSizeInBytes() + 2));
            fail("Expecting exception for buffer length not a multiple of 4");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        // Try writing buffer size that is more than the maximum authorized
        try {
            mST25DVTag.extendedWriteMultipleBlock(firstBlockAddress, randomData);
            fail("Expecting exception for buffer length bigger than 4 * 4 bytes");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }


        // Test extendedWriteMultipleBlock in ascending number of blocks for both prototypes
        // Read n block from offset 0x02
        ReadBlockResult tagData = null;

        for (int n = 1; n < ST25DVTag.MAX_WRITE_MULTIPLE_BLOCKS + 1; n++) {
            STLog.i("Test extendedWriteMultipleBlock with " + n + " byte" + ((n > 1)? "s" : ""));
            System.out.println("Test extendedWriteMultipleBlock with " + n + " byte" + ((n > 1)? "s" : ""));

            int sizeInBytes = n * mST25DVTag.getBlockSizeInBytes();

            mST25DVTag.extendedWriteMultipleBlock(firstBlockAddress, Arrays.copyOfRange(randomData, 0, sizeInBytes));

            // Read data back from tag
            // First n blocks should be the ones we just wrote and the others should match the original data
            tagData = mST25DVTag.readBlocks(firstBlockAddress, readTestSizeInBlocks);
            assertArrayEquals(Arrays.copyOfRange(randomData, 0, sizeInBytes), Arrays.copyOfRange(tagData.data, 0, sizeInBytes));
            assertArrayEquals(
                    Arrays.copyOfRange(originalTagData.data, sizeInBytes, originalTagData.data.length),
                    Arrays.copyOfRange(tagData.data, sizeInBytes, tagData.data.length));

            // Write back original blocks
            mST25DVTag.writeBlocks(firstBlockAddress, originalTagData.data);
        }


        ////////////////////////////////////////////////////////////////////////////////////
        // Same tests with second prototype where the user specifies the length to be copied
        ////////////////////////////////////////////////////////////////////////////////////
        for (int n = 0; n < ST25DVTag.MAX_WRITE_MULTIPLE_BLOCKS; n++) {
            STLog.i("Test extendedWriteMultipleBlock with " + (n + 1) + " byte" + ((n > 0)? "s" : ""));
            System.out.println("Test extendedWriteMultipleBlock with " + (n + 1) + " byte" + ((n > 0)? "s" : ""));

            int sizeInBytes = (n + 1) * mST25DVTag.getBlockSizeInBytes();

            mST25DVTag.extendedWriteMultipleBlock(firstBlockAddress, (byte) n, Arrays.copyOfRange(randomData, 0, sizeInBytes));

            // Read data back from tag
            // First (n + 1) blocks should be the ones we just wrote and the others should match the original data
            tagData = mST25DVTag.readBlocks(firstBlockAddress, readTestSizeInBlocks);

            assertArrayEquals(Arrays.copyOfRange(randomData, 0, sizeInBytes), Arrays.copyOfRange(tagData.data, 0, sizeInBytes));
            assertArrayEquals(
                    Arrays.copyOfRange(originalTagData.data, sizeInBytes, originalTagData.data.length),
                    Arrays.copyOfRange(tagData.data, sizeInBytes, tagData.data.length));

            // Write back original blocks
            mST25DVTag.writeBlocks(firstBlockAddress, originalTagData.data);
        }

        try {
            mST25DVTag.extendedWriteMultipleBlock(firstBlockAddress, 3, randomData);
            fail("Expecting exception for buffer length bigger than 4 * 4 bytes");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            mST25DVTag.extendedWriteMultipleBlock(firstBlockAddress, 4, Arrays.copyOfRange(randomData, 0, 8));
            fail("Expecting exception for number of blocks bigger than 3");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            mST25DVTag.extendedWriteMultipleBlock(-1, 2, Arrays.copyOfRange(randomData, 0, 8));
            fail("Expecting exception for negative block address");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            mST25DVTag.extendedWriteMultipleBlock(firstBlockAddress, -2, Arrays.copyOfRange(randomData, 0, 8));
            fail("Expecting exception for negative number of blocks");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }


        // Test tag size and check expected read
        int lastBlockAddress =  st25DVTag.getMemSizeInBytes() / st25DVTag.getBlockSizeInBytes() - 1;

        // We should get an error from the tag as we try to write over the last block address
        try {
            mST25DVTag.extendedWriteMultipleBlock(lastBlockAddress, Arrays.copyOfRange(randomData, 0, 4 * mST25DVTag.getBlockSizeInBytes()));
            fail("Expecting exception for reaching a wrong block");
        } catch (STException e) {
            if (e.getError() == STExceptionCode.CMD_FAILED) {
                byte[] expectedError = new byte[]{0x01, 0x0F};
                assertArrayEquals("Check that the command failed with the correct ISO error code", expectedError, e.getErrorData());
            } else {
                fail("Unexpected exception");
            }
        }

        //////////////////////////////////////////////////////////////////
        STLog.i("Test End");
    }
}

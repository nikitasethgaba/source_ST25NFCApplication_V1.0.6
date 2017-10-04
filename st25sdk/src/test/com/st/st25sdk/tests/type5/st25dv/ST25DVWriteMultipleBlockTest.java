package com.st.st25sdk.tests.type5.st25dv;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import com.st.st25sdk.RFReaderInterface.TransceiveMode;
import com.st.st25sdk.STException;
import com.st.st25sdk.STException.STExceptionCode;
import com.st.st25sdk.STLog;
import com.st.st25sdk.tests.generic.NFCTagUtils;
import com.st.st25sdk.type5.ReadBlockResult;
import com.st.st25sdk.type5.ST25DVTag;

public class ST25DVWriteMultipleBlockTest {

    static ST25DVTag mST25DVTag = null;

    static public void run(ST25DVTag st25DVTag) throws STException {
        mST25DVTag = st25DVTag;

        //////////////////////////////////////////////////////////////////
        STLog.i("Test ST25DV writeMultipleBlock");

        // Test wrong parameters
        try {
            mST25DVTag.writeMultipleBlock((byte) 02, null);
            fail("Expecting exception for null buffer");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            mST25DVTag.writeMultipleBlock((byte) 02, (byte) 02, null);
            fail("Expecting exception for null buffer");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            mST25DVTag.writeMultipleBlock((byte) 02, (byte) 02, null, (byte) 02);
            fail("Expecting exception for null buffer");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        // Allocate 4 blocks of random data
        byte[] randomData = NFCTagUtils.allocateRandomData(4 * mST25DVTag.getBlockSizeInBytes());

        // Check that command is well-formed
        mST25DVTag.getReaderInterface().setTransceiveMode(TransceiveMode.EVAL);
        try {
            mST25DVTag.writeMultipleBlock((byte) 0x02, (byte) 0x03, randomData, (byte) 0x02);
            fail("Transceive in EVAL mode should throw an exception");
        } catch (STException e) {
            if (e.getError() == STExceptionCode.TRANSCEIVE_EVAL_MODE) {
                byte[] expectedCommand = ArrayUtils.addAll(new byte[]{
                        0x02,           // Flag
                        (byte) 0x24,    // Cmd
                        (byte) 0x02,    // Block Address
                        (byte) 0x03},   // Number of Blocks
                        randomData);    // Data
                assertArrayEquals("Check that the command is well formed", expectedCommand, e.getErrorData());
            } else {
                fail("Unexpected exception");
            }
        } finally {
            mST25DVTag.getReaderInterface().setTransceiveMode(TransceiveMode.NORMAL);
        }

        final int readTestSizeInBlocks = 6;
        // Read readTestSizeInBlocks of original data from tag
        ReadBlockResult originalTagData = new ReadBlockResult(readTestSizeInBlocks, 4);
        try {
            originalTagData = mST25DVTag.readBlocks(0x02, readTestSizeInBlocks);
        } catch (STException e) {
            fail("Not expecting exceptions");
        }
        assertEquals(originalTagData.data.length, readTestSizeInBlocks * 4);

        // Allocate readTestSizeInBlocks new blocks of random data
        randomData = NFCTagUtils.allocateRandomData(readTestSizeInBlocks * mST25DVTag.getBlockSizeInBytes());

        // Size zero
        byte blockAddress = 0x02;

        try {
            mST25DVTag.writeMultipleBlock(blockAddress, Arrays.copyOfRange(randomData, 0, 0));
            fail("Expecting exception for buffer length of 0");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            mST25DVTag.writeMultipleBlock(blockAddress, (byte) 0, Arrays.copyOfRange(randomData, 0, 0));
            fail("Expecting exception for buffer length of 0");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            mST25DVTag.writeMultipleBlock(blockAddress, Arrays.copyOfRange(randomData, 0, 1));
            fail("Expecting exception for buffer length less than a block size");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        // Try writing buffer size that is not a multiple of blockSizeInBytes
        try {
            mST25DVTag.writeMultipleBlock(blockAddress, Arrays.copyOfRange(randomData, 0, mST25DVTag.getBlockSizeInBytes() + 2));
            fail("Expecting exception for buffer length not a multiple of 4");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        // Try writing buffer size that is more than the maximum authorized
        try {
            mST25DVTag.writeMultipleBlock(blockAddress, randomData);
            fail("Expecting exception for buffer length bigger than 4 * 4 bytes");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }


        // Test writeMultipleBlock in ascending number of blocks for both prototypes
        // Read n block from offset 0x02
        ReadBlockResult tagData = null;

        for (int n = 1; n < ST25DVTag.MAX_WRITE_MULTIPLE_BLOCKS + 1; n++) {
            STLog.i("Test writeMultipleBlock with " + n + " byte" + ((n > 1)? "s" : ""));
            System.out.println("Test writeMultipleBlock with " + n + " byte" + ((n > 1)? "s" : ""));

            int sizeInBytes = n * mST25DVTag.getBlockSizeInBytes();

            try {
                mST25DVTag.writeMultipleBlock(blockAddress, Arrays.copyOfRange(randomData, 0, sizeInBytes));
            } catch (STException e) {
                fail("Not expecting exceptions");
            }

            // Read data back from tag
            // First n blocks should be the ones we just wrote and the others should match the original data
            try {
                tagData = mST25DVTag.readBlocks(blockAddress, readTestSizeInBlocks);
            } catch (STException e) {
                fail("Not expecting exceptions");
            }
            assertArrayEquals(Arrays.copyOfRange(randomData, 0, sizeInBytes), Arrays.copyOfRange(tagData.data, 0, sizeInBytes));
            assertArrayEquals(
                    Arrays.copyOfRange(originalTagData.data, sizeInBytes, originalTagData.data.length),
                    Arrays.copyOfRange(tagData.data, sizeInBytes, tagData.data.length));

            // Write back original blocks
            try {
                mST25DVTag.writeBlocks(blockAddress, originalTagData.data);
            } catch (STException e) {
                fail("Not expecting exceptions");
            }
        }


        ////////////////////////////////////////////////////////////////////////////////////
        // Same tests with second prototype where the user specifies the length to be copied
        ////////////////////////////////////////////////////////////////////////////////////
        for (int n = 0; n < ST25DVTag.MAX_WRITE_MULTIPLE_BLOCKS; n++) {
            STLog.i("Test writeMultipleBlock with " + (n + 1) + " byte" + ((n > 0)? "s" : ""));
            System.out.println("Test writeMultipleBlock with " + (n + 1) + " byte" + ((n > 0)? "s" : ""));

            int sizeInBytes = (n + 1) * mST25DVTag.getBlockSizeInBytes();

            try {
                mST25DVTag.writeMultipleBlock(blockAddress, (byte) n, Arrays.copyOfRange(randomData, 0, sizeInBytes));
            } catch (STException e) {
                fail("Not expecting exceptions");
            }

            // Read data back from tag
            // First (n + 1) blocks should be the ones we just wrote and the others should match the original data
            try {
                tagData = mST25DVTag.readBlocks(blockAddress, readTestSizeInBlocks);
            } catch (STException e) {
                fail("Not expecting exceptions");
            }
            assertArrayEquals(Arrays.copyOfRange(randomData, 0, sizeInBytes), Arrays.copyOfRange(tagData.data, 0, sizeInBytes));
            assertArrayEquals(
                    Arrays.copyOfRange(originalTagData.data, sizeInBytes, originalTagData.data.length),
                    Arrays.copyOfRange(tagData.data, sizeInBytes, tagData.data.length));

            // Write back original blocks
            try {
                mST25DVTag.writeBlocks(blockAddress, originalTagData.data);
            } catch (STException e) {
                fail("Not expecting exceptions");
            }
        }

        try {
            mST25DVTag.writeMultipleBlock(blockAddress, (byte) 3, randomData);
            fail("Expecting exception for buffer length bigger than 4 * 4 bytes");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            mST25DVTag.writeMultipleBlock(blockAddress, (byte) 4, Arrays.copyOfRange(randomData, 0, 8));
            fail("Expecting exception for number of blocks bigger than 3");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        // Test tag size and check expected read
        int lastBlockAddress =  st25DVTag.getMemSizeInBytes() / st25DVTag.getBlockSizeInBytes() - 1;

        if (lastBlockAddress > 0xFF) {
            // Write multiple blocks over a 0xFF byte-size limit
            // This should be valid
            try {
                mST25DVTag.writeMultipleBlock((byte) 0xFF, Arrays.copyOfRange(randomData, 0, 4 * mST25DVTag.getBlockSizeInBytes()));
            } catch (STException e) {
                fail("Not expecting exceptions");
            }

            try {
                tagData.data = mST25DVTag.readMultipleBlock((byte) 0xFF, (byte) 3);
            } catch (STException e) {
                fail("Not expecting exceptions");
            }

            assertArrayEquals(Arrays.copyOfRange(randomData, 0, 4 * mST25DVTag.getBlockSizeInBytes()), Arrays.copyOfRange(tagData.data, 1, tagData.data.length));
        } else {
            // Testing on ST25DV04K or 08K
            // We should get an error from the tag
            try {
                mST25DVTag.writeMultipleBlock((byte) lastBlockAddress, Arrays.copyOfRange(randomData, 0, 4 * mST25DVTag.getBlockSizeInBytes()));
                fail("Expecting exception for reaching a wrong block");
            } catch (STException e) {
                if (e.getError() == STExceptionCode.CMD_FAILED) {
                    byte[] expectedError = new byte[]{0x01, 0x0F};
                    assertArrayEquals("Check that the command failed with the correct ISO error code", expectedError, e.getErrorData());
                } else {
                    fail("Unexpected exception");
                }
            }

        }

        //////////////////////////////////////////////////////////////////
        STLog.i("Test End");
    }
}

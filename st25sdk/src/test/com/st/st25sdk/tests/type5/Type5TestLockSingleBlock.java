package com.st.st25sdk.tests.type5;


import com.st.st25sdk.Helper;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.type5.Type5Tag;

import org.apache.commons.lang3.ArrayUtils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class Type5TestLockSingleBlock {

    // Set this to true if you really want to lock some blocks (otherwise the test is only
    // done in EVAL mode).
    // Warning it will not be possible to change this block anymore!
    private static boolean lockBlockForReal = false;


    static public void run(Type5Tag type5Tag) throws STException {
        byte blockNumber = 3;   // Arbitrary value

        byte[] uid = Helper.reverseByteArray(type5Tag.getUid());

        type5Tag.getReaderInterface().setTransceiveMode(RFReaderInterface.TransceiveMode.EVAL);

        try {

            type5Tag.lockSingleBlock(blockNumber);
            fail("Transceive in EVAL mode should throw an exception");

        } catch (STException e) {
            if (e.getError() == STException.STExceptionCode.TRANSCEIVE_EVAL_MODE) {

                // Build the expectedCommand
                byte[] expectedCommand = ArrayUtils.addAll(new byte[]{
                            0x22,                       // Flag
                            (byte) 0x22},               // LockSingleBlock Cmd
                            uid);

                // Amend the blockNumber
                expectedCommand = ArrayUtils.addAll(expectedCommand, blockNumber);

                assertArrayEquals(expectedCommand, e.getErrorData());

            } else {
                fail("Unexpected exception");
            }
        } finally {
            // Restore the default TransceiveMode
            type5Tag.getReaderInterface().setTransceiveMode(RFReaderInterface.TransceiveMode.NORMAL);
        }

        if(lockBlockForReal) {
            // Lock the block
            type5Tag.lockSingleBlock(blockNumber);

            // And check that a write now fails
            try {
                byte[] dummyData = new byte[] {(byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD };

                type5Tag.writeSingleBlock(blockNumber, dummyData);
                fail("writeSingleBlock successful while it should have failed!");

            } catch (STException e) {
                assertEquals(STException.STExceptionCode.ISO15693_BLOCK_IS_LOCKED, e.getError());
            }
        }

    }

}

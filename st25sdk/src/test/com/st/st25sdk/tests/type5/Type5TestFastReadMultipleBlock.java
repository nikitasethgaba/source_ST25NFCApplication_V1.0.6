package com.st.st25sdk.tests.type5;

import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Assume;

import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.type5.STType5Tag;

public class Type5TestFastReadMultipleBlock {

    static boolean mFastCommandsAvailable = false;

    static public void run(STType5Tag type5Tag, String readerName) throws Exception, STException {

        // Skip test for readers that do not support fast mode
        mFastCommandsAvailable = (readerName.contains("CR95HF") || readerName.contains("ST25R3911B-DISCO"));
        Assume.assumeTrue("Fast commands available on select readers only", mFastCommandsAvailable);

        //////////////////////////////////////////////////////////////////
        STLog.i("Test FastReadMultipleBlock commands");

        Assume.assumeTrue("This tag doesn't have enough memory to run the test", type5Tag.getMemSizeInBytes() >= 256);

        int nbrOfBytesPerBlock = type5Tag.getBlockSizeInBytes();
        byte[] dataRead;
        byte[] readBlock1 = new byte[nbrOfBytesPerBlock * 4];
        byte[] readBlock2 = new byte[nbrOfBytesPerBlock * 1];
        byte[] readBlock3 = new byte[nbrOfBytesPerBlock * 2];
        byte blockAddressBlock1 = 0;
        byte blockAddressBlock2 = 0x1A;   // Arbitrary values
        byte blockAddressBlock3 = 0x30 ;

        // Allocate some blocks containing random data
        byte[] block1 = new byte[nbrOfBytesPerBlock * 4];
        new Random().nextBytes(block1);

        byte[] block2 = new byte[nbrOfBytesPerBlock * 1];
        new Random().nextBytes(block2);

        byte[] block3 = new byte[nbrOfBytesPerBlock * 2];
        new Random().nextBytes(block3);

        type5Tag.writeBlocks(blockAddressBlock1, block1);
        type5Tag.writeBlocks(blockAddressBlock2, block2);
        type5Tag.writeBlocks(blockAddressBlock3, block3);

        // Read the blocks
        dataRead = type5Tag.fastReadMultipleBlock(blockAddressBlock1, (byte) (readBlock1.length / nbrOfBytesPerBlock - 1));
        readBlock1 = Arrays.copyOfRange(dataRead, 1, dataRead.length);    // Skip the status Byte

        // Check the blocks content
        Assert.assertArrayEquals(block1, readBlock1);

        dataRead = type5Tag.fastReadMultipleBlock(blockAddressBlock2, (byte) (readBlock2.length / nbrOfBytesPerBlock - 1));
        readBlock2 = Arrays.copyOfRange(dataRead, 1, dataRead.length);    // Skip the status Byte

        // Check the blocks content
        Assert.assertArrayEquals(block2, readBlock2);

        dataRead = type5Tag.fastReadMultipleBlock(blockAddressBlock3, (byte) (readBlock3.length / nbrOfBytesPerBlock - 1));
        readBlock3 = Arrays.copyOfRange(dataRead, 1, dataRead.length);    // Skip the status Byte

        // Check the blocks content
        Assert.assertArrayEquals(block3, readBlock3);

        //////////////////////////////////////////////////////////////////
        STLog.i("Test End");
    }
}

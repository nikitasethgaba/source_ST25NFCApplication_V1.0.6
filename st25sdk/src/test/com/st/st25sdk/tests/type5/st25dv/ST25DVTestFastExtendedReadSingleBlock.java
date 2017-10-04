package com.st.st25sdk.tests.type5.st25dv;

import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Assume;

import com.st.st25sdk.Helper;
import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.type5.ST25DVTag;

public class ST25DVTestFastExtendedReadSingleBlock {
    static boolean mFastCommandsAvailable = false;

    static public void run(ST25DVTag type5Tag, String readerName) throws STException {

        // Skip test for readers that do not support fast mode
        mFastCommandsAvailable = (readerName.contains("CR95HF") || readerName.contains("ST25R3911B-DISCO"));
        Assume.assumeTrue("Fast commands available on select readers only", mFastCommandsAvailable);

        //////////////////////////////////////////////////////////////////
        STLog.i("Test FastExtendedReadSingleBlock commands");

        Assume.assumeTrue("This tag doesn't have enough memory to run the test", type5Tag.getMemSizeInBytes() >= 256);

        int nbrOfBytesPerBlock = type5Tag.getBlockSizeInBytes();
        byte[] dataRead;
        byte[] readBlock1 = new byte[nbrOfBytesPerBlock];
        byte[] readBlock2 = new byte[nbrOfBytesPerBlock];
        byte[] readBlock3 = new byte[nbrOfBytesPerBlock];
        byte blockAddressBlock1 = 0;
        byte blockAddressBlock2 = 0x002E;   // Arbitrary values
        byte blockAddressBlock3 = 0x39 ;

        // Allocate some blocks containing random data
        byte[] block1 = new byte[nbrOfBytesPerBlock];
        new Random().nextBytes(block1);

        byte[] block2 = new byte[nbrOfBytesPerBlock];
        new Random().nextBytes(block2);

        byte[] block3 = new byte[nbrOfBytesPerBlock];
        new Random().nextBytes(block3);

        // Write data to tag
        type5Tag.writeSingleBlock(blockAddressBlock1, block1);
        type5Tag.writeSingleBlock(blockAddressBlock2, block2);
        type5Tag.writeSingleBlock(blockAddressBlock3, block3);

        // Read the block
        dataRead = type5Tag.fastExtendedReadSingleBlock(blockAddressBlock1);
        readBlock1 = Arrays.copyOfRange(dataRead, 1, dataRead.length);    // Skip the status Byte

        // Check the blocks content
        Assert.assertArrayEquals(block1, readBlock1);

        // Read the block
        dataRead = type5Tag.fastExtendedReadSingleBlock(blockAddressBlock2);
        readBlock2 = Arrays.copyOfRange(dataRead, 1, dataRead.length);    // Skip the status Byte

        // Check the blocks content
        Assert.assertArrayEquals(block2, readBlock2);

        // Read the block
        dataRead = type5Tag.fastExtendedReadSingleBlock(blockAddressBlock3);
        readBlock3 = Arrays.copyOfRange(dataRead, 1, dataRead.length);    // Skip the status Byte

        // Check the blocks content
        Assert.assertArrayEquals(block3, readBlock3);


        //////////////////////////////////////////////////////////////
        // Test High Density tags
        Assume.assumeTrue("This tag doesn't have enough memory to run the test", type5Tag.getMemSizeInBytes() >= 512);

        byte[] readBlock4 = new byte[nbrOfBytesPerBlock];
        byte[] readBlock5 = new byte[nbrOfBytesPerBlock];
        byte[] readBlock6 = new byte[nbrOfBytesPerBlock];
        int blockAddressBlock4 = 0x00FF;
        int blockAddressBlock5 = 0x01E5;    // Arbitrary value
        int blockAddressBlock6 = 0x03FF;    // Last address of 4Kb tag

        // Allocate some blocks containing random data
        byte[] block4 = new byte[nbrOfBytesPerBlock];
        new Random().nextBytes(block4);

        byte[] block5 = new byte[nbrOfBytesPerBlock];
        new Random().nextBytes(block5);

        byte[] block6 = new byte[nbrOfBytesPerBlock];
        new Random().nextBytes(block6);


        // Write data to tag
        type5Tag.extendedWriteSingleBlock(Helper.convertIntTo2BytesHexaFormat(blockAddressBlock4), block4);

        // Read data back from tag
        dataRead = type5Tag.fastExtendedReadSingleBlock(blockAddressBlock4);
        readBlock4 = Arrays.copyOfRange(dataRead, 1, dataRead.length);    // Skip the status Byte

        // Check the blocks content
        Assert.assertArrayEquals(block4, readBlock4);


        // Write data to tag
        type5Tag.extendedWriteSingleBlock(Helper.convertIntTo2BytesHexaFormat(blockAddressBlock5), block5);

        // Read data back from tag
        dataRead = type5Tag.fastExtendedReadSingleBlock(blockAddressBlock5);
        readBlock5 = Arrays.copyOfRange(dataRead, 1, dataRead.length);    // Skip the status Byte

        // Check the blocks content
        Assert.assertArrayEquals("Bug on ST25DV64K c2.1 chip", block5, readBlock5);


        // Write data to tag
        type5Tag.extendedWriteSingleBlock(Helper.convertIntTo2BytesHexaFormat(blockAddressBlock6), block6);

        // Read data back from tag
        dataRead = type5Tag.fastExtendedReadSingleBlock(blockAddressBlock6);
        readBlock6 = Arrays.copyOfRange(dataRead, 1, dataRead.length);    // Skip the status Byte

        // Check the blocks content
        Assert.assertArrayEquals("Bug on ST25DV64K c2.1 chip", block6, readBlock6);


        //////////////////////////////////////////////////////////////////
        STLog.i("Test End");
    }
}

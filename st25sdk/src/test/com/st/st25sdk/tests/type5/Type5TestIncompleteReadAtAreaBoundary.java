package com.st.st25sdk.tests.type5;

import com.st.st25sdk.MultiAreaInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.tests.generic.NFCTagUtils;
import com.st.st25sdk.type5.ReadBlockResult;
import com.st.st25sdk.type5.STVicinityTag;
import com.st.st25sdk.type5.Type5Tag;

import org.junit.Assert;

import java.util.Random;

import static com.st.st25sdk.MultiAreaInterface.AREA1;
import static com.st.st25sdk.command.Iso15693Protocol.ADDRESSED_MODE;
import static com.st.st25sdk.command.Iso15693Protocol.HIGH_DATA_RATE_MODE;
import static com.st.st25sdk.command.Iso15693Protocol.OPTION_FLAG;
import static com.st.st25sdk.command.Iso15693Protocol.PROTOCOL_FORMAT_EXTENSION;

public class Type5TestIncompleteReadAtAreaBoundary {

    // NB: This should be a multiple of the block size
    static private final int DATA_SIZE_IN_BYTES = 8;

    static public void run(Type5Tag type5Tag, MultiAreaInterface multiAreaInterface) throws STException {
        byte[] data;

        // Allocate some bytes and write them at the end of Area1
        byte[] randomData = new byte[DATA_SIZE_IN_BYTES];
        new Random().nextBytes(randomData);

        int area1SizeInBytes = multiAreaInterface.getAreaSizeInBytes(AREA1);
        int startAddress = area1SizeInBytes-DATA_SIZE_IN_BYTES;

        type5Tag.writeBytes(startAddress, randomData);

        /////////////////////////////////
        // Test of readBytes() behavior
        /////////////////////////////////

        // Read more bytes than available in Area1
        int nbrOfBytesToRead = 2 * DATA_SIZE_IN_BYTES;

        data = type5Tag.readBytes(startAddress, nbrOfBytesToRead);
        Assert.assertArrayEquals(randomData, data);


        if (!NFCTagUtils.isReadLooping(type5Tag)) {
            /////////////////////////////////
            // Test of readBlocks() behavior
            /////////////////////////////////

            // Read more blocks than available in Area1
            int nbrOfBlocksToRead = (2 * DATA_SIZE_IN_BYTES) / type5Tag.getBlockSizeInBytes();
            int blockAddress = startAddress / type5Tag.getBlockSizeInBytes();

            ReadBlockResult result = type5Tag.readBlocks(blockAddress, nbrOfBlocksToRead);
            Assert.assertArrayEquals(randomData, result.data);

            //////////////////////////////////////////////////////
            // Test of readBlocks() with Block Security Status
            //////////////////////////////////////////////////////
            byte flag = HIGH_DATA_RATE_MODE | ADDRESSED_MODE | OPTION_FLAG;
            if (type5Tag instanceof STVicinityTag) {
                flag |= PROTOCOL_FORMAT_EXTENSION;
            }

            result = type5Tag.readBlocks(blockAddress, nbrOfBlocksToRead, flag);
            Assert.assertArrayEquals(randomData, result.data);
        }
    }

}


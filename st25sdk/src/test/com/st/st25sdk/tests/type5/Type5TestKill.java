package com.st.st25sdk.tests.type5;


import com.st.st25sdk.Helper;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.command.Iso15693CustomKillCommandInterface;
import com.st.st25sdk.type5.Type5Tag;

import org.apache.commons.lang3.ArrayUtils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

public class Type5TestKill {

    static public void run(Type5Tag type5Tag, Iso15693CustomKillCommandInterface killInterface) throws STException {

        byte[] uid = Helper.reverseByteArray(type5Tag.getUid());

        ////////////////////////////////////
        // Test of writeKill command
        ////////////////////////////////////

        byte[] originalKillPassword = new byte[]{0x00, 0x00, 0x00, 0x00};
        byte[] newKillPassword = new byte[]{0x00, 0x00, 0x00, 0x01};

        killInterface.writeKill(newKillPassword);

        // Command successful

        // Restore original password
        killInterface.writeKill(originalKillPassword);

        ////////////////////////////////////
        // Test of lockKill command
        ////////////////////////////////////

        type5Tag.getReaderInterface().setTransceiveMode(RFReaderInterface.TransceiveMode.EVAL);

        try {
            killInterface.lockKill();
            fail("Transceive in EVAL mode should throw an exception");

        } catch (STException e) {
            if (e.getError() == STException.STExceptionCode.TRANSCEIVE_EVAL_MODE) {
                // Build the expectedCommand
                byte[] expectedCommand = ArrayUtils.addAll(new byte[]{
                                0x22,                       // Flag
                                (byte) 0xB2,                // Lock Kill Cmd
                                0x02},                      // STM
                                uid);

                expectedCommand = ArrayUtils.addAll(expectedCommand, new byte[] {
                        0x00,                       // Kill access
                        0x01                        // Protect Status
                        });

                assertArrayEquals(expectedCommand, e.getErrorData());

            } else {
                fail("Unexpected exception");
            }
        } finally {
            // Restore the default TransceiveMode
            type5Tag.getReaderInterface().setTransceiveMode(RFReaderInterface.TransceiveMode.NORMAL);
        }

        ////////////////////////////////////
        // Test of kill command
        ////////////////////////////////////
        type5Tag.getReaderInterface().setTransceiveMode(RFReaderInterface.TransceiveMode.EVAL);

        try {
            byte[] unencryptedKillPassword = new byte[]{0x00, 0x00, 0x00, 0x00};

            killInterface.kill(unencryptedKillPassword);
            fail("Transceive in EVAL mode should throw an exception");

        } catch (STException e) {
            if (e.getError() == STException.STExceptionCode.TRANSCEIVE_EVAL_MODE) {

                // Build the expectedCommand
                byte[] expectedCommand = ArrayUtils.addAll(new byte[]{
                            0x22,                       // Flag
                            (byte) 0xA6,                // Kill Cmd
                            0x02},                      // STM
                            uid);

                expectedCommand = ArrayUtils.addAll(expectedCommand, new byte[] {
                            0x00,                       // Kill param
                            0x00, 0x00, 0x00, 0x00      // Kill password
                            });

                assertArrayEquals(expectedCommand, e.getErrorData());

            } else {
                fail("Unexpected exception");
            }
        } finally {
            // Restore the default TransceiveMode
            type5Tag.getReaderInterface().setTransceiveMode(RFReaderInterface.TransceiveMode.NORMAL);
        }

    }

}

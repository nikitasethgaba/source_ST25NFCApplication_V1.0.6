package com.st.st25sdk.tests.type5.st25tv;


import com.st.st25sdk.Helper;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.tests.generic.NFCTagUtils;
import com.st.st25sdk.type5.ST25TVTag;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

public class ST25TVTestEas {
    static public void run(ST25TVTag st25TVTag) throws STException {
        String easTelegram, easTelegramRead;

        // Enable EAS
        st25TVTag.setEas();

        ///////////////////////////////////////////////
        // Test of writeEasId() and readEasId()
        ///////////////////////////////////////////////
        int easId = 0xBABA;
        st25TVTag.writeEasId(easId);

        // EAS should be enabled otherwise the command will timeout
        int readEasId = st25TVTag.readEasId();
        Assert.assertEquals(easId, readEasId);

        ///////////////////////////////////////////////
        // Test of Telegram functions
        ///////////////////////////////////////////////
        int maxTelegramLength = st25TVTag.getMaxEasTelegramLength();
        Assert.assertEquals(32, maxTelegramLength);

        //            Telegram length > 32 bytes
        try {
            easTelegram = "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE";
            st25TVTag.writeEasTelegram(easTelegram);
            fail("An exception should have been raised");

        } catch (STException e) {
            Assert.assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        // 16 Bytes < Telegram length <= 32 bytes
        easTelegram = "AAAAAAAAAAAAAAAAAAAA";
        st25TVTag.writeEasTelegram(easTelegram);
        easTelegramRead = st25TVTag.readEasTelegram();
        Assert.assertEquals(easTelegram, easTelegramRead);

        //  8 Bytes < Telegram length <= 16 bytes
        easTelegram = "BBBBBBBBBBBB";
        st25TVTag.writeEasTelegram(easTelegram);
        easTelegramRead = st25TVTag.readEasTelegram();
        Assert.assertEquals(easTelegram, easTelegramRead);

        //  4 Bytes < Telegram length <= 8 bytes
        easTelegram = "CCCCCCC";
        st25TVTag.writeEasTelegram(easTelegram);
        easTelegramRead = st25TVTag.readEasTelegram();
        Assert.assertEquals(easTelegram, easTelegramRead);

        //            Telegram length <= 4 bytes
        easTelegram = "DDD";
        st25TVTag.writeEasTelegram(easTelegram);
        easTelegramRead = st25TVTag.readEasTelegram();
        Assert.assertEquals(easTelegram, easTelegramRead);

        // NB: The test of writeEasTelegram() is also testing writeEasConfig()

        ///////////////////////////////////////////////
        // Test of writeEasSecurityConfiguration()
        ///////////////////////////////////////////////

        // Ensure that configuration is locked by password
        ST25TVUtils.lockConfiguration(st25TVTag);

        // Try calling writeEasSecurityConfiguration() without entering the password
        try {
            st25TVTag.writeEasSecurityConfiguration(true);
            fail("An exception should have been raised");

        } catch (STException e) {
            Assert.assertEquals(STException.STExceptionCode.CONFIG_PASSWORD_NEEDED, e.getError());
        }

        // Present the Configuration password
        byte[] password = new byte[] {0x00, 0x00, 0x00, 0x00};
        ST25TVUtils.presentConfigurationPassword(st25TVTag, password);

        st25TVTag.getReaderInterface().setTransceiveMode(RFReaderInterface.TransceiveMode.EVAL);

        byte[] uid = Helper.reverseByteArray(st25TVTag.getUid());

        try {
            st25TVTag.writeEasSecurityConfiguration(false);
            fail("Transceive in EVAL mode should throw an exception");

        } catch (STException e) {
            if (e.getError() == STException.STExceptionCode.TRANSCEIVE_EVAL_MODE) {

                // Build the expectedCommand
                byte[] expectedCommand = ArrayUtils.addAll(new byte[]{
                                0x22,                       // Flag
                                (byte) 0xA1,                // WriteConfig Cmd
                                0x02},                      // STM
                                uid);

                // Add Register Id and Config byte
                expectedCommand = ArrayUtils.addAll(expectedCommand, new byte[]{
                                0x02,                       // RegisterId
                                0x00                        // Config byte
                                });

                assertArrayEquals(expectedCommand, e.getErrorData());

            } else {
                fail("Unexpected exception");
            }
        }

        try {
            st25TVTag.writeEasSecurityConfiguration(true);
            fail("Transceive in EVAL mode should throw an exception");

        } catch (STException e) {
            if (e.getError() == STException.STExceptionCode.TRANSCEIVE_EVAL_MODE) {

                // Build the expectedCommand
                byte[] expectedCommand = ArrayUtils.addAll(new byte[]{
                                0x22,                       // Flag
                                (byte) 0xA1,                // WriteConfig Cmd
                                0x02},                      // STM
                                uid);

                // Add Register Id and Config byte
                expectedCommand = ArrayUtils.addAll(expectedCommand, new byte[]{
                        0x02,                       // RegisterId
                        0x01                        // Config byte
                });

                assertArrayEquals(expectedCommand, e.getErrorData());

            } else {
                fail("Unexpected exception");
            }
        }

        ///////////////////////////////////////////////
        // Test of lockEas()
        ///////////////////////////////////////////////
        try {
            st25TVTag.lockEas();
            fail("Transceive in EVAL mode should throw an exception");

        } catch (STException e) {
            if (e.getError() == STException.STExceptionCode.TRANSCEIVE_EVAL_MODE) {

                // Build the expectedCommand
                byte[] expectedCommand = ArrayUtils.addAll(new byte[]{
                                0x22,                       // Flag
                                (byte) 0xA4,                // lockEas Cmd
                                0x02},                      // STM
                                uid);

                assertArrayEquals(expectedCommand, e.getErrorData());

            } else {
                fail("Unexpected exception");
            }
        }

        // Restore the default TransceiveMode
        st25TVTag.getReaderInterface().setTransceiveMode(RFReaderInterface.TransceiveMode.NORMAL);

        // Disable EAS
        st25TVTag.resetEas();

    }
}

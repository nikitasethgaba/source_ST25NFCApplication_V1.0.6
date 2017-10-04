package com.st.st25sdk.tests.type5.st25dv;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assume;

import com.st.st25sdk.Helper;
import com.st.st25sdk.RFReaderInterface.TransceiveMode;
import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.type5.ST25DVTag;

public class ST25DVTestLockConfiguration {

    // Set this to true if you really want to lock the config on the tag
    // Warning it will not be possible to change this block anymore except via I2C!
    // Last tested OK on August 29th 2017
    private static boolean lockConfigOnTag = false;

    static public void run(ST25DVTag st25DVTag) throws STException {

        byte[] uid = Helper.reverseByteArray(st25DVTag.getUid());

        //////////////////////////////////////////////////////////////////
        STLog.i("Test ST25DV lockConfiguration command");

        Assume.assumeTrue(lockConfigOnTag);

        // Check that command is well-formed
        st25DVTag.getReaderInterface().setTransceiveMode(TransceiveMode.RECORD);
        try {
            st25DVTag.lockConfiguration();
        } catch (STException e) {
            fail("Unexpected exception");
        } finally {
            st25DVTag.getReaderInterface().setTransceiveMode(TransceiveMode.NORMAL);
        }

        byte[] actualCmd = st25DVTag.getReaderInterface().getLastTransceivedData();

        byte[] expectedCommand = ArrayUtils.addAll(
                new byte[]{
                    (byte) 0x02,    // Flag
                    (byte) 0xA1,    // Cmd
                    (byte) 0x02,    // ST Mfr code
                    (byte) 0x0F,    // Register Address
                    (byte) 0x01},    // Register value - Locked configuration
                uid
                );
        assertArrayEquals("lockConfiguration command not formed as expected", expectedCommand, actualCmd);

        //////////////////////////////////////////////////////////////////
        STLog.i("Test End");
    }
}

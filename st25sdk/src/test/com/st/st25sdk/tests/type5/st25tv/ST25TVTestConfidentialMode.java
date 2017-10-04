package com.st.st25sdk.tests.type5.st25tv;


import com.st.st25sdk.Helper;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.TagHelper;
import com.st.st25sdk.type5.ST25TVTag;

import org.apache.commons.lang3.ArrayUtils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

public class ST25TVTestConfidentialMode {

    static public void run(ST25TVTag st25TVTag) throws STException {

        byte[] uid = Helper.reverseByteArray(st25TVTag.getUid());

        // Get a 16 bits random number
        byte[] randomNumber = st25TVTag.getRandomNumber();

        byte[] password = new byte[]{0x00, 0x00, 0x00, 0x00};
        TagHelper.xorBetweenPwdAndRandomNbr(password, randomNumber);

        st25TVTag.getReaderInterface().setTransceiveMode(RFReaderInterface.TransceiveMode.EVAL);

        try {
            st25TVTag.enableConfidentialMode(password);
            fail("Transceive in EVAL mode should throw an exception");

        } catch (STException e) {
            if (e.getError() == STException.STExceptionCode.TRANSCEIVE_EVAL_MODE) {

                // Build the expectedCommand
                byte[] expectedCommand = ArrayUtils.addAll(new byte[]{
                            0x22,                       // Flag
                            (byte) 0xBA,                // ConfidentialMode Cmd
                            0x02},                      // STM
                            uid);

                // Add Confidential Access Code
                expectedCommand = ArrayUtils.addAll(expectedCommand, (byte) 0x00);

                // Add password
                expectedCommand = ArrayUtils.addAll(expectedCommand, password);

                assertArrayEquals(expectedCommand, e.getErrorData());

            } else {
                fail("Unexpected exception");
            }
        } finally {
            // Restore the default TransceiveMode
            st25TVTag.getReaderInterface().setTransceiveMode(RFReaderInterface.TransceiveMode.NORMAL);
        }

    }

}

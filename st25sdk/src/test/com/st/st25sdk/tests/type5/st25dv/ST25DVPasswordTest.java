package com.st.st25sdk.tests.type5.st25dv;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import static com.st.st25sdk.type5.STType5PasswordInterface.PasswordLength.PWD_ON_64_BITS;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import com.st.st25sdk.RFReaderInterface.TransceiveMode;
import com.st.st25sdk.STException;
import com.st.st25sdk.STException.STExceptionCode;
import com.st.st25sdk.STLog;
import com.st.st25sdk.type5.ST25DVTag;

public class ST25DVPasswordTest {

    static ST25DVTag mST25DVTag = null;
    final static byte[] mWrongPassword   = new byte[]{0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00};
    final static byte[] mNewPassword     = new byte[]{0x01, 0x20, 0x33, (byte) 0xFF, (byte) 0xEE, 0x7F, (byte) 0x80, 0x00};

    static public void run(ST25DVTag st25DVTag) throws STException {
        mST25DVTag = st25DVTag;

        //////////////////////////////////////////////////////////////////
        STLog.i("Test ST25DV presentPassword command for all passwords");
        testPresentPassword();

        //////////////////////////////////////////////////////////////////
        STLog.i("Test ST25DV writePassword command for all passwords");
        testWritePassword();

        //////////////////////////////////////////////////////////////////
        STLog.i("Test End");
    }

    static void testPresentPassword() throws STException {
        // Ask for wrong passwordNumberLength
        try {
            mST25DVTag.getPasswordLength((byte) 0xFF);
            fail("Expecting exception");
        } catch (STException e) {
            assertEquals(STExceptionCode.BAD_PARAMETER, e.getError());
        }

        // Present null password
        try {
            mST25DVTag.presentPassword((byte) 0, null, (byte) 0x02);
            fail("Expecting exception for null password");
        } catch (STException e) {
            assertEquals(STExceptionCode.BAD_PARAMETER, e.getError());
        }

        // For each available password
        for (int i = 0; i < 4; i++) {
            // Check password length
            // All ST25DV passwords are 64-bit long
            assertEquals("Password " + i + "'s length should be 64 bits", PWD_ON_64_BITS, mST25DVTag.getPasswordLength((byte) i));

            // Present Password tests
            // Test presentPassword command string in Eval mode
            mST25DVTag.getReaderInterface().setTransceiveMode(TransceiveMode.EVAL);
            try {
                mST25DVTag.presentPassword((byte) i, ST25DVTests.mDefaultPassword, (byte) 0x02);
                fail("Transceive in EVAL mode should throw an exception");
            } catch (STException e) {
                if (e.getError() == STExceptionCode.TRANSCEIVE_EVAL_MODE) {
                    byte[] expectedCommand = ArrayUtils.addAll(new byte[]{
                        0x02,
                        (byte) 0xB3,
                        0x02,
                        (byte) i},
                            ST25DVTests.mDefaultPassword);
                    assertArrayEquals("Check that the command is well formed", expectedCommand, e.getErrorData());
                } else {
                    fail("Unexpected exception");
                }
            } finally {
                mST25DVTag.getReaderInterface().setTransceiveMode(TransceiveMode.NORMAL);
            }

            // Present correct default password
            mST25DVTag.presentPassword((byte) i, ST25DVTests.mDefaultPassword);

            // Present wrong password
            try {
                mST25DVTag.presentPassword((byte) i, mWrongPassword);
                fail("Expecting exception for wrong password");
            } catch (STException e) {
                if (e.getError() == STExceptionCode.CMD_FAILED) {
                    byte[] expectedError = new byte[]{0x01, 0x0F};
                    assertArrayEquals("Check that the command failed with the correct ISO error code", expectedError, e.getErrorData());
                } else {
                    fail("Unexpected exception");
                }
            }

            // Present incomplete password data (missing bytes)
            try {
                mST25DVTag.presentPassword((byte) i, Arrays.copyOfRange(ST25DVTests.mDefaultPassword, 1, ST25DVTests.mDefaultPassword.length), (byte) 0x02);
                fail("Expecting exception for password with wrong length");
            } catch (STException e) {
                assertEquals(STExceptionCode.ISO15693_CMD_NOT_RECOGNIZED, e.getError());
            }

            // Present command with wrong option (flag with option bit)
            try {
                mST25DVTag.presentPassword((byte) i, ST25DVTests.mDefaultPassword, (byte) 0x42);
                fail("Expecting exception for command with wrong option");
            } catch (STException e) {
                assertEquals(STExceptionCode.ISO15693_CMD_OPTION_NOT_SUPPORTED, e.getError());
            }
        }

        // Test with wrong password number
        try {
            mST25DVTag.presentPassword((byte) 0xFF, ST25DVTests.mDefaultPassword, (byte) 0x02);
            fail("Expecting exception for command with wrong password number");
        } catch (STException e) {
            assertEquals(STExceptionCode.ISO15693_BLOCK_NOT_AVAILABLE, e.getError());
        }
    }

    static void testWritePassword() throws STException {
        // Write null password
        try {
            mST25DVTag.writePassword((byte) 0, null, (byte) 0x02);
            fail("Expecting exception for null password");
        } catch (STException e) {
            assertEquals(STExceptionCode.BAD_PARAMETER, e.getError());
        }

        // For each available password
        for (int i = 0; i < 4; i++) {
            // Try to write new password without presenting the correct one first
            try {
                mST25DVTag.writePassword((byte) i, mNewPassword, (byte) 0x02);
                fail("Expecting exception for changing password without presenting the old one first");
            } catch (STException e) {
                assertEquals(STExceptionCode.ISO15693_BLOCK_IS_LOCKED, e.getError());
            }

            // Present correct default password
            mST25DVTag.presentPassword((byte) i, ST25DVTests.mDefaultPassword, (byte) 0x02);

            // Test writePassword command string with Eval mode
            mST25DVTag.getReaderInterface().setTransceiveMode(TransceiveMode.EVAL);
            try {
                mST25DVTag.writePassword((byte) i, mNewPassword, (byte) 0x02);
                fail("Transceive in EVAL mode should throw an exception");
            } catch (STException e) {
                if (e.getError() == STExceptionCode.TRANSCEIVE_EVAL_MODE) {
                    byte[] expectedCommand = ArrayUtils.addAll(new byte[]{
                        0x02,
                        (byte) 0xB1,
                        0x02,
                        (byte) i},
                            mNewPassword);
                    assertArrayEquals("Check that the command is well formed", expectedCommand, e.getErrorData());
                } else {
                    fail("Unexpected exception");
                }
            } finally {
                mST25DVTag.getReaderInterface().setTransceiveMode(TransceiveMode.NORMAL);
            }

            // Write new password
            mST25DVTag.writePassword((byte) i, mNewPassword);

            // Present wrong password
            try {
                mST25DVTag.presentPassword((byte) i, mWrongPassword);
                fail("Expecting exception for wrong password");
            } catch (STException e) {
                if (e.getError() == STExceptionCode.CMD_FAILED) {
                    byte[] expectedError = new byte[]{0x01, 0x0F};
                    assertArrayEquals("Check that the command failed with the correct ISO error code", expectedError, e.getErrorData());
                } else {
                    fail("Unexpected exception");
                }
            }

            // Present correct new password
            mST25DVTag.presentPassword((byte) i, mNewPassword, (byte) 0x02);

            // Write back default password
            mST25DVTag.writePassword((byte) i, ST25DVTests.mDefaultPassword, (byte) 0x02);
        }
    }

}

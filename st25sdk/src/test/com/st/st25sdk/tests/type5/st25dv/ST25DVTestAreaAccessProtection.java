package com.st.st25sdk.tests.type5.st25dv;

import static com.st.st25sdk.TagHelper.ReadWriteProtection.READABLE_AND_WRITABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.Assert;

import com.st.st25sdk.STException;
import com.st.st25sdk.STException.STExceptionCode;
import com.st.st25sdk.STLog;
import com.st.st25sdk.TagHelper;
import com.st.st25sdk.type5.ST25DVTag;
import com.st.st25sdk.type5.STType5PasswordInterface;

public class ST25DVTestAreaAccessProtection {

    final static byte[] mPassword1 = new byte[]{0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
    final static byte[] mPassword2 = new byte[]{0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02};
    final static byte[] mPassword3 = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

    public static final HashMap<Integer, byte[]> passwordValue = new HashMap<>();
    static {
        passwordValue.put(0, ST25DVTests.mDefaultPassword);
        passwordValue.put(1, mPassword1);
        passwordValue.put(2, mPassword2);
        passwordValue.put(3, mPassword3);
    }

    static TagHelper.ReadWriteProtection rwProtection = null;

    static public void run(ST25DVTag st25DVTag) throws STException {

        ST25DVTests.setFourAreaWithoutProtections();

        // Define new values for all 3 passwords (not touching configuration password here)
        for (int i = 1; i < 4; i++) {
            try {
                st25DVTag.presentPassword((byte) i, ST25DVTests.mDefaultPassword);
                st25DVTag.writePassword((byte) i, passwordValue.get(i));
            } catch (STException e) {
                fail("Not expecting exceptions");
            }
        }

        //////////////////////////////////////////////////////////////////
        STLog.i("Test ST25DV set/getPasswordNumber");
        testSetGetPasswordNumber(st25DVTag);

        //////////////////////////////////////////////////////////////////
        STLog.i("Test ST25DV set/getReadWriteProtection");
        testSetGetReadWriteProtection(st25DVTag);

        //////////////////////////////////////////////////////////////////

        // Write back default protection
        for (int area = ST25DVTag.AREA1; area <= ST25DVTag.AREA4; area++) {
            try {
                st25DVTag.setReadWriteProtection(area, READABLE_AND_WRITABLE, ST25DVTests.mDefaultPassword);
            } catch (STException e) {
                fail("Not expecting exceptions");
            }

            try {
                rwProtection = st25DVTag.getReadWriteProtection(area);
            } catch (STException e) {
                fail("Not expecting exceptions");
            }
            Assert.assertEquals(READABLE_AND_WRITABLE, rwProtection);
        }

        // Write back default passwords
        for (int i = 1; i < 4; i++) {
            try {
                st25DVTag.presentPassword((byte) i, passwordValue.get(i));
                st25DVTag.writePassword((byte) i, ST25DVTests.mDefaultPassword);
            } catch (STException e) {
                fail("Not expecting exceptions");
            }
        }

        // Keep config session open for other tests
        st25DVTag.presentPassword((byte) 0, ST25DVTests.mDefaultPassword);

        //////////////////////////////////////////////////////////////////
        STLog.i("Test End");
    }


    private static void testSetGetPasswordNumber(ST25DVTag st25DVTag) throws STException {
        // Present configuration passwrod to open session
        st25DVTag.presentPassword((byte) 0, ST25DVTests.mDefaultPassword);

        // Set passwords
        for (int area = ST25DVTag.AREA1, pwd = 1; area <= ST25DVTag.AREA4; area++, pwd++) {
            try {
                if (area == ST25DVTag.AREA4) {
                    // AREA4 is not protected by password
                    pwd = 0;
                }
                // All other areas are protected by a different password
                st25DVTag.setPasswordNumber(area, (byte) pwd);
            } catch (STException e) {
                fail("Not expecting exceptions");
            }
        }

        // Check that passwords were well written in cache
        for (int area = ST25DVTag.AREA1, pwd = 1; area <= ST25DVTag.AREA4; area++, pwd++) {
            byte pwdNumber = 0;
            try {
                if (area == ST25DVTag.AREA4) {
                    // AREA4 is not protected by password
                    pwd = 0;
                }
                // All other areas are protected by a different password
                pwdNumber = st25DVTag.getPasswordNumber(area);
            } catch (STException e) {
                fail("Not expecting exceptions");
            }
            if (area == ST25DVTag.AREA4) {
                assertEquals(0, pwdNumber);
            } else {
                assertEquals(pwd, pwdNumber);
            }
        }

        // Check that passwords were well written on the tag
        st25DVTag.deactivateCache();

        for (int area = ST25DVTag.AREA1, pwd = 1; area <= ST25DVTag.AREA4; area++, pwd++) {
            byte pwdNumber = 0;
            try {
                if (area == ST25DVTag.AREA4) {
                    // AREA4 is not protected by password
                    pwd = 0;
                }
                // All other areas are protected by a different password
                pwdNumber = st25DVTag.getPasswordNumber(area);
            } catch (STException e) {
                fail("Not expecting exceptions");
            }

            if (area == ST25DVTag.AREA4) {
                assertEquals(0, pwdNumber);
            } else {
                assertEquals(pwd, pwdNumber);
            }

            // Check getAreaPasswordLength
            STType5PasswordInterface.PasswordLength length = STType5PasswordInterface.PasswordLength.PWD_ON_32_BITS;
            try {
                if (area == ST25DVTag.AREA4) {
                    // AREA4 is not protected by password
                    pwd = 0;
                }
                // All other areas are protected by a different password
                length = st25DVTag.getAreaPasswordLength(area);
            } catch (STException e) {
                if (area != ST25DVTag.AREA4 || e.getError() != STExceptionCode.BAD_PARAMETER)
                    fail("Not expecting exceptions");
            }

            if (area == ST25DVTag.AREA4) {
                assertEquals(0, pwdNumber);
            } else {
                assertEquals(pwd, pwdNumber);
                assertEquals(STType5PasswordInterface.PasswordLength.PWD_ON_64_BITS, length);
            }
        }

        st25DVTag.activateCache();

        // Test wrong parameters
        try {
            st25DVTag.setPasswordNumber(-1, (byte) 0);
            fail("Expecting exception for negative parameter value");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            st25DVTag.setPasswordNumber(ST25DVTag.AREA1, (byte) 5);
            fail("Expecting exception for wrong parameter value");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            st25DVTag.getPasswordNumber(-1);
            fail("Expecting exception for negative parameter value");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            st25DVTag.getAreaPasswordLength(-1);
            fail("Expecting exception for negative parameter value");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            st25DVTag.getAreaPasswordLength(18);
            fail("Expecting exception for wrong parameter value");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }
    }


    private static void testSetGetReadWriteProtection(ST25DVTag st25DVTag) throws STException {
        for (int area = ST25DVTag.AREA1; area <= ST25DVTag.AREA4; area++) {
            try {
                // Pass configuration password
                st25DVTag.setReadWriteProtection(area, READABLE_AND_WRITABLE, ST25DVTests.mDefaultPassword);
            } catch (STException e) {
                fail("Not expecting exceptions");
            }

            try {
                rwProtection = st25DVTag.getReadWriteProtection(area);
            } catch (STException e) {
                fail("Not expecting exceptions");
            }
            Assert.assertEquals(READABLE_AND_WRITABLE, rwProtection);
        }

        // Test wrong parameters
        try {
            st25DVTag.setReadWriteProtection(-1, READABLE_AND_WRITABLE, ST25DVTests.mDefaultPassword);
            fail("Expecting exception for negative parameter value");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            st25DVTag.setReadWriteProtection(-1, READABLE_AND_WRITABLE);
            fail("Expecting exception for negative parameter value");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            rwProtection = st25DVTag.getReadWriteProtection(-1);
            fail("Expecting exception for negative parameter value");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            rwProtection = st25DVTag.getReadWriteProtection(-1);
            fail("Expecting exception for negative parameter value");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }


    }
}

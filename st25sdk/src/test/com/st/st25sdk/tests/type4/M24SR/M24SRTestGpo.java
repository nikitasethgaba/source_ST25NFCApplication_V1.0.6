package com.st.st25sdk.tests.type4.M24SR;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;

import com.st.st25sdk.RFReaderInterface.TransceiveMode;
import com.st.st25sdk.STException;
import com.st.st25sdk.type4a.STType4GpoInterface.GpoMode;
import com.st.st25sdk.type4a.m24srtahighdensity.M24SRTag;

public class M24SRTestGpo {
    static M24SRTag mTag;

    // These are test values for Gpo mode field
    // TODO: check if support of i2c GPO modes are also required (0x01, 0x02, 0x3,...)
    static public final HashMap<GpoMode,Byte> expGpoValues = new HashMap<GpoMode,Byte>() {
        {
            put(GpoMode.GPO_HIGH_IMPEDANCE,(byte)0x00);
            put(GpoMode.GPO_SESSION_OPENED,(byte)0x10);
            put(GpoMode.GPO_WIP,(byte)0x20);
            put(GpoMode.GPO_MIP,(byte)0x30);
            put(GpoMode.GPO_INTERRUPT,(byte)0x40);
            put(GpoMode.GPO_STATE_CONTROL,(byte)0x50);
            put(GpoMode.GPO_RF_BUSY,(byte)0x60);
        }
    };


    static public void run (M24SRTag tag) throws STException {
        mTag = tag;
        // M24SR doesn't allow to change the GPO modes from RF
        // so only run the M24SR Gpo Mode READ test
        testGpoRead();

        // test M24SR GPO exceptions
        testGpoErrors();

        // test M24SR GPO interrupt & StateControl
        testGpoControl();
    }

    static private void testGpoRead() throws STException {
        List<M24SRTag.GpoMode> gpoModeList = mTag.getSupportedGpoModes();

        Assert.assertEquals(expGpoValues.size(), gpoModeList.size());

        for(M24SRTag.GpoMode gpoMode : expGpoValues.keySet()) {
            Assert.assertTrue(gpoModeList.contains(gpoMode));
            Assert.assertEquals(mTag.getGpoMode(expGpoValues.get(gpoMode)), gpoMode);
        }

        // No value to check against (depends on the last i2c configuration of the M24SR)
        // Just checks that it doesn't cast any exception!
        mTag.getGpo();

        // Confirm that the M24SR doesn't allow GPO config by the RF
        Assert.assertFalse(mTag.isGpoConfigurableByRf());

    }

    static private void testGpoErrors () {
        try {
            mTag.setGpo(expGpoValues.get(GpoMode.GPO_HIGH_IMPEDANCE));
            fail("setGpo didn't throw a NOT_SUPPORTED exception");

        } catch (STException e) {
            assertEquals(STException.STExceptionCode.NOT_SUPPORTED, e.getError());
        }

        try {
            mTag.setGpoMode(GpoMode.GPO_HIGH_IMPEDANCE);
            fail("setGpoMode didn't throw a NOT_SUPPORTED exception");

        } catch (STException e) {
            assertEquals(STException.STExceptionCode.NOT_SUPPORTED, e.getError());
        }

        try {
            // Call getGpoMode with a bad parameter
            mTag.getGpoMode((byte) 0xFF);
            fail("getGpoMode didn't throw a BAD_PARAMETER exception");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }
    }

    static private void testGpoControl () throws STException {
        byte[] actualCmd;

        try {
            mTag.getReaderInterface().setTransceiveMode(TransceiveMode.RECORD);
            mTag.sendInterruptCommand();
            Assert.assertTrue("sendInterruptCommand didn't trow an INVALID_CMD_PARAM when expected", mTag.isGpoInInterruptedMode());
        } catch (STException e) {
            if (mTag.isGpoInInterruptedMode()) {
                fail("sendInterruptCommand trew an exception while enabled");
            } else {
                assertEquals(STException.STExceptionCode.INVALID_CMD_PARAM, e.getError());
            }
        } finally {
            mTag.getReaderInterface().setTransceiveMode(TransceiveMode.NORMAL);
        }
        actualCmd = mTag.getReaderInterface().getLastTransceivedData();
        Assert.assertArrayEquals(new byte[] {(byte)0xA2,(byte)0xD6,0x00,0x1E,0x00},
                Arrays.copyOfRange(actualCmd, 1 ,actualCmd.length));

        try {
            mTag.getReaderInterface().setTransceiveMode(TransceiveMode.RECORD);
            mTag.setStateControlCommand(0);
            Assert.assertTrue("setStateControlCommand didn't trow an INVALID_CMD_PARAM when expected", mTag.isGpoInStateControlMode());
        } catch (STException e) {
            if (mTag.isGpoInStateControlMode()) {
                fail("setStateControlCommand trew an exception while enabled");
            } else {
                assertEquals(STException.STExceptionCode.INVALID_CMD_PARAM, e.getError());
            }
        } finally {
            mTag.getReaderInterface().setTransceiveMode(TransceiveMode.NORMAL);
        }
        actualCmd = mTag.getReaderInterface().getLastTransceivedData();
        Assert.assertArrayEquals(new byte[] {(byte)0xA2,(byte)0xD6,0x00,0x1F,0x01,0x00},
                Arrays.copyOfRange(actualCmd, 1 ,actualCmd.length));

    }

}

/*
 * @author STMicroelectronics MMY Application team
 *
 ******************************************************************************
 * @attention
 *
 * <h2><center>&copy; COPYRIGHT 2017 STMicroelectronics</center></h2>
 *
 * Licensed under ST MIX_MYLIBERTY SOFTWARE LICENSE AGREEMENT (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *        http://www.st.com/Mix_MyLiberty
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied,
 * AND SPECIFICALLY DISCLAIMING THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************
 */

package com.st.st25sdk.tests.type5.st25dv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Assume;

import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.type5.ST25DVTag;


public class ST25DVTestMailbox {

    static ST25DVTag mST25DVTag = null;

    // Test data
    static byte[] data          = new byte[]{0,1,2,3,4,5,6,7};
    static byte[] data0_3       = new byte[]{0,1,2,3};
    static byte[] data2_6       = new byte[]{2,3,4,5,6};
    static byte[] fastData      = new byte[]{7,6,5,4,3,2,1,0};
    static byte[] fastData4_2   = new byte[]{4,3,2};
    static byte[] fastData7_5   = new byte[]{7,6,5};
    static boolean mFastCommandsAvailable = false;

    static public void run(ST25DVTag st25DVTag, String readerName) throws STException, InterruptedException {

        mST25DVTag = st25DVTag;

        // First make sure Vcc is available (required for the Mailbox test)
        Assume.assumeTrue("ST25DV Mailbox test not run due to Vcc not beeing detected!\n> Connect the ST25DV to a powered Discovery Kit",
                mST25DVTag.isVccOn());

        // There is no built-in mechanism to now if a feature is supported by a reader
        // so just rely on the test application to provide basic reader information.
        mFastCommandsAvailable = (readerName.contains("CR95HF") || readerName.contains("ST25R3911B-DISCO"));

        //////////////////////////////////////////////////////////////////
        STLog.i("Test Mailbox using regular commands");
        testMailboxWithRegularCommands();

        //////////////////////////////////////////////////////////////////
        STLog.i("Test Mailbox in non-addressed mode & writing only part of the data");
        testMailboxWithFlagAndSize();

        //////////////////////////////////////////////////////////////////
        STLog.i("Test Mailbox using fast commands");
        testMailboxFastCommands(readerName);

        //////////////////////////////////////////////////////////////////
        STLog.i("Test Mailbox fast commands in non-addressed mode & writing only part of the data");
        testMailboxFastCommandsWithFlagAndSize(readerName);

        //////////////////////////////////////////////////////////////////
        STLog.i("Test Mailbox dynamic config status methods");
        testMailboxStatus();

        //////////////////////////////////////////////////////////////////
        STLog.i("Test Mailbox API exceptions");
        testMailboxExceptions();

        //////////////////////////////////////////////////////////////////
        STLog.i("Test End");

    }

    /**
     * Enable the MB, and write a message
     * Read message length & read back the message (partially & full message)
     * @throws STException
     */
    static private void testMailboxWithRegularCommands() throws STException {
        // enable Mailbox
        mST25DVTag.writeDynConfig(ST25DVTag.REGISTER_DYN_MB_CTRL_ADDRESS, (byte)0x01);

        // write a message
        mST25DVTag.writeMailboxMessage(data);

        // check that Mailbox has been enabled and message has been written
        byte[] response = mST25DVTag.readDynConfig(ST25DVTag.REGISTER_DYN_MB_CTRL_ADDRESS);
        Assert.assertEquals(0x85, 0xFF & response[1]);

        // check message length
        int len = mST25DVTag.readMailboxMessageLength();
        Assert.assertEquals(data.length, len);

        // retrieve the message from offset 2, ignoring last byte
        response = mST25DVTag.readMailboxMessage((byte)2, len - 3);
        byte[] message = Arrays.copyOfRange(response, 1, response.length);
        Assert.assertArrayEquals(data2_6, message);

        // retrieve the full message
        response = mST25DVTag.readMailboxMessage((byte)0, len);
        message = Arrays.copyOfRange(response, 1, response.length);
        Assert.assertArrayEquals(data, message);

        // disable the Mailbox
        mST25DVTag.writeDynConfig(ST25DVTag.REGISTER_DYN_MB_CTRL_ADDRESS, (byte)0x00);
    }

    /**
     * Enable the MB, and write a message in non addressed mode and using a size < data length
     * Read message length & read back the message in non-addressed mode
     * @throws STException
     */
    static private void testMailboxWithFlagAndSize() throws STException {
        // enable Mailbox
        mST25DVTag.writeDynConfig(ST25DVTag.REGISTER_DYN_MB_CTRL_ADDRESS, (byte) 0x01);

        // write a message with the 4 first bytes & flag
        mST25DVTag.writeMailboxMessage(4, data, (byte)0x02);

        // check message length
        int len = mST25DVTag.readMailboxMessageLength();
        Assert.assertEquals(4, len);

        // retrieve the full message
        byte[] response = mST25DVTag.readMailboxMessage((byte)0, len, (byte) 0x02);
        byte[] message = Arrays.copyOfRange(response, 1, response.length);
        Assert.assertArrayEquals(data0_3, message);

        // disable the Mailbox
        mST25DVTag.writeDynConfig(ST25DVTag.REGISTER_DYN_MB_CTRL_ADDRESS, (byte)0x00);
    }

    /**
     * Enable the MB, and write a message using fast commands
     * Read message length & read back the message using fast commands (partially & full message)
     * @throws STException
     */
    static private void testMailboxFastCommands(String readerName) throws STException {
        // Disable test for readers that do not support fast mode
        Assume.assumeTrue("Fast commands available on select readers only", mFastCommandsAvailable);

        // enable Mailbox
        mST25DVTag.fastWriteDynConfig(ST25DVTag.REGISTER_DYN_MB_CTRL_ADDRESS, (byte)0x01);

        byte[] response = mST25DVTag.fastReadDynConfig(ST25DVTag.REGISTER_DYN_MB_CTRL_ADDRESS);

        // write a message
        mST25DVTag.fastWriteMailboxMessage(fastData);

        // check that Mailbox has been enabled
        response = mST25DVTag.fastReadDynConfig(ST25DVTag.REGISTER_DYN_MB_CTRL_ADDRESS);
        Assert.assertEquals(0x85, 0xFF & response[1]);

        // check message length
        int len = mST25DVTag.fastReadMailboxMessageLength();
        Assert.assertEquals(fastData.length, len);

        // retrieve the message from offset 3, ignoring 2 last bytes
        response = mST25DVTag.fastReadMailboxMessage((byte)3, len - 5);
        byte[] message = Arrays.copyOfRange(response, 1, response.length);
        Assert.assertArrayEquals(fastData4_2, message);

        // retrieve the message
        response = mST25DVTag.fastReadMailboxMessage((byte)0, len);
        message = Arrays.copyOfRange(response, 1, response.length);
        Assert.assertArrayEquals(fastData, message);

        // disable the MB
        mST25DVTag.fastWriteDynConfig(ST25DVTag.REGISTER_DYN_MB_CTRL_ADDRESS, (byte)0x00);
    }

    /**
     * Enable the MB, and write a message in non addressed mode and using a size < data length
     * Read message length & read back the message in non-addressed mode
     * Using fast commands
     * @throws STException
     */
    static private void testMailboxFastCommandsWithFlagAndSize(String readerName) throws STException {
        // Disable test for readers that do not support fast mode
        Assume.assumeTrue("Fast commands available on select readers only", mFastCommandsAvailable);

        // enable Mailbox
        mST25DVTag.fastWriteDynConfig(ST25DVTag.REGISTER_DYN_MB_CTRL_ADDRESS, (byte)0x01);

        // write a message with the 3 first bytes & flag
        mST25DVTag.fastWriteMailboxMessage(3, fastData, (byte)0x02);

        // check message length
        int len = mST25DVTag.fastReadMailboxMessageLength();
        Assert.assertEquals(3, len);

        // retrieve the full message
        byte[] response = mST25DVTag.fastReadMailboxMessage((byte)0, len, (byte)0x02);
        byte[] message = Arrays.copyOfRange(response, 1, response.length);
        Assert.assertArrayEquals(fastData7_5, message);

        // disable the Mailbox
        mST25DVTag.fastWriteDynConfig(ST25DVTag.REGISTER_DYN_MB_CTRL_ADDRESS, (byte)0x00);
    }

    static private void checkMBStatus( boolean refresh,
            boolean useFastCmd,
            boolean enable,
            boolean hostPutMsg,
            boolean rfPutMsg,
            boolean hostMissMsg,
            boolean rfMissMsg) throws STException {

        // check first using the fast command control boolean
        Assert.assertEquals(mST25DVTag.isMBEnabled(refresh, useFastCmd), enable);
        Assert.assertEquals(mST25DVTag.hasHostPutMsg(refresh, useFastCmd), hostPutMsg);
        Assert.assertEquals(mST25DVTag.hasRFPutMsg(refresh, useFastCmd), rfPutMsg);
        Assert.assertEquals(mST25DVTag.hasHostMissMsg(refresh, useFastCmd), hostMissMsg);
        Assert.assertEquals(mST25DVTag.hasRFMissMsg(refresh, useFastCmd), rfMissMsg);

        // Then test using the simpler prototype (no fast command control => regular command used)
        if (! useFastCmd) {
            Assert.assertEquals(mST25DVTag.isMBEnabled(refresh), enable);
            Assert.assertEquals(mST25DVTag.hasHostPutMsg(refresh), hostPutMsg);
            Assert.assertEquals(mST25DVTag.hasRFPutMsg(refresh), rfPutMsg);
            Assert.assertEquals(mST25DVTag.hasHostMissMsg(refresh), hostMissMsg);
            Assert.assertEquals(mST25DVTag.hasRFMissMsg(refresh), rfMissMsg);
        }
    }

    /**
     * 1. Disable the MB & check Dynamic status (with auto-refresh)
     * 2. Enable the MB & check Dynamic status (with manual refresh)
     * 3. Write a Msg to the MB & check Dynamic status (with fast commands & manual refresh)
     * 4. Configure & wait MB watchdog & check Dynamic status (with auto-refresh & fast commands)
     * 5. Reset the MB (with tag API) & check Dynamic status
     * 6. Disable the MB (with tag API) & check Dynamic status
     * 7. Enable the MB (with tag API) & check Dynamic status
     * @throws STException
     * @throws InterruptedException
     */
    static private void testMailboxStatus() throws STException, InterruptedException {
        boolean fast = mFastCommandsAvailable;

        // disable watchdog
        mST25DVTag.writeConfig(ST25DVTag.REGISTER_MB_WDG_ADDRESS, (byte)0x00);

        STLog.i("1. Disable the MB & check Dynamic status (with auto-refresh)");
        // disable the Mailbox
        mST25DVTag.writeDynConfig(ST25DVTag.REGISTER_DYN_MB_CTRL_ADDRESS, (byte)0x00);
        //          refresh fast  enabled hstMsg rfMsg hstMiss rfMiss
        checkMBStatus(true, false, false, false, false, false, false);

        STLog.i("2. Enable the MB & check Dynamic status (with manual refresh)");
        // enable the Mailbox
        mST25DVTag.writeDynConfig(ST25DVTag.REGISTER_DYN_MB_CTRL_ADDRESS, (byte)0x01);
        // check with no refresh
        //          refresh fast  enabled hstMsg rfMsg hstMiss rfMiss
        checkMBStatus(false, false, false, false, false, false, false);
        // check after refresh
        mST25DVTag.refreshMBStatus();
        //          refresh fast  enabled hstMsg rfMsg hstMiss rfMiss
        checkMBStatus(false, false, true, false, false, false, false);

        STLog.i("3. Write a Msg to the MB & check Dynamic status (with fast commands & manual refresh)");
        // write a message
        mST25DVTag.writeMailboxMessage(data);
        // check with no refresh
        //          refresh fast  enabled hstMsg rfMsg hstMiss rfMiss
        checkMBStatus(false, fast,  true, false, false, false, false);
        // check after refresh (using fast command)
        mST25DVTag.refreshMBStatus(fast);
        //          refresh fast  enabled hstMsg rfMsg hstMiss rfMiss
        checkMBStatus(false, fast,  true, false, true, false, false);

        STLog.i("4. Configure & wait MB watchdog & check Dynamic status (with auto-refresh & fast commands)");
        // Configure the MB watchdog to timeout after max 36 ms!
        mST25DVTag.writeConfig(ST25DVTag.REGISTER_MB_WDG_ADDRESS, (byte)0x01);
        TimeUnit.MILLISECONDS.sleep(50);
        // check after refresh
        //          refresh fast  enabled hstMsg rfMsg hstMiss rfMiss
        checkMBStatus(true, fast,  true, false, false, true, false);

        STLog.i("5. Reset the MB (with tag API) & check Dynamic status");
        // writeDynConfig modify the Tag without updating the cache, so invalidate it
        mST25DVTag.invalidateCache();
        // reset the MB
        mST25DVTag.resetMB();
        //          refresh fast  enabled hstMsg rfMsg hstMiss rfMiss
        checkMBStatus(true, false, true, false, false, false, false);

        STLog.i("6. Disable the MB (with tag API) & check Dynamic status");
        mST25DVTag.disableMB();
        //          refresh fast  enabled hstMsg rfMsg hstMiss rfMiss
        checkMBStatus(true, fast,  false, false, false, false, false);

        STLog.i("7. Enable the MB (with tag API) & check Dynamic status");
        mST25DVTag.enableMB();
        //          refresh fast  enabled hstMsg rfMsg hstMiss rfMiss
        checkMBStatus(true, false, true, false, false, false, false);
        checkMBStatus(true, fast,  true, false, false, false, false);

        // disable MB
        mST25DVTag.disableMB();
        // disable watchdog
        mST25DVTag.writeConfig(ST25DVTag.REGISTER_MB_WDG_ADDRESS, (byte)0x00);

    }

    static private void testMailboxExceptions () throws STException {
        // enable Mailbox
        mST25DVTag.writeDynConfig(ST25DVTag.REGISTER_DYN_MB_CTRL_ADDRESS, (byte)0x01);

        try {
            // write an empty message
            mST25DVTag.writeMailboxMessage(0,new byte[]{0},(byte) 0x02);
            fail("Expecting exception for message length of 0");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            // write an empty message
            mST25DVTag.writeMailboxMessage(2,null,(byte) 0x02);
            fail("Expecting exception for a null message");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            // write an empty message
            mST25DVTag.writeMailboxMessage(2,new byte[]{0},(byte) 0x02);
            fail("Expecting exception for a message too short");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            // fast write an empty message
            mST25DVTag.fastWriteMailboxMessage(0,new byte[]{0},(byte) 0x02);
            fail("Expecting exception for message length of 0");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            // write an empty message
            mST25DVTag.fastWriteMailboxMessage(2,null,(byte) 0x02);
            fail("Expecting exception for a null message");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }

        try {
            // write an empty message
            mST25DVTag.fastWriteMailboxMessage(2,new byte[]{0},(byte) 0x02);
            fail("Expecting exception for a message too short");
        } catch (STException e) {
            assertEquals(STException.STExceptionCode.BAD_PARAMETER, e.getError());
        }


        // disable MB
        mST25DVTag.disableMB();
    }
}

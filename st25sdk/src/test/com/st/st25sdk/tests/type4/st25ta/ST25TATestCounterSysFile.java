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

package com.st.st25sdk.tests.type4.st25ta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Assume;

import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.ndef.NDEFMsg;
import com.st.st25sdk.ndef.TextRecord;
import com.st.st25sdk.type4a.st25ta.ST25TATag;


public class ST25TATestCounterSysFile {

    static public void run(ST25TATag st25TATag) throws STException {

        //////////////////////////////////////////////////////////////////
        STLog.i("Test ST25TA counter");

        // Set this to true if you really want to lock the Event Counter configuration
        // Warning it will not be possible to unlock Event Counter configuration anymore!
        boolean lockEventCounterForReal = false;

        NDEFMsg ndefMsgToWrite = new NDEFMsg();

        boolean isCounterLocked = false;
        boolean isCounterEnabled = false;
        boolean isCounterIncrementedOnRead = false;
        boolean isCounterIncrementedOnWrite = false;
        int counterValue;
        byte[] expectedCounterBytes = new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0x01};
        int expectedCounterValue = 1;

        //Check Counter is not locked
        isCounterLocked = st25TATag.isCounterLocked();
        // Current implementation of counter tests, assumes that the counter is not locked
        Assume.assumeFalse("Test on counter if counter is not locked", isCounterLocked);

        //Disable Counter
        st25TATag.disableCounter();

        //Check Counter disabled
        isCounterEnabled = st25TATag.isCounterEnabled();
        Assert.assertFalse(isCounterEnabled);

        //Enable Counter
        st25TATag.enableCounter();

        isCounterEnabled = st25TATag.isCounterEnabled();
        Assert.assertTrue(isCounterEnabled);

        //Set write Configuration Counter
        st25TATag.incrementCounterOnWrite();
        isCounterIncrementedOnWrite = st25TATag.isCounterIncrementedOnWrite();
        Assert.assertTrue(isCounterIncrementedOnWrite);

        // Create a NDEF containing a Text Record
        TextRecord textRecordToWrite = new TextRecord("Hello world!");
        ndefMsgToWrite.addRecord(textRecordToWrite);
        // Write the NDEF to the tag
        st25TATag.writeNdefMessage(ndefMsgToWrite);

        //Check counter incremented after Write NDEF Message
        counterValue = st25TATag.getCounterValue();
        Assert.assertEquals(expectedCounterValue, counterValue);

        byte[] counterBytes = st25TATag.getCounterBytes();
        Assert.assertArrayEquals(expectedCounterBytes, counterBytes);

        //Set read Configuration Counter
        st25TATag.incrementCounterOnRead();
        isCounterIncrementedOnRead = st25TATag.isCounterIncrementedOnRead();
        Assert.assertTrue(isCounterIncrementedOnRead);

        //Check counter incremented after Read NDEF Message
        st25TATag.readNdefMessage();
        counterValue = st25TATag.getCounterValue();
        Assert.assertEquals(expectedCounterValue, counterValue);

        //Disable Counter
        st25TATag.disableCounter();
        isCounterEnabled = st25TATag.isCounterEnabled();
        Assert.assertFalse(isCounterEnabled);

        //Locked Counter
        if (lockEventCounterForReal)  {
            st25TATag.lockCounter();

            //Check Counter is locked
            isCounterLocked = st25TATag.isCounterLocked();
            Assume.assumeTrue(isCounterLocked);

            //lock again Event Counter
            try {
                st25TATag.lockCounter();
                fail("The lock command should have failed!");

            } catch (STException e) {
                assertEquals(STException.STExceptionCode.INVALID_USE_CONTEXT, e.getError());
            }
        }
    }

}

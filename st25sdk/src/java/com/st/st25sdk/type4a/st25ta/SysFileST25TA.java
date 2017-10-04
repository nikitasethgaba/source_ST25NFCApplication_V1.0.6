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

package com.st.st25sdk.type4a.st25ta;

import java.nio.ByteBuffer;

import com.st.st25sdk.STException;
import com.st.st25sdk.command.Type4Command;
import com.st.st25sdk.command.Type4CustomCommand;
import com.st.st25sdk.type4a.STSysFileType4;
import com.st.st25sdk.type4a.STType4CounterInterface;

public class SysFileST25TA extends STSysFileType4 implements STType4CounterInterface {


    /**
     * Cache management
     */

    protected byte mEventCounter;
    protected byte mReservedSysByte;
    protected byte[] mCounterBytes = new byte[3];
    protected byte mProductVersion;

    private static final byte COUNTER_INCREMENT_BIT = (byte) 0x01;
    private static final byte COUNTER_INCREMENT_ON_READ = (byte) 0x00;
    private static final byte COUNTER_INCREMENT_ON_WRITE = (byte) 0x01;

    private static final byte COUNTER_ENABLED = (byte) 0x02;
    private static final byte COUNTER_LOCKED = (byte) 0x80;

    /** */

    public SysFileST25TA(Type4CustomCommand type4CustomCommand) {
        super(type4CustomCommand);
    }

    public byte getReservedSysByte() throws STException {
        checkCache();
        return mReservedSysByte;
    }

    public byte getProductCode() throws STException {
        checkCache();
        return mProductCode;
    }

    @Override
    public byte getEventCounter() throws STException {
        checkCache();
        return mEventCounter;
    }

    @Override
    public byte[] getCounterBytes() throws STException {
        // force the cache to be updated Counter can be updated on each Read/Write
        invalidateCache();
        checkCache();
        return mCounterBytes;
    }

    @Override
    public int getCounterValue() throws STException {
        byte[] counterBytes = getCounterBytes();

        if ( (counterBytes == null) || (counterBytes.length != 3) ) {
            throw new STException(STException.STExceptionCode.CMD_FAILED);
        }

        // counterBytes[2] contains the LSByte
        int counterValue = ( ( (counterBytes[0] & 0xFF) << 16) +
                ( (counterBytes[1] & 0xFF) <<  8) +
                ( (counterBytes[2] & 0xFF) ) );

        return counterValue;
    }

    public byte getProductVersion() throws STException {
        checkCache();
        return mProductVersion;
    }

    @Override
    public boolean isCounterLocked() throws STException {
        byte lc = getEventCounter();
        // check if b7 is 1
        return ((lc & COUNTER_LOCKED) == COUNTER_LOCKED);
    }
    @Override
    public boolean isCounterEnabled() throws STException {
        byte lc = getEventCounter();
        // check if b2 is 1
        return ((lc & COUNTER_ENABLED) == COUNTER_ENABLED);
    }
    @Override
    public boolean isCounterIncrementedOnRead() throws STException {
        byte lc = getEventCounter();
        // check if b0 is 0
        return ((lc & COUNTER_INCREMENT_BIT) == COUNTER_INCREMENT_ON_READ);
    }
    @Override
    public boolean isCounterIncrementedOnWrite() throws STException {
        byte lc = getEventCounter();
        // check if b0 is 1
        return ((lc & COUNTER_INCREMENT_BIT) == COUNTER_INCREMENT_ON_WRITE);
    }


    @Override
    public void lockCounter() throws STException {
        byte lc = getEventCounter();
        // do not modify all bits except b7
        lc = (byte) (lc | COUNTER_LOCKED);
        synchronized (Type4Command.mLock) {
            select();
            ((Type4CustomCommand)mType4Command).setConfigCounter(lc);
            mEventCounter = lc;
        }
    }

    @Override
    public void enableCounter() throws STException {
        byte lc = getEventCounter();
        // b1 set to 1
        lc = (byte) (lc | COUNTER_ENABLED);
        synchronized (Type4Command.mLock) {
            select();
            ((Type4CustomCommand)mType4Command).setConfigCounter(lc);
            mEventCounter = lc;
        }
    }

    @Override
    public void disableCounter() throws STException {
        byte lc = getEventCounter();
        // b1 set to 0
        lc = (byte) (lc & ~COUNTER_ENABLED);
        synchronized (Type4Command.mLock) {
            select();
            ((Type4CustomCommand)mType4Command).setConfigCounter(lc);
            mEventCounter = lc;
        }
    }

    @Override
    public void incrementCounterOnRead() throws STException {
        byte lc = getEventCounter();
        lc = (byte) (lc & ~COUNTER_INCREMENT_BIT);
        synchronized (Type4Command.mLock) {
            select();
            ((Type4CustomCommand)mType4Command).setConfigCounter(lc);
            mEventCounter = lc;
        }
    }

    @Override
    public void incrementCounterOnWrite() throws STException {
        byte lc = getEventCounter();
        lc = (byte) (lc | COUNTER_INCREMENT_ON_WRITE);
        synchronized (Type4Command.mLock) {
            select();
            ((Type4CustomCommand)mType4Command).setConfigCounter(lc);
            mEventCounter = lc;
        }
    }

    @Override
    protected void parseSysFile(byte[] buffer) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        mLength = ((byteBuffer.get() & 0xFF) << 8);
        mLength +=  (byteBuffer.get() & 0xFF);
        mReservedSysByte = byteBuffer.get();
        mEventCounter = byteBuffer.get();
        byteBuffer.get(mCounterBytes, 0 , 3);
        mProductVersion = byteBuffer.get();
        byteBuffer.get(mUid, 0, 7);
        mMemorySizeInBytes = ((byteBuffer.get() & 0xFF) << 8);
        mMemorySizeInBytes +=  (byteBuffer.get() & 0xFF) + 1;
        mICRef = byteBuffer.get();

        mBuffer = byteBuffer.array();
        mLength = mBuffer.length;
    }


}

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

package com.st.st25sdk.type4a.m24srtahighdensity;

import java.nio.ByteBuffer;

import com.st.st25sdk.STException;
import com.st.st25sdk.command.Type4Command;

public class SysFileM24SR extends SysFileM24SRTAHighDensity {

    private M24SRTag mTag;


    /**
     * Cache management
     */
    protected byte mI2CProtected;
    protected byte mI2CWatchdog;
    protected byte mReservedSysByte;
    protected byte mGpo;
    protected byte mRFEnabled;

    public static final byte I2C_PROTECTED_OFFSET = 0x02;
    public static final byte I2C_WATCHDOG_OFFSET = 0x02;
    public static final byte I2C_GPO_OFFSET = 0x04;
    public static final byte I2C_RF_ENABLE_OFFSET = 0x06;

    public SysFileM24SR(Type4Command type4Command) {
        super(type4Command);
    }


    public byte getI2CProtected() throws STException {
        checkCache();
        return mI2CProtected;
    }

    public byte getI2CWatchdog() throws STException {
        checkCache();
        return mI2CWatchdog;
    }

    public byte getRfEnabled() throws STException {
        checkCache();
        return mRFEnabled;
    }

    public byte getGpo() throws STException {
        checkCache();
        return mGpo;
    }

    @Override
    public int getNDEFFileNumber() throws STException {
        checkCache();
        return mNDEFFileNumber;
    }



    @Override
    protected void parseSysFile(byte[] buffer) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        mLength = ((byteBuffer.get() & 0xFF) << 8);
        mLength +=  (byteBuffer.get() & 0xFF);
        mI2CProtected = byteBuffer.get();
        mI2CWatchdog = byteBuffer.get();
        mGpo = byteBuffer.get();
        mReservedSysByte = byteBuffer.get();
        mRFEnabled = byteBuffer.get();
        mNDEFFileNumber = ((byteBuffer.get() & 0xFF));
        byteBuffer.get(mUid, 0, 7);
        mMemorySizeInBytes = ((byteBuffer.get() & 0xFF) << 8);
        mMemorySizeInBytes +=  (byteBuffer.get() & 0xFF) + 1;
        mICRef = byteBuffer.get();

        mBuffer = byteBuffer.array();
        mLength = mBuffer.length;
    }

}

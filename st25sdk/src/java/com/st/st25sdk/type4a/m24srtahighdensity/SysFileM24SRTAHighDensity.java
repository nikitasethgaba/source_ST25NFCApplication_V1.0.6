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
import com.st.st25sdk.type4a.STSysFileType4;

public class SysFileM24SRTAHighDensity extends STSysFileType4 {

    /**
     * Cache management
     */

    protected int  mNDEFFileNumber;
    protected byte  mByte2Reserved;
    protected byte  mByte3Reserved;
    protected byte  mByte4Reserved;
    protected byte  mByte5Reserved;
    protected byte  mByte6Reserved;

    public SysFileM24SRTAHighDensity(Type4Command type4Command) {
        super(type4Command);
    }

    public int getNDEFFileNumber() throws STException {
        checkCache();
        return mNDEFFileNumber;
    }

    @Override
    protected void parseSysFile(byte[] buffer) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        mLength = ((byteBuffer.get() & 0xFF) << 8);
        mLength +=  (byteBuffer.get() & 0xFF);
        mByte2Reserved = byteBuffer.get();
        mByte3Reserved = byteBuffer.get();
        mByte4Reserved = byteBuffer.get();
        mByte5Reserved = byteBuffer.get();
        mByte6Reserved = byteBuffer.get();
        mNDEFFileNumber = (byteBuffer.get() & 0xFF);
        byteBuffer.get(mUid, 0, 7);
        mMemorySizeInBytes = ((byteBuffer.get() & 0xFF) << 8);
        mMemorySizeInBytes +=  (byteBuffer.get() & 0xFF) + 1;
        mICRef = byteBuffer.get();

        mBuffer = byteBuffer.array();
        mLength = mBuffer.length;
    }
}

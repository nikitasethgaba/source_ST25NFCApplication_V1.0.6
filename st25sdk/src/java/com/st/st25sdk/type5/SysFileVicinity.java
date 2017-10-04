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

package com.st.st25sdk.type5;

import com.st.st25sdk.Helper;
import com.st.st25sdk.STException;
import com.st.st25sdk.command.VicinityCommand;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class SysFileVicinity extends SysFile {

    private VicinityCommand mVicinityCommand;

    public SysFileVicinity(VicinityCommand vicinityCommand) {
        mVicinityCommand = vicinityCommand;
    }

    @Override
    public byte[] read() throws STException {

        if (!mCacheActivated  || mCacheInvalidated) {
            byte[] cmdBuffer;
            cmdBuffer = mVicinityCommand.getSystemInfo();
            if (cmdBuffer[0] != 0x00) {
                throw (new STException(STException.STExceptionCode.CMD_FAILED));
            }

            mBuffer = Arrays.copyOfRange(cmdBuffer, 1, cmdBuffer.length);
        }
        return mBuffer;
    }

    @Override
    protected void parseSysFile(byte[] buffer) {

        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        byte sysInfoByte = byteBuffer.get();

        byteBuffer.get(mUid, 0, 8);

        // Put UID in correct order
        mUid = Helper.reverseByteArray(mUid);

        if ((sysInfoByte & DSFID_MASK) == DSFID_MASK) {
            mDSFIDSupported = true;
            mDSFID = byteBuffer.get();
        } else
            mDSFIDSupported = false;

        if ((sysInfoByte & AFI_MASK) == AFI_MASK) {
            mAFISupported = true;
            mAFI = byteBuffer.get();
        } else
            mAFISupported = false;

        if ((sysInfoByte & VICC_MEM_SIZE_MASK) == VICC_MEM_SIZE_MASK) {
            mVICCMemSizesupported = true;
            mNumberOfBlocks = (byteBuffer.get() & 0xFF);
            mNumberOfBlocks += ((byteBuffer.get() & 0xFF) << 8);
            mBlockSize = (byteBuffer.get() & 0x1F) + 1;
        } else
            mVICCMemSizesupported = false;

        if ((sysInfoByte & ICREF_MASK) == ICREF_MASK) {
            mICRefSupported = true;
            mICRef = byteBuffer.get();
        } else
            mICRefSupported = false;

        mBuffer = byteBuffer.array();
        mLength = mBuffer.length;
    }

}

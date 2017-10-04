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
import com.st.st25sdk.command.Iso15693Command;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class SysFileType5Extended extends SysFileType5 {


    public SysFileType5Extended(Iso15693Command iso15693Command) {
        super(iso15693Command);
    }


    @Override
    public byte[] read() throws STException {

        byte[] cmdBuffer;

        if (!mCacheActivated  || mCacheInvalidated) {
            cmdBuffer = mIso15693Command.extendedGetSystemInfo();

            if (cmdBuffer[0] != 0x00) {
                // ExtendedGetSystemInfo command failed, try with GetSystemInfo
                cmdBuffer = mIso15693Command.getSystemInfo();
                if (cmdBuffer[0] != 0x00) {
                    throw (new STException(STException.STExceptionCode.CMD_FAILED));
                }
            }
            mBuffer = Arrays.copyOfRange(cmdBuffer, 1, cmdBuffer.length);
        }

        return mBuffer;
    }


    protected void parseSysFile(byte[] buffer) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        byte sysInfoByte = byteBuffer.get();

        byteBuffer.get(mUid, 0, 8);

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

        // If buffer[0] > 0x0F, then we assume that buffer is the response from extendedGetSystemInfo (0x3B) intead of getSystemInfo (0x2B).
        if (sysInfoByte > 0x0F) {
            // In this case, memory size is coded on 2 bytes instead of 1
            if ((sysInfoByte & VICC_MEM_SIZE_MASK) == VICC_MEM_SIZE_MASK) {
                mVICCMemSizesupported = true;
                mNumberOfBlocks = (byteBuffer.get() & 0x00FF);
                mNumberOfBlocks += ((byteBuffer.get() & 0x00FF) << 8);
                mBlockSize = (byteBuffer.get() & 0x1F) + 1;
            } else
                mVICCMemSizesupported = false;

        } else {
            // getSystemInfo response: memory size coded on 1 byte
            if ((sysInfoByte & VICC_MEM_SIZE_MASK) == VICC_MEM_SIZE_MASK) {
                mVICCMemSizesupported = true;
                mNumberOfBlocks = byteBuffer.get() & 0xFF;
                mBlockSize = (byteBuffer.get() & 0x1F) + 1;
            } else
                mVICCMemSizesupported = false;
        }

        if ((sysInfoByte & ICREF_MASK) == ICREF_MASK) {
            mICRefSupported = true;
            mICRef = byteBuffer.get();
        } else
            mICRefSupported = false;

        if (sysInfoByte > 0x0F) {
            // If the MOI bit contained in the response flag is set to 1, then the tag operates in 2-byte addressing mode
            m2ByteAddressing = ((buffer[0] & MOI_VALUE) == MOI_VALUE);

            // Reverse the order of the 4-byte supported command list if it is present in the response
            if ((sysInfoByte & VICC_CMD_LIST_MASK) == VICC_CMD_LIST_MASK) {
                mVICCCommandListSupported = true;
                byteBuffer.get(mVICCCommandList, 0, 4);
                mVICCCommandList = Helper.reverseByteArray(mVICCCommandList);
            } else
                mVICCCommandListSupported = false;
        }

        mBuffer = byteBuffer.array();
        mLength = mBuffer.length;
    }

}

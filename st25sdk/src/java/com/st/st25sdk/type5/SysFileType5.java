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

import com.st.st25sdk.STException;
import com.st.st25sdk.command.Iso15693Command;

import java.util.Arrays;

public class SysFileType5 extends SysFile {

    protected Iso15693Command mIso15693Command;

    protected boolean mVICCCommandListSupported = false;
    protected boolean m2ByteAddressing = false;
    protected byte[] mVICCCommandList = new byte[4];

    protected static final byte MOI_VALUE = 0x10;
    protected static final byte VICC_CMD_LIST_MASK = 0x20;


    public SysFileType5(Iso15693Command iso15693Command) {
        mIso15693Command = iso15693Command;
    }

    /**
     * @return the mMOI
     */
    public boolean getMOI() throws STException {
        checkCache();
        return m2ByteAddressing;
    }

    /**
     * @return the list of supported commands.
     */
    public byte[] getVICCCommandList() throws STException {
        checkCache();
        return mVICCCommandList;
    }

    @Override
    public byte[] read() throws STException {

        if (!mCacheActivated  || mCacheInvalidated) {
            byte[] cmdBuffer;

            cmdBuffer = mIso15693Command.getSystemInfo();
            if (cmdBuffer == null || cmdBuffer[0] != 0x00) {
                throw (new STException(STException.STExceptionCode.CMD_FAILED));
            }

            mBuffer = Arrays.copyOfRange(cmdBuffer, 1, cmdBuffer.length);

        }

        return mBuffer;
    }
}

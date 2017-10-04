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

import static com.st.st25sdk.STException.STExceptionCode.BAD_PARAMETER;
import static com.st.st25sdk.STException.STExceptionCode.NOT_SUPPORTED;
import static com.st.st25sdk.type4a.STType4GpoInterface.GpoMode.GPO_HIGH_IMPEDANCE;
import static com.st.st25sdk.type4a.STType4GpoInterface.GpoMode.GPO_INTERRUPT;
import static com.st.st25sdk.type4a.STType4GpoInterface.GpoMode.GPO_MIP;
import static com.st.st25sdk.type4a.STType4GpoInterface.GpoMode.GPO_RF_BUSY;
import static com.st.st25sdk.type4a.STType4GpoInterface.GpoMode.GPO_SESSION_OPENED;
import static com.st.st25sdk.type4a.STType4GpoInterface.GpoMode.GPO_STATE_CONTROL;
import static com.st.st25sdk.type4a.STType4GpoInterface.GpoMode.GPO_WIP;

import java.util.ArrayList;
import java.util.List;

import com.st.st25sdk.NFCTag;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.command.Type4Command;
import com.st.st25sdk.type4a.STType4GpoInterface;

public class M24SRTag extends M24SRTAHighDensityTag implements STType4GpoInterface {
    protected List<GpoMode> mSupportedGpoModes;

    public M24SRTag(RFReaderInterface readerInterface, byte[] uid) throws STException {
        super(readerInterface, uid);
        mName = "M24SR02KTag";
        mTypeDescription = NFCTag.DYNAMIC_NFC_RFID_TAG;
        mMemSize = 256;
        mSysFile = new SysFileM24SR(mType4Cmd);
        mCache.add(mSysFile);

        mSupportedGpoModes = new ArrayList<>();
        mSupportedGpoModes.add(GPO_HIGH_IMPEDANCE);
        mSupportedGpoModes.add(GPO_SESSION_OPENED);
        mSupportedGpoModes.add(GPO_WIP);
        mSupportedGpoModes.add(GPO_MIP);
        mSupportedGpoModes.add(GPO_INTERRUPT);
        mSupportedGpoModes.add(GPO_STATE_CONTROL);
        mSupportedGpoModes.add(GPO_RF_BUSY);
    }

    @Override
    public GpoMode getGpoMode(byte gpoConfig) throws STException {
        byte gpo = (byte) (gpoConfig & (byte)0x70);
        GpoMode mode;
        switch (gpo) {
            case 0x00:
                mode = GPO_HIGH_IMPEDANCE;
                break;
            case 0x10:
                mode = GPO_SESSION_OPENED;
                break;
            case 0x20:
                mode = GPO_WIP;
                break;
            case 0x30:
                mode = GPO_MIP;
                break;
            case 0x40:
                mode = GPO_INTERRUPT;
                break;
            case 0x50:
                mode = GPO_STATE_CONTROL;
                break;
            case 0x60:
                mode = GPO_RF_BUSY;
                break;
            default:
                throw new STException(BAD_PARAMETER);
        }
        return mode;
    }

    @Override
    /**
     * On M24SR, we cannot modify GPO value by RF - Only read is enabled
     * */
    public void setGpoMode(GpoMode mode) throws STException {
        throw new STException(NOT_SUPPORTED);
    }

    @Override
    public List<GpoMode> getSupportedGpoModes() throws STException {
        return mSupportedGpoModes;
    }


    public byte getI2CProtected() throws STException {
        return ((SysFileM24SR) mSysFile).getI2CProtected();
    }

    public byte getI2CWatchdog() throws STException {
        return ((SysFileM24SR) mSysFile).getI2CWatchdog();
    }


    public byte getRfEnabled() throws STException {
        return ((SysFileM24SR) mSysFile).getRfEnabled();
    }

    @Override
    public byte getGpo() throws STException {
        return ((SysFileM24SR) mSysFile).getGpo();
    }

    /**
     * On M24SR, we cannot modify GPO value by RF - Only read is enabled
     * @param value gpo byte value - Tag dependant
     * @throws STException
     */
    @Override
    public void setGpo(byte value) throws STException {
        throw new STException(NOT_SUPPORTED);
    }

    @Override
    public void lockGpo(byte value) throws STException {
        throw new STException(NOT_SUPPORTED);
    }

    @Override
    public void lockGpo() throws STException {
        throw new STException(NOT_SUPPORTED);
    }

    @Override
    public boolean isGpoLocked() throws STException {
        throw new STException(NOT_SUPPORTED);
    }

    @Override
    public boolean isGpoConfigurableByRf() {
        return false;
    }

    @Override
    public boolean isGpoInInterruptedMode()  throws STException {
        byte gpo = ((SysFileM24SR) mSysFile).getGpo();
        return getGpoMode(gpo) == GPO_INTERRUPT;
    }

    @Override
    public boolean isGpoInStateControlMode()  throws STException {
        byte gpo = ((SysFileM24SR) mSysFile).getGpo();
        return getGpoMode(gpo) == GPO_STATE_CONTROL;
    }

    /**
     * Send interrupt command
     *
     * @throws STException
     */
    @Override
    public void sendInterruptCommand() throws STException {
        synchronized (Type4Command.mLock) {
            selectSysFile();
            mSTType4Cmd.sendInterrupt();
        }
    }

    /**
     * @param value  0x00 drive the Gpo low, value 1 release the Gpo
     * @throws STException
     */
    @Override
    public void setStateControlCommand(int value) throws STException {
        synchronized (Type4Command.mLock) {
            selectSysFile();
            mSTType4Cmd.setStateControl((byte) value);
        }
    }
}

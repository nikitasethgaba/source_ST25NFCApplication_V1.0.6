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

import static com.st.st25sdk.type4a.STType4GpoInterface.GpoMode.GPO_FIELD_DETECT;
import static com.st.st25sdk.type4a.STType4GpoInterface.GpoMode.GPO_INTERRUPT;
import static com.st.st25sdk.type4a.STType4GpoInterface.GpoMode.GPO_MIP;
import static com.st.st25sdk.type4a.STType4GpoInterface.GpoMode.GPO_NOT_USED;
import static com.st.st25sdk.type4a.STType4GpoInterface.GpoMode.GPO_RF_BUSY;
import static com.st.st25sdk.type4a.STType4GpoInterface.GpoMode.GPO_SESSION_OPENED;
import static com.st.st25sdk.type4a.STType4GpoInterface.GpoMode.GPO_STATE_CONTROL;
import static com.st.st25sdk.type4a.STType4GpoInterface.GpoMode.GPO_WIP;

import java.util.ArrayList;
import java.util.List;

import com.st.st25sdk.STException;
import com.st.st25sdk.command.Type4Command;
import com.st.st25sdk.command.Type4CustomCommand;
import com.st.st25sdk.type4a.STType4GpoInterface;


public class SysFileST25TAGpo extends SysFileST25TA implements STType4GpoInterface {

    protected Type4CustomCommand mType4CustomCommand;
    protected byte mGpo;

    private static final byte GPO_LOCKED_BIT = (byte) 0x80;
    private static final byte GPO_LOCKED = (byte) 0x80;

    protected List<GpoMode> mSupportedGpoModes;

    /** */

    public SysFileST25TAGpo(Type4CustomCommand type4CustomCommand) {
        super(type4CustomCommand);

        mSupportedGpoModes = new ArrayList<>();
        mSupportedGpoModes.add(GpoMode.GPO_NOT_USED);
        mSupportedGpoModes.add(GPO_SESSION_OPENED);
        mSupportedGpoModes.add(GPO_WIP);
        mSupportedGpoModes.add(GPO_MIP);
        mSupportedGpoModes.add(GPO_INTERRUPT);
        mSupportedGpoModes.add(GPO_STATE_CONTROL);
        mSupportedGpoModes.add(GPO_RF_BUSY);
        mSupportedGpoModes.add(GPO_FIELD_DETECT);
    }

    @Override
    public List<GpoMode> getSupportedGpoModes() throws STException {
        return mSupportedGpoModes;
    }

    @Override
    public GpoMode getGpoMode(byte gpoConfig) throws STException {
        byte gpo = (byte) (gpoConfig & (byte)0x70);
        GpoMode mode;
        switch (gpo) {
            case 0x00:
                mode = GPO_NOT_USED;
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
            case 0x70:
                mode = GPO_FIELD_DETECT;
                break;
            default:
                throw new STException(STException.STExceptionCode.BAD_PARAMETER);
        }
        return mode;
    }

    @Override
    public void setGpoMode(GpoMode mode) throws STException {
        byte gpo = getGpo();
        switch (mode) {
            case GPO_NOT_USED:
                gpo = (byte) (gpo & (byte)0x80);
                break;
            case GPO_SESSION_OPENED:
                gpo = (byte) ((byte) (gpo | (byte)0x10) & (byte)0x90);
                break;
            case GPO_WIP:
                gpo = (byte) ((byte) (gpo | (byte)0x20) & (byte)0xA0);
                break;
            case GPO_MIP:
                gpo = (byte) ((byte) (gpo | (byte)0x30) & (byte)0xB0);
                break;
            case GPO_INTERRUPT:
                gpo = (byte) ((byte) (gpo | (byte)0x40) & (byte)0xC0);
                break;
            case GPO_STATE_CONTROL:
                gpo = (byte) ((byte) (gpo | (byte)0x50) & (byte)0xD0);
                break;
            case GPO_RF_BUSY:
                gpo = (byte) ((byte) (gpo | (byte)0x60) & (byte)0xE0);
                break;
            case GPO_FIELD_DETECT:
                gpo = (byte) ((byte) (gpo | (byte)0x70) & (byte)0xF0);
                break;
            default:
                throw new STException(STException.STExceptionCode.BAD_PARAMETER);
        }
        setGpo(gpo);
    }

    @Override
    public byte getGpo() throws STException {
        checkCache();
        return mGpo;
    }

    @Override
    public void setGpo(byte value) throws STException {
        synchronized (Type4Command.mLock) {
            select();
            ((Type4CustomCommand)mType4Command).setGpo(value);
            mGpo = value;
        }
    }

    @Override
    public void lockGpo(byte value) throws STException {
        synchronized (Type4Command.mLock) {
            value |= (byte)0x80;
            select();
            ((Type4CustomCommand)mType4Command).setGpo(value);
            mGpo = value;
        }
    }

    @Override
    public void lockGpo() throws STException {
        synchronized (Type4Command.mLock) {
            byte value = (byte) (getGpo() | (byte)0x80);
            select();
            ((Type4CustomCommand)mType4Command).setGpo(value);
            mGpo = value;
        }
    }

    @Override
    public boolean isGpoLocked() throws STException {
        byte lc = getGpo();
        // check if b7 is 1
        return ((lc & GPO_LOCKED_BIT) == GPO_LOCKED);
    }

    @Override
    public boolean isGpoConfigurableByRf() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isGpoInInterruptedMode() throws STException {
        byte gpo = getGpo();
        return getGpoMode(gpo) == GPO_INTERRUPT;
    }

    @Override
    public boolean isGpoInStateControlMode() throws STException {
        byte gpo = getGpo();
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
            select();
            ((Type4CustomCommand) mType4Command).sendInterrupt();
        }
    }

    /**
     * @param value pwd 0x00 drive the GPO low, value 1 release the GPO
     * @throws STException
     */
    @Override
    public void setStateControlCommand(int value) throws STException {
        synchronized (Type4Command.mLock) {
            select();
            ((Type4CustomCommand) mType4Command).setStateControl((byte) value);
        }
    }

    @Override
    protected void parseSysFile(byte[] buffer) {
        super.parseSysFile(buffer);
        if (buffer.length > 2) {
            mGpo = (byte) (buffer[2] & (byte)0xFF);
        }
    }

}

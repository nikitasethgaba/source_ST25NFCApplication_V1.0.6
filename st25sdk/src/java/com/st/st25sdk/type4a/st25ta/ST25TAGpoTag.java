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

import java.util.List;

import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.type4a.STType4GpoInterface;

public class ST25TAGpoTag extends ST25TATag implements STType4GpoInterface {

    public ST25TAGpoTag(RFReaderInterface readerInterface, byte[] uid) throws STException {
        super(readerInterface, uid);
        mSysFile = new SysFileST25TAGpo(mSTType4Cmd);
    }

    @Override
    public boolean isGpoConfigurableByRf() {
        return ((SysFileST25TAGpo) mSysFile).isGpoConfigurableByRf();
    }

    @Override
    public GpoMode getGpoMode(byte gpoConfig) throws STException {
        return ((SysFileST25TAGpo) mSysFile).getGpoMode(gpoConfig);
    }

    @Override
    public void setGpoMode(GpoMode mode) throws STException {
        ((SysFileST25TAGpo) mSysFile).setGpoMode(mode);
    }

    @Override
    public List<GpoMode> getSupportedGpoModes() throws STException {
        return ((SysFileST25TAGpo) mSysFile).getSupportedGpoModes();
    }

    @Override
    public byte getGpo() throws STException {
        return ((SysFileST25TAGpo) mSysFile).getGpo();
    }

    @Override
    public void setGpo(byte value) throws STException {
        ((SysFileST25TAGpo) mSysFile).setGpo(value);
    }

    @Override
    public void lockGpo(byte value) throws STException {
        ((SysFileST25TAGpo) mSysFile).lockGpo(value);
    }

    @Override
    public void lockGpo() throws STException {
        ((SysFileST25TAGpo) mSysFile).lockGpo();
    }

    @Override
    public boolean isGpoLocked() throws STException {
        return ((SysFileST25TAGpo) mSysFile).isGpoLocked();
    }

    @Override
    public boolean isGpoInInterruptedMode() throws STException {
        return ((SysFileST25TAGpo) mSysFile).isGpoInInterruptedMode();
    }

    @Override
    public boolean isGpoInStateControlMode() throws STException {
        return ((SysFileST25TAGpo) mSysFile).isGpoInStateControlMode();
    }

    /**
     * Send interrupt command
     *
     * @throws STException
     */
    @Override
    public void sendInterruptCommand() throws STException {
        ((SysFileST25TAGpo) mSysFile).sendInterruptCommand();
    }

    /**
     * @param value pwd 0x00 drive the GPO low, value 1 release the GPO
     * @throws STException
     */

    @Override
    public void setStateControlCommand(int value) throws STException {
        ((SysFileST25TAGpo) mSysFile).setStateControlCommand((byte) value);
    }

}

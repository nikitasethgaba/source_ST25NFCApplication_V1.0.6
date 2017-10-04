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

import static com.st.st25sdk.type4a.Type4Tag.AccessStatus.LOCKED_BY_PASSWORD;
import static com.st.st25sdk.type4a.Type4Tag.AccessStatus.NOT_AUTHORIZED;
import static com.st.st25sdk.type4a.Type4Tag.AccessStatus.NOT_LOCKED;
import static com.st.st25sdk.type4a.Type4Tag.AccessStatus.STATUS_UNKNOWN;

import com.st.st25sdk.NFCTag;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.command.Iso7816Type4RApduStatus;
import com.st.st25sdk.type4a.STType4CounterInterface;
import com.st.st25sdk.type4a.STType4Tag;

public class ST25TATag extends STType4Tag implements STType4CounterInterface {

    public ST25TATag(RFReaderInterface readerInterface, byte[] uid) throws STException {
        super(readerInterface, uid);
        mSysFile = new SysFileST25TA(mSTType4Cmd);
        mTypeDescription = NFCTag.NFC_RFID_TAG;
        Iso7816Type4RApduStatus.mIgnoreSw2= false;
    }

    @Override
    public byte getEventCounter() throws STException {
        return ((SysFileST25TA) mSysFile).getEventCounter();
    }

    @Override
    public byte[] getCounterBytes() throws STException {
        return ((SysFileST25TA) mSysFile).getCounterBytes();
    }

    @Override
    public int getCounterValue() throws STException {
        return ((SysFileST25TA) mSysFile).getCounterValue();
    }

    public byte getProductVersion() throws STException {
        return ((SysFileST25TA) mSysFile).getProductVersion();
    }

    // Counter implementation
    @Override
    public void lockCounter() throws STException {
        ((SysFileST25TA) mSysFile).lockCounter();
    }

    @Override
    public boolean isCounterLocked() throws STException {
        return ((SysFileST25TA) mSysFile).isCounterLocked();
    }

    @Override
    public void enableCounter() throws STException {
        ((SysFileST25TA) mSysFile).enableCounter();
    }

    @Override
    public boolean isCounterEnabled() throws STException {
        return ((SysFileST25TA) mSysFile).isCounterEnabled();
    }

    @Override
    public void disableCounter() throws STException {
        ((SysFileST25TA) mSysFile).disableCounter();
    }

    @Override
    public void incrementCounterOnRead() throws STException {
        ((SysFileST25TA) mSysFile).incrementCounterOnRead();
    }

    @Override
    public boolean isCounterIncrementedOnRead() throws STException {
        return ((SysFileST25TA) mSysFile).isCounterIncrementedOnRead();
    }

    @Override
    public void incrementCounterOnWrite() throws STException {
        ((SysFileST25TA) mSysFile).incrementCounterOnWrite();
    }

    @Override
    public boolean isCounterIncrementedOnWrite() throws STException {
        return ((SysFileST25TA) mSysFile).isCounterIncrementedOnWrite();
    }


    // overrided for ST25TA cut 2.1
    // New way of managing read access status
    @Override
    public AccessStatus getFileReadAccessStatus(int fileId) throws STException {
        AccessStatus readAccessStatus;
        byte productVersion = getProductVersion();

        if (productVersion >= 33) {
            // need to add verify cmd to check state
            byte readAccess = getFileReadAccess(fileId);
            if (readAccess == (byte) 0x00) {
                try {
                    selectFile(fileId);
                    byte[] response = mIso7816Cmd.verify((byte) 0x00, (byte) 0x00, (byte) 0x01, null);
                    // if we arrived here tag is normally not locked otherwise an exception has been raised
                    // The checkReadAccessStatusResponse will manage the response for all cases
                    // in case of verify cmd do not raise exception during its behaviour
                    readAccessStatus = checkReadAccessStatusResponse(response);
                } catch(STException e) {
                    switch (e.getError()) {
                        case PASSWORD_NEEDED:
                            readAccessStatus = LOCKED_BY_PASSWORD;
                            break;
                        case WRONG_SECURITY_STATUS:
                            readAccessStatus = NOT_AUTHORIZED;
                            break;
                        default:
                            throw  e ;
                    }
                }
            } else {
                readAccessStatus = STATUS_UNKNOWN;
            }
        } else {
            readAccessStatus = super.getFileReadAccessStatus(fileId);
        }
        return readAccessStatus;
    }

    private AccessStatus checkReadAccessStatusResponse(byte[] response) {
        AccessStatus status = STATUS_UNKNOWN;
        if ((response != null) && (response[0] != 0x00)) {
            if (response.length >= 2) {
                if (response[0] == (byte) 0x90 && response[1] == (byte) 0x00) {
                    status = NOT_LOCKED;
                }
                if (response[0] == (byte) 0x63 && response[1] == (byte) 0x00) {
                    status = LOCKED_BY_PASSWORD;
                }
                if (response[0] == (byte) 0x69 && (response[1] == (byte) 0x84 || response[1] == (byte) 0x82)) {
                    status = NOT_AUTHORIZED;
                }
            } else {
                status = STATUS_UNKNOWN;
            }
        }
        return status;
    }

    @Override
    public AccessStatus getFileWriteAccessStatus(int fileId) throws STException {
        AccessStatus writeAccessStatus;
        byte productVersion = getProductVersion();

        if (productVersion >= 33) {
            // need to add verify cmd to check state
            byte writeAccess = getFileWriteAccess(fileId);
            if (writeAccess == (byte) 0xFF) {
                try {
                    selectFile(fileId);
                    byte[] response = mIso7816Cmd.verify((byte) 0x00, (byte) 0x00, (byte) 0x02, null);
                    // if we arrived here tag is normally not locked otherwise an exception has been raised
                    // The checkWriteAccessStatusResponse will manage the response for all cases
                    // in case of verify cmd do not raise exception during its behaviour
                    writeAccessStatus = checkWriteAccessStatusResponse(response, writeAccess);
                } catch (STException e) {
                    switch (e.getError()) {
                        case PASSWORD_NEEDED:
                            writeAccessStatus = LOCKED_BY_PASSWORD;
                            break;
                        case WRONG_SECURITY_STATUS:
                        case INVALID_DATA_PARAM:
                            writeAccessStatus = NOT_AUTHORIZED;
                            break;
                        default:
                            throw  e ;
                    }
                }
            } else {
                if (writeAccess == (byte) 0x00) {
                    writeAccessStatus = NOT_LOCKED;
                } else
                    writeAccessStatus = STATUS_UNKNOWN;
            }
        } else {
            writeAccessStatus = super.getFileWriteAccessStatus(fileId);
        }
        return writeAccessStatus;
    }

    private AccessStatus checkWriteAccessStatusResponse(byte[] response, byte access) {
        AccessStatus status = STATUS_UNKNOWN;
        if ((response != null) && (response[0] != 0x00)) {
            if (response.length >= 2) {
                if (response[0] == (byte) 0x90 && response[1] == (byte) 0x00 && access == (byte) 0x00) {
                    status = NOT_LOCKED;
                }
                if (response[0] == (byte) 0x63 && response[1] == (byte) 0x00 && access == (byte) 0xFF) {
                    status = LOCKED_BY_PASSWORD;
                }
                if (response[0] == (byte) 0x69 && response[1] == (byte) 0x84 && access == (byte) 0xFF) {
                    status = NOT_AUTHORIZED;
                }
            } else {
                status = STATUS_UNKNOWN;
            }
        }
        return status;
    }


}

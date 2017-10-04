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

import com.st.st25sdk.NFCTag;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.command.Iso15693Protocol;

import static com.st.st25sdk.STException.STExceptionCode.NOT_IMPLEMENTED;

public class M24LR16KTag extends STVicinityTag implements STType5PasswordInterface, STVicinityConfigInterface {

    public M24LR16KTag(RFReaderInterface readerInterface, byte[] uid) throws STException {
        super(readerInterface, uid, 16, 32);

        mName = "M24LR16E";
        mTypeDescription = NFCTag.DYNAMIC_NFC_RFID_TAG;
        setMaxReadMultipleBlocksReturned(32);

        mIso15693CustomCommand.setFlag((byte) (Iso15693Protocol.HIGH_DATA_RATE_MODE | Iso15693Protocol.ADDRESSED_MODE));
    }

    @Override
    public void presentPassword(byte passwordNumber, byte[] password) throws STException {
        mIso15693CustomCommand.presentPwd(passwordNumber, password);
    }

    @Override
    public void writePassword(byte passwordNumber, byte[] newPassword) throws STException {
        mIso15693CustomCommand.writePwd(passwordNumber, newPassword);
    }

    @Override
    public PasswordLength getPasswordLength(byte passwordNumber) throws STException {
        return PasswordLength.PWD_ON_32_BITS;
    }

    @Override
    public byte getConfigurationPasswordNumber() throws STException {
        throw new STException(NOT_IMPLEMENTED);
    }

    public void lockSector(int sector, byte value) throws STException {
        setSecurityStatus(sector, value);
    }

    // Energy Harvesting Functions

    /**
     * readCfg command as defined in M24LR specification.
     * @param flag Request flag for the command
     * @return 1 byte response for status + 1 byte for the Configuration byte
     * @throws STException
     */
    public byte[] readCfg(byte flag) throws STException {
        return mIso15693CustomCommand.readCfg(flag, getUid());
    }

    /**
     * readCfg command as defined in M24LR specification.
     * @return 1 byte response for status + 1 byte for the Configuration byte
     * @throws STException
     */
    @Override
    public byte[] readCfg() throws STException {
        return readCfg(mIso15693CustomCommand.getFlag());
    }

    /**
     * Write EHCfg command as defined in M24LR specification.
     * @param data Data to write.
     * @param flag Request flag for the command
     * @return 1 byte response for status
     * @throws STException
     */
    public byte writeEHCfg(byte data, byte flag) throws STException {
        return mIso15693CustomCommand.writeEHCfg(data, flag, getUid());
    }

    /**
     * Write EHCfg command as defined in M24LR specification.
     * @param data Data to write.
     * @return 1 byte response for status
     * @throws STException
     */
    @Override
    public byte writeEHCfg(byte data) throws STException {
        return writeEHCfg(data, mIso15693CustomCommand.getFlag());
    }

    /**
     * Write DOCfg command as defined in M24LR specification.
     * @param data Data to write.
     * @param flag Request flag for the command
     * @return 1 byte response for status
     * @throws STException
     */
    public byte writeDOCfg(byte data, byte flag) throws STException {
        return mIso15693CustomCommand.writeDOCfg(data, flag, getUid());
    }

    /**
     * Write DOCfg command as defined in M24LR specification.
     * @param data Data to write.
     * @return 1 byte response for status
     * @throws STException
     */
    @Override
    public byte writeDOCfg(byte data) throws STException {
        return writeDOCfg(data, mIso15693CustomCommand.getFlag());
    }

    /**
     * Set RstEHEn command as defined in M24LR specification.
     * @param data Data to write.  0=Reset 1=Set
     * @param flag Request flag for the command
     * @return 1 byte response for status
     * @throws STException
     */
    public byte setRstEHEn(byte data, byte flag) throws STException {
        return mIso15693CustomCommand.setRstEHEn(data, flag, getUid());
    }

    /**
     * Set RstEHEn command as defined in M24LR specification.
     * @param data Data to write.  0=Reset 1=Set
     * @return 1 byte response for status
     * @throws STException
     */
    @Override
    public byte setRstEHEn(byte data) throws STException {
        return setRstEHEn(data, mIso15693CustomCommand.getFlag());
    }

    /**
     * Check EHEn command as defined in M24LR specification.
     * @param flag Request flag for the command
     * @return checkEnable data response
     * @return array of bytes = 1 byte response flag + 1 byte data
     * @throws STException
     */
    public byte[] checkEHEn(byte flag) throws STException {
        return mIso15693CustomCommand.checkEHEn(flag, getUid());
    }

    /**
     * Check EHEn command as defined in M24LR specification.
     * @return checkEnable data response
     * @return array of bytes = 1 byte response flag + 1 byte data
     * @throws STException
     */
    public byte[] checkEHEn() throws STException {
        return checkEHEn(mIso15693CustomCommand.getFlag());
    }

    ////////////////////////////////INITIATE COMMANDS  ////////////////////////////

    public byte[] initiate(byte flag) throws STException {
        return mIso15693CustomCommand.initiate(flag);
    }

    public byte[] inventoryInitiated(byte flag) throws STException {
        return mIso15693CustomCommand.inventoryInitiated(flag);
    }
    public byte[] inventoryInitiated(byte flag, byte maskLength, byte[] maskValue) throws STException {
        return mIso15693CustomCommand.inventoryInitiated(flag, maskLength, maskValue);
    }
    public byte[] inventoryInitiated(byte flag, byte maskLength, byte[] maskValue, byte afiField) throws STException {
        return mIso15693CustomCommand.inventoryInitiated(flag, maskLength, maskValue, afiField);
    }

    public byte[] fastInitiate(byte flag) throws STException{
        return mIso15693CustomCommand.fastInitiate(flag);
    }

    public byte[] fastInventoryInitiated(byte flag) throws STException {
        return mIso15693CustomCommand.fastInventoryInitiated(flag);
    }
    public byte[] fastInventoryInitiated(byte flag, byte maskLength, byte[] maskValue) throws STException {
        return mIso15693CustomCommand.fastInventoryInitiated(flag, maskLength, maskValue);
    }
    public byte[] fastInventoryInitiated(byte flag, byte maskLength, byte[] maskValue, byte afiField) throws STException {
        return mIso15693CustomCommand.fastInventoryInitiated(flag, maskLength, maskValue, afiField);
    }

}

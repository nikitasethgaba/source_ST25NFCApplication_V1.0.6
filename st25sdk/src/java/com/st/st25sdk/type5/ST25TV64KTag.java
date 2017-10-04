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
import com.st.st25sdk.MultiAreaInterface;
import com.st.st25sdk.NFCTag;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.RegisterInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.STRegister;
import com.st.st25sdk.TagHelper;
import com.st.st25sdk.command.Iso15693CustomCommand;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterEndAi;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterLockCfg;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterRfAiSS;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.st.st25sdk.STException.STExceptionCode.BAD_PARAMETER;
import static com.st.st25sdk.STException.STExceptionCode.CMD_FAILED;
import static com.st.st25sdk.STException.STExceptionCode.NOT_IMPLEMENTED;
import static com.st.st25sdk.type5.st25dv.ST25DVRegisterRfAiSS.ST25DVSecurityStatusPWDControl.NO_PWD_SELECTED;
import static com.st.st25sdk.type5.st25dv.ST25DVRegisterRfAiSS.ST25DVSecurityStatusPWDControl.PROTECTED_BY_PWD1;
import static com.st.st25sdk.type5.st25dv.ST25DVRegisterRfAiSS.ST25DVSecurityStatusPWDControl.PROTECTED_BY_PWD2;
import static com.st.st25sdk.type5.st25dv.ST25DVRegisterRfAiSS.ST25DVSecurityStatusPWDControl.PROTECTED_BY_PWD3;
import static com.st.st25sdk.type5.STType5PasswordInterface.PasswordLength.PWD_ON_64_BITS;


public class ST25TV64KTag extends STType5MultiAreaTag implements STType5PasswordInterface, MultiAreaInterface, RegisterInterface {

    public static final byte MAX_MEMORY_AREA_SUPPORTED = (byte) 0x04;
    public static final byte MAX_WRITE_MULTIPLE_BLOCKS = 4;

    // Passwords
    public static final byte ST25TV64K_CONFIGURATION_PASSWORD_ID = 0x0;
    public static final byte ST25TV64K_PASSWORD_1 = 0x1;
    public static final byte ST25TV64K_PASSWORD_2 = 0x2;
    public static final byte ST25TV64K_PASSWORD_3 = 0x3;

    // Static registers
    private List<STRegister> mST25TV64KRegisterList;
    // On ST25TV64K, there is no Dynamic registers

    // Registers definition
    private ST25DVRegisterEndAi mRegisterEndArea1;
    private ST25DVRegisterEndAi mRegisterEndArea2;
    private ST25DVRegisterEndAi mRegisterEndArea3;
    private ST25DVRegisterRfAiSS mRegisterRFA1SS;
    private ST25DVRegisterRfAiSS mRegisterRFA2SS;
    private ST25DVRegisterRfAiSS mRegisterRFA3SS;
    private ST25DVRegisterRfAiSS mRegisterRFA4SS;
    private ST25DVRegisterLockCfg mRegisterLockCfg;


    public ST25TV64KTag(RFReaderInterface readerInterface, byte[] uid) throws STException {
        super(readerInterface, uid);

        // For ST25TV64KTag, we redefine "mSysFile" of parent class Type5Tag in order to use an extended version
        mSysFile = new SysFileType5Extended(mIso15693CustomCommand);

        // Define ST25TV64KTag registers
        mST25TV64KRegisterList = new ArrayList<>();
        initRegisters(mIso15693CustomCommand);

        mCache.add(mSysFile);

        for (STRegister it: mST25TV64KRegisterList)
            mCache.add(it);

        initAreaList();

        mName = "ST25TV64K";
        mTypeDescription = NFCTag.DYNAMIC_NFC_RFID_TAG;
        setMaxReadMultipleBlocksReturned(256);
    }

    private void initRegisters(Iso15693CustomCommand cmd) {
        // Area definition
        mRegisterEndArea1 = ST25DVRegisterEndAi.newInstance(cmd,1);
        // Add listener to receive notifications when register mRegisterEndArea1 changes value
        mRegisterEndArea1.addRegisterListener(new STRegister.RegisterListener() {
            @Override
            public void registerChange() throws STException {
                // Register "mRegisterEndArea1" has changed so the MultiArea data are no more valid
                mRegisterEndArea1.invalidateCache();
                initAreaList();
            }
        });

        mRegisterEndArea2 = ST25DVRegisterEndAi.newInstance(cmd,2);
        // Add listener to receive notifications when register mRegisterEndArea2 changes value
        mRegisterEndArea2.addRegisterListener(new STRegister.RegisterListener() {
            @Override
            public void registerChange() throws STException {
                // Register "mRegisterEndArea2" has changed so the MultiArea data are no more valid
                mRegisterEndArea2.invalidateCache();
                initAreaList();
            }
        });

        mRegisterEndArea3 = ST25DVRegisterEndAi.newInstance(cmd,3);
        // Add listener to receive notifications when register mRegisterEndArea3 changes value
        mRegisterEndArea3.addRegisterListener(new STRegister.RegisterListener() {
            @Override
            public void registerChange() throws STException {
                // Register "mRegisterEndArea3" has changed so the MultiArea data are no more valid
                mRegisterEndArea3.invalidateCache();
                initAreaList();
            }
        });

        // Area Security Status definition
        mRegisterRFA1SS = ST25DVRegisterRfAiSS.newInstance(cmd, 1);
        mRegisterRFA2SS = ST25DVRegisterRfAiSS.newInstance(cmd, 2);
        mRegisterRFA3SS = ST25DVRegisterRfAiSS.newInstance(cmd, 3);
        mRegisterRFA4SS = ST25DVRegisterRfAiSS.newInstance(cmd, 4);

        // Disable Configuration change by RFs
        mRegisterLockCfg = ST25DVRegisterLockCfg.newInstance(cmd);

        // Add registers at the right index of the list (given by the RegisterId)
        mST25TV64KRegisterList.add(mRegisterRFA1SS);
        mST25TV64KRegisterList.add(mRegisterEndArea1);
        mST25TV64KRegisterList.add(mRegisterRFA2SS);
        mST25TV64KRegisterList.add(mRegisterEndArea2);
        mST25TV64KRegisterList.add(mRegisterRFA3SS);
        mST25TV64KRegisterList.add(mRegisterEndArea3);
        mST25TV64KRegisterList.add(mRegisterRFA4SS);

        mST25TV64KRegisterList.add(mRegisterLockCfg);
    }

    @Override
    public PasswordLength getPasswordLength(byte passwordNumber) throws STException {
        if ((passwordNumber & 0xFF) > ST25TV64K_PASSWORD_3) {
            throw new STException(BAD_PARAMETER);
        }
        return PWD_ON_64_BITS;
    }

    @Override
    public void writePassword(byte passwordNumber, byte[] newPassword) throws STException {
        mIso15693CustomCommand.writePwd(passwordNumber, newPassword);
    }

    public void writePassword(byte passwordNumber, byte[] newPassword, byte flag) throws STException {
        mIso15693CustomCommand.writePwd(passwordNumber, newPassword, flag, mUid);
    }

    @Override
    public void presentPassword(byte passwordNumber, byte[] password) throws STException {
        mIso15693CustomCommand.presentPwd(passwordNumber, password);
    }

    public void presentPassword(byte passwordNumber, byte[] password, byte flag) throws STException {
        mIso15693CustomCommand.presentPwd(passwordNumber, password, flag, mUid);
    }

    @Override
    public byte getConfigurationPasswordNumber() throws STException {
        return ST25TV64K_CONFIGURATION_PASSWORD_ID;
    }

    /**
     * Writes the complete buffer in EEPROM at the specified block address.
     * The buffer size must be a multiple of a block size (4 bytes on ST25TV64K).
     * @param blockAddress Block address in target's EEPROM where to write the data contained in buffer
     * @param buffer Data to write in increments of 4 bytes within the limit of 4 blocks (16 bytes max on ST25TV64K)
     * @return
     * @throws STException
     */
    public byte writeMultipleBlock(byte blockAddress, byte[] buffer) throws STException {
        if (buffer == null) {
            throw new STException(BAD_PARAMETER);
        }

        return writeMultipleBlock(blockAddress, (byte) ((buffer.length / getBlockSizeInBytes()) - 1), buffer);
    }

    /**
     * Writes the specified number of blocks + 1 of the buffer at the specified EEPROM block address
     * The buffer size must be a multiple of a block size (4 bytes on ST25TV64K) with a max size of 4 blocks.
     * @param blockAddress Block address in target's EEPROM where to write the data contained in buffer
     * @param numberOfBlocks Must be less than MAX_WRITE_MULTIPLE_BLOCKS. The tag will write (numberOfBlocks + 1)
     *                       blocks in its EEPROM.
     * @param buffer Data to write in the limit of 4 blocks (16 bytes on ST25TV64K)
     * @return
     * @throws STException
     */
    public byte writeMultipleBlock(byte blockAddress, byte numberOfBlocks, byte[] buffer) throws STException {
        return writeMultipleBlock(blockAddress, numberOfBlocks, buffer, mIso15693Cmd.getFlag());
    }

    /**
     * Writes the specified number of blocks + 1 of the buffer at the specified EEPROM block address.
     * The buffer size must be a multiple of a block size (4 bytes on ST25TV64K) with a max size of 4 blocks.
     * @param blockAddress Block address in target's EEPROM where to write the data contained in buffer
     * @param numberOfBlocks Must be less than MAX_WRITE_MULTIPLE_BLOCKS. The tag will write (numberOfBlocks + 1)
     *                       blocks in its EEPROM. Set to 0 to write a single block.
     * @param buffer Data to write in multiples of blocks (max 4 blocks = 16 bytes on ST25TV64K)
     * @param flag
     * @return
     * @throws STException
     */
    public byte writeMultipleBlock(byte blockAddress, byte numberOfBlocks, byte[] buffer, byte flag) throws STException {
        if (buffer == null || buffer.length == 0) {
            throw new STException(BAD_PARAMETER);
        }

        if (numberOfBlocks >= MAX_WRITE_MULTIPLE_BLOCKS) {
            throw new STException(BAD_PARAMETER);
        }

        if (buffer.length != (numberOfBlocks + 1) * getBlockSizeInBytes()) {
            throw new STException(BAD_PARAMETER);
        }

        return mIso15693Cmd.writeMultipleBlock(blockAddress, numberOfBlocks, buffer, flag, mUid);
    }

    /**
     * Writes the complete buffer in EEPROM at the specified block address (Up to 0xFFFF).
     * The buffer size must be a multiple of a block size (4 bytes on ST25TV64K).
     * @param blockAddress Block address in target's EEPROM where to write the data contained in buffer.
     *                     LSByte first
     * @param buffer Data to write in increments of 4 bytes within the limit of 4 blocks (16 bytes max on ST25TV64K)
     * @return
     * @throws STException
     */
    public byte extendedWriteMultipleBlock(int blockAddress, byte[] buffer) throws STException {
        if (buffer == null) {
            throw new STException(BAD_PARAMETER);
        }
        return extendedWriteMultipleBlock(blockAddress, (buffer.length / getBlockSizeInBytes()) - 1, buffer);
    }

    /**
     * Writes the specified number of blocks + 1 of the buffer at the specified EEPROM block address.
     * The buffer size must be a multiple of a block size (4 bytes on ST25TV64K) with a max size of 4 blocks.
     * @param blockAddress Block address in target's EEPROM where to write the data contained in buffer. LSByte first.
     * @param numberOfBlocks Must be less than MAX_WRITE_MULTIPLE_BLOCKS. The tag will write (numberOfBlocks + 1)
     *                       blocks in its EEPROM.
     *                       Set to 0 to write a single block
     *                       LSByte first
     * @param buffer Data to write in increments of 4 bytes within the limit of 4 blocks (16 bytes max on ST25TV64K)
     * @return
     * @throws STException
     */
    public byte extendedWriteMultipleBlock(int blockAddress, int numberOfBlocks, byte[] buffer) throws STException {
        return extendedWriteMultipleBlock(blockAddress, numberOfBlocks, buffer, mIso15693Cmd.getFlag());
    }

    /**
     * Writes the specified number of blocks + 1 of the buffer at the specified EEPROM block address.
     * The buffer size must be a multiple of a block size (4 bytes on ST25TV64K) with a max size of 4 blocks.
     * @param blockAddress Block address in target's EEPROM where to write the data contained in buffer. LSByte first.
     * @param numberOfBlocks Must be less than MAX_WRITE_MULTIPLE_BLOCKS. The tag will write (numberOfBlocks + 1)
     *                       blocks in its EEPROM.
     *                       Set to 0 to write a single block
     *                       LSByte first
     * @param buffer Data to write in increments of 4 bytes within the limit of 4 blocks (16 bytes max on ST25TV64K)
     * @param flag
     * @return
     * @throws STException
     */
    public byte extendedWriteMultipleBlock(int blockAddress, int numberOfBlocks, byte[] buffer, byte flag) throws STException {
        if (blockAddress < 0 || numberOfBlocks < 0) {
            throw new STException(BAD_PARAMETER);
        }

        if (buffer == null || buffer.length == 0) {
            throw new STException(BAD_PARAMETER);
        }

        if (numberOfBlocks >= MAX_WRITE_MULTIPLE_BLOCKS) {
            throw new STException(BAD_PARAMETER);
        }

        if (buffer.length != (numberOfBlocks + 1) * getBlockSizeInBytes()) {
            throw new STException(BAD_PARAMETER);
        }

        return mIso15693Cmd.extendedWriteMultipleBlock(Helper.convertIntTo2BytesHexaFormat(blockAddress),
                Helper.convertIntTo2BytesHexaFormat(numberOfBlocks), buffer, flag, mUid);
    }

    public byte[] extendedGetMultipleBlockSecurityStatus(int blockAddress, int numberOfBlocks) throws STException {
        return extendedGetMultipleBlockSecurityStatus(blockAddress, numberOfBlocks, mIso15693Cmd.getFlag());
    }

    public byte[] extendedGetMultipleBlockSecurityStatus(int blockAddress, int numberOfBlocks, byte flag) throws STException {
        if (blockAddress < 0 || numberOfBlocks < 0) {
            throw new STException(BAD_PARAMETER);
        }
        return mIso15693Cmd.extendedGetMultipleBlockSecStatus(Helper.convertIntTo2BytesHexaFormat(blockAddress),
                Helper.convertIntTo2BytesHexaFormat(numberOfBlocks), flag, getUid());
    }

    public byte[] fastExtendedReadSingleBlock(int blockAddress) throws STException {
        return fastExtendedReadSingleBlock(blockAddress, mIso15693CustomCommand.getFlag());
    }

    public byte[] fastExtendedReadSingleBlock(int blockAddress, byte flag) throws STException {
        return mIso15693CustomCommand.fastExtendedReadSingleBlock(Helper.convertIntTo2BytesHexaFormat(blockAddress),
                flag, getUid());
    }

    public byte[] fastExtendedReadMultipleBlock(int blockAddress, int numberOfBlocks) throws STException {
        return fastExtendedReadMultipleBlock(blockAddress, numberOfBlocks, mIso15693CustomCommand.getFlag());
    }

    public byte[] fastExtendedReadMultipleBlock(int blockAddress, int numberOfBlocks, byte flag) throws STException {
        if (blockAddress < 0 || numberOfBlocks < 0) {
            throw new STException(BAD_PARAMETER);
        }
        return mIso15693CustomCommand.fastExtendedReadMultipleBlock(Helper.convertIntTo2BytesHexaFormat(blockAddress),
                Helper.convertIntTo2BytesHexaFormat(numberOfBlocks), flag, getUid());
    }

    public byte[] readConfig(byte configId) throws STException {
        return mIso15693CustomCommand.readConfig(configId);
    }

    public byte writeConfig(byte configId, byte value) throws STException {
        return mIso15693CustomCommand.writeConfig(configId, value);
    }

    public byte[] readDynConfig(byte configId) throws STException {
        return mIso15693CustomCommand.readDynConfig(configId);
    }

    public byte writeDynConfig(byte configId, byte value) throws STException {
        return mIso15693CustomCommand.writeDynConfig(configId, value);
    }

    public byte[] fastReadDynConfig(byte configId) throws STException {
        return mIso15693CustomCommand.fastReadDynConfig(configId);
    }

    public byte fastWriteDynConfig(byte configId, byte value) throws STException {
        return mIso15693CustomCommand.fastWriteDynConfig(configId, value);
    }

    // Methods from MultiAreaInterface
    // -----------------------------------------------------------------------------------
    @Override
    public int getMaxNumberOfAreas() {
        return MAX_MEMORY_AREA_SUPPORTED;
    }

    @Override
    public int getNumberOfAreas() throws STException {
        int numberOfAreas = 1;
        int endArea1, endArea2, endArea3;
        int maxEndOfAreaValue = getMaxEndOfAreaValue() & 0xFF;

        // Warning: bytes are signed so use "& 0xFF" to get them unsigned
        endArea1 = mRegisterEndArea1.getEndArea() & 0xFF;
        endArea2 = mRegisterEndArea2.getEndArea() & 0xFF;
        endArea3 = mRegisterEndArea3.getEndArea() & 0xFF;

        if (endArea1 != maxEndOfAreaValue) {
            // We have at least one area
            numberOfAreas++;
        }

        if (endArea2 != maxEndOfAreaValue) {
            numberOfAreas++;
        }

        if (endArea3 != maxEndOfAreaValue) {
            numberOfAreas++;
        }

        return numberOfAreas;
    }

    @Override
    public void setNumberOfAreas(int nbOfAreas) throws STException {
        throw new STException(NOT_IMPLEMENTED);
    }

    @Override
    public int getAreaSizeInBytes(int area) throws STException {
        int areaSizeInBlocks = 0;
        switch (area) {
            case AREA1:
                areaSizeInBlocks = (mRegisterEndArea1.getEndAreaInBlock()) + 1;
                break;
            case AREA2:
                areaSizeInBlocks = mRegisterEndArea2.getEndAreaInBlock() - mRegisterEndArea1.getEndAreaInBlock();
                break;
            case AREA3:
                areaSizeInBlocks = mRegisterEndArea3.getEndAreaInBlock() - mRegisterEndArea2.getEndAreaInBlock();
                break;
            case AREA4:
                areaSizeInBlocks = (getNumberOfBlocks() -1) - mRegisterEndArea3.getEndAreaInBlock();
                break;
            default:
                throw new STException(BAD_PARAMETER);
        }
        return areaSizeInBlocks * getBlockSizeInBytes();
    }

    @Override
    public int getAreaOffsetInBlocks(int area) throws STException {
        int areaOffsetInBlocks = 0;

        if (area > getNumberOfAreas()) {
            throw new STException(BAD_PARAMETER);
        }

        switch (area) {
            case AREA1:
                areaOffsetInBlocks = 0;
                break;
            case AREA2:
                areaOffsetInBlocks = (mRegisterEndArea1.getEndAreaInBlock()) + 1;
                break;
            case AREA3:
                areaOffsetInBlocks = (mRegisterEndArea2.getEndAreaInBlock()) + 1;
                break;
            case AREA4:
                areaOffsetInBlocks = (mRegisterEndArea3.getEndAreaInBlock()) + 1;
                break;
            default:
                throw new STException(BAD_PARAMETER);
        }
        return areaOffsetInBlocks;
    }

    @Override
    public int getAreaOffsetInBytes(int area) throws STException {
        int areaOffsetInBytes;
        areaOffsetInBytes = getAreaOffsetInBlocks(area) * getBlockSizeInBytes();
        return areaOffsetInBytes;
    }

    @Override
    public int getAreaFromBlockAddress(int blockNumber) throws STException {
        if (blockNumber < 0) {
            throw new STException(BAD_PARAMETER);
        }

        int numberOfAreas = getNumberOfAreas();
        // Area containing this block
        int blockArea = numberOfAreas;

        for (int area = AREA1; area <= numberOfAreas; area++) {
            if (blockNumber < getAreaOffsetInBlocks(area)) {
                // This block belongs to the area before
                blockArea = area - 1;
                break;
            }
        }

        return blockArea;
    }

    @Override
    public PasswordLength getAreaPasswordLength(int area) throws STException {
        if (area < AREA1 || area > AREA4) {
            throw new STException(BAD_PARAMETER);
        }
        return PWD_ON_64_BITS;
    }

    @Override
    public byte getPasswordNumber(int area) throws STException {
        ST25DVRegisterRfAiSS.ST25DVSecurityStatusPWDControl pwdControl;
        byte pwdNumber;
        switch (area) {
            case AREA1:
                pwdControl = mRegisterRFA1SS.getSSPWDControl();
                break;
            case AREA2:
                pwdControl = mRegisterRFA2SS.getSSPWDControl();
                break;
            case AREA3:
                pwdControl = mRegisterRFA3SS.getSSPWDControl();
                break;
            case AREA4:
                pwdControl = mRegisterRFA4SS.getSSPWDControl();
                break;
            default:
                throw new STException(BAD_PARAMETER);
        }

        switch(pwdControl) {
            case NO_PWD_SELECTED:
                pwdNumber = 0;
                break;
            case PROTECTED_BY_PWD1:
                pwdNumber = 1;
                break;
            case PROTECTED_BY_PWD2:
                pwdNumber = 2;
                break;
            case PROTECTED_BY_PWD3:
                pwdNumber = 3;
                break;
            default:
                throw new STException(BAD_PARAMETER);
        }
        return pwdNumber;
    }

    @Override
    public void setPasswordNumber(int area, byte passwordNumber) throws STException {
        ST25DVRegisterRfAiSS.ST25DVSecurityStatusPWDControl pwdControl;
        switch(passwordNumber) {
            case 0:
                // passwordNumber 0 is NOT the Configuration Password, but means "no password selected".
                // In such case, no password can be used to unlock this area.
                pwdControl = NO_PWD_SELECTED;
                break;
            case 1:
                pwdControl = PROTECTED_BY_PWD1;
                break;
            case 2:
                pwdControl = PROTECTED_BY_PWD2;
                break;
            case 3:
                pwdControl = PROTECTED_BY_PWD3;
                break;
            default:
                throw new STException(BAD_PARAMETER);
        }

        switch (area) {
            case AREA1:
                mRegisterRFA1SS.setSSPWDControl(pwdControl);
                break;
            case AREA2:
                mRegisterRFA2SS.setSSPWDControl(pwdControl);
                break;
            case AREA3:
                mRegisterRFA3SS.setSSPWDControl(pwdControl);
                break;
            case AREA4:
                mRegisterRFA4SS.setSSPWDControl(pwdControl);
                break;
            default:
                throw new STException(BAD_PARAMETER);
        }
    }

    @Override
    public TagHelper.ReadWriteProtection getReadWriteProtection(int area) throws STException {
        TagHelper.ReadWriteProtection ss;
        switch (area) {
            case AREA1:
                ss = mRegisterRFA1SS.getSSRWProtection();
                break;
            case AREA2:
                ss = mRegisterRFA2SS.getSSRWProtection();
                break;
            case AREA3:
                ss = mRegisterRFA3SS.getSSRWProtection();
                break;
            case AREA4:
                ss = mRegisterRFA4SS.getSSRWProtection();
                break;
            default:
                throw new STException(BAD_PARAMETER);
        }
        return ss;
    }

    @Override
    public void setReadWriteProtection(int area, TagHelper.ReadWriteProtection protection) throws STException {
        ST25DVRegisterRfAiSS reg;
        switch (area) {
            case AREA1:
                reg = mRegisterRFA1SS;
                break;
            case AREA2:
                reg = mRegisterRFA2SS;
                break;
            case AREA3:
                reg = mRegisterRFA3SS;
                break;
            case AREA4:
                reg = mRegisterRFA4SS;
                break;
            default:
                throw new STException(BAD_PARAMETER);
        }
        reg.setSSReadWriteProtection(protection);
    }

    @Override
    public void setReadWriteProtection(int area, TagHelper.ReadWriteProtection protection, byte[] password) throws  STException {
        if (area < AREA1 || area > AREA4) {
            throw new STException(BAD_PARAMETER);
        }
        presentPassword(ST25TV64K_CONFIGURATION_PASSWORD_ID, password);
        setReadWriteProtection(area, protection);
    }

    // -----------------------------------------------------------------------------------
    // End Methods from MultiAreaInterface



    // Methods from Registers
    // -----------------------------------------------------------------------------------

    /**
     * Get registers list
     * @return List of available DV registers
     */
    @Override
    public List<STRegister> getRegisterList()  {
        return mST25TV64KRegisterList;
    }

    @Override
    public List<STRegister> getDynamicRegisterList()  {
        // On ST25TV64K there is no Dynamic Register
        return null;
    }

    public void refreshRegistersStatus() throws STException {
        for (int i = 0; i < mST25TV64KRegisterList.size(); i++) {
            mST25TV64KRegisterList.get(i).invalidateCache();
            mST25TV64KRegisterList.get(i).getRegisterValue();
        }
    }

    @Override
    public STRegister getRegister(int registerAddress) {
        return mST25TV64KRegisterList.get(registerAddress);
    }

    public ST25DVRegisterEndAi getRegisterEndArea1() {
        return mRegisterEndArea1;
    }

    public ST25DVRegisterEndAi getRegisterEndArea2() {
        return mRegisterEndArea2;
    }

    public ST25DVRegisterEndAi getRegisterEndArea3() {
        return mRegisterEndArea3;
    }

    public ST25DVRegisterEndAi getRegisterEndArea(int area) throws STException {
        switch(area) {
            case AREA1:
                return mRegisterEndArea1;
            case AREA2:
                return mRegisterEndArea2;
            case AREA3:
                return mRegisterEndArea3;
            default:
                throw new STException(BAD_PARAMETER);
        }
    }

    /**
     * When programming EndAi registers, a precise sequence should be respected.
     * This function will write those registers by following the procedure from the datasheet.
     * @param endOfArea1
     * @param endOfArea2
     * @param endOfArea3
     */
    public void setAreaEndValues(byte endOfArea1, byte endOfArea2, byte endOfArea3) throws STException {

        byte maxEndOfAreaValue = getMaxEndOfAreaValue();

        STLog.i("Current values:");
        STLog.i("endOfArea1 : " + String.format("%02x", getRegisterEndArea1().getRegisterValue()).toUpperCase() );
        STLog.i("endOfArea2 : " + String.format("%02x", getRegisterEndArea2().getRegisterValue()).toUpperCase() );
        STLog.i("endOfArea3 : " + String.format("%02x", getRegisterEndArea3().getRegisterValue()).toUpperCase() );

        STLog.i("New values:");
        STLog.i("endOfArea1 : " + Helper.convertByteToHexString(endOfArea1));
        STLog.i("endOfArea2 : " + Helper.convertByteToHexString(endOfArea2));
        STLog.i("endOfArea3 : " + Helper.convertByteToHexString(endOfArea3));

        if (((endOfArea1 & 0xFF) > (endOfArea2 & 0xFF)) || ((endOfArea2 & 0xFF) > (endOfArea3 & 0xFF))) {
            throw new STException(BAD_PARAMETER);
        }

        if (endOfArea1 == endOfArea2) {
            // This is allowed only if they are equal to maxEndOfAreaValue
            if (endOfArea1 != maxEndOfAreaValue) {
                throw new STException(BAD_PARAMETER);
            }
        }

        if (endOfArea2 == endOfArea3) {
            // This is allowed only if they are equal to maxEndOfAreaValue
            if (endOfArea2 != maxEndOfAreaValue) {
                throw new STException(BAD_PARAMETER);
            }
        }

        /* Proceed with EndArea programming as described in ST25TV64K datasheet */
        if (getRegisterEndArea3().getRegisterValue() != (maxEndOfAreaValue & 0xFF)) {
            getRegisterEndArea3().setRegisterValue((maxEndOfAreaValue & 0xFF));
        }
        if (getRegisterEndArea2().getRegisterValue() != (maxEndOfAreaValue & 0xFF)) {
            getRegisterEndArea2().setRegisterValue((maxEndOfAreaValue & 0xFF));
        }

        getRegisterEndArea1().setRegisterValue(endOfArea1 & 0xFF);

        if ((endOfArea2 & 0xFF) > (endOfArea1 & 0xFF)) {
            getRegisterEndArea2().setRegisterValue(endOfArea2 & 0xFF);
        }

        if ((endOfArea3 & 0xFF) > (endOfArea2 & 0xFF)) {
            getRegisterEndArea3().setRegisterValue(endOfArea3 & 0xFF);
        }

    }

    /**
     * Indicates the maximum value that can be set in an EndArea register.
     * This maximum value depends on the tag's size.
     * @return
     */
    public byte getMaxEndOfAreaValue() throws STException {
        int memSizeInBlocks = getMemSizeInBytes() / getBlockSizeInBytes();
        int addressOfLastBlock = memSizeInBlocks - 1;
        int maxEndOfAreaValue = (addressOfLastBlock - 7) / 8;

        return ((byte) maxEndOfAreaValue);
    }

    // -----------------------------------------------------------------------------------
    // End Methods from Registers

    /**
     * This command will permanently lock every ST25TV64K's registers.
     * In that case, it is not possible to change any register anymore by RF.
     * (registers can still be unlocked through I2C command).
     */
    public void lockConfiguration() throws STException {
        mRegisterLockCfg.setLockCfgMode(true);
    }

}

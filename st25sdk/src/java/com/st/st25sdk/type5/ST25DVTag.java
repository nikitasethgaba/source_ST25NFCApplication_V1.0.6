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

import static com.st.st25sdk.STException.STExceptionCode.BAD_PARAMETER;
import static com.st.st25sdk.STException.STExceptionCode.CMD_FAILED;
import static com.st.st25sdk.STException.STExceptionCode.NOT_IMPLEMENTED;
import static com.st.st25sdk.type5.st25dv.ST25DVRegisterRfAiSS.ST25DVSecurityStatusPWDControl.NO_PWD_SELECTED;
import static com.st.st25sdk.type5.st25dv.ST25DVRegisterRfAiSS.ST25DVSecurityStatusPWDControl.PROTECTED_BY_PWD1;
import static com.st.st25sdk.type5.st25dv.ST25DVRegisterRfAiSS.ST25DVSecurityStatusPWDControl.PROTECTED_BY_PWD2;
import static com.st.st25sdk.type5.st25dv.ST25DVRegisterRfAiSS.ST25DVSecurityStatusPWDControl.PROTECTED_BY_PWD3;
import static com.st.st25sdk.type5.STType5PasswordInterface.PasswordLength.PWD_ON_64_BITS;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;

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
import com.st.st25sdk.type5.st25dv.ST25DVDynRegisterEh;
import com.st.st25sdk.type5.st25dv.ST25DVDynRegisterGpo;
import com.st.st25sdk.type5.st25dv.ST25DVDynRegisterMb;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterEh;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterEndAi;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterGpo;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterITTime;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterLockCfg;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterMbMode;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterMbWdg;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterRfAiSS;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterRfMgt;


public class ST25DVTag extends STType5MultiAreaTag implements STType5PasswordInterface, MultiAreaInterface, RegisterInterface {

    public static final byte MAX_MEMORY_AREA_SUPPORTED = (byte) 0x04;
    public static final byte MAX_WRITE_MULTIPLE_BLOCKS = 4;

    //Pointer address for read/write Register
    public static final byte REGISTER_GPO_ADDRESS = (byte) 0x00;
    public static final byte REGISTER_IT_TIME_ADDRESS = (byte) 0x01;
    public static final byte REGISTER_EH_MODE_ADDRESS = (byte) 0x02;
    public static final byte REGISTER_RF_MNGT_ADDRESS = (byte) 0x03;
    public static final byte REGISTER_RFA1SS_ADDRESS = (byte) 0x04;
    public static final byte REGISTER_ENDA1_ADDRESS = (byte) 0x05;
    public static final byte REGISTER_RFA2SS_ADDRESS = (byte) 0x06;
    public static final byte REGISTER_ENDA2_ADDRESS = (byte) 0x07;
    public static final byte REGISTER_RFA3SS_ADDRESS = (byte) 0x08;
    public static final byte REGISTER_ENDA3_ADDRESS = (byte) 0x09;
    public static final byte REGISTER_RFA4SS_ADDRESS = (byte) 0x0A;
    // LockCCFile is not accessible by system register, use regular Iso commands on blocks 0 and 1
    public static final byte REGISTER_CCFILE_LOCK_ADDRESS = (byte) 0x0C;
    public static final byte REGISTER_MB_MODE_ADDRESS = (byte) 0x0D;
    public static final byte REGISTER_MB_WDG_ADDRESS = (byte) 0x0E;
    public static final byte REGISTER_LOCK_CFG_ADDRESS = (byte) 0x0F;

    // Pointer address for read/write dynamic registers
    public static final byte REGISTER_DYN_GPO_ADDRESS = (byte) 0x00;
    public static final byte REGISTER_DYN_EH_CTRL_ADDRESS = (byte) 0x02;
    public static final byte REGISTER_DYN_MB_CTRL_ADDRESS = (byte) 0x0D;

    // Passwords
    public static final byte ST25DV_CONFIGURATION_PASSWORD_ID = 0x0;
    public static final byte ST25DV_PASSWORD_1 = 0x1;
    public static final byte ST25DV_PASSWORD_2 = 0x2;
    public static final byte ST25DV_PASSWORD_3 = 0x3;

    // GPO settings
    public enum GpoCommand {
        INTERRUPT,
        SET,
        RESET
    }

    private static final EnumMap<GpoCommand, Byte> mGpoCommandValue = new EnumMap<>(GpoCommand.class);
    static {
        mGpoCommandValue.put(GpoCommand.INTERRUPT, (byte)0x80);
        mGpoCommandValue.put(GpoCommand.SET, (byte)0x00);
        mGpoCommandValue.put(GpoCommand.RESET, (byte) 0x01);
    }

    // Static registers
    private List<STRegister> mST25DVRegisterList;
    // Dynamic registers
    private List<STRegister> mST25DVDynRegisterList;

    // Registers definition
    private ST25DVRegisterGpo mRegisterGpO;
    private ST25DVRegisterITTime mRegisterItTime;
    private ST25DVRegisterEh mRegisterEhMode;
    private ST25DVRegisterRfMgt mRegisterRfMgt;

    private ST25DVRegisterEndAi mRegisterEndArea1;
    private ST25DVRegisterEndAi mRegisterEndArea2;
    private ST25DVRegisterEndAi mRegisterEndArea3;
    private ST25DVRegisterRfAiSS mRegisterRFA1SS;
    private ST25DVRegisterRfAiSS mRegisterRFA2SS;
    private ST25DVRegisterRfAiSS mRegisterRFA3SS;
    private ST25DVRegisterRfAiSS mRegisterRFA4SS;

    private ST25DVRegisterMbMode mRegisterMbMode;
    private ST25DVRegisterMbWdg mRegisterMbWdg;
    private ST25DVRegisterLockCfg mRegisterLockCfg;

    // Dynamic registers
    private ST25DVDynRegisterGpo mDynamicRegisterGpo;
    private ST25DVDynRegisterEh mDynamicRegisterEhCtrl;
    private ST25DVDynRegisterMb mDynamicRegisterMbCtrl;

    public ST25DVTag(RFReaderInterface readerInterface, byte[] uid) throws STException {
        super(readerInterface, uid);

        // For ST25DVTag, we redefine "mSysFile" of parent class Type5Tag in order to use an extended version
        mSysFile = new SysFileType5Extended(mIso15693CustomCommand);

        // Define ST25DV registers
        mST25DVRegisterList = new ArrayList<>();
        initRegisters(mIso15693CustomCommand);

        // Define ST25DV dynamic registers
        mST25DVDynRegisterList = new ArrayList<>();
        initDynamicRegisters(mIso15693CustomCommand);

        mCache.add(mSysFile);
        for (STRegister it: mST25DVDynRegisterList)
            mCache.add(it);

        for (STRegister it: mST25DVRegisterList)
            mCache.add(it);

        initAreaList();

        mName = "ST25DV";
        mTypeDescription = NFCTag.DYNAMIC_NFC_RFID_TAG;
        setMaxReadMultipleBlocksReturned(256);
    }

    private void initRegisters(Iso15693CustomCommand cmd) {
        // GPO
        mRegisterGpO = ST25DVRegisterGpo.newInstance(cmd);

        // Interuption pulse duration
        mRegisterItTime = ST25DVRegisterITTime.newInstance(cmd);

        // Energy Harvesting
        mRegisterEhMode = ST25DVRegisterEh.newInstance(cmd);

        // Disable RF interpreter
        mRegisterRfMgt = ST25DVRegisterRfMgt.newInstance(cmd);

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

        // Mail Box
        mRegisterMbMode = ST25DVRegisterMbMode.newInstance(cmd);

        // Define Mail box Watch dog duration
        mRegisterMbWdg = ST25DVRegisterMbWdg.newInstance(cmd);

        // Disable Configuration change by RFs
        mRegisterLockCfg = ST25DVRegisterLockCfg.newInstance(cmd);

        // Add registers at the right index of the list (given by the RegisterId)
        mST25DVRegisterList.add(mRegisterGpO);
        mST25DVRegisterList.add(mRegisterItTime);
        mST25DVRegisterList.add(mRegisterEhMode);
        mST25DVRegisterList.add(mRegisterRfMgt);

        mST25DVRegisterList.add(mRegisterRFA1SS);
        mST25DVRegisterList.add(mRegisterEndArea1);
        mST25DVRegisterList.add(mRegisterRFA2SS);
        mST25DVRegisterList.add(mRegisterEndArea2);
        mST25DVRegisterList.add(mRegisterRFA3SS);
        mST25DVRegisterList.add(mRegisterEndArea3);
        mST25DVRegisterList.add(mRegisterRFA4SS);

        mST25DVRegisterList.add(mRegisterMbMode);
        mST25DVRegisterList.add(mRegisterMbWdg);
        mST25DVRegisterList.add(mRegisterLockCfg);
    }

    private void initDynamicRegisters(Iso15693CustomCommand cmd) {
        // GPO
        mDynamicRegisterGpo = ST25DVDynRegisterGpo.newInstance(cmd);
        // Energy Harvesting
        mDynamicRegisterEhCtrl = ST25DVDynRegisterEh.newInstance(cmd);

        // Fast Transfer Mode (Mailbox)
        mDynamicRegisterMbCtrl = ST25DVDynRegisterMb.newInstance(cmd);

        // Add dynamic registers to the list
        mST25DVDynRegisterList.add(mDynamicRegisterGpo);
        mST25DVDynRegisterList.add(mDynamicRegisterEhCtrl);
        mST25DVDynRegisterList.add(mDynamicRegisterMbCtrl);
    }

    public boolean isVccOn() throws STException {
        String fieldName = ST25DVDynRegisterEh.ST25DVEHControl.VCC_ON.toString();
        STRegister.STRegisterField fieldVccOn = mDynamicRegisterEhCtrl.getRegisterField(fieldName);
        return (fieldVccOn.getValue() == 1);
    }

    private byte computeMsgLengthParam(int size) {
        // size = 0 is a valid input for mailbox readMsg
        // In that case, the ST25DV return the whole message
        byte length = 0;
        if (size > 0) {
            length = (byte) (size - 1);
        }
        return length;
    }

    public void refreshMBStatus() throws STException {
        refreshMBStatus(false);
    }

    public void refreshMBStatus(boolean useFastCommand) throws STException {
        mDynamicRegisterMbCtrl.invalidateCache();
        mDynamicRegisterMbCtrl.getRegisterValue(useFastCommand);
    }

    public void enableMB() throws STException {
        mRegisterMbMode.setRegisterValue(1);
        mDynamicRegisterMbCtrl.setRegisterValue(1);
    }

    public void disableMB() throws STException {
        mDynamicRegisterMbCtrl.setRegisterValue(0);
    }

    public void resetMB() throws STException {
        disableMB();
        enableMB();
    }

    private boolean isMbCtrlEnable(ST25DVDynRegisterMb.ST25DVMBControl mbCtrlField, boolean refresh, boolean useFastCommand) throws STException {
        if (refresh) {
            mDynamicRegisterMbCtrl.invalidateCache();
        }
        return mDynamicRegisterMbCtrl.isMBFieldEnabled(mbCtrlField, useFastCommand);
    }

    public boolean isMBEnabled(boolean refresh) throws STException {
        return isMBEnabled(refresh, false);
    }

    public boolean hasHostPutMsg(boolean refresh) throws STException {
        return hasHostPutMsg(refresh, false);
    }

    public boolean hasRFPutMsg(boolean refresh) throws STException {
        return hasRFPutMsg(refresh, false);
    }

    public boolean hasHostMissMsg(boolean refresh) throws STException {
        return hasHostMissMsg(refresh, false);
    }

    public boolean hasRFMissMsg(boolean refresh) throws STException {
        return hasRFMissMsg(refresh, false);
    }

    public boolean isMBEnabled(boolean refresh, boolean useFastCommand) throws STException {
        return isMbCtrlEnable(ST25DVDynRegisterMb.ST25DVMBControl.MB_EN, refresh, useFastCommand);
    }

    public boolean hasHostPutMsg(boolean refresh, boolean useFastCommand) throws STException {
        return isMbCtrlEnable(ST25DVDynRegisterMb.ST25DVMBControl.HOST_PUT_MSG, refresh, useFastCommand);
    }

    public boolean hasRFPutMsg(boolean refresh, boolean useFastCommand) throws STException {
        return isMbCtrlEnable(ST25DVDynRegisterMb.ST25DVMBControl.RF_PUT_MSG, refresh, useFastCommand);
    }

    public boolean hasHostMissMsg(boolean refresh, boolean useFastCommand) throws STException {
        return isMbCtrlEnable(ST25DVDynRegisterMb.ST25DVMBControl.HOST_MISS_MSG, refresh, useFastCommand);
    }

    public boolean hasRFMissMsg(boolean refresh, boolean useFastCommand) throws STException {
        return isMbCtrlEnable(ST25DVDynRegisterMb.ST25DVMBControl.RF_MISS_MSG, refresh, useFastCommand);
    }

    @Override
    public PasswordLength getPasswordLength(byte passwordNumber) throws STException {
        if ((passwordNumber & 0xFF) > ST25DV_PASSWORD_3) {
            throw new STException(BAD_PARAMETER);
        }
        return PWD_ON_64_BITS;
    }

    /**
     * Changes the password corresponding to a given passwordNumber.
     * Usually a correct password must be presented for this command to be successful.
     * @param passwordNumber
     * @param newPassword newPassword[0] contains the MSByte
     * @throws STException
     */
    @Override
    public void writePassword(byte passwordNumber, byte[] newPassword) throws STException {
        mIso15693CustomCommand.writePwd(passwordNumber, newPassword);
    }

    /**
     * Changes the password corresponding to a given passwordNumber.
     * Usually a correct password must be presented for this command to be successful.
     * @param passwordNumber
     * @param newPassword newPassword[0] contains the MSByte
     * @param flag
     * @throws STException
     */
    public void writePassword(byte passwordNumber, byte[] newPassword, byte flag) throws STException {
        mIso15693CustomCommand.writePwd(passwordNumber, newPassword, flag, mUid);
    }

    /**
     * Presents a byte[] value for a given password to unlock the area protected by this password
     * @param passwordNumber
     * @param password password[0] contains the MSByte
     * @throws STException
     */
    @Override
    public void presentPassword(byte passwordNumber, byte[] password) throws STException {
        mIso15693CustomCommand.presentPwd(passwordNumber, password);
    }

    /**
     * Presents a byte[] value for a given password to unlock the area protected by this password
     * @param passwordNumber
     * @param password password[0] contains the MSByte
     * @param flag
     * @throws STException
     */
    public void presentPassword(byte passwordNumber, byte[] password, byte flag) throws STException {
        mIso15693CustomCommand.presentPwd(passwordNumber, password, flag, mUid);
    }

    @Override
    public byte getConfigurationPasswordNumber() throws STException {
        return ST25DV_CONFIGURATION_PASSWORD_ID;
    }

    /**
     * Writes the complete buffer in EEPROM at the specified block address.
     * The buffer size must be a multiple of a block size (4 bytes on ST25DV).
     * @param blockAddress Block address in target's EEPROM where to write the data contained in buffer
     * @param buffer Data to write in increments of 4 bytes within the limit of 4 blocks (16 bytes max on ST25DV)
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
     * The buffer size must be a multiple of a block size (4 bytes on ST25DV) with a max size of 4 blocks.
     * @param blockAddress Block address in target's EEPROM where to write the data contained in buffer
     * @param numberOfBlocks Must be less than MAX_WRITE_MULTIPLE_BLOCKS. The tag will write (numberOfBlocks + 1)
     *                       blocks in its EEPROM.
     * @param buffer Data to write in the limit of 4 blocks (16 bytes on ST25DV)
     * @return
     * @throws STException
     */
    public byte writeMultipleBlock(byte blockAddress, byte numberOfBlocks, byte[] buffer) throws STException {
        return writeMultipleBlock(blockAddress, numberOfBlocks, buffer, mIso15693Cmd.getFlag());
    }

    /**
     * Writes the specified number of blocks + 1 of the buffer at the specified EEPROM block address.
     * The buffer size must be a multiple of a block size (4 bytes on ST25DV) with a max size of 4 blocks.
     * @param blockAddress Block address in target's EEPROM where to write the data contained in buffer
     * @param numberOfBlocks Must be less than MAX_WRITE_MULTIPLE_BLOCKS. The tag will write (numberOfBlocks + 1)
     *                       blocks in its EEPROM. Set to 0 to write a single block.
     * @param buffer Data to write in multiples of blocks (max 4 blocks = 16 bytes on ST25DV)
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
     * The buffer size must be a multiple of a block size (4 bytes on ST25DV).
     * @param blockAddress Block address in target's EEPROM where to write the data contained in buffer.
     *                     LSByte first
     * @param buffer Data to write in increments of 4 bytes within the limit of 4 blocks (16 bytes max on ST25DV)
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
     * The buffer size must be a multiple of a block size (4 bytes on ST25DV) with a max size of 4 blocks.
     * @param blockAddress Block address in target's EEPROM where to write the data contained in buffer. LSByte first.
     * @param numberOfBlocks Must be less than MAX_WRITE_MULTIPLE_BLOCKS. The tag will write (numberOfBlocks + 1)
     *                       blocks in its EEPROM.
     *                       Set to 0 to write a single block
     *                       LSByte first
     * @param buffer Data to write in increments of 4 bytes within the limit of 4 blocks (16 bytes max on ST25DV)
     * @return
     * @throws STException
     */
    public byte extendedWriteMultipleBlock(int blockAddress, int numberOfBlocks, byte[] buffer) throws STException {
        return extendedWriteMultipleBlock(blockAddress, numberOfBlocks, buffer, mIso15693Cmd.getFlag());
    }

    /**
     * Writes the specified number of blocks + 1 of the buffer at the specified EEPROM block address.
     * The buffer size must be a multiple of a block size (4 bytes on ST25DV) with a max size of 4 blocks.
     * @param blockAddress Block address in target's EEPROM where to write the data contained in buffer. LSByte first.
     * @param numberOfBlocks Must be less than MAX_WRITE_MULTIPLE_BLOCKS. The tag will write (numberOfBlocks + 1)
     *                       blocks in its EEPROM.
     *                       Set to 0 to write a single block
     *                       LSByte first
     * @param buffer Data to write in increments of 4 bytes within the limit of 4 blocks (16 bytes max on ST25DV)
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

    public byte manageGpoCommand(GpoCommand cmd, byte flag) throws STException {
        return mIso15693CustomCommand.manageGpo(mGpoCommandValue.get(cmd), flag, mUid);
    }

    public byte writeMailboxMessage(int size, byte[] buffer) throws STException {
        return writeMailboxMessage(size, buffer, mIso15693CustomCommand.getFlag());
    }

    public byte writeMailboxMessage(int size, byte[] buffer, byte flag) throws STException {
        if ((size <= 0) || (buffer == null) || (buffer.length < size)) {
            throw new STException(BAD_PARAMETER);
        }

        byte length = computeMsgLengthParam(size);
        return mIso15693CustomCommand.writeMsg(length, buffer, flag, mUid);
    }

    public byte writeMailboxMessage(byte[] buffer) throws STException {
        return writeMailboxMessage(buffer.length, buffer);
    }

    public byte[] readMailboxMessage(byte offset, int size, byte flag) throws STException {
        if (size <= 0) {
            throw new STException(BAD_PARAMETER);
        }
        byte length = computeMsgLengthParam(size);
        return mIso15693CustomCommand.readMsg(offset, length, flag, mUid);
    }

    public byte[] readMailboxMessage(byte offset, int size) throws STException {
        return readMailboxMessage(offset, size, mIso15693CustomCommand.getFlag());
    }

    public int readMailboxMessageLength() throws STException {
        byte[] response = mIso15693CustomCommand.readMsgLength();
        if (response.length < 2) {
            throw new STException(CMD_FAILED, response);
        }
        // response[0] is the status byte
        return (response[1] & 0xFF) + 1;
    }

    public byte fastWriteMailboxMessage(byte[] buffer) throws STException {
        return fastWriteMailboxMessage(buffer.length, buffer);
    }

    public byte fastWriteMailboxMessage(int size, byte[] buffer) throws STException {
        return fastWriteMailboxMessage(size, buffer, mIso15693CustomCommand.getFlag());
    }

    public byte fastWriteMailboxMessage(int size, byte[] buffer, byte flag) throws STException {
        if ((size <= 0) || (buffer == null) || (buffer.length < size)) {
            throw new STException(STException.STExceptionCode.BAD_PARAMETER);
        }

        byte length = computeMsgLengthParam(size);
        return mIso15693CustomCommand.fastWriteMsg(length, buffer, flag, mUid);
    }

    public byte[] fastReadMailboxMessage(byte offset, int size) throws STException {
        return fastReadMailboxMessage(offset, size, mIso15693CustomCommand.getFlag());
    }

    public byte[] fastReadMailboxMessage(byte offset, int size, byte flag) throws STException {
        if (size <= 0) {
            throw new STException(BAD_PARAMETER);
        }
        byte length = computeMsgLengthParam(size);
        return mIso15693CustomCommand.fastReadMsg(offset, length, flag, mUid);
    }

    public int fastReadMailboxMessageLength() throws STException {
        byte[] response = mIso15693CustomCommand.fastReadMsgLength();
        if (response.length < 2) {
            throw new STException(CMD_FAILED, response);
        }
        // response[0] is the status byte
        return (response[1] & 0xFF) + 1;
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

        byte pwdNumber = getPasswordNumber(area);

        // Test area password number
        if (pwdNumber == 0) {
            // This area is not protected by a password
            throw new STException(BAD_PARAMETER);
        } else {
            return getPasswordLength(pwdNumber);
        }
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
        presentPassword(ST25DV_CONFIGURATION_PASSWORD_ID, password);
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
        return mST25DVRegisterList;
    }

    @Override
    public List<STRegister> getDynamicRegisterList()  {
        return mST25DVDynRegisterList;
    }

    public void refreshRegistersStatus() throws STException {
        for (int i = 0; i < mST25DVRegisterList.size(); i++) {
            mST25DVRegisterList.get(i).invalidateCache();
            mST25DVRegisterList.get(i).getRegisterValue();
        }
    }

    public void refreshDynamicRegistersStatus() throws STException {
        for (int i = 0; i < mST25DVDynRegisterList.size(); i++) {
            mST25DVDynRegisterList.get(i).invalidateCache();
            mST25DVDynRegisterList.get(i).getRegisterValue();
        }
    }

    @Override
    public STRegister getRegister(int registerAddress) {
        return mST25DVRegisterList.get(registerAddress);
    }

    public STRegister getDynamicRegister(int registerAddress) {
        return mST25DVDynRegisterList.get(registerAddress);
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

        /* Proceed with EndArea programming as described in ST25DV datasheet */
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
     * This command will permanently lock every ST25DV's registers.
     * In that case, it is not possible to change any register anymore by RF.
     * (registers can still be unlocked through I2C command).
     */
    public void lockConfiguration() throws STException {
        mRegisterLockCfg.setLockCfgMode(true);
    }

}

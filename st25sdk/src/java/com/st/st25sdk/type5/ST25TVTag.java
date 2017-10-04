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
import static com.st.st25sdk.STException.STExceptionCode.NOT_SUPPORTED;
import static com.st.st25sdk.type5.STType5PasswordInterface.PasswordLength.PWD_ON_32_BITS;
import static com.st.st25sdk.type5.STType5PasswordInterface.PasswordLength.PWD_ON_64_BITS;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.st.st25sdk.ConfidentialModeInterface;
import com.st.st25sdk.MultiAreaInterface;
import com.st.st25sdk.NFCTag;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.RandomNumberInterface;
import com.st.st25sdk.RegisterInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.STRegister;
import com.st.st25sdk.TagHelper;
import com.st.st25sdk.command.Iso15693CustomKillCommandInterface;
import com.st.st25sdk.command.Iso15693Protocol;
import com.st.st25sdk.type5.st25tv.ST25TVRegisterArea1SecurityAttribute;
import com.st.st25sdk.type5.st25tv.ST25TVRegisterArea2SecurityAttribute;
import com.st.st25sdk.type5.st25tv.ST25TVRegisterCounterConfiguration;
import com.st.st25sdk.type5.st25tv.ST25TVRegisterCounterValue;
import com.st.st25sdk.type5.st25tv.ST25TVRegisterEasSecurityActivation;
import com.st.st25sdk.type5.st25tv.ST25TVRegisterKeyId;
import com.st.st25sdk.type5.st25tv.ST25TVRegisterLockConfiguration;
import com.st.st25sdk.type5.st25tv.ST25TVRegisterTamperConfiguration;


public class ST25TVTag extends STType5MultiAreaTag implements MultiAreaInterface,
STType5PasswordInterface,
Iso15693CustomKillCommandInterface,
RandomNumberInterface,
ConfidentialModeInterface,
RegisterInterface {

    private List<STRegister> mST25TVRegisterList;

    private ST25TVRegisterArea1SecurityAttribute mRegisterArea1SecurityAttribute;
    private ST25TVRegisterArea2SecurityAttribute mRegisterArea2SecurityAttribute;
    private ST25TVRegisterEasSecurityActivation mRegisterEasSecurityActivation;
    private ST25TVRegisterCounterConfiguration mRegisterCounterConfiguration;
    private ST25TVRegisterCounterValue mRegisterCounterValue;
    private ST25TVRegisterTamperConfiguration mRegisterTamperConfiguration;
    private ST25TVRegisterLockConfiguration mRegisterLockConfiguration;
    private ST25TVRegisterKeyId mRegisterKeyId;
    private boolean mIsEasEnabled = false;

    // ST25TV's registers
    public static final byte ST25TV_REGISTER_AREA1_SECURITY_ATTRIBUTE    = 0x0;
    public static final byte ST25TV_REGISTER_AREA2_SECURITY_ATTRIBUTE    = 0x1;
    public static final byte ST25TV_REGISTER_EAS_SECURITY_ACTIVATION     = 0x2;
    public static final byte ST25TV_REGISTER_WRITE_COUNTER_CONFIGURATION = 0x3;
    public static final byte ST25TV_REGISTER_COUNTER_READ                = 0x4;
    public static final byte ST25TV_REGISTER_TAMPER_CONFIGURATION        = 0x5;
    public static final byte ST25TV_REGISTER_LOCK_CONFIGURATION          = 0x6;
    public static final byte ST25TV_REGISTER_KEY_ID                      = 0x7;

    // ST25TV's passwords
    public static final byte ST25TV_KILL_PASSWORD_ID = 0x0;
    public static final byte ST25TV_CONFIDENTIAL_PASSWORD_ID = 0x0;   // On ST25TV, Kill and Confidential passwords are the same passwords (= password 0)
    public static final byte ST25TV_AREA1_PASSWORD_ID = 0x1;
    public static final byte ST25TV_AREA2_PASSWORD_ID = 0x2;
    public static final byte ST25TV_CONFIGURATION_PASSWORD_ID = 0x3;

    // EAS Telegram should be written in blocks 248 to 255
    private static final int EAS_TELEGRAM_BLOCK_OFFSET = 248;


    public ST25TVTag(RFReaderInterface readerInterface, byte[] uid) throws STException {
        super(readerInterface, uid);

        mName = "ST25TV";
        mTypeDescription = NFCTag.NFC_RFID_TAG;
        mMemSize = 256;

        mST25TVRegisterList = new ArrayList<>();
        mRegisterArea1SecurityAttribute = ST25TVRegisterArea1SecurityAttribute.newInstance(mIso15693CustomCommand, ST25TV_REGISTER_AREA1_SECURITY_ATTRIBUTE);
        mRegisterArea2SecurityAttribute = ST25TVRegisterArea2SecurityAttribute.newInstance(mIso15693CustomCommand, ST25TV_REGISTER_AREA2_SECURITY_ATTRIBUTE);
        mRegisterLockConfiguration = ST25TVRegisterLockConfiguration.newInstance(mIso15693CustomCommand, ST25TV_REGISTER_LOCK_CONFIGURATION);
        mRegisterKeyId = ST25TVRegisterKeyId.newInstance(mIso15693CustomCommand, ST25TV_REGISTER_KEY_ID);
        mRegisterEasSecurityActivation = ST25TVRegisterEasSecurityActivation.newInstance(mIso15693CustomCommand);
        mRegisterCounterConfiguration = ST25TVRegisterCounterConfiguration.newInstance(mIso15693CustomCommand);
        mRegisterCounterValue = ST25TVRegisterCounterValue.newInstance(mIso15693CustomCommand);
        mRegisterTamperConfiguration = ST25TVRegisterTamperConfiguration.newInstance(mIso15693CustomCommand);


        // Add these registers at the right index of the list (given by the registerAddress)
        mST25TVRegisterList.add(mRegisterArea1SecurityAttribute.getRegisterAddress(), mRegisterArea1SecurityAttribute);
        mST25TVRegisterList.add(mRegisterArea2SecurityAttribute.getRegisterAddress(), mRegisterArea2SecurityAttribute);
        mST25TVRegisterList.add(mRegisterEasSecurityActivation.getRegisterAddress(), mRegisterEasSecurityActivation);
        mST25TVRegisterList.add(mRegisterCounterConfiguration.getRegisterAddress(), mRegisterCounterConfiguration);
        mST25TVRegisterList.add(mRegisterCounterValue.getRegisterAddress(), mRegisterCounterValue);
        mST25TVRegisterList.add(mRegisterTamperConfiguration.getRegisterAddress(), mRegisterTamperConfiguration);
        mST25TVRegisterList.add(mRegisterLockConfiguration.getRegisterAddress(), mRegisterLockConfiguration);
        mST25TVRegisterList.add(mRegisterKeyId.getRegisterAddress(), mRegisterKeyId);

        for (STRegister register : mST25TVRegisterList) {
            mCache.add(register);
        }
        initAreaList();

        // Receive notification when register mRegisterArea1SecurityAttribute is changing
        mRegisterArea1SecurityAttribute.addRegisterListener(new STRegister.RegisterListener() {
            @Override
            public void registerChange() throws STException {
                // Register "mRegisterArea1SecurityAttribute" has changed so the number of areas may have changed.
                // Flush the multi area data.
                mRegisterArea1SecurityAttribute.invalidateCache();
                initAreaList();
            }
        });

        // Receive notification when register mRegisterCounterConfiguration is changing
        mRegisterCounterConfiguration.addRegisterListener(new STRegister.RegisterListener() {
            @Override
            public void registerChange() throws STException {
                int clearValue = mRegisterCounterConfiguration.getRegisterField("CLEAR").getValue();
                if(clearValue != 0) {
                    // Reset bit has been set. The counter is disabled and the counter value is reset
                    // so we should invalidate the cache
                    mRegisterCounterConfiguration.invalidateCache();
                    mRegisterCounterValue.invalidateCache();
                }
            }
        });

        // Check if EAS is enabled. In that case, an alarm should be raised
        readEasState();

    }

    ///////////////////////////   Password management   //////////////////////////

    @Override
    public byte[] getRandomNumber()  throws STException {
        byte[] response = mIso15693CustomCommand.getRandomNumber();

        if (response.length == 3) {
            // Command successful
            byte[] randomNumber = new byte[2];

            randomNumber[0] = response[1];
            randomNumber[1] = response[2];
            return randomNumber;
        }

        throw new STException(CMD_FAILED, response);
    }

    @Override
    public void presentPassword(byte passwordNumber, byte[] password) throws STException {
        mIso15693CustomCommand.presentPwd(passwordNumber, password);
    }

    @Override
    public void writePassword(byte passwordNumber, byte[] newPassword) throws STException {

        if(getPasswordLength(passwordNumber) == PWD_ON_64_BITS) {
            // 64 bits password of Area1
            // 2 write password commands are necessary to write the whole password

            // Check that newPassword is on 8 bytes
            if (newPassword.length != 8) {
                throw new STException(BAD_PARAMETER);
            }

            byte[] newPasswordPart1 = Arrays.copyOfRange(newPassword, 0, 4);
            byte[] newPasswordPart2 = Arrays.copyOfRange(newPassword, 4, 8);

            mIso15693CustomCommand.writePwd(ST25TV_AREA1_PASSWORD_ID, newPasswordPart1);
            mIso15693CustomCommand.writePwd(ST25TV_AREA2_PASSWORD_ID, newPasswordPart2);

        } else {
            // 32 bits password

            // Check that newPassword is on 4 bytes
            if (newPassword.length != 4) {
                throw new STException(BAD_PARAMETER);
            }

            mIso15693CustomCommand.writePwd(passwordNumber, newPassword);
        }

    }



    @Override
    public PasswordLength getPasswordLength(byte passwordNumber) throws STException {
        PasswordLength passwordLength;

        switch(passwordNumber) {
            default:
            case ST25TV_KILL_PASSWORD_ID:
            case ST25TV_AREA2_PASSWORD_ID:
            case ST25TV_CONFIGURATION_PASSWORD_ID:
                // All those passwords are always on 32 bits;
                passwordLength = PWD_ON_32_BITS;
                break;

            case ST25TV_AREA1_PASSWORD_ID:
                // If ST25TV configured with 1 memory area : Area1's Password is on 64 bits
                // If ST25TV configured with 2 memory areas: Area1's Password is on 32 bits
                if(getNumberOfAreas() == 1) {
                    passwordLength = PWD_ON_64_BITS;
                } else {
                    passwordLength = PWD_ON_32_BITS;
                }
        }

        return passwordLength;
    }

    @Override
    public PasswordLength getAreaPasswordLength(int area) throws STException {
        PasswordLength passwordLength;

        if (area == AREA1) {
            passwordLength = getPasswordLength(ST25TV_AREA1_PASSWORD_ID);
        } else  if (area == AREA2) {
            passwordLength = getPasswordLength(ST25TV_AREA2_PASSWORD_ID);
        } else {
            throw new STException(BAD_PARAMETER);
        }

        return passwordLength;
    }

    @Override
    public byte getConfigurationPasswordNumber() {
        return ST25TV_CONFIGURATION_PASSWORD_ID;
    }

    @Override
    public byte getPasswordNumber(int area) throws STException {
        if(area == AREA1) {
            return ST25TV_AREA1_PASSWORD_ID;
        } else  if (area == AREA2) {
            return ST25TV_AREA2_PASSWORD_ID;
        } else {
            throw new STException(BAD_PARAMETER);
        }
    }

    @Override
    public void setPasswordNumber(int area, byte passwordNumber) throws STException {
        // On ST25TV, the passwordNumber assigned to each area is fixed and cannot be changed.
        throw new STException(NOT_SUPPORTED);
    }

    @Override
    public TagHelper.ReadWriteProtection getReadWriteProtection(int area) throws STException {
        switch (area) {
            case 1:
                return mRegisterArea1SecurityAttribute.getArea1ReadWriteProtection();
            case 2:
                return mRegisterArea2SecurityAttribute.getArea2SecurityStatus();
            default:
                throw new STException(NOT_SUPPORTED);
        }
    }

    @Override
    public void setReadWriteProtection(int area, TagHelper.ReadWriteProtection protection) throws  STException{
        switch (area) {
            case 1:
                mRegisterArea1SecurityAttribute.setArea1ReadWriteProtection(protection);
                break;
            case 2:
                mRegisterArea2SecurityAttribute.setArea2SecurityStatus(protection);
                break;
            default:
                throw new STException(NOT_SUPPORTED);
        }
    }

    @Override
    public void setReadWriteProtection(int area, TagHelper.ReadWriteProtection protection, byte[] password) throws  STException {
        presentPassword(ST25TV_CONFIGURATION_PASSWORD_ID, password);
        setReadWriteProtection(area, protection);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    public byte[] readConfig(byte configId) throws STException {
        return mIso15693CustomCommand.readConfig(configId);
    }

    public byte writeConfig(byte configId, byte value) throws STException {
        return mIso15693CustomCommand.writeConfig(configId, value);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Indicates if ST25TV's Counter is enabled
     * @return
     */
    public boolean isCounterEnabled() throws STException {
        return getRegisterCounterConfiguration().isCounterEnabled();
    }

    /**
     * Changes state of ST25TV's Counter
     * @param enableCounter
     * @throws STException
     */
    public void enableCounter(boolean enableCounter) throws STException {
        getRegisterCounterConfiguration().setIsCounterEnabled(enableCounter);
    }

    /**
     *  Reset value of ST25TV's Counter
     * @throws STException
     */
    public void resetCounter() throws STException {
        getRegisterCounterConfiguration().resetCounter();
    }

    /**
     * Read ST25TV's Counter value
     * @return
     * @throws STException
     */
    public int readCounterValue() throws STException {
        return getRegisterCounterValue().getCounterValue();
    }

    @Override
    public void enableConfidentialMode(byte[] obfuscatedConfidentialPassword) throws STException {
        mIso15693CustomCommand.enableConfidentialMode(obfuscatedConfidentialPassword);
    }

    //////////////////////////// Multiple Area management ///////////////////////////////////

    @Override
    public int getMaxNumberOfAreas() {
        // This tag can manage up to 2 Areas
        return 2;
    }

    @Override
    public int getNumberOfAreas() throws STException {
        if(mRegisterArea1SecurityAttribute.isMemoryConfiguredInSingleArea()) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public void setNumberOfAreas(int nbOfAreas) throws STException {
        if(nbOfAreas == 1) {
            mRegisterArea1SecurityAttribute.setIsMemoryConfiguredInSingleArea(true);
        } else {
            mRegisterArea1SecurityAttribute.setIsMemoryConfiguredInSingleArea(false);
        }
    }

    @Override
    public int getAreaSizeInBytes(int area) throws STException {

        if(area > getNumberOfAreas()) {
            throw new STException(BAD_PARAMETER);
        }

        if(mRegisterArea1SecurityAttribute.isMemoryConfiguredInSingleArea()) {
            // Tag configured in 1 area of 2048 bits = 256 Bytes = 32 blocks
            return 256;
        } else {
            // Tag configured in 2 areas of 1024 bits = 128 Bytes = 32 blocks
            return 128;
        }
    }


    @Override
    public int getAreaOffsetInBlocks(int area) throws STException {
        int offsetInBlocks;

        if(area > getNumberOfAreas()) {
            throw new STException(BAD_PARAMETER);
        }

        switch(area) {
            default:
            case AREA1:
                offsetInBlocks = 0;
                break;

            case AREA2:
                offsetInBlocks = 32;
                break;
        }

        return offsetInBlocks;
    }

    @Override
    public int getAreaFromBlockAddress(int blockNumber) throws STException {
        int numberOfAreas = getNumberOfAreas();

        if (numberOfAreas == 1) {
            return AREA1;
        } else if (blockNumber < getAreaOffsetInBlocks(AREA2)) {
            return AREA1;
        } else {
            return AREA2;
        }
    }

    @Override
    public int getAreaOffsetInBytes(int area) throws STException {
        return (getAreaOffsetInBlocks(area) * getBlockSizeInBytes());
    }




    ////////////////////////////   EAS   ////////////////////////////////////

    /**
     * Activate the EAS feature
     * @throws STException
     */
    public void setEas() throws STException {
        setEas(mIso15693CustomCommand.getFlag());
    }
    public void setEas(byte flag)  throws STException {
        mIso15693CustomCommand.setEas(flag, getUid());
    }

    /**
     * Deactivate the EAS feature
     * @throws STException
     */
    public void resetEas()  throws STException {
        resetEas(mIso15693CustomCommand.getFlag());
    }
    public void resetEas(byte flag)  throws STException {
        mIso15693CustomCommand.resetEas(flag, getUid());
    }

    /**
     * Permanently lock the EAS_ID, EAS Telegram and EAS configuration
     * @throws STException
     */
    public void lockEas()  throws STException {
        lockEas(mIso15693CustomCommand.getFlag());
    }
    public void lockEas(byte flag)  throws STException {
        mIso15693CustomCommand.lockEas(flag, getUid());
    }

    /**
     * Indicates if the EAS feature is activated.
     * @return
     */
    public boolean isEasEnabled() {
        return mIsEasEnabled;
    }

    /**
     * Read EAS telegram
     * @return
     * @throws STException
     */
    public String readEasTelegram()  throws STException {
        return readEasTelegram(mIso15693CustomCommand.getFlag());
    }
    public String readEasTelegram(byte flag)  throws STException {
        byte[] response =  mIso15693CustomCommand.enableEAS(flag, getUid());

        if (response.length > 1) {
            // Command successful

            // Skip the first byte containing the command status and you get the telegram
            // Add one byte at the end of telegram to ensure that it ends with '\0'
            byte[] telegramData = Arrays.copyOfRange(response, 1, response.length + 1);
            if(telegramData.length > 0) {
                // Convert the telegramData into a string
                String telegram;

                try {
                    telegram = new String(telegramData, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // Conversion failed!
                    throw new STException(CMD_FAILED, response);
                }

                // Trim the null characters
                telegram = telegram.trim();

                return telegram;
            }
        }

        throw new STException(CMD_FAILED, response);
    }

    /**
     * Read EAS_IS value (16 bits integer)
     *
     * WARNING: EAS should be enabled otherwise the command will timeout
     *
     * @return
     * @throws STException
     */
    public int readEasId()  throws STException {
        // optionFlag should be set to read the EAS_ID
        return readEasId((byte) (mIso15693CustomCommand.getFlag() | Iso15693Protocol.OPTION_FLAG));
    }
    public int readEasId(byte flag)  throws STException {
        if ((flag & Iso15693Protocol.OPTION_FLAG) != Iso15693Protocol.OPTION_FLAG) {
            // The option flag bit should be set in order to read the EAS_ID
            throw new STException(BAD_PARAMETER);
        }

        byte[] response = mIso15693CustomCommand.enableEAS(flag, getUid());

        if (response.length == 3) {
            // Command successful

            // response[1] contains the LSB and response[2] the MSB
            int easId = ((response[2] & 0xFF) << 8) + (response[1] & 0xFF) ;

            return easId;
        }

        throw new STException(CMD_FAILED, response);
    }

    /**
     * Write an EAS ID into the tag (16 bits max)
     * @param easId
     * @throws STException
     */
    public void writeEasId(int easId)  throws STException {
        writeEasId(easId, mIso15693CustomCommand.getFlag());
    }
    public void writeEasId(int easId, byte flag)  throws STException {

        if ((easId < 0) || (easId > 0xFFFF)) {
            throw new STException(BAD_PARAMETER);
        }

        // Convert the EAS_ID to a 2 byte array
        byte[] easIdData = new byte[] {
                (byte) ((easId) & 0xFF),
                (byte) ((easId >> 8) & 0xFF)
                };

        mIso15693CustomCommand.writeEasId(easIdData, flag, getUid());
    }

    /**
     * Private function reading if EAS is enabled
     * @throws STException
     */
    private void readEasState()  throws STException {
        // One solution to know if EAS is enabled is to read the EAS telegram.
        // If EAS is disabled, the command will fail and timeout.
        try {
            readEasTelegram();
            // Command succeeded so EAS enabled
            mIsEasEnabled = true;
        } catch (STException e) {
            mIsEasEnabled = false;
        }
    }

    /**
     * Write EAS Telegram into the tag.
     * @param telegram
     * @throws STException
     */
    public void writeEasTelegram(String telegram)  throws STException {
        writeEasTelegram(telegram, mIso15693CustomCommand.getFlag());
    }
    public void writeEasTelegram(String telegram, byte flag)  throws STException {
        byte[] telegramData = telegram.getBytes();

        // Default value. easConfig for 32 bytes long telegram
        byte easConfig;

        if(telegramData.length > getMaxEasTelegramLength()) {
            throw new STException(BAD_PARAMETER);

        } else if(telegramData.length > 16) {
            // 16 Bytes < Telegram length <= 32 bytes

            // Configuration for a 256 bits (= 32 Bytes) EAS telegram
            easConfig = 0x00;
        } else if(telegramData.length > 8) {
            // 8 Bytes < Telegram length <= 16 bytes

            // Configuration for a 128 bits (= 16 Bytes) EAS telegram
            easConfig = 0x01;
        } else if(telegramData.length > 4) {
            // 4 Bytes < Telegram length <= 8 bytes

            // Configuration for a 64 bits (= 8 Bytes) EAS telegram
            easConfig = 0x02;
        } else {
            // Telegram length <= 4 bytes

            // Configuration for a 32 bits (= 4 Bytes) EAS telegram
            easConfig = 0x03;
        }

        // Create a telegram padded (with '0') to 32 Bytes
        byte[] paddedTelegram = Arrays.copyOfRange(telegramData, 0, 32);

        writeEasConfig(easConfig, flag);

        // EAS Telegram should be written in blocks 248 to 255

        // NB: We cannot call writeBytes() directly because this range is out of EEPROM range (bytes 0 to 255)
        //     so the range verification will fail.
        //     We instead call mType5Cmd.writeBytes() which is "after" the range verification.
        mType5Cmd.writeBytes(EAS_TELEGRAM_BLOCK_OFFSET * getBlockSizeInBytes(), paddedTelegram);

    }

    /**
     * Indicates the max possible length for the EAS Telegram.
     * @return
     */
    public int getMaxEasTelegramLength() {
        // The max telegram length is 256 bits = 32 Bytes
        return 32;
    }

    /**
     * Write EAS configuration
     * @param config
     * @throws STException
     */
    public void writeEasConfig(byte config)  throws STException {
        writeEasConfig(config, mIso15693CustomCommand.getFlag());
    }
    public void writeEasConfig(byte config, byte flag)  throws STException {
        mIso15693CustomCommand.writeEasConfig(config, flag, getUid());
    }

    /**
     * Write EAS Security Configuration. It allows to activate the protection by password
     * of the EAS configuration
     * @param isEasWriteProtected
     * @throws STException
     */
    public void writeEasSecurityConfiguration(boolean isEasWriteProtected) throws STException {
        getRegisterEasSecurityActivation().setIsEasWriteProtected(isEasWriteProtected);
    }

    //////////////////////////// KILL COMMANDS  ///////////////////////////////////

    @Override
    public byte kill(byte[] unencryptedKillCode)  throws STException {
        return mIso15693CustomCommand.kill(unencryptedKillCode);
    }

    @Override
    public byte writeKill(byte[] unencryptedKillPassword)  throws STException {
        return mIso15693CustomCommand.writeKill(unencryptedKillPassword);
    }

    @Override
    public byte lockKill()  throws STException {
        return mIso15693CustomCommand.lockKill();
    }

    ////////////////////////// INITIATE & INVENTORY READ COMMANDS  //////////////////////

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

    public byte[] inventoryRead(byte flag, byte blockAddress, byte nbrOfBlocks) throws STException {
        return mIso15693CustomCommand.inventoryRead(flag, blockAddress, nbrOfBlocks);
    }
    public byte[] inventoryRead(byte flag, byte maskLength, byte[] maskValue, byte blockAddress, byte nbrOfBlocks) throws STException {
        return mIso15693CustomCommand.inventoryRead(flag, maskLength, maskValue, blockAddress, nbrOfBlocks);
    }
    public byte[] inventoryRead(byte flag, byte maskLength, byte[] maskValue, byte afiField, byte blockAddress, byte nbrOfBlocks) throws STException {
        return mIso15693CustomCommand.inventoryRead(flag, maskLength, maskValue, afiField, blockAddress, nbrOfBlocks);
    }

    public byte[] fastInventoryRead(byte flag, byte blockAddress, byte nbrOfBlocks) throws STException {
        return mIso15693CustomCommand.fastInventoryRead(flag, blockAddress, nbrOfBlocks);
    }
    public byte[] fastInventoryRead(byte flag, byte maskLength, byte[] maskValue, byte blockAddress, byte nbrOfBlocks) throws STException {
        return mIso15693CustomCommand.fastInventoryRead(flag, maskLength, maskValue, blockAddress, nbrOfBlocks);
    }
    public byte[] fastInventoryRead(byte flag, byte maskLength, byte[] maskValue, byte afiField, byte blockAddress, byte nbrOfBlocks) throws STException {
        return mIso15693CustomCommand.fastInventoryRead(flag, maskLength, maskValue, afiField, blockAddress, nbrOfBlocks);
    }


    /////////////////////////   Registers   /////////////////////////////////

    @Override
    public List<STRegister> getRegisterList() {
        return mST25TVRegisterList;
    }

    @Override
    public List<STRegister> getDynamicRegisterList() {
        return null;
    }

    @Override
    public STRegister getRegister(int registerAddress) {
        return mST25TVRegisterList.get(registerAddress);
    }


    public ST25TVRegisterEasSecurityActivation getRegisterEasSecurityActivation() {
        return mRegisterEasSecurityActivation;
    }

    public ST25TVRegisterCounterConfiguration getRegisterCounterConfiguration() {
        return mRegisterCounterConfiguration;
    }

    public ST25TVRegisterCounterValue getRegisterCounterValue() {
        return mRegisterCounterValue;
    }

    public ST25TVRegisterTamperConfiguration getRegisterTamperConfiguration() {
        return mRegisterTamperConfiguration;
    }

    /**
     * This command will permanently lock every ST25TV's registers.
     * In that case, it is not possible to change any register anymore.
     */
    public void lockConfiguration() throws STException {
        mRegisterLockConfiguration.lockConfiguration();
    }

}

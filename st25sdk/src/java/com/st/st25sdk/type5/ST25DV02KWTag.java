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
import com.st.st25sdk.STRegister;
import com.st.st25sdk.TagHelper;
import com.st.st25sdk.type5.st25dv02kw.ST25DV02KWRegisterPwmCtrlSecurityAttribute;
import com.st.st25sdk.type5.st25dv02kw.ST25DV02KWRegisterPwmRfConfiguration;
import com.st.st25sdk.type5.st25tv.ST25TVRegisterArea1SecurityAttribute;
import com.st.st25sdk.type5.st25tv.ST25TVRegisterArea2SecurityAttribute;
import com.st.st25sdk.type5.st25tv.ST25TVRegisterKeyId;
import com.st.st25sdk.type5.st25tv.ST25TVRegisterLockConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.st.st25sdk.STException.STExceptionCode.BAD_PARAMETER;
import static com.st.st25sdk.STException.STExceptionCode.CMD_FAILED;
import static com.st.st25sdk.type5.STType5PasswordInterface.PasswordLength.PWD_ON_32_BITS;
import static com.st.st25sdk.type5.STType5PasswordInterface.PasswordLength.PWD_ON_64_BITS;

public class ST25DV02KWTag extends STType5MultiAreaTag implements MultiAreaInterface,
STType5PasswordInterface,
RegisterInterface {

    private List<STRegister> mST25DV02KWRegisterList;

    private ST25TVRegisterArea1SecurityAttribute mRegisterArea1SecurityAttribute;
    private ST25TVRegisterArea2SecurityAttribute mRegisterArea2SecurityAttribute;
    private ST25DV02KWRegisterPwmCtrlSecurityAttribute mRegisterPwmCtrlSecurityAttribute;
    private ST25DV02KWRegisterPwmRfConfiguration mRegisterPwmRfConfiguration;
    private ST25TVRegisterLockConfiguration mRegisterLockConfiguration;
    private ST25TVRegisterKeyId mRegisterKeyId;

    public static final byte ST25DV02KW_REGISTER_AREA1_SECURITY_ATTRIBUTE    = 0x0;
    public static final byte ST25DV02KW_REGISTER_AREA2_SECURITY_ATTRIBUTE    = 0x1;
    public static final byte ST25DV02KW_REGISTER_PWM_CTRL_SECURITY_ATTRIBUTES = 0x2;
    public static final byte ST25DV02KW_REGISTER_PWM_RF_CONFIGURATION         = 0x3;
    public static final byte ST25DV02KW_REGISTER_LOCK_CONFIGURATION           = 0x4;
    public static final byte ST25DV02KW_REGISTER_KEY_ID                       = 0x5;

    public static final byte ST25DV02KW_PWM_PASSWORD_ID = 0x00;
    public static final byte ST25DV02KW_AREA1_PASSWORD_ID = 0x01;
    public static final byte ST25DV02KW_AREA2_PASSWORD_ID = 0x02;
    public static final byte ST25DV02KW_CONFIGURATION_PASSWORD_ID = 0x03;

    public static final byte ST25DV02KW_PWM1_CONTROL_ADDR = (byte) 0xF8;
    public static final byte ST25DV02KW_PWM2_CONTROL_ADDR = (byte) 0xF9;

    public static final double  ST25DV02KW_PWM_RESOLUTION_NS = 62.5;
    public static final int     ST25DV02KW_PWM_MAX_PWM_FREQ = 31250;
    public static final int     ST25DV02KW_PWM_MIN_PWM_FREQ = 488;

    private byte[] mPwm1Control = new byte[4];
    private byte[] mPwm2Control = new byte[4];

    public ST25DV02KWTag(RFReaderInterface readerInterface, byte[] uid) throws STException {
        super(readerInterface, uid);

        mName = "ST25DV02K-W";
        mTypeDescription = NFCTag.NFC_RFID_TAG;
        mMemSize = 256;

        mST25DV02KWRegisterList = new ArrayList<>();

        mRegisterArea1SecurityAttribute = ST25TVRegisterArea1SecurityAttribute.newInstance(mIso15693CustomCommand, ST25DV02KW_REGISTER_AREA1_SECURITY_ATTRIBUTE);
        mRegisterArea2SecurityAttribute = ST25TVRegisterArea2SecurityAttribute.newInstance(mIso15693CustomCommand, ST25DV02KW_REGISTER_AREA2_SECURITY_ATTRIBUTE);
        mRegisterPwmCtrlSecurityAttribute = ST25DV02KWRegisterPwmCtrlSecurityAttribute.newInstance(mIso15693CustomCommand, ST25DV02KW_REGISTER_PWM_CTRL_SECURITY_ATTRIBUTES);
        mRegisterPwmRfConfiguration = ST25DV02KWRegisterPwmRfConfiguration.newInstance(mIso15693CustomCommand, ST25DV02KW_REGISTER_PWM_RF_CONFIGURATION);
        mRegisterLockConfiguration = ST25TVRegisterLockConfiguration.newInstance(mIso15693CustomCommand, ST25DV02KW_REGISTER_LOCK_CONFIGURATION );
        mRegisterKeyId = ST25TVRegisterKeyId.newInstance(mIso15693CustomCommand, ST25DV02KW_REGISTER_KEY_ID);

        // Add these registers at the right index of the list (given by the registerAddress)
        mST25DV02KWRegisterList.add(mRegisterArea1SecurityAttribute.getRegisterAddress(), mRegisterArea1SecurityAttribute);
        mST25DV02KWRegisterList.add(mRegisterArea2SecurityAttribute.getRegisterAddress(), mRegisterArea2SecurityAttribute);
        mST25DV02KWRegisterList.add(mRegisterPwmCtrlSecurityAttribute.getRegisterAddress(), mRegisterPwmCtrlSecurityAttribute);
        mST25DV02KWRegisterList.add(mRegisterPwmRfConfiguration.getRegisterAddress(), mRegisterPwmRfConfiguration);
        mST25DV02KWRegisterList.add(mRegisterLockConfiguration.getRegisterAddress(), mRegisterLockConfiguration);
        mST25DV02KWRegisterList.add(mRegisterKeyId.getRegisterAddress(), mRegisterKeyId);


        for (STRegister register : mST25DV02KWRegisterList) {
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
    }


    @Override
    public List<STRegister> getRegisterList() {
        return mST25DV02KWRegisterList;
    }

    @Override
    public List<STRegister> getDynamicRegisterList() {
        return null;
    }

    @Override
    public STRegister getRegister(int registerAddress) {
        return mST25DV02KWRegisterList.get(registerAddress);
    }

    ///////////////////////////   Password management   //////////////////////////


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
            if(newPassword.length != 8) {
                throw new STException(BAD_PARAMETER);
            }

            byte[] newPasswordPart1 = Arrays.copyOfRange(newPassword, 0, 4);
            byte[] newPasswordPart2 = Arrays.copyOfRange(newPassword, 4, 8);

            mIso15693CustomCommand.writePwd(ST25DV02KW_AREA1_PASSWORD_ID , newPasswordPart1);
            mIso15693CustomCommand.writePwd(ST25DV02KW_AREA2_PASSWORD_ID , newPasswordPart2);

        } else {
            // 32 bits password

            // Check that newPassword is on 4 bytes
            if(newPassword.length != 4) {
                throw new STException(BAD_PARAMETER);
            }

            mIso15693CustomCommand.writePwd(passwordNumber, newPassword);
        }

    }



    @Override
    public PasswordLength getPasswordLength(byte passwordNumber) throws STException {
        PasswordLength passwordLength;

        switch (passwordNumber) {
            default:
            case ST25DV02KW_AREA2_PASSWORD_ID :
            case ST25DV02KW_CONFIGURATION_PASSWORD_ID:
            case ST25DV02KW_PWM_PASSWORD_ID:
                // All those passwords are always on 32 bits;
                passwordLength = PWD_ON_32_BITS;
                break;

            case ST25DV02KW_AREA1_PASSWORD_ID:
                // If ST25DV02K-W configured with 1 memory area : Area1's Password is on 64 bits
                // If ST25DV02K-W configured with 2 memory areas: Area1's Password is on 32 bits
                if (getNumberOfAreas() == 1) {
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
            passwordLength = getPasswordLength(ST25DV02KW_AREA1_PASSWORD_ID);
        } else  if (area == AREA2) {
            passwordLength = getPasswordLength(ST25DV02KW_AREA2_PASSWORD_ID);
        } else {
            throw new STException(BAD_PARAMETER);
        }

        return passwordLength;
    }

    @Override
    public byte getConfigurationPasswordNumber() {
        return ST25DV02KW_CONFIGURATION_PASSWORD_ID;
    }

    @Override
    public byte getPasswordNumber(int area) throws STException {
        if (area == AREA1) {
            return ST25DV02KW_AREA1_PASSWORD_ID;
        } else  if (area == AREA2) {
            return ST25DV02KW_AREA2_PASSWORD_ID;
        } else {
            throw new STException(BAD_PARAMETER);
        }
    }

    //////////////////////////// Multiple Area management ///////////////////////////////////

    @Override
    public int getMaxNumberOfAreas() {
        // This tag can manage up to 2 Areas
        return 2;
    }

    @Override
    public int getNumberOfAreas() throws STException {
        if (mRegisterArea1SecurityAttribute.isMemoryConfiguredInSingleArea()) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public void setNumberOfAreas(int nbOfAreas) throws STException {
        if (nbOfAreas == 1) {
            mRegisterArea1SecurityAttribute.setIsMemoryConfiguredInSingleArea(true);
        } else {
            mRegisterArea1SecurityAttribute.setIsMemoryConfiguredInSingleArea(false);
        }
    }

    @Override
    public int getAreaSizeInBytes(int area) throws STException {

        if (area > getNumberOfAreas()) {
            throw new STException(BAD_PARAMETER);
        }

        if (mRegisterArea1SecurityAttribute.isMemoryConfiguredInSingleArea()) {
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

        if (area > getNumberOfAreas()) {
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


    @Override
    public void setPasswordNumber(int area, byte passwordNumber) throws STException {
        // On ST25DV02K-W, the passwordNumber assigned to each area is fixed and cannot be changed.
        throw new STException(STException.STExceptionCode.NOT_SUPPORTED);
    }

    @Override
    public TagHelper.ReadWriteProtection getReadWriteProtection(int area) throws STException {
        switch (area) {
            case 1:
                return mRegisterArea1SecurityAttribute.getArea1ReadWriteProtection();
            case 2:
                return mRegisterArea2SecurityAttribute.getArea2SecurityStatus();
            default:
                throw new STException(STException.STExceptionCode.NOT_SUPPORTED);
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
                throw new STException(STException.STExceptionCode.NOT_SUPPORTED);
        }
    }

    @Override
    public void setReadWriteProtection(int area, TagHelper.ReadWriteProtection protection, byte[] password) throws  STException {
        presentPassword(ST25DV02KW_CONFIGURATION_PASSWORD_ID, password);
        setReadWriteProtection(area, protection);
    }

    public TagHelper.ReadWriteProtection getPwmCtrlAccessRights() throws STException {
        return mRegisterPwmCtrlSecurityAttribute.getPwmCtrlAccessRights();
    }

    public void  setPwmCtrlAccessRights(TagHelper.ReadWriteProtection readWriteProtection) throws STException {
        mRegisterPwmCtrlSecurityAttribute.setPwmCtrlAccessRights(readWriteProtection);
    }


    public ST25DV02KWRegisterPwmRfConfiguration.OutputDriverTrimming getPwm1OutputDriverTrimming() throws STException {
        return mRegisterPwmRfConfiguration.getOutputDriverTrimming(ST25DV02KWRegisterPwmRfConfiguration.PwmDrive.PWM1_DRIVE);
    }

    public ST25DV02KWRegisterPwmRfConfiguration.OutputDriverTrimming getPwm2OutputDriverTrimming() throws STException {
        return mRegisterPwmRfConfiguration.getOutputDriverTrimming(ST25DV02KWRegisterPwmRfConfiguration.PwmDrive.PWM2_DRIVE);
    }

    public void setPwm1OutputDriverTrimming(ST25DV02KWRegisterPwmRfConfiguration.OutputDriverTrimming trimmingValue) throws STException {
        mRegisterPwmRfConfiguration.setOutputDriverTrimming(ST25DV02KWRegisterPwmRfConfiguration.PwmDrive.PWM1_DRIVE, trimmingValue);
    }

    public void setPwm2OutputDriverTrimming(ST25DV02KWRegisterPwmRfConfiguration.OutputDriverTrimming trimmingValue ) throws STException {
        mRegisterPwmRfConfiguration.setOutputDriverTrimming(ST25DV02KWRegisterPwmRfConfiguration.PwmDrive.PWM2_DRIVE, trimmingValue);
    }

    public ST25DV02KWRegisterPwmRfConfiguration.DualityManagement getDualityManagement() throws STException {
        return mRegisterPwmRfConfiguration.getDualityManagement();
    }

    public void setDualityManagement(ST25DV02KWRegisterPwmRfConfiguration.DualityManagement value) throws STException {
        mRegisterPwmRfConfiguration.setDualityManagement(value);
    }
    /**
     * This command will permanently lock every ST25DV02K-W's registers.
     * In that case, it is not possible to change any register anymore.
     */
    public void lockConfiguration() throws STException {
        mRegisterLockConfiguration.lockConfiguration();
    }

    private byte[] readPwmControl(byte pwmControlAddr) throws STException {
        byte[] pwmControl;

        if (pwmControlAddr == ST25DV02KW_PWM1_CONTROL_ADDR)
            pwmControl = mPwm1Control;
        else if (pwmControlAddr == ST25DV02KW_PWM2_CONTROL_ADDR)
            pwmControl = mPwm2Control;
        else
            throw new STException(STException.STExceptionCode.BAD_PARAMETER);

        if (mCache.isCacheActivated() && !mCache.isCacheValid(pwmControl)) {
            byte[] buf = readSingleBlock(pwmControlAddr);

            if (buf.length < (getBlockSizeInBytes() + 1)) {
                throw new STException(CMD_FAILED, buf);
            }
            System.arraycopy(buf, 1, pwmControl, 0, 4);
            mCache.add(pwmControl);
        }

        return pwmControl;
    }


    private void writePwmControl(byte pwmControlAddr, byte[] pwmControl) throws STException {
        mCache.remove(pwmControl);
        writeSingleBlock(pwmControlAddr, pwmControl);

        if (pwmControlAddr == ST25DV02KW_PWM1_CONTROL_ADDR)
            System.arraycopy(pwmControl, 0, mPwm1Control, 0, 4);
        else if (pwmControlAddr == ST25DV02KW_PWM2_CONTROL_ADDR)
            System.arraycopy(pwmControl, 0, mPwm2Control, 0, 4);

        mCache.add(pwmControl);
    }

    /**
     * This method writes the pwm1 control value of the tag
     * @param pwmControl control value to write
     * @throws STException
     */
    public void writePwm1Control(byte[] pwmControl)  throws STException {
        writePwmControl(ST25DV02KW_PWM1_CONTROL_ADDR, pwmControl);
    }

    /**
     * This method reads the pwm1 control value of the tag
     * @return the byte[] of pwm1 control value
     * @throws STException
     */
    public byte[] readPwm1Control()  throws STException {
        return readPwmControl(ST25DV02KW_PWM1_CONTROL_ADDR);
    }

    /**
     * This method writes the pwm2 control value of the tag
     * @param pwmControl control value to write
     * @throws STException
     */
    public void writePwm2Control(byte[] pwmControl)  throws STException {
        writePwmControl(ST25DV02KW_PWM2_CONTROL_ADDR, pwmControl);
    }

    /**
     * This method reads the pwm2 control value of the tag
     * @return the byte[] of pwm2 control value
     * @throws STException
     */
    public byte[] readPwm2Control()  throws STException {
        return readPwmControl(ST25DV02KW_PWM2_CONTROL_ADDR);
    }

    /**
     * Compute the period taking as parameter the control value in byte[]
     * @param control input value contained or to be written on the tag
     * @return the period value of pwm
     */
    public int computePeriodFromControl(byte[] control) {
        if (control == null || control.length < 4)
            return 0;
        return Helper.convert2BytesHexaFormatToInt(new byte[]{(byte) (control[1] & 0x7F), control[0]});
    }

    /**
     * Compute the pulse width taking as parameter the control value in byte[]
     * @param control input value contained or to be written on the tag
     * @return the pulse width of pwm
     */
    public int computePulseWidthFromControl(byte[] control) {
        if (control == null || control.length < 4)
            return 0;
        return Helper.convert2BytesHexaFormatToInt(new byte[]{(byte) (control[3] & 0x7F), control[2]});
    }

    /**
     * Compute the duty cycle taking as parameter the control value in byte[]
     * @param control input value contained or to be written on the tag
     * @return the duty cycle of pwm
     */
    public int computeDutyCycleFromControl(byte[] control) {
        int period = computePeriodFromControl(control);
        if (period != 0) {
            if (((100 * computePulseWidthFromControl(control)) % period) == 0)
                return (100 * computePulseWidthFromControl(control)) / period;
            else
                return (100 * computePulseWidthFromControl(control)) / period + 1;
        }
        else
            return 0;
    }

    /**
     * Compute the frequence taking as parameter the control value in byte[]
     * * Refer to Datasheet for more details
     * @param control input value contained or to be written on the tag
     * @return the pwm frequency
     */
    public int computeFreqFromControl(byte[] control) {
        int period = computePeriodFromControl(control);
        if (period != 0) {
            return (int) (1000000000 / (period * ST25DV02KWTag.ST25DV02KW_PWM_RESOLUTION_NS));
        }
        else
            return 0;
    }

    /**
     * Compute the period taking as parameter frequence
     * * Refer to Datasheet for more details
     * @param freq to compute period
     * @return the pwm period
     */
    public int computePeriod(int freq) throws  STException {
        if ( freq > ST25DV02KW_PWM_MAX_PWM_FREQ || freq < ST25DV02KW_PWM_MIN_PWM_FREQ)
            throw new STException(STException.STExceptionCode.BAD_PARAMETER);
        return (int) (1000000000/(freq*ST25DV02KWTag.ST25DV02KW_PWM_RESOLUTION_NS));
    }

    /**
     *
     * @param period period value
     * @param dutyCycle
     * @return the pwm pulse width
     */
    public int computePulseWidth(int period, int dutyCycle) throws STException{
        if ( period < 0 || dutyCycle < 0 || dutyCycle > 100)
            throw new STException(STException.STExceptionCode.BAD_PARAMETER);
        return period * dutyCycle /100;
    }

    /**
     * Returns if pwm enable with the control value given as parameter
     * Refer to Datasheet for more details
     * @param control input value contained or to be written on the tag
     * @return pwm status
     */
    public boolean isPwmEnable(byte[] control) {
        return control != null && control.length >= 4 && ((control[3] & (byte) 0x80) == (byte) 0x80);
    }

    /**
     * This method write the pwm1 config value taking as parameter period pulse width and if pwm is enable or not
     * Refer to Datasheet for more details
     * @param freq period value to write
     * @param dutyCycle pulse width to write
     * @param pwmEnable pwm enable or not
     * @throws STException
     */
    public void writePwm1Control(int freq, int dutyCycle, boolean pwmEnable)  throws STException {
        if ( freq > ST25DV02KW_PWM_MAX_PWM_FREQ || freq < ST25DV02KW_PWM_MIN_PWM_FREQ || dutyCycle < 0 || dutyCycle > 100)
            throw new STException(STException.STExceptionCode.BAD_PARAMETER);
        int period = computePeriod(freq);
        int pulseWidth = computePulseWidth(period, dutyCycle);
        computeControlFromPeriodAndPulseWidth(mPwm1Control, period, pulseWidth, pwmEnable);
        writePwm1Control(mPwm1Control);
    }

    /**
     * This method write the pwm2 config value taking as parameter period pulse width and if pwm is enable or not
     * Refer to Datasheet for more details
     * @param freq period value to write
     * @param dutyCycle pulse width to write
     * @param pwmEnable pwm enable or not
     * @throws STException
     */
    public void writePwm2Control(int freq, int dutyCycle, boolean pwmEnable)  throws STException {
        if ( freq > ST25DV02KW_PWM_MAX_PWM_FREQ || freq < ST25DV02KW_PWM_MIN_PWM_FREQ || dutyCycle < 0 || dutyCycle > 100)
            throw new STException(STException.STExceptionCode.BAD_PARAMETER);
        int period = computePeriod(freq);
        int pulseWidth = computePulseWidth(period, dutyCycle);
        computeControlFromPeriodAndPulseWidth(mPwm2Control, period, pulseWidth, pwmEnable);
        writePwm2Control(mPwm2Control);
    }

    /**
     * This method returns a byte[] control to write on the Tag taking as parameter the period pulse width and if pwm is enable or not
     * * Refer to Datasheet for more details
     * @param control output 4 bytes that can be written on the tag
     * @param period period value to compute
     * @param pulseWidth pulse width to compute
     * @param pwmEnable is pwm enable or not
     */
    public void computeControlFromPeriodAndPulseWidth(byte[] control, int period, int pulseWidth, boolean pwmEnable) {
        if (control == null || control.length < 4)
            return;

        control[1] = (byte) ((period & 0x7F00) >> 8);
        control[0] = (byte) ((period & 0xFF));
        if (pwmEnable) {
            control[3] = (byte) (((pulseWidth & 0x7F00) >> 8) | 0x80);
        }
        else {
            control[3] = (byte) ((pulseWidth & 0x7F00) >> 8);
        }
        control[2] = (byte) ((pulseWidth & 0xFF));
    }


}

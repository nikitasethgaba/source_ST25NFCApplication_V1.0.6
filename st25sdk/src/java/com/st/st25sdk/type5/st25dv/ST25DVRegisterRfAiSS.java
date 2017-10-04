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

package com.st.st25sdk.type5.st25dv;

import static com.st.st25sdk.STRegister.RegisterAccessRights.REGISTER_READ_WRITE;
import static com.st.st25sdk.STRegister.RegisterDataSize.REGISTER_DATA_ON_8_BITS;
import static com.st.st25sdk.type5.ST25DVTag.REGISTER_RFA1SS_ADDRESS;
import static com.st.st25sdk.type5.ST25DVTag.REGISTER_RFA2SS_ADDRESS;
import static com.st.st25sdk.type5.ST25DVTag.REGISTER_RFA3SS_ADDRESS;
import static com.st.st25sdk.type5.ST25DVTag.REGISTER_RFA4SS_ADDRESS;

import java.util.ArrayList;
import java.util.List;

import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.STRegister;
import com.st.st25sdk.TagHelper;
import com.st.st25sdk.command.Iso15693CustomCommand;


/**
 * ST25DVRegisterRfAiSS represents a class that aims to manage the Password and protection of DV Areai
 */
public class ST25DVRegisterRfAiSS extends STRegister {

    private int mAreaId;

    public enum ST25DVSecurityStatusPWDControl {
        NO_PWD_SELECTED,
        PROTECTED_BY_PWD1,
        PROTECTED_BY_PWD2,
        PROTECTED_BY_PWD3,
    }

    public static ST25DVRegisterRfAiSS newInstance(Iso15693CustomCommand iso15693CustomCommand, int index) {

        String registerName = "RFA" + index + "SS";
        String registerContentDescription = "Area " + index + " Security Status for RF access protection";
        byte registerAddress = REGISTER_RFA1SS_ADDRESS;

        switch(index) {
            case 1:
                registerAddress = REGISTER_RFA1SS_ADDRESS;
                break;
            case 2:
                registerAddress = REGISTER_RFA2SS_ADDRESS;
                break;
            case 3:
                registerAddress = REGISTER_RFA3SS_ADDRESS;
                break;
            case 4:
                registerAddress = REGISTER_RFA4SS_ADDRESS;
                break;
            default:
                STLog.e("Wrong register index - Available index [1-4]");
                //throw new STException(BAD_PARAMETER);
        }

        return new ST25DVRegisterRfAiSS(
                iso15693CustomCommand,
                index,
                registerAddress,
                registerName,
                registerContentDescription,
                REGISTER_READ_WRITE,
                REGISTER_DATA_ON_8_BITS);
    }
    public ST25DVRegisterRfAiSS(Iso15693CustomCommand iso15693CustomCommand,
            int areaId,
            byte registerId,
            String registerName,
            String registerContentDescription,
            RegisterAccessRights registerAccessRights,
            RegisterDataSize registerDataSize) {

        super(iso15693CustomCommand,registerId, registerName, registerContentDescription, registerAccessRights, registerDataSize);

        mAreaId = areaId;

        final String area = String.valueOf(areaId);
        List<STRegisterField> registerFields = new ArrayList<>();
        registerFields.add(new STRegisterField(
                "PWD_CTRL_A" + area,
                "Area " + area + " RF user security session\n" +
                        "00: Security session can't be opened by password\n" +
                        "01: Security session opened by RF_PWD_1\n" +
                        "10: Security session opened by RF_PWD_2\n" +
                        "11: Security session opened by RF_PWD_3",
                        0b11));

        if (mAreaId == 1) {
            // Permissions for RFA1SS
            registerFields.add(new STRegisterField(
                    "RW_PROTECTION_A" + area,
                    "Area " + area + " RF access\n" +
                            "00: Read always allowed / Write always allowed\n" +
                            "01: Read always allowed / Write allowed if RF user security session is open\n" +
                            "10: Read always allowed / Write allowed if RF user security session is open\n" +
                            "11: Read always allowed / Write always forbidden",
                            0b1100));
        } else {
            // Permissions for RFA2SS, RFA3SS and RFA4SS:
            registerFields.add(new STRegisterField(
                    "RW_PROTECTION_A" + area,
                    "Area " + area + " RF access\n" +
                            "00: Read always allowed / Write always allowed\n" +
                            "01: Read always allowed / Write allowed if RF user security session is open\n" +
                            "10: Read allowed if RF user security session is open / Write allowed if RF user security session is open\n" +
                            "11: Read allowed if RF user security session is open / Write always forbidden",
                            0b1100));
        }

        registerFields.add(new STRegisterField("RFU","RFU", 0b11110000));

        createFieldHash(registerFields);
    }


    /////////// Getters - Setters of the decoded value //////////////
    // Those Getters and Setters are specific to this register

    /**
     *
     * @return The Password control Security status
     * @throws STException {@link}STException
     */
    public ST25DVSecurityStatusPWDControl getSSPWDControl() throws STException {
        ST25DVSecurityStatusPWDControl pwdControl;
        int fieldValue = getRegisterField("PWD_CTRL_A" + mAreaId).getValue();
        pwdControl = getSSPWDControl(fieldValue);
        return pwdControl;
    }


    /**
     *
     * @return The Read/Write protection Security status
     * @throws STException {@link}STException
     */
    public TagHelper.ReadWriteProtection getSSRWProtection() throws STException {
        TagHelper.ReadWriteProtection rwProtection;
        int fieldValue = getRegisterField("RW_PROTECTION_A" + mAreaId).getValue();
        rwProtection = getSSRWProtection(fieldValue);
        return rwProtection;
    }

    /**
     * Set the Password control Security status
     * @param pwdControl Pwd security status
     * @throws STException {@link}STException
     */
    public void setSSPWDControl(ST25DVSecurityStatusPWDControl pwdControl) throws STException {

        int fieldValue = computeSSPWDControlValue(pwdControl);
        getRegisterField("PWD_CTRL_A" + mAreaId).setValue(fieldValue);
    }


    /**
     * Set the Read/Write protection Security status
     * @param rwProtection rw security status
     * @throws STException {@link}STException
     */
    public void setSSReadWriteProtection(TagHelper.ReadWriteProtection rwProtection) throws STException {

        int fieldValue = computeSSReadWriteProtection(rwProtection);
        getRegisterField("RW_PROTECTION_A" + mAreaId).setValue(fieldValue);
    }


    /**
     * Function doing the conversion "decoded values" to "raw value"
     * @param pwdControl pwd control status
     * @return Raw Value
     */
    private int computeSSPWDControlValue(ST25DVSecurityStatusPWDControl pwdControl) throws STException {
        int fieldValue;

        // set the pwd control
        switch (pwdControl) {
            default:
            case NO_PWD_SELECTED:
                fieldValue = 0x00;
                break;
            case PROTECTED_BY_PWD1:
                fieldValue = 0x01;
                break;
            case PROTECTED_BY_PWD2:
                fieldValue = 0x02;
                break;
            case PROTECTED_BY_PWD3:
                fieldValue = 0x03;
                break;
        }

        return fieldValue;
    }

    /**
     * Function doing the conversion "decoded values" to "raw value"
     * @param rwProtection rw protection status
     * @return Raw Value
     */
    private int computeSSReadWriteProtection(TagHelper.ReadWriteProtection rwProtection) throws STException {
        int fieldValue = 0x00;

        // add the rw protection
        if(mAreaId == 1) {
            /*
             Possible values for RFA1SS:
             - READABLE_AND_WRITABLE
             - READABLE_AND_WRITE_PROTECTED_BY_PWD
             - READABLE_AND_WRITE_IMPOSSIBLE
             */
            switch (rwProtection) {
                case READABLE_AND_WRITABLE:
                    // Nothing to do
                    break;
                case READABLE_AND_WRITE_PROTECTED_BY_PWD:
                    fieldValue = (byte) 0x01;
                    break;
                case READABLE_AND_WRITE_IMPOSSIBLE:
                    fieldValue = (byte) 0x03;
                    break;
                default:
                    throw new STException(STException.STExceptionCode.BAD_PARAMETER);
            }
        } else {
            /*
                Possible values for RFA2SS, RFA3SS and RFA4SS:
                - READABLE_AND_WRITABLE
                - READABLE_AND_WRITE_PROTECTED_BY_PWD
                - READ_AND_WRITE_PROTECTED_BY_PWD
                - READ_PROTECTED_BY_PWD_AND_WRITE_IMPOSSIBLE
             */
            switch (rwProtection) {
                case READABLE_AND_WRITABLE:
                    // Nothing to do
                    break;
                case READABLE_AND_WRITE_PROTECTED_BY_PWD:
                    fieldValue = (byte) 0x01;
                    break;
                case READ_AND_WRITE_PROTECTED_BY_PWD:
                    fieldValue = (byte) 0x02;
                    break;
                case READ_PROTECTED_BY_PWD_AND_WRITE_IMPOSSIBLE:
                    fieldValue = (byte) 0x03;
                    break;
                default:
                    throw new STException(STException.STExceptionCode.BAD_PARAMETER);
            }
        }

        return fieldValue;
    }

    /**
     * Private function doing the conversion "field value" to "decoded value" for the field "PWDControl"
     *
     * @param fieldValue
     * @return PassWord Control status
     */
    private ST25DVSecurityStatusPWDControl getSSPWDControl(int fieldValue) {
        ST25DVSecurityStatusPWDControl pwdControl;
        switch (fieldValue) {
            default:
            case 0x00:
                pwdControl = ST25DVSecurityStatusPWDControl.NO_PWD_SELECTED;
                break;
            case 0x01:
                pwdControl = ST25DVSecurityStatusPWDControl.PROTECTED_BY_PWD1;
                break;
            case 0x02:
                pwdControl = ST25DVSecurityStatusPWDControl.PROTECTED_BY_PWD2;
                break;
            case 0x03:
                pwdControl = ST25DVSecurityStatusPWDControl.PROTECTED_BY_PWD3;
                break;

        }
        return pwdControl;
    }
    /**
     * Private function doing the conversion "field value" to "decoded value" for the field "RWProtection"
     *
     * @param fieldValue
     * @return Read Write protection status
     */
    private TagHelper.ReadWriteProtection getSSRWProtection(int fieldValue) {
        TagHelper.ReadWriteProtection rwProtection;

        // RFA1SS has some access rights that differ from RFA2SS, RFA3SS and RFA4SS because area1 should always be readable.
        if(mAreaId == 1) {
            /*
             Possible values for RFA1SS:
             - READABLE_AND_WRITABLE
             - READABLE_AND_WRITE_PROTECTED_BY_PWD
             - READABLE_AND_WRITE_IMPOSSIBLE
             */
            switch (fieldValue) {
                default:
                case 0x00:
                    rwProtection = TagHelper.ReadWriteProtection.READABLE_AND_WRITABLE;
                    break;
                case 0x01:
                case 0x02:
                    rwProtection = TagHelper.ReadWriteProtection.READABLE_AND_WRITE_PROTECTED_BY_PWD;
                    break;
                case 0x03:
                    rwProtection = TagHelper.ReadWriteProtection.READABLE_AND_WRITE_IMPOSSIBLE;
                    break;
            }
        } else {
            /*
            Possible values for RFA2SS, RFA3SS and RFA4SS:
            - READABLE_AND_WRITABLE
            - READABLE_AND_WRITE_PROTECTED_BY_PWD
            - READ_AND_WRITE_PROTECTED_BY_PWD
            - READ_PROTECTED_BY_PWD_AND_WRITE_IMPOSSIBLE
             */
            switch (fieldValue) {
                default:
                case 0x00:
                    rwProtection = TagHelper.ReadWriteProtection.READABLE_AND_WRITABLE;
                    break;
                case 0x01:
                    rwProtection = TagHelper.ReadWriteProtection.READABLE_AND_WRITE_PROTECTED_BY_PWD;
                    break;
                case 0x02:
                    rwProtection = TagHelper.ReadWriteProtection.READ_AND_WRITE_PROTECTED_BY_PWD;
                    break;
                case 0x03:
                    rwProtection = TagHelper.ReadWriteProtection.READ_PROTECTED_BY_PWD_AND_WRITE_IMPOSSIBLE;
                    break;
            }
        }

        return rwProtection;
    }
}

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
import static com.st.st25sdk.type5.ST25DVTag.REGISTER_GPO_ADDRESS;

import java.util.ArrayList;
import java.util.List;

import com.st.st25sdk.STException;
import com.st.st25sdk.STRegister;
import com.st.st25sdk.command.Iso15693CustomCommand;


/**
 * GPO register
 * Fields managed
 * "RF_USER_EN"
 * "RF_BUSY_EN",
 * "RF_INTERUPT_EN",
 * "FIELD_CHANGE_EN",
 * "RF_PUTMSG_EN",
 * "RF_GETMSG_EN",
 * "RF_WRITE_EN",
 * "GPO_EN",
 */
public class ST25DVRegisterGpo extends STRegister {
    public static final byte GPO_EN_BIT_MASK = (byte) 0x80;
    public static final byte RF_WRITE_EN_BIT_MASK = (byte) 0x40;
    public static final byte RF_GETMSG_BIT_MASK = (byte) 0x20;
    public static final byte RF_PUTMSG_BIT_MASK = (byte) 0x10;
    public static final byte FIELD_CHANGE_BIT_MASK = (byte) 0x08;
    public static final byte RF_INTERUPT_BIT_MASK = (byte) 0x04;
    public static final byte RF_BUSY_BIT_MASK = (byte) 0x02;
    public static final byte RF_USER_BIT_MASK = (byte) 0x01;

    /**
     * Fields of the GPO register
     */
    public enum ST25DVGPOControl {
        RF_USER_EN,
        RF_BUSY_EN,
        RF_INTERUPT_EN,
        FIELD_CHANGE_EN,
        RF_PUTMSG_EN,
        RF_GETMSG_EN,
        RF_WRITE_EN,
        GPO_EN
    }

    public static ST25DVRegisterGpo newInstance(Iso15693CustomCommand iso15693CustomCommand) {
        return new ST25DVRegisterGpo(
                iso15693CustomCommand,
                REGISTER_GPO_ADDRESS,
                "GPO",
                "Enable/disable ITs on GPO",
                REGISTER_READ_WRITE,
                REGISTER_DATA_ON_8_BITS);
    }

    public ST25DVRegisterGpo(Iso15693CustomCommand iso15693CustomCommand,
            byte registerId,
            String registerName,
            String registerContentDescription,
            RegisterAccessRights registerAccessRights,
            RegisterDataSize registerDataSize) {

        super(iso15693CustomCommand, registerId, registerName, registerContentDescription, registerAccessRights, registerDataSize);

        String disableEnable = "0: Disable\n1: Enable\n";

        List<STRegisterField> registerFields = new ArrayList<>();
        registerFields.add(new STRegisterField(
                ST25DVGPOControl.RF_USER_EN.toString(),
                "GPO output level is controlled by Manage GPO Command (set/reset)\n" + disableEnable,
                ST25DVRegisterGpo.RF_USER_BIT_MASK));

        registerFields.add(new STRegisterField(
                ST25DVGPOControl.RF_BUSY_EN.toString(),
                "GPO output level change from RF command SOF to response EOF\n" + disableEnable,
                ST25DVRegisterGpo.RF_BUSY_BIT_MASK));

        registerFields.add(new STRegisterField(
                ST25DVGPOControl.RF_INTERUPT_EN.toString(),
                "GPO output level is controlled by Manage GPO Command (pulse)\n" + disableEnable,
                ST25DVRegisterGpo.RF_INTERUPT_BIT_MASK));

        registerFields.add(new STRegisterField(
                ST25DVGPOControl.FIELD_CHANGE_EN.toString(),
                "A pulse is emitted on GPO, when RF field appears or disappears\n" + disableEnable,
                ST25DVRegisterGpo.FIELD_CHANGE_BIT_MASK));

        registerFields.add(new STRegisterField(
                ST25DVGPOControl.RF_PUTMSG_EN.toString(),
                "A pulse is emitted on GPO at completion of valid RF Write Message command\n" + disableEnable,
                ST25DVRegisterGpo.RF_PUTMSG_BIT_MASK));

        registerFields.add(new STRegisterField(
                ST25DVGPOControl.RF_GETMSG_EN.toString(),
                "A pulse is emitted on GPO at completion of valid RF Read Message command\n" + disableEnable,
                ST25DVRegisterGpo.RF_GETMSG_BIT_MASK));

        registerFields.add(new STRegisterField(
                ST25DVGPOControl.RF_WRITE_EN.toString(),
                "A pulse is emitted on GPO at completion of valid RF write operation in EEPROM\n" + disableEnable,
                ST25DVRegisterGpo.RF_WRITE_EN_BIT_MASK));

        registerFields.add(new STRegisterField(
                ST25DVGPOControl.GPO_EN.toString(),
                "0: GPO output is disabled. GPO is High-Z (CMOS), 0 (Open Drain)\n" +
                        "1: GPO output is enabled. GPO outputs enabled interrupts\n",
                        ST25DVRegisterGpo.GPO_EN_BIT_MASK));

        createFieldHash(registerFields);
    }


    /////////// Getters - Setters of the decoded value //////////////
    // Those Getters and Setters are specific to this register


    /**
     * Is GPO enabled
     * @param gpoControl GPO control field
     * @return true if GPO is enabled
     * @throws STException {@link}STException
     */
    public boolean isGPOFieldEnabled(ST25DVGPOControl gpoControl) throws STException {
        boolean gpoEnable;
        STRegisterField gpoField = getRegisterField(gpoControl.toString());
        gpoEnable = (gpoField.getValue() != 0);
        return gpoEnable;
    }


    /**
     * Update the register value taking into account value and new gpoControl setting
     * @param value initial value to keep into account if any
     * @param gpoControl the field to consider on GPO register
     * @param enable true to enable field
     * @throws STException {@link}STException
     */
    public void setGPO(byte value, ST25DVGPOControl gpoControl, boolean enable) throws STException {
        STRegisterField gpoField = getRegisterField(gpoControl.toString());

        // Convert value from byte to int
        int gpoValue = value & 0xFF;

        int registerValue = gpoField.computeRegisterValue(gpoValue, (enable? 1 : 0));
        setRegisterValue(registerValue);
    }

    /**
     * Update the register value according to gpoControl field
     * @param gpoControl the field to consider on GPO register
     * @param enable true to enable field
     * @throws STException {@link}STException
     */
    public void setGPO(ST25DVGPOControl gpoControl, boolean enable) throws STException {
        STRegisterField gpoField = getRegisterField(gpoControl.toString());
        gpoField.setValue(enable ? 1 : 0);
    }

}

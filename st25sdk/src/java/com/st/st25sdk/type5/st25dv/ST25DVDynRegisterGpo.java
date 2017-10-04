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
import static com.st.st25sdk.type5.ST25DVTag.REGISTER_DYN_GPO_ADDRESS;

import java.util.ArrayList;
import java.util.List;

import com.st.st25sdk.STDynamicRegister;
import com.st.st25sdk.STException;
import com.st.st25sdk.command.Iso15693CustomCommand;


/**
 * GPO Dyn register
 * Fields managed
 * "GPO_VAL bit0",
 * "GPO_IT  bit7",
 *
 */
public class ST25DVDynRegisterGpo extends STDynamicRegister {

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
    public enum ST25DVGPODynControl {
        RF_USER_EN,
        RF_BUSY_EN,
        RF_INTERUPT_EN,
        FIELD_CHANGE_EN,
        RF_PUTMSG_EN,
        RF_GETMSG_EN,
        RF_WRITE_EN,
        GPO_EN
    }

    public static ST25DVDynRegisterGpo newInstance(Iso15693CustomCommand iso15693CustomCommand) {
        return new ST25DVDynRegisterGpo(
                iso15693CustomCommand,
                REGISTER_DYN_GPO_ADDRESS,
                "GPO Control Dyn",
                "Dynamically enable/disable interrupts on GPO",
                REGISTER_READ_WRITE,
                REGISTER_DATA_ON_8_BITS);
    }



    public ST25DVDynRegisterGpo(Iso15693CustomCommand iso15693CustomCommand,
            byte registerId,
            String registerName,
            String registerContentDescription, RegisterAccessRights registerAccessRights, RegisterDataSize registerDataSize) {

        super(iso15693CustomCommand, registerId, registerName, registerContentDescription, registerAccessRights, registerDataSize);

        List<STRegisterField> registerFields = new ArrayList<>();
        registerFields.add(new STRegisterField(
                ST25DVGPODynControl.RF_USER_EN.toString(),
                "0: Disabled \n1: GPO output level is controlled by ManageGPO Command (set/reset)\n",
                ST25DVDynRegisterGpo.RF_USER_BIT_MASK));

        registerFields.add(new STRegisterField(
                ST25DVGPODynControl.RF_BUSY_EN.toString(),
                "GPO output level change from RF command SOF to response EOF\n" +
                        "0: Disable \n" +
                        "1: GPO output level changes from RF command SOF to response EOF\n",
                        ST25DVDynRegisterGpo.RF_BUSY_BIT_MASK));

        registerFields.add(new STRegisterField(
                ST25DVGPODynControl.RF_INTERUPT_EN.toString(),
                "0: Disabled\n1: GPO output level is controlled by Manage GPO Command (pulse)\n",
                ST25DVDynRegisterGpo.RF_INTERUPT_BIT_MASK));

        registerFields.add(new STRegisterField(
                ST25DVGPODynControl.FIELD_CHANGE_EN.toString(),
                "0: Disabled\n1: A pulse is emitted on GPO, when RF field appears or disappears\n",
                ST25DVDynRegisterGpo.FIELD_CHANGE_BIT_MASK));

        registerFields.add(new STRegisterField(
                ST25DVGPODynControl.RF_PUTMSG_EN.toString(),
                "0: Disabled\n1: A pulse is emitted on GPO at completion of valid RF Write Message command\n",
                ST25DVDynRegisterGpo.RF_PUTMSG_BIT_MASK));

        registerFields.add(new STRegisterField(
                ST25DVGPODynControl.RF_GETMSG_EN.toString(),
                "0: Disabled\n1: A pulse is emitted on GPO at completion of valid RF Read Message command\n",
                ST25DVDynRegisterGpo.RF_GETMSG_BIT_MASK));

        registerFields.add(new STRegisterField(
                ST25DVGPODynControl.RF_WRITE_EN.toString(),
                "0: Disabled\n1: A pulse is emitted on GPO at completion of valid RF write operation in EEPROM\n",
                ST25DVDynRegisterGpo.RF_WRITE_EN_BIT_MASK));

        registerFields.add(new STRegisterField(
                ST25DVGPODynControl.GPO_EN.toString(),
                "0: GPO output is disabled. GPO is High-Z (CMOS), 0 (Open Drain)\n" +
                        "1: GPO output is enabled. GPO outputs enabled interrupts\n",
                        ST25DVDynRegisterGpo.GPO_EN_BIT_MASK));

        createFieldHash(registerFields);
    }


    /////////// Getters //////////////
    /* the register is Read Only for every fields. no setter functions */

    /**
     * Is GPO Dynamic Register Field is  enabled
     * @param gpoFieldVal GPO Dyn control field
     * @return true if GPO Dyn field is enabled
     * @throws STException {@link}STException
     */
    public boolean isGPOFieldEnabled(ST25DVGPODynControl gpoFieldVal) throws STException {

        boolean status;
        STRegisterField registerField = getRegisterField(gpoFieldVal.toString());
        status = (registerField.getValue() != 0);
        return status;
    }
}

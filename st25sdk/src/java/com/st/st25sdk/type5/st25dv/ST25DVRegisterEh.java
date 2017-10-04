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
import static com.st.st25sdk.type5.ST25DVTag.REGISTER_EH_MODE_ADDRESS;

import java.util.ArrayList;
import java.util.List;

import com.st.st25sdk.STException;
import com.st.st25sdk.STRegister;
import com.st.st25sdk.command.Iso15693CustomCommand;


/**
 * EH static register
 * Fields managed
 * "EH_MODE bit0",
 * "RFU     bit7:1",
 *
 */
public class ST25DVRegisterEh extends STRegister {

    public static final byte EH_MODE_BIT_MASK  = (byte) 0x01;
    public static final byte RFU_BIT_MASK      = (byte) 0xFE;

    /**
     * Fields of the EH register
     */
    public enum ST25DVRegisterEhControl {
        EH_MODE,
        RFU
    }

    public static ST25DVRegisterEh newInstance(Iso15693CustomCommand iso15693CustomCommand) {
        return new ST25DVRegisterEh(
                iso15693CustomCommand,
                REGISTER_EH_MODE_ADDRESS,
                "EH_MODE",
                "Energy Harvesting default strategy after power on",
                REGISTER_READ_WRITE,
                REGISTER_DATA_ON_8_BITS);
    }

    public ST25DVRegisterEh(Iso15693CustomCommand iso15693CustomCommand,
            byte registerId,
            String registerName,
            String registerContentDescription,
            RegisterAccessRights registerAccessRights,
            RegisterDataSize registerDataSize) {

        super(iso15693CustomCommand, registerId, registerName, registerContentDescription, registerAccessRights, registerDataSize);

        List<STRegisterField> registerFields = new ArrayList<>();
        registerFields.add(new STRegisterField(
                ST25DVRegisterEhControl.EH_MODE.toString(),
                "0: EH forced after boot\n1: EH on demand only\n",
                EH_MODE_BIT_MASK));

        registerFields.add(new STRegisterField(
                ST25DVRegisterEhControl.RFU.toString(),
                "RFU\n",
                RFU_BIT_MASK));

        createFieldHash(registerFields);
    }


    /////////// Getters - Setters //////////////
    /* the register is Read / Write for every fields*/

    /**
     * Is EH static Register Field is  enabled
     * @param ehField EH control field
     * @return true if EH Ctrl field is enabled
     * @throws STException {@link}STException
     */
    public boolean isEHFieldEnabled(ST25DVRegisterEhControl ehField) throws STException {
        boolean status;
        STRegisterField registerField = getRegisterField(ehField.toString());
        status = (registerField.getValue() != 0);
        return status;
    }

    /**
     * Update the register value according to EH field
     * @param ehField the field to consider on EH register
     * @param enable  true to enable field
     * @throws STException {@link}STException
     */
    public void setEH(ST25DVRegisterEhControl ehField, boolean enable) throws STException {
        STRegisterField registerField = getRegisterField(ehField.toString());
        int val = enable ? 1 : 0;
        registerField.setValue(val);
    }

}

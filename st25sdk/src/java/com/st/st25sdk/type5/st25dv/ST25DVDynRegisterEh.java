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
import static com.st.st25sdk.type5.ST25DVTag.REGISTER_DYN_EH_CTRL_ADDRESS;

import java.util.ArrayList;
import java.util.List;

import com.st.st25sdk.STDynamicRegister;
import com.st.st25sdk.STException;
import com.st.st25sdk.command.Iso15693CustomCommand;


/**
 * ST25DVDynRegisterEh represents a class that aims to manage the Energy Harvesting Dynamic Register
 */
public class ST25DVDynRegisterEh extends STDynamicRegister {

    public static final byte EH_EN_BIT_MASK = (byte) 0b01;
    public static final byte EH_ON_BIT_MASK = (byte) 0b10;
    public static final byte FIELD_ON_BIT_MASK = (byte) 0b100;
    public static final byte VCC_ON_BIT_MASK = (byte) 0b1000;
    public static final byte RFU_ON_BIT_MASK = (byte) 0b11110000;

    public enum ST25DVEHControl {
        EH_EN,
        EH_ON,
        FIELD_ON,
        VCC_ON,
    }

    public static ST25DVDynRegisterEh newInstance(Iso15693CustomCommand iso15693CustomCommand) {

        return new ST25DVDynRegisterEh(
                iso15693CustomCommand,
                REGISTER_DYN_EH_CTRL_ADDRESS,
                "EH Control Dyn",
                "Energy Harvesting management and usage status",
                REGISTER_READ_WRITE,
                REGISTER_DATA_ON_8_BITS);
    }

    public ST25DVDynRegisterEh(Iso15693CustomCommand iso15693CustomCommand,
            byte registerId,
            String registerName,
            String registerContentDescription, RegisterAccessRights registerAccessRights, RegisterDataSize registerDataSize) {

        super(iso15693CustomCommand,registerId, registerName, registerContentDescription, registerAccessRights, registerDataSize);

        List<STRegisterField> registerFields = new ArrayList<>();
        registerFields.add(new STRegisterField(
                ST25DVEHControl.EH_EN.toString(),
                "0: (R/W) Disable Energy Harvesting\n" +
                        "1: (R/W) Enable Energy Harvesting\n",
                        ST25DVDynRegisterEh.EH_EN_BIT_MASK));

        registerFields.add(new STRegisterField(
                ST25DVEHControl.EH_ON.toString(),
                "0: (RO) Energy Harvesting state is inactive\n" +
                        "1: (RO) Energy Harvesting state is active\n",
                        ST25DVDynRegisterEh.EH_ON_BIT_MASK));

        registerFields.add(new STRegisterField(
                ST25DVEHControl.FIELD_ON.toString(),
                "0: (RO) RF state is inactive\n" +
                        "1: (RO) RF state is active\n",
                        ST25DVDynRegisterEh.FIELD_ON_BIT_MASK));

        registerFields.add(new STRegisterField(
                ST25DVEHControl.VCC_ON.toString(),
                "0: (RO) VCC state is inactive\n" +
                        "1: (RO) VCC state is active\n",
                        ST25DVDynRegisterEh.VCC_ON_BIT_MASK));

        registerFields.add(new STRegisterField("RFU","RFU", ST25DVDynRegisterEh.RFU_ON_BIT_MASK));

        createFieldHash(registerFields);
    }


    /////////// Getters - Setters of the decoded value //////////////
    // Those Getters and Setters are specific to this register

    /**
     * Is EH_ON enabled
     * @param ehControl EH control field
     * @return true if EH_ON is enabled
     * @throws STException {@link}STException
     */
    public boolean isEHFieldEnabled(ST25DVEHControl ehControl) throws STException {
        boolean ehEnable;
        STRegisterField ehField = getRegisterField(ehControl.toString());
        ehEnable = (ehField.getValue() != 0);
        return ehEnable;
    }


    /**
     * Update the register value according to ehControl field
     *  Only EH_EN could be written to set or reset the EH usage
     * @param ehControl the field to consider on EH register
     * @param enable true to enable field
     * @throws STException {@link}STException
     */
    public void setEH(ST25DVEHControl ehControl, boolean enable) throws STException {
        STRegisterField ehField = getRegisterField(ehControl.toString());
        ehField.setValue(enable ? 1 : 0);
    }
}

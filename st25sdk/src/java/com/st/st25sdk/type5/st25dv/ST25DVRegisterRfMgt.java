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
import static com.st.st25sdk.type5.ST25DVTag.REGISTER_RF_MNGT_ADDRESS;

import java.util.ArrayList;
import java.util.List;

import com.st.st25sdk.STException;
import com.st.st25sdk.STRegister;
import com.st.st25sdk.command.Iso15693CustomCommand;


/**
 * RF_MNGT static register
 * Fields managed
 * "RF_DISABLE  bit0",
 * "RF_SLEEP    bit1",
 * "RFU         bit7:2",
 *
 */
public class ST25DVRegisterRfMgt extends STRegister {

    public static final byte RF_DISABLE_BIT_MASK  = (byte) 0x01;
    public static final byte RF_SLEEP_BIT_MASK    = (byte) 0x02;
    public static final byte RFU_BIT_MASK         = (byte) 0xFC;

    /**
     * Fields of the RF_MNGT register
     */
    public enum ST25DVRegisterRFMngtControl {
        RF_DISABLE,
        RF_SLEEP,
        RFU
    }

    public static ST25DVRegisterRfMgt newInstance(Iso15693CustomCommand iso15693CustomCommand) {
        return new ST25DVRegisterRfMgt(
                iso15693CustomCommand,
                REGISTER_RF_MNGT_ADDRESS,
                "RF_MNGT",
                "RF interface state after power on",
                REGISTER_READ_WRITE,
                REGISTER_DATA_ON_8_BITS);
    }

    public ST25DVRegisterRfMgt(Iso15693CustomCommand iso15693CustomCommand,
            byte registerId,
            String registerName,
            String registerContentDescription,
            RegisterAccessRights registerAccessRights,
            RegisterDataSize registerDataSize) {

        super(iso15693CustomCommand, registerId, registerName, registerContentDescription, registerAccessRights, registerDataSize);

        List<STRegisterField> registerFields = new ArrayList<>();
        registerFields.add(new STRegisterField(
                ST25DVRegisterRFMngtControl.RF_DISABLE.toString(),
                "0: RF commands executed\n" +
                        "1: RF commands no executed (error 0Fh returned)\n",
                        RF_DISABLE_BIT_MASK));

        registerFields.add(new STRegisterField(
                ST25DVRegisterRFMngtControl.RF_SLEEP.toString(),
                "0: RF communication enabled\n" +
                        "1: RF communication disabled (ST25DV remains silent)\n",
                        RF_SLEEP_BIT_MASK));

        registerFields.add(new STRegisterField(
                ST25DVRegisterRFMngtControl.RFU.toString(),
                "RFU\n",
                RFU_BIT_MASK));

        createFieldHash(registerFields);
    }


    /////////// Getters - Setters //////////////
    /* the register is Read / Write for every fields*/

    /**
     * Is RF Mngt static Register Field is  enabled
     * @param fieldVal RF mngt field
     * @return true if RF mngt  field is enabled
     * @throws STException {@link}STException
     */
    public boolean isRFMngtFieldEnabled(ST25DVRegisterRFMngtControl fieldVal) throws STException {
        boolean status;
        STRegisterField rfMngtField = getRegisterField(fieldVal.toString());
        status = (rfMngtField.getValue() != 0);
        return status;
    }

    /**
     * Update the register value according to RF Mngt field
     * @param fieldVal the field to consider on RF Mngt register
     * @param enable  true to enable field
     * @throws STException {@link}STException
     */
    public void setRFMngt(ST25DVRegisterRFMngtControl fieldVal, boolean enable) throws STException {
        STRegisterField registerField = getRegisterField(fieldVal.toString());
        int val = enable ? 1 : 0;
        registerField.setValue(val);
    }

}

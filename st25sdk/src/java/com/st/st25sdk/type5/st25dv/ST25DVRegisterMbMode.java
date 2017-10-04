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
import static com.st.st25sdk.type5.ST25DVTag.REGISTER_MB_MODE_ADDRESS;

import java.util.ArrayList;
import java.util.List;

import com.st.st25sdk.STException;
import com.st.st25sdk.STRegister;
import com.st.st25sdk.command.Iso15693CustomCommand;


/**
 * MB_Mode static register
 * Fields managed
 * "RF_DISABLE  bit0",
 * "RF_SLEEP    bit1",
 * "RFU         bit7:2",
 *
 */
public class ST25DVRegisterMbMode extends STRegister {

    public static final byte MB_MODE_BIT_MASK  = (byte) 0x01;
    public static final byte RFU_BIT_MASK         = (byte) 0xFE;

    /**
     * Fields of the register
     */
    public enum ST25DVRegisterMBControl {
        MB_MODE,
        RFU
    }

    public static ST25DVRegisterMbMode newInstance(Iso15693CustomCommand iso15693CustomCommand) {
        return new ST25DVRegisterMbMode(
                iso15693CustomCommand,
                REGISTER_MB_MODE_ADDRESS,
                "MB_MODE",
                "Fast Transfer Mode state after power on",
                REGISTER_READ_WRITE,
                REGISTER_DATA_ON_8_BITS);
    }


    public ST25DVRegisterMbMode(Iso15693CustomCommand iso15693CustomCommand,
            byte registerId,
            String registerName,
            String registerContentDescription,
            RegisterAccessRights registerAccessRights,
            RegisterDataSize registerDataSize) {

        super(iso15693CustomCommand, registerId, registerName, registerContentDescription, registerAccessRights, registerDataSize);

        List<STRegisterField> registerFields = new ArrayList<>();
        registerFields.add(new STRegisterField(
                ST25DVRegisterMBControl.MB_MODE.toString(),
                "0: Enabling Fast Transfer Mode is forbidden\n" +
                        "1: Enabling Fast Transfer Mode is authorized. Fast Transfer Mode activation can be done through the Dynamic Register 'MB_CTRL_Dyn'\n",
                        MB_MODE_BIT_MASK));

        registerFields.add(new STRegisterField(
                ST25DVRegisterMBControl.RFU.toString(),
                "RFU\n",
                RFU_BIT_MASK));

        createFieldHash(registerFields);
    }


    /////////// Getters - Setters //////////////
    /* the register is Read / Write for every fields*/

    /**
     * Is MB static Register Field is enabled
     * @return true if MB_MODE  field is enabled ; false otherwise
     * @throws STException
     */
    public boolean isMBModeEnabled() throws STException {
        boolean status;
        STRegisterField mbField = getRegisterField(ST25DVRegisterMBControl.MB_MODE.toString());
        status = (mbField.getValue() != 0);
        return status;
    }

    /**
     * set the MB mode
     * @param enable  true to enable MB / false to disable MB
     * @throws STException
     */
    public void setMBMode(boolean enable) throws STException {
        STRegisterField registerField = getRegisterField(ST25DVRegisterMBControl.MB_MODE.toString());
        int val = enable ? 1 : 0;
        registerField.setValue(val);
    }

}

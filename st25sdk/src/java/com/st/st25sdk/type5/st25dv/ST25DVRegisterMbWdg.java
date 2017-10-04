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
import static com.st.st25sdk.type5.ST25DVTag.REGISTER_MB_WDG_ADDRESS;

import java.util.ArrayList;
import java.util.List;

import com.st.st25sdk.STException;
import com.st.st25sdk.STRegister;
import com.st.st25sdk.command.Iso15693CustomCommand;


/**
 * MB_WD static register
 * Fields managed
 * "MB_WDG  bit0:2",
 * "RFU     bit7:3",
 *
 */
public class ST25DVRegisterMbWdg extends STRegister {

    public static final byte MB_WDG_BIT_MASK  = (byte) 0x07;
    public static final byte RFU_BIT_MASK     = (byte) 0xF8;

    /**
     * Fields of the register
     */
    public enum ST25DVRegisterMbWdgControl {
        MB_WDG,
        RFU
    }

    public static ST25DVRegisterMbWdg newInstance(Iso15693CustomCommand iso15693CustomCommand) {
        return new ST25DVRegisterMbWdg(
                iso15693CustomCommand,
                REGISTER_MB_WDG_ADDRESS,
                "MB_WDG",
                "Mail box Watch dog duration = Maximum time before message is automatically released",
                REGISTER_READ_WRITE,
                REGISTER_DATA_ON_8_BITS);
    }

    public ST25DVRegisterMbWdg(Iso15693CustomCommand iso15693CustomCommand,
            byte registerId,
            String registerName,
            String registerContentDescription,
            RegisterAccessRights registerAccessRights,
            RegisterDataSize registerDataSize) {

        super(iso15693CustomCommand, registerId, registerName, registerContentDescription, registerAccessRights, registerDataSize);

        List<STRegisterField> registerFields = new ArrayList<>();
        registerFields.add(new STRegisterField(
                ST25DVRegisterMbWdgControl.MB_WDG.toString(),
                "Watch dog duration = MB_WDG x 30 ms +/- 6 %\n" +
                        "If MB_WDG = 0, then watchdog duration is infinite\n",
                        MB_WDG_BIT_MASK));

        registerFields.add(new STRegisterField(
                ST25DVRegisterMbWdgControl.RFU.toString(),
                "RFU\n",
                RFU_BIT_MASK));

        createFieldHash(registerFields);
    }


    /////////// Getters - Setters //////////////
    /* the register is Read / Write for every fields*/

    /**
     * get the MB WDG Value
     * @return a byte with WDG Value
     * @throws STException {@link}STException
     */
    public byte getMBWDG() throws STException {
        STRegisterField registerField = getRegisterField(ST25DVRegisterMbWdgControl.MB_WDG.toString());
        return (byte)registerField.getValue();
    }

}

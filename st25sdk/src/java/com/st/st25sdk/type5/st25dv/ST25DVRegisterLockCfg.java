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
import static com.st.st25sdk.type5.ST25DVTag.REGISTER_LOCK_CFG_ADDRESS;

import java.util.ArrayList;
import java.util.List;

import com.st.st25sdk.STException;
import com.st.st25sdk.STRegister;
import com.st.st25sdk.command.Iso15693CustomCommand;


/**
 * LOCK_CFG static register
 * Fields managed
 * "LCK_CFG  bit0",
 * "RFU      bit7:1",
 *
 */
public class ST25DVRegisterLockCfg extends STRegister {

    public static final byte LOCK_CFG_BIT_MASK = (byte) 0x01;
    public static final byte RFU_BIT_MASK      = (byte) 0xFE;

    /**
     * Fields of the register
     */
    public enum ST25DVRegisterLockCfgCtrl {
        LOCK_CFG,
        RFU
    }

    public static ST25DVRegisterLockCfg newInstance(Iso15693CustomCommand iso15693CustomCommand) {
        return new ST25DVRegisterLockCfg(
                iso15693CustomCommand,
                REGISTER_LOCK_CFG_ADDRESS,
                "LockCfg",
                "Disable System Configuration change by RF",
                REGISTER_READ_WRITE,
                REGISTER_DATA_ON_8_BITS);
    }

    public ST25DVRegisterLockCfg(Iso15693CustomCommand iso15693CustomCommand,
            byte registerId,
            String registerName,
            String registerContentDescription,
            RegisterAccessRights registerAccessRights,
            RegisterDataSize registerDataSize) {

        super(iso15693CustomCommand, registerId, registerName, registerContentDescription, registerAccessRights, registerDataSize);

        List<STRegisterField> registerFields = new ArrayList<>();
        registerFields.add(new STRegisterField(
                ST25DVRegisterLockCfgCtrl.LOCK_CFG.toString(),
                "0: Configuration is unlocked\n" +
                        "1: Configuration is locked\n",
                        LOCK_CFG_BIT_MASK));

        registerFields.add(new STRegisterField(
                ST25DVRegisterLockCfgCtrl.RFU.toString(),
                "RFU\n",
                RFU_BIT_MASK));

        createFieldHash(registerFields);
    }


    /////////// Getters - Setters //////////////
    /* the register is Read / Write for every fields*/

    /**
     * Is LCK_CFG  Field is enabled
     * @return true if LCK_CFG  field is enabled ; false otherwise
     * @throws STException {@link}STException
     */
    public boolean isLockCfgEnabled() throws STException {
        boolean status;
        STRegisterField lockCfgField = getRegisterField(ST25DVRegisterLockCfgCtrl.LOCK_CFG.toString());
        status = (lockCfgField.getValue() != 0);
        return status;
    }

    /**
     * set the LCK_CFG mode
     * @param enable  true to lock configuration / false to unlock configuration
     * @throws STException {@link}STException
     */
    public void setLockCfgMode(boolean enable) throws STException {
        STRegisterField registerField = getRegisterField(ST25DVRegisterLockCfgCtrl.LOCK_CFG.toString());
        int val = enable ? 1 : 0;
        registerField.setValue(val);
    }

}

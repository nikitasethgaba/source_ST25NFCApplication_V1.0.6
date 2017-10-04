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

package com.st.st25sdk.type5.st25tv;

import com.st.st25sdk.STException;
import com.st.st25sdk.STRegister;
import com.st.st25sdk.command.Iso15693CustomCommand;

import java.util.ArrayList;

import static com.st.st25sdk.STRegister.RegisterAccessRights.REGISTER_READ_WRITE;
import static com.st.st25sdk.STRegister.RegisterDataSize.REGISTER_DATA_ON_8_BITS;


public class ST25TVRegisterLockConfiguration extends STRegister {


    public static ST25TVRegisterLockConfiguration newInstance(Iso15693CustomCommand iso15693CustomCommand, byte registerAddress) {


        String registerName = "LockConfiguration";

        String registerContentDescription =
                "Bit [0] : Lock configuration\n" +
                        "              0b: Configuration registers can be written (after presenting the Configuration password)\n" +
                        "              1b: Configuration registers are permanently locked\n" +
                        "Bits [7:1] : RFU";

        return new ST25TVRegisterLockConfiguration(
                iso15693CustomCommand,
                registerAddress,
                registerName,
                registerContentDescription,
                REGISTER_READ_WRITE,
                REGISTER_DATA_ON_8_BITS);
    }

    public ST25TVRegisterLockConfiguration(Iso15693CustomCommand iso15693CustomCommand,
            byte registerAddress,
            String registerName,
            String registerContentDescription,
            RegisterAccessRights registerAccessRights,
            RegisterDataSize registerDataSize) {

        super(iso15693CustomCommand, registerAddress, registerName, registerContentDescription, registerAccessRights, registerDataSize);
        createFieldHash( new ArrayList<STRegisterField>() {
            {
                add(new STRegisterField("LOCK_CONFIGURATION","Configuration registers are permanently locked\n",0b00000001));
                add(new STRegisterField("RFU","RFU", 0b11111110));
            }
        });

    }

    /////////// Getters - Setters of the decoded value //////////////
    // Those Getters and Setters are specific to this register

    public boolean isConfigurationLocked() throws STException {
        boolean isConfigurationLocked;

        int fieldValue = getRegisterField("LOCK_CONFIGURATION").getValue();
        isConfigurationLocked = (fieldValue == 1);

        return isConfigurationLocked;
    }

    public void lockConfiguration() throws STException {
        int fieldValue = 1;
        getRegisterField("LOCK_CONFIGURATION").setValue(fieldValue);
    }

}

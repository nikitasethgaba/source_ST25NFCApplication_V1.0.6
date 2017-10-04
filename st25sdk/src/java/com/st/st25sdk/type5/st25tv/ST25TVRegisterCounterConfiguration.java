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
import static com.st.st25sdk.type5.ST25TVTag.ST25TV_REGISTER_WRITE_COUNTER_CONFIGURATION;


public class ST25TVRegisterCounterConfiguration extends STRegister {


    public static ST25TVRegisterCounterConfiguration newInstance(Iso15693CustomCommand iso15693CustomCommand) {

        String registerName = "CounterConfiguration";

        String registerContentDescription =
                "Bit [0] : Counter activity\n" +
                        "              0b: Counter is disabled\n" +
                        "              1b: Counter is enabled\n" +
                        "Bit [1] : Counter value reset\n" +
                        "              0b: don't care for counter\n" +
                        "              1b: Counter is reset\n" +
                        "Bits [7:2] : RFU";

        return new ST25TVRegisterCounterConfiguration(
                iso15693CustomCommand,
                ST25TV_REGISTER_WRITE_COUNTER_CONFIGURATION,
                registerName,
                registerContentDescription,
                REGISTER_READ_WRITE,
                REGISTER_DATA_ON_8_BITS);
    }

    public ST25TVRegisterCounterConfiguration(Iso15693CustomCommand iso15693CustomCommand,
            byte registerAddress,
            String registerName,
            String registerContentDescription,
            RegisterAccessRights registerAccessRights,
            RegisterDataSize registerDataSize) {

        super(iso15693CustomCommand, registerAddress, registerName, registerContentDescription, registerAccessRights, registerDataSize);
        createFieldHash( new ArrayList<STRegisterField>() {
            {
                add(new STRegisterField("COUNTER_ACTIVITY","Counter is enabled\n",0b00000001));
                add(new STRegisterField("CLEAR","Reset the counter\n",0b00000010));
                add(new STRegisterField("RFU","RFU", 0b11111100));
            }
        });

    }

    /////////// Getters - Setters of the decoded value //////////////
    // Those Getters and Setters are specific to this register

    public boolean isCounterEnabled() throws STException {
        boolean isCounterEnabled;

        int fieldValue = getRegisterField("COUNTER_ACTIVITY").getValue();
        isCounterEnabled = (fieldValue == 1);

        return isCounterEnabled;
    }

    public void setIsCounterEnabled(boolean isCounterEnabled) throws STException {

        int fieldValue = isCounterEnabled ? 1 : 0;
        getRegisterField("COUNTER_ACTIVITY").setValue(fieldValue);
    }

    public void resetCounter() throws STException {
        getRegisterField("CLEAR").setValue(1);
    }

}

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

import static com.st.st25sdk.STRegister.RegisterAccessRights.REGISTER_READ_ONLY;
import static com.st.st25sdk.STRegister.RegisterDataSize.REGISTER_DATA_ON_16_BITS;
import static com.st.st25sdk.type5.ST25TVTag.ST25TV_REGISTER_COUNTER_READ;


public class ST25TVRegisterCounterValue extends STRegister {


    public static ST25TVRegisterCounterValue newInstance(Iso15693CustomCommand iso15693CustomCommand) {

        String registerName = "CounterValue";

        String registerContentDescription = "Bit [15:0] : Counter value";

        return new ST25TVRegisterCounterValue(
                iso15693CustomCommand,
                ST25TV_REGISTER_COUNTER_READ,
                registerName,
                registerContentDescription,
                REGISTER_READ_ONLY,
                REGISTER_DATA_ON_16_BITS);
    }

    public ST25TVRegisterCounterValue(Iso15693CustomCommand iso15693CustomCommand,
            byte registerAddress,
            String registerName,
            String registerContentDescription,
            RegisterAccessRights registerAccessRights,
            RegisterDataSize registerDataSize) {

        super(iso15693CustomCommand, registerAddress, registerName, registerContentDescription, registerAccessRights, registerDataSize);

    }

    /////////// Getters - Setters of the decoded value //////////////
    // Those Getters and Setters are specific to this register

    public int getCounterValue() throws STException {

        return getRegisterValue();
    }

}

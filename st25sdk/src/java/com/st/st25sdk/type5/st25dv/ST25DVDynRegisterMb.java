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
import static com.st.st25sdk.type5.ST25DVTag.REGISTER_DYN_MB_CTRL_ADDRESS;

import java.util.ArrayList;
import java.util.List;

import com.st.st25sdk.STDynamicRegister;
import com.st.st25sdk.STException;
import com.st.st25sdk.command.Iso15693CustomCommand;


/**
 * ST25DVDynRegisterEh represents a class that aims to manage the Energy Harvesting Dynamic Register
 */
public class ST25DVDynRegisterMb extends STDynamicRegister {

    public static final byte MB_EN_BIT_MASK              = (byte) 0b01;
    public static final byte HOST_PUT_MSG_BIT_MASK       = (byte) 0b10;
    public static final byte RF_PUT_MSG_BIT_MASK         = (byte) 0b100;
    public static final byte HOST_MISS_MSG_BIT_MASK      = (byte) 0b10000;
    public static final byte RF_MISS_MSG_BIT_MASK        = (byte) 0b100000;
    public static final byte HOST_CURRENT_MSG_BIT_MASK   = (byte) 0b1000000;
    public static final byte RF_CURRENT_MSG_BIT_MASK     = (byte) 0b10000000;

    public enum ST25DVMBControl {
        MB_EN,
        HOST_PUT_MSG,
        RF_PUT_MSG,
        HOST_MISS_MSG,
        RF_MISS_MSG
    }

    public static ST25DVDynRegisterMb newInstance(Iso15693CustomCommand iso15693CustomCommand) {
        return new ST25DVDynRegisterMb(
                iso15693CustomCommand,
                REGISTER_DYN_MB_CTRL_ADDRESS,
                "MB Control Dyn",
                "Fast Transfer Mode control and status",
                REGISTER_READ_WRITE,
                REGISTER_DATA_ON_8_BITS);
    }

    public ST25DVDynRegisterMb(Iso15693CustomCommand iso15693CustomCommand,
            byte registerId,
            String registerName,
            String registerContentDescription, RegisterAccessRights registerAccessRights, RegisterDataSize registerDataSize) {

        super(iso15693CustomCommand,registerId, registerName, registerContentDescription, REGISTER_READ_WRITE, REGISTER_DATA_ON_8_BITS);

        List<STRegisterField> registerFields = new ArrayList<>();
        registerFields.add(new STDynamicRegisterField(
                "MB_EN",
                "0: (R/W) Disable Mail box\n1: (R/W) Enable Mail box\n",
                ST25DVDynRegisterMb.MB_EN_BIT_MASK));

        registerFields.add(new STDynamicRegisterField(
                "HOST_PUT_MSG",
                "0: (RO) Message put via serial interface is not set\n" +
                        "1: (RO) Message put via serial interface is set\n",
                        ST25DVDynRegisterMb.HOST_PUT_MSG_BIT_MASK));

        registerFields.add(new STDynamicRegisterField(
                "RF_PUT_MSG",
                "0: (RO) Message put via radio interface is not set\n" +
                        "1: (RO) Message put via radio interface is set\n",
                        ST25DVDynRegisterMb.RF_PUT_MSG_BIT_MASK));

        registerFields.add(new STDynamicRegisterField(
                "HOST_MISS_MSG",
                "0: (RO) Message read via serial interface\n" +
                        "1: (RO) Message not read via serial interface\n",
                        ST25DVDynRegisterMb.HOST_MISS_MSG_BIT_MASK));

        registerFields.add(new STDynamicRegisterField(
                "RF_MISS_MSG",
                "0: (RO) Message read via radio interface\n" +
                        "1: (RO) Message not read via radio interface\n",
                        ST25DVDynRegisterMb.RF_MISS_MSG_BIT_MASK));

        registerFields.add(new STDynamicRegisterField(
                "HOST_CURRENT_MSG",
                "0: No message or message not coming from I2C\n" +
                        "1: Current Message in FTM mailbox comes from I2\n",
                        ST25DVDynRegisterMb.HOST_CURRENT_MSG_BIT_MASK));

        registerFields.add(new STDynamicRegisterField(
                "RF_CURRENT_MSG",
                "0: No message or message not coming from RF\n" +
                        "1: Current Message in FTM mailbox comes from RF\n",
                        ST25DVDynRegisterMb.RF_CURRENT_MSG_BIT_MASK));

        createFieldHash(registerFields);
    }


    /////////// Getters - Setters of the decoded value //////////////
    // Those Getters and Setters are specific to this register

    /**
     * Is MB Field enabled
     * @param mbControl MB control field
     * @return true if MB field is enabled
     * @throws STException {@link}STException
     */
    public boolean isMBFieldEnabled(ST25DVMBControl mbControl) throws STException {
        return isMBFieldEnabled(mbControl,false);
    }

    /**
     * Is MB Field enabled
     * @param mbControl MB control field
     * @param useFastCommand Select the fast command to access the dynamic register
     * @return true if MB field is enabled
     * @throws STException {@link}STException
     */
    public boolean isMBFieldEnabled(ST25DVMBControl mbControl, boolean useFastCommand) throws STException {
        boolean ehEnable;
        STDynamicRegisterField mbField = getDynRegisterField(mbControl.toString());
        ehEnable = (mbField.getValue(useFastCommand) != 0);
        return ehEnable;
    }


    /**
     * Update the register value according to mbControl field.
     *  Only MB_EN could be written to set or reset the MailBox usage
     * @param mbControl the field to consider on MB CTRL Dyn register
     * @param enable true to enable field
     * @throws STException {@link}STException
     */
    public void setMB(ST25DVMBControl mbControl, boolean enable) throws STException {
        setMB(mbControl,enable, false);
    }

    /**
     * Update the register value according to mbControl field.
     *  Only MB_EN could be written to set or reset the MailBox usage
     * @param mbControl the field to consider on MB CTRL Dyn register
     * @param enable true to enable field
     * @param useFastCommand Select the fast command to access the dynamic register
     * @throws STException {@link}STException
     */
    public void setMB(ST25DVMBControl mbControl, boolean enable, boolean useFastCommand) throws STException {
        STDynamicRegisterField mbField = getDynRegisterField(mbControl.toString());
        mbField.setValue(enable ? 1 : 0, useFastCommand);
    }
}

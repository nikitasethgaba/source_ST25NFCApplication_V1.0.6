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
import static com.st.st25sdk.type5.ST25DVTag.REGISTER_ENDA1_ADDRESS;
import static com.st.st25sdk.type5.ST25DVTag.REGISTER_ENDA2_ADDRESS;
import static com.st.st25sdk.type5.ST25DVTag.REGISTER_ENDA3_ADDRESS;

import java.util.ArrayList;
import java.util.List;

import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.STRegister;
import com.st.st25sdk.command.Iso15693CustomCommand;


/**
 * ST25DVRegisterEndAi represents a class that aims to manage the EndAi of DV registers
 * Default setting: End 1 = FFh; End2 = FFh; End3 = FFh
 * End 1 = Max Endi = FFh, only one area available
 * Area 1: 0000 h to [( End1+1)*32 -1] in byte (0 to 8191) bytes
 */
public class ST25DVRegisterEndAi extends STRegister {

    public static final byte END_AI_BIT_MASK = (byte) 0xFF;


    public static ST25DVRegisterEndAi newInstance(Iso15693CustomCommand iso15693CustomCommand, int index) {

        String registerName = "EndA" + index;
        byte registerAddress = REGISTER_ENDA1_ADDRESS;
        String registerContentDescription = "End of Area " + index;

        switch(index) {
            case 1:
                registerAddress = REGISTER_ENDA1_ADDRESS;
                break;
            case 2:
                registerAddress = REGISTER_ENDA2_ADDRESS;
                break;
            case 3:
                registerAddress = REGISTER_ENDA3_ADDRESS;
                break;
            default:
                STLog.e("Wrong register index - Available index [1-3]");
                //throw new STException(BAD_PARAMETER);
        }

        return new ST25DVRegisterEndAi(
                iso15693CustomCommand,
                index,
                registerAddress,
                registerName,
                registerContentDescription,
                REGISTER_READ_WRITE,
                REGISTER_DATA_ON_8_BITS);
    }

    public ST25DVRegisterEndAi(Iso15693CustomCommand iso15693CustomCommand,
            int areaId,
            byte registerId,
            String registerName,
            String registerContentDescription,
            RegisterAccessRights registerAccessRights,
            RegisterDataSize registerDataSize) {

        super(iso15693CustomCommand,registerId, registerName, registerContentDescription, registerAccessRights, registerDataSize);
        final String area = String.valueOf(areaId);

        List<STRegisterField> registerFields = new ArrayList<>();
        registerFields.add(new STRegisterField(
                "ENDA" + area,
                "End Area " + area + " = 8*ENDA" + area + " +7 when expressed in blocks (RF)\n" +
                        "End Area " + area +  " = 32*ENDA" + area + " +31 when expressed in bytes (I2C)\n",
                        END_AI_BIT_MASK));

        createFieldHash(registerFields);
    }


    /////////// Getters - Setters of the decoded value //////////////
    // Those Getters and Setters are specific to this register

    /**
     *
     * @return Return the value corresponding to the end of the Area as register raw value
     * This returned value is not formatted - raw value
     * @throws STException {@link}STException
     */
    public byte getEndArea() throws STException {
        int regValue = getRegisterValue();
        return (byte) (regValue);
    }

    /**
     *
     * @return Return the decoded value corresponding to the end of the Area
     * This returned value is in blocks
     * @throws STException {@link}STException
     */
    public int getEndAreaInBlock() throws STException {
        int endArea = getEndArea() & 0xFF;
        endArea = endArea * 8 + 7;
        return endArea;
    }

}

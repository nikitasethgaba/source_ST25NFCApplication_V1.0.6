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
import static com.st.st25sdk.type5.ST25DVTag.REGISTER_IT_TIME_ADDRESS;

import java.util.ArrayList;
import java.util.List;

import com.st.st25sdk.STException;
import com.st.st25sdk.STRegister;
import com.st.st25sdk.command.Iso15693CustomCommand;


/**
 * ITime register
 */
public class ST25DVRegisterITTime extends STRegister {

    public static final byte IT_TIME_BIT_MASK = (byte) 0b0111;
    public static final byte RFU_BIT_MASK = (byte) 0b11111000;

    /**
     * Field of the ITime register
     */
    public enum ST25DVITTimeControl {
        ITIME_000, // "301.18us"
        ITIME_001, // "263.53us"
        ITIME_010, // "225.88us"
        ITIME_011, // "188.24us"
        ITIME_100, // "150.59us"
        ITIME_101, // "112.94u"
        ITIME_110, // "75.25us"
        ITIME_111, // "37.65us"
    }

    public static ST25DVRegisterITTime newInstance(Iso15693CustomCommand iso15693CustomCommand) {
        return new ST25DVRegisterITTime(
                iso15693CustomCommand,
                REGISTER_IT_TIME_ADDRESS,
                "IT_Time",
                "Interrupt pulse duration",
                REGISTER_READ_WRITE,
                REGISTER_DATA_ON_8_BITS);
    }

    public ST25DVRegisterITTime(Iso15693CustomCommand iso15693CustomCommand,
            byte registerId,
            String registerName,
            String registerContentDescription,
            RegisterAccessRights registerAccessRights,
            RegisterDataSize registerDataSize) {

        super(iso15693CustomCommand, registerId, registerName, registerContentDescription, registerAccessRights, registerDataSize);

        List<STRegisterField> registerFields = new ArrayList<>();
        registerFields.add(new STRegisterField(
                "IT_TIME",
                "Pulse duration = 301us - IT_TIME x 37.65us +/- 2us\n" +
                        "000: Typical 301.18us duration\n" +
                        "001: Typical 263.53us duration\n" +
                        "010: Typical 225.88us duration\n" +
                        "011: Typical 188.24us duration\n" +
                        "100: Typical 150.59us duration\n" +
                        "101: Typical 112.94us duration\n" +
                        "110: Typical 75.25us duration\n" +
                        "111: Typical 37.65us duration\n",
                        IT_TIME_BIT_MASK));

        registerFields.add(new STRegisterField(
                "RFU",
                "RFU",
                RFU_BIT_MASK));

        createFieldHash(registerFields);
    }


    /////////// Getters - Setters of the decoded value //////////////
    // Those Getters and Setters are specific to this register


    /**
     * Is ITimeControlField enable
     *
     * @param iTTimeControl ITime control field
     * @return true if field enable
     * @throws STException {@link}STException
     */
    public boolean isITTimeControlFieldEnabled(ST25DVITTimeControl iTTimeControl) throws STException {
        boolean iTimeEnable;
        int fieldValue = getRegisterField("IT_TIME").getValue();
        iTimeEnable = isEnabled(fieldValue, iTTimeControl);
        return iTimeEnable;
    }


    /**
     * Set the Interuption Time register value corresponding to the iTTimeControl field
     * @param iTTimeControl the field to consider on ITime register
     * @throws STException {@link}STException
     */
    public void setITTimeControl(ST25DVITTimeControl iTTimeControl) throws STException {
        int fieldValue = computeRawValue(iTTimeControl);
        getRegisterField("IT_TIME").setValue(fieldValue);
    }


    /**
     * Function doing the conversion "decoded values" to "raw value"
     *
     * @param iTTimeControl
     * @return register raw value
     */
    private int computeRawValue(ST25DVITTimeControl iTTimeControl) {
        int computeRaw;
        switch (iTTimeControl) {
            default:
            case ITIME_000:
                computeRaw = 0;
                break;
            case ITIME_001:
                computeRaw = 1;
                break;
            case ITIME_010:
                computeRaw = 2;
                break;
            case ITIME_011:
                computeRaw = 3;
                break;
            case ITIME_100:
                computeRaw = 4;
                break;
            case ITIME_101:
                computeRaw = 5;
                break;
            case ITIME_110:
                computeRaw = 6;
                break;
            case ITIME_111:
                computeRaw = 7;
                break;
        }
        return computeRaw;
    }


    /**
     * Private function doing the conversion "raw value" to "decoded value" for the field "ITime"
     *
     * @param fieldValue
     * @param iTTimeControl
     * @return
     */
    private boolean isEnabled(int fieldValue, ST25DVITTimeControl iTTimeControl) {
        boolean ret;

        switch (iTTimeControl) {
            default:
            case ITIME_000:
                ret = fieldValue == 0;
                break;
            case ITIME_001:
                ret = fieldValue == 1;
                break;
            case ITIME_010:
                ret = fieldValue == 2;
                break;
            case ITIME_011:
                ret = fieldValue == 3;
                break;
            case ITIME_100:
                ret = fieldValue == 4;
                break;
            case ITIME_101:
                ret = fieldValue == 5;
                break;
            case ITIME_110:
                ret = fieldValue == 6;
                break;
            case ITIME_111:
                ret = fieldValue == 7;
                break;
        }
        return ret;
    }
}

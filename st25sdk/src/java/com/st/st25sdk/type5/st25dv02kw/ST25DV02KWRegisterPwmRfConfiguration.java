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

package com.st.st25sdk.type5.st25dv02kw;

import static com.st.st25sdk.STRegister.RegisterAccessRights.REGISTER_READ_WRITE;
import static com.st.st25sdk.STRegister.RegisterDataSize.REGISTER_DATA_ON_8_BITS;
import static com.st.st25sdk.type5.st25dv02kw.ST25DV02KWRegisterPwmRfConfiguration.DualityManagement.FULL_DUPLEX;
import static com.st.st25sdk.type5.st25dv02kw.ST25DV02KWRegisterPwmRfConfiguration.DualityManagement.PWM_FREQ_REDUCED;
import static com.st.st25sdk.type5.st25dv02kw.ST25DV02KWRegisterPwmRfConfiguration.DualityManagement.PWM_FREQ_REDUCED_AND_ONE_QUARTER_FULL_POWER_WHILE_RF_CMD;
import static com.st.st25sdk.type5.st25dv02kw.ST25DV02KWRegisterPwmRfConfiguration.DualityManagement.PWM_IN_HZ_WHILE_RF_CMD;
import static com.st.st25sdk.type5.st25dv02kw.ST25DV02KWRegisterPwmRfConfiguration.DualityManagement.PWM_ONE_QUARTER_FULL_POWER_WHILE_RF_CMD;
import static com.st.st25sdk.type5.st25dv02kw.ST25DV02KWRegisterPwmRfConfiguration.DualityManagement.UNKNOWN_MGT;
import static com.st.st25sdk.type5.st25dv02kw.ST25DV02KWRegisterPwmRfConfiguration.OutputDriverTrimming.FULL_POWER;
import static com.st.st25sdk.type5.st25dv02kw.ST25DV02KWRegisterPwmRfConfiguration.OutputDriverTrimming.HALF_FULL_POWER;
import static com.st.st25sdk.type5.st25dv02kw.ST25DV02KWRegisterPwmRfConfiguration.OutputDriverTrimming.ONE_QUARTER_FULL_POWER;
import static com.st.st25sdk.type5.st25dv02kw.ST25DV02KWRegisterPwmRfConfiguration.OutputDriverTrimming.THREE_QUARTER_FULL_POWER;
import static com.st.st25sdk.type5.st25dv02kw.ST25DV02KWRegisterPwmRfConfiguration.OutputDriverTrimming.UNKNOWN;

import java.util.ArrayList;

import com.st.st25sdk.STException;
import com.st.st25sdk.STRegister;
import com.st.st25sdk.command.Iso15693CustomCommand;


public class ST25DV02KWRegisterPwmRfConfiguration extends STRegister {

    public enum OutputDriverTrimming {
        FULL_POWER,
        ONE_QUARTER_FULL_POWER,
        HALF_FULL_POWER,
        THREE_QUARTER_FULL_POWER,
        UNKNOWN
    }

    public enum DualityManagement {
        FULL_DUPLEX,
        PWM_IN_HZ_WHILE_RF_CMD,
        PWM_ONE_QUARTER_FULL_POWER_WHILE_RF_CMD,
        PWM_FREQ_REDUCED,
        PWM_FREQ_REDUCED_AND_ONE_QUARTER_FULL_POWER_WHILE_RF_CMD,
        UNKNOWN_MGT
    }

    public enum PwmDrive {
        PWM1_DRIVE,
        PWM2_DRIVE
    }

    public static ST25DV02KWRegisterPwmRfConfiguration newInstance(Iso15693CustomCommand iso15693CustomCommand, byte registerAddress) {


        String registerName = "PwmRFConfigRegister";

        String registerContentDescription =
                "Bits [1:0] : Pwm1 output driver trimming\n" +
                        "Bits [3:2] : Pwm2 output driver trimming\n" +
                        "Bits [6:4] : Pwm vs Rf coexistence management\n" +
                        "Bits [7] : RFU";

        return new ST25DV02KWRegisterPwmRfConfiguration(
                iso15693CustomCommand,
                registerAddress,
                registerName,
                registerContentDescription,
                REGISTER_READ_WRITE,
                REGISTER_DATA_ON_8_BITS);
    }

    public ST25DV02KWRegisterPwmRfConfiguration(Iso15693CustomCommand iso15693CustomCommand,
            byte registerAddress,
            String registerName,
            String registerContentDescription,
            RegisterAccessRights registerAccessRights,
            RegisterDataSize registerDataSize) {

        super(iso15693CustomCommand, registerAddress, registerName, registerContentDescription, registerAccessRights, registerDataSize);
        createFieldHash( new ArrayList<STRegisterField>() {
            {
                add(new STRegisterField(PwmDrive.PWM1_DRIVE.toString(),"Pwm1 output driver trimming\n",0b00000011));
                add(new STRegisterField(PwmDrive.PWM2_DRIVE.toString(),"Pwm2 output driver trimming\n",0b00001100));
                add(new STRegisterField("DUALITY_MGT","Pwm vs Rf coexistence management\n",0b01110000));
                add(new STRegisterField("RFU","RFU",0b10000000));
            }
        });
    }

    /////////// Getters - Setters of the decoded value //////////////
    // Those Getters and Setters are specific to this register


    public void setOutputDriverTrimming(PwmDrive pwmDrive, OutputDriverTrimming value ) throws STException {
        int fieldValue = getPwmRawTrimmingValue(value);
        getRegisterField(pwmDrive.toString()).setValue(fieldValue);
    }

    public OutputDriverTrimming getOutputDriverTrimming(PwmDrive pwmDrive) throws STException {
        int fieldValue =  getRegisterField(pwmDrive.toString()).getValue();
        return getPwmTrimmingValue(fieldValue);
    }

    private OutputDriverTrimming getPwmTrimmingValue(int value) {
        OutputDriverTrimming outputDriverTrimming;

        switch(value) {
            case 0x0:
                outputDriverTrimming = FULL_POWER;
                break;
            case 0x1:
                outputDriverTrimming = THREE_QUARTER_FULL_POWER;
                break;
            case 0x2:
                outputDriverTrimming = HALF_FULL_POWER;
                break;
            case 0x3:
                outputDriverTrimming = ONE_QUARTER_FULL_POWER;
                break;
            default:
                outputDriverTrimming = UNKNOWN;
                break;
        }

        return outputDriverTrimming;
    }

    private int getPwmRawTrimmingValue(OutputDriverTrimming value) {
        switch(value) {
            case FULL_POWER:
                return 0;
            case THREE_QUARTER_FULL_POWER:
                return 1;
            case HALF_FULL_POWER:
                return 2;
            case ONE_QUARTER_FULL_POWER:
                return 3;
            default:
                return 0;
        }
    }


    public void setDualityManagement(DualityManagement value ) throws STException {
        int fieldValue = getRawDualityManagementFromValue(value);
        getRegisterField("DUALITY_MGT").setValue(fieldValue);
    }

    public DualityManagement getDualityManagement() throws STException {
        int fieldValue =  getRegisterField("DUALITY_MGT").getValue();
        return getDualityManagementFromValue(fieldValue);
    }

    private DualityManagement getDualityManagementFromValue(int value) {
        if (value == 0x03)
            return PWM_FREQ_REDUCED_AND_ONE_QUARTER_FULL_POWER_WHILE_RF_CMD;
        else if (value == 0x04)
            return PWM_IN_HZ_WHILE_RF_CMD;
        else if (value == 0x02)
            return PWM_ONE_QUARTER_FULL_POWER_WHILE_RF_CMD;
        else if (value == 0x01)
            return PWM_FREQ_REDUCED;
        else if (value == 0x00)
            return FULL_DUPLEX;

        return UNKNOWN_MGT;
    }

    private int getRawDualityManagementFromValue(DualityManagement value) {
        switch(value) {
            case FULL_DUPLEX:
                return 0;
            case PWM_IN_HZ_WHILE_RF_CMD:
                return 4;
            case PWM_ONE_QUARTER_FULL_POWER_WHILE_RF_CMD:
                return 2;
            case PWM_FREQ_REDUCED:
                return 1;
            case PWM_FREQ_REDUCED_AND_ONE_QUARTER_FULL_POWER_WHILE_RF_CMD:
                return 3;
            default:
                return 0;
        }
    }

}

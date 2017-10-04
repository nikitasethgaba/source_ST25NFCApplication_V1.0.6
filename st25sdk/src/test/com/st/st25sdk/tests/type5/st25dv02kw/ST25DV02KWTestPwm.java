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

package com.st.st25sdk.tests.type5.st25dv02kw;

import static com.st.st25sdk.type5.ST25DV02KWTag.ST25DV02KW_CONFIGURATION_PASSWORD_ID;
import static com.st.st25sdk.type5.ST25DV02KWTag.ST25DV02KW_PWM_PASSWORD_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.TagHelper;
import com.st.st25sdk.type5.ST25DV02KWTag;
import com.st.st25sdk.type5.st25dv02kw.ST25DV02KWRegisterPwmRfConfiguration;


public class ST25DV02KWTestPwm {

    static ST25DV02KWTag mTag;

    static public void run(ST25DV02KWTag st25DV02KWTag) throws STException {

        mTag = st25DV02KWTag;

        //////////////////////////////////////////////////////////////////
        // !!! Assumption is default pwd = {0x00, 0x00, 0x00, 0x00 }

        STLog.i("Test Pwm Password commands");
        testPwmPasswordCommands();

        //////////////////////////////////////////////////////////////////
        // !!! Assumption is default pwd = {0x00, 0x00, 0x00, 0x00 }
        STLog.i("Test Pwm configs");
        testPwmConfigs();


        //////////////////////////////////////////////////////////////////
        STLog.i("Test End");

    }



    /**
     * @throws STException
     */
    static private void testPwmPasswordCommands() throws STException {
        byte[] initialPassword = new byte[]{0x00, 0x00, 0x00, 0x00};
        //byte[] testPassword = new byte[]{(byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef};
        byte[] testPassword = new byte[]{0x00, 0x00, 0x00, 0x01};

        mTag.presentPassword(ST25DV02KW_PWM_PASSWORD_ID, initialPassword);
        mTag.writePassword(ST25DV02KW_PWM_PASSWORD_ID, testPassword);
        mTag.writePassword(ST25DV02KW_PWM_PASSWORD_ID, initialPassword);
        TagHelper.ReadWriteProtection accessRight = mTag.getPwmCtrlAccessRights();

        if (accessRight != TagHelper.ReadWriteProtection.READABLE_AND_WRITABLE)
            fail("Should be re-init to be readable and writable");

        mTag.presentPassword(ST25DV02KW_CONFIGURATION_PASSWORD_ID, initialPassword);
        //mTag.presentPassword(ST25DV02KW_CONFIGURATION_PASSWORD_ID, testPassword );
        mTag.setPwmCtrlAccessRights(TagHelper.ReadWriteProtection.READABLE_AND_WRITE_PROTECTED_BY_PWD);

        try {
            mTag.writePwm1Control(22000, 50, true);
        }
        catch (STException e) {
            if (!e.getError().equals(STException.STExceptionCode.ISO15693_BLOCK_IS_LOCKED))
                fail("Write should failed with exception block is locked ");
            mTag.presentPassword(ST25DV02KW_PWM_PASSWORD_ID, initialPassword);
            mTag.writePwm1Control(22000, 50, true);

            ST25DV02KWRegisterPwmRfConfiguration.OutputDriverTrimming pwm1Value = mTag.getPwm1OutputDriverTrimming();
            try {
                mTag.setPwm1OutputDriverTrimming(pwm1Value);
            }
            catch (STException f) {
                if (!f.getError().equals(STException.STExceptionCode.CONFIG_PASSWORD_NEEDED))
                    fail("Wrong exception");

                mTag.presentPassword(ST25DV02KW_CONFIGURATION_PASSWORD_ID, initialPassword);
                //mTag.presentPassword(ST25DV02KW_CONFIGURATION_PASSWORD_ID, testPassword );
                mTag.setPwmCtrlAccessRights(TagHelper.ReadWriteProtection.READABLE_AND_WRITABLE);



                ST25DV02KWRegisterPwmRfConfiguration.OutputDriverTrimming trimmingValue = mTag.getPwm1OutputDriverTrimming();
                mTag.setPwm1OutputDriverTrimming(trimmingValue );
                trimmingValue  = mTag.getPwm2OutputDriverTrimming();
                mTag.setPwm2OutputDriverTrimming(trimmingValue);

                ST25DV02KWRegisterPwmRfConfiguration.DualityManagement dualityMgtValue = mTag.getDualityManagement();
                mTag.setDualityManagement(dualityMgtValue);


                mTag.presentPassword(ST25DV02KW_CONFIGURATION_PASSWORD_ID, initialPassword);
                //mTag.presentPassword(ST25DV02KW_CONFIGURATION_PASSWORD_ID, testPassword );
                mTag.writePassword(ST25DV02KW_CONFIGURATION_PASSWORD_ID, /*initialPassword*/ testPassword);
                //mTag.presentPassword(ST25DV02KW_CONFIGURATION_PASSWORD_ID, initialPassword);
                mTag.presentPassword(ST25DV02KW_CONFIGURATION_PASSWORD_ID, testPassword );

                trimmingValue = mTag.getPwm1OutputDriverTrimming();
                mTag.setPwm1OutputDriverTrimming(trimmingValue);

                try {
                    //mTag.presentPassword(ST25DV02KW_CONFIGURATION_PASSWORD_ID, testPassword );
                    mTag.presentPassword(ST25DV02KW_CONFIGURATION_PASSWORD_ID, initialPassword);

                }
                catch (STException g) {
                    if (!g.getError().equals(STException.STExceptionCode.CMD_FAILED)) {
                        fail("Exception different from password needed");
                    }
                    try {
                        mTag.setPwmCtrlAccessRights(TagHelper.ReadWriteProtection.READABLE_AND_WRITABLE);
                    }
                    catch (STException h) {
                        if (!h.getError().equals((STException.STExceptionCode.CONFIG_PASSWORD_NEEDED))) {
                            mTag.presentPassword(ST25DV02KW_CONFIGURATION_PASSWORD_ID, testPassword);
                            mTag.writePassword(ST25DV02KW_CONFIGURATION_PASSWORD_ID, initialPassword);
                            fail("Wrong exception");
                        }
                    }
                    mTag.presentPassword(ST25DV02KW_CONFIGURATION_PASSWORD_ID, testPassword);
                    mTag.writePassword(ST25DV02KW_CONFIGURATION_PASSWORD_ID, initialPassword);
                    return;
                }
                fail("Should have an exception cmd failed as config password is not right");
            }
            fail("Should have an exception as config password not presented");
        }
        fail("Should have an exception as pwm password not presented");
    }

    /**
     * @throws STException
     */
    static private void testPwmConfigs() throws STException {

        byte[] testPassword = new byte[]{0x00, 0x00, 0x00, 0x00};

        mTag.writePwm1Control(22000, 50, true);
        mTag.invalidateCache();
        byte[] pwmControl = mTag.readPwm1Control();
        int dutyCycle = mTag.computeDutyCycleFromControl(pwmControl);
        assertEquals(dutyCycle, 50);
        int freq = mTag.computeFreqFromControl(pwmControl);
        assertEquals(freq, 22008);

        try {
            mTag.writePwm2Control(33000, 50, true);
        }
        catch (STException e) {
            if (!e.getError().equals(STException.STExceptionCode.BAD_PARAMETER)) {
                fail("Exception difference from bad parameter");
            }

            try {
                mTag.writePwm2Control(22000, 150, true);
            }
            catch (STException f) {
                if (!f.getError().equals(STException.STExceptionCode.BAD_PARAMETER)) {
                    fail("Exception difference from bad parameter");
                }
                return;
            }
            fail("Exception should happen with bad parameter");
        }
        fail("Exception should happen with bad parameter");
    }

}

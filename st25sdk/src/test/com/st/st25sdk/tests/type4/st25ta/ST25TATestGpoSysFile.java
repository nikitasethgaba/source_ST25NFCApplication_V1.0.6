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

package com.st.st25sdk.tests.type4.st25ta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import org.junit.Assert;
import org.junit.Assume;

import com.st.st25sdk.STException;
import com.st.st25sdk.type4a.STType4GpoInterface;
import com.st.st25sdk.type4a.STType4GpoInterface.GpoMode;
import com.st.st25sdk.type4a.st25ta.ST25TATag;


public class ST25TATestGpoSysFile {

    static public void run(ST25TATag st25TATag) throws STException {

        // This test can only be run on tags implementing STType4GpoInterface
        assumeTrue(st25TATag instanceof STType4GpoInterface);

        STType4GpoInterface gpoInterface = (STType4GpoInterface) st25TATag;

        //////////////////////////////////////////////////////////////////

        boolean isGpoLocked = false;
        GpoMode gpoModeValue = null;

        // Set this to true if you really want to lock the Event Counter configuration
        // Warning it will not be possible to unlock Event Counter configuration anymore!
        boolean lockGpoForReal = false;

        //Check Gpo is not locked
        isGpoLocked = gpoInterface.isGpoLocked();

        // Current implementation of gpo tests, assumes that the gpo is not locked
        Assume.assumeFalse(isGpoLocked);

        //set Gpo in RF Field mode
        gpoInterface.setGpoMode(GpoMode.GPO_FIELD_DETECT);

        //Get Gpo value
        gpoModeValue = gpoInterface.getGpoMode(gpoInterface.getGpo());
        Assert.assertEquals(GpoMode.GPO_FIELD_DETECT, gpoModeValue);

        //Test Errors on Gpo setting : Interrupt command must fail if Gpo not configures as Interrupt mode
        try {
            gpoInterface.sendInterruptCommand();
            fail("The sendInterruptCommand command should have failed!");

        } catch (STException e) {
            assertEquals(STException.STExceptionCode.INVALID_CMD_PARAM, e.getError());
        }

        //Test Errors on Gpo setting : State Control command must fail if Gpo not configures as State Control mode
        try {
            gpoInterface.setStateControlCommand(0);
            fail("The setStateControlCommand command should have failed!");

        } catch (STException e) {
            assertEquals(STException.STExceptionCode.INVALID_CMD_PARAM, e.getError());
        }

        //set Gpo in INTERRUPT mode & test send interrupt command is send
        gpoInterface.setGpoMode(GpoMode.GPO_INTERRUPT);
        gpoInterface.sendInterruptCommand();
        Assert.assertTrue(gpoInterface.isGpoInInterruptedMode());

        //set Gpo in STATE CONTROL mode & test send state control command is send
        gpoInterface.setGpoMode(GpoMode.GPO_STATE_CONTROL);

        //Test Errors on Gpo setting : State Control command must fail if Gpo not configures as State Control mode
        gpoInterface.setStateControlCommand(0);
        Assert.assertTrue(gpoInterface.isGpoInStateControlMode());

        //Locked GPO
        if (lockGpoForReal)  {
            gpoInterface.lockGpo();

            //Check GPO is locked
            isGpoLocked = gpoInterface.isGpoLocked();
            Assume.assumeTrue(isGpoLocked);

            //lock again GPO
            try {
                gpoInterface.lockGpo();
                fail("The lock command should have failed!");

            } catch (STException e) {
                assertEquals(STException.STExceptionCode.INVALID_USE_CONTEXT, e.getError());
            }
        }
    }

}

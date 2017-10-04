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

package com.st.st25sdk.type4a;

import java.util.List;

import com.st.st25sdk.STException;

public interface STType4GpoInterface {

    // List of all the possible GPO modes.
    // A tag may support only some of them
    enum GpoMode {
        GPO_NOT_USED,
        GPO_HIGH_IMPEDANCE,
        GPO_SESSION_OPENED,
        GPO_WIP,
        GPO_MIP,
        GPO_INTERRUPT,
        GPO_STATE_CONTROL,
        GPO_RF_BUSY,
        GPO_FIELD_DETECT
    }

    /**
     * Retrieve the Gpo mode from gpoConfig byte
     * @param gpoConfig gpo config byte
     * @return GpoMode
     * @throws STException
     */
    GpoMode getGpoMode(byte gpoConfig) throws STException ;

    /**
     * Set the Gpo to the mode
     * @param mode GpoMode to be set on Tag
     * @throws STException
     */
    void setGpoMode(GpoMode mode) throws STException ;

    /**
     * Get list of available mode
     * @return available Gpo modes
     * @throws STException
     */
    List<GpoMode> getSupportedGpoModes() throws STException;

    /**
     * Retrieve Gpo value
     * @return gpo byte value
     * @throws STException
     */
    byte getGpo() throws STException ;

    /**
     * Set the Gpo byte value
     * @param value gpo byte value - Tag dependant
     * @throws STException
     */
    void setGpo(byte value) throws STException ;

    /**
     * Lock GPO
     * @param value
     * @throws STException
     */
    public void lockGpo(byte value) throws STException;

    /**
     * Lock GPO
     * @throws STException
     */
    public void lockGpo() throws STException;

    /**
     * Indicates if GPO is locked
     * @return
     * @throws STException
     */
    public boolean isGpoLocked() throws STException;

    /**
     * Check if Gpo is configurable from a RF point of view
     * @return true/false
     */
    boolean isGpoConfigurableByRf();

    /**
     * Check if Gpo is in Interrupt mode
     * @return true if gpo in interrupt mode
     * @throws STException
     */
    boolean isGpoInInterruptedMode() throws STException ;

    /**
     * Check if Gpo is in StateControl mode
     * @return true if gpo in StateControl mode
     * @throws STException
     */
    boolean isGpoInStateControlMode() throws STException ;

    /**
     * Send an interrupt command that will interact on gpo
     * @throws STException
     */
    void sendInterruptCommand() throws STException;

    /**
     * Send a State control command that will interact on gpo
     * @param value 00h or 01h (GPO value GND or VCC)
     * @throws STException
     */
    void setStateControlCommand(int value) throws STException;
}
